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

package org.test.sofun.core.team.table;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.sofun.core.CoreUtil;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.core.community.table.MemberRankingTableEntryImpl;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.team.TeamImpl;
import org.sofun.core.team.table.TeamRankingTableImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTeamRankingTable extends SofunCoreTestCase {

    public TestTeamRankingTable(String testName) {
        super(testName);
    }

    @Test
    public void testAddTeamRankingTableEntry() throws CoreException {

        Team t = new TeamImpl("test");

        assertEquals(t, ((TeamRankingTable) t.getRankingTable()).getTeam());

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);

        Member m3 = new MemberImpl("julien@anguenot.org", null, null);
        t.addMember(m1);

        assertEquals(1, t.getRankingTable().getEntries().size());

        // Ensure we don't add an entry for a membre twice.
        t.getRankingTable().addEntryForMember(m1);
        assertEquals(1, t.getRankingTable().getEntries().size());

        t.addMember(m3);
        assertEquals(1, t.countMembers());
        assertEquals(1, t.getRankingTable().getEntries().size());

        t.getRankingTable().addEntryForMember(m2);
        assertEquals(2, t.getRankingTable().getEntries().size());
        assertEquals(1, t.countMembers());

    }

    @Test
    public void testTeamRankingTableEntryLastUpdated() throws CoreException {

        TeamRankingTable table = new TeamRankingTableImpl();
        assertNull(table.getLastModified());

        table.setLastModified(null);
        assertNull(table.getLastModified());

        Date now = Calendar.getInstance().getTime();
        table.setLastModified(now);
        assertEquals(now, table.getLastModified());

        table.setLastModified(null);
        assertEquals(now, table.getLastModified());

    }

    @Test
    public void testTeamRankingTableEntryOnCreate() throws CoreException {

        TeamRankingTable table = new TeamRankingTableImpl();
        assertNull(table.getLastModified());

        em.persist(table);
        em.flush();

        assertNotNull(table.getLastModified());

    }

    @Test
    public void testTeamRankingTableEntryInitFromTeam() throws CoreException {

        Team t = new TeamImpl("test");

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);
        Member m3 = new MemberImpl("julien@anguenot.org", null, null);

        t.addMember(m1);
        t.addMember(m2);
        t.addMember(m3);

        assertEquals(2, t.countMembers());

        TeamRankingTable newTable = new TeamRankingTableImpl(t);
        assertEquals(2, newTable.getEntries().size());
        assertEquals(m1, newTable.getEntryForMember(m1).getMember());
        assertEquals(m2, newTable.getEntryForMember(m2).getMember());
        assertNull(null, newTable.getEntryForMember(null));

        assertEquals(0, newTable.getValueFor(m1));
        assertEquals(0, newTable.getValueFor(m2));

        // Same email means same user
        assertEquals(0, newTable.getValueFor(m3));

        Member m4 = new MemberImpl("otherdude@sofungaming.com", null, null);
        assertEquals(-1, newTable.getValueFor(m4));

        newTable.addEntry(new MemberRankingTableEntryImpl(newTable, m4));
        assertEquals(0, newTable.getValueFor(m4));
        assertEquals(2, newTable.getPositionFor(m4));

        assertEquals(-1, newTable.getPositionFor(null));

    }

    @Test
    public void testLastUpdatedAfterInsert() throws CoreException {

        Team t = new TeamImpl("test");

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);

        t.addMember(m1);
        t.addMember(m2);

        TeamRankingTable table = new TeamRankingTableImpl(t);

        Date lastModified = table.getLastModified();

        System.out.println(lastModified.toString());

        MemberRankingTableEntry e = table.getEntryForMember(m1);

        CoreUtil.waitFor(1000);

        e.setValue(10);

        System.out.println(e.getRankingTable().getLastModified().toString());

        assertEquals(table, e.getRankingTable());
        assertTrue(lastModified.before(e.getRankingTable().getLastModified()));
        assertTrue(e.getRankingTable().getLastModified().after(lastModified));

        assertEquals(table.getLastModified(),
                e.getRankingTable().getLastModified());

    }

    @Test
    public void testContainment() throws CoreException {

        Team t = new TeamImpl("test");

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);

        t.addMember(m1);
        t.addMember(m2);

        TeamRankingTable table = new TeamRankingTableImpl(t);

        assertNotNull(table.getEntryForMember(m1));
        assertNotNull(table.getEntryForMember(m2));

    }

}
