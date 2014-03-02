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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentSeasonLeagueTable;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.tournament.table.TournamentSeasonLeagueTableImpl;

/**
 * Tournament season implementation.
 * 
 * @see {@link Tournament}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_seasons")
public class TournamentSeasonImpl implements TournamentSeason {

    private static final long serialVersionUID = -5103038501516827667L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = false)
    protected long uuid;

    @Column(name = "year_label", unique = false, nullable = true)
    protected String yearLabel;

    @Column(name = "name", unique = false, nullable = true)
    protected String name;

    @Column(name = "status", unique = false, nullable = true)
    protected String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    protected Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    protected Date endDate;

    @ManyToOne(
            targetEntity = TournamentImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id")
    protected Tournament tournament;

    @OneToMany(
            targetEntity = TournamentStageImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "season")
    @OrderBy("startDate ASC")
    protected List<TournamentStage> stages;

    @ManyToMany(
            targetEntity = SportContestantImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "sports_tournaments_contestants",
            joinColumns = { @JoinColumn(
                    name = "tournament_season_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(
                    name = "sport_contestant_id",
                    referencedColumnName = "id") })
    protected List<SportContestant> contestants;

    @OneToMany(
            targetEntity = TournamentSeasonLeagueTableImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "table_id")
    protected List<TournamentSeasonLeagueTable> tables;

    public TournamentSeasonImpl() {

    }

    public TournamentSeasonImpl(long uuid) {
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
    public String getYearLabel() {
        return yearLabel;
    }

    @Override
    public void setYearLabel(String yearLabel) {
        this.yearLabel = yearLabel;
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
    public Tournament getTournament() {
        return tournament;
    }

    @Override
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    @Override
    public List<SportContestant> getConstestants() {
        if (contestants == null) {
            contestants = new ArrayList<SportContestant>();
        }
        return contestants;
    }

    @Override
    public void setContestants(List<SportContestant> contestants) {
        this.contestants = contestants;
    }

    @Override
    public List<TournamentStage> getStages() {
        if (stages == null) {
            stages = new ArrayList<TournamentStage>();
        }
        return stages;
    }

    @Override
    public void setStages(List<TournamentStage> stages) {
        this.stages = stages;
    }

    @Override
    public void addStage(TournamentStage stage) {
        if (stage != null) {
            if (!getStages().contains(stage)) {
                getStages().add(stage);
            }
        }
    }

    @Override
    public void addContestant(SportContestant contestant) {
        if (contestant != null) {
            if (!getConstestants().contains(contestant)) {
                getConstestants().add(contestant);
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
        if (obj instanceof TournamentSeason) {
            TournamentSeason s = (TournamentSeason) obj;
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
    public List<TournamentRound> getRounds() {
        List<TournamentRound> rounds = new ArrayList<TournamentRound>();
        for (TournamentStage stage : getStages()) {
            List<TournamentRound> srounds = stage.getRounds();
            if (srounds != null) {
                rounds.addAll(srounds);
            }
        }
        return rounds;
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
    public List<SportContestant> getPlayers() {
        List<SportContestant> players = new ArrayList<SportContestant>();
        for (SportContestant c : getConstestants()) {
            if (c.getSportContestantType().equals(SportContestantType.TEAM)) {
                if (c.getPlayers() != null) {
                    players.addAll(c.getPlayers());
                }
            } else {
                players.add(c);
            }
        }
        return players;
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
    public TournamentStage getStageByName(String name) {
        if (getStages() != null) {
            for (TournamentStage stage : getStages()) {
                if (stage.getName() != null && stage.getName().equals(name)) {
                    return stage;
                }
            }
        }
        return null;
    }

    @Override
    public TournamentRound getRoundByName(String name) {
        if (getRounds() != null) {
            for (TournamentRound round : getRounds()) {
                if (round.getName() != null && round.getName().equals(name)) {
                    return round;
                }
            }
        }
        return null;
    }

    @Override
    public TournamentRound getRoundByUUID(String uuid) {
        if (getRounds() != null) {
            for (TournamentRound round : getRounds()) {
                // BBB
                if (round.getId() == Long.valueOf(uuid)) {
                    return round;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        if (getUUID() != 0 && getName() != null && getYearLabel() != null) {
            return TournamentSeasonImpl.class.getName() + ": uuid="
                    + String.valueOf(getUUID()) + " name=" + getName()
                    + " yearLabel=" + getYearLabel();
        }
        return super.toString();
    }

    @Override
    public List<TournamentSeasonLeagueTable> getTables() {
        if (tables == null) {
            tables = new ArrayList<TournamentSeasonLeagueTable>();
        }
        return tables;
    }

    @Override
    public void setTables(List<TournamentSeasonLeagueTable> tables) {
        if (tables != null) {
            this.tables = tables;
        }
    }

    @Override
    public TournamentSeasonLeagueTable getTableByType(String type) {
        TournamentSeasonLeagueTable t = null;
        for (TournamentSeasonLeagueTable each : getTables()) {
            if (type.equals(each.getType())) {
                t = each;
                break;
            }
        }
        return t;
    }

    @Override
    public void addTable(TournamentSeasonLeagueTable table) {
        if (getTableByType(table.getType()) == null) {
            tables.add(table);
        }
    }

}
