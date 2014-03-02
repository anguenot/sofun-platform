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

import java.util.Date;
import java.util.Set;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;

/**
 * Community interface.
 * 
 * <p>
 * 
 * A {@link Community} contains a set of {@link Member}. Members are part of
 * {@link Team} and Teams contains a set of {@link Kup}.
 * 
 * <p>
 * 
 * A Community has a default Team (invisible) that allows Members to place bets
 * without having to join any Teams initially.
 * 
 * <p>
 * 
 * A Community has 1 or several Administrator
 * 
 * @see {@link Team}
 * @see {@link Kup}
 * @see {@link Member}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Community extends BaseCommunity {

    /**
     * Returns the teams belonging to this {@link Community}.
     * 
     * @return a {@link Set} of {@link Team} instances.
     */
    Set<Team> getTeams();

    /**
     * Adds a team to this community.
     * 
     * @param team: a {@link Team} instance.
     */
    void addTeam(Team team);

    /**
     * Removes a team from this {@link Community}.
     * 
     * @param team: a {@link Team} instance.
     */
    void delTeam(Team team);

    /**
     * Returns the default {@link Team}.
     * 
     * <p/>
     * 
     * A default team includes all {@link Community} {@link Member}s
     * 
     * @return a {@link Team} instance.
     */
    Team getDefaultTeam();

    /**
     * Sets the default {@link Team}.
     * 
     * <p/>
     * 
     * A default team includes all {@link Community} {@link Member}s
     * 
     * @param team: a {@link Team} instance.
     */
    void setDefaultTeam(Team team);

    /**
     * Returns the creation date.
     * 
     * @return a {@link Date} instance in UTC
     */
    @Override
    Date getCreated();

    /**
     * Sets the creation date.
     * 
     * @param: a {@link Date} instance in UTC
     */
    @Override
    void setCreated(Date date);

    /**
     * Does the community only provides one ranking at community level (versus
     * Community + Teams)
     * 
     * @return true or false.
     */
    boolean isCommunityRankingOnly();

    /**
     * Does the community only provides one ranking at community level (versus
     * Community + Teams) ?
     * 
     * @param communityRankingOnly: true or false.
     */
    void setCommunityRankingOnly(boolean communityRankingOnly);

    /**
     * Does the community provides one or several community activity feed.
     * 
     * @return true or false.
     */
    boolean isCommunityFeedOnly();

    /**
     * Sets the community only provides one ranking at community level (versus
     * Community + Teams) ?
     * 
     * @param communityFeedOnly: true or false.
     */
    void setCommunityFeedOnly(boolean communityFeedOnly);

}
