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

package org.sofun.core.api.community;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.feed.Feed;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;

/**
 * Base Community interface.
 * 
 * <p/>
 * 
 * The {@link BaseCommunity} interface defines an abstraction around the
 * representation of a set of {@link Member}. It defines an API allowing to
 * manage users, administrators, security and {@link community} related
 * properties.
 * 
 * <p/>
 * 
 * @see {@link Community}
 * @see {@link Team}
 * @see {@link Kup}
 * @see {@link Member}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface BaseCommunity extends Serializable {

    /**
     * Returns the team internal identifier.
     * 
     * @return: a {@link Long}
     */
    long getId();

    /**
     * Returns the name of the community.
     * 
     * @return a {@link String}
     */
    String getName();

    /**
     * Sets the name of the community.
     * 
     * @param name: the name of the community
     */
    void setName(String name);

    /**
     * Returns the description of the community.
     * 
     * @return a {@link String}
     */
    String getDescription();

    /**
     * Sets the description of the community.
     * 
     * @param description: the description of the community
     */
    void setDescription(String description);

    /**
     * Returns the community creation date.
     * 
     * @return a {@link Date} instance in UTC
     */
    Date getCreated();

    /**
     * Sets the community creation date.
     * 
     * @param created: a {@link Date} in UTC
     */
    void setCreated(Date created);

    /**
     * Returns the list of all Members part of the Community.
     * 
     * We do not expect a huge # of administrators on a particular community: no
     * iterator provided to list the administrators.
     * 
     * @return a list of {@link Member}
     */
    Set<Member> getAdmins();

    /**
     * Add a new admin for this community.
     * 
     * @param member: a {@link Member} instance.
     */
    void addAdmin(Member admin);

    /**
     * Remove an admin from this community.
     * 
     * @param member: a {@link Member} instance.
     */
    void removeAdmin(Member admin);

    /**
     * Sets administrators.
     * 
     * @param admins: a {@link Set} of {@link Member} instances.
     */
    void setAdmins(Set<Member> admins);

    /**
     * Returns the list of all members.
     * 
     * @return a {@link List} of link Member}
     */
    Set<Member> getMembers();

    /**
     * Add a new {@link Community} {@link Member}.
     * 
     * @param member: a {@link Member} instance.
     * @return a new {@link Member} instance.
     */
    Member addMember(Member member);

    /**
     * Removes a {@link Member} from the {@link Community}.
     * 
     * @param member: a {@link Member} instance.
     */
    void removeMember(Member member);

    /**
     * Returns the # of {@link Member} in the community.
     * 
     * @return a non null integer
     */
    int countMembers();

    /**
     * Sets members.
     * 
     * @param members: a {@link Set} of {@link Member} instances.
     */
    void setMembers(Set<Member> members);

    /**
     * Returns the # of administrators in the community.
     * 
     * @return a non null integer
     */
    int countAdmins();

    /**
     * Is a given user a {@link Member} of this community?
     * 
     * @param member: a {@link Member}
     * @return true or false.
     */
    boolean isMember(Member member);

    /**
     * Is a given {@link Member} an administrator?
     * 
     * @param member: a {@link Member}
     * @return true or false
     */
    boolean isAdmin(Member member);

    /**
     * Returns the member ranking table.
     * 
     * @return: a {@link MemberRankingTable} instance.
     */
    MemberRankingTable getRankingTable();

    /**
     * Returns the corresponding activity feed.
     * 
     * @return: a {@link Feed} instance.
     */
    Feed getActivityFeed();

}
