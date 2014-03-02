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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentStageLeagueTable;
import org.sofun.core.sport.tournament.table.TournamentStageLeagueTableImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_stages")
public class TournamentStageImpl implements TournamentStage {

    private static final long serialVersionUID = 969965285783267944L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = false)
    protected long uuid;

    @Column(name = "name", unique = false, nullable = true)
    protected String name;

    @Column(name = "description", unique = false, nullable = true)
    protected String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    protected Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    protected Date endDate;

    @OneToMany(
            targetEntity = TournamentRoundImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "stage")
    @OrderBy("startDate ASC")
    protected List<TournamentRound> rounds;

    @ManyToOne(
            targetEntity = TournamentSeasonImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_season_id")
    protected TournamentSeason season;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(
            name = "sports_tournaments_stages_properties",
            joinColumns = @JoinColumn(name = "id"))
    protected Map<String, String> properties = new HashMap<String, String>();

    @Column(name = "status", unique = false, nullable = true)
    protected String status;

    @OneToMany(
            targetEntity = TournamentStageLeagueTableImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected List<TournamentStageLeagueTable> tables;

    public TournamentStageImpl() {
        super();
    }

    public TournamentStageImpl(long uuid) {
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
    public List<TournamentRound> getRounds() {
        return rounds;
    }

    @Override
    public void setRounds(List<TournamentRound> rounds) {
        this.rounds = rounds;
    }

    @Override
    public TournamentSeason getSeason() {
        return season;
    }

    @Override
    public void setSeason(TournamentSeason season) {
        this.season = season;
    }

    @Override
    public void addRound(TournamentRound round) {
        if (round != null) {
            if (getRounds() == null) {
                List<TournamentRound> rounds = new ArrayList<TournamentRound>();
                rounds.add(round);
                setRounds(rounds);
            } else {
                if (!getRounds().contains(round)) {
                    getRounds().add(round);
                }
            }
        }
    }

    @Override
    public TournamentRound getRoundByNumber(int number) {
        if (getRounds() != null) {
            for (TournamentRound round : getRounds()) {
                if (round.getRoundNumber() == number) {
                    return round;
                }
            }
        }
        return null;
    }

    @Override
    public TournamentRound getRoundByLabel(String label) {
        if (getRounds() != null) {
            for (TournamentRound round : getRounds()) {
                if (label.equals(round.getRoundLabel())) {
                    // First found with label returned. Application level
                    // concern
                    return round;
                }
            }
        }
        return null;
    }

    @Override
    public TournamentRound getRoundByName(String name) {
        if (getRounds() != null && name != null) {
            for (TournamentRound round : getRounds()) {
                if (name.equals(round.getName())) {
                    return round;
                }
            }
        }
        return null;
    }

    @Override
    public TournamentRound getRoundById(long id) {
        if (getRounds() != null) {
            for (TournamentRound round : getRounds()) {
                if (round.getId() == id) {
                    return round;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TournamentStage) {
            TournamentStage s = (TournamentStage) obj;
            if (s.getUUID() != 0 && getUUID() != 0) {
                return s.getUUID() == getUUID() ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getUUID()).hashCode();
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
    public void clear() {
        getProperties().clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return getProperties().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getProperties().containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return getProperties().entrySet();
    }

    @Override
    public String get(Object key) {
        return getProperties().get(key);
    }

    @Override
    public boolean isEmpty() {
        return getProperties().isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return getProperties().keySet();
    }

    @Override
    public String put(String key, String value) {
        return getProperties().put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        getProperties().putAll(m);

    }

    @Override
    public String remove(Object key) {
        return getProperties().remove(key);
    }

    @Override
    public int size() {
        return getProperties().size();
    }

    @Override
    public Collection<String> values() {
        return getProperties().values();
    }

    @Override
    public int compareTo(TournamentStage other) {
        return getStartDate().compareTo(other.getStartDate());
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
    public List<TournamentStageLeagueTable> getTables() {
        if (tables == null) {
            tables = new ArrayList<TournamentStageLeagueTable>();
        }
        return tables;
    }

    @Override
    public void setTables(List<TournamentStageLeagueTable> tables) {
        if (tables != null) {
            this.tables = tables;
        }

    }

    @Override
    public TournamentStageLeagueTable getTableByType(String type) {
        TournamentStageLeagueTable t = null;
        for (TournamentStageLeagueTable each : getTables()) {
            if (type.equals(each.getType())) {
                t = each;
                break;
            }
        }
        return t;
    }

    @Override
    public void addTable(TournamentStageLeagueTable table) {
        if (getTableByType(table.getType()) == null) {
            tables.add(table);
        }
    }

    @Override
    public List<TournamentGame> getGames() {
        List<TournamentGame> games = new ArrayList<TournamentGame>();
        for (TournamentRound round : getRounds()) {
            List<TournamentGame> rgames = round.getGames();
            if (rgames != null) {
                games.addAll(rgames);
            }
        }
        return games;
    }

    @Override
    public String toString() {
        if (getUUID() != 0 && getName() != null) {
            // to ease debug
            return TournamentStageImpl.class.getName() + ": uuid="
                    + String.valueOf(getUUID()) + " name=" + getName();
        }
        return super.toString();
    }

    @Override
    public TournamentRound getRoundByUUID(long uuid) {
        if (getRounds() != null) {
            for (TournamentRound round : getRounds()) {
                if (round.getId() == uuid) {
                    return round;
                }
            }
        }
        return null;
    }

}
