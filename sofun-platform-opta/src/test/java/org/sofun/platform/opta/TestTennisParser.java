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
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.sport.SportServiceImpl;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.impl.OptaServiceImpl;
import org.sofun.platform.opta.testing.SofunCoreTestCase;

/**
 * TAB Feed Parsers Integration test case.
 * 
 * @author @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTennisParser extends SofunCoreTestCase {

    private SportService sports;

    private OptaService opta;

    private static final String tab1Feed = "feeds/t/TAB1.xml";

    private static final String tab2Feed = "feeds/t/TAB2.xml";

    private static final String tab7Feed = "feeds/t/TAB7.xml";

    public TestTennisParser(String testName) {
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
    public void testTAB1() throws Exception {

        File file = this.getFile(tab1Feed);
        opta.tab1Sync(file);

        Sport tennis = sports.getSportByName("Tennis");
        assertEquals("Tennis", tennis.getName());

        Tournament tournament = sports.getSportTournamentByCode("Grand Slam");
        assertEquals("Tennis - Grand Slam", tournament.getName());

        List<TournamentSeason> seasons = tournament.getSeasons();
        assertEquals(4, seasons.size());

        for (TournamentSeason season : seasons) {
            assertNotNull(season.getName());
            assertNotNull(season.getId());
            assertNotNull(season.getStartDate());
            assertNotNull(season.getEndDate());
            assertNotNull(season.getStatus());
            assertTrue(season.getStages().size() >= 2);
            for (TournamentStage stage : season.getStages()) {
                assertNotNull(stage.getName());
                assertNotNull(stage.getId());
                assertEquals(3, stage.getProperties().size());
            }
            if ("Tennis - Grand Slam - US Open".equals(season.getName())) {
                Date now = new Date();
                assertEquals(7312, season.getId());
                assertEquals(TournamentSeasonStatus.TERMINATED,
                        season.getStatus());
                assertTrue(season.getStartDate().compareTo(now) < 0);
                assertTrue(season.getEndDate().compareTo(now) < 0);
                assertTrue(season.getEndDate().compareTo(season.getStartDate()) > 0);
            }
        }

    }

    @Test
    public void testTAB2() throws Exception {

        File file = this.getFile(tab1Feed);
        opta.tab1Sync(file);
        file = this.getFile(tab2Feed);
        opta.tab2Sync(file);

        Tournament tournament = sports.getSportTournamentByCode("Grand Slam");
        TournamentSeason season = sports.getSeasonByUUID(tournament, 7295);

        final String stageName = season.getName() + " - " + "Mens Singles";
        TournamentStage stage = season.getStageByName(stageName);

        assertEquals(7, stage.getRounds().size());

    }

    @Test
    public void testTAB7() throws Exception {

        File file = this.getFile(tab1Feed);
        opta.tab1Sync(file);
        file = this.getFile(tab2Feed);
        opta.tab2Sync(file);

        TournamentGame game = sports.getTournamentGame("TAB292324");
        assertNotNull(game);
        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());
        assertEquals(0, game.getContestants().size());
        assertEquals(0, game.getScore().getScoreTeam1());
        assertEquals(0, game.getScore().getScoreTeam2());

        file = this.getFile(tab7Feed);
        opta.tab7Sync(file);

        assertEquals(TournamentGameStatus.TERMINATED, game.getGameStatus());
        assertEquals(2, game.getContestants().size());

        // Second run
        file = this.getFile(tab7Feed);
        opta.tab7Sync(file);

        assertEquals(2, game.getContestants().size());

        SportContestant c1 = sports.getSportContestantPlayer("TABP4");
        assertNotNull(c1);
        assertEquals("Rafael Nadal", c1.getName());
        assertEquals("ESP", c1.getProperties().get("nationality"));

        SportContestant c2 = sports.getSportContestantPlayer("TABP3");
        assertNotNull(c2);
        assertEquals("Roger Federer", c2.getName());
        assertEquals("SUI", c2.getProperties().get("nationality"));

    }

}
