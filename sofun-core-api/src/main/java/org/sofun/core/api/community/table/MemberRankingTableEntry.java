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

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.team.Team;

/**
 * Member Ranking Table Entry
 * 
 * @see {@link MemberRankingTable}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberRankingTableEntry extends Serializable,
        Comparable<MemberRankingTableEntry> {

    /**
     * Returns the internal unique identifier.
     * 
     * @return a unique {@link Long}
     */
    long getId();

    /**
     * Returns the corresponding {@link Member}.
     * 
     * @return a {@link Member} instance.
     */
    Member getMember();

    /**
     * Sets the corresponding {@link Member}.
     * 
     * @param member: a {@link Member} instance.
     */
    void setMember(Member member);

    /**
     * Returns the corresponding stored value.
     * 
     * @return an {@link Integer}
     */
    int getValue();

    /**
     * Sets the corresponding value.
     * 
     * @param value: an {@link Integer}
     */
    void setValue(int value);

    /**
     * Returns the corresponding {@link MemberRankingTable}.
     * 
     * @return a {@link MemberRankingTable} or null if not bound to any.
     */
    MemberRankingTable getRankingTable();

    /**
     * Sets the corresponding {@link MemberRankingTable}.
     * 
     * @param table: a {@link MemberRankingTable}
     */
    void setRankingTable(MemberRankingTable table);

    /**
     * Returns the corresponding statistics.
     * 
     * <p/>
     * Typically, outline the corresponding {@link Member} performances in the
     * context in which this table applies. (i.e.: {@link Team} and {@link Kup})
     * 
     * @return a {@link MemberRankingTableEntryStats} instance or null if it
     *         does not exist.
     */
    MemberRankingTableEntryStats getStats();

    /**
     * Sets the corresponding statistics.
     * 
     * <p/>
     * Typically, outline the corresponding {@link Member} performances in the
     * context in which this table applies. (i.e.: {@link Team} and {@link Kup})
     * 
     * @param stats: a {@link MemberRankingTableEntryStats} instance
     */
    void setStats(MemberRankingTableEntryStats stats);

    /**
     * Returns the actual member position in the table.
     * 
     * @return an integer
     */
    int getPosition();

    /**
     * Sets the actual member position in the table.
     * 
     * <p/>
     * 
     * Useful when some members do have the same amount of points.
     * 
     * @param position: an integer.
     */
    void setPosition(int position);

    /**
     * Returns the winnings associated with this entry.
     * 
     * @return a value as a float number
     */
    float getWinnings();

    /**
     * Sets the winnings associated with this entry.
     * 
     * @param winnings: a value as a float number
     */
    void setWinnings(float winnings);

    /**
     * Returns the amount of correct predictions for this entry.
     * 
     * @return an integer >= 0 or null if not initialized
     */
    Integer getCorrectPredictions();

    /**
     * Sets the amount of correct predictions for this entry.
     * 
     * @param correctPredictions: an integer >= 0
     */
    void setCorrectPredictions(int correctPredictions);

    /**
     * Returns the date at which the first prediction has been recorded
     * 
     * @return a {@link Date} instance
     */
    Date getFirstPredictions();

    /**
     * Sets the date at which the first prediction has been recorded
     * 
     * @param date: a {@link Date} instance
     */
    void setFirstPredictions(Date date);

    /**
     * Returns the trend for this entry.
     * 
     * <ul>
     * <li>-1: down</li>
     * <li>0: stable</li>
     * </li>1: up</li>
     * </ul>
     * 
     * @return -1 / 0 / 1
     */
    byte getTrend();

    /**
     * Returns the trend for this entry.
     * 
     * <ul>
     * <li>-1: down</li>
     * <li>0: stable</li>
     * </li>1: up</li>
     * </ul>
     * 
     * @param trend: -1 / 0 / 1
     */
    void setTrend(byte trend);

    /**
     * Returns the offset in between the player answer and the tiebreaker
     * question(s).
     * 
     * <p>
     * 
     * This is used as a sorting index to retrieve sortable ranking table using
     * SQL directly.
     * 
     * @return an integer or null if none.
     */
    Integer getTiebreakerOffset();

    /**
     * Sets the offset in between the player answer and the tiebreaker
     * question(s).
     * 
     * <p>
     * 
     * This is used as a sorting index to retrieve sortable ranking table using
     * SQL directly.
     * 
     * @param an integer or null if none.
     */
    void setTiebreakerOffset(int offset);

}
