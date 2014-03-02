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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameScore;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.sport.SportContestantImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments_games")
public class TournamentGameImpl implements TournamentGame {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = true)
    protected String uuid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    protected Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    protected Date endDate;

    @Column(name = "status", unique = false, nullable = true)
    protected String status;

    @Column(name = "description", unique = false, nullable = true)
    protected String description;

    @ManyToMany(targetEntity = SportContestantImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "sports_tournaments_games_contestants", joinColumns = { @JoinColumn(name = "tournament_game_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "sport_contestant_id", referencedColumnName = "id") })
    @OrderColumn(name = "pos")
    protected List<SportContestant> contestants;

    @ManyToOne(targetEntity = TournamentRoundImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "round_id")
    protected TournamentRound round;

    @ManyToOne(targetEntity = SportContestantImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "winner_contestant_id")
    protected SportContestant winner;

    @OneToOne(targetEntity = TournamentGameScoreImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "score_id")
    protected TournamentGameScore score;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "sports_tournaments_games_properties", joinColumns = @JoinColumn(name = "id"))
    protected Map<String, String> properties = new HashMap<String, String>();

    public TournamentGameImpl() {

    }

    public TournamentGameImpl(String uuid) {
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
    public String getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(String uuid) {
        this.uuid = uuid;
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
    public List<SportContestant> getContestants() {
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
    public void addContestant(SportContestant contestant) {
        if (!getContestants().contains(contestant)) {
            getContestants().add(contestant);
        }
    }

    @Override
    public TournamentRound getRound() {
        return round;
    }

    @Override
    public void setRound(TournamentRound round) {
        this.round = round;
    }

    @Override
    public String getGameStatus() {
        return status;
    }

    @Override
    public void setGameStatus(String status) {
        this.status = status;
    }

    @Override
    public SportContestant getWinner() {
        return winner;
    }

    @Override
    public void setWinner(SportContestant winner) {
        this.winner = winner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof TournamentGame) {
            TournamentGame g = (TournamentGame) obj;
            if (g.getUUID() != null && getUUID() != null) {
                return g.getUUID().equals(getUUID()) ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }

    @Override
    public TournamentGameScore getScore() {
        if (score == null) {
            score = new TournamentGameScoreImpl();
        }
        return score;
    }

    @Override
    public void setScore(TournamentGameScore score) {
        this.score = score;
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
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
