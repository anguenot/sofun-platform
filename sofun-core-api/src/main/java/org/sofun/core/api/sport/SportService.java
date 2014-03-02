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
import java.util.Iterator;
import java.util.List;

import org.sofun.core.api.country.Country;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;

/**
 * Sport Service interface.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface SportService extends Serializable {

    long countSportContestants();

    long countSportContestantTeams();

    SportContestant getSportContestantTeam(String uuid);

    SportContestant getSportContestantPlayer(String uuid);

    SportCategory getSportCategory(long id);

    long countSportCategories();

    Sport getSport(long id);

    Sport getSportByName(String name);

    long countSports();

    long countSportTournaments();

    Tournament getSportTournament(long id);

    Tournament getSportTournamentByCode(String code);

    Iterator<Tournament> getAllTournaments();

    TournamentSeason getTournamentSeason(long id);

    TournamentGame getTournamentGame(String id);

    TournamentStage getTournamentStage(long id);

    Country getCountry(String iso);

    Country getCountryByName(String name);

    TournamentLeagueTable getTournamentLeagueTable(long id);

    TournamentRound getTournamentRound(long id);

    TournamentRound getTournamentRoundByUUID(long id);

    /**
     * Returns the list of games given a status.
     * 
     * @param status: one of a {@link TournamentGameStatus} or null
     * @return a {@link List} of {@link TournamentGame} instances
     */
    List<TournamentGame> getTournamentGamesByStatus(String status);

    /**
     * Returns the list of rounds given a status
     * 
     * @param status: one of a {@link TournamentRoundStatus} or null
     * @return a {@link List} of {@link TournamentRound} instances
     */
    List<TournamentRound> getTournamentRoundsByStatus(String status);

    /**
     * Returns the list of stages given a status.
     * 
     * @param status: one of a {@link TournamentStageStatus} or null
     * @return a {@link List} of {@link TournamentStage} instances
     */
    List<TournamentStage> getTournamentStagesByStatus(String status);

    /**
     * Returns the list of seasons given a status.
     * 
     * @param status: one if {@link TournamentSeasonStatus} or null
     * @return a {@link List} of {@link TournamentSeason} instances
     */
    List<TournamentSeason> getTournamentSeasonsByStatus(String status);

    /**
     * Returns all {@link TournamentSeason}e given a {@link Tournament}
     * instance.
     * 
     * @param tournament: a {@link Tournament} instance
     * @return a list of {@link TournamentSeason} instances
     */
    List<TournamentSeason> getSeasonsFor(Tournament tournament);

    /**
     * Returns the season matching a given year label.
     * 
     * @param tournament: a {@link Tournament} instance
     * @param yearLabel: the year label
     * @return a {@link TournamentSeason} instances.
     */
    TournamentSeason getSeasonByYearLabel(Tournament tournament,
            String yearLabel);

    /**
     * Returns the season matching a given name.
     * 
     * @param tournament: a {@link Tournament} instance
     * @param name: the season name
     * @return a {@link TournamentSeason} instances.
     */
    TournamentSeason getSeasonByName(Tournament tournament, String name);

    /**
     * Returns the season matching a given UUID.
     * 
     * @param tournament: a {@link Tournament} instance
     * @param uuid: the season UUID
     * @return a {@link TournamentSeason} instances.
     */
    TournamentSeason getSeasonByUUID(Tournament tournament, long uuid);

    /**
     * Returns the active season for a given tournament.
     * 
     * <p>
     * 
     * Only one active season at a time.
     * 
     * @param tournament: a {@link Tournament} instance
     * @return a {@link TournamentSeason} instance.
     */
    TournamentSeason getActiveSeasonFor(Tournament tournament);

}
