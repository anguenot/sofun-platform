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

import java.util.Set;

import org.sofun.core.api.community.BaseCommunity;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.table.TeamRankingTable;

/**
 * Team / (a.k.a.) Room.
 * 
 * <p>
 * 
 * A {@link Team} is a sub-set of {@link Community} {@link Member}s. A
 * {@link Team} is always, and most of the time, part of at least one (1)
 * {@link Community}.
 * 
 * <p>
 * 
 * A {@link Team} can have access restrictions (@see {@link TeamPrivacy}) and
 * like a community has its own administrators.
 * 
 * <p>
 * 
 * A {@link Team} provides a list of {@link Kup} where {@link Member} can place
 * their bets and predictions.
 * 
 * <p>
 * 
 * A {@link Team} might be known as a `Room` in Betkup and other clients
 * application
 * 
 * @see {@link Community}
 * @see {@link TeamType}
 * @see {@link TeamTag}
 * @see {@link TeamPrivacy}
 * @see {@link Member}
 * @see {@link Kup}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Team extends BaseCommunity {

    /**
     * Add a given {@link Kup} to this {@link Team}.
     * 
     * @param kup a {@link Kup} instance.
     * @throws CoreException
     */
    void addKup(Kup kup) throws CoreException;

    /**
     * Remove a given {@link Kup} from this {@link Team}.
     * 
     * <p>
     * 
     * It does not delete the {@link Kup} instance but just unregister the
     * actual {@link Kup} from this {@link Team}.
     * 
     * @param kup: a {@link Kup} instance.
     * @throws CoreException
     */
    void delKup(Kup kup) throws CoreException;

    /**
     * Returns the list of {@link Community} this {@link Team} belongs to.
     * 
     * <p>
     * 
     * A {@link Team} is at least, and most of the time, in a single community.
     * 
     * @return: a {@link Set} of {@link Community} instance.
     */
    Set<Community> getCommunities();

    /**
     * Returns a {@link Kup} given its identifier if it exists within this
     * {@link Team}.
     * 
     * @param kupId: a Kup identifier
     * @return a {@link Kup} instance.
     */
    Kup getKupById(long kupId);

    /**
     * Returns a {@link Kup} given its name if it exists within this
     * {@link Team}.
     * 
     * @param kupName: a Kup identifier
     * @return a {@link Kup} instance.
     */
    Kup getKupByName(String name);

    /**
     * Returns the list of {@link Kup} linked to this {@link Team}
     * 
     * @return an {@link Set} of {@link Kup} instances.
     */
    Set<Kup> getKups();

    /**
     * Returns the {@link Team} privacy
     * 
     * @see {@link TeamPrivacy}
     * 
     * @return a {@link String} representing the privacy.
     */
    String getPrivacy();

    /**
     * Returns the team ranking table.
     * 
     * @return: a {@link TeamRankingTable} instance.
     */
    TeamRankingTable getTeamRanking();

    /**
     * Sets the list of {@link Community} the {@link Team} belongs to.
     * 
     * @param communities: a {@link Set} of {@link Community} instance.
     */
    void setCommunities(Set<Community> communities);

    /**
     * Adds a {@link Community}.
     * 
     * @param community: a {@link Community} instance.
     */
    void addCommmunity(Community community);

    /**
     * Delete relationship from to a given {@link Community}
     * 
     * @param community: a {@link Community} instance.
     */
    void delCommunity(Community community);

    /**
     * Sets the {@link Team} {@link KupPrivacyType}
     * 
     * @param privacyType: a {@link String}
     */
    void setPrivacy(String privacy);

    /**
     * Sets the team ranking table.
     * 
     * @param table: a {@link TeamRankingTable} instance.
     */
    void setTeamRanking(TeamRankingTable table);

    /**
     * Returns the password needed to access the {@link Team} if the team is
     * private and password protected.
     * 
     * @return a SHA-256 hash.
     */
    String getPassword();

    /**
     * Returns the password needed to access the {@link Team} if the team is
     * private and password protected.
     * 
     * @param: password a SHA-256 hash.
     */
    void setPassword(String password) throws CoreException;

    /**
     * Resets the password needed to access the {@link Team} if the team is
     * private and password protected.
     * 
     * <p/>
     * 
     * It will reset the password, generate a new one, store the hash digest and
     * return the clear text password.
     * 
     * @return: a pair clear / hash passwords.
     */
    String resetPassword() throws CoreException;

    /**
     * Verify the team password
     * 
     * @param password: a string clear text password
     * @return true if password matches or false if not.
     */
    boolean verifyPassword(String password) throws CoreException;

    /**
     * Returns the Team avatar
     * 
     * @return: a path
     */
    String getAvatar();

    /**
     * Sets the team avatar
     * 
     * @param avatar: a path
     */
    void setAvatar(String avatar);

    /**
     * Returns the team tags.
     * 
     * <p/>
     * 
     * Tags are free: admin can set them freely.
     * 
     * @return a set of {@link TeamTag} instances.
     */
    Set<TeamTag> getTags();

    /**
     * Sets the team tags.
     * 
     * <p/>
     * 
     * Tags are free: admin can set them freely.
     * 
     * @param tags: a set of {@link TeamTag} instances.
     */
    void setTags(Set<TeamTag> tags);

    /**
     * Add a team tag.
     * 
     * Tags are free: admin can set them freely.
     * 
     * @param tag: a {@link TeamTag} instance.
     */
    void addTag(TeamTag tag);

    /**
     * Delete a team tag.
     * 
     * Tags are free: admin can set them freely.
     * 
     * @param tag: a {@link TeamTag} instance.
     */
    void delTag(TeamTag tag);

    /**
     * Returns the team types.
     * 
     * <p/>
     * 
     * Admin can set them from a list of predefined types.
     * 
     * @return a set of {@link TeamType} instances.
     */
    Set<TeamType> getTypes();

    /**
     * Sets the team types.
     * 
     * <p/>
     * 
     * Admin can set them from a list of predefined types.
     * 
     * @param types: a set of {@link TeamType} instances.
     */
    void setTypes(Set<TeamType> types);

    /**
     * Add a team type.
     * 
     * Admin can set them from a list of predefined types.
     * 
     * @param type: a {@link TeamType} instance.
     */
    void addType(TeamType type);

    /**
     * Delete a team type.
     * 
     * Admin can set them from a list of predefined types.
     * 
     * @param type: a {@link TeamType} instance.
     */
    void delType(TeamType type);

    /**
     * Does this team hold a member ranking table?
     * 
     * <p>
     * 
     * Most of them do but some may use the community ranking table per
     * configuration.
     * 
     * @return true or false
     */
    boolean hasRankingTable();

    /**
     * Returns the number of members.
     * 
     * <p>
     * 
     * Used as a search sorting index.
     * 
     */
    int getNbMembers();

    /**
     * Sets the number of members.
     * 
     * <p>
     * 
     * Used as a search sorting index.
     * 
     * @param nbMembers: positive integer
     */
    void setNbMembers(int nbMembers);

}
