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

package org.sofun.core.sport;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.local.SportServiceLocal;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;

/**
 * Sports Graph Life Cycle Manager.
 * 
 * <p>
 * 
 * Singleton handling graph elements (Season, Stage, Round and Game) life cycle.
 * Responsibility is to transition these elements when required.
 * 
 * <p>
 * 
 * Note, the current implementation does not support live betting.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SportsGraphLifeCycleManager {

    private static final Log log = LogFactory
            .getLog(SportsGraphLifeCycleManager.class);

    @EJB(beanName = "SportServiceImpl", beanInterface = SportServiceLocal.class)
    private SportService sports;

    /** Processing lock */
    private boolean available = true;

    /**
     * Returns the reference time for end of predictions.
     * 
     * @return: a {@link Calendar} instance.
     */
    private Calendar getReferenceTime() {
        final Calendar ref = Calendar.getInstance();
        ref.add(Calendar.SECOND, CoreConstants.TIME_TO_BET_BEFORE_EVENT_STARTS);
        return ref;
    }

    /**
     * Checks sports graph elements (Season, Stage, Round and Game) and
     * transitioning their status when needed.
     * 
     * @throws Exception
     */
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {
            final Calendar ref = getReferenceTime();
            log.debug("Reference time for end of predictions "
                    + ref.getTime().toString());

            // Check scheduled games: This is important for this manager to
            // handle the `scheduled` to `on_going` transition (versus feed
            // provider component). This status conditions the ability for a
            // player to place a bet.
            for (TournamentGame game : sports
                    .getTournamentGamesByStatus(TournamentGameStatus.SCHEDULED)) {
                final Date startDate = game.getStartDate();
                if (startDate != null) {
                    final Calendar calStartDate = Calendar.getInstance();
                    calStartDate.setTime(startDate);
                    if (ref.compareTo(calStartDate) >= 0) {
                        log.info("End of predictions for game with uuid="
                                + game.getUUID() + " starting @ "
                                + calStartDate.getTime().toString());
                        game.setGameStatus(TournamentGameStatus.ON_GOING);
                    }
                }
            }
            // Check for games that do not have any status. Let's not assume the
            // third party provider never does mistakes.
            for (TournamentGame game : sports.getTournamentGamesByStatus(null)) {
                final Date startDate = game.getStartDate();
                if (startDate != null) {
                    final Calendar calStartDate = Calendar.getInstance();
                    calStartDate.setTime(startDate);
                    if (ref.compareTo(calStartDate) < 0) {
                        log.info("Marking game with uuid=" + game.getUUID()
                                + " as scheduled. Former status was unknown.");
                        game.setGameStatus(TournamentGameStatus.SCHEDULED);
                    } else {
                        log.info("Marking game with uuid=" + game.getUUID()
                                + " as terminated. Former status was unknown.");
                        game.setGameStatus(TournamentGameStatus.TERMINATED);
                    }
                }
            }
            // Subsequent transitions (closed, cancelled, postponed, etc.) will
            // he handled by the component implementing the third party
            // external feed provider (see sofun-platform-opta for instance)

            // Check rounds: `scheduled` -> `on going` -> `terminated`
            for (TournamentRound round : sports
                    .getTournamentRoundsByStatus(TournamentRoundStatus.SCHEDULED)) {
                boolean terminated = true;
                boolean onGoing = false;
                if (round.getGames().size() > 0) {
                    for (TournamentGame game : round.getGames()) {
                        final String gameStatus = game.getGameStatus();
                        if (!(TournamentGameStatus.TERMINATED
                                .equals(gameStatus) || TournamentGameStatus.CANCELLED
                                .equals(gameStatus))) {
                            // One game is still scheduled. Nothing to do.
                            terminated = false;
                            break;
                        }
                        if (TournamentGameStatus.ON_GOING.equals(gameStatus)) {
                            // One game is ongoing and the game is marked as
                            // scheduled. Let's transition round
                            onGoing = true;
                            break;
                        }
                    }
                    if (terminated) {
                        // All games for this round are terminated. Marking
                        // round as terminated.
                        log.info("All games for round w/ UUID="
                                + String.valueOf(round.getUUID())
                                + " are marked as terminated. Marking round terminated");
                        round.setStatus(TournamentRoundStatus.TERMINATED);
                    }
                    if (onGoing) {
                        log.info("At least one game in round w/ UUID="
                                + String.valueOf(round.getUUID())
                                + " is marked as ON_GOING. Marking round as ON_GOING");
                        round.setStatus(TournamentRoundStatus.ON_GOING);
                    }
                } else {
                    // Handle case of a rounds with no games. (CY, F1, etc.)
                    final Calendar startDate = Calendar.getInstance();
                    startDate.setTime(round.getStartDate());
                    if (ref.compareTo(startDate) >= 0
                            && TournamentRoundStatus.SCHEDULED.equals(round
                                    .getStatus())) {
                        log.info("Round w/ UUID="
                                + String.valueOf(round.getUUID())
                                + " is marked as ON_GOING.");
                        round.setStatus(TournamentRoundStatus.ON_GOING);
                    }
                }
            }
            // Check rounds: unknown status
            for (TournamentRound round : sports
                    .getTournamentRoundsByStatus(null)) {
                boolean onGoing = false;
                boolean scheduled = false;
                for (TournamentGame game : round.getGames()) {
                    final String gameStatus = game.getGameStatus();
                    if (TournamentGameStatus.ON_GOING.equals(gameStatus)) {
                        onGoing = true;
                        break;
                    } else if (TournamentGameStatus.SCHEDULED
                            .equals(gameStatus)
                            || TournamentGameStatus.POSTPONED
                                    .equals(gameStatus)) {
                        scheduled = true;
                    }
                }
                if (onGoing) {
                    if (!TournamentRoundStatus.ON_GOING.equals(round
                            .getStatus())) {
                        log.info("At least one game in round w/ UUID="
                                + String.valueOf(round.getUUID())
                                + " is marked as ON_GOING. Marking round as "
                                + "ON_GOING. Former status was unknown");
                        round.setStatus(TournamentRoundStatus.ON_GOING);
                    }
                } else if (scheduled) {
                    if (!TournamentRoundStatus.SCHEDULED.equals(round
                            .getStatus())) {
                        log.info("At least one game in round w/ UUID="
                                + String.valueOf(round.getUUID())
                                + " is marked as SCHEDULED. Marking round as "
                                + "SCHEDULED. Former status was unknown");
                        round.setStatus(TournamentRoundStatus.SCHEDULED);
                    }
                } else {
                    if (!TournamentRoundStatus.TERMINATED.equals(round
                            .getStatus())) {
                        log.info("No game in round w/ UUID="
                                + String.valueOf(round.getUUID())
                                + " is marked as SCHEDULED, ON_GOING or "
                                + "POSPONED Marking round as "
                                + "TERMINATED. Former status was unknown");
                        round.setStatus(TournamentRoundStatus.TERMINATED);
                    }
                }

            }

            // Check stages: `scheduled` -> `on going` -> `terminated`
            List<TournamentStage> stages = sports
                    .getTournamentStagesByStatus(TournamentStageStatus.SCHEDULED);
            stages.addAll(sports
                    .getTournamentStagesByStatus(TournamentStageStatus.ON_GOING));
            for (TournamentStage stage : stages) {
                boolean terminated = true;
                boolean onGoing = false;
                for (TournamentRound round : stage.getRounds()) {
                    final String roundStatus = round.getStatus();
                    if (TournamentRoundStatus.ON_GOING.equals(roundStatus)) {
                        // One round is ongoing and the stage is marked as
                        // scheduled. Let's transition stage
                        terminated = false;
                        onGoing = true;
                        break;
                    }
                    if (!(TournamentRoundStatus.TERMINATED.equals(roundStatus) || TournamentRoundStatus.CANCELLED
                            .equals(roundStatus))) {
                        // One round is still scheduled. Nothing to do.
                        terminated = false;
                        break;
                    }
                }
                if (terminated) {
                    // All rounds for this stage are terminated. Marking stage
                    // as terminated.
                    if (!TournamentStageStatus.TERMINATED.equals(stage
                            .getStatus())) {
                        log.info("All rounds for stage w/ UUID="
                                + String.valueOf(stage.getUUID())
                                + " are marked as terminated. Marking stage terminated");
                        stage.setStatus(TournamentStageStatus.TERMINATED);
                    }
                }
                if (onGoing) {
                    if (!TournamentStageStatus.ON_GOING.equals(stage
                            .getStatus())) {
                        log.info("At least one round in stage w/ UUID="
                                + String.valueOf(stage.getUUID())
                                + " is marked as ON_GOING. Marking stage as ON_GOING");
                        stage.setStatus(TournamentStageStatus.ON_GOING);
                    }
                }
            }
            // Check stages: unknown status
            for (TournamentStage stage : sports
                    .getTournamentStagesByStatus(null)) {
                boolean onGoing = false;
                boolean scheduled = false;
                for (TournamentRound round : stage.getRounds()) {
                    final String roundStatus = round.getStatus();
                    if (TournamentRoundStatus.ON_GOING.equals(roundStatus)) {
                        onGoing = true;
                        break;
                    } else if (TournamentRoundStatus.SCHEDULED
                            .equals(roundStatus)
                            || TournamentRoundStatus.POSTPONED
                                    .equals(roundStatus)) {
                        scheduled = true;
                    }
                }
                if (onGoing) {
                    log.info("At least one round in stage w/ UUID="
                            + String.valueOf(stage.getUUID())
                            + " is marked as ON_GOING. Marking stage as "
                            + "ON_GOING. Former status was unknown");
                    stage.setStatus(TournamentStageStatus.ON_GOING);
                } else if (scheduled) {
                    log.info("At least one round in stage w/ UUID="
                            + String.valueOf(stage.getUUID())
                            + " is marked as SCHEDULED. Marking stage as "
                            + "SCHEDULED. Former status was unknown");
                    stage.setStatus(TournamentStageStatus.SCHEDULED);
                } else {
                    log.info("No round in stage w/ UUID="
                            + String.valueOf(stage.getUUID())
                            + " is marked as SCHEDULED, ON_GOING or "
                            + "POSPONED Marking stage as "
                            + "TERMINATED. Former status was unknown");
                    stage.setStatus(TournamentStageStatus.TERMINATED);
                }

            }

            // Check seasons: `scheduled` -> `on going` -> `terminated`
            for (TournamentSeason season : sports
                    .getTournamentSeasonsByStatus(TournamentSeasonStatus.SCHEDULED)) {
                boolean terminated = true;
                boolean onGoing = false;
                for (TournamentStage stage : season.getStages()) {
                    final String stageStatus = stage.getStatus();
                    if (TournamentStageStatus.ON_GOING.equals(stageStatus)) {
                        // One stage is ongoing and the season is marked as
                        // scheduled. Let's transition stage
                        onGoing = true;
                        terminated = false;
                        break;
                    }
                    if (!(TournamentStageStatus.TERMINATED.equals(stageStatus) || TournamentStageStatus.CANCELLED
                            .equals(stageStatus))) {
                        // One stage is still scheduled. Nothing to do.
                        terminated = false;
                        break;
                    }
                }
                if (terminated) {
                    // All stages for this stage are terminated. Marking season
                    // as terminated.
                    log.info("All stages for season w/ UUID="
                            + String.valueOf(season.getUUID())
                            + " are marked as terminated. Marking season terminated");
                    season.setStatus(TournamentSeasonStatus.TERMINATED);
                }
                if (onGoing) {
                    log.info("At least one stage in season w/ UUID="
                            + String.valueOf(season.getUUID())
                            + " is marked as ON_GOING. Marking season as ON_GOING");
                    season.setStatus(TournamentSeasonStatus.ON_GOING);
                }
            }
            // Check seasons: unknown status
            for (TournamentSeason season : sports
                    .getTournamentSeasonsByStatus(null)) {
                boolean onGoing = false;
                boolean scheduled = false;
                for (TournamentStage stage : season.getStages()) {
                    final String stageStatus = stage.getStatus();
                    if (TournamentStageStatus.ON_GOING.equals(stageStatus)) {
                        onGoing = true;
                        break;
                    } else if (TournamentStageStatus.SCHEDULED
                            .equals(stageStatus)
                            || TournamentStageStatus.POSTPONED
                                    .equals(stageStatus)) {
                        scheduled = true;
                    }
                }
                if (onGoing) {
                    log.info("At least one stage in season w/ UUID="
                            + String.valueOf(season.getUUID())
                            + " is marked as ON_GOING. Marking season as "
                            + "ON_GOING. Former status was unknown");
                    season.setStatus(TournamentSeasonStatus.ON_GOING);
                } else if (scheduled) {
                    log.info("At least one stage in season w/ UUID="
                            + String.valueOf(season.getUUID())
                            + " is marked as SCHEDULED. Marking season as "
                            + "SCHEDULED. Former status was unknown");
                    season.setStatus(TournamentSeasonStatus.SCHEDULED);
                } else {
                    log.info("No stage in season w/ UUID="
                            + String.valueOf(season.getUUID())
                            + " is marked as SCHEDULED, ON_GOING or "
                            + "POSPONED Marking season as "
                            + "TERMINATED. Former status was unknown");
                    season.setStatus(TournamentSeasonStatus.TERMINATED);
                }

            }
        } finally {
            available = true;
        }

    }
}
