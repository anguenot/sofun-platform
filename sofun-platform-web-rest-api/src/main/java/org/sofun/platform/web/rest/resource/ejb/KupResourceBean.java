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

package org.sofun.platform.web.rest.resource.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.kup.KupSearchResults;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.PredictionQuestionKupTiebreaker;
import org.sofun.core.api.prediction.tournament.PredictionGameQuestion;
import org.sofun.core.api.prediction.tournament.PredictionGameScore;
import org.sofun.core.api.prediction.tournament.contestant.PredictionGameOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionRoundOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionSeasonOrderedContestantsList;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.team.Team;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.exception.ReSTRuntimeException;
import org.sofun.platform.web.rest.api.kup.ReSTKup;
import org.sofun.platform.web.rest.api.kup.RestKupSearchResults;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.question.ReSTQuestion;
import org.sofun.platform.web.rest.api.team.ReSTMemberRankingTable;
import org.sofun.platform.web.rest.api.team.ReSTMemberRankingTableEntry;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentGame;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentGameImpl;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentRound;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentSeason;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentStage;
import org.sofun.platform.web.rest.resource.ejb.api.KupResource;
import org.sofun.platform.web.rest.util.JSONUtil;

/**
 * Kup Web APIs.
 * 
 * <p/>
 * 
 * Provides ReST APIs to interact w/ Kups.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
public class KupResourceBean extends AbstractResource implements KupResource {

    private static final long serialVersionUID = 5280338926261146085L;

    private static final Log log = LogFactory.getLog(KupResourceBean.class);

    @Override
    public Response searchKups(Map<String, String> params) throws ReSTException {
        KupSearchResults results;
        try {
            results = getKupService().search(params);
        } catch (CoreException e) {
            throw new ReSTRuntimeException(500, e.getMessage());
        }
        return Response.status(202).entity(new RestKupSearchResults(results))
                .build();
    }

    @Override
    public Response placeKupBet(long kupId, String email) throws ReSTException {

        Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        try {
            getKupService().placeKupBet(member, kup);
        } catch (CoreException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return Response.status(500).entity("Internal error.").build();
        }

        return Response.status(202).entity("OK").build();

    }

    @Override
    public Response getKupCredentials(long kupId, String email) {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        Set<String> credentials = getKupService().getCredentials(kup, member);
        return Response.status(202).entity(credentials).build();

    }

    @Override
    public Response getKup(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        return Response.status(202).entity(new ReSTKup(kup)).build();

    }

    @Override
    public Response getKupRounds(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTTournamentRound> rounds = new ArrayList<ReSTTournamentRound>();
        for (TournamentRound coreRound : kup.getBettableRounds()) {
            rounds.add(new ReSTTournamentRound(coreRound));
        }

        return Response.status(202).entity(rounds).build();

    }

    @Override
    public Response getKupGames(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTTournamentGame> games = new ArrayList<ReSTTournamentGame>();
        for (TournamentGame coreGame : kup.getBettableGames()) {
            games.add(new ReSTTournamentGameImpl(coreGame));
        }

        return Response.status(202).entity(games).build();

    }

    @Override
    public Response getKupSeasons(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTTournamentSeason> seasons = new ArrayList<ReSTTournamentSeason>();
        for (TournamentSeason coreSeason : kup.getBettableTournaments()) {
            seasons.add(new ReSTTournamentSeason(coreSeason));
        }

        return Response.status(202).entity(seasons).build();

    }

    @Override
    public Response getKupStages(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTTournamentStage> stages = new ArrayList<ReSTTournamentStage>();
        for (TournamentStage coreStage : kup.getBettableStages()) {
            stages.add(new ReSTTournamentStage(coreStage));
        }

        return Response.status(202).entity(stages).build();

    }

    @Override
    public Response getKupQuestions(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTQuestion> questions = new ArrayList<ReSTQuestion>();
        for (Question question : kup.getBettableQuestion()) {
            questions.add(new ReSTQuestion(question));
        }

        return Response.status(202).entity(questions).build();

    }

    @Override
    public Response getKupQuestionsTiebreaker(long kupId) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTQuestion> questions = new ArrayList<ReSTQuestion>();
        for (QuestionKupTiebreaker question : kup.getQuestionsTiebreaker()) {
            questions.add(new ReSTQuestion(question));
        }

        return Response.status(202).entity(questions).build();

    }

    @Override
    public Response addKupPredictions(long kupId, String email,
            Map<String, String> params) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        // Automatically add member to team when adding a prediction.
        Team t = kup.getTeam();
        if (t.getId() != 740 && (!t.isMember(member) || !t.isAdmin(member))) {
            t.addMember(member);
        }

        final Map<String, String> se = JSONUtil
                .getMapFromJSON(params.get("se"));
        if (se != null) {
            Map<String, List<Integer>> predictions = new HashMap<String, List<Integer>>();
            for (Map.Entry<String, String> entry : se.entrySet()) {
                final String key = entry.getKey();
                final int score = Integer.valueOf(entry.getValue());
                String[] teamScore = key.split("_");
                final String gameUUID = teamScore[0];
                final int teamPosition = Integer.valueOf(teamScore[1]);
                List<Integer> gameScore;
                if (predictions.get(gameUUID) == null) {
                    gameScore = new ArrayList<Integer>();
                    predictions.put(gameUUID, gameScore);
                    gameScore.add(0);
                    gameScore.add(0);
                } else {
                    gameScore = predictions.get(gameUUID);
                }
                if (teamPosition == 1) {
                    gameScore.remove(0);
                    gameScore.add(0, score);
                } else if (teamPosition == 2) {
                    gameScore.remove(1);
                    gameScore.add(1, score);
                }
            }
            for (Map.Entry<String, List<Integer>> prediction : predictions
                    .entrySet()) {
                final String uuid = prediction.getKey();
                List<Integer> score = prediction.getValue();
                if (score.size() != 2) {
                    return Response.status(400).entity("Invalid prediction.")
                            .build();
                }
                TournamentGame game = getSportService().getTournamentGame(uuid);
                try {
                    getKupService().addPredictionScore("se", member, kup, game,
                            score);
                } catch (CoreException e) {
                    return Response.status(500).entity("Internal error")
                            .build();
                }
            }
        }

        final Map<String, String> ic = JSONUtil
                .getMapFromJSON(params.get("ic"));
        if (ic != null) {
            for (Map.Entry<String, String> entry : ic.entrySet()) {
                final int choice = Integer.valueOf(entry.getValue());
                //
                // Below are expected choice values
                //
                // -1 : no prediction
                // 1 : left side winner
                // 2 : drawn game
                // 3 : right side winner
                //
                String gameUUID = entry.getKey();
                TournamentGame game = getSportService().getTournamentGame(
                        gameUUID);
                SportContestant c1 = game.getContestants().get(0);
                SportContestant c2 = game.getContestants().get(1);
                List<SportContestant> prediction = new ArrayList<SportContestant>();
                if (c1 != null && c2 != null) {
                    if (choice == 1) {
                        prediction.add(c1);
                        prediction.add(c2);
                    } else if (choice == 3) {
                        prediction.add(c2);
                        prediction.add(c1);
                    } else if (choice == 2) {
                        // No winner: no contestant but prediction created.
                        // Means no
                        // winner.
                    } else if (choice == -1) {
                        prediction = null;
                    }
                }
                try {
                    getKupService().addPrediction("ic", member, kup, game,
                            prediction);
                } catch (CoreException e) {
                    return Response.status(500).entity("Internal error")
                            .build();
                }
            }
        }

        final Map<String, String> questions = JSONUtil.getMapFromJSON(params
                .get("q"));
        if (questions != null) {
            for (Map.Entry<String, String> entry : questions.entrySet()) {
                final String[] gameQuestion = entry.getKey().split("_");
                if (gameQuestion.length != 2) {
                    continue;
                }
                final String gameUUID = gameQuestion[0].replace("'", "");
                TournamentGame game = getSportService().getTournamentGame(
                        gameUUID);
                final long questionUUID = Long.valueOf(gameQuestion[1].replace(
                        "'", ""));
                Question question = getPredictionService().getQuestionById(
                        questionUUID);
                if (question != null && game != null) {
                    String choice = entry.getValue();
                    if ("-1".equals(choice)) {
                        choice = null;
                    }
                    try {
                        getKupService().addGameQuestionPrediction("q", member,
                                kup, game, question, choice);
                    } catch (CoreException e) {
                        return Response.status(500).entity("Internal error")
                                .build();
                    }
                }
            }
        }

        final Map<String, String> tiebreakers = JSONUtil.getMapFromJSON(params
                .get("tb"));
        if (tiebreakers != null) {
            for (Map.Entry<String, String> entry : tiebreakers.entrySet()) {
                final long questionUUID = Long.valueOf(entry.getKey());
                QuestionKupTiebreaker question = getPredictionService()
                        .getQuestionKupTiebreakerById(questionUUID);
                if (question != null) {
                    String choice = entry.getValue();
                    if ("-1".equals(choice)) {
                        choice = null;
                    }
                    try {
                        getKupService().addQuestionKupTiebreaker(member, kup,
                                question, choice);
                    } catch (CoreException e) {
                        return Response.status(500).entity("Internal error")
                                .build();
                    }
                }
            }
        }

        final List<String> full = JSONUtil.getListFromJSON(params.get("full"));
        if (full != null) {
            List<SportContestant> contestants = new ArrayList<SportContestant>();
            for (String teamId : full) {
                if (teamId == null) {
                    contestants.add(null);
                } else {
                    SportContestant contestant = getSportService()
                            .getSportContestantTeam(teamId);
                    contestants.add(contestant);
                }
            }
            if (contestants.size() > 0) {
                final long seasonId = Long.valueOf(params.get("seasonId"));
                TournamentSeason season = getSportService()
                        .getTournamentSeason(seasonId);
                getPredictionService().createPredictionFor(member, season,
                        "full", contestants, kup);
            }
        }

        final String metaType = params.get("type");
        if (metaType == null) {
            // XXX this block with type detection should be a generic one.
            // Aboe need some cleanup
        } else {

            final List<String> playerPredictions = JSONUtil
                    .getListFromJSON(params.get(metaType));

            if (metaType.equals("euro2012_final_stage")) {

                if (playerPredictions != null) {

                    final String seasonUUID = params.get("seasonUUID");
                    List<SportContestant> teams = new ArrayList<SportContestant>();
                    for (String teamUUID : playerPredictions) {
                        if (teamUUID == null || "null".equals(teamUUID)) {
                            teams.add(null);
                        } else {
                            SportContestant team = getSportService()
                                    .getSportContestantTeam(teamUUID);
                            teams.add(team);
                        }
                    }

                    if (teams.size() > 0) {
                        final long seasonId = Long.valueOf(seasonUUID);
                        TournamentSeason season = getSportService()
                                .getTournamentSeason(seasonId);
                        getPredictionService().createPredictionFor(member,
                                season, metaType, teams, kup);
                    }

                }

            } else if (metaType.equals("cycling_maillot_jaune")
                    || metaType.equals("cycling_maillot_blanc")
                    || metaType.equals("cycling_maillot_vert")
                    || metaType.equals("cycling_maillot_apois")
                    || metaType.equals("cycling_podium_individual")
                    || metaType.equals("cycling_podium_team")) {

                List<SportContestant> cyclist = new ArrayList<SportContestant>();
                for (String driverId : playerPredictions) {
                    if (driverId == null) {
                        cyclist.add(null);
                    } else {
                        SportContestant driver;
                        if (metaType.equals("cycling_podium_team")) {
                            driver = getSportService().getSportContestantTeam(
                                    driverId);
                        } else {
                            driver = getSportService()
                                    .getSportContestantPlayer(driverId);
                        }
                        cyclist.add(driver);
                    }
                }
                if (cyclist.size() > 0) {

                    final long seasonId = Long.valueOf(params.get("seasonId"));
                    final String roundUUID = params.get("roundUUID");
                    TournamentSeason season = getSportService()
                            .getTournamentSeason(seasonId);
                    TournamentRound round = season.getRoundByUUID(roundUUID);
                    getPredictionService().createPredictionFor(member, round,
                            metaType, cyclist, kup);

                }

            } else {

                if (playerPredictions != null) {

                    List<SportContestant> drivers = new ArrayList<SportContestant>();
                    for (String driverId : playerPredictions) {
                        if (driverId == null) {
                            drivers.add(null);
                        } else {
                            SportContestant driver = getSportService()
                                    .getSportContestantPlayer(driverId);
                            drivers.add(driver);
                        }
                    }
                    if (drivers.size() > 0) {
                        final long seasonId = Long.valueOf(params
                                .get("seasonId"));
                        final String stageName = params.get("stageName");
                        final String roundName = params.get("roundName");
                        TournamentSeason season = getSportService()
                                .getTournamentSeason(seasonId);
                        TournamentStage stage = season
                                .getStageByName(stageName);
                        TournamentRound round = stage.getRoundByName(roundName);
                        getPredictionService().createPredictionFor(member,
                                round, metaType, drivers, kup);
                    }

                }
            }
        }

        return Response.status(202).entity("OK").build();
    }

    @Override
    public Response getKupPredictions(long kupId, String email, String type)
            throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        List<Map<String, String>> response = new ArrayList<Map<String, String>>();

        if ("ic".equals(type)) {
            Map<String, String> predictions = new HashMap<String, String>();
            Map<String, String> results = new HashMap<String, String>();
            Map<String, String> status = new HashMap<String, String>();
            for (Prediction prediction : getPredictionService()
                    .getPredictionsFor(kup, member, type)) {
                if (prediction instanceof PredictionGameOrderedContestantsList) {

                    PredictionGameOrderedContestantsList corePrediction = (PredictionGameOrderedContestantsList) prediction;

                    String key = corePrediction.getTournamentGame().getUUID();
                    String value = "";

                    if (corePrediction.isDrawn()) {
                        value = "2";
                    } else if (corePrediction.getContestants().size() == 0) {
                        if (corePrediction.getKup().getName()
                                .equals("label_kup_title_votre_tableau")) {
                            // BBB for one particular Kup.
                            // corePrediction.setDrawn(true);
                            value = "2";
                        } else {
                            value = "-1";
                        }
                    } else if (corePrediction
                            .getContestants()
                            .get(0)
                            .equals(corePrediction.getTournamentGame()
                                    .getContestants().get(0))) {
                        value = "1";
                    } else if (corePrediction
                            .getContestants()
                            .get(0)
                            .equals(corePrediction.getTournamentGame()
                                    .getContestants().get(1))) {
                        value = "3";
                    }

                    predictions.put(key, value);
                    results.put(key, String.valueOf(corePrediction.getPoints()));
                    status.put(key,
                            String.valueOf(corePrediction.isPointsComputed()));
                }

                response.add(predictions);
                response.add(results);
                response.add(status);

            }
            return Response.status(202).entity(response).build();
        } else if ("se".equals(type)) {
            Map<String, String> predictions = new HashMap<String, String>();
            Map<String, String> results = new HashMap<String, String>();
            Map<String, String> status = new HashMap<String, String>();
            for (Prediction prediction : getPredictionService()
                    .getPredictionsFor(kup, member, type)) {

                PredictionGameScore corePrediction = (PredictionGameScore) prediction;

                String key1 = String.valueOf(corePrediction.getTournamentGame()
                        .getUUID()) + "_1";
                String key2 = String.valueOf(corePrediction.getTournamentGame()
                        .getUUID()) + "_2";

                String value1 = String.valueOf(corePrediction.getScoreTeam1());
                String value2 = String.valueOf(corePrediction.getScoreTeam2());

                predictions.put(key1, value1);
                predictions.put(key2, value2);

                results.put(corePrediction.getTournamentGame().getUUID(),
                        String.valueOf(corePrediction.getPoints()));

                status.put(corePrediction.getTournamentGame().getUUID(),
                        String.valueOf(corePrediction.isPointsComputed()));

            }

            response.add(predictions);
            response.add(results);
            response.add(status);

            return Response.status(202).entity(response).build();
        } else if ("q".equals(type)) {
            Map<String, String> predictions = new HashMap<String, String>();
            Map<String, String> results = new HashMap<String, String>();
            Map<String, String> status = new HashMap<String, String>();
            for (Prediction prediction : getPredictionService()
                    .getPredictionsFor(kup, member, type)) {

                PredictionGameQuestion corePrediction = (PredictionGameQuestion) prediction;

                String key = corePrediction.getTournamentGame().getUUID();
                key += "_";
                key += String.valueOf(corePrediction.getQuestion().getId());

                String value = corePrediction.getAnswer();

                predictions.put(key, value);
                results.put(key, String.valueOf(corePrediction.getPoints()));
                status.put(key,
                        String.valueOf(corePrediction.isPointsComputed()));

            }
            response.add(predictions);
            response.add(results);
            response.add(status);
            return Response.status(202).entity(response).build();

        } else if ("tb".equals(type)) {
            Map<String, String> predictions = new HashMap<String, String>();
            Map<String, String> results = new HashMap<String, String>();
            Map<String, String> status = new HashMap<String, String>();
            for (Prediction prediction : getPredictionService()
                    .getPredictionsFor(kup, member, type)) {

                PredictionQuestionKupTiebreaker corePrediction = (PredictionQuestionKupTiebreaker) prediction;
                String key = String.valueOf(corePrediction.getQuestion()
                        .getId());

                String value = corePrediction.getAnswer();

                predictions.put(key, value);
                results.put(key, String.valueOf(countTotalScores(kup)));
                status.put(key,
                        String.valueOf(corePrediction.isPointsComputed()));

            }
            response.add(predictions);
            response.add(results);
            response.add(status);
            return Response.status(202).entity(response).build();

        } else if ("full".equals(type)) {

            List<String> preds = new ArrayList<String>();

            List<Prediction> predictions = getPredictionService()
                    .getPredictionsFor(kup, member, type);
            if (predictions.size() > 0) {

                Prediction prediction = predictions.get(0); // XXX only 1 full
                                                            // prediction type
                                                            // for a given Kup.

                PredictionSeasonOrderedContestantsList corePrediction = (PredictionSeasonOrderedContestantsList) prediction;
                for (SportContestant contestant : corePrediction
                        .getContestants()) {
                    if (contestant != null) {
                        preds.add(contestant.getUUID());
                    } else {
                        preds.add(null);
                    }
                }

            }

            return Response.status(202).entity(preds).build();

        } else if ("euro2012_final_stage".equals(type)) {

            List<String> preds = new ArrayList<String>();

            List<Prediction> predictions = getPredictionService()
                    .getPredictionsFor(kup, member, type);
            if (predictions.size() > 0) {

                Prediction prediction = predictions.get(0); // XXX only 1 full
                                                            // prediction type
                                                            // for a given Kup.

                PredictionSeasonOrderedContestantsList corePrediction = (PredictionSeasonOrderedContestantsList) prediction;
                for (SportContestant contestant : corePrediction
                        .getContestants()) {
                    if (contestant != null) {
                        preds.add(contestant.getUUID());
                    } else {
                        preds.add(null);
                    }
                }

            }

            return Response.status(202).entity(preds).build();

        } else if (type.startsWith("cycling")) {

            List<List<List<String>>> resp = new ArrayList<List<List<String>>>();
            List<List<String>> preds = new ArrayList<List<String>>();
            List<String> pred = new ArrayList<String>();
            List<String> lastModified = new ArrayList<String>();
            List<Prediction> predictions = getPredictionService()
                    .getPredictionsFor(kup, member, type);
            for (Prediction prediction : predictions) {
                pred = new ArrayList<String>();
                lastModified = new ArrayList<String>();
                PredictionRoundOrderedContestantsList corePrediction = (PredictionRoundOrderedContestantsList) prediction;
                for (SportContestant contestant : corePrediction
                        .getContestants()) {
                    if (contestant != null) {
                        pred.add(contestant.getUUID());
                    } else {
                        pred.add(null);
                    }
                }
                preds.add(pred);
                lastModified.add(String.valueOf(String.valueOf(corePrediction
                        .getTournamentRound().getId())));
                preds.add(lastModified);
            }
            resp.add(preds);
            return Response.status(202).entity(resp).build();

        } else {
            List<List<String>> resp = new ArrayList<List<String>>();
            List<String> preds = new ArrayList<String>();
            List<String> lastModified = new ArrayList<String>();
            List<Prediction> predictions = getPredictionService()
                    .getPredictionsFor(kup, member, type);
            if (predictions.size() > 0) {
                Prediction prediction = null;
                for (Prediction coreP : predictions) {
                    if (type.equals(coreP.getType())) {
                        prediction = coreP;
                    }
                }
                PredictionRoundOrderedContestantsList corePrediction = (PredictionRoundOrderedContestantsList) prediction;
                for (SportContestant contestant : corePrediction
                        .getContestants()) {
                    if (contestant != null) {
                        preds.add(contestant.getUUID());
                    } else {
                        preds.add(null);
                    }
                    lastModified.add(String.valueOf(corePrediction
                            .getLastModified().getTime()));
                }
            }
            resp.add(preds);
            resp.add(lastModified);
            return Response.status(202).entity(resp).build();
        }

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
            // We do not count scores for tennis games where game has been
            // stopped.
            if (game.getScore() != null && game.getWinner() != null) {
                score += game.getScore().getScoreTeam1();
                score += game.getScore().getScoreTeam2();
            }
        }
        return score;
    }

    @Override
    public Response getKupGamesByStatus(long kupId, String gameStatus)
            throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        List<ReSTTournamentGame> games = new ArrayList<ReSTTournamentGame>();
        for (TournamentGame coreGame : kup.getBettableGames()) {
            if (coreGame.getGameStatus().equals(gameStatus)) {
                games.add(new ReSTTournamentGameImpl(coreGame));
            }
        }

        return Response.status(202).entity(games).build();

    }

    @Override
    public Response getKupRanking(long kupId, int offset, int batchSize)
            throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }
        KupRankingTable coreTable;
        try {
            coreTable = getKupService().getKupRanking(kup, offset, batchSize);
        } catch (CoreException e) {
            return Response.status(500).entity("An internal error occured")
                    .build();
        }

        ReSTMemberRankingTable table = new ReSTMemberRankingTable(coreTable,
                offset, batchSize);

        // Insert tie breaker question here so that client won't have to.
        if (kup.getQuestionsTiebreaker().size() > 0) {
            // XXX we only support one (1) right now.
            QuestionKupTiebreaker q = kup.getQuestionsTiebreaker().get(0);
            for (ReSTMemberRankingTableEntry entry : table.getEntries()) {
                final String a = getPredictionService()
                        .getPredictionQuestionKupTieBreakerFor(
                                getMemberService().getMember(
                                        entry.getMember().getEmail()), kup, q)
                        .getAnswer();
                entry.setTieBreakerQuestionAnswer(a);
            }
        }

        return Response.status(202).entity(table).build();
    }

    @Override
    public Response getKupRankingFor(long kupId, String email, int offset,
            int batchSize) throws ReSTException {
        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }
        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }
        KupRankingTable coreTable;
        try {
            coreTable = getKupService().getKupRanking(kup, offset, batchSize);
        } catch (CoreException e) {
            return Response.status(500).entity("An internal error occured")
                    .build();
        }

        ReSTMemberRankingTable table = new ReSTMemberRankingTable(coreTable,
                offset, batchSize, new ReSTMember(member));

        // Insert tie breaker question here so that client won't have to.
        if (kup.getQuestionsTiebreaker().size() > 0) {
            // XXX we only support one (1) right now.
            QuestionKupTiebreaker q = kup.getQuestionsTiebreaker().get(0);
            for (ReSTMemberRankingTableEntry entry : table.getEntries()) {
                Member coreMember = getMemberService().getMember(
                        entry.getMember().getEmail());
                PredictionQuestionKupTiebreaker pq = getPredictionService()
                        .getPredictionQuestionKupTieBreakerFor(coreMember, kup,
                                q);
                String a = "0";
                if (pq != null) {
                    a = pq.getAnswer();
                }
                entry.setTieBreakerQuestionAnswer(a);
            }
        }

        return Response.status(202).entity(table).build();
    }

    @Override
    public Response getKupPredictionsLastModifiedFor(long kupId, String email)
            throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }
        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        Date date;
        try {
            date = getKupService().getPredictionsLastModifiedFor(member, kup);
        } catch (CoreException e) {
            return Response.status(500).entity("Internal error").build();
        }
        return Response.status(202).entity(date).build();
    }

    @Override
    public Response invite(long kupId, Map<String, String> params)
            throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        final String inviter = params.get("inviter_email");
        Member member = getMemberService().getMember(inviter);
        if (member == null) {
            return Response.status(400).entity("Inviter member not found")
                    .build();
        }

        final Map<String, String> recipients = JSONUtil.getMapFromJSON(params
                .get("email_rcpts"));

        Map<String, String> emailing = new HashMap<String, String>();
        emailing.put("subject", params.get("email_subject"));
        emailing.put("body", params.get("email_body"));
        emailing.put("emails", StringUtils.join(recipients.keySet(), ','));

        try {
            getKupService().invite(kup, member, emailing);
        } catch (CoreException e) {
            return Response.status(500)
                    .entity("An error occured while sending invites").build();
        }

        return Response.status(202).entity(true).build();
    }

    @Override
    public Response hasPredictions(long kupId, String email)
            throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        final boolean hasPrediction = getKupService()
                .hasPrediction(member, kup);
        if (hasPrediction) {
            return Response.status(202).entity("OK").build();
        } else {
            return Response.status(400).entity("NOTOK").build();
        }

    }

    @Override
    public Response hasBet(long kupId, String email) throws ReSTException {

        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        boolean hasBet;
        try {
            hasBet = getKupService().hasBet(member, kup);
        } catch (CoreException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
        if (hasBet) {
            return Response.status(202).entity("OK").build();
        } else {
            return Response.status(400).entity("NOTOK").build();
        }

    }

    @Override
    public Response getKupRankingFacebookFor(long kupId, String email,
            int offset, int batchSize) throws ReSTException {
        final Kup kup = getKupService().getKupById(kupId);
        if (kup == null) {
            return Response.status(400).entity("Kup not found").build();
        }
        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }
        KupRankingTable coreTable;
        try {
            coreTable = getKupService().getKupRanking(kup, offset, batchSize);
        } catch (CoreException e) {
            return Response.status(500).entity("An internal error occured")
                    .build();
        }
        ReSTMemberRankingTable table = new ReSTMemberRankingTable(coreTable,
                offset, batchSize, new ReSTMember(member), true, null);
        return Response.status(202).entity(table).build();
    }

}
