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

package org.test.sofun.core.member;

import java.util.ArrayList;
import java.util.List;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Sofun Core Member implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestMemberImpl extends SofunCoreTestCase {

    public TestMemberImpl(String testName) {
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

    public void testCreateMember() throws CoreException {
        Member m = new MemberImpl("julien@anguenot.org", null, null);
        assertEquals("julien@anguenot.org", m.getEmail());
    }

    public void xtestCreateMemberWrongEmail_00() throws CoreException {
        Exception exception = null;
        try {
            new MemberImpl("anguenot", null, null);
        } catch (CoreException e) {
            exception = e;
        }
        assertNotNull("No expected exception", exception);
    }

    public void xtestCreateMemberWrongEmail_01() throws CoreException {
        Exception exception = null;
        try {
            new MemberImpl("anguenot@sofungaming", null, null);
        } catch (CoreException e) {
            exception = e;
        }
        assertNotNull("No expected exception", exception);

    }

    public void xtestCreateMemberWrongEmail_02() throws CoreException {
        Exception exception = null;
        try {
            new MemberImpl("@sofungaming", null, null);
        } catch (CoreException e) {
            exception = e;
        }
        assertNotNull("No expected exception", exception);
    }

    public void xtestCreateMemberWrongEmail_03() throws CoreException {
        Exception exception = null;
        try {
            new MemberImpl("@sofungaming.com", null, null);
        } catch (CoreException e) {
            exception = e;
        }
        assertNotNull("No expected exception", exception);
    }

    public void xtestCreateMemberWrongEmail_04() throws CoreException {
        Exception exception = null;
        try {
            new MemberImpl("aa@soééaming.com", null, null);
        } catch (CoreException e) {
            exception = e;
        }
        assertNotNull("No expected exception", exception);
    }

    public void xtestCreateMemberWrongEmail_05() throws CoreException {
        Exception exception = null;
        try {
            new MemberImpl("aa@sofungaming.céé", null, null);
        } catch (CoreException e) {
            exception = e;
        }
        assertNotNull("No expected exception", exception);
    }

    public void testMemberPersistence() throws CoreException {
        Member m = new MemberImpl("julien@anguenot.org", null, null);
        em.persist(m);
        assertTrue(em.contains(m));
        em.flush();
        commitTransaction();
        assertTrue(em.contains(m));
        beginTransaction();
        em.clear();
        assertFalse(em.contains(m));
    }

    public void testMemberComparison() throws Exception {

        Member m1 = new MemberImpl("juliena@sofungaming.com", null, null);
        Member m2 = new MemberImpl("julienp@sofungaming.com", null, null);

        assertFalse(m1.equals(m2));
        assertFalse(m2.equals(m1));

        m2.setEmail("juliena@sofungaming.com");
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));

        List<Member> l = new ArrayList<Member>();
        l.add(m1);
        l.add(m2);
        assertEquals(2, l.size());
        assertTrue(l.contains(m1));
        assertTrue(l.contains(m2));

        l.remove(1);
        l.remove(0);

        assertEquals(0, l.size());
        assertFalse(l.contains(m1));
        assertFalse(l.contains(m2));

    }

}
