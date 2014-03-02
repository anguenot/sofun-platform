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

package org.sofun.platform.web.rest.api.team;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.team.table.TeamRankingTable;
import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * Team ranking table Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTMemberRankingTable {

    private static final Log log = LogFactory
            .getLog(ReSTMemberRankingTable.class);

    protected List<ReSTMemberRankingTableEntry> entries = new ArrayList<ReSTMemberRankingTableEntry>();

    protected int totalMembers = 0;

    protected int memberPosition = 0;

    protected int totalPoints = 0;

    protected int totalFriends = 0;

    protected int friendsMemberPosition = 0;

    protected Date lastUpdated;

    protected long entriesTotalPoints = 0;

    public ReSTMemberRankingTable() {
    }

    public ReSTMemberRankingTable(MemberRankingTable coreTable, int offset,
            int batchSize, ReSTMember member) {
        this(coreTable, offset, batchSize, member, false, null);
    }

    public ReSTMemberRankingTable(MemberRankingTable coreTable) {
        this(coreTable, 0, 0);
    }

    public ReSTMemberRankingTable(MemberRankingTable coreTable, int offset,
            int batchSize) {
        this(coreTable, offset, batchSize, null);
    }

    public ReSTMemberRankingTable(MemberRankingTable coreTable, int offset,
            int batchSize, ReSTMember member, Boolean friendsOnly,
            List<ReSTMember> filterByMembers) {

        this();

        setEntriesTotalPoints(coreTable.getEntriesTotalPoints());

        String memberEmail = null;
        List<ReSTMember> friends = new ArrayList<ReSTMember>();
        if (member != null) {
            memberEmail = member.getEmail();
            friends = filterFriendsFor(member.getFriends(), coreTable);
            totalFriends = friends.size() + 1;
        }

        List<MemberRankingTableEntry> slice = new ArrayList<MemberRankingTableEntry>();
        List<MemberRankingTableEntry> coreEntries = new ArrayList<MemberRankingTableEntry>(
                coreTable.getEntries());
        totalMembers = coreEntries.size();
        int totalSize = totalMembers;
        if (friendsOnly) {
            coreEntries = filteredEntries(coreEntries, friends, member);
            totalSize = totalFriends;
        } else if (filterByMembers != null) {
            coreEntries = filteredEntries(coreEntries, filterByMembers, member);
            totalSize = filterByMembers.size();
        }

        if (batchSize == 0) {
            batchSize = totalSize;
        }

        try {
            if (totalSize < offset) {
                slice = coreEntries;
            } else if (totalSize < (offset + batchSize)) {

                slice = coreEntries.subList(offset, totalSize);
            } else {
                slice = coreEntries.subList(offset, offset + batchSize);
            }
        } catch (IndexOutOfBoundsException e) {
            slice = coreEntries;
            String logMessage = "Index out of bounds while computing ranking "
                    + "table for member with email=";
            if (memberEmail != null) {
                logMessage += memberEmail;
            } else {
                logMessage += "UNKNOWN";
            }
            log.error(logMessage);
        }

        ListIterator<MemberRankingTableEntry> sliceIt = slice.listIterator();
        int i = 0;
        while (sliceIt.hasNext()) {
            i++;
            MemberRankingTableEntry entry = sliceIt.next();
            ReSTMemberRankingTableEntry rEntry = new ReSTMemberRankingTableEntry(
                    entry);
            rEntry.setPosition(coreEntries.indexOf(entry) + 1);
            entries.add(rEntry);
            if (friendsOnly && entry.getMember().getEmail().equals(memberEmail)) {
                friendsMemberPosition = i;
                totalPoints = entry.getValue();

            }
        }

        // If current member outside batch: we add it on top and get the
        // relative position if we compute friends.
        if (memberEmail != null && (!friendsOnly && memberPosition < i - 1)
                || (friendsOnly && friendsMemberPosition < i)) {
            ListIterator<MemberRankingTableEntry> tail = coreEntries
                    .listIterator(i - 1);
            while (tail.hasNext()) {
                MemberRankingTableEntry entry = tail.next();
                if (entry.getMember().getEmail().equals(memberEmail)) {
                    memberPosition = coreEntries.indexOf(entry) + 1;
                    totalPoints = entry.getValue();
                    if (friendsOnly) {
                        friendsMemberPosition = i - 1;
                    }
                    if (offset == 0) {
                        ReSTMemberRankingTableEntry rEntry = new ReSTMemberRankingTableEntry(
                                entry);
                        rEntry.setPosition(coreEntries.indexOf(entry) + 1);
                        entries.add(0, rEntry);
                        break;
                    }
                }
            }
        }

        this.setLastUpdated(coreTable.getLastModified());

    }

    private List<MemberRankingTableEntry> filteredEntries(
            List<MemberRankingTableEntry> entries, List<ReSTMember> friends,
            ReSTMember member) {
        List<MemberRankingTableEntry> filteredEntries = new ArrayList<MemberRankingTableEntry>();
        Iterator<MemberRankingTableEntry> it = entries.listIterator();
        int i = 0;
        while (it.hasNext()) {
            i++;
            MemberRankingTableEntry entry = it.next();
            if (member != null
                    && entry.getMember().getEmail().equals(member.getEmail())) {
                memberPosition = i;
                totalPoints = entry.getValue();
                filteredEntries.add(entry);
            } else if (friends.contains(new ReSTMember(entry.getMember()))
                    || (member != null && entry.getMember().getEmail()
                            .equals(member.getEmail()))) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }

    private List<ReSTMember> filterFriendsFor(List<ReSTMember> members,
            MemberRankingTable table) {

        List<ReSTMember> filtered = new ArrayList<ReSTMember>();

        if (table instanceof TeamRankingTable) {
            ReSTTeam team = new ReSTTeam(((TeamRankingTable) table).getTeam());
            for (ReSTMember member : members) {
                if (team.isMember(member)) {
                    filtered.add(member);
                }
            }
        } else if (table instanceof KupRankingTable) {
            Kup kup = ((KupRankingTable) table).getKup();
            for (ReSTMember member : members) {
                if (kup.getParticipants().contains(member)) {
                    filtered.add(member);
                }
            }
        }

        return filtered;

    }

    public List<ReSTMemberRankingTableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ReSTMemberRankingTableEntry> entries) {
        this.entries = entries;
    }

    public Date getLastUpdated() {
        if (lastUpdated == null) {
            return null;
        }
        return (Date) lastUpdated.clone();
    }

    public void setLastUpdated(Date lastUpdated) {
        if (lastUpdated != null) {
            this.lastUpdated = (Date) lastUpdated.clone();
        }
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public int getMemberPosition() {
        return memberPosition;
    }

    public void setMemberPosition(int memberPostion) {
        this.memberPosition = memberPostion;
    }

    public int getTotalFriends() {
        return totalFriends;
    }

    public void setTotalFriends(int totalFriends) {
        this.totalFriends = totalFriends;
    }

    public int getFriendsMemberPosition() {
        return friendsMemberPosition;
    }

    public void setFriendsMemberPosition(int friendsMemberPosition) {
        this.friendsMemberPosition = friendsMemberPosition;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public ReSTMemberRankingTableEntry getEntryFor(String email) {
        for (ReSTMemberRankingTableEntry entry : getEntries()) {
            if (entry.getMember().getEmail().equals(email)) {
                return entry;
            }
        }
        return null;
    }

    public long getEntriesTotalPoints() {
        return entriesTotalPoints;
    }

    public void setEntriesTotalPoints(long entriesTotalPoints) {
        this.entriesTotalPoints = entriesTotalPoints;
    }

}
