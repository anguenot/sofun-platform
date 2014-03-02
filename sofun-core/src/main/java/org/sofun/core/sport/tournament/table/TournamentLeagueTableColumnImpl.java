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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;

/**
 * Tournament league table column.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_columns")
public class TournamentLeagueTableColumnImpl implements
        TournamentLeagueTableColumn {

    private static final long serialVersionUID = 7140474805834784594L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @Column(name = "key", unique = false, nullable = false)
    protected String key;

    @Column(name = "value", unique = false, nullable = true)
    protected String value;

    @ManyToOne(
            targetEntity = TournamentLeagueTableRowImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "row_id")
    protected TournamentLeagueTableRow row;

    public TournamentLeagueTableColumnImpl() {
        super();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getColumnKey() {
        return key;
    }

    @Override
    public void setColumnKey(String key) {
        this.key = key;
    }

    @Override
    public String getColumnValue() {
        return value;
    }

    @Override
    public void setColumnValue(String value) {
        this.value = value;
    }

    @Override
    public TournamentLeagueTableRow getRow() {
        return row;
    }

    @Override
    public void setRow(TournamentLeagueTableRow row) {
        this.row = row;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TournamentLeagueTableColumn) {
            TournamentLeagueTableColumn c = (TournamentLeagueTableColumn) obj;
            if (c.getColumnKey() != null && getColumnKey() != null) {
                return c.getColumnKey().equals(getColumnKey()) ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public int compareTo(TournamentLeagueTableColumn o) {

        if (o == null) {
            return 1;
        }

        String v = getColumnValue();
        String ov = o.getColumnValue();

        try {
            return Long.valueOf(v).compareTo(Long.valueOf(ov));
        } catch (NumberFormatException e) {
            return v.compareTo(ov);
        }

    }
}
