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

package org.test.sofun.core.feed;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.feed.FeedEntry;
import org.sofun.core.api.member.Member;
import org.sofun.core.community.CommunityImpl;
import org.sofun.core.feed.FeedEntryImpl;
import org.sofun.core.feed.FeedImpl;
import org.sofun.core.member.MemberImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestFeed extends SofunCoreTestCase {

    public TestFeed(String testName) {
        super(testName);
    }

    @Test
    public void testFeedPersistence() {
        Feed f = new FeedImpl();
        em.persist(f);
        em.flush();
    }

    @Test
    public void testFeedEntryPersistence() throws CoreException {

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        em.persist(m1);

        Feed feed = new FeedImpl();
        em.persist(feed);
        
        FeedEntry fe = new FeedEntryImpl();
        fe.setFeed(feed);
        fe.setMember(m1);
        em.persist(fe);
        em.flush();

        FeedEntry fe2 = new FeedEntryImpl();
        fe2.setContent("A new feed entry");
        fe2.setFeed(feed);
        fe2.setDate(new Date());
        fe2.setMember(m1);

        em.persist(fe2);
        em.flush();

    }

    @Test
    public void testFeedEntryComparison() throws CoreException {

        Calendar now = Calendar.getInstance();

        FeedEntry fe1 = new FeedEntryImpl();
        fe1.setDate(now.getTime());

        FeedEntry fe2 = new FeedEntryImpl();
        now.add(Calendar.HOUR, 1);
        fe2.setDate(now.getTime());

        assertEquals(1, fe2.compareTo(fe1));
        assertEquals(-1, fe1.compareTo(fe2));

        fe1.setDate(now.getTime());
        assertEquals(0, fe2.compareTo(fe1));
        assertEquals(0, fe1.compareTo(fe2));

    }

    @Test
    public void testCommunityFeed() throws CoreException {

        Member m1 = new MemberImpl("julien@anguenot.org", null, null);
        em.persist(m1);

        Member m2 = new MemberImpl("ja@sofungaming.com", null, null);
        em.persist(m2);

        Calendar now = Calendar.getInstance();

        FeedEntry fe1 = new FeedEntryImpl();
        fe1.setDate(now.getTime());
        fe1.setMember(m1);

        FeedEntry fe2 = new FeedEntryImpl();
        now.add(Calendar.HOUR, 1);
        fe2.setDate(now.getTime());
        fe1.setMember(m2);

        Community c = new CommunityImpl("junit");
       
        c.getActivityFeed().addFeedEntry(fe1);
        c.getActivityFeed().addFeedEntry(fe2);

        // Default event when default team is created would be added here.
        //em.persist(c);
        //em.flush();
        
        assertEquals(fe2, c.getActivityFeed().getBatchedFeedEntries(0, 10).get(0));
        assertEquals(fe1, c.getActivityFeed().getBatchedFeedEntries(0, 10).get(1));

        Iterator<FeedEntry> it = c.getActivityFeed().getFeedEntries();
        while (it.hasNext()) {
            assertNotNull(it.next());
        }

        //
        // Ensure default team gets events.
        //

        assertEquals(
                2,
                c.getDefaultTeam().getActivityFeed().getBatchedFeedEntries(0, 10).size());

        //assertEquals(1, m1.getBatchedFeedEntries(0, 10).size());
        //assertEquals(1, m1.getBatchedFeedEntries(0, 10).size());
    }

}
