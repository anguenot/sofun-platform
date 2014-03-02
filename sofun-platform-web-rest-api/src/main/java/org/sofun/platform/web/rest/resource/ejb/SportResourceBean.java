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
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.sport.ReSTSportContestant;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentGameImpl;
import org.sofun.platform.web.rest.api.tournament.ReSTTournamentRound;
import org.sofun.platform.web.rest.resource.ejb.api.SportResource;

/**
 * Sports Web API
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
public class SportResourceBean extends AbstractResource implements
        SportResource {

    private static final long serialVersionUID = 1L;

    @Override
    public Response getPlayer(String playerId) throws ReSTException {
        SportContestant player = getSportService().getSportContestantPlayer(
                playerId);
        if (player == null) {
            return Response.status(400).entity("Player not found.").build();
        }
        return Response.status(202).entity(new ReSTSportContestant(player))
                .build();
    }

    @Override
    public Response getTeam(String teamId) throws ReSTException {
        SportContestant team = getSportService().getSportContestantTeam(teamId);
        if (team == null) {
            return Response.status(400).entity("Team not found.").build();
        }
        return Response.status(202).entity(new ReSTSportContestant(team))
                .build();
    }

    @Override
    public Response getTeamPlayers(String teamId) throws ReSTException {

        SportContestant team = getSportService().getSportContestantTeam(teamId);
        if (team == null) {
            return Response.status(400).entity("Team not found.").build();
        }

        List<ReSTSportContestant> players = new ArrayList<ReSTSportContestant>();
        for (SportContestant corePlayer : team.getPlayers()) {
            players.add(new ReSTSportContestant(corePlayer));
        }

        return Response.status(202).entity(players).build();

    }

    @Override
    public Response getSeasonTeams(long seasonId) throws ReSTException {

        TournamentSeason season = getSportService().getTournamentSeason(
                seasonId);
        if (season == null) {
            return Response.status(400).entity("Tournament season not found.")
                    .build();
        }

        List<ReSTSportContestant> teams = new ArrayList<ReSTSportContestant>();
        for (SportContestant coreTeam : season.getConstestants()) {
            teams.add(new ReSTSportContestant(coreTeam));
        }

        return Response.status(202).entity(teams).build();

    }

    @Override
    public Response getSeasonPlayers(long seasonId) throws ReSTException {

        TournamentSeason season = getSportService().getTournamentSeason(
                seasonId);
        if (season == null) {
            return Response.status(400).entity("Tournament season not found.")
                    .build();
        }

        List<ReSTSportContestant> players = new ArrayList<ReSTSportContestant>();
        for (SportContestant corePlayer : season.getPlayers()) {
            players.add(new ReSTSportContestant(corePlayer));
        }

        return Response.status(202).entity(players).build();

    }

    @Override
    public Response getRoundOrderedResultsFor(long seasonId, String roundName,
            String tableType) throws ReSTException {

        TournamentSeason season = getSportService().getTournamentSeason(
                seasonId);
        if (season == null) {
            return Response.status(400).entity("Tournament season not found.")
                    .build();
        }

        TournamentRound round = season.getRoundByName(roundName);
        if (round == null) {
            return Response.status(400).entity("Tournament round not found.")
                    .build();
        }

        List<ReSTSportContestant> results = new ArrayList<ReSTSportContestant>();

        TournamentRoundLeagueTable table = round.getTableByType(tableType);
        if (table == null) {
            return Response.status(202).entity(results).build();
        }

        for (SportContestant coreContestant : table
                .getOrderedContestantsForKey("pos")) {
            results.add(new ReSTSportContestant(coreContestant));
        }

        return Response.status(202).entity(results).build();
    }

    @Override
    public Response getRound(long seasonId, String roundName)
            throws ReSTException {

        TournamentSeason season = getSportService().getTournamentSeason(
                seasonId);
        if (season == null) {
            return Response.status(400).entity("Tournament season not found.")
                    .build();
        }

        TournamentRound round = season.getRoundByName(roundName);
        if (round == null) {
            return Response.status(400).entity("Tournament round not found.")
                    .build();
        }

        return Response.status(202).entity(new ReSTTournamentRound(round))
                .build();
    }

    @Override
    public Response getGame(String uuid) throws ReSTException {
        TournamentGame game = getSportService().getTournamentGame(uuid);
        if (game == null) {
            return Response.status(400).entity("Tournament game not found.")
                    .build();
        }
        return Response.status(202).entity(new ReSTTournamentGameImpl(game))
                .build();
    }

    @Override
    public Response getRoundOrderedResults(long seasonId, String roundUUID,
            String tableType, int offset, int batchSize) throws ReSTException {

        TournamentSeason season = getSportService().getTournamentSeason(
                seasonId);
        if (season == null) {
            return Response.status(400).entity("Tournament season not found.")
                    .build();
        }

        TournamentRound round = season.getRoundByUUID(roundUUID);
        if (round == null) {
            return Response.status(400).entity("Tournament round not found.")
                    .build();
        }

        List<ReSTSportContestant> results = new ArrayList<ReSTSportContestant>();

        TournamentRoundLeagueTable table = round.getTableByType(tableType);
        if (table == null) {
            return Response.status(202).entity(results).build();
        }

        for (SportContestant coreContestant : table
                .getOrderedContestantsForKey("pos").subList(offset, batchSize)) {
            results.add(new ReSTSportContestant(coreContestant));
        }

        return Response.status(202).entity(results).build();

    }

}
