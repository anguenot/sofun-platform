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

package org.sofun.core.kup.policy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.PredictionQuestionKupTiebreaker;
import org.sofun.core.api.prediction.PredictionService;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.sport.tournament.TournamentGame;

/**
 * Kup Policy Timer.
 * 
 * <p/>
 * 
 * triggering Kup life cycle checks on regular basis out of process for active
 * Kups only.
 * 
 * <p/>
 * 
 * This singleton is responsible to perform Kups status transitions. This is not
 * responsible for any kind of computations whatsover.
 * 
 * <p/>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class KupClosedLifeCyclePolicyManager extends
        AbstractKupLifeCycleManager {

    private static final Log log = LogFactory
            .getLog(KupClosedLifeCyclePolicyManager.class);

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    @EJB(
            beanName = "PredictionServiceImpl",
            beanInterface = PredictionServiceLocal.class)
    private PredictionService predictions;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @Override
    public KupService getKups() {
        return kups;
    }

    @Override
    public PredictionService getPredictions() {
        return predictions;
    }

    /**
     * Kup life cycle management.
     * 
     * @throws Exception
     */
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            log.debug("Checking Kups and apply policy...");

            //
            // Deal with closed Kups.
            //

            final byte[] closeStatus = new byte[1];
            closeStatus[0] = KupStatus.CLOSED;
            final List<Kup> closedKups = kups
                    .getKupsByStatus(closeStatus, null);

            ListIterator<Kup> kupsIter = closedKups.listIterator();
            while (kupsIter.hasNext()) {

                Kup kup = kupsIter.next();
                // Ensure no changes have been made by another transaction in
                // the mean time.
                em.refresh(kup);
                if (kup.getStatus() != KupStatus.CLOSED) {
                    continue;
                }

                final Calendar now = Calendar.getInstance();
                if (now.getTime().compareTo(kup.getEffectiveEndDate()) < 0) {
                    // Although, predictions and bets are forbidden the Kup is
                    // still ongoing. (i.e. some sports events are most
                    // likely to be played. So we do not need to settled it now.
                    continue;
                }

                boolean compute = true;
                ListIterator<Prediction> predictionsIter = getPredictions()
                        .getPredictionsFor(kup).listIterator();
                while (predictionsIter.hasNext()) {
                    Prediction prediction = predictionsIter.next();
                    if (!prediction.isPointsComputed()) {
                        compute = false;
                        break;
                    }
                }

                // Not all predictions got computed. We will retry later on.
                // Computation is performed by another scheduled timer.
                if (!compute) {
                    continue;
                }

                // Sort out winners and apply policy in case of players w/ same
                // amount of points.
                KupRankingTable table = kup.getRankingTable();
                List<MemberRankingTableEntry> entries = new ArrayList<MemberRankingTableEntry>(
                        table.getEntries());
                if (kup.getQuestionsTiebreaker().size() > 0) {
                    setTiebreakerOffsets(entries, kup);
                }

                // The table is final at this stage. We do not have any risk of
                // conflicts since no new participants will ever be
                // added nor values updated.
                table.setEntries(new HashSet<MemberRankingTableEntry>(entries));

                // Set final member's position in table.
                int i = 0;
                for (MemberRankingTableEntry entry : entries) {
                    MemberRankingTableEntry actual = table
                            .getEntryForMember(entry.getMember());
                    actual.setPosition(i);
                    i++;
                }

                // Verify if we need to cancel the kup because there are no
                // winners when we are in a non-free type of Kup to meet CGUs.
                if (KupType.GAMBLING_FR.equals(kup.getType())) {

                    Map<Integer, Float> repartition = kups
                            .getWinningsRepartitionRulesFor(kup);
                    int actualWinners = 0;
                    Iterator<MemberRankingTableEntry> ki = kup
                            .getRankingTable().getEntries().iterator();
                    while (ki.hasNext()) {
                        MemberRankingTableEntry entry = ki.next();
                        if (entry.getValue() > 0) {
                            actualWinners += 1;
                        }
                        if (actualWinners >= repartition.size()) {
                            break;
                        }
                    }

                    if (actualWinners == 0) {
                        kups.cancelKup(kup);
                        log.info("Kup with id=" + String.valueOf(kup.getId())
                                + " has been cancelled because there are no "
                                + "winners");
                        continue;
                    }
                }

                // Kup is closed and all predictions have been computed.
                log.info("Kup with id=" + String.valueOf(kup.getId())
                        + " has now status=" + KupStatus.SETTLED);
                kup.setStatus(KupStatus.SETTLED);

            }

        } catch (Throwable t) {
            log.error(t.getMessage());
            t.printStackTrace();
        } finally {
            available = true;
        }

        // Cancel status in exceptional situations will be handled manually per
        // administrator request.
        // @see KupeService.cancelKup()

    }

    protected void setTiebreakerOffsets(List<MemberRankingTableEntry> entries,
            Kup kup) {
        QuestionKupTiebreaker q = kup.getQuestionsTiebreaker().get(0);
        int tb = 0;
        if (q.getId() == 3) {
            tb = countTotalGames(kup);
        } else if (q.getId() == 4) {
            tb = countTotalTries(kup);
        } else if (q.getId() == 5) {
            tb = countFirstGamePoints(kup);
        } else {
            tb = countTotalScores(kup);
        }
        ListIterator<MemberRankingTableEntry> it = entries.listIterator();
        while (it.hasNext()) {
            MemberRankingTableEntry e = it.next();
            e.setTiebreakerOffset(Math.abs(getTiebreakerQuestionAnswerFor(
                    e.getMember(), kup)
                    - tb));
        }
    }

    private int getTiebreakerQuestionAnswerFor(Member member, Kup kup) {
        int answer = 0;
        // FIXME deal with different and possibly several questions.
        if (kup.getQuestionsTiebreaker().size() > 0) {
            QuestionKupTiebreaker q = kup.getQuestionsTiebreaker().get(0);
            PredictionQuestionKupTiebreaker p = getPredictions()
                    .getPredictionQuestionKupTieBreakerFor(member, kup, q);
            if (p != null) {
                if (!p.getAnswer().isEmpty()) {
                    try {
                        answer = Integer.valueOf(p.getAnswer());
                    } catch (NumberFormatException e) {
                        log.warn("Issue validating tie breaker question.");
                    }
                }
            }
        }
        return answer;
    }

    /**
     * Add all scores from all games.
     * 
     * @param kup: a {@link Kup} instance
     * @return the total number of goals, sets, etc. depending on sport.
     */
    private int countTotalGames(Kup kup) {
        int score = 0;
        for (TournamentGame game : kup.getBettableGames()) {
            if (game.getScore() != null) {
                kup.getQuestionsTiebreaker().get(0);
                if (game.getWinner() == null) {
                    // We do not count scores for tennis games where game has
                    // been stopped.
                    continue;
                }
                final String firstEntryScore = game.getProperties().get(
                        "scoreFirstEntryId");
                final String secondSecondScore = game.getProperties().get(
                        "scoreSecondEntryId");
                Integer.valueOf(game.getProperties().get("number_of_sets"));
                int offset = 0;
                while (offset < firstEntryScore.length()) {
                    // XXX US Open has tiebreak in every sets. (not the 3
                    // others)
                    score += Integer.valueOf(String.valueOf(firstEntryScore
                            .charAt(offset)));
                    score += Integer.valueOf(String.valueOf(secondSecondScore
                            .charAt(offset)));
                    offset += 1;
                }

            }
        }
        return score;
    }

    /**
     * Add all scores from all games.
     * 
     * @param kup: a {@link Kup} instance
     * @return the total number of goals, sets, etc. depending on sport.
     */
    private int countTotalScores(Kup kup) {
        int score = 0;
        for (TournamentGame game : kup.getBettableGames()) {
            if (game.getScore() != null) {
                QuestionKupTiebreaker q = kup.getQuestionsTiebreaker().get(0);
                if (q.getId() == 2 && game.getWinner() == null) {
                    // We do not count scores for tennis games where game has
                    // been stopped.
                    continue;
                }
                score += game.getScore().getScoreTeam1();
                score += game.getScore().getScoreTeam2();
            }
        }
        return score;
    }

    /**
     * All points for first game
     * 
     * @param kup: a {@link Kup} instance
     * @return the total number of goals, sets, etc. depending on sport.
     */
    private int countFirstGamePoints(Kup kup) {
        int score = 0;
        TournamentGame game = kup.getBettableGames().get(0);
        if (game.getScore() != null) {
            kup.getQuestionsTiebreaker().get(0);
            score += game.getScore().getScoreTeam1();
            score += game.getScore().getScoreTeam2();
        }
        return score;
    }

    /**
     * Count all tries within all games.
     * 
     * @param kup: a {@link Kup} instance
     * @return the total number of goals, sets, etc. depending on sport.
     */
    private int countTotalTries(Kup kup) {
        int score = 0;
        for (TournamentGame game : kup.getBettableGames()) {
            if (game.getScore() != null) {
                final int tries1 = Integer.valueOf(game.getProperties().get(
                        "GAME_PROP_TEAM1_NB_TRIES"));
                final int tries2 = Integer.valueOf(game.getProperties().get(
                        "GAME_PROP_TEAM2_NB_TRIES"));
                final int total = tries1 + tries2;
                score += total;
            }
        }
        return score;
    }

}
