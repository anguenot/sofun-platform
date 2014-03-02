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

package org.sofun.core.community.table;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.community.table.MemberRankingTableEntryStats;
import org.sofun.core.api.member.Member;
import org.sofun.core.member.MemberImpl;

/**
 * Member Ranking table Entry Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_ranking_tables_entries")
@Inheritance(strategy = InheritanceType.JOINED)
public class MemberRankingTableEntryImpl implements MemberRankingTableEntry {

    private static final long serialVersionUID = 6504412512865063708L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    protected Member member;

    @ManyToOne(targetEntity = MemberRankingTableImpl.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    protected MemberRankingTable table;

    @OneToOne(targetEntity = MemberRankingTableEntryStatsImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stats_id")
    protected MemberRankingTableEntryStats stats;

    @Column(name = "value", nullable = false, columnDefinition = "int default 0")
    protected int value = 0;

    @Column(name = "position", nullable = false, columnDefinition = "int default 0")
    protected int position = 0;

    @Column(name = "winnings", nullable = true, columnDefinition = "float default 0")
    protected float winnings = 0;

    @Column(name = "trend", nullable = true, columnDefinition = "int default 0")
    protected byte trend = 0;

    @Column(name = "correct_predictions", nullable = true)
    protected Integer correctPredictions = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "first_predictions")
    protected Date firstPredictions;

    @Column(name = "tiebreaker_offset", nullable = true, columnDefinition = "int default 0")
    protected Integer tiebreakerOffset = 0;

    public MemberRankingTableEntryImpl() {
        super();
    }

    public MemberRankingTableEntryImpl(Member member) {
        this();
        this.member = member;
    }

    public MemberRankingTableEntryImpl(MemberRankingTable table, Member member) {
        this(member);
        this.table = table;
    }

    @Override
    public int compareTo(MemberRankingTableEntry other) {
        if (other != null) {
            return getValue() - other.getValue();
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof MemberRankingTableEntry) {
            MemberRankingTableEntry t = (MemberRankingTableEntry) obj;
            if (t.getId() != 0 && getId() != 0) {
                return t.getId() == getId();
            } else if (t.getMember() != null && getMember() != null) {
                return t.getMember().equals(getMember());
            }
        }
        return super.equals(obj);
    }

    protected static Date getTime() {
        return Calendar.getInstance().getTime();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public MemberRankingTable getRankingTable() {
        return table;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getId()).hashCode();
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
        if (table != null) {
            table.setLastModified(getTime());
        }
    }

    @Override
    public void setRankingTable(MemberRankingTable table) {
        if (table != null) {
            this.table = table;
        }

    }

    @Override
    public MemberRankingTableEntryStats getStats() {
        if (stats == null) {
            // Lazy initialization
            this.stats = new MemberRankingTableEntryStatsImpl();
        }
        return stats;
    }

    @Override
    public void setStats(MemberRankingTableEntryStats stats) {
        this.stats = stats;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public float getWinnings() {
        return winnings;
    }

    @Override
    public void setWinnings(float winnings) {
        this.winnings = winnings;
    }

    @Override
    public Integer getCorrectPredictions() {
        return correctPredictions;
    }

    @Override
    public void setCorrectPredictions(int correctPredictions) {
        this.correctPredictions = new Integer(correctPredictions);
    }

    @Override
    public Date getFirstPredictions() {
        if (firstPredictions != null) {
            return (Date) firstPredictions.clone();
        }
        return null;
    }

    @Override
    public void setFirstPredictions(Date date) {
        if (date != null) {
            firstPredictions = (Date) date.clone();
        }

    }

    @Override
    public byte getTrend() {
        return trend;
    }

    @Override
    public void setTrend(byte trend) {
        this.trend = trend;
    }

    @Override
    public Integer getTiebreakerOffset() {
        return tiebreakerOffset;
    }

    @Override
    public void setTiebreakerOffset(int offset) {
        this.tiebreakerOffset = offset;
    }

}
