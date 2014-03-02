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

import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;

/**
 * Tournament league table key.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_league_tables_keys")
public class TournamentLeagueTableKeyImpl implements TournamentLeagueTableKey {

    private static final long serialVersionUID = 5664870548882532892L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @Column(name = "key", unique = false, nullable = false)
    protected String key;

    @Column(name = "name", unique = false, nullable = false)
    @JoinColumn(name = "name")
    protected String columnName;

    @ManyToOne(
            targetEntity = TournamentLeagueTableImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected TournamentLeagueTable table;

    public TournamentLeagueTableKeyImpl() {
        super();
    }

    public TournamentLeagueTableKeyImpl(String key, String columnName) {
        setKey(key);
        setColumnName(columnName);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public void setColumnName(String columnName) {
        this.columnName = columnName;

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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TournamentLeagueTableKey) {
            TournamentLeagueTableKey k = (TournamentLeagueTableKey) obj;
            if (k.getKey() != null && getKey() != null) {
                return k.getKey().equals(getKey()) ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

}
