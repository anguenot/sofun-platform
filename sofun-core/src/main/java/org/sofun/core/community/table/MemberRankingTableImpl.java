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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.community.table.MemberRankingTable;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.member.Member;

/**
 * Member Ranking Table Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_ranking_tables")
@Inheritance(strategy = InheritanceType.JOINED)
public class MemberRankingTableImpl implements MemberRankingTable {

    private static final long serialVersionUID = 6971114391623803380L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @OneToMany(
            targetEntity = MemberRankingTableEntryImpl.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "table")
    // The `orderBy` clause below implements the rules. Do not change this
    // without a global audit.
    @OrderBy(
            value = "value DESC, correctPredictions DESC, tiebreakerOffset ASC, firstPredictions ASC")
    protected Set<MemberRankingTableEntry> entries;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified")
    protected Date modified;

    @Column(
            name = "final",
            nullable = false,
            columnDefinition = "boolean default false")
    protected boolean fin = false;

    @Column(
            name = "entries_total_points",
            nullable = false,
            columnDefinition = "int default 0")
    protected long entriesTotalPoints = 0;

    public MemberRankingTableImpl() {
        super();
    }

    protected static MemberRankingTableEntry createEntryFor(Member member) {
        return new MemberRankingTableEntryImpl(member);
    }

    protected static Date getTime() {
        return Calendar.getInstance().getTime();
    }

    protected MemberRankingTableEntry createEntryFor(MemberRankingTable table,
            Member member) {
        return new MemberRankingTableEntryImpl(table, member);
    }

    @Override
    public void addEntry(MemberRankingTableEntry entry) {
        doAddEntry(entry);
    }

    @Override
    public void addEntryForMember(Member member) {
        doAddEntry(createEntryFor(this, member));
    }

    protected void doAddEntry(MemberRankingTableEntry e) {
        getEntries().add(e);
        setLastModified(getTime());
    }

    @Override
    public Set<MemberRankingTableEntry> getEntries() {
        if (entries == null) {
            entries = new LinkedHashSet<MemberRankingTableEntry>();
        }
        return entries;
    }

    @Override
    public MemberRankingTableEntry getEntryForMember(Member member) {
        if (member != null) {
            final MemberRankingTableEntry e = createEntryFor(member);
            final List<MemberRankingTableEntry> l = new ArrayList<MemberRankingTableEntry>(
                    getEntries());
            if (l.contains(e)) {
                return l.get(l.indexOf(e));
            }
        }
        return null;
    }

    @Override
    public Date getLastModified() {
        if (modified == null) {
            return null;
        }
        return (Date) modified.clone();
    }

    @Override
    public int getPositionFor(Member member) {
        if (member != null) {
            final MemberRankingTableEntry e = createEntryFor(member);
            if (getEntries().contains(e)) {
                final List<MemberRankingTableEntry> l = new ArrayList<MemberRankingTableEntry>(
                        getEntries());
                return l.indexOf(e);
            }
        }
        return -1;
    }

    @Override
    public int getValueFor(Member member) {
        final MemberRankingTableEntry e = getEntryForMember(member);
        if (e != null) {
            return e.getValue();
        }
        return -1;
    }

    @PrePersist
    @PreUpdate
    public void onCreate() {
        setLastModified(getTime());
    }

    @Override
    public void setLastModified(Date modified) {
        if (modified != null) {
            this.modified = (Date) modified.clone();
        }
    }

    @Override
    public void setEntries(Set<MemberRankingTableEntry> entries) {
        this.entries = entries;
    }

    @Override
    public boolean isFinal() {
        return fin;
    }

    @Override
    public void setFinal(boolean fin) {
        this.fin = fin;
    }

    @Override
    public long getEntriesTotalPoints() {
        return entriesTotalPoints;
    }

    @Override
    public void setEntriesTotalPoints(long entriesTotalPoints) {
        this.entriesTotalPoints = entriesTotalPoints;
    }

}
