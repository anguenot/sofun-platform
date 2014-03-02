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

package org.test.sofun.core.sport;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportCategory;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.sport.SportCategoryImpl;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.SportImpl;
import org.sofun.core.sport.tournament.TournamentGameImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestSport extends SofunCoreTestCase {

    public TestSport(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSportCategoryPersitence() {

        SportCategory c1 = new SportCategoryImpl("Soccer");
        assertEquals("Soccer", c1.getName());
        assertEquals(0, c1.getUUID());
        em.persist(c1);
        assertTrue(em.contains(c1));
        em.flush();
        commitTransaction();
        assertTrue(em.contains(c1));
        beginTransaction();
        em.clear();
        assertFalse(em.contains(c1));
    }

    @Test
    public void testSportPersitence() {

        Sport s1 = new SportImpl("Soccer");
        assertEquals("Soccer", s1.getName());
        assertEquals(0, s1.getUUID());
        em.persist(s1);
        assertTrue(em.contains(s1));
        em.flush();
        commitTransaction();
        assertTrue(em.contains(s1));
        beginTransaction();
        em.clear();
        assertFalse(em.contains(s1));
    }

    @Test
    public void testSportContestantPersitence() {

        SportContestant c1 = new SportContestantImpl("1", SportContestantType.INDIVIDUAL);
        c1.setName("Dude");
        assertEquals("Dude", c1.getName());
        assertEquals("1", c1.getUUID());
        em.persist(c1);
        assertTrue(em.contains(c1));
        em.flush();
        commitTransaction();
        assertTrue(em.contains(c1));
        beginTransaction();
        em.clear();
        assertFalse(em.contains(c1));
    }

    @Test
    public void testSportContestantTeamsPersitence() {

        SportContestant c1 = new SportContestantImpl("1", SportContestantType.INDIVIDUAL);
        c1.setName("Dude1");
        
        assertEquals("Dude1", c1.getName());
        c1.setUUID("1");
        assertEquals("1", c1.getUUID());

        SportContestant c2 = new SportContestantImpl("2", SportContestantType.INDIVIDUAL);
        c2.setName("Dude2");
        
        assertEquals("Dude2", c2.getName());
        c2.setUUID("2");
        assertEquals("2", c2.getUUID());

        List<SportContestant> team_players = new ArrayList<SportContestant>();
        team_players.add(c1);
        team_players.add(c2);

        SportContestant t1 = new SportContestantImpl("Team1", SportContestantType.TEAM);
        t1.setTeams(team_players);

        // em.persist(c1);
        // em.persist(c2);
        em.persist(t1);
        assertTrue(em.contains(t1));

        em.flush();
        commitTransaction();

        assertTrue(em.contains(t1));
        beginTransaction();

        em.clear();
        assertFalse(em.contains(c1));
        assertFalse(em.contains(c2));
        assertFalse(em.contains(t1));

    }

    @Test
    public void testSportTournamentPersistence() {

        Tournament t = new TournamentImpl("Ligue 1", 1);
        em.persist(t);
        em.flush();

    }

    @Test
    public void testSportTournamentGamePersistence() {

        TournamentGame tg = new TournamentGameImpl();
        em.persist(tg);
        em.flush();

    }

    @Test
    public void testSportTournamentStagePersistence() {

        TournamentStage ts = new TournamentStageImpl();
        em.persist(ts);
        em.flush();

    }

    @Test
    public void testSportTournamentSeasonPersistence() {

        TournamentSeason ts = new TournamentSeasonImpl();
        ts.setUUID(1);
        ts.setYearLabel("2010/2011");
        em.persist(ts);
        em.flush();

    }

}
