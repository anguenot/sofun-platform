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

package org.sofun.core.prediction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.PredictionQuestionKupTiebreaker;
import org.sofun.core.api.prediction.PredictionService;
import org.sofun.core.api.prediction.tournament.PredictionGameQuestion;
import org.sofun.core.api.prediction.tournament.PredictionGameScore;
import org.sofun.core.api.prediction.tournament.contestant.PredictionGameOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionRoundOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionSeasonOrderedContestantsList;
import org.sofun.core.api.prediction.tournament.contestant.PredictionStageOrderedContestantsList;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.remote.PredictionServiceRemote;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.prediction.tournament.PredictionGameQuestionImpl;
import org.sofun.core.prediction.tournament.PredictionGameScoreImpl;
import org.sofun.core.prediction.tournament.contestant.PredictionGameOrderedContestantsListImpl;
import org.sofun.core.prediction.tournament.contestant.PredictionRoundOrderedContestantsListImpl;
import org.sofun.core.prediction.tournament.contestant.PredictionSeasonOrderedContestantsListImpl;
import org.sofun.core.prediction.tournament.contestant.PredictionStageOrderedContestantsListImpl;
import org.sofun.core.question.QuestionImpl;
import org.sofun.core.question.QuestionKupTiebreakerImpl;

