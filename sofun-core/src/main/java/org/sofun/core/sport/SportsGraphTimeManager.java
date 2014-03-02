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

import java.util.Date;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.local.SportServiceLocal;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;

/**
 * Sports Graph Time manager.
 * 
 * <p>
 * 
 * Singleton handling graph elements (Season, Stage and Round (not game)) timer.
 * It basically, uses game elements start date, set by component from third
 * party reference feed provider(s).
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
public class SportsGraphTimeManager {

    private static final Log log = LogFactory
            .getLog(SportsGraphTimeManager.class);

    @EJB(beanName = "SportServiceImpl", beanInterface = SportServiceLocal.class)
    private SportService sports;

    /** Processing lock */
    private boolean available = true;

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

            // Update scheduled rounds start date
            for (TournamentRound round : sports
                    .getTournamentRoundsByStatus(TournamentRoundStatus.SCHEDULED)) {
                Date first = null;
                for (TournamentGame game : round.getGames()) {
                    Date startDate = game.getStartDate();
                    if (first == null) {
                        first = startDate;
                    } else if (startDate != null
                            && startDate.compareTo(first) < 0) {
                        first = game.getStartDate();
                    }
                }
                if (first != null && !first.equals(round.getStartDate())) {
                    round.setStartDate(first);
                    log.info("Updated start_date for round w/ uuid="
                            + round.getUUID());
                }
            }

            // Update scheduled stages start date
            for (TournamentStage stage : sports
                    .getTournamentStagesByStatus(TournamentStageStatus.SCHEDULED)) {
                Date first = null;
                for (TournamentRound round : stage.getRounds()) {
                    Date startDate = round.getStartDate();
                    if (first == null) {
                        first = startDate;
                    } else if (startDate != null
                            && startDate.compareTo(first) < 0) {
                        first = round.getStartDate();
                    }
                }
                if (first != null && !first.equals(stage.getStartDate())) {
                    stage.setStartDate(first);
                    log.info("Update start_date for stage w/ uuid="
                            + stage.getUUID());
                }
            }

            // Update scheduled seasons start date
            for (TournamentSeason season : sports
                    .getTournamentSeasonsByStatus(TournamentSeasonStatus.SCHEDULED)) {
                Date first = null;
                for (TournamentStage stage : season.getStages()) {
                    Date startDate = stage.getStartDate();
                    if (first == null) {
                        first = startDate;
                    } else if (startDate != null
                            && startDate.compareTo(first) < 0) {
                        first = stage.getStartDate();
                    }
                }
                if (first != null && !first.equals(season.getStartDate())) {
                    season.setStartDate(first);
                    log.info("Update start_date for season w/ uuid="
                            + season.getUUID());
                }
            }
        } finally {
            available = true;
        }

    }

}
