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
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.core.api.sport.tournament.table.TournamentSeasonLeagueTable;
import org.sofun.core.sport.SportServiceImpl;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.impl.OptaServiceImpl;
import org.sofun.platform.opta.parser.f1.MR1Parser;
import org.sofun.platform.opta.parser.f1.MR2Parser;
import org.sofun.platform.opta.parser.f1.MR4Parser;
import org.sofun.platform.opta.parser.f1.MR5Parser;
import org.sofun.platform.opta.parser.f1.MR6Parser;
import org.sofun.platform.opta.testing.SofunCoreTestCase;

/**
 * MR Feed Parsers Integration test case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestMRParser extends SofunCoreTestCase {

    private SportService sports;

    private OptaService opta;

    private static final String mr4Feed = "feeds/f1/F1_STANDINGS_DRIVER_2012.xml";

    private static final String mr5Feed = "feeds/f1/F1_STANDINGS_TEAMS_2012.xml";

    private static final String mr6Feed = "feeds/f1/F1_CALENDAR_2012.xml";

    private static final String mr2FeedQUALI = "feeds/f1/F1_QUALI_01_20120317_NT.xml";

    private static final String mr1FeedRace = "feeds/f1/F1_RACE_01_20120318_NT.xml";

    public TestMRParser(String testName) {
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
    public void testMR1Parser() throws Exception {

        File file = this.getFile(mr6Feed);
        opta.mr6Sync(file);

        file = this.getFile(mr4Feed);
        opta.mr4Sync(file);

        file = this.getFile(mr5Feed);
        opta.mr5Sync(file);

        file = this.getFile(mr1FeedRace);
        MR1Parser p = opta.mr1Sync(file);

        TournamentRound round = p.getRound();
        TournamentRoundLeagueTable table = round
                .getTableByType(MR2Parser.TABLE_TYPE_DRIVERS);

        assertNotNull(table);

        assertEquals(5, table.getKeys().size());
        List<TournamentLeagueTableRow> rows = table.getRows();
        assertEquals(22, rows.size());

        assertEquals("f1d1",
                round.getProperties().get(MR1Parser.FAST_TIME_DRIVER_UUID));

        SportContestant button = sports.getSportContestantPlayer("f1d1");
        assertEquals("1", table.getRowFor(button).getColumn("pos")
                .getColumnValue());

        SportContestant vettel = sports.getSportContestantPlayer("f1d5");
        assertEquals("2", table.getRowFor(vettel).getColumn("pos")
                .getColumnValue());

        SportContestant hamilton = sports.getSportContestantPlayer("f1d2");
        assertEquals("3", table.getRowFor(hamilton).getColumn("pos")
                .getColumnValue());

        SportContestant webber = sports.getSportContestantPlayer("f1d6");
        assertEquals("4", table.getRowFor(webber).getColumn("pos")
                .getColumnValue());

    }

    @Test
    public void testMR2Parser() throws Exception {

        File file = this.getFile(mr6Feed);
        opta.mr6Sync(file);

        file = this.getFile(mr4Feed);
        opta.mr4Sync(file);

        file = this.getFile(mr5Feed);
        opta.mr5Sync(file);

        file = this.getFile(mr2FeedQUALI);
        MR2Parser p = opta.mr2Sync(file);

        TournamentRound round = p.getRound();
        TournamentRoundLeagueTable table = round
                .getTableByType(MR2Parser.TABLE_TYPE_DRIVERS);

        assertNotNull(table);

        assertEquals(1, table.getKeys().size());
        List<TournamentLeagueTableRow> rows = table.getRows();
        assertEquals(24, rows.size());

        SportContestant hamilton = sports.getSportContestantPlayer("f1d2");
        assertEquals("1", table.getRowFor(hamilton).getColumn("pos")
                .getColumnValue());

        SportContestant button = sports.getSportContestantPlayer("f1d1");
        assertEquals("2", table.getRowFor(button).getColumn("pos")
                .getColumnValue());

        SportContestant grosjean = sports.getSportContestantPlayer("f1d291");
        assertEquals("3", table.getRowFor(grosjean).getColumn("pos")
                .getColumnValue());

        SportContestant schumacher = sports.getSportContestantPlayer("f1d3");
        assertEquals("4", table.getRowFor(schumacher).getColumn("pos")
                .getColumnValue());

        SportContestant vettel = sports.getSportContestantPlayer("f1d5");
        assertEquals("6", table.getRowFor(vettel).getColumn("pos")
                .getColumnValue());

    }

    @Test
    public void testMR4Parser() throws Exception {

        File file = this.getFile(mr6Feed);
        opta.mr6Sync(file);

        file = this.getFile(mr4Feed);
        MR4Parser p = opta.mr4Sync(file);

        TournamentSeason season = p.getSeason();
        assertNotNull(season);

        assertNull(season.getTableByType(MR4Parser.TABLE_TYPE_LIVE));

        TournamentSeasonLeagueTable table = season
                .getTableByType(MR4Parser.TABLE_TYPE_POST_RACE);
        assertNotNull(table);

        assertEquals(3, table.getKeys().size());
        List<TournamentLeagueTableRow> rows = table.getRows();
        assertEquals(24, rows.size());

        for (TournamentLeagueTableRow row : rows) {

            SportContestant contestant = row.getSportContestant();
            assertNotNull(contestant);
            assertEquals(SportContestantType.INDIVIDUAL,
                    contestant.getSportContestantType());
            assertTrue(contestant.getUUID().startsWith(MR4Parser.PREFIX_DRIVER));
            assertNotNull(contestant.getName());
            assertNotNull(contestant.getGivenName());
            assertNotNull(contestant.getLastName());
            assertNotNull(contestant.getProperties().get("nat"));
            assertNotNull(contestant.getProperties().get("driver_code"));

            assertEquals(1, contestant.getTeams().size());
            SportContestant team = contestant.getTeams().get(0);
            assertNotNull(team);
            assertEquals(SportContestantType.TEAM,
                    team.getSportContestantType());
            assertTrue(team.getUUID().startsWith(MR5Parser.PREFIX_TEAM));
            assertNotNull(team.getName());
            assertNotNull(team.getProperties().get("team_carbike"));
            assertNotNull(team.getProperties().get("team_engine"));

            assertEquals(table, row.getLeagueTable());
            assertNotNull(row.getColumn("pos"));
            assertNotNull(row.getColumn("points"));
            assertNotNull(row.getColumn("victories"));
        }

    }

    @Test
    public void testMR5Parser() throws Exception {

        File file = this.getFile(mr6Feed);
        opta.mr6Sync(file);

        file = this.getFile(mr5Feed);
        MR5Parser p = opta.mr5Sync(file);

        TournamentSeason season = p.getSeason();
        assertNotNull(season);
        assertNull(season.getTableByType(MR5Parser.TABLE_TYPE_LIVE));
        assertEquals(12, season.getConstestants().size());

        TournamentSeasonLeagueTable table = season
                .getTableByType(MR5Parser.TABLE_TYPE_POST_RACE);
        assertNotNull(table);

        assertEquals(3, table.getKeys().size());
        List<TournamentLeagueTableRow> rows = table.getRows();
        assertEquals(12, rows.size());

        for (TournamentLeagueTableRow row : rows) {

            SportContestant contestant = row.getSportContestant();
            assertNotNull(contestant);
            assertEquals(SportContestantType.TEAM,
                    contestant.getSportContestantType());
            assertTrue(contestant.getUUID().startsWith(MR5Parser.PREFIX_TEAM));
            assertNotNull(contestant.getName());
            assertNotNull(contestant.getProperties().get("team_carbike"));
            assertNotNull(contestant.getProperties().get("team_engine"));

            assertEquals(table, row.getLeagueTable());
            assertNotNull(row.getColumn("pos"));
            assertNotNull(row.getColumn("points"));
            assertNotNull(row.getColumn("victories"));
        }

    }

    @Test
    public void testMR6Parser() throws Exception {

        File file = this.getFile(mr6Feed);
        MR6Parser p = opta.mr6Sync(file);

        Tournament tournament = p.getTournament();
        TournamentSeason season = p.getSeason();

        assertEquals("F1", tournament.getCode());
        assertEquals("Formula 1", tournament.getName());

        assertEquals(tournament, season.getTournament());
        assertEquals("Formula 1 - 2012", season.getName());

        List<TournamentStage> stages = season.getStages();
        assertEquals(20, stages.size());

        List<String> tracks = new ArrayList<String>();
        for (TournamentStage stage : stages) {
            tracks.add(stage.getDescription());
            List<String> sessions = new ArrayList<String>();
            assertEquals(5, stage.getRounds().size());
            for (TournamentRound round : stage.getRounds()) {
                sessions.add(round.getRoundLabel());
                assertEquals(TournamentRoundStatus.SCHEDULED, round.getStatus());
            }
            String[] expectedSessions = new String[] { "FP1", "FP2", "FP3",
                    "QUALI", "RACE" };
            for (String expected : expectedSessions) {
                assertTrue(sessions.contains(expected));
            }
        }

        String[] expectedTracks = new String[] { "Bahrain", "Melbourne",
                "Kuala Lumpur", "Shanghai", "Yeongam", "Catalunya",
                "Monte Carlo", "Montreal", "Austin", "Valencia", "Silverstone",
                "Hockenheim", "Hungaroring", "Spa-Francorchamps", "Monza",
                "Singapore", "Suzuka", "New Delhi", "Yas Marina", "Sao Paulo" };
        for (String expected : expectedTracks) {
            assertTrue(tracks.contains(expected));
        }

    }

}
