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

package org.sofun.core.sport.tournament.table;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;

/**
 * Tournament league table.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables")
@Inheritance(strategy = InheritanceType.JOINED)
public class TournamentLeagueTableImpl implements TournamentLeagueTable {

    private static final long serialVersionUID = 399276972471293766L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = true)
    protected long uuid;

    @Column(name = "name", unique = false, nullable = false)
    protected String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified")
    protected Date modified;

    @Column(name = "type", unique = false, nullable = false)
    protected String type;

    @OneToMany(targetEntity = TournamentLeagueTableKeyImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "table")
    protected List<TournamentLeagueTableKey> keys;

    @OneToMany(targetEntity = TournamentLeagueTableRowImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "table")
    protected List<TournamentLeagueTableRow> rows;

    public TournamentLeagueTableImpl() {
        super();
    }

    public TournamentLeagueTableImpl(String name) {
        this();
        this.name = name;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(long uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public Date getModified() {
        if (modified == null) {
            return null;
        }
        return (Date) modified.clone();
    }

    @Override
    public List<TournamentLeagueTableKey> getKeys() {
        if (keys == null) {
            keys = new ArrayList<TournamentLeagueTableKey>();
        }
        return keys;
    }

    @Override
    public void setKeys(List<TournamentLeagueTableKey> keys) {
        this.keys = keys;
    }

    @Override
    public List<TournamentLeagueTableRow> getRows() {
        if (rows == null) {
            rows = new ArrayList<TournamentLeagueTableRow>();
        }
        return rows;
    }

    @Override
    public void setRows(List<TournamentLeagueTableRow> rows) {
        this.rows = rows;
    }

    @Override
    public void setModified(Date modified) {
        if (modified != null) {
            this.modified = (Date) modified.clone();
        }
    }

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        Calendar now = Calendar.getInstance();
        modified = now.getTime();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void addRow(TournamentLeagueTableRow row) {
        if (!rows.contains(row)) {
            rows.add(row);
        }
    }

    @Override
    public TournamentLeagueTableRow getRowFor(SportContestant contestant) {
        TournamentLeagueTableRow r = null;
        for (TournamentLeagueTableRow each : getRows()) {
            if (each.getSportContestant().equals(contestant)) {
                r = each;
                break;
            }
        }
        return r;
    }

    @Override
    public List<SportContestant> getOrderedContestantsForKey(String key) {

        List<SportContestant> contestants = new ArrayList<SportContestant>();
        List<TournamentLeagueTableColumn> columns = new ArrayList<TournamentLeagueTableColumn>();

        for (TournamentLeagueTableRow row : getRows()) {
            TournamentLeagueTableColumn column = row.getColumn(key);
            if (column != null) {
                columns.add(column);
            }
        }

        Collections.sort(columns);

        for (TournamentLeagueTableColumn column : columns) {
            contestants.add(column.getRow().getSportContestant());
        }

        return contestants;
    }

}
