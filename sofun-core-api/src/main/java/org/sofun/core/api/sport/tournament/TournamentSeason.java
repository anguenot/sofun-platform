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

package org.sofun.core.api.sport.tournament;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.table.TournamentSeasonLeagueTable;

/**
 * Tournament Season.
 * 
 * <p />
 * A Tournament season is an instance of a {@link Tournament} that occurred on
 * regular basis. (every year, every month, etc...)
 * 
 * @see {@link Tournament}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentSeason extends Serializable {

    /**
     * Returns the internal tournament season unique identifier.
     * 
     * @return {@link Long}
     */
    long getId();

    /**
     * Sets the internal tournament season unique identifier.
     * 
     * @param id: {@link Long}
     */
    void setId(long id);

    /**
     * Returns the tournament season UUID.
     * 
     * @return {@link Long}
     */
    long getUUID();

    /**
     * Sets the tournament season UUID.
     * 
     * @param uuid: {@link Long}
     */
    void setUUID(long uuid);

    /**
     * Returns the tournament season label.
     * <p />
     * For instance, 2010/2011.
     * 
     * @return a {@link String}
     */
    String getYearLabel();

    /**
     * Sets the tournament season label.
     * <p />
     * For instance, 2010/2011.
     * 
     * @param yearLabel: a {@link String}
     */
    void setYearLabel(String yearLabel);

    /**
     * Returns the season name.
     * 
     * @return {@link String} not null
     */
    String getName();

    /**
     * Sets the season name.
     * 
     * @param name: a {@link String} not null
     */
    void setName(String name);

    /**
     * Returns the tournament season start date.
     * <p />
     * The Timezone will depend on the actual system or app server
     * configuration.
     * 
     * @return a {@link Date}
     */
    Date getStartDate();

    /**
     * Sets the tournament season start date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the
     * resulting timezone will depend on the actual system or app server
     * configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setStartDate(Date startDate);

    /**
     * Returns the tournament season end date.
     * <p />
     * The Timezone will depend on the actual system or app server
     * configuration.
     * 
     * @return a {@link Date}
     */
    Date getEndDate();

    /**
     * Sets the tournament season end date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the
     * resulting timezone will depend on the actual system or app server
     * configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setEndDate(Date endDate);

    /**
     * Returns the corresponding tournament.
     * 
     * @return a {@link Tournament}
     */
    Tournament getTournament();

    /**
     * Sets the corresponding tournament.
     * 
     * @param tournament: a {@link Tournament}
     */
    void setTournament(Tournament tournament);

    /**
     * Returns the list of contestants for this tournament.
     * 
     * @return a {@link List} of {@link SportContestant}
     */
    List<SportContestant> getConstestants();

    /**
     * Sets the list of contestants for this tournament.
     * 
     * @param contestants: a {@link List} of {@link SportContestant}
     */
    void setContestants(List<SportContestant> contestants);

    /**
     * Adds a contestants if it does not exist.
     * 
     * @param contestant: a {@link SportContestant}
     */
    void addContestant(SportContestant contestant);

    /**
     * Returns the tournament stages.
     * <p />
     * A Stage corresponds to a tournament phase. Qualification, 1/8, 1/4 etc...
     * are such examples.
     * 
     * @return a {@link List} of {@link TournamentStage}
     */
    List<TournamentStage> getStages();

    /**
     * Returns a stage given its name.
     * 
     * @param name: the stage name.
     * @return a {@link TournamentStage} instance or null if not found.
     */
    TournamentStage getStageByName(String name);

    /**
     * Returns a round given its name.
     * 
     * @param name: the round name.
     * @return a {@link TournamentRound} instance or null if not found.
     */
    TournamentRound getRoundByName(String name);

    /**
     * Returns a round given its uuid.
     * 
     * @param uuid: the round uuid.
     * @return a {@link TournamentRound} instance or null if not found.
     */
    TournamentRound getRoundByUUID(String uuid);

    /**
     * Sets the tournament stages.
     * <p />
     * A Stage corresponds to a tournament phase. Qualification, 1/8, 1/4 etc...
     * are such examples.
     * 
     * @param tournamentStages: a {@link List} of {@link TournamentStage}
     */
    void setStages(List<TournamentStage> stages);

    /**
     * Adds a stage if does not exist.
     * 
     * @param stage: a {@link TournamentStage}
     */
    void addStage(TournamentStage stage);

    /**
     * Returns all season's rounds.
     * 
     * @return a {@link List} of {@link TournamentRound}
     */
    List<TournamentRound> getRounds();

    /**
     * Returns all season's games.
     * 
     * @return a {@link List} of {@link TournamentGame}
     */
    List<TournamentGame> getGames();

    /**
     * Returns all season's players.
     * 
     * @return a {@link List} of {@link SportContestant}
     */
    List<SportContestant> getPlayers();

    /**
     * Return the season's status.
     * 
     * @see{@link TournamentSeasonStatus}
     * @return: a season status
     */
    String getStatus();

    /**
     * Sets the season's status.
     * 
     * @see{@link TournamentSeasonStatus}
     * @param status: a season status
     */
    void setStatus(String status);

    /**
     * Returns all tournament tables.
     * 
     * @return a {@link List} for {@link TournamentSeasonLeagueTable}
     */
    List<TournamentSeasonLeagueTable> getTables();

    /**
     * Sets all tournament tables.
     * 
     * @param tables: a {@link List} for {@link TournamentSeasonLeagueTable}
     */
    void setTables(List<TournamentSeasonLeagueTable> tables);

    /**
     * Returns a {@link TournamentSeasonLeagueTable} instance given its type.
     * 
     * @param type: table's type.
     * @return a {@link TournamentSeasonLeagueTable} instance.
     */
    TournamentSeasonLeagueTable getTableByType(String type);

    /**
     * Adds a table.
     * 
     * @param table: a {@link TournamentSeasonLeagueTable} instance.
     */
    void addTable(TournamentSeasonLeagueTable table);

}
