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
import java.util.List;

import org.junit.Test;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.sport.SportServiceImpl;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.impl.OptaServiceImpl;
import org.sofun.platform.opta.testing.SofunCoreTestCase;

/**
 * BB feed testing.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestBBParserInt extends SofunCoreTestCase {

    public SportService sports;

    public OptaService opta;

    private static final String bb1Feed = "feeds/bb/BB1-6-2012-7.xml";

    public TestBBParserInt(String testName) {
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
        sports = null;
        opta = null;
        super.tearDown();
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
    public void testBB1() throws Exception {

        File f = this.getFile(bb1Feed);
        opta.bb1Sync(f);

        assertEquals(30, sports.countSportContestantTeams());
        List<TournamentSeason> seasons = sports
                .getTournamentSeasonsByStatus(TournamentSeasonStatus.SCHEDULED);
        TournamentSeason nba = seasons.get(0);
        List<SportContestant> teams = nba.getConstestants();
        assertEquals(30, teams.size());
        
        for (SportContestant team : teams) {
            assertNotNull(team.getName());
        }
        
        assertEquals(1, nba.getStages().size());
        assertEquals(1, nba.getRounds().size());
        assertEquals(1230, nba.getGames().size());
        
        for (TournamentGame game : nba.getGames()) {
            assertEquals(2, game.getContestants().size());
            assertNotNull(game.getGameStatus());
            assertNotNull(game.getStartDate());
            if (TournamentGameStatus.TERMINATED.equals(game.getGameStatus())) {
                assertTrue(game.getScore().getScoreTeam1() != 0);
                assertTrue(game.getScore().getScoreTeam2() != 0);
            }
        }

    }

}
