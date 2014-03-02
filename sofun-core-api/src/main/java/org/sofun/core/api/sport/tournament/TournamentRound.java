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

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;

/**
 * Tournament Round.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentRound extends Comparable<TournamentRound>, Serializable {

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
     * Returns the round label.
     * <p />
     * For instance, 1, 2, 1/4, 1/2, final etc...
     * 
     * @return a {@link String}
     */
    String getRoundLabel();

    /**
     * Sets the round label.
     * <p />
     * For instance, 1, 2, 1/4, 1/2, final etc...
     * 
     * @param label: a {@link String}
     */
    void setRoundLabel(String label);

    /**
     * Returns the round number.
     * 
     * @return a unique {@link Integer}
     */
    int getRoundNumber();

    /**
     * Sets the round number.
     * 
     * @param roundNumber: a unique {@link Integer}
     */
    void setRoundNumber(int roundNumber);

    /**
     * Returns the list of games happening at this round.
     * 
     * @return a {@link List} of {@link TournamentGame}
     */
    List<TournamentGame> getGames();

    /**
     * Sets the list of games happening at this round.
     * 
     * @param tournamentGames: a {@link List} of {@link TournamentGame}
     */
    void setGames(List<TournamentGame> games);

    /**
     * Adds a game to the round if does not exist.
     * 
     * @param game: a {@link TournamentGame}
     */
    void addGame(TournamentGame game);

    /**
     * Returns the round stage.
     * 
     * @return a {@link TournamentStage}
     */
    TournamentStage getStage();

    /**
     * Sets the tournament stage.
     * 
     * @param stage a {@link TournamentStage}
     */
    void setStage(TournamentStage stage);

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
     * The Timezone will depend on the actual system or app server configuration.
     * 
     * @return a {@link Date}
     */
    Date getStartDate();

    /**
     * Sets the stage start date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the resulting timezone will depend on the actual
     * system or app server configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setStartDate(Date startDate);

    /**
     * Returns the stage end date.
     * <p />
     * The Timezone will depend on the actual system or app server configuration.
     * 
     * @return a {@link Date}
     */
    Date getEndDate();

    /**
     * Sets the stage end date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the resulting timezone will depend on the actual
     * system or app server configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setEndDate(Date endDate);

    String getStatus();

    void setStatus(String status);

    List<SportContestant> getContestants();

    /**
     * Returns the games properties.
     * 
     * @return a {@link Map} from prop id to prop value.
     */
    Map<String, String> getProperties();

    /**
     * Sets the games properties.
     * 
     * @param properties a {@link Map} from prop id to prop value.
     */
    void setProperties(Map<String, String> properties);
    
    List<TournamentRoundLeagueTable> getTables();

    void setTables(List<TournamentRoundLeagueTable> tables);

    TournamentRoundLeagueTable getTableByType(String type);

    void addTable(TournamentRoundLeagueTable table);

}
