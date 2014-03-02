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

package org.sofun.core.feed;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.sofun.core.CoreConstants;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.feed.FeedEntry;
import org.sofun.core.api.feed.FeedEntryType;
import org.sofun.core.api.feed.FeedService;
import org.sofun.core.api.local.FeedServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.remote.FeedServiceRemote;
import org.sofun.core.api.team.Team;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(FeedServiceLocal.class)
@Remote(FeedServiceRemote.class)
public class FeedServiceImpl implements FeedService {

    // private static final Log log = LogFactory.getLog(FeedServiceImpl.class);

    private static final long serialVersionUID = 2401967833627695424L;

    @EJB(beanName = "FeedServiceImpl", beanInterface = FeedServiceLocal.class)
    protected FeedService feedService;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    private static final String TEAM_CREATED_MSG = "event_feed_team_create";

    private static final String NEW_TEAM_MEMBER = "event_feed_team_joined";

    private static final String NEW_PREDICTION = "event_feed_member_predicted";

    private static final String MEMBER_LOGGED_IN = "event_feed_member_logged_in";

    private static final String NEW_COMMUNITY_MEMBER = "event_feed_member_joined_community";

    private static Date getTime() {
        return Calendar.getInstance().getTime();
    }

    private FeedEntry createFeedEntry(Team team, Member member, String type) {
        FeedEntry entry = new FeedEntryImpl();
        entry.setDate(getTime());
        return entry;
    }

    @Override
    public void addFeedEntry(Team team, Member member, String type,
            String content) {
        Feed feed = team.getActivityFeed();
        FeedEntry entry = createFeedEntry(team, member, type);
        entry.setContent(content);
        if (member != null) {
            entry.setMember(member);
        }
        feed.addFeedEntry(entry);
        entry.setFeed(feed);
        em.merge(feed);
    }

    @Override
    public String getStringFor(String entry_type) {
        if (FeedEntryType.NEW_TEAM.equals(entry_type)) {
            return TEAM_CREATED_MSG;
        } else if (FeedEntryType.NEW_PREDICTION.equals(entry_type)) {
            return NEW_PREDICTION;
        } else if (FeedEntryType.NEW_TEAM_MEMBER.equals(entry_type)) {
            return NEW_TEAM_MEMBER;
        } else if (FeedEntryType.MEMBER_LOGGED_IN.equals(entry_type)) {
            return MEMBER_LOGGED_IN;
        } else if (FeedEntryType.NEW_COMMUNITY_MEMBER.equals(entry_type)) {
            return NEW_COMMUNITY_MEMBER;
        } else {
            return null;
        }
    }

}
