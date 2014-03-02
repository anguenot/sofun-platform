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

package org.sofun.core.api.team;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.table.TeamRankingTable;

/**
 * Team (aka "room") service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 */
public interface TeamService extends Serializable {

    /**
     * Returns a {@link Team} by Id.
     * 
     * @param id: the team identifier
     * @return a {@link Team} instance.
     */
    Team getTeam(long id);

    /**
     * Returns a {@link Team} by name.
     * 
     * @param name: team's unique name.
     * @return a {@link Team} instance.
     */
    Team getTeam(String name);

    /**
     * Returns the number of teams created on the whole platform independely
     * from the communities.
     * 
     * @return the total of {@link Team}
     */
    long countTeams();

    Team createTeam(String name);

    Team createTeam(String name, boolean hasRankingtable);

    /**
     * Add a {@link Team}
     * 
     * @param team: a {@link Team} instance.
     * @return the newly created {@link Team}
     */
    Team addTeam(Team team);

    Team updateTeam(Team team);

    /**
     * Add a new {@link Team} giving its name.
     * 
     * @param name: {@link String} for the new {@link Team}
     * @return a {@link Team} instance.
     */
    Team addTeam(String name);

    /**
     * Removes a {@link Team}.
     * 
     * @param team: a {@link Team} instance.
     * @return a {@link Team} instance
     */
    Team removeTeam(Team team);

    /**
     * Removes a {@link Team}.
     * 
     * @param id: the team identifier
     * @return a {@link Team} instance.
     */
    Team removeTeamById(long id);

    /**
     * Does {@link Team} already exists?
     * 
     * @param team: a Team instance.
     * @return true or false.
     */
    boolean exists(Team team);

    TeamRankingTable getRankingTableFor(Team team);

    /**
     * Returns all team tags.
     * 
     * @param limit: maximum amount of tags to return.
     * 
     * @return a set of {@link TeamTag} instance.
     */
    Set<TeamTag> getTeamTags(int limit);

    /**
     * Returns a team tag.
     * 
     * @param name: tag name
     * @return a {@link TeamTag} instance or null if does not exist.
     */
    TeamTag getTeamTag(String name);

    /**
     * Returns all team types.
     * 
     * @return a set of team types.
     */
    Set<TeamType> getTeamTypes();

    /**
     * Returns a team type by name.
     * 
     * @param name: a team type name
     * @return a {@link TeamType} instance or null
     */
    TeamType getTeamType(String name);

    /**
     * Returns all team privacy types.
     * 
     * @return a {@link Set} of {@link String}
     */
    Set<String> getTeamPrivacyTypes();

    /**
     * Search teams given search parameters.
     * 
     * @param params: a map from string to {@link String} instances.
     * @return a {@link Set} of {@link Team}
     * @throws CoreException
     */
    Set<Team> searchTeams(Map<String, String> params) throws CoreException;

    /**
     * Count the number of results for a given search.
     * 
     * This is useful, since {@link TeamService#search(Map)} only provides paged
     * results.
     * 
     * @param params: a {@link Map} from param key to param value.
     * @return an {@link Integer} value
     * @throws CoreException
     */
    int countSearchTeams(Map<String, String> params) throws CoreException;

    /**
     * Returns the credentials for a Member in a given team.
     * 
     * 
     * @param team: a {@link Team} instance
     * @param member: a {@link Member} instance
     * @return a set of unique string
     */
    Set<String> getCredentials(Team team, Member member);

    /**
     * Member sends invitation.
     * 
     * @param team: a {@link Team} instance.
     * @param inviter: a {@link Member} instance. Member sending invites.
     * @param params: parameters including email recipients, etc.
     */
    void invite(Team team, Member inviter, Map<String, String> params)
            throws CoreException;

    /**
     * Returns a batch of {@link Kup}s registered against a given {@link Team}
     * 
     * @param team: a {@link Team} instance
     * @param offset: >= 0
     * @param size: >= 1
     * @return a {@link List} of {@link Kup} instance
     * @throws CoreException
     */
    List<Kup> getKupsFor(Team team, int offset, int size) throws CoreException;

    /**
     * Can a given {@link Team} be removed?
     * 
     * <p>
     * 
     * A {@link Team} can be removed if no Kups are currently opened with more
     * than 1 participant.
     * 
     * @param team: a {@link Team} instance
     * @return true or false
     */
    boolean isTeamRemovable(Team team);

    List<Team> getTeamsWithActiveKups();

}
