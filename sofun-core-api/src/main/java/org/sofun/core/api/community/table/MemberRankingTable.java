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

package org.sofun.core.api.community.table;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.table.TeamRankingTable;

/**
 * Member Ranking Table.
 * 
 * <p/>
 * 
 * A {@link MemberRankingTable} is a data structure holding
 * {@link MemberRankingTableEntry}s.
 * 
 * @see {@link TeamRankingTable}
 * @see {@link KupRankingTable}
 * @see {@link MemberRankingTableEntry}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberRankingTable extends Serializable {

    /**
     * Returns a {@link Set} of {@link MemberRankingTableEntry} instances.
     * 
     * @return a {@link Set} of {@link MemberRankingTableEntry} instances.
     */
    Set<MemberRankingTableEntry> getEntries();

    /**
     * Sets entries.
     * 
     * <p/>
     * 
     * This should only be used if sure the table is final to avoid write
     * conflicts.
     * 
     * @param entries: a {@link Set} of {@link MemberRankingTableEntry}
     *        instances.
     */
    void setEntries(Set<MemberRankingTableEntry> entries);

    /**
     * Adds a new {@link MemberRankingTableEntry} to the
     * {@link MemberRankingTable}.
     * 
     * <p/>
     * 
     * The entry will be inserted at the end of the set. If an entry exists for
     * this given member then this entry will <b>not</b> be inserted.
     * 
     * @param entry: a {@link MemberRankingTableEntry} instance.
     */
    void addEntry(MemberRankingTableEntry entry);

    /**
     * Creates and adds a {@link MemberRankingTableEntry} to the
     * {@link MemberRankingTable} given a {@link Member}
     * 
     * <p/>
     * 
     * The entry will be inserted at the end of the set. If an entry already
     * exists for the given member then this entry will <b>not</b> be inserted.
     * 
     * @param member : a {@link Member} instance.
     */
    void addEntryForMember(Member member);

    /**
     * Returns the corresponding {@link MemberRankingTableEntry} for a given
     * {@link Member} if it exists.
     * 
     * @param member: a {@link Member} instance.
     * @return a {@link MemberRankingTableEntry} or null if it does not exist.
     */
    MemberRankingTableEntry getEntryForMember(Member member);

    /**
     * Returns the position of a given {@link Member} in the
     * {@link TeamRankingTable} if it exists.
     * 
     * @param member: a {@link Member} instance.
     * @return an {@link Integer} or -1 if does not exist.
     */
    int getPositionFor(Member member);

    /**
     * Returns the value of a given {@link Member} in the
     * {@link TeamRankingTable} if exists.
     * 
     * <p/>
     * 
     * Typically, the `value` corresponds to an amount of points.
     * 
     * @param member: a {@link Member} instance.
     * @return an {@link Integer} or -1 if does not exist.
     */
    int getValueFor(Member member);

    /**
     * Returns the date at which the {@link TeamRankingTable} has been last
     * modified.
     * 
     * <p/>
     * 
     * It is when an actual insert, removal or update appends at last.
     * 
     * @return a {@link Date} in UTC.
     */
    Date getLastModified();

    /**
     * Sets the date at which the {@link TeamRankingTable} has been last
     * modified.
     * 
     * <p/>
     * 
     * It is when an actual insert, removal or update appends at last.
     * 
     * @return a {@link Date} in UTC.
     */
    void setLastModified(Date modified);

    /**
     * Is the table final ?
     * 
     * <p>
     * 
     * A table is final when the kups has been settled and the actual statistics
     * generated.
     * 
     * @return
     */
    boolean isFinal();

    /**
     * Set the table status.
     * 
     * <p>
     * 
     * A table is final when the kups has been settled and the actual statistics
     * generated.
     * 
     * @param fin: a boolean
     */
    void setFinal(boolean fin);

    /**
     * Get the sum of all entry points for this ranking
     * 
     * @return a {@link Long}
     */
    long getEntriesTotalPoints();

    /**
     * Set the total entry points for this ranking.
     * 
     * @param entriesTotalPoints: sum of all entry points.
     */
    void setEntriesTotalPoints(long entriesTotalPoints);

}
