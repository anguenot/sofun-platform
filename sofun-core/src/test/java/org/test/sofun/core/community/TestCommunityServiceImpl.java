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
import org.sofun.core.api.community.CommunityService;
import org.sofun.core.community.CommunityServiceImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Community Service Integration Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestCommunityServiceImpl extends SofunCoreTestCase {

    private CommunityService communities;

    public TestCommunityServiceImpl(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        communities = new CommunityServiceImpl(em);

    }

    @Override
    protected void tearDown() throws Exception {
        communities = null;
        super.tearDown();
    }

    @Test
    public void testAPI() throws Exception {

        assertEquals(0, communities.count());

        Community c1 = communities.addCommunity("Betkup");
        assertTrue(communities.exists("Betkup"));
        assertTrue(communities.exists(c1.getName()));
        assertEquals(1, communities.count());

        Community c2 = communities.addCommunity("Another one");
        assertTrue(communities.exists("Betkup"));
        assertTrue(communities.exists("Another one"));
        assertTrue(communities.exists(c1.getName()));
        assertTrue(communities.exists(c2.getName()));
        assertEquals(2, communities.count());

        assertFalse(communities.exists("Does not exist"));

        // Test equals()
        final Community c11 = communities.getCommunityByName("Betkup");
        final Community c22 = communities.getCommunityByName("Another one");
        assertTrue(c11.equals(c1));
        assertTrue(c22.equals(c2));

        /*
         * // Test delete final Member m1 = new
         * MemberImpl("juliena@sofungaming.com", MemberAccountStatus.CREATED,
         * MemberAccountType.SIMPLE);
         * 
         * 
         * final Member m2 = new MemberImpl("julienp@sofungaming.com",
         * MemberAccountStatus.CREATED, MemberAccountType.SIMPLE);
         * 
         * c1.addMember(m1); c1.addMember(m2);
         * 
         * c2.addMember(m2);
         * 
         * assertEquals(2, c1.getMembers().size()); assertEquals(1,
         * c2.getMembers().size());
         * 
         * Team default2 = c2.getDefaultTeam();
         * assertNotNull(teams.getTeam(default2.getId()));
         * 
         * communities.delCommunity("Another one");
         * assertFalse(communities.exists(c2.getName())); assertEquals(1,
         * communities.count());
         * 
         * // Test actual members in C1 are still here despite the fact we
         * deleted // an entire community. assertEquals(2,
         * c1.getMembers().size());
         * 
         * c1 = communities.getCommunityByName("Betkup"); assertEquals(2,
         * c1.getMembers().size()); assertEquals(1, communities.count());
         * 
         * assertNull(teams.getTeam(default2.getId()));
         * 
         * Team default1 = c1.getDefaultTeam();
         * assertNotNull(teams.getTeam(default1.getId()));
         * 
         * communities.delCommunity(c1);
         * assertFalse(communities.exists(c1.getName())); assertEquals(0,
         * communities.count());
         * 
         * assertNull(teams.getTeam(default1.getId()));
         */

    }

}
