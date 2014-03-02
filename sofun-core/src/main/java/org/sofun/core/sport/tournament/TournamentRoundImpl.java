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

package org.sofun.core.sport.tournament;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.core.sport.tournament.table.TournamentRoundLeagueTableImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_rounds")
public class TournamentRoundImpl implements TournamentRound {

    private static final long serialVersionUID = 5591273783146467985L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = false)
    protected long uuid;

    @Column(name = "round_number", unique = false, nullable = false)
    protected int roundNumber;

    @Column(name = "round_label", unique = false, nullable = true)
    protected String roundLabel;

    @Column(name = "name", unique = false, nullable = true)
    protected String name;

    @Column(name = "description", unique = false, nullable = true)
    protected String description;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date endDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "sports_tournaments_rounds_properties", joinColumns = @JoinColumn(name = "id"))
    protected Map<String, String> properties = new HashMap<String, String>();

    @OneToMany(targetEntity = TournamentGameImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "round")
    @OrderBy("startDate ASC")
    protected List<TournamentGame> games;

    @ManyToOne(targetEntity = TournamentStageImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_stage_id")
    protected TournamentStage stage;

    @OneToMany(targetEntity = TournamentRoundLeagueTableImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected List<TournamentRoundLeagueTable> tables;

    @Column(name = "status", unique = false, nullable = true)
    protected String status;

    public TournamentRoundImpl() {
        super();
    }

    public TournamentRoundImpl(long uuid) {
        this();
        this.uuid = uuid;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public List<TournamentGame> getGames() {
        if (games == null) {
            games = new ArrayList<TournamentGame>();
        }
        return games;
    }

    @Override
    public void setGames(List<TournamentGame> games) {
        this.games = games;
    }

    @Override
    public String getRoundLabel() {
        return roundLabel;
    }

    @Override
    public void setRoundLabel(String label) {
        this.roundLabel = label;
    }

    @Override
    public TournamentStage getStage() {
        return stage;
    }

    @Override
    public void setStage(TournamentStage stage) {
        this.stage = stage;
    }

    @Override
    public int getRoundNumber() {
        return roundNumber;
    }

    @Override
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    @Override
    public void addGame(TournamentGame game) {
        if (game != null) {
            if (getGames() == null) {
                List<TournamentGame> games = new ArrayList<TournamentGame>();
                games.add(game);
                setGames(games);
            } else {
                if (!getGames().contains(game)) {
                    getGames().add(game);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TournamentRound) {
            TournamentRound r = (TournamentRound) obj;
            if (r.getUUID() != 0 && getUUID() != 0) {
                return r.getUUID() == getUUID() ? true : false;
            }
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getUUID()).hashCode();
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
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getStartDate() {
        if (startDate == null) {
            return null;
        }
        return (Date) startDate.clone();
    }

    @Override
    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = (Date) startDate.clone();
        }
    }

    @Override
    public Date getEndDate() {
        if (endDate == null) {
            return null;
        }
        return (Date) endDate.clone();
    }

    @Override
    public void setEndDate(Date endDate) {
        if (endDate != null) {
            this.endDate = (Date) endDate.clone();
        }
    }

    @Override
    public int compareTo(TournamentRound o) {
        return getStartDate().compareTo(o.getStartDate());
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public List<SportContestant> getContestants() {
        List<SportContestant> contestants = new ArrayList<SportContestant>();
        for (TournamentGame game : getGames()) {
            for (SportContestant contestant : game.getContestants()) {
                if (!contestants.contains(contestant)) {
                    contestants.add(contestant);
                }
            }
        }
        return contestants;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public List<TournamentRoundLeagueTable> getTables() {
        if (tables == null) {
            tables = new ArrayList<TournamentRoundLeagueTable>();
        }
        return tables;
    }

    @Override
    public void setTables(List<TournamentRoundLeagueTable> tables) {
        if (tables != null) {
            this.tables = tables;
        }
    }

    @Override
    public TournamentRoundLeagueTable getTableByType(String type) {
        TournamentRoundLeagueTable t = null;
        for (TournamentRoundLeagueTable each : getTables()) {
            if (type.equals(each.getType())) {
                t = each;
                break;
            }
        }
        return t;
    }

    @Override
    public void addTable(TournamentRoundLeagueTable table) {
        if (getTableByType(table.getType()) == null) {
            tables.add(table);
        }
    }

    @Override
    public String toString() {
        if (getUUID() != 0 && getName() != null) {
            // to ease debug
            return TournamentRoundImpl.class.getName() + " : uuid=" + String.valueOf(getUUID()) + " name=" + getName();
        }
        return super.toString();
    }

}
