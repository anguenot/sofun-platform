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

package org.test.sofun.core.community;

import org.junit.Test;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.community.CommunityImpl;
import org.sofun.core.member.MemberImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Community Implementation Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestCommunityImpl extends SofunCoreTestCase {

    public TestCommunityImpl(String testName) {
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
    public void testCreate() throws CoreException {
        final Community c = new CommunityImpl("Betkup");
        assertEquals("Betkup", c.getName());
        assertNull(c.getDescription());
        assertNotNull(c.getId());
    }

    @Test
    public void testPersistence() throws CoreException {
        final Community c = new CommunityImpl("Betkup");
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
    public void testComparison() {

        final Community c1 = new CommunityImpl("one");
        final Community c2 = new CommunityImpl("two");

        final Community c11 = new CommunityImpl("one");
        final Community c22 = new CommunityImpl("two");

        assertTrue(c11.equals(c1));
        assertTrue(c1.equals(c11));
        assertTrue(c22.equals(c2));
        assertTrue(c2.equals(c22));

        c11.setName("two");
        c22.setName("one");

        assertFalse(c11.equals(c1));
        assertFalse(c1.equals(c11));
        assertFalse(c22.equals(c2));
        assertFalse(c2.equals(c22));

        assertTrue(c22.equals(c1));
        assertTrue(c1.equals(c22));
        assertTrue(c11.equals(c2));
        assertTrue(c2.equals(c11));

    }

    private Member createMember(String email) throws Exception {
        return new MemberImpl(email, null, null);
    }

    @Test
    public void testMembers() throws Exception {

        final Community c1 = new CommunityImpl("Betkup");
        final Community c2 = new CommunityImpl("Another one");

        final Member m1 = createMember("juliena@sofungaming.com");
        final Member m2 = createMember("julienp@sofungaming.com");

        c1.addMember(m1);
        assertEquals(1, c1.countMembers());
        assertEquals(0, c2.countMembers());
        assertTrue(c1.isMember(m1));
        assertFalse(c1.isMember(m2));
        assertFalse(c2.isMember(m1));
        assertFalse(c2.isMember(m2));

        c1.addMember(m2);
        assertEquals(2, c1.countMembers());
        assertEquals(0, c2.countMembers());
        assertTrue(c1.isMember(m1));
        assertTrue(c1.isMember(m2));
        assertFalse(c2.isMember(m1));
        assertFalse(c2.isMember(m2));

        c1.removeMember(m1);
        assertEquals(1, c1.countMembers());
        assertEquals(0, c2.countMembers());
        assertFalse(c1.isMember(m1));
        assertTrue(c1.isMember(m2));
        assertFalse(c2.isMember(m1));
        assertFalse(c2.isMember(m2));

        c2.addMember(m1);
        assertEquals(1, c1.countMembers());
        assertEquals(1, c2.countMembers());
        assertFalse(c1.isMember(m1));
        assertTrue(c1.isMember(m2));
        assertTrue(c2.isMember(m1));
        assertFalse(c2.isMember(m2));

        c2.removeMember(m1);
        c1.removeMember(m2);
        assertEquals(0, c1.countMembers());
        assertEquals(0, c2.countMembers());
        assertFalse(c1.isMember(m1));
        assertFalse(c1.isMember(m2));
        assertFalse(c2.isMember(m1));
        assertFalse(c2.isMember(m2));

    }

    @Test
    public void testAdmins() throws Exception {

        final Community c1 = new CommunityImpl("Betkup");
        final Community c2 = new CommunityImpl("Another one");

        final Member m1 = createMember("juliena@sofungaming.com");
        final Member m2 = createMember("julienp@sofungaming.com");

        c1.addAdmin(m1);
        assertEquals(1, c1.countAdmins());
        assertEquals(0, c2.countAdmins());
        assertTrue(c1.isAdmin(m1));
        assertFalse(c1.isAdmin(m2));
        assertFalse(c2.isAdmin(m1));
        assertFalse(c2.isAdmin(m2));

        c1.addAdmin(m2);
        assertEquals(2, c1.countAdmins());
        assertEquals(0, c2.countAdmins());
        assertTrue(c1.isAdmin(m1));
        assertTrue(c1.isAdmin(m2));
        assertFalse(c2.isAdmin(m1));
        assertFalse(c2.isAdmin(m2));

        c1.removeAdmin(m1);
        assertEquals(1, c1.countAdmins());
        assertEquals(0, c2.countAdmins());
        assertFalse(c1.isAdmin(m1));
        assertTrue(c1.isAdmin(m2));
        assertFalse(c2.isAdmin(m1));
        assertFalse(c2.isAdmin(m2));

        c2.addAdmin(m1);
        assertEquals(1, c1.countAdmins());
        assertEquals(1, c2.countAdmins());
        assertFalse(c1.isAdmin(m1));
        assertTrue(c1.isAdmin(m2));
        assertTrue(c2.isAdmin(m1));
        assertFalse(c2.isAdmin(m2));

        c2.removeAdmin(m1);
        c1.removeAdmin(m2);
        assertEquals(0, c1.countAdmins());
        assertEquals(0, c2.countAdmins());
        assertFalse(c1.isAdmin(m1));
        assertFalse(c1.isAdmin(m2));
        assertFalse(c2.isAdmin(m1));
        assertFalse(c2.isAdmin(m2));

    }

}
