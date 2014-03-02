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

package org.sofun.platform.opta;

import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.platform.opta.parser.football.F01Parser;
import org.sofun.platform.opta.parser.football.F07Parser;
import org.sofun.platform.opta.parser.football.F40Parser;

/**
 * F parsers unit tests.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestFParser extends TestCase {

    private static final String f01FeedPath = "feeds/f/srml-24-2011-results.xml";

    private static final String f01_02_FeedPath = "feeds/f/srml-21-2011-results.xml";

    private static final String f40FeedPath = "feeds/f/srml-24-2011-squads.xml";

    @Test
    public void testF01Parser() throws Exception {

        F01Parser parser = new F01Parser(f01FeedPath);
        Tournament tournament = parser.getTournament();

        assertEquals(24, tournament.getUUID());
        assertEquals("French Ligue 1", tournament.getName());
        assertEquals("FR_L1", tournament.getCode());
        assertEquals("Soccer", tournament.getSports().get(0).getName());

        TournamentSeason season = parser.getTournamentSeason();
        assertEquals("2011", season.getYearLabel());
        assertEquals("French Ligue 1 - Season 2011/2012", season.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals(season, tournament.getSeasons().get(0));

        List<TournamentStage> stages = season.getStages();
        assertEquals(1, stages.size());

        TournamentStage stage = stages.get(0);
        assertEquals(season.getName(), stage.getName());
        assertEquals(season, stage.getSeason());

        List<TournamentRound> rounds = stage.getRounds();
        assertEquals(38, rounds.size());

        for (TournamentRound round : rounds) {
            assertEquals(10, round.getGames().size());
            for (TournamentGame game : round.getGames()) {
                assertEquals(2, game.getContestants().size());
                assertEquals(2, game.getScore().getScore().size());
            }
        }

    }

    @Test
    public void testF01_02_Parser() throws Exception {
        // Smoke test
        new F01Parser(f01_02_FeedPath);
    }

    @Test
    public void testF40Parser() throws Exception {

        F40Parser parser = new F40Parser(f40FeedPath);

        Tournament tournament = parser.getTournament();

        assertEquals(24, tournament.getUUID());
        assertEquals("French Ligue 1", tournament.getName());
        assertEquals("FR_L1", tournament.getCode());
        assertEquals("Soccer", tournament.getSports().get(0).getName());

        TournamentSeason season = parser.getTournamentSeason();
        assertEquals("2011", season.getYearLabel());
        assertEquals("French Ligue 1 - Season 2011/2012", season.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals(season, tournament.getSeasons().get(0));

        List<SportContestant> teams = parser.getTeams();
        assertEquals(20, teams.size());

        List<SportContestant> players = parser.getPlayers();
        assertEquals(598, players.size());

        for (SportContestant team : teams) {
            assertEquals(SportContestantType.TEAM,
                    team.getSportContestantType());
            assertTrue(team.getPlayers().size() > 11);
            assertTrue(!team.getName().contains("Stade"));
        }

        for (SportContestant player : players) {
            assertEquals(SportContestantType.INDIVIDUAL,
                    player.getSportContestantType());
            assertEquals(1, player.getTeams().size());
            assertTrue(teams.contains(player.getTeams().get(0)));
            assertNotNull(player.getName());
            assertNotNull(player.getGivenName());
            assertNotNull(player.getLastName());
            // assertNotNull(player.getCountry());
            assertNotNull(player.getProperties().get("jersey_num"));
            assertNotNull(player.getProperties().get("weight"));
            assertNotNull(player.getProperties().get("height"));
            assertNotNull(player.getProperties().get("real_position"));
            assertNotNull(player.getProperties().get("birth_date"));
            assertNotNull(player.getProperties().get("position"));
        }

    }

    @Test
    public void testGetGameMinuteFor() {

        Calendar start = Calendar.getInstance();

        Calendar event = (Calendar) start.clone();
        event.add(Calendar.MINUTE, 10);
        event.add(Calendar.SECOND, 59);

        assertEquals(10,
                F07Parser.getGameMinuteFor(start.getTime(), event.getTime()));

    }

    @Test
    public void testGetQuarterFor() {

        assertEquals("first_quarter",
                F07Parser.getQuarterFor(0, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("first_quarter",
                F07Parser.getQuarterFor(10, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("first_quarter",
                F07Parser.getQuarterFor(15, F07Parser.STATUS_GAME_FIRST_HALF));

        assertEquals("second_quarter",
                F07Parser.getQuarterFor(16, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("second_quarter",
                F07Parser.getQuarterFor(21, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("second_quarter",
                F07Parser.getQuarterFor(30, F07Parser.STATUS_GAME_FIRST_HALF));

        assertEquals("third_quarter",
                F07Parser.getQuarterFor(31, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("third_quarter",
                F07Parser.getQuarterFor(42, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("third_quarter",
                F07Parser.getQuarterFor(45, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("third_quarter",
                F07Parser.getQuarterFor(47, F07Parser.STATUS_GAME_FIRST_HALF));
        assertEquals("third_quarter",
                F07Parser.getQuarterFor(57, F07Parser.STATUS_GAME_FIRST_HALF));

        assertEquals("forth_quarter", F07Parser.getQuarterFor(45,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("forth_quarter", F07Parser.getQuarterFor(53,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("forth_quarter", F07Parser.getQuarterFor(60,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));

        assertEquals("fifth_quarter", F07Parser.getQuarterFor(61,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("fifth_quarter", F07Parser.getQuarterFor(70,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("fifth_quarter", F07Parser.getQuarterFor(75,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));

        assertEquals("sixth_quarter", F07Parser.getQuarterFor(76,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("sixth_quarter", F07Parser.getQuarterFor(82,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("sixth_quarter", F07Parser.getQuarterFor(90,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("sixth_quarter", F07Parser.getQuarterFor(92,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));
        assertEquals("sixth_quarter", F07Parser.getQuarterFor(110,
                F07Parser.STATUS_GAME_SECOND_HALF_TIME));

        assertEquals("sixth_quarter", F07Parser.getQuarterFor(92,
                F07Parser.STATUS_GAME_EXTRA_FIRST_HALF));
        assertEquals("sixth_quarter", F07Parser.getQuarterFor(110,
                F07Parser.STATUS_GAME_EXTRA_FIRST_HALF));

        assertEquals("sixth_quarter", F07Parser.getQuarterFor(92,
                F07Parser.STATUS_GAME_EXTRA_SECOND_HALF));
        assertEquals("sixth_quarter", F07Parser.getQuarterFor(110,
                F07Parser.STATUS_GAME_EXTRA_SECOND_HALF));

        assertEquals("0",
                F07Parser.getQuarterFor(92, F07Parser.STATUS_GAME_SHOOTOUT));
        assertEquals("0",
                F07Parser.getQuarterFor(110, F07Parser.STATUS_GAME_SHOOTOUT));

        assertEquals("sixth_quarter",
                F07Parser.getQuarterFor(92, F07Parser.STATUS_GAME_FULL_TIME));
        assertEquals("sixth_quarter",
                F07Parser.getQuarterFor(110, F07Parser.STATUS_GAME_FULL_TIME));

    }

}
