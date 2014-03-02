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

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.sport.SportServiceImpl;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.impl.OptaServiceImpl;
import org.sofun.platform.opta.parser.rugby.RU6Parser;
import org.sofun.platform.opta.testing.SofunCoreTestCase;

/**
 * RU Parser integration tests.
 * 
 * <p/>
 * 
 * Integration tests with JPA and services.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestRUParserInt extends SofunCoreTestCase {

    private SportService sports;

    private OptaService opta;

    private static final String ru10FeedPath = "feeds/ru/ru10_comp-210.xml";

    private static final String ru10FeedPath2 = "feeds/ru/ru10_comp-210-2.xml";

    private static final String ru10FeedPath4 = "feeds/ru/ru10_comp-203.xml";

    private static final String ru10FeedPath4_2 = "feeds/ru/ru10_comp-203-2.xml";

    private static final String ru2FeedPath = "feeds/ru/ru2_tables.210.2012.xml";

    private static final String ru1FeedPath = "feeds/ru/ru1_compfixtures.210.2012.20110831070016.xml";

    private static final String ru1FeedPath_2 = "feeds/ru/ru1_compfixtures.203.2012.20120114152100.xml";

    private static final String ru6FeedPath1 = "feeds/ru/ru6_wapresults.6667.201109110104.xml";

    private static final String ru6FeedPath2 = "feeds/ru/ru6_wapresults.6667.201109110105.xml";

    private static final String ru6FeedPath3 = "feeds/ru/ru6_wapresults.6662.201109100350.xml";

    private static final String ru6FeedPath4 = "feeds/ru/ru6_wapresults.6661.201109100196.xml";

    private final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyyMMdd HH:mm:ss");

    public TestRUParserInt(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sports = new SportServiceImpl(this.em);
        opta = new OptaServiceImpl(this.em, sports);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        sports = null;
        opta = null;
    }

    /**
     * Returns a {@link File} given its path.
     * 
     * @param path: path of the file within the test resources.
     * @return a {@link File} instance
     */
    private File getFile(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(path);
        return new File(url.getFile());
    }

    @Test
    public void testRU10Sync() throws Exception {

        File file = this.getFile(ru1FeedPath);
        opta.ru1Sync(file);

        file = this.getFile(ru10FeedPath);
        opta.ru10Sync(file);

        Tournament tournament = sports.getSportTournament(210);
        TournamentSeason season = sports.getSeasonByYearLabel(tournament,
                "2012");

        assertEquals(20, season.getConstestants().size());
        assertEquals(20, sports.countSportContestantTeams());

        assertEquals(597, season.getPlayers().size());

        SportContestant team = sports.getSportContestantTeam("rut650");

        assertEquals(SportContestantType.TEAM, team.getSportContestantType());

        if (team.getUUID().equals("rut650")) {
            assertEquals("France", team.getName());
        }

        List<SportContestant> players = team.getPlayers();
        assertTrue(players.size() >= 28);

        for (SportContestant player : players) {

            assertEquals(team, player.getTeams().get(0));
            assertEquals(SportContestantType.INDIVIDUAL,
                    player.getSportContestantType());

            if (player.getUUID().equals("rup10918")) {

                assertEquals("Raphael Lakafia", player.getName());
                assertEquals("Raphael", player.getGivenName());
                assertEquals("Lakafia", player.getLastName());
            }

        }

        //
        // Second run
        //

        opta.ru10Sync(file);

        tournament = sports.getSportTournament(210);
        season = sports.getSeasonByYearLabel(tournament, "2012");

        assertEquals(20, season.getConstestants().size());
        assertEquals(20, sports.countSportContestantTeams());

        assertEquals(597, season.getPlayers().size());

        team = sports.getSportContestantTeam("rut650");

        assertEquals(SportContestantType.TEAM, team.getSportContestantType());

        if (team.getUUID().equals("rut650")) {
            assertEquals("France", team.getName());
        }

        players = team.getPlayers();
        assertTrue(players.size() >= 28);

        for (SportContestant player : players) {

            assertEquals(team, player.getTeams().get(0));
            assertEquals(SportContestantType.INDIVIDUAL,
                    player.getSportContestantType());

            if (player.getUUID().equals("rup10918")) {

                assertEquals("Raphael Lakafia", player.getName());
                assertEquals("Raphael", player.getGivenName());
                assertEquals("Lakafia", player.getLastName());
            }

        }

        //
        // Third run with more teams and players.
        //

        file = this.getFile(ru10FeedPath2);
        opta.ru10Sync(file);

        tournament = sports.getSportTournament(210);
        season = sports.getSeasonByYearLabel(tournament, "2012");

        assertEquals(21, season.getConstestants().size());
        assertEquals(601, season.getPlayers().size()); // Addition

        assertEquals(21, sports.countSportContestantTeams());

        team = sports.getSportContestantTeam("rut650");

        assertEquals(SportContestantType.TEAM, team.getSportContestantType());

        if (team.getUUID().equals("rut650")) {
            assertEquals("France", team.getName());
        }

        players = team.getPlayers();
        assertTrue(players.size() >= 28);

        for (SportContestant player : players) {

            assertEquals(team, player.getTeams().get(0));
            assertEquals(SportContestantType.INDIVIDUAL,
                    player.getSportContestantType());

            if (player.getUUID().equals("rup10918")) {

                assertEquals("New Name", player.getName());
                assertEquals("Raphael", player.getGivenName());
                assertEquals("Lakafia", player.getLastName());
            }

        }

        assertEquals(SportContestantType.TEAM, team.getSportContestantType());

        //
        // NEW PLAYER IN EXISTING TEAM
        //

        SportContestant newPlayer = sports
                .getSportContestantPlayer("rup1000000");
        assertEquals("Julien Anguenot", newPlayer.getName());
        assertEquals("Julien", newPlayer.getGivenName());
        assertEquals("Anguenot", newPlayer.getLastName());

        //
        // NEW TEAM WITH NEW PLAYER
        //

        team = sports.getSportContestantTeam("rut222222");

        assertEquals("Sofun Gaming", team.getName());

        players = team.getPlayers();
        assertEquals(1, players.size());

        for (SportContestant player : players) {

            assertEquals(team, player.getTeams().get(0));
            assertEquals(SportContestantType.INDIVIDUAL,
                    player.getSportContestantType());

            if (player.getUUID().equals("rup10918")) {

                assertEquals("Julien De Préaumont", player.getName());
                assertEquals("Julien", player.getGivenName());
                assertEquals("De Préaumont", player.getLastName());
            }

        }

    }

    @Test
    public void testRU10Sync_2() throws Exception {
        // smoke test: should not throw if no RU1 processed before.
        File file = this.getFile(ru10FeedPath4);
        opta.ru10Sync(file);
    }

    @Test
    public void testRU1Sync() throws Exception {

        File file = this.getFile(ru1FeedPath);
        opta.ru1Sync(file);

        Tournament tournament = sports.getSportTournament(210);
        TournamentSeason season = sports.getSeasonByYearLabel(tournament,
                "2012");
        assertEquals(season, sports.getActiveSeasonFor(tournament));

        assertEquals(8, season.getStages().size());
        List<TournamentRound> rounds = season.getRounds();
        assertEquals(2, rounds.size());

        TournamentGame game = sports.getTournamentGame("ru6660");
        if (game.getUUID().equals("ru6660")) {

            assertNotNull(game.getRound());
            assertNotNull(game.getRound().getStage());

            final Date startDate = game.getStartDate();

            String date = "20110909" + " " + "8:30:00";
            Date expected = sdf.parse(date);
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
            assertEquals(expected, startDate);

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);

            assertEquals(9, cal.get(Calendar.DATE));
            assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
            assertEquals(2011, cal.get(Calendar.YEAR));

            assertEquals(8, cal.get(Calendar.HOUR));
            assertEquals(30, cal.get(Calendar.MINUTE));

            assertEquals("1", game.getRound().getName());
            assertEquals("A", game.getRound().getStage().getName());

            List<SportContestant> teams = game.getContestants();
            assertEquals(2, teams.size());
            assertEquals("rut850", teams.get(0).getUUID());
            assertEquals("rut750", teams.get(1).getUUID());

        }

    }

    @Test
    public void testRU2Sync() throws Exception {

        File file = this.getFile(ru1FeedPath);
        opta.ru1Sync(file);

        file = this.getFile(ru2FeedPath);
        opta.ru2Sync(file);

        Tournament tournament = sports.getSportTournament(210);
        TournamentSeason season = sports.getSeasonByYearLabel(tournament,
                "2012");
        assertEquals(season, sports.getActiveSeasonFor(tournament));

        for (TournamentStage stage : season.getStages()) {

            if (stage.getName().equals("A") || stage.getName().equals("B")
                    || stage.getName().equals("C")
                    || stage.getName().equals("D")) {

                assertEquals(1, stage.getTables().size());

                TournamentLeagueTable table = stage
                        .getTableByType("main_ranking");
                assertEquals(15, table.getKeys().size());
                assertEquals(5, table.getRows().size());
                for (TournamentLeagueTableRow row : table.getRows()) {
                    assertNotNull(row.getSportContestant());
                    assertNotNull(row.getLeagueTable());
                    assertEquals(15, row.getColumns().size());
                }
            }

        }

    }

    @Test
    public void testRU6Parser_01() throws Exception {

        File file = this.getFile(ru1FeedPath);
        opta.ru1Sync(file);

        file = this.getFile(ru10FeedPath);
        opta.ru10Sync(file);

        // First feed with results status.
        file = this.getFile(ru6FeedPath1);
        opta.ru6Sync(file);

        TournamentGame game = sports.getTournamentGame("ru6667");
        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());

        assertEquals(17, game.getScore().getScoreTeam1());
        assertEquals(16, game.getScore().getScoreTeam2());

        assertEquals("South Africa", game.getContestants().get(0).getName());
        assertEquals("Wales", game.getContestants().get(1).getName());

        assertEquals(game.getContestants().get(0), game.getWinner());

        Map<String, String> gameProps = game.getProperties();

        int nbTriesTeam1 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_NB_TRIES_KEY));
        int nbTriesTeam2 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_NB_TRIES_KEY));

        assertEquals(2, nbTriesTeam1);
        assertEquals(1, nbTriesTeam2);

        String teamFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_TEAM_UUID_FIRST_TRY_KEY);
        String playerFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_PLAYER_UUID_FIRST_TRY_KEY);

        assertEquals(game.getContestants().get(0).getUUID(), teamFirstTry);
        assertEquals("rup8583", playerFirstTry);

        String team1PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY);
        String[] team1Players = team1PlayerTries.split(",");
        assertEquals(2, team1Players.length);

        String team2PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY);
        String[] team2Players = team2PlayerTries.split(",");
        assertEquals(1, team2Players.length);

        assertEquals("",
                gameProps.get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_DROPS_KEY));
        assertEquals("",
                gameProps.get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_DROPS_KEY));

        String team1PlayerConvs = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_CONVS_KEY);
        team1Players = team1PlayerConvs.split(",");
        assertEquals(2, team1Players.length);

        String team2PlayerConvs = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_CONVS_KEY);
        team2Players = team2PlayerConvs.split(",");
        assertEquals(1, team2Players.length);

        String team1PlayerPenks = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_PENKS_KEY);
        team1Players = team1PlayerPenks.split(",");
        assertEquals(1, team1Players.length);

        String team2PlayerPenks = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_PENKS_KEY);
        team2Players = team2PlayerPenks.split(",");
        assertEquals(3, team2Players.length);

        // Second feed for same game with results status.
        file = this.getFile(ru6FeedPath2);
        opta.ru6Sync(file);

        game = sports.getTournamentGame("ru6667");
        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());

        assertEquals(17, game.getScore().getScoreTeam1());
        assertEquals(16, game.getScore().getScoreTeam2());

        assertEquals("South Africa", game.getContestants().get(0).getName());
        assertEquals("Wales", game.getContestants().get(1).getName());

        assertEquals(game.getContestants().get(0), game.getWinner());

        gameProps = game.getProperties();

        nbTriesTeam1 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_NB_TRIES_KEY));
        nbTriesTeam2 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_NB_TRIES_KEY));

        assertEquals(2, nbTriesTeam1);
        assertEquals(1, nbTriesTeam2);

        teamFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_TEAM_UUID_FIRST_TRY_KEY);
        playerFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_PLAYER_UUID_FIRST_TRY_KEY);

        assertEquals(game.getContestants().get(0).getUUID(), teamFirstTry);
        assertEquals("rup8583", playerFirstTry);

        team1PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY);
        team1Players = team1PlayerTries.split(",");
        assertEquals(2, team1Players.length);

        team2PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY);
        team2Players = team2PlayerTries.split(",");
        assertEquals(1, team2Players.length);

        assertEquals("",
                gameProps.get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_DROPS_KEY));
        assertEquals("",
                gameProps.get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_DROPS_KEY));

        team1PlayerConvs = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_CONVS_KEY);
        team1Players = team1PlayerConvs.split(",");
        assertEquals(2, team1Players.length);

        team2PlayerConvs = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_CONVS_KEY);
        team2Players = team2PlayerConvs.split(",");
        assertEquals(1, team2Players.length);

        team1PlayerPenks = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_PENKS_KEY);
        team1Players = team1PlayerPenks.split(",");
        assertEquals(1, team1Players.length);

        team2PlayerPenks = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_PENKS_KEY);
        team2Players = team2PlayerPenks.split(",");
        assertEquals(3, team2Players.length);

    }

    @Test
    public void testRU6Parser_02() throws Exception {

        File file = this.getFile(ru1FeedPath);
        opta.ru1Sync(file);

        file = this.getFile(ru10FeedPath);
        opta.ru10Sync(file);

        // First feed with results status.
        file = this.getFile(ru6FeedPath3);
        opta.ru6Sync(file);

        TournamentGame game = sports.getTournamentGame("ru6662");
        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());

        assertEquals(49, game.getScore().getScoreTeam1());
        assertEquals(25, game.getScore().getScoreTeam2());

        assertEquals("Fiji", game.getContestants().get(0).getName());
        assertEquals("Namibia", game.getContestants().get(1).getName());

        assertEquals(game.getContestants().get(0), game.getWinner());

        Map<String, String> gameProps = game.getProperties();

        int nbTriesTeam1 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_NB_TRIES_KEY));
        int nbTriesTeam2 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_NB_TRIES_KEY));

        assertEquals(6, nbTriesTeam1);
        assertEquals(2, nbTriesTeam2);

        String teamFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_TEAM_UUID_FIRST_TRY_KEY);
        String playerFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_PLAYER_UUID_FIRST_TRY_KEY);

        assertEquals(game.getContestants().get(0).getUUID(), teamFirstTry);
        assertEquals("rup9215", playerFirstTry);

        String team1PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY);
        String[] team1Players = team1PlayerTries.split(",");
        assertEquals(6, team1Players.length);

        String team2PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY);
        String[] team2Players = team2PlayerTries.split(",");
        assertEquals(2, team2Players.length);

        assertEquals("",
                gameProps.get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_DROPS_KEY));
        String team2PlayersDrops = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_DROPS_KEY);
        team2Players = team2PlayersDrops.split(",");
        assertEquals(3, team2Players.length);

        String team1PlayerConvs = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_CONVS_KEY);
        team1Players = team1PlayerConvs.split(",");
        assertEquals(5, team1Players.length);

        String team2PlayerConvs = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_CONVS_KEY);
        team2Players = team2PlayerConvs.split(",");
        assertEquals(1, team2Players.length);

        String team1PlayerPenks = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_PENKS_KEY);
        team1Players = team1PlayerPenks.split(",");
        assertEquals(3, team1Players.length);

        String team2PlayerPenks = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_PENKS_KEY);
        team2Players = team2PlayerPenks.split(",");
        assertEquals(2, team2Players.length);

    }

    @Test
    public void testRU6Parser_03() throws Exception {

        File file = this.getFile(ru1FeedPath);
        opta.ru1Sync(file);

        file = this.getFile(ru10FeedPath);
        opta.ru10Sync(file);

        // First feed with results status.
        file = this.getFile(ru6FeedPath4);
        opta.ru6Sync(file);

        TournamentGame game = sports.getTournamentGame("ru6661");
        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());

        assertEquals(34, game.getScore().getScoreTeam1());
        assertEquals(24, game.getScore().getScoreTeam2());

        assertEquals("Scotland", game.getContestants().get(0).getName());
        assertEquals("Romania", game.getContestants().get(1).getName());

        assertEquals(game.getContestants().get(0), game.getWinner());

        Map<String, String> gameProps = game.getProperties();

        int nbTriesTeam1 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_NB_TRIES_KEY));
        int nbTriesTeam2 = Integer.valueOf(gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_NB_TRIES_KEY));

        assertEquals(4, nbTriesTeam1);
        assertEquals(2, nbTriesTeam2);

        String teamFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_TEAM_UUID_FIRST_TRY_KEY);
        String playerFirstTry = gameProps
                .get(RU6Parser.GAME_PROP_PLAYER_UUID_FIRST_TRY_KEY);

        assertEquals(game.getContestants().get(0).getUUID(), teamFirstTry);
        assertEquals("rup3074", playerFirstTry);

        String team1PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY);
        String[] team1Players = team1PlayerTries.split(",");
        assertEquals(4, team1Players.length);
        System.out.println(team1PlayerTries);

        String team2PlayerTries = gameProps
                .get(RU6Parser.GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY);
        String[] team2Players = team2PlayerTries.split(",");
        assertEquals(2, team2Players.length);
        System.out.println(team2PlayerTries);

    }

    @Test
    public void testRU1OPlayersMoves() throws Exception {

        File file = this.getFile(ru1FeedPath_2);
        opta.ru1Sync(file);

        file = this.getFile(ru10FeedPath4);
        opta.ru10Sync(file);

        SportContestant stade = sports.getSportContestantTeam("rut1600");
        SportContestant racing = sports.getSportContestantTeam("rut41");
        SportContestant rabadan = sports.getSportContestantPlayer("rup2276");

        assertEquals(48, stade.getPlayers().size());
        assertEquals(55, racing.getPlayers().size());

        assertTrue(stade.getPlayers().contains(rabadan));
        assertFalse(racing.getPlayers().contains(rabadan));
        assertTrue(rabadan.getTeams().contains(stade));
        assertFalse(rabadan.getTeams().contains(racing));

        file = this.getFile(ru10FeedPath4_2);
        opta.ru10Sync(file);

        stade = sports.getSportContestantTeam("rut1600");
        racing = sports.getSportContestantTeam("rut41");
        rabadan = sports.getSportContestantPlayer("rup2276");

        assertEquals(47, stade.getPlayers().size());
        assertEquals(56, racing.getPlayers().size());

        assertFalse(stade.getPlayers().contains(rabadan));
        assertFalse(rabadan.getTeams().contains(stade));
        assertTrue(racing.getPlayers().contains(rabadan));
        assertTrue(rabadan.getTeams().contains(racing));

    }

    /**
     * Prints a League Table in sys out
     * 
     * @param table: a league table instance
     */
    protected void printTable(TournamentLeagueTable table) {

        System.out.println("Tournanemnt League Table : " + table.getName());

        System.out.println("Table Keys");
        for (TournamentLeagueTableKey key : table.getKeys()) {
            System.out.println("Key=" + key.getKey() + " Name="
                    + key.getColumnName());
        }

        System.out.println("Table Rows: ");
        for (TournamentLeagueTableRow row : table.getRows()) {

            System.out.println("Row for team="
                    + String.valueOf(row.getSportContestant().getUUID()));
            for (TournamentLeagueTableColumn column : row.getColumns()) {
                System.out.println("Key=" + column.getColumnKey() + " value="
                        + column.getColumnValue());
            }

        }

    }

}
