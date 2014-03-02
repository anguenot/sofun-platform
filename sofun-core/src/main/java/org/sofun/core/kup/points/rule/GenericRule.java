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

package org.sofun.core.kup.points.rule;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.prediction.KupPredictionPointsRule;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.tournament.PredictionGameQuestion;
import org.sofun.core.api.prediction.tournament.PredictionGameScore;
import org.sofun.core.api.prediction.tournament.contestant.PredictionGameOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionRoundOrderedContestantsList;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameScore;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.prediction.PredictionResultImpl;
import org.sofun.core.prediction.contestant.PredictionOrderedContestantResultImpl;

/**
 * Generic points computation rule.
 * 
 * <p>
 * 
 * XXX need to be split up and isolated per Kup. Then registered to the kup
 * itself
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class GenericRule implements KupPredictionPointsRule {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(GenericRule.class);

    private final String ligue1BgPatternString = "label_kup_title_201213_ligue1_d.*_.*_.*";

    private final Pattern ligue1BgPattern = Pattern
            .compile(ligue1BgPatternString);

    private final String prlBgPatternString = "label_kup_title_201213_premier_league_.*_.*";

    private final Pattern prlBgPattern = Pattern.compile(prlBgPatternString);

    private final String ligaBgPatternString = "label_kup_title_201213_liga_.*_.*";

    private final Pattern ligaBgPattern = Pattern.compile(ligaBgPatternString);

    private final String legaBgPatternString = "label_kup_title_201213_lega_.*_.*";

    private final Pattern legaBgPattern = Pattern.compile(legaBgPatternString);

    private final String bundesligaBgPatternString = "label_kup_title_201213_bundesliga_.*_.*";

    private final Pattern bundesligaBgPattern = Pattern
            .compile(bundesligaBgPatternString);

    private final String clBgPatternString = "label_kup_title_201213_cl_bg_d.*_.*_.*";

    private final Pattern clBgPattern = Pattern.compile(clBgPatternString);

    private final String top14OneBgPatternString = "label_kup_title_201213_rugby_onebg_.*_.*_.*";

    private final Pattern top14OneBgPattern = Pattern
            .compile(top14OneBgPatternString);

    private final String top14BgPatternString = "label_kup_title_201213_rugby_bg_.*";

    private final Pattern top14BgPattern = Pattern
            .compile(top14BgPatternString);

    @Override
    public int getPointsFor(Kup kup, Prediction prediction, KupService kups)
            throws CoreException {

        int points = 0;

        if (kup.getName().startsWith("label_kup_title_f1_gp_")) {

            String predictionType = prediction.getType();

            PredictionRoundOrderedContestantsList roundPrediction = (PredictionRoundOrderedContestantsList) prediction;
            TournamentRound round = roundPrediction.getTournamentRound();
            List<SportContestant> member = roundPrediction.getContestants();

            if ("f1_driver_best_lap".equals(predictionType) && member != null
                    && member.size() > 0) {
                SportContestant playerChoice = member.get(0);
                if (round.getProperties().get("fast_time_driver_uuid")
                        .equals(playerChoice.getUUID())) {
                    final int predictionsPoints = 20;
                    prediction
                            .addResult(new PredictionOrderedContestantResultImpl(
                                    "position", predictionsPoints, prediction,
                                    0));
                    points += predictionsPoints;
                }
            } else if ("f1_driver_grid".equals(predictionType)) {

                List<SportContestant> actual = round.getTableByType("DRIVERS")
                        .getOrderedContestantsForKey("pos");

                try {
                    if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 20;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(3) != null
                            && member.get(3).getUUID()
                                    .equals(actual.get(3).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 3));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }

            } else if ("f1_driver_ranking".equals(predictionType)) {

                List<SportContestant> actual = round.getTableByType("DRIVERS")
                        .getOrderedContestantsForKey("pos");

                int top3 = 0;

                try {
                    if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        top3 += 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                try {
                    if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 18;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        top3 += 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        top3 += 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(3) != null
                            && member.get(3).getUUID()
                                    .equals(actual.get(3).getUUID())) {
                        final int predictionsPoints = 12;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 3));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(4) != null
                            && member.get(4).getUUID()
                                    .equals(actual.get(4).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 4));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(5) != null
                            && member.get(5).getUUID()
                                    .equals(actual.get(5).getUUID())) {
                        final int predictionsPoints = 8;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 5));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(6) != null
                            && member.get(6).getUUID()
                                    .equals(actual.get(6).getUUID())) {
                        final int predictionsPoints = 6;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 6));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(7) != null
                            && member.get(7).getUUID()
                                    .equals(actual.get(7).getUUID())) {
                        final int predictionsPoints = 4;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 7));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(8) != null
                            && member.get(8).getUUID()
                                    .equals(actual.get(8).getUUID())) {
                        final int predictionsPoints = 2;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 8));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(9) != null
                            && member.get(9).getUUID()
                                    .equals(actual.get(9).getUUID())) {
                        final int predictionsPoints = 1;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 9));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                //
                // TOP 3 BONUS
                //

                if (top3 == 3) {
                    prediction.addResult(new PredictionResultImpl(
                            "bonus_top_3", 50, prediction));
                    points += 50;
                }

                //
                // TOP 5 BONUS
                //

                for (int i = 0; i <= 4; i++) {
                    try {
                        SportContestant m = member.get(i);
                        for (int j = 0; j <= 4; j++) {
                            SportContestant a = actual.get(j);
                            if (m != null && a.getUUID().equals(m.getUUID())
                                    && i != j) {
                                points += 5;
                                prediction.addResult(new PredictionResultImpl(
                                        "bonus_top_5", 5, prediction));
                                break;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                    }
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().equals(
                "label_kup_title_cycling_2012_tdf_etape_etape")
                || kup.getName().startsWith(
                        "label_kup_title_cycling_2012_tdf_e")) {

            String predictionType = prediction.getType();

            PredictionRoundOrderedContestantsList roundPrediction = (PredictionRoundOrderedContestantsList) prediction;
            TournamentRound round = roundPrediction.getTournamentRound();
            List<SportContestant> member = roundPrediction.getContestants();

            if ("cycling_maillot_jaune".equals(predictionType)) {
                if (member == null || member.size() < 1) {
                    return 0;
                }
                SportContestant playerChoice = member.get(0);
                List<SportContestant> actuals = round.getTableByType(
                        "INDIVIDUAL_AGGREGATED").getOrderedContestantsForKey(
                        "pos");
                if (actuals == null || actuals.size() == 0) {
                    throw new CoreException(
                            "No results for INDIVIDUAL_AGGREGATED in kup with id="
                                    + kup.getId() + " and name="
                                    + kup.getName());
                }
                if (actuals.get(0).getUUID().equals(playerChoice.getUUID())) {
                    final int predictionsPoints = 15;
                    prediction
                            .addResult(new PredictionOrderedContestantResultImpl(
                                    "position", predictionsPoints, prediction,
                                    0));
                    points += predictionsPoints;
                }
                // Check if 4 good predictions on jersey's for 15 points bonus
                if (points > 0) {
                    List<Prediction> playerPredictions = kups
                            .getPredictionsFor(prediction.getMember(), kup);
                    int goodOnes = 1;
                    for (Prediction p : playerPredictions) {
                        if ((p.getType().equals("cycling_maillot_vert") && p
                                .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_apois") && p
                                        .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_blanc") && p
                                        .getPoints() == 15)) {
                            PredictionRoundOrderedContestantsList rp = (PredictionRoundOrderedContestantsList) p;
                            if (rp.getTournamentRound().getUUID() == round
                                    .getUUID()) {
                                goodOnes += 1;
                            }
                        }
                    }
                    if (goodOnes == 4) {
                        log.info("Bonus 15 points 4 jerseys are corrects!");
                        points += 15;
                    }
                }
            } else if ("cycling_maillot_vert".equals(predictionType)) {
                if (member == null || member.size() < 1) {
                    return 0;
                }
                SportContestant playerChoice = member.get(0);
                List<SportContestant> actuals = round.getTableByType(
                        "GREEN_AGGREGATED").getOrderedContestantsForKey("pos");
                if (actuals == null || actuals.size() == 0) {
                    throw new CoreException(
                            "No results for GREEN_AGGREGATED in kup with id="
                                    + kup.getId() + " and name="
                                    + kup.getName());
                }
                if (actuals.get(0).getUUID().equals(playerChoice.getUUID())) {
                    final int predictionsPoints = 15;
                    prediction
                            .addResult(new PredictionOrderedContestantResultImpl(
                                    "position", predictionsPoints, prediction,
                                    0));
                    points += predictionsPoints;
                }
                // Check if 4 good predictions on jersey's for 15 points bonus
                if (points > 0) {
                    List<Prediction> playerPredictions = kups
                            .getPredictionsFor(prediction.getMember(), kup);
                    int goodOnes = 1;
                    for (Prediction p : playerPredictions) {
                        if ((p.getType().equals("cycling_maillot_jaune") && p
                                .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_apois") && p
                                        .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_blanc") && p
                                        .getPoints() == 15)) {
                            PredictionRoundOrderedContestantsList rp = (PredictionRoundOrderedContestantsList) p;
                            if (rp.getTournamentRound().getUUID() == round
                                    .getUUID()) {
                                goodOnes += 1;
                            }
                        }
                    }
                    if (goodOnes == 4) {
                        log.info("Bonus 15 points 4 jerseys are corrects!");
                        points += 15;
                    }
                }
            } else if ("cycling_maillot_apois".equals(predictionType)) {
                if (member == null || member.size() < 1) {
                    return 0;
                }
                SportContestant playerChoice = member.get(0);
                List<SportContestant> actuals = round.getTableByType(
                        "MOUNTAIN_AGGREGATED").getOrderedContestantsForKey(
                        "pos");
                if (actuals == null || actuals.size() == 0) {
                    throw new CoreException(
                            "No results for MOUNTAIN_AGGREGATED in kup with id="
                                    + kup.getId() + " and name="
                                    + kup.getName());
                }
                if (actuals.get(0).getUUID().equals(playerChoice.getUUID())) {
                    final int predictionsPoints = 15;
                    prediction
                            .addResult(new PredictionOrderedContestantResultImpl(
                                    "position", predictionsPoints, prediction,
                                    0));
                    points += predictionsPoints;
                }
                // Check if 4 good predictions on jersey's for 15 points bonus
                if (points > 0) {
                    List<Prediction> playerPredictions = kups
                            .getPredictionsFor(prediction.getMember(), kup);
                    int goodOnes = 1;
                    for (Prediction p : playerPredictions) {
                        if ((p.getType().equals("cycling_maillot_jaune") && p
                                .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_vert") && p
                                        .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_blanc") && p
                                        .getPoints() == 15)) {
                            PredictionRoundOrderedContestantsList rp = (PredictionRoundOrderedContestantsList) p;
                            if (rp.getTournamentRound().getUUID() == round
                                    .getUUID()) {
                                goodOnes += 1;
                            }
                        }
                    }
                    if (goodOnes == 4) {
                        log.info("Bonus 15 points 4 jerseys are corrects!");
                        points += 15;
                    }
                }
            } else if ("cycling_maillot_blanc".equals(predictionType)) {
                if (member == null || member.size() < 1) {
                    return 0;
                }
                SportContestant playerChoice = member.get(0);
                List<SportContestant> actuals = round.getTableByType(
                        "YOUNG_AGGREGATED").getOrderedContestantsForKey("pos");
                if (actuals == null || actuals.size() == 0) {
                    throw new CoreException(
                            "No results for YOUNG_AGGREGATED in kup with id="
                                    + kup.getId() + " and name="
                                    + kup.getName());
                }
                if (actuals.get(0).getUUID().equals(playerChoice.getUUID())) {
                    final int predictionsPoints = 15;
                    prediction
                            .addResult(new PredictionOrderedContestantResultImpl(
                                    "position", predictionsPoints, prediction,
                                    0));
                    points += predictionsPoints;
                }
                // Check if 4 good predictions on jersey's for 15 points bonus
                if (points > 0) {
                    List<Prediction> playerPredictions = kups
                            .getPredictionsFor(prediction.getMember(), kup);
                    int goodOnes = 1;
                    for (Prediction p : playerPredictions) {
                        if ((p.getType().equals("cycling_maillot_jaune") && p
                                .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_vert") && p
                                        .getPoints() == 15)
                                || (p.getType().equals("cycling_maillot_apois") && p
                                        .getPoints() == 15)) {
                            PredictionRoundOrderedContestantsList rp = (PredictionRoundOrderedContestantsList) p;
                            if (rp.getTournamentRound().getUUID() == round
                                    .getUUID()) {
                                goodOnes += 1;
                            }
                        }
                    }
                    if (goodOnes == 4) {
                        log.info("Bonus 15 points 4 jerseys are corrects!");
                        points += 15;
                    }
                }
            } else if ("cycling_podium_individual".equals(predictionType)) {

                List<SportContestant> actual = round.getTableByType(
                        "INDIVIDUAL_STAGE").getOrderedContestantsForKey("pos");

                int top3 = 0;
                int inTop3 = 0;

                try {
                    if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        top3 += 1;
                        inTop3 += 1;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(3).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(4).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(5).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(6).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(7).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(8).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(9).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(10).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(11).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(12).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(13).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(14).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(15).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(16).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(17).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(18).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(19).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        top3 += 1;
                        inTop3 += 1;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(4).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(5).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(6).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(7).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(8).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(9).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(10).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(11).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(12).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(13).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(14).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(15).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(16).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(17).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(18).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(19).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        top3 += 1;
                        inTop3 += 1;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(4).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(5).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(6).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(7).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(8).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(9).getUUID())) {
                        final int predictionsPoints = 10;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(10).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(11).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(12).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(13).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(14).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(15).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(16).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(17).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(18).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(19).getUUID())) {
                        final int predictionsPoints = 5;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                //
                // TOP 3 BONUS
                //

                if (top3 == 3) {
                    log.info("Bonus 20 points for 3 correct predictions in correct order");
                    points += 20;
                } else if (inTop3 == 3) {
                    log.info("Bonus 15 points for 3 correct predictions in wrong order");
                    points += 15;
                } else if (inTop3 == 2) {
                    log.info("Bonus 10 points for 2 correct predictions in wrong or correct order");
                    points += 10;
                }

            } else if ("cycling_podium_team".equals(predictionType)) {

                List<SportContestant> actual = round.getTableByType(
                        "TEAM_STAGE").getOrderedContestantsForKey("pos");

                int top3 = 0;
                int inTop3 = 0;

                try {
                    if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        top3 += 1;
                        inTop3 += 1;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(0) != null
                            && member.get(0).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 0));
                        points += predictionsPoints;
                        inTop3 += 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        top3 += 1;
                        inTop3 += 1;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(1) != null
                            && member.get(1).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 1));
                        points += predictionsPoints;
                        inTop3 += 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(2).getUUID())) {
                        final int predictionsPoints = 25;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        top3 += 1;
                        inTop3 += 1;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(0).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        inTop3 += 1;
                    } else if (member.get(2) != null
                            && member.get(2).getUUID()
                                    .equals(actual.get(1).getUUID())) {
                        final int predictionsPoints = 15;
                        prediction
                                .addResult(new PredictionOrderedContestantResultImpl(
                                        "position", predictionsPoints,
                                        prediction, 2));
                        points += predictionsPoints;
                        inTop3 += 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                //
                // TOP 3 BONUS
                //

                if (top3 == 3) {
                    log.info("Bonus 20 points for 3 correct predictions in correct order");
                    points += 20;
                } else if (inTop3 == 3) {
                    log.info("Bonus 15 points for 3 correct predictions in wrong order");
                    points += 15;
                } else if (inTop3 == 2) {
                    log.info("Bonus 10 points for 2 correct predictions in wrong order");
                    points += 10;
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (ligue1BgPattern.matcher(kup.getName()).find()
                || clBgPattern.matcher(kup.getName()).find()
                || (prlBgPattern.matcher(kup.getName()).find() && !(kup
                        .getName()
                        .equals("label_kup_title_201213_premier_league_match_match") || kup
                        .getName().startsWith(
                                "label_kup_title_201213_premier_league_k10_d")))
                || (ligaBgPattern.matcher(kup.getName()).find() && !(kup
                        .getName().equals(
                                "label_kup_title_201213_liga_match_match") || kup
                        .getName().startsWith(
                                "label_kup_title_201213_liga_k10_d")))
                || (legaBgPattern.matcher(kup.getName()).find() && !(kup
                        .getName().equals(
                                "label_kup_title_201213_lega_match_match") || kup
                        .getName().startsWith(
                                "label_kup_title_201213_lega_k10_d")))
                || (bundesligaBgPattern.matcher(kup.getName()).find() && !kup
                        .getName()
                        .equals("label_kup_title_201213_bundesliga_match_match"))
                || (kup.getName().startsWith("label_kup_title_201213_cdll_bg"))
                || (kup.getName()
                        .startsWith("label_kup_title_201213_internationals_"))) {

            if ("se".equals(prediction.getType())
                    && prediction instanceof PredictionGameScore) {

                PredictionGameScore gamePrediction = (PredictionGameScore) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();

                if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()
                        && score.getScoreTeam2() == gamePrediction
                                .getScoreTeam2()) {
                    points += 20;
                } else if (score.getScoreTeam1() > score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() > gamePrediction
                                .getScoreTeam2()) {
                    points += 6;
                } else if (score.getScoreTeam1() < score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() < gamePrediction
                                .getScoreTeam2()) {
                    points += 6;
                } else if (score.getScoreTeam1() == score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() == gamePrediction
                                .getScoreTeam2()) {
                    points += 6;
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();

                if ("label_question_winner_first_half".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_WINNER_FIRST_HALF");
                    String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        answer = "";
                    }

                    if (result.equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_team_winner_second_half"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_WINNER_SECOND_HALF");
                    String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        answer = "";
                    }

                    if (result.equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_team_first_scores".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_GOAL");
                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_which_quaters_first_score"
                        .equals(label)) {

                    final String result = game.getProperties().get(
                            "GAME_PROP_FIRST_GOAL_QUARTER");
                    final String answer = questionPrediction.getAnswer();
                    if (result.equals(answer)) {
                        points += 15;
                    }

                } else if ("label_question_total_goals_fulltime".equals(label)
                        || "label_question_total_goals_fulltime_and_extra"
                                .equals(label)) {

                    final int nbGoals = game.getScore().getScoreTeam1()
                            + game.getScore().getScoreTeam2();

                    String answer = questionPrediction.getAnswer();
                    if (answer == null || "".equals(answer)) {
                        answer = "0";
                    }

                    if (nbGoals == Integer.valueOf(answer)) {
                        points += 15;
                    }

                } else if ("label_question_extra_time".equals(label)) {

                    final String answer = questionPrediction.getAnswer();
                    final String result = game.getProperties().get(
                            "GAME_PROP_HAS_EXTRA_TIME");
                    if ("label_yes".equals(answer) && "1".equals(result)) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !"1".equals(result)) {
                        points += 5;
                    }

                } else if ("label_question_penalties".equals(label)) {

                    final String answer = questionPrediction.getAnswer();
                    final String result = game.getProperties().get(
                            "GAME_PROP_HAS_SHOOTOUT");
                    if ("label_yes".equals(answer) && "1".equals(result)) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !"1".equals(result)) {
                        points += 5;
                    }

                } else if ("label_question_team_home_scores_first"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();
                    final String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_GOAL");
                    if ("label_yes".equals(answer)
                            && game.getContestants().get(0).getUUID()
                                    .equals(result)) {
                        points += 5;
                    } else if ("label_no".equals(answer)
                            && !game.getContestants().get(0).getUUID()
                                    .equals(result)) {
                        points += 5;
                    }

                } else if ("label_question_more_than_2_goals".equals(label)) {

                    final String answer = questionPrediction.getAnswer();
                    final int result = game.getScore().getScoreTeam1()
                            + game.getScore().getScoreTeam2();
                    if ("label_yes".equals(answer) && result > 2) {
                        points += 5;
                    } else if ("label_no".equals(answer) && result <= 2) {
                        points += 5;
                    }

                } else if ("label_question_samaras_greece_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p10866";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_lewandowsk_poland_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p56764";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_arshavin_russia_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p13227";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_goals_difference_at_end"
                        .equals(label)) {

                    TournamentGameScore score = game.getScore();
                    String answer = questionPrediction.getAnswer();

                    int diffScore = Math.abs(score.getScoreTeam1()
                            - score.getScoreTeam2());

                    if (String.valueOf(diffScore).equals(answer)) {
                        points += 15;
                    }

                } else if ("label_question_baros_repcheque_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p10602";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_gomez_germany_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p17884";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_Ronaldo_portugal_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p14937";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_ronaldo_real_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p14937";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_vanpersie_holland_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p12297";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_bendtner_denmark_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p27697";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_xavi_spain_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p5816";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_cassano_italie_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p7174";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_keane_ireland_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p1710";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_olic_croatie_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p13139";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_benzema_france_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p19927";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_lampard_england_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p2051";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_milevskyi_ukraine_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p14566";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_ibrahimovic_sweden_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p9808";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_dudka_poland_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p26917";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_pavlioutchenko_russia_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p20298";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_salpingidis_grece_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p28130";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_pekhart_repcheque_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p37647";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_meireles_portugal_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p19921";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_krohndehli_denmark_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p16099";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_ozil_germany_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p37605";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_robben_holland_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p8533";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_obraniak_bordeaux_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p40676";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_yedder_toulouse_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p83912";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_vanbommel_holland_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p4854";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_rossi_italie_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p10625";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_kranjcar_croatie_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p28097";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_ramos_spain_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p17861";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_duff_ireland_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p1256";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_nasri_france_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p28554";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_milevskyi_ukraine_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p14566";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_cole_england_scores".equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p3785";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_kallstrom_sweden_scores"
                        .equals(label)) {

                    final String answer = questionPrediction.getAnswer();

                    // Ivory coast
                    final String uuid = "p14985";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }

                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }

                } else if ("label_question_papastathopoulos_greece_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p39476";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_dzagoev_russia_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p51437";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_ribery_france_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p28559";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_ribery_bayern_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p28559";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_pastore_psg_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p54782";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_leko_zagreb_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p13169";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_pantelic_olympiakos_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p26747";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_herrera_mhsc_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p119588";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_okazaki_japan_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p78412";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_elmander_sweden_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p4739";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_plasil_repcheque_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p12057";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_blaszczykowski_poland_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37092";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_nani_portugal_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p38530";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_kvist_denmark_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p27261";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_alonso_spain_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p3508";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_modric_croatie_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37055";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_rooney_england_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p13017";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_podolski_germany_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p17733";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_rosicky_repcheque_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p8597";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_karagounis_greece_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p6994";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_torres_spain_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p14402";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_balotelli_italie_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p42493";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_moutinho_portugal_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p19624";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_fabregas_spain_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p17878";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_iniesta_spain_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p12237";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_erding_rennes_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p19510";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_briand_lyon_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p27675";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_aubameyang_asse_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p54694";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_roux_lille_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p51525";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_remy_om_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p38419";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_contout_sochaux_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p42759";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_medjani_ajaccio_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p17337";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_ibrahimovic_paris_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p9808";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_hulk_porto_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p53645";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_kalou_lille_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37352";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_bressan_bate_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p63448";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_belhanda_mhsc_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p66959";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_ayew_marseille_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p45124";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_lavezzi_paris_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p45154";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_plasil_bordeaux_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p12057";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_kalou_lille_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37352";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_menez_paris_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p19500";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gomez_lyon_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p20088";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_samassa_valenciennes_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37786";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_pitroipa_rennes_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p18168";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_monnetpaquet_lorient_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p38261";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_hamouna_asse_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p67276";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_nogueira_sochaux_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p45033";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gignac_marseille_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37827";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_mutu_ajaccio_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p4193";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_maoulida_bastia_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p10019";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_govou_evian_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p6600";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_payet_lille_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37901";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_lacazette_lyon_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p59966";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gomis_lyon_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37998";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gouffran_bordeaux_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p42727";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_feret_rennes_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p45082";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_mavouba_lille_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p18787";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_utaka_mhsc_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p13000";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_rami_valence_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p41795";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_cabaye_france_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p27341";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_suarez_uruguay_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p44404";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gerrard_liverpool_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p1814";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_nasri_manchester_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p28554";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gago_valence_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p19975";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_iniesta_barcelone_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p12237";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_giroud_france_scores".equals(label)
                        || "label_question_giroud_arsenal_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p44346";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_hazard_chelsea_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p42786";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_pukki_finland_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p57127";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_valbuena_france_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p37852";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_putsila_belarus_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p49432";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_hamouna_asse_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p67276";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_nogueira_sochaux_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p45033";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_pitroipa_rennes_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p18168";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_monnetpaquet_lorient_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p38261";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_milevskiy_kiev_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p14566";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_arteta_arsenal_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p8758";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_gomez_bayern_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p17884";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_huntelaar_schalke_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p14941";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_messi_barcelona_scores"
                        .equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p19054";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_boateng_inter_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p20360";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else if ("label_question_forlan_milan_scores".equals(label)) {
                    final String answer = questionPrediction.getAnswer();
                    final String uuid = "p12273";
                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYERS_UUID_GOALS");
                    String[] playerUUIDs = result.split(",");
                    boolean scored = false;
                    if (Arrays.asList(playerUUIDs).contains(uuid)) {
                        scored = true;
                    }
                    if ("label_yes".equals(answer) && scored) {
                        points += 5;
                    } else if ("label_no".equals(answer) && !scored) {
                        points += 5;
                    }
                } else {
                    throw new CoreException(
                            "Missing questions rules for kup with id="
                                    + kup.getId() + " and name="
                                    + kup.getName() + " FOR questions=" + label);
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith("label_kup_title_201213_ligue1_d")
                || kup.getName().startsWith("label_kup_title_201213_cdll_d")
                || kup.getName().startsWith(
                        "label_kup_title_201213_mediapronos_w")
                || kup.getName().startsWith(
                        "label_kup_title_201213_fan2sport_w")) {

            if ("se".equals(prediction.getType())
                    && prediction instanceof PredictionGameScore) {

                PredictionGameScore gamePrediction = (PredictionGameScore) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();

                if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()
                        && score.getScoreTeam2() == gamePrediction
                                .getScoreTeam2()) {
                    points += 15;
                } else {
                    if (score.getScoreTeam1() > score.getScoreTeam2()

                            && gamePrediction.getScoreTeam1() > gamePrediction
                                    .getScoreTeam2()) {
                        points += 6;
                    } else if (score.getScoreTeam1() < score.getScoreTeam2()
                            && gamePrediction.getScoreTeam1() < gamePrediction
                                    .getScoreTeam2()) {
                        points += 6;
                    } else if (score.getScoreTeam1() < score.getScoreTeam2()
                            && gamePrediction.getScoreTeam1() < gamePrediction
                                    .getScoreTeam2()) {
                        points += 6;
                    } else if (score.getScoreTeam1() == score.getScoreTeam2()
                            && gamePrediction.getScoreTeam1() == gamePrediction
                                    .getScoreTeam2()) {
                        points += 6;
                    }
                    if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()) {
                        points += 3;
                    } else if (score.getScoreTeam2() == gamePrediction
                            .getScoreTeam2()) {
                        points += 3;
                    }
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith("label_kup_title_usopen_2012_d")
                || kup.getName().startsWith("label_kup_title_usopen_2012_bg")
                || kup.getName().equals(
                        "label_kup_title_usopen_2012_final_women")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 2;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 2;
                    }
                }

            } else if ("se".equals(prediction.getType())
                    && prediction instanceof PredictionGameScore) {

                PredictionGameScore gamePrediction = (PredictionGameScore) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();

                if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()
                        && score.getScoreTeam2() == gamePrediction
                                .getScoreTeam2()) {
                    points += 10;
                } else if (score.getScoreTeam1() > score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() > gamePrediction
                                .getScoreTeam2()) {
                    points += 2;
                } else if (score.getScoreTeam1() < score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() < gamePrediction
                                .getScoreTeam2()) {
                    points += 2;
                } else if (score.getScoreTeam1() == score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() == gamePrediction
                                .getScoreTeam2()) {
                    points += 2;
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();

                if ("label_question_tennis_sets_number".equals(label)) {

                    final String firstEntryScore = game.getProperties().get(
                            "scoreFirstEntryId");
                    final String secondEntryScore = game.getProperties().get(
                            "scoreSecondEntryId");

                    final int nb_sets_max = Integer.valueOf(game
                            .getProperties().get("number_of_sets"));

                    if (firstEntryScore == null || firstEntryScore.isEmpty()
                            || secondEntryScore == null
                            || secondEntryScore.isEmpty()) {
                        throw new CoreException(
                                "Missing results in kup with id=" + kup.getId()
                                        + " and gameUUID=" + game.getUUID());
                    }

                    final String answer = questionPrediction.getAnswer();
                    final List<String> answerSets = Arrays.asList(answer
                            .split("#"));

                    boolean finalTbGame = false;
                    if (firstEntryScore.length() > nb_sets_max) {
                        finalTbGame = true;
                    }

                    int offset = 0;
                    int nb_se_sets = 0;
                    int nb_ic_sets = 0;
                    while (offset < firstEntryScore.length()) {

                        try {

                            // Player predicted less sets than actual match
                            if (offset > Math.round(answerSets.size() / 2)
                                    || offset > firstEntryScore.length()) {
                                break;
                            }

                            // TODO fixme tb game.

                            // SE set
                            if (Integer.valueOf(String.valueOf(firstEntryScore
                                    .charAt(offset))) == Integer
                                    .valueOf(answerSets.get(offset))
                                    && Integer.valueOf(String
                                            .valueOf(secondEntryScore
                                                    .charAt(offset))) == Integer
                                            .valueOf(answerSets.get(offset
                                                    + Math.round(answerSets
                                                            .size() / 2)))) {
                                nb_se_sets += 1;
                                nb_ic_sets += 1;
                                points += 40;
                            } else if ((Integer.valueOf(String
                                    .valueOf(firstEntryScore.charAt(offset))) < Integer
                                    .valueOf(String.valueOf(secondEntryScore
                                            .charAt(offset))) && Integer
                                    .valueOf(answerSets.get(offset)) < Integer
                                    .valueOf(answerSets.get(offset
                                            + Math.round(answerSets.size() / 2))))
                                    || (Integer.valueOf(String
                                            .valueOf(firstEntryScore
                                                    .charAt(offset))) > Integer
                                            .valueOf(String
                                                    .valueOf(secondEntryScore
                                                            .charAt(offset))) && Integer
                                            .valueOf(answerSets.get(offset)) > Integer
                                            .valueOf(answerSets.get(offset
                                                    + Math.round(answerSets
                                                            .size() / 2))))) {
                                // IC set
                                nb_ic_sets += 1;
                                points += 5;
                            }

                        } catch (IndexOutOfBoundsException e) {
                            log.warn("Cannot compute prediction with uuid="
                                    + prediction.getId());
                        } finally {
                            offset += 1;
                        }

                    }

                    // Compute combo
                    if (!finalTbGame) {
                        // Points for the actual amount of sets
                        if (Math.round(answerSets.size() / 2) == firstEntryScore
                                .length()) {
                            points += 20;
                            // Combo only applies when correct number of sets
                            if (nb_se_sets == firstEntryScore.length()) {
                                points += 50;
                            } else if (nb_ic_sets == firstEntryScore.length()) {
                                points += 30;
                            }

                        }
                    } else {
                        // Points for the actual amount of sets
                        if (Math.round(answerSets.size() / 2) == nb_sets_max) {
                            points += 20;
                            // Combo only applies when correct number of sets
                            if (nb_se_sets == nb_sets_max) {
                                points += 50;
                            } else if (nb_ic_sets == nb_sets_max) {
                                points += 30;
                            }
                        }
                    }

                } else {
                    throw new CoreException("Missing rules for kup with id="
                            + kup.getId() + " and name=" + kup.getName());
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith("label_kup_title_201213_ligue2_d")
                || kup.getName().startsWith(
                        "label_kup_title_201213_ligue1_k8_d")
                || kup.getName().startsWith("label_kup_title_2012_wc_q_k8_")
                || kup.getName().startsWith("label_kup_title_201213_el_k8_d")
                || kup.getName().startsWith("label_kup_title_201213_cl_d")
                || kup.getName().startsWith("label_kup_title_201213_cdll_k8_d")
                || kup.getName().startsWith(
                        "label_kup_title_201213_soccer_k12_")
                || kup.getName()
                        .startsWith("label_kup_title_201213_lega_k10_d")
                || kup.getName()
                        .startsWith("label_kup_title_201213_liga_k10_d")
                || kup.getName().startsWith(
                        "label_kup_title_201213_premier_league_k10_d")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 2;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 4;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 3;
                    }
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith(
                "label_kup_title_201213_rugby_hcup_k12_d")
                || kup.getName().startsWith(
                        "label_kup_title_201213_rugby_hcup_d")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 4;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 10;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 6;
                    }
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().equals(
                "label_kup_title_201213_ligue2_match_match")
                || (kup.getName()
                        .equals("label_kup_title_201213_ligue1_match_match"))
                || (kup.getName()
                        .equals("label_kup_title_201213_premier_league_match_match"))
                || (kup.getName()
                        .equals("label_kup_title_201213_liga_match_match"))
                || (kup.getName()
                        .equals("label_kup_title_201213_bundesliga_match_match"))
                || (kup.getName()
                        .equals("label_kup_title_201213_lega_match_match"))
                || (kup.getName()
                        .equals("label_kup_title_201213_cl_match_par_match"))
                || (kup.getName().equals("label_kup_title_201213_cl_rounds"))) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 5;
                    }
                }

            } else if ("se".equals(prediction.getType())
                    && prediction instanceof PredictionGameScore) {

                PredictionGameScore gamePrediction = (PredictionGameScore) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();

                if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()
                        && score.getScoreTeam2() == gamePrediction
                                .getScoreTeam2()) {
                    points += 15;
                } else if (score.getScoreTeam1() > score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() > gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                } else if (score.getScoreTeam1() < score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() < gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                } else if (score.getScoreTeam1() == score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() == gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();

                if ("label_question_first_player_that_scores".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYER_UUID_FIRST_GOAL");
                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set.
                    // If not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 20;
                    }

                } else {
                    throw new CoreException("Missing rules for kup with id="
                            + kup.getId() + " and name=" + kup.getName());
                }

            }

        } else if (kup.getName().equals(
                "label_kup_title_201213_rugby_top14_match_match")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();
                if ("label_question_number_of_total_tries_team1".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 15;
                    }

                } else if ("label_question_number_of_total_tries_team2"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 15;
                    }

                }
            }

        } else if (kup.getName().equals(
                "label_kup_title_201213_rugby_hcup_match_match")) {

            // This section should be updated after fixtures.
            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 4;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 10;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 6;
                    }
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();
                if ("label_question_number_of_total_tries_team1".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_number_of_total_tries_team2"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 10;
                    }

                }
            }

        } else if (top14OneBgPattern.matcher(kup.getName()).find()) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();
                if ("label_question_number_of_total_tries_team1".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_number_of_total_tries_team2"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_number_of_total_drops".equals(label)) {

                    String answer = questionPrediction.getAnswer();

                    int dropsTeam1 = Integer.valueOf(game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_DROPS"));
                    int dropsTeam2 = Integer.valueOf(game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_DROPS"));

                    int total = dropsTeam1 + dropsTeam2;
                    String result = String.valueOf(total);

                    if (result.equals(answer)) {
                        points += 30;
                    }

                } else if ("label_question_number_of_total_penks".equals(label)) {

                    String answer = questionPrediction.getAnswer();

                    int penksTeam1 = Integer.valueOf(game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_PENKS"));
                    int penksTeam2 = Integer.valueOf(game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_PENKS"));

                    int total = penksTeam1 + penksTeam2;
                    String result = String.valueOf(total);

                    if (result.equals(answer)) {
                        points += 30;
                    }

                } else if ("label_question_first_team_that_tries".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_TRY");

                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_points_difference".equals(label)) {

                    int scoret1 = game.getScore().getScoreTeam1();
                    int scoret2 = game.getScore().getScoreTeam2();

                    final int diff = Math.abs(scoret1 - scoret2);

                    final String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        if (diff == 0) {
                            points += 20;
                        }
                    } else if (answer != null
                            && diff == Integer.valueOf(answer)) {
                        points += 20;
                    }

                } else if ("label_question_first_player_scores_left"
                        .equals(label)) {

                    String answer = questionPrediction.getAnswer();
                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY");

                    String[] players = result.split(",");
                    if (players.length == 0 && "0".equals(answer)) {
                        points += 20;
                    } else if (players.length > 0) {
                        String player = players[0];
                        if (player.equals(answer)) {
                            points += 20;
                        }
                    }

                } else if ("label_question_first_player_scores_right"
                        .equals(label)) {

                    String answer = questionPrediction.getAnswer();
                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY");

                    String[] players = result.split(",");
                    if (players.length == 0 && "0".equals(answer)) {
                        points += 20;
                    } else if (players.length > 0) {
                        String player = players[0];
                        if (player.equals(answer)) {
                            points += 20;
                        }
                    }
                } else {
                    throw new CoreException("Missing rules for kup with id="
                            + kup.getId() + " and name=" + kup.getName()
                            + "for label" + label);
                }

            }

        } else if (kup.getName().startsWith(
                "label_kup_title_201213_rugby_hcup_bg_d")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();
                if ("label_question_first_team_that_tries".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_TRY");

                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_number_of_total_tries".equals(label)) {

                    int tries1 = Integer.valueOf(game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_TRIES"));
                    int tries2 = Integer.valueOf(game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_TRIES"));
                    final int total = tries1 + tries2;

                    final String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        if (total == 0) {
                            points += 20;
                        }
                    } else if (answer != null
                            && total == Integer.valueOf(answer)) {
                        points += 20;
                    }

                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (top14BgPattern.matcher(kup.getName()).find()) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() == score.getScoreTeam2()) {
                    if (playerPrediction.size() == 0) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())
                    && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();
                if ("label_question_number_of_total_tries_team1".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM1_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 15;
                    }

                } else if ("label_question_number_of_total_tries_team2"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM2_NB_TRIES");
                    String answer = questionPrediction.getAnswer();
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set. If
                    // not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 15;
                    }

                } else if ("label_question_first_team_that_tries".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_TRY");

                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_points_difference".equals(label)) {

                    int scoret1 = game.getScore().getScoreTeam1();
                    int scoret2 = game.getScore().getScoreTeam2();

                    final int diff = Math.abs(scoret1 - scoret2);

                    final String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        if (diff == 0) {
                            points += 20;
                        }
                    } else if (answer != null
                            && diff == Integer.valueOf(answer)) {
                        points += 20;
                    }

                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith(
                "label_kup_title_2012_usopen_match_match")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                }

            } else if ("se".equals(prediction.getType())
                    && prediction instanceof PredictionGameScore) {

                PredictionGameScore gamePrediction = (PredictionGameScore) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();

                if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()
                        && score.getScoreTeam2() == gamePrediction
                                .getScoreTeam2()) {
                    points += 15;
                } else if (score.getScoreTeam1() > score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() > gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                } else if (score.getScoreTeam1() < score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() < gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith("label_kup_title_k8_usopen_")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                }

            }

        } else if (kup.getName().startsWith("label_kup_title_201213_kross_")
                || kup.getName()
                        .startsWith("label_kup_title_201213_cl_krossc_")
                || kup.getName()
                        .startsWith("label_kup_title_201213_el_klub3_d")
                || kup.getName().startsWith(
                        "label_kup_title_201213_cdll_kross_")) {

            if ("se".equals(prediction.getType())
                    && prediction instanceof PredictionGameScore) {

                PredictionGameScore gamePrediction = (PredictionGameScore) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();

                if (score.getScoreTeam1() == gamePrediction.getScoreTeam1()
                        && score.getScoreTeam2() == gamePrediction
                                .getScoreTeam2()) {
                    points += 15;
                } else if (score.getScoreTeam1() > score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() > gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                } else if (score.getScoreTeam1() < score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() < gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                } else if (score.getScoreTeam1() == score.getScoreTeam2()
                        && gamePrediction.getScoreTeam1() == gamePrediction
                                .getScoreTeam2()) {
                    points += 5;
                }

            } else if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())

            && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                String label = questionPrediction.getQuestion().getLabel();

                if ("label_question_first_player_that_scores".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_PLAYER_UUID_FIRST_GOAL");
                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    // If result is null let it fail with an NPE. It will be
                    // then retried later on when actual result will be set.
                    // If not it means there is an issue.
                    if (result.equals(answer)) {
                        points += 15;
                    }

                } else if ("label_question_winner_first_half".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_WINNER_FIRST_HALF");
                    String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        answer = "";
                    }

                    if (result.equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_winner_second_half".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_WINNER_SECOND_HALF");
                    String answer = questionPrediction.getAnswer();

                    if ("0".equals(answer)) {
                        answer = "";
                    }

                    if (result.equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_goals_difference_at_end"
                        .equals(label)) {

                    TournamentGameScore score = game.getScore();
                    String answer = questionPrediction.getAnswer();

                    int diffScore = Math.abs(score.getScoreTeam1()
                            - score.getScoreTeam2());

                    if (String.valueOf(diffScore).equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_team_first_scores".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_GOAL");
                    String answer = questionPrediction.getAnswer();
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 10;
                    }

                } else if ("label_question_extra_time".equals(label)) {

                    final String answer = questionPrediction.getAnswer();
                    final String result = game.getProperties().get(
                            "GAME_PROP_HAS_EXTRA_TIME");
                    if ("label_yes".equals(answer) && "1".equals(result)) {
                        points += 10;
                    } else if ("label_no".equals(answer) && !"1".equals(result)) {
                        points += 10;
                    }

                } else {
                    throw new CoreException("Missing rules for kup with id="
                            + kup.getId() + " and name=" + kup.getName()
                            + " and question=" + label);
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith("label_kup_title_201213_nba_k15_")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 7;
                    }
                }

            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().equals(
                "label_kup_title_201213_nba_match_match")
                || kup.getName().equals(
                        "label_kup_title_201213_nba_match_bulls")
                || kup.getName().equals(
                        "label_kup_title_201213_nba_match_lakers")
                || kup.getName().equals(
                        "label_kup_title_201213_nba_match_spurs")
                || kup.getName()
                        .equals("label_kup_title_201213_nba_match_heat")
                || kup.getName().equals(
                        "label_kup_title_201213_nba_match_lakers")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())

            && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                final String label = questionPrediction.getQuestion()
                        .getLabel();
                final String answer = questionPrediction.getAnswer();

                if ("label_question_basket_difference_points_range"
                        .equals(label)) {

                    final int diff = Math.abs(game.getScore().getScoreTeam1()
                            - game.getScore().getScoreTeam2());
                    if ("30".equals(answer) && diff >= 30) {
                        points += 22;
                    } else if ("20".equals(answer) && diff >= 20) {
                        points += 14;
                    } else if ("10".equals(answer) && diff >= 10) {
                        points += 8;
                    }

                }
            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else if (kup.getName().startsWith("label_kup_title_201213_nba_bg_")) {

            if ("ic".equals(prediction.getType())
                    && prediction instanceof PredictionGameOrderedContestantsList) {

                PredictionGameOrderedContestantsList gamePrediction = (PredictionGameOrderedContestantsList) prediction;
                TournamentGame game = gamePrediction.getTournamentGame();
                TournamentGameScore score = game.getScore();
                List<SportContestant> playerPrediction = gamePrediction
                        .getContestants();

                if (score.getScoreTeam1() > score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(0)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                } else if (score.getScoreTeam1() < score.getScoreTeam2()) {
                    if (playerPrediction.size() > 0
                            && game.getContestants().get(1)
                                    .equals(playerPrediction.get(0))) {
                        points += 5;
                    }
                }

            } else if ("q".equals(prediction.getType())

            && prediction instanceof PredictionGameQuestion) {

                PredictionGameQuestion questionPrediction = (PredictionGameQuestion) prediction;
                TournamentGame game = questionPrediction.getTournamentGame();

                final String label = questionPrediction.getQuestion()
                        .getLabel();
                String answer = questionPrediction.getAnswer();

                if ("label_question_basket_difference_points_range"
                        .equals(label)) {

                    final int diff = Math.abs(game.getScore().getScoreTeam1()
                            - game.getScore().getScoreTeam2());
                    if ("30".equals(answer) && diff >= 30) {
                        points += 22;
                    } else if ("20".equals(answer) && diff >= 20) {
                        points += 14;
                    } else if ("10".equals(answer) && diff >= 10) {
                        points += 8;
                    }

                } else if ("label_question_basket_180_plus".equals(label)) {

                    final int total = game.getScore().getScoreTeam1()
                            + game.getScore().getScoreTeam2();
                    if (total > 180 && "label_yes".equals(answer)) {
                        points += 7;
                    } else if (total <= 180 && "label_no".equals(answer)) {
                        points += 7;
                    }

                } else if ("label_question_basket_home_scores_first"
                        .equals(label)) {

                    final String result = game.getProperties().get(
                            "GAME_PROP_TEAM_UUID_FIRST_GOAL");
                    if ("label_yes".equals(answer)
                            && game.getContestants().get(0).getUUID()
                                    .equals(result)) {
                        points += 5;
                    } else if ("label_no".equals(answer)
                            && !game.getContestants().get(0).getUUID()
                                    .equals(result)) {
                        points += 5;
                    }

                } else if ("label_question_team_winner_half_time".equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_WINNER_FIRST_HALF");

                    if ("0".equals(answer)) {
                        answer = "";
                    }

                    if (result.equals(answer)) {
                        points += 7;
                    }

                } else if ("label_question_which_team_best_scorer"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_BASKET_TEAM_BEST_SCORER");
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 5;
                    }

                } else if ("label_question_which_team_best_intercepteur"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_BASKET_TEAM_BEST_INTERCEPTEUR");
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 7;
                    }

                } else if ("label_question_which_team_best_contreur"
                        .equals(label)) {

                    String result = game.getProperties().get(
                            "GAME_PROP_BASKET_TEAM_BEST_CONTREUR");
                    if ("0".equals(answer)) {
                        answer = "-1";
                    }
                    if (result.equals(answer)) {
                        points += 7;
                    }

                } else {
                    throw new CoreException("Missing rules for kup with id="
                            + kup.getId() + " for label=" + label);
                }
            } else {
                throw new CoreException("Missing rules for kup with id="
                        + kup.getId() + " and name=" + kup.getName());
            }

        } else {
            throw new CoreException("Missing rules for kup with id="
                    + kup.getId() + " and name=" + kup.getName());
        }

        return points;
    }
}
