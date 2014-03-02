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

package org.test.sofun.core.team;

import org.junit.Test;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.community.CommunityService;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamService;
import org.sofun.core.community.CommunityServiceImpl;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.team.TeamImpl;
import org.sofun.core.team.TeamServiceImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Team Service Integration Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTeamServiceImpl extends SofunCoreTestCase {

    private TeamService teams;

    private CommunityService communities;

    public TestTeamServiceImpl(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        teams = new TeamServiceImpl(em);
        communities = new CommunityServiceImpl(em);
    }

    @Override
    protected void tearDown() throws Exception {
        teams = null;
        communities = null;
        super.tearDown();
    }

    @Test
    public void testAPI() throws Exception {

        assertEquals(0, teams.countTeams());

        Team t1 = teams.addTeam("Ligue 1");
        assertTrue(teams.exists(t1));
        assertEquals(1, teams.countTeams());
        assertEquals(t1, teams.getTeam(t1.getId()));

        Team t2 = teams.addTeam("Another one");
        assertTrue(teams.exists(t2));
        assertEquals(2, teams.countTeams());
        assertEquals(t2, teams.getTeam(t2.getId()));

        assertTrue(!t1.getName().equals(t2.getName()));

        Team newTeam = new TeamImpl("Ligue 1");
        newTeam = teams.addTeam(newTeam);
        assertTrue(teams.exists(newTeam));
        assertEquals(2, teams.countTeams());
        assertEquals(newTeam, teams.getTeam(newTeam.getId()));
        assertTrue(newTeam.equals(t1));
        assertFalse(newTeam.equals(t2));

        // Test team update.

        assertEquals(t1.getDescription(), null);
        t1.setDescription("New description");
        teams.updateTeam(t1);

        commitTransaction();
        beginTransaction();

        t1 = teams.getTeam(t1.getId());
        assertEquals("New description", t1.getDescription());

        assertEquals(2, teams.countTeams());

        /*
         * teams.removeTeam(newTeam); assertFalse(teams.exists(newTeam));
         * assertEquals(1, teams.countTeams());
         * 
         * teams.removeTeamById(t2.getId()); assertFalse(teams.exists(t2));
         * assertEquals(0, teams.countTeams());
         */

    }

    public void testRemove() throws Exception {

        Community c = communities.addCommunity("new");
        assertNotNull(c);
        c = communities.getCommunityByName("new");
        assertNotNull(c);

        assertEquals(0, teams.countTeams());

        em.getTransaction().commit();
        em.getTransaction().begin();

        // Test delete
        final Member m1 = new MemberImpl("juliena@sofungaming.com",
                MemberAccountStatus.CREATED, MemberAccountType.SIMPLE);
        em.persist(m1);

        final Member m2 = new MemberImpl("julienp@sofungaming.com",
                MemberAccountStatus.CREATED, MemberAccountType.SIMPLE);
        em.persist(m2);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Team t1 = teams.addTeam(teams.createTeam("Ligue 1", false));
        t1 = teams.getTeam("Ligue 1");
        assertNotNull(t1);
        c.addTeam(t1);
        t1 = teams.getTeam("Ligue 1");
        assertNotNull(t1);

        em.getTransaction().commit();
        em.getTransaction().begin();

        t1.addMember(m1);
        t1.addMember(m2);

        em.persist(t1);

        Team t2 = teams.addTeam(teams.createTeam("Ligue 2", false));
        c.addTeam(t2);
        t2 = teams.getTeam("Ligue 2");
        assertNotNull(t2);
        assertEquals(2, c.getTeams().size());
        t2 = teams.getTeam("Ligue 2");
        assertNotNull(t2);

        t2.addMember(m1);

        em.getTransaction().commit();
        em.getTransaction().begin();

        t2 = teams.getTeam("Ligue 2");
        c.delTeam(t2);
        teams.removeTeam(t2);

        t1 = teams.getTeam("Ligue 1");

        // We should still find the actual members here.
        assertEquals(2, t1.getMembers().size());

    }

}
