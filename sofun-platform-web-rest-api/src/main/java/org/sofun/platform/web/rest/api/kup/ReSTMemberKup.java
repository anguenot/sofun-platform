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

package org.sofun.platform.web.rest.api.kup;

import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTMemberKup extends ReSTKup {

    private static final long serialVersionUID = 1L;

    protected ReSTMember member;

    protected int points;

    protected int ranking;

    protected int totalParticipants = 0;

    protected double winnings = 0;

    protected String teamName;

    protected long teamUUID;

    public ReSTMemberKup(Kup kup, Member member) {
        super(kup);
        if (member != null) {
            setMember(new ReSTMember(member));
            if (kup != null) {
                MemberRankingTableEntry entry = kup.getRankingTable()
                        .getEntryForMember(member);

                // Entry might be null in case of gambling Kups where player did
                // record a prediction but did not place bet.
                if (entry != null) {
                    setPoints(entry.getValue());
                    setRanking(entry.getPosition() + 1);
                    setWinnings(entry.getWinnings());
                }
                setTotalParticipants(kup.getParticipants().size());
                setTeamName(kup.getTeam().getName());
                setTeamUUID(kup.getTeam().getId());
            }
        }

    }

    public ReSTMember getMember() {
        return member;
    }

    public void setMember(ReSTMember member) {
        this.member = member;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public double getWinnings() {
        return winnings;
    }

    public void setWinnings(double winnings) {
        this.winnings = winnings;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getTeamUUID() {
        return teamUUID;
    }

    public void setTeamUUID(long teamUUID) {
        this.teamUUID = teamUUID;
    }

}
