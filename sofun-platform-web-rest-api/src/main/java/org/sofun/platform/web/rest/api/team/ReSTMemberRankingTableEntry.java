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

import java.util.Date;

import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.platform.web.rest.api.member.ReSTMember;

/**
 * Member Ranking Table Entry.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTMemberRankingTableEntry {

    protected ReSTMember member;

    protected int position;

    protected int value;

    protected float winnings;

    protected int correctPredictions = 0;

    protected Date firstPrediction = null;

    protected byte trend = 0;

    protected String tieBreakerQuestionAnswer;

    protected ReSTMemberRankingTableEntryStats stats;

    public ReSTMemberRankingTableEntry() {
    }

    public ReSTMemberRankingTableEntry(MemberRankingTableEntry coreEntry) {
        this();
        if (coreEntry != null) {
            setValue(coreEntry.getValue());
            setMember(new ReSTMember(coreEntry.getMember()));
            setWinnings(coreEntry.getWinnings());
            setStats(new ReSTMemberRankingTableEntryStats(coreEntry.getStats()));
            if (coreEntry.getCorrectPredictions() != null) {
                setCorrectPredictions(coreEntry.getCorrectPredictions());
            }
            if (coreEntry.getFirstPredictions() != null) {
                setFirstPrediction(coreEntry.getFirstPredictions());
            }
            setTrend(coreEntry.getTrend());
        }
    }

    public ReSTMember getMember() {
        return member;
    }

    public void setMember(ReSTMember member) {
        this.member = member;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ReSTMemberRankingTableEntryStats getStats() {
        return stats;
    }

    public void setStats(ReSTMemberRankingTableEntryStats stats) {
        this.stats = stats;
    }

    public float getWinnings() {
        return winnings;
    }

    public void setWinnings(float winnings) {
        this.winnings = winnings;
    }

    public int getCorrectPredictions() {
        return correctPredictions;
    }

    public void setCorrectPredictions(int correctPredictions) {
        this.correctPredictions = correctPredictions;
    }

    public Date getFirstPrediction() {
        return firstPrediction;
    }

    public void setFirstPrediction(Date firstPrediction) {
        this.firstPrediction = firstPrediction;
    }

    public byte getTrend() {
        return trend;
    }

    public void setTrend(byte trend) {
        this.trend = trend;
    }

    public String getTieBreakerQuestionAnswer() {
        return tieBreakerQuestionAnswer;
    }

    public void setTieBreakerQuestionAnswer(String tieBreakerQuestionAnswer) {
        this.tieBreakerQuestionAnswer = tieBreakerQuestionAnswer;
    }

}
