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

import java.util.Iterator;

import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.member.MemberServiceImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Member Service Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestMemberServiceImpl extends SofunCoreTestCase {

    private MemberService service;

    public TestMemberServiceImpl(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new MemberServiceImpl(em);
    }

    @Override
    protected void tearDown() throws Exception {
        service = null;
        super.tearDown();
    }
    
    public void testMemberService() {
        assertNotNull(service);
    }

    public void testCreateDeleteMember() throws Exception {

        // Member m = new MemberImpl("julien@anguenot.org");
        // em.persist(m);

        // Create a new member
        Member m = service.createMember("julien@anguenot.org", null, null);
        assertNotNull(m);
        assertEquals("julien@anguenot.org", m.getEmail());
        assertEquals(1, service.countMembers());

        // Try to create another member with the same email.
        Member n = service.createMember("julien@anguenot.org", null, null);
        assertNotNull(n);
        assertEquals("julien@anguenot.org", n.getEmail());
        assertEquals(1, service.countMembers());

        // Try to create another member with another email.
        Member o = service.createMember("julienp@sofungaming.com", null, null);
        assertNotNull(o);
        assertEquals("julienp@sofungaming.com", o.getEmail());
        assertEquals(2, service.countMembers());

        // Getting members using iterator
        Iterator<Member> it = service.listMembers(0, 100);
        int i = 0;
        while (it.hasNext()) {
            i++;
            Member one = it.next();
            assertTrue(one != null);
        }
        assertEquals(2, i);

        // Delete Member
        o = service.deleteMember("julienp@sofungaming.com");
        assertNotNull(o);
        assertEquals("julienp@sofungaming.com", o.getEmail());

        assertEquals(1, service.countMembers());
        n = service.createMember("julien@anguenot.org", null, null);
        assertNotNull(n);
        assertEquals("julien@anguenot.org", n.getEmail());
        assertEquals(1, service.countMembers());

        // Delete Member
        o = service.deleteMember("julien@anguenot.org");
        assertNotNull(o);
        assertEquals("julien@anguenot.org", o.getEmail());

    }

}
