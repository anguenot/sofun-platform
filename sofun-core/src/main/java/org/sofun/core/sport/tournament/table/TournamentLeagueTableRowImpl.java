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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.sport.SportContestantImpl;

/**
 * Tournament league table row.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_rows")
public class TournamentLeagueTableRowImpl implements TournamentLeagueTableRow {

    private static final long serialVersionUID = 915535010119330208L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @OneToMany(
            targetEntity = TournamentLeagueTableColumnImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "row")
    protected List<TournamentLeagueTableColumn> columns;

    @ManyToOne(
            targetEntity = SportContestantImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "contestant_id")
    protected SportContestant contestant;

    @ManyToOne(
            targetEntity = TournamentLeagueTableImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected TournamentLeagueTable table;

    public TournamentLeagueTableRowImpl() {
        super();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public SportContestant getSportContestant() {
        return contestant;
    }

    @Override
    public void setSportContestant(SportContestant contestants) {
        this.contestant = contestants;
    }

    @Override
    public TournamentLeagueTable getLeagueTable() {
        return table;
    }

    @Override
    public void setLeagueTable(TournamentLeagueTable table) {
        this.table = table;
    }

    @Override
    public List<TournamentLeagueTableColumn> getColumns() {
        if (columns == null) {
            columns = new ArrayList<TournamentLeagueTableColumn>();
        }
        return columns;
    }

    @Override
    public void setColumns(List<TournamentLeagueTableColumn> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TournamentLeagueTableRow) {
            TournamentLeagueTableRow k = (TournamentLeagueTableRow) obj;
            if (k.getSportContestant() != null && getSportContestant() != null) {
                return k.getSportContestant().getUUID().equals(getSportContestant().getUUID()) ? true
                        : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public TournamentLeagueTableColumn getColumn(String key) {
        TournamentLeagueTableColumn c = null;
        for (TournamentLeagueTableColumn each : getColumns()) {
            if (key.equals(each.getColumnKey())) {
                c = each;
                break;
            }
        }
        return c;
    }

    @Override
    public void addColumn(TournamentLeagueTableColumn column) {
        if (!getColumns().contains(column)) {
            columns.add(column);
        }

    }

}
