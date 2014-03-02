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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.community.table.MemberRankingTableEntryImpl;
import org.sofun.core.community.table.MemberRankingTableEntrySortByValueDesc;
import org.sofun.core.community.table.MemberRankingTableImpl;
import org.sofun.core.member.MemberImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTeamRankingTableEntry extends SofunCoreTestCase {

    public TestTeamRankingTableEntry(String testName) {
        super(testName);
    }

    @Test
    public void testComparison() throws CoreException {

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);
        Member m3 = new MemberImpl("julien@anguenot.org", null, null);

        MemberRankingTableEntry e1 = new MemberRankingTableEntryImpl(m1);
        MemberRankingTableEntry e2 = new MemberRankingTableEntryImpl(m2);
        MemberRankingTableEntry e3 = new MemberRankingTableEntryImpl(m3);
        
        assertEquals(0, e1.compareTo(e3));
        assertEquals(0, e3.compareTo(e1));
        
        assertEquals(0, e1.compareTo(e2));
        assertEquals(0, e2.compareTo(e1));
        
        assertEquals(0, e3.compareTo(e2));
        assertEquals(0, e2.compareTo(e3));
        
        e1.setValue(3);
        e2.setValue(1);
        e3.setValue(2);
        
        assertEquals(1, e1.compareTo(e3));
        assertEquals(-1, e3.compareTo(e1));
        
        assertEquals(2, e1.compareTo(e2));
        assertEquals(-2, e2.compareTo(e1));
        
        assertEquals(1, e3.compareTo(e2));
        assertEquals(-1, e2.compareTo(e3));
        
        assertEquals(1, e1.compareTo(null));
        
    }

    @Test
    public void testComparable() throws CoreException {
        
        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);
        Member m3 = new MemberImpl("julien@anguenot.org", null, null);

        MemberRankingTableEntry e1 = new MemberRankingTableEntryImpl(m1);
        MemberRankingTableEntry e2 = new MemberRankingTableEntryImpl(m2);
        MemberRankingTableEntry e3 = new MemberRankingTableEntryImpl(m3);
        
        List<MemberRankingTableEntry> entries = new ArrayList<MemberRankingTableEntry>();
        entries.add(e1);
        entries.add(e2);
        entries.add(e3);
        
        assertEquals(e1, entries.get(0));
        assertEquals(e2, entries.get(1));
        assertEquals(e3, entries.get(2));
        
        e1.setValue(3);
        e2.setValue(1);
        e3.setValue(2);
        
        assertEquals(e1, entries.get(0));
        assertEquals(e2, entries.get(1));
        assertEquals(e3, entries.get(2));
        
        Collections.sort(entries, new MemberRankingTableEntrySortByValueDesc());
        
        assertEquals(e1, entries.get(0));
        assertEquals(e3, entries.get(1));
        assertEquals(e2, entries.get(2));
        
    }
    
    @Test
    public void xtestEquals() throws CoreException {
        
        MemberRankingTable table = new MemberRankingTableImpl();
        
        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);
        Member m3 = new MemberImpl("julien@anguenot.org", null, null);
        
        MemberRankingTableEntry e1 = new MemberRankingTableEntryImpl(m1);
        e1.setRankingTable(table);
        table.addEntry(e1);
        MemberRankingTableEntry e2 = new MemberRankingTableEntryImpl(m2);
        e2.setRankingTable(table);
        e2.setRankingTable(table);
        MemberRankingTableEntry e3 = new MemberRankingTableEntryImpl(m3);
        e3.setRankingTable(table);
        e3.setRankingTable(table);
        
        assertTrue(e1.equals(e3));
        assertTrue(e3.equals(e1));
        
        assertFalse(e1.equals(e2));
        assertFalse(e2.equals(e1));
        
        assertFalse(e3.equals(e2));
        assertFalse(e2.equals(e3));
        
        em.persist(table);
        
        em.persist(m1);
        em.persist(m2);
        
        em.persist(e1);
        em.persist(e2);
          
        em.flush();
        
        // Persisted - now with identifiers.
        
        
        
        em.refresh(e1);
        em.refresh(e2);
        
        // No id for e3
        assertTrue(e1.equals(e3));
        assertTrue(e3.equals(e1));
        
        assertFalse(e1.equals(e2));
        assertFalse(e2.equals(e1));
        
        assertFalse(e3.equals(e2));
        assertFalse(e2.equals(e3));
        
        e2.setMember(e1.getMember());
        
        // Different ids.
       
        assertFalse(e1.equals(e2));
        assertFalse(e2.equals(e1));
        
        // Other cases
        assertFalse(e1.equals(null));
        assertTrue(e1.equals(e1));
        assertFalse(e1.equals(m1));
        
        
    }

}
