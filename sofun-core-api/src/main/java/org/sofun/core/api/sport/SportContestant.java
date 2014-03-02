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

package org.sofun.core.api.sport;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.sofun.core.api.country.Country;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;

/**
 * Sport contestant.
 * 
 * <p />
 * 
 * A sport contestant is an individual or team involved in a contest. We do use
 * the same representation for both an individual and a team.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface SportContestant extends Map<String, String>, Serializable {

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
     * Returns the sport contestant name.
     * 
     * @return a {@link String} not null.
     */
    String getName();

    /**
     * Sets the sport contestant name.
     * 
     * @param name: a {@link String} not null.
     */
    void setName(String name);

    /**
     * Returns the sport contestant given name.
     * 
     * @return a {@link String} or null if the contestant is a team for
     *         instance.
     */
    String getGivenName();

    /**
     * Sets the sport contestant given name.
     * 
     * @param giveName: a {@link String} or null if the contestant is a team for
     *        instance.
     */
    void setGivenName(String giveName);

    /**
     * Returns the sport contestant last name.
     * 
     * @return a {@link String} or null if the contestant is a team for
     *         instance.
     */
    String getLastName();

    /**
     * Sets the sport contestant last name.
     * 
     * @param giveName: a {@link String} or null if the contestant is a team for
     *        instance.
     */
    void setLastName(String lastName);

    /**
     * Returns the list of teams this participant belongs to.
     * 
     * @return {@link List} of {@link SportContestant} if the contestant is a
     *         already a team.
     */
    List<SportContestant> getTeams();

    /**
     * Sets the list of teams this participant belongs to.
     * 
     * @param teams: {@link List} of {@link SportContestant} if the contestant
     *        is a already a team.
     */
    void setTeams(List<SportContestant> teams);

    /**
     * Adds a team if it does not exist.
     * 
     * @param team: a {@link SportContestant} instance.
     */
    void addTeam(SportContestant team);

    /**
     * Removes a team if it does exist.
     * 
     * @param team: a {@link SportContestant} instaOnce
     */
    void delTeam(SportContestant team);

    /**
     * Returns the list of team members.
     * 
     * @return a {@link List} of {@link SportContestant} or null of the
     *         contestant is an individual.
     */
    List<SportContestant> getPlayers();

    /**
     * Sets the list of team members.
     * 
     * @param players: a {@link List} of {@link SportContestant} or null of the
     *        contestant is an individual.
     */
    void setPlayers(List<SportContestant> players);

    /**
     * Adds a team player if it does not exist.
     * 
     * @param constestant: a {@link SportContestant}
     */
    void addPlayer(SportContestant player);

    /**
     * Removes a team player if exists.
     * 
     * @param player: a {@link SportContestant} instaOnce
     */
    void delPlayer(SportContestant player);

    /**
     * Returns the contestant type.
     * 
     * @see {@link SportContestantType}
     * @return a {@link String}
     */
    String getSportContestantType();

    /**
     * Sets the contestant type.
     * 
     * @see {@link SportContestantType}
     * @param sportContestantType: a {@link String}
     */
    void setSportContestantType(String sportContestantType);

    /**
     * Returns the contestant country of origin.
     * 
     * @return a {@link Country}
     */
    Country getCountry();

    /**
     * Sets the contestant country of origin.
     * 
     * @param country: a {@link Country}
     */
    void setCountry(Country country);

    List<TournamentLeagueTableRow> getLeagueTableRows();

    void setLeagueTableRows(List<TournamentLeagueTableRow> rows);

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);

}
