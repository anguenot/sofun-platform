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
import java.util.Map;

import org.sofun.core.api.sport.tournament.table.TournamentStageLeagueTable;

/**
 * Tournament stage.
 * 
 * @author <a href="mailto:anguenot@sofunTournamentStageulien Anguenot</a>
 * 
 */
public interface TournamentStage extends Map<String, String>,
        Comparable<TournamentStage>, Serializable {

    /**
     * Returns the internal unique identifier.
     * 
     * @return {@link Long}
     */
    long getId();

    /**
     * Sets the internal unique identifier.
     * 
     * @param id: {@link Long}
     */
    void setId(long id);

    /**
     * Returns the UUID.
     * 
     * @return {@link Long}
     */
    long getUUID();

    /**
     * Sets the UUID.
     * 
     * @param uuid: {@link Long}
     */
    void setUUID(long uuid);

    /**
     * Returns the stage name.
     * 
     * @return {@link String} not null
     */
    String getName();

    /**
     * Sets the stage name.
     * 
     * @param name: a {@link String} not null
     */
    void setName(String name);

    /**
     * Returns the stage description.
     * 
     * @return {@link String} can be null
     */
    String getDescription();

    /**
     * Sets the stage description.
     * 
     * @param description: {@link String} can be null.
     */
    void setDescription(String description);

    /**
     * Returns the stage start date.
     * <p />
     * The Timezone will depend on the actual system or app server
     * configuration.
     * 
     * @return a {@link Date}
     */
    Date getStartDate();

    /**
     * Sets the stage start date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the
     * resulting timezone will depend on the actual system or app server
     * configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setStartDate(Date startDate);

    /**
     * Returns the stage end date.
     * <p />
     * The Timezone will depend on the actual system or app server
     * configuration.
     * 
     * @return a {@link Date}
     */
    Date getEndDate();

    /**
     * Sets the stage end date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the
     * resulting timezone will depend on the actual system or app server
     * configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setEndDate(Date endDate);

    /**
     * Returns the corresponding tournament season.
     * 
     * @return a {@link TournamentSeason}
     */
    TournamentSeason getSeason();

    /**
     * Sets the corresponding tournament season.
     * 
     * @param season: a {@link TournamentSeason}
     */
    void setSeason(TournamentSeason season);

    /**
     * Returns the corresponding tournament rounds.
     * 
     * @return a {@link List} of {@link TournamentRound}
     */
    List<TournamentRound> getRounds();

    /**
     * Returns the corresponding tournament games.
     * 
     * @return a {@link List} of {@link TournamentGame}
     */
    List<TournamentGame> getGames();

    /**
     * Sets the corresponding tournament rounds.
     * 
     * @param rounds: a {@link List} of {@link TournamentRound}
     */
    void setRounds(List<TournamentRound> rounds);

    /**
     * Adds a round if it does not exist.
     * 
     * @param round: a {@link TournamentRound}
     */
    void addRound(TournamentRound round);

    /**
     * Returns a round given its number.
     * 
     * @param number: a {@link Integer}
     * @return a {@link TournamentRound}
     */
    TournamentRound getRoundByNumber(int number);

    TournamentRound getRoundById(long id);

    TournamentRound getRoundByUUID(long uuid);

    TournamentRound getRoundByName(String name);

    TournamentRound getRoundByLabel(String label);

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);

    String getStatus();

    void setStatus(String status);

    List<TournamentStageLeagueTable> getTables();

    void setTables(List<TournamentStageLeagueTable> tables);

    TournamentStageLeagueTable getTableByType(String type);

    void addTable(TournamentStageLeagueTable table);

}
