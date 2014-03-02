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
import java.util.Date;
import java.util.List;

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
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.core.sport.SportServiceImpl;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.impl.OptaServiceImpl;
import org.sofun.platform.opta.parser.football.F07Parser;
import org.sofun.platform.opta.testing.SofunCoreTestCase;

/**
 * F feed parser integration tests.
 * 
 * <p/>
 * 
 * Integration tests with JPA and services.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestFParserInt extends SofunCoreTestCase {

    private SportService sports;

    private OptaService optaService;

    private static final String f1_FrenchLigue1 = "feeds/f/srml-24-2011-results.xml";

    private static final String f1_UKPremierLeague = "feeds/f/srml-8-2011-results.xml";

    private static final String f1_ITLiga = "feeds/f/srml-21-2011-results.xml";

    private static final String f1_UEFACL = "feeds/f/srml-5-2011-results.xml";

    private static final String f1_UEFACL_2010 = "feeds/f/srml-5-2010-results.xml";

    private static final String f1_CAN_2010 = "feeds/f/srml-190-2009-results.xml";

    private static final String f3_FrenchLigue1 = "feeds/f/srml-24-2011-standings.xml";

    private static final String f3_UEFACL = "feeds/f/srml-5-2011-standings.xml";

    private static final String f3_UEFACL_2010 = "feeds/f/srml-5-2010-standings.xml";

    private static final String f3_CAN_2010 = "feeds/f/srml-190-2009-standings.xml";

    private static final String f3_UKPremierLeague = "feeds/f/srml-8-2011-standings.xml";

    private static final String f7_FrenchLigue1_01 = "feeds/f/srml-24-2011-f359299-matchresults.xml";

    private static final String f7_FrenchLigue1_02 = "feeds/f/srml-24-2011-f359338-matchresults.xml";

    private static final String f40_FrenchLigue1 = "feeds/f/srml-24-2011-squads.xml";

    private static final String f40_FrenchLigue1_2 = "feeds/f/srml-24-2011-squads-2.xml";

    private static final String f40_UKPremierLeague = "feeds/f/srml-8-2011-squads.xml";

    private static final String f40_UEFACL = "feeds/f/srml-5-2011-squads.xml";

    private static final String f40_UEFACL_2010 = "feeds/f/srml-5-2010-squads.xml";

    private static final String f40_CANL_2010 = "feeds/f/srml-190-2009-squads.xml";

    public TestFParserInt(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sports = new SportServiceImpl(this.em);
        optaService = new OptaServiceImpl(this.em, sports);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        sports = null;
        optaService = null;
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
    public void testF1Sync() throws Exception {

        File file = this.getFile(f40_FrenchLigue1);
        optaService.f40Sync(file);

        file = this.getFile(f1_FrenchLigue1);
        optaService.f1Sync(file);

        Tournament tournament = sports.getSportTournament(24);

        assertEquals(24, tournament.getUUID());
        assertEquals("French Ligue 1", tournament.getName());
        assertEquals("FR_L1", tournament.getCode());
        assertEquals("Soccer", tournament.getSports().get(0).getName());

        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertEquals("2011", season.getYearLabel());
        assertEquals("French Ligue 1 - Season 2011/2012", season.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals(season, tournament.getSeasons().get(0));
        assertEquals(season, sports.getSeasonByYearLabel(tournament, "2011"));

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
                assertNotNull(game.getContestants().get(0));
                assertNotNull(game.getContestants().get(1));
                assertTrue(game.getContestants().get(0).getPlayers().size() >= 11);
                assertTrue(game.getContestants().get(1).getPlayers().size() >= 11);
                assertEquals(2, game.getScore().getScore().size());
            }
        }

    }

    @Test
    public void testF1_02Sync() throws Exception {

        // below is a test with two runs on f1.

        File file = this.getFile(f1_ITLiga);
        optaService.f1Sync(file);

        file = this.getFile(f1_ITLiga);
        optaService.f1Sync(file);

        TournamentGame game = sports.getTournamentGame("f377481");
        game.getStartDate();

    }

    @Test
    public void testF40Sync() throws Exception {

        File file = this.getFile(f40_FrenchLigue1);
        optaService.f40Sync(file);

        Tournament tournament = sports.getSportTournament(24);

        assertEquals(24, tournament.getUUID());
        assertEquals("French Ligue 1", tournament.getName());
        assertEquals("FR_L1", tournament.getCode());
        assertEquals("Soccer", tournament.getSports().get(0).getName());

        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertEquals("2011", season.getYearLabel());
        assertEquals("French Ligue 1 - Season 2011/2012", season.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals(season, tournament.getSeasons().get(0));
        assertEquals(season, sports.getSeasonByYearLabel(tournament, "2011"));

        List<SportContestant> teams = season.getConstestants();
        assertEquals(20, teams.size());

        List<SportContestant> players = season.getPlayers();
        assertEquals(598, players.size());

        for (SportContestant team : teams) {
            assertEquals(SportContestantType.TEAM,
                    team.getSportContestantType());
            assertTrue(team.getPlayers().size() > 11);
        }

        for (SportContestant player : players) {
            assertEquals(SportContestantType.INDIVIDUAL,
                    player.getSportContestantType());
            assertEquals(1, player.getTeams().size());
            assertTrue(teams.contains(player.getTeams().get(0)));
            assertNotNull(player.getName());
            assertNotNull(player.getGivenName());
            assertNotNull(player.getLastName());
            assertNotNull(player.getProperties().get("jersey_num"));
            assertNotNull(player.getProperties().get("weight"));
            assertNotNull(player.getProperties().get("height"));
            assertNotNull(player.getProperties().get("real_position"));
            assertNotNull(player.getProperties().get("birth_date"));
            assertNotNull(player.getProperties().get("position"));

        }

    }

    @Test
    public void testF3_01Sync() throws Exception {

        // Import initial games so that we can actually test on something.
        File file = this.getFile(f40_FrenchLigue1);
        optaService.f40Sync(file);

        file = this.getFile(f1_FrenchLigue1);
        optaService.f1Sync(file);

        file = this.getFile(f3_FrenchLigue1);
        optaService.f3Sync(file);

        Tournament tournament = sports.getSportTournament(24);
        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);

        List<TournamentStage> stages = season.getStages();
        assertEquals(1, stages.size());
        assertEquals(38, season.getRounds().size());
        assertEquals(380, season.getGames().size());
        assertEquals(20, season.getConstestants().size());

        TournamentStage stage = season.getStageByName(season.getName());
        assertNotNull(stage);

        assertEquals(0, stage.getTables().size());

        TournamentRound round = season.getRoundByName("11");
        assertNotNull(round);

        assertEquals(10, round.getGames().size());

        List<TournamentRoundLeagueTable> tables = round.getTables();
        assertEquals(1, tables.size());

        TournamentLeagueTable table = round.getTableByType("main_ranking");
        assertEquals(23, table.getKeys().size());
        assertEquals(20, table.getRows().size());
        for (TournamentLeagueTableRow row : table.getRows()) {
            assertNotNull(row.getSportContestant());
            assertNotNull(row.getLeagueTable());
            assertEquals(23, row.getColumns().size());
            for (TournamentLeagueTableColumn c : row.getColumns()) {
                assertNotNull(c.getColumnValue());
            }
        }

    }

    @Test
    public void testF3_02Sync() throws Exception {

        File file = this.getFile(f40_UKPremierLeague);
        optaService.f40Sync(file);

        file = this.getFile(f1_UKPremierLeague);
        optaService.f1Sync(file);

        file = this.getFile(f3_UKPremierLeague);
        optaService.f3Sync(file);

        Tournament tournament = sports.getSportTournament(8);
        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertNotNull(season);

        List<TournamentStage> stages = season.getStages();
        assertEquals(1, stages.size());

        assertEquals(38, season.getRounds().size());
        assertEquals(380, season.getGames().size());
        assertEquals(20, season.getConstestants().size());

        TournamentStage stage = season.getStageByName(season.getName());
        assertNotNull(stage);

        assertEquals(0, stage.getTables().size());

        TournamentRound round = season.getRoundByName("11");
        assertNotNull(round);

        assertEquals(10, round.getGames().size());

        List<TournamentRoundLeagueTable> tables = round.getTables();
        assertEquals(1, tables.size());

        TournamentLeagueTable table = round.getTableByType("main_ranking");
        assertEquals(23, table.getKeys().size());
        assertEquals(20, table.getRows().size());
        for (TournamentLeagueTableRow row : table.getRows()) {
            assertNotNull(row.getSportContestant());
            assertNotNull(row.getLeagueTable());
            assertEquals(23, row.getColumns().size());
            for (TournamentLeagueTableColumn c : row.getColumns()) {
                assertNotNull(c.getColumnValue());
            }
        }

        boolean drogbaFound = false;
        for (SportContestant team : season.getConstestants()) {
            for (SportContestant player : team.getPlayers()) {
                if (player.getLastName().equals("Drogba")) {
                    drogbaFound = true;
                    break;
                }
            }
        }
        assertTrue(drogbaFound);

    }

    @Test
    public void testF3_03Sync() throws Exception {

        File file = this.getFile(f40_UEFACL);
        optaService.f40Sync(file);

        file = this.getFile(f1_UEFACL);
        optaService.f1Sync(file);

        file = this.getFile(f3_UEFACL);
        optaService.f3Sync(file);

        Tournament tournament = sports.getSportTournament(5);
        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertNotNull(season);

        List<TournamentStage> stages = season.getStages();
        assertEquals(1, stages.size());
        assertEquals(8, season.getRounds().size());
        assertEquals(32, season.getConstestants().size());

        TournamentStage stage = season.getStageByName("Round");
        assertNotNull(stage);

        assertEquals(0, stage.getTables().size());

        List<TournamentRound> rounds = stage.getRounds();
        assertEquals(8, rounds.size());

        String[] groups = { "A", "B", "C", "D", "E", "F", "G", "H" };
        for (String group : groups) {
            TournamentRound round = stage.getRoundByName(group);

            assertEquals(8, stage.getRounds().size());
            assertEquals(96, stage.getGames().size());

            List<TournamentRoundLeagueTable> tables = round.getTables();
            assertEquals(1, tables.size());

            TournamentLeagueTable table = round.getTableByType("main_ranking");
            assertEquals(23, table.getKeys().size());
            assertEquals(4, table.getRows().size());
            for (TournamentLeagueTableRow row : table.getRows()) {
                assertNotNull(row.getSportContestant());
                assertNotNull(row.getLeagueTable());
                assertEquals(23, row.getColumns().size());
                for (TournamentLeagueTableColumn c : row.getColumns()) {
                    assertNotNull(c.getColumnValue());
                }
            }
        }

    }

    @Test
    public void testF3_04Sync() throws Exception {

        File file = this.getFile(f40_CANL_2010);
        optaService.f40Sync(file);

        file = this.getFile(f1_CAN_2010);
        optaService.f1Sync(file);

        file = this.getFile(f3_CAN_2010);
        optaService.f3Sync(file);

        Tournament tournament = sports.getSportTournament(190);
        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertNotNull(season);

        assertEquals(5, season.getStages().size());
        assertEquals(8, season.getRounds().size());
        assertEquals(29, season.getGames().size());
        assertEquals(15, season.getConstestants().size());

        for (TournamentGame game : season.getGames()) {
            assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());
        }

        String[] stageNames = { "Round", "Quarter-Finals", "Semi-Finals",
                "Final", "3rd and 4th Place" };
        for (String stageName : stageNames) {

            TournamentStage stage = season.getStageByName(stageName);
            assertNotNull(stage);
            assertEquals(0, stage.getTables().size());

            if ("Round".equals(stageName)) {

                assertEquals(4, stage.getRounds().size());
                assertEquals(21, stage.getGames().size());

                String[] groups = { "A", "B", "C", "D" };
                for (String group : groups) {
                    TournamentRound round = stage.getRoundByName(group);

                    List<TournamentRoundLeagueTable> tables = round.getTables();
                    assertEquals(1, tables.size());

                    TournamentLeagueTable table = round
                            .getTableByType("main_ranking");
                    assertEquals(23, table.getKeys().size());
                    if ("B".equals(group)) { // One team got cancelled.
                        assertEquals(3, round.getGames().size());
                        assertEquals(3, table.getRows().size());
                    } else {
                        assertEquals(6, round.getGames().size());
                        assertEquals(4, table.getRows().size());
                    }
                    for (TournamentLeagueTableRow row : table.getRows()) {
                        assertNotNull(row.getSportContestant());
                        assertNotNull(row.getLeagueTable());
                        assertEquals(23, row.getColumns().size());
                        for (TournamentLeagueTableColumn c : row.getColumns()) {
                            assertNotNull(c.getColumnValue());
                        }
                    }
                }

            } else if ("Quarter-Finals".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(4, round.getGames().size());

            } else if ("Semi-Finals".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(2, round.getGames().size());

            } else if ("Final".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(1, round.getGames().size());

            } else if ("3rd and 4th Place".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(1, round.getGames().size());

            }

        }

    }

    @Test
    public void testF3_05Sync() throws Exception {

        File file = this.getFile(f40_UEFACL_2010);
        optaService.f40Sync(file);

        file = this.getFile(f1_UEFACL_2010);
        optaService.f1Sync(file);

        file = this.getFile(f3_UEFACL_2010);
        optaService.f3Sync(file);

        Tournament tournament = sports.getSportTournament(5);
        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertNotNull(season);

        for (TournamentGame game : season.getGames()) {
            assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());
        }

        assertEquals(4, season.getStages().size());
        assertEquals(12, season.getRounds().size());
        assertEquals(125, season.getGames().size());
        assertEquals(32, season.getConstestants().size());

        String[] stageNames = { "Round", "Quarter-Finals", "Semi-Finals",
                "Final" };
        for (String stageName : stageNames) {

            TournamentStage stage = season.getStageByName(stageName);
            assertNotNull(stage);
            assertEquals(0, stage.getTables().size());

            if ("Round".equals(stageName)) {

                assertEquals(9, stage.getRounds().size());
                assertEquals(112, stage.getGames().size());

                String[] groups = { "A", "B", "C", "D", "E", "F", "G", "H" };
                for (String group : groups) {
                    TournamentRound round = stage.getRoundByName(group);
                    assertEquals(12, round.getGames().size());

                    List<TournamentRoundLeagueTable> tables = round.getTables();
                    assertEquals(1, tables.size());

                    TournamentLeagueTable table = round
                            .getTableByType("main_ranking");
                    assertEquals(23, table.getKeys().size());

                    assertEquals(12, round.getGames().size());
                    assertEquals(4, table.getRows().size());
                    for (TournamentLeagueTableRow row : table.getRows()) {
                        assertNotNull(row.getSportContestant());
                        assertNotNull(row.getLeagueTable());
                        assertEquals(23, row.getColumns().size());
                        for (TournamentLeagueTableColumn c : row.getColumns()) {
                            assertNotNull(c.getColumnValue());
                        }
                    }

                }

            } else if ("Quarter-Finals".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(8, round.getGames().size());

            } else if ("Semi-Finals".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(4, round.getGames().size());

            } else if ("Final".equals(stageName)) {

                List<TournamentRound> rounds = stage.getRounds();
                assertEquals(1, rounds.size());

                List<TournamentRoundLeagueTable> tables = stage.getRounds()
                        .get(0).getTables();
                assertEquals(0, tables.size());

                TournamentRound round = stage.getRoundByName(stageName);
                assertEquals(1, round.getGames().size());

            }

        }

    }

    @Test
    public void testF7Parser_01() throws Exception {

        File file = this.getFile(f40_FrenchLigue1);
        optaService.f40Sync(file);

        file = this.getFile(f1_FrenchLigue1);
        optaService.f1Sync(file);

        file = this.getFile(f7_FrenchLigue1_01);
        optaService.f7Sync(file);

        Tournament tournament = sports.getSportTournament(24);

        assertEquals(24, tournament.getUUID());
        assertEquals("French Ligue 1", tournament.getName());
        assertEquals("FR_L1", tournament.getCode());
        assertEquals("Soccer", tournament.getSports().get(0).getName());

        TournamentSeason season = sports.getSeasonsFor(tournament).get(0);
        assertEquals("2011", season.getYearLabel());
        assertEquals("French Ligue 1 - Season 2011/2012", season.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals(season, tournament.getSeasons().get(0));
        assertEquals(season, sports.getSeasonByYearLabel(tournament, "2011"));

        assertEquals("French Ligue 1 - Season 2011/2012", season.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals(season, tournament.getSeasons().get(0));

        TournamentGame game = sports.getTournamentGame("f359299");
        assertNotNull(game);

        assertNotNull(game.getContestants());

        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());
        assertNotNull(game.getWinner());

        assertEquals(5, game.getScore().getScoreTeam1());
        assertEquals(3, game.getScore().getScoreTeam2());

        assertEquals(
                "p86446",
                game.getProperties().get(
                        F07Parser.GAME_PROP_PLAYER_UUID_FIRST_GOAL_KEY));

        assertEquals(
                "first_quarter",
                game.getProperties().get(
                        F07Parser.GAME_PROP_FIRST_GOAL_QUARTER_KEY));

        // 20111015T180000+0100
        final Date startDate = game.getStartDate();
        // Now we only set start date if > now()
        assertNull(startDate);
        /*
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        assertEquals(2011, cal.get(Calendar.YEAR));
        assertEquals(9, cal.get(Calendar.MONTH)); // January is zero
        assertEquals(15, cal.get(Calendar.DATE));
        */

    }

    @Test
    public void testF7Parser_02() throws Exception {

        File file = this.getFile(f40_FrenchLigue1);
        optaService.f40Sync(file);

        file = this.getFile(f1_FrenchLigue1);
        optaService.f1Sync(file);

        file = this.getFile(f7_FrenchLigue1_02);
        optaService.f7Sync(file);

        TournamentGame game = sports.getTournamentGame("f359338");
        assertNotNull(game);

        assertNotNull(game.getContestants());

        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());
        assertNotNull(game.getWinner());

        assertEquals(1, game.getScore().getScoreTeam1());
        assertEquals(2, game.getScore().getScoreTeam2());

        assertEquals(
                "p37919",
                game.getProperties().get(
                        F07Parser.GAME_PROP_PLAYER_UUID_FIRST_GOAL_KEY));

        assertEquals(
                "third_quarter",
                game.getProperties().get(
                        F07Parser.GAME_PROP_FIRST_GOAL_QUARTER_KEY));

    }

    @Test
    public void xtestF40ParserPlayersMoving() throws Exception {

        File file = this.getFile(f40_FrenchLigue1);
        optaService.f40Sync(file);

        SportContestant lille = sports.getSportContestantTeam("t429");
        SportContestant bordeaux = sports.getSportContestantTeam("t140");

        assertEquals(27, lille.getPlayers().size());
        assertEquals(28, bordeaux.getPlayers().size());

        SportContestant obraniak = sports.getSportContestantPlayer("p40676");
        assertTrue(lille.getPlayers().contains(obraniak));
        assertFalse(bordeaux.getPlayers().contains(obraniak));
        assertTrue(obraniak.getTeams().contains(lille));
        assertFalse(obraniak.getTeams().contains(bordeaux));

        file = this.getFile(f40_FrenchLigue1_2);
        optaService.f40Sync(file);

        lille = sports.getSportContestantTeam("t429");
        bordeaux = sports.getSportContestantTeam("t140");

        assertEquals(26, lille.getPlayers().size());
        assertEquals(29, bordeaux.getPlayers().size());

        obraniak = sports.getSportContestantPlayer("p40676");
        assertFalse(lille.getPlayers().contains(obraniak));
        assertTrue(bordeaux.getPlayers().contains(obraniak));
        assertFalse(obraniak.getTeams().contains(lille));
        assertTrue(obraniak.getTeams().contains(bordeaux));

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
