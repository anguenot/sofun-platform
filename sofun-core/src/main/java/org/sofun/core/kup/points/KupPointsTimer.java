/*
 * Copyright (c)  Sofun Gaming SAS.
 * Copyright (c)  Julien Anguenot <julien@anguenot.org>
 * Copyright (c)  Julien De Preaumont <juliendepreaumont@gmail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Julien Anguenot <julien@anguenot.org> - initial API and implementation
*/

package org.sofun.core.kup.points;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.PredictionService;

/**
 * Kup Points Timer.
 * 
 * <p>
 * Clock triggering points computations.
 * </p>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class KupPointsTimer {

    private static final Log log = LogFactory.getLog(KupPointsTimer.class);

    @EJB(beanName = "PredictionServiceImpl", beanInterface = PredictionServiceLocal.class)
    private PredictionService predictions;

    @EJB(beanName = "MemberServiceImpl", beanInterface = MemberServiceLocal.class)
    private MemberService members;

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    private boolean available = true;

    @Timeout
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @Lock(LockType.READ)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            List<Prediction> toCompute = predictions
                    .getPredictionsGameToCompute();
            toCompute.addAll(predictions.getPredictionsRoundToCompute());
            toCompute.addAll(predictions.getPredictionsStageToCompute());
            toCompute.addAll(predictions.getPredictionsSeasonToCompute());

            final int totalPredictions = toCompute.size();
            if (totalPredictions > 0) {

                log.info("Found " + String.valueOf(toCompute.size())
                        + " predictions for which we need to compute points.");

                int current = 1;
                ListIterator<Prediction> predictionIter = toCompute
                        .listIterator();
                List<Long> moderatedKups = new ArrayList<Long>();
                while (predictionIter.hasNext()) {

                    Prediction prediction = predictionIter.next();

                    if (prediction == null || prediction.isPointsComputed()) {
                        continue;
                    }

                    // Verify the Kup has been moderated.
                    if (!prediction.getKup().isModerated()
                            || (!prediction.getKup().isTemplate() && (kups
                                    .getTemplateFor(prediction.getKup()) != null && !kups
                                            .getTemplateFor(prediction.getKup()).isModerated()) 
                                    )) {
                        if (!moderatedKups
                                .contains(prediction.getKup().getId())) {
                            log.warn("Kup with uuid="
                                    + prediction.getKup().getId()
                                    + " has not been moderated yet...");
                            moderatedKups.add(prediction.getKup().getId());
                        }
                        continue;
                    }

                    // Reload member from context.
                    Member member = members.getMember(prediction.getMember()
                            .getEmail());

                    // Get corresponding Kup.
                    Kup kup = prediction.getKup();

                    KupRankingTable kupRanking = kup.getRankingTable();
                    MemberRankingTableEntry kupRankingEntry = kupRanking
                            .getEntryForMember(member);

                    if (kupRankingEntry == null) {
                        if (KupType.GAMBLING_FR.equals(kup.getType())) {
                            log.debug("member=" + member.getEmail()
                                    + " in kup w/ uuid=" + kup.getId()
                                    + " is not a participant."
                                    + " (predictions but no bet probably)");
                            prediction.setPointsComputed(true);
                        } else if (KupType.FREE.equals(kup.getType())) {
                            log.error("member=" + member.getEmail()
                                    + " in kup w/ uuid=" + kup.getId()
                                    + " Cannot be found within ranking table.");
                        }
                        continue;
                    }

                    final int predictionPoints = kups.getPointsPredictionFor(
                            kup, prediction);
                    log.info(String.format(
                            "Points for prediction w/ uuid=%d in kup"
                                    + " w/ uuid=%d for member with email=%s =>"
                                    + " %d points (prediction %d/%d)",
                            prediction.getId(), kup.getId(), member.getEmail(),
                            predictionPoints, current, totalPredictions));

                    // Update points and settle prediction
                    prediction.setPoints(predictionPoints);
                    kupRankingEntry.setValue(kupRankingEntry.getValue()
                            + predictionPoints);
                    prediction.setPointsComputed(true);

                    // Update correct number of predictions.
                    if (predictionPoints > 0) {
                        Integer correct = kupRankingEntry
                                .getCorrectPredictions();
                        if (correct == null) {
                            correct = kups.countCorrectPredictionsFor(kup,
                                    member);
                        } else {
                            correct = correct + 1;
                        }
                        kupRankingEntry.setCorrectPredictions(correct);
                    }
                    if (kupRankingEntry.getFirstPredictions() == null) {
                        kupRankingEntry.setFirstPredictions(kups
                                .getFirstPredictionDateFor(member, kup));
                    }

                    // Update last modified.
                    final Date modified = Calendar.getInstance().getTime();
                    kupRanking.setLastModified(modified);

                    current++;

                }

            }

        } catch (Throwable t) {
            t.printStackTrace();
            log.error(t.getMessage());
        } finally {
            available = true;
        }

    }
}
