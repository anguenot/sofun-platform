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
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamPrivacy;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.team.TeamImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Team implementation Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTeamImpl extends SofunCoreTestCase {

    public TestTeamImpl(String testName) {
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
    public void testCreateTeam() throws CoreException {
        Team t = new TeamImpl("Ligue1");
        assertEquals("Ligue1", t.getName());
        assertNull(t.getDescription());
        assertEquals(TeamPrivacy.PRIVATE, t.getPrivacy());
        assertNull(t.getCreated());
    }

    @Test
    public void testTeamPersistence() throws CoreException {
        Team c = new TeamImpl("Ligue1");
        em.persist(c);
        assertTrue(em.contains(c));
        em.flush();
        commitTransaction();
        assertTrue(em.contains(c));
        beginTransaction();
        em.clear();
        assertFalse(em.contains(c));
    }

    @Test
    public void testTeamComparison() {

        // Several teams can have the same name.
        
        Team t1 = new TeamImpl("one");
        Team t2 = new TeamImpl("two");

        Team t11 = new TeamImpl("one");
        Team t22 = new TeamImpl("two");

        assertTrue(t11.equals(t1));
        assertTrue(t1.equals(t11));
        assertTrue(t22.equals(t2));
        assertTrue(t2.equals(t22));

    }

    private Member createMember(String email) throws Exception {
        return new MemberImpl(email, null, null);
    }

    @Test
    public void testMemberTeamAPI() throws Exception {

        Team t1 = new TeamImpl("Ligue 1");
        Team t2 = new TeamImpl("Top 14");

        Member m1 = createMember("juliena@sofungaming.com");
        Member m2 = createMember("julienp@sofungaming.com");

        t1.addMember(m1);
        assertEquals(1, t1.countMembers());
        assertEquals(0, t2.countMembers());
        assertTrue(t1.isMember(m1));
        assertFalse(t1.isMember(m2));
        assertFalse(t2.isMember(m1));
        assertFalse(t2.isMember(m2));

        t1.addMember(m2);
        assertEquals(2, t1.countMembers());
        assertEquals(0, t2.countMembers());
        assertTrue(t1.isMember(m1));
        assertTrue(t1.isMember(m2));
        assertFalse(t2.isMember(m1));
        assertFalse(t2.isMember(m2));

        t1.removeMember(m1);
        assertEquals(1, t1.countMembers());
        assertEquals(0, t2.countMembers());
        assertFalse(t1.isMember(m1));
        assertTrue(t1.isMember(m2));
        assertFalse(t2.isMember(m1));
        assertFalse(t2.isMember(m2));

        t2.addMember(m1);
        assertEquals(1, t1.countMembers());
        assertEquals(1, t2.countMembers());
        assertFalse(t1.isMember(m1));
        assertTrue(t1.isMember(m2));
        assertTrue(t2.isMember(m1));
        assertFalse(t2.isMember(m2));

        t2.removeMember(m1);
        t1.removeMember(m2);
        assertEquals(0, t1.countMembers());
        assertEquals(0, t2.countMembers());
        assertFalse(t1.isMember(m1));
        assertFalse(t1.isMember(m2));
        assertFalse(t2.isMember(m1));
        assertFalse(t2.isMember(m2));

    }

    @Test
    public void testAdminsTeamAPI() throws Exception {

        Team t1 = new TeamImpl("Ligue 1");
        Team t2 = new TeamImpl("Top 14");

        Member m1 = createMember("juliena@sofungaming.com");
        Member m2 = createMember("julienp@sofungaming.com");

        t1.addAdmin(m1);
        assertEquals(1, t1.countAdmins());
        assertEquals(0, t2.countAdmins());
        assertTrue(t1.isAdmin(m1));
        assertFalse(t1.isAdmin(m2));
        assertFalse(t2.isAdmin(m1));
        assertFalse(t2.isAdmin(m2));

        t1.addAdmin(m2);
        assertEquals(2, t1.countAdmins());
        assertEquals(0, t2.countAdmins());
        assertTrue(t1.isAdmin(m1));
        assertTrue(t1.isAdmin(m2));
        assertFalse(t2.isAdmin(m1));
        assertFalse(t2.isAdmin(m2));

        t1.removeAdmin(m1);
        assertEquals(1, t1.countAdmins());
        assertEquals(0, t2.countAdmins());
        assertFalse(t1.isAdmin(m1));
        assertTrue(t1.isAdmin(m2));
        assertFalse(t2.isAdmin(m1));
        assertFalse(t2.isAdmin(m2));

        t2.addAdmin(m1);
        assertEquals(1, t1.countAdmins());
        assertEquals(1, t2.countAdmins());
        assertFalse(t1.isAdmin(m1));
        assertTrue(t1.isAdmin(m2));
        assertTrue(t2.isAdmin(m1));
        assertFalse(t2.isAdmin(m2));

        t2.removeAdmin(m1);
        t1.removeAdmin(m2);
        assertEquals(0, t1.countAdmins());
        assertEquals(0, t2.countAdmins());
        assertFalse(t1.isAdmin(m1));
        assertFalse(t1.isAdmin(m2));
        assertFalse(t2.isAdmin(m1));
        assertFalse(t2.isAdmin(m2));

    }

}