/**
 * Prediction service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(PredictionServiceLocal.class)
@Remote(PredictionServiceRemote.class)
public class PredictionServiceImpl implements PredictionService {

    private static final long serialVersionUID = -6395292367248839881L;

    private static final Log log = LogFactory
            .getLog(PredictionServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    public PredictionServiceImpl() {
        super();
    }

    public PredictionServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    private Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public PredictionStageOrderedContestantsList getPredictionFor(
            Member member, TournamentStage stage, String type, Kup kup) {

        if (member == null) {
            return null;
        }

        String queryStr = "from "
                + PredictionStageOrderedContestantsListImpl.class
                        .getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.stage.uuid=:uuid and m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("uuid", stage.getUUID());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("type", type);

        try {
            return (PredictionStageOrderedContestantsList) query
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public PredictionSeasonOrderedContestantsList getPredictionFor(
            Member member, TournamentSeason season, String type, Kup kup) {

        if (member == null) {
            return null;
        }

        String queryStr = "from "
                + PredictionSeasonOrderedContestantsListImpl.class
                        .getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.season.uuid=:uuid and m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("uuid", season.getUUID());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("type", type);

        try {
            return (PredictionSeasonOrderedContestantsList) query
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public PredictionGameOrderedContestantsList getPredictionFor(Member member,
            TournamentGame game, String type, Kup kup) {
        if (member == null) {
            return null;
        }

        String queryStr = "from "
                + PredictionGameOrderedContestantsListImpl.class
                        .getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.game.uuid=:uuid and m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("uuid", game.getUUID());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("type", type);

        try {
            return (PredictionGameOrderedContestantsList) query
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public PredictionGameScore getPredictionScoreFor(Member member,
            TournamentGame game, String type, Kup kup) {
        if (member == null) {
            return null;
        }

        String queryStr = "from "
                + PredictionGameScoreImpl.class.getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.game.uuid=:uuid and m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("uuid", game.getUUID());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("type", type);

        try {
            return (PredictionGameScore) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public PredictionGameQuestion getPredictionQuestionFor(Member member,
            TournamentGame game, String type, Kup kup, Question question) {

        String queryStr = "from "
                + PredictionGameQuestionImpl.class.getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.game.uuid=:uuid and m.kup.id=:kup_id and m.question.id=:question_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("uuid", game.getUUID());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("question_id", question.getId());
        query.setParameter("type", type);

        try {
            return (PredictionGameQuestion) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public PredictionRoundOrderedContestantsList getPredictionFor(
            Member member, TournamentRound round, String type, Kup kup) {

        if (member == null) {
            return null;
        }

        String queryStr = "from "
                + PredictionRoundOrderedContestantsListImpl.class
                        .getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.round.uuid=:uuid and m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("uuid", round.getUUID());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("type", type);

        try {
            return (PredictionRoundOrderedContestantsList) query
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public boolean hasPredictions(Member member, Kup kup) {

        if (member == null || kup == null) {
            return false;
        }

        String queryStr = "from " + PredictionImpl.class.getSimpleName()
                + " m where m.member.email=:email AND m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("kup_id", kup.getId());

        @SuppressWarnings("unchecked")
        List<Prediction> results = query.getResultList();
        return results.size() > 0 ? true : false;

    }

    @Override
    public PredictionGameOrderedContestantsList createPredictionFor(
            Member member, TournamentGame game, String type,
            List<SportContestant> contestants, Kup kup) {

        PredictionGameOrderedContestantsList prediction = getPredictionFor(
                member, game, type, kup);

        if (prediction == null) {
            prediction = new PredictionGameOrderedContestantsListImpl();
            prediction.setType(type);
            prediction.setMember(member);
            prediction.setTournamentGame(game);
            prediction.setKup(kup);

            kup.addMember(member);
            if (kup.getType().equals(KupType.FREE)) {
                kup.addParticipant(member);
            } else {
                boolean hasBet;
                try {
                    hasBet = kups.hasBet(member, kup);
                } catch (CoreException e) {
                    log.error("An error occured while checking member participation."
                            + " member email="
                            + member.getEmail()
                            + " kup uuid=" + kup.getId());
                    hasBet = false;
                }
                if (hasBet) {
                    kup.addParticipant(member);
                }
            }
            em.persist(prediction);
            log.info("New game prediction (ordered) saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on game having uuid="
                    + game.getUUID());
        } else {
            prediction.setLastModified(new Date());
            log.info("Game prediction (ordered) updated for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on game having uuid="
                    + game.getUUID());
        }

        // Update
        if (contestants != null && contestants.size() == 0) {
            prediction.setDrawn(true);
        } else {
            prediction.setDrawn(false);
        }
        prediction.setContestants(contestants);

        return prediction;
    }

    @Override
    public PredictionGameScore createPredictionScoreFor(Member member,
            TournamentGame game, String type, List<Integer> score, Kup kup) {

        PredictionGameScore prediction = getPredictionScoreFor(member, game,
                type, kup);

        if (prediction == null) {
            prediction = new PredictionGameScoreImpl();
            prediction.setType(type);
            prediction.setMember(member);
            prediction.setTournamentGame(game);
            prediction.setKup(kup);
            kup.addMember(member);
            if (kup.getType().equals(KupType.FREE)) {
                kup.addParticipant(member);
            } else {
                boolean hasBet;
                try {
                    hasBet = kups.hasBet(member, kup);
                } catch (CoreException e) {
                    log.error("An error occured while checking member participation."
                            + " member email="
                            + member.getEmail()
                            + " kup uuid=" + String.valueOf(kup.getId()));
                    hasBet = false;
                }
                if (hasBet) {
                    kup.addParticipant(member);
                }
            }
            em.persist(prediction);
            log.info("New game prediction (score) saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on game having uuid="
                    + game.getUUID());
        } else {
            prediction.setLastModified(new Date());
            log.info("Game prediction (score) updated for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on game having uuid="
                    + game.getUUID());
        }

        // Update
        prediction.setScoreTeam1(score.get(0));
        prediction.setScoreTeam2(score.get(1));

        return prediction;

    }

    @Override
    public PredictionGameQuestion createQuestionPredictionFor(Member member,
            TournamentGame game, String type, Question question, Kup kup,
            String choice) {

        PredictionGameQuestion prediction = getPredictionQuestionFor(member,
                game, type, kup, question);

        if (prediction == null) {
            prediction = new PredictionGameQuestionImpl();
            prediction.setType(type);
            prediction.setMember(member);
            prediction.setTournamentGame(game);
            prediction.setKup(kup);
            prediction.setQuestion(question);
            kup.addMember(member);
            if (kup.getType().equals(KupType.FREE)) {
                kup.addParticipant(member);
            } else {
                boolean hasBet;
                try {
                    hasBet = kups.hasBet(member, kup);
                } catch (CoreException e) {
                    log.error("An error occured while checking member participation."
                            + " member email="
                            + member.getEmail()
                            + " kup uuid=" + String.valueOf(kup.getId()));
                    hasBet = false;
                }
                if (hasBet) {
                    kup.addParticipant(member);
                }
            }
            em.persist(prediction);
            log.info("New game prediction (question) saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on game having uuid="
                    + game.getUUID());
        } else {
            prediction.setLastModified(new Date());
            log.info("Game prediction (question) updated for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on game having uuid="
                    + game.getUUID());
        }

        // Update
        prediction.setAswer(choice);

        return prediction;

    }

    @Override
    public PredictionRoundOrderedContestantsList createPredictionFor(
            Member member, TournamentRound round, String type,
            List<SportContestant> contestants, Kup kup) {

        PredictionRoundOrderedContestantsList prediction = getPredictionFor(
                member, round, type, kup);

        if (prediction == null) {
            prediction = new PredictionRoundOrderedContestantsListImpl();
            prediction.setType(type);
            prediction.setMember(member);
            prediction.setTournamentRound(round);
            prediction.setKup(kup);
            kup.addMember(member);
            if (kup.getType().equals(KupType.FREE)) {
                kup.addParticipant(member);
            } else {
                boolean hasBet;
                try {
                    hasBet = kups.hasBet(member, kup);
                } catch (CoreException e) {
                    log.error("An error occured while checking member participation."
                            + " member email="
                            + member.getEmail()
                            + " kup uuid=" + String.valueOf(kup.getId()));
                    hasBet = false;
                }
                if (hasBet) {
                    kup.addParticipant(member);
                }
            }
            em.persist(prediction);
            log.info("New round prediction (orderd) saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on round having uuid="
                    + String.valueOf(round.getUUID()));
        } else {
            prediction.setLastModified(new Date());
            log.info("Round prediction (ordered) updated for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on round having uuid="
                    + String.valueOf(round.getUUID()));
        }

        // Update
        if (contestants != null && contestants.size() == 0) {
            prediction.setDrawn(true);
        } else {
            prediction.setDrawn(false);
        }
        prediction.setContestants(contestants);

        return prediction;
    }

    @Override
    public PredictionStageOrderedContestantsList createPredictionFor(
            Member member, TournamentStage stage, String type,
            List<SportContestant> contestants, Kup kup) {

        PredictionStageOrderedContestantsList prediction = getPredictionFor(
                member, stage, type, kup);

        if (prediction == null) {
            prediction = new PredictionStageOrderedContestantsListImpl();
            prediction.setType(type);
            prediction.setMember(member);
            prediction.setTournamentStage(stage);
            prediction.setContestants(contestants);
            prediction.setKup(kup);
            kup.addMember(member);
            if (kup.getType().equals(KupType.FREE)) {
                kup.addParticipant(member);
            } else {
                boolean hasBet;
                try {
                    hasBet = kups.hasBet(member, kup);
                } catch (CoreException e) {
                    log.error("An error occured while checking member participation."
                            + " member email="
                            + member.getEmail()
                            + " kup uuid=" + String.valueOf(kup.getId()));
                    hasBet = false;
                }
                if (hasBet) {
                    kup.addParticipant(member);
                }
            }
            em.persist(prediction);
            log.info("New stage prediction (orderd) saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on stage having uuid="
                    + String.valueOf(stage.getUUID()));
        } else {
            prediction.setLastModified(new Date());
            log.info("Stage prediction (ordered) updated for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on stage having uuid="
                    + String.valueOf(stage.getUUID()));
        }

        // Update
        if (contestants != null && contestants.size() == 0) {
            prediction.setDrawn(true);
        } else {
            prediction.setDrawn(false);
        }
        prediction.setContestants(contestants);

        return prediction;
    }

    @Override
    public PredictionSeasonOrderedContestantsList createPredictionFor(
            Member member, TournamentSeason season, String type,
            List<SportContestant> contestants, Kup kup) {

        PredictionSeasonOrderedContestantsList prediction = getPredictionFor(
                member, season, type, kup);

        if (prediction == null) {
            prediction = new PredictionSeasonOrderedContestantsListImpl();
            prediction.setType(type);
            prediction.setMember(member);
            prediction.setTournamentSeason(season);
            prediction.setContestants(contestants);
            prediction.setKup(kup);
            kup.addMember(member);
            if (kup.getType().equals(KupType.FREE)) {
                kup.addParticipant(member);
            } else {
                boolean hasBet;
                try {
                    hasBet = kups.hasBet(member, kup);
                } catch (CoreException e) {
                    log.error("An error occured while checking member participation."
                            + " member email="
                            + member.getEmail()
                            + " kup uuid=" + String.valueOf(kup.getId()));
                    hasBet = false;
                }
                if (hasBet) {
                    kup.addParticipant(member);
                }
            }
            em.persist(prediction);
            log.info("New season prediction (orderd) saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on season having uuid="
                    + String.valueOf(season.getUUID()));
        } else {
            prediction.setLastModified(new Date());
            log.info("Season prediction (ordered) updated for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()) + " on season having uuid="
                    + String.valueOf(season.getUUID()));
        }

        // Update
        if (contestants != null && contestants.size() == 0) {
            prediction.setDrawn(true);
        } else {
            prediction.setDrawn(false);
        }
        prediction.setContestants(contestants);

        return prediction;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(TournamentStage stage, Kup kup) {

        String queryStr = "from "
                + PredictionStageOrderedContestantsListImpl.class
                        .getSimpleName() + " m where m.stage.uuid=:uuid";

        Query query = createQuery(queryStr);
        query.setParameter("uuid", stage.getUUID());

        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(TournamentRound round, Kup kup) {

        String queryStr = "from "
                + PredictionRoundOrderedContestantsListImpl.class
                        .getSimpleName() + " m where m.round.uuid=:uuid";

        Query query = createQuery(queryStr);
        query.setParameter("uuid", round.getUUID());

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(TournamentGame game, Kup kup) {

        String queryStr = "from "
                + PredictionGameOrderedContestantsListImpl.class
                        .getSimpleName() + " m where m.game.uuid=:uuid";

        Query query = createQuery(queryStr);
        query.setParameter("uuid", game.getUUID());

        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(TournamentSeason season, Kup kup) {

        String queryStr = "from "
                + PredictionSeasonOrderedContestantsListImpl.class
                        .getSimpleName()
                + " m where m.season.uuid=:seasonUUID AND m.kup.id=:kupId";

        Query query = createQuery(queryStr);
        query.setParameter("seasonUUID", season.getUUID());
        query.setParameter("kupId", kup.getId());

        return query.getResultList();

    }

    @Override
    public boolean isPredictionAllowedOn(TournamentGame game) {

        Calendar ref = Calendar.getInstance();
        ref.add(Calendar.SECOND, CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);

        boolean allowed = true;

        if (game == null) {
            return false;
        }

        if (!TournamentGameStatus.SCHEDULED.equals(game.getGameStatus())) {
            return false;
        }

        if (game.getStartDate() != null) {
            Calendar gameStartDate = Calendar.getInstance();
            gameStartDate.setTime(game.getStartDate());

            if (ref.compareTo(gameStartDate) >= 0) {
                return false;
            }
        } // start date may not be announced until the last day.

        return allowed;
    }

    @Override
    public boolean isPredictionAllowedOn(TournamentRound round) {

        Calendar ref = Calendar.getInstance();
        ref.add(Calendar.SECOND, CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);

        boolean allowed = true;

        if (round == null) {
            return false;
        }

        if (!TournamentGameStatus.SCHEDULED.equals(round.getStatus())) {
            return false;
        }

        if (round.getStartDate() != null) {
            Calendar roundStartDate = Calendar.getInstance();
            roundStartDate.setTime(round.getStartDate());

            if (ref.compareTo(roundStartDate) >= 0) {
                return false;
            }
        } // start date may not be announced until the last day.

        return allowed;

    }

    @Override
    public boolean isPredictionAllowedOn(TournamentStage stage) {

        Calendar ref = Calendar.getInstance();
        ref.add(Calendar.SECOND, CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);

        boolean allowed = true;

        if (stage == null) {
            return false;
        }

        if (!TournamentGameStatus.SCHEDULED.equals(stage.getStatus())) {
            return false;
        }

        if (stage.getStartDate() != null) {
            Calendar stageStartDate = Calendar.getInstance();
            stageStartDate.setTime(stage.getStartDate());

            if (ref.compareTo(stageStartDate) >= 0) {
                return false;
            }
        } // start date may not be announced until the last day.

        return allowed;

    }

    @Override
    public boolean isPredictionAllowedOn(TournamentSeason season) {

        Calendar ref = Calendar.getInstance();
        ref.add(Calendar.SECOND, CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);

        boolean allowed = true;

        if (season == null) {
            return false;
        }

        if (season.getStartDate() != null) {
            Calendar seasonStartDate = Calendar.getInstance();
            seasonStartDate.setTime(season.getStartDate());

            if (ref.compareTo(seasonStartDate) >= 0) {
                return false;
            }
        } // start date may not be announced until the last day.

        return allowed;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(Kup kup) {

        String queryStr = "from " + PredictionImpl.class.getSimpleName()
                + " p where p.kup.id=:id";

        Query query = createQuery(queryStr);
        query.setParameter("id", kup.getId());

        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(Kup kup, Member member) {

        String queryStr = "from " + PredictionImpl.class.getSimpleName()
                + " p where p.kup.id=:kup_id AND p.member.id=:member_id";

        Query query = createQuery(queryStr);
        query.setParameter("kup_id", kup.getId());
        query.setParameter("member_id", member.getId());

        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(Kup kup, Member member,
            String type) {

        String queryStr = "from "
                + PredictionImpl.class.getSimpleName()
                + " p where p.kup.id=:kup_id AND p.member.id=:member_id AND p.type=:type";

        Query query = createQuery(queryStr);
        query.setParameter("kup_id", kup.getId());
        query.setParameter("member_id", member.getId());
        query.setParameter("type", type);

        return query.getResultList();

    }

    @Override
    public Question getQuestionById(long questionId) {

        String queryStr = "from " + QuestionImpl.class.getSimpleName()
                + " q where q.id=:questionId";
        Query query = createQuery(queryStr);
        query.setParameter("questionId", questionId);

        try {
            return (Question) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsGameToCompute() {

        List<Prediction> predictions = new ArrayList<Prediction>();

        String queryStr = "from "
                + PredictionGameOrderedContestantsListImpl.class
                        .getSimpleName()
                + " p where p.game.status=:status and p.pointsComputed=:pointsComputed";

        Query query = createQuery(queryStr);
        query.setParameter("status", TournamentGameStatus.TERMINATED);
        query.setParameter("pointsComputed", false);

        predictions.addAll(query.getResultList());

        queryStr = "from "
                + PredictionGameScoreImpl.class.getSimpleName()
                + " p where p.game.status=:status and p.pointsComputed=:pointsComputed";

        query = createQuery(queryStr);
        query.setParameter("status", TournamentGameStatus.TERMINATED);
        query.setParameter("pointsComputed", false);

        predictions.addAll(query.getResultList());

        queryStr = "from "
                + PredictionGameQuestionImpl.class.getSimpleName()
                + " p where p.game.status=:status and p.pointsComputed=:pointsComputed";

        query = createQuery(queryStr);
        query.setParameter("status", TournamentGameStatus.TERMINATED);
        query.setParameter("pointsComputed", false);

        predictions.addAll(query.getResultList());

        return predictions;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsStageToCompute() {

        List<Prediction> predictions = new ArrayList<Prediction>();

        String queryStr = "from "
                + PredictionStageOrderedContestantsListImpl.class
                        .getSimpleName()
                + " p where p.stage.status=:status and p.pointsComputed=:pointsComputed";

        Query query = createQuery(queryStr);
        query.setParameter("status", TournamentStageStatus.TERMINATED);
        query.setParameter("pointsComputed", false);

        predictions.addAll(query.getResultList());

        // XXX we do not support nor propose questions and scores on stages yet.

        return predictions;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsSeasonToCompute() {

        List<Prediction> predictions = new ArrayList<Prediction>();

        String queryStr = "from "
                + PredictionSeasonOrderedContestantsListImpl.class
                        .getSimpleName()
                + " p where p.pointsComputed=:pointsComputed";

        Query query = createQuery(queryStr);
        // XXX
        //query.setParameter("status", TournamentSeasonStatus.TERMINATED);
        query.setParameter("pointsComputed", false);

        predictions.addAll(query.getResultList());

        // XXX we do not support nor propose questions and scores on seasons
        // yet.

        return predictions;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsRoundToCompute() {

        List<Prediction> predictions = new ArrayList<Prediction>();

        String queryStr = "from "
                + PredictionRoundOrderedContestantsListImpl.class
                        .getSimpleName()
                + " p where p.round.status=:status and p.pointsComputed=:pointsComputed";

        Query query = createQuery(queryStr);
        query.setParameter("status", TournamentGameStatus.TERMINATED);
        query.setParameter("pointsComputed", false);

        predictions.addAll(query.getResultList());

        return predictions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Prediction> getPredictionsFor(Member member, int offset,
            int batchSize) {

        List<Prediction> predictions = new ArrayList<Prediction>();

        String queryStr = "from " + PredictionImpl.class.getSimpleName()
                + " p where p.member.id=:id ORDER BY p.created DESC";

        Query query = createQuery(queryStr);
        query.setParameter("id", member.getId());

        List<Prediction> results = query.getResultList();

        int totalSize = results.size();
        if (batchSize == 0) {
            batchSize = totalSize;
        }

        try {
            if (totalSize < offset) {
                predictions = results;
            } else if (totalSize < (offset + batchSize)) {
                predictions = results.subList(offset, totalSize);
            } else {
                predictions = results.subList(offset, offset + batchSize);
            }
        } catch (IndexOutOfBoundsException e) {
            predictions = results;
            log.error(e);
        }

        return predictions;

    }

    @Override
    public Date getPredictionsLastModifiedFor(Member member, Kup kup)
            throws CoreException {

        String queryStr = "from "
                + PredictionImpl.class.getSimpleName()
                + " p where p.kup.id=:kup_id AND p.member.id=:member_id ORDER BY p.modified DESC";

        Query query = createQuery(queryStr);
        query.setParameter("kup_id", kup.getId());
        query.setParameter("member_id", member.getId());
        query.setMaxResults(1);

        @SuppressWarnings("unchecked")
        List<Prediction> results = query.getResultList();
        if (results.size() == 1) {
            return results.get(0).getLastModified();
        }
        return null;
    }

    @Override
    public boolean isPredictionAllowedOn(Kup kup) {
        // XXX this is only used for the tie breaker question right now.
        // Not sure if we want to extend this.
        return true;
    }

    @Override
    public QuestionKupTiebreaker getQuestionKupTiebreakerById(long questionId) {

        String queryStr = "from "
                + QuestionKupTiebreakerImpl.class.getSimpleName()
                + " q where q.id=:questionId";
        Query query = createQuery(queryStr);
        query.setParameter("questionId", questionId);

        try {
            return (QuestionKupTiebreaker) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public PredictionQuestionKupTiebreaker createQuestionTiebreakerPredictionFor(
            Member member, QuestionKupTiebreaker question, Kup kup,
            String choice) {

        PredictionQuestionKupTiebreaker prediction = getPredictionQuestionKupTieBreakerFor(
                member, kup, question);

        if (prediction == null) {
            prediction = new PredictionQuestionKupTiebreakerImpl();
            prediction.setType("tb");
            prediction.setPointsComputed(true); // NO POINTS HERE
            prediction.setMember(member);
            prediction.setKup(kup);
            prediction.setQuestion(question);
            em.persist(prediction);
            log.info("New tiebreaker question saved for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()));
        } else {
            prediction.setLastModified(new Date());
            log.info("Update tiebreaker question for member="
                    + member.getEmail() + " in Kup="
                    + String.valueOf(kup.getId()));
        }

        // Update
        prediction.setAswer(choice);

        return prediction;
    }

    @Override
    public PredictionQuestionKupTiebreaker getPredictionQuestionKupTieBreakerFor(
            Member member, Kup kup, QuestionKupTiebreaker question) {

        String queryStr = "from "
                + PredictionQuestionKupTiebreakerImpl.class.getSimpleName()
                + " m where m.type=:type and m.member.email=:email and m.kup.id=:kup_id and m.question.id=:question_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("kup_id", kup.getId());
        query.setParameter("question_id", question.getId());
        query.setParameter("type", "tb");

        try {
            return (PredictionQuestionKupTiebreaker) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
