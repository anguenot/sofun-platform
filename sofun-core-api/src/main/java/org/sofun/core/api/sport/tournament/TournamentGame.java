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

 * @version $Id: TournamentGame.java 3877 2014-03-02 07:24:54Z anguenot $
 */

package org.sofun.core.api.sport.tournament;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sofun.core.api.sport.SportContestant;

/**
 * Tournament game.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface TournamentGame extends Serializable {

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
     * @return {@link String}
     */
    String getUUID();

    /**
     * Sets the UUID.
     * 
     * @param uuid: {@link String}
     */
    void setUUID(String uuid);

    /**
     * Returns the game start date.
     * <p />
     * The Timezone will depend on the actual system or app server
     * configuration.
     * 
     * @return a {@link Date}
     */
    Date getStartDate();

    /**
     * Sets the game start date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the
     * resulting timezone will depend on the actual system or app server
     * configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setStartDate(Date startDate);

    /**
     * Returns the game end date.
     * <p />
     * The Timezone will depend on the actual system or app server
     * configuration.
     * 
     * @return a {@link Date}
     */
    Date getEndDate();

    /**
     * Sets the game end date.
     * <p />
     * The Timezone will be taken into consideration. While storing the date the
     * resulting timezone will depend on the actual system or app server
     * configuration.
     * 
     * @param startDate: a {@link Date}
     */
    void setEndDate(Date endDate);

    /**
     * Returns the game status.
     * 
     * @see: {@link TournamentGameStatus}
     * @return a {@link String}
     */
    String getGameStatus();

    /**
     * Sets the game status.
     * 
     * @see: {@link TournamentGameStatus}
     * @param status: a {@link String}
     */
    void setGameStatus(String status);

    /**
     * Returns the list of {@link SportContestant}.
     * 
     * @return a {@link List} of {@link SportContestant}
     */
    List<SportContestant> getContestants();

    /**
     * Sets the list of {@link SportContestant}.
     * 
     * @param contestants: a {@link List} of {@link SportContestant}
     */
    void setContestants(List<SportContestant> contestants);

    /**
     * Adds a contestant to the game if does not exist.
     * 
     * @param contestant: a {@link SportContestant}
     */
    void addContestant(SportContestant contestant);

    /**
     * Returns the contestant winner.
     * 
     * @return {@link SportContestant} or null if no winner.
     */
    SportContestant getWinner();

    /**
     * Sets the contestant winner.
     * 
     * @param winner: {@link SportContestant} or null if no winner.
     */
    void setWinner(SportContestant winner);

    /**
     * Returns the corresponding tournament round.
     * 
     * @return a {@link TournamentRound}
     */
    TournamentRound getRound();

    /**
     * Sets the corresponding tournament round.
     * 
     * @param round: a {@link TournamentRound}
     */
    void setRound(TournamentRound round);

    /**
     * Returns the game score.
     * 
     * @return {@link TournamentGameScore}
     */
    TournamentGameScore getScore();

    /**
     * Sets the {@link TournamentGameScore}
     * 
     * @param score: a {@link TournamentGameScore}
     */
    void setScore(TournamentGameScore score);

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

}
