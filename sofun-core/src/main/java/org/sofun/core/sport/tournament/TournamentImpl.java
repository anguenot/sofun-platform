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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.sport.SportImpl;

/**
 * Tournament definition implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_tournaments")
public class TournamentImpl implements Tournament {

    private static final long serialVersionUID = -7032832535262834069L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    protected long id;

    @Column(name = "uuid", unique = true, nullable = false)
    protected long uuid;

    @Column(name = "name", unique = false, nullable = false)
    protected String name;

    @Column(name = "code", unique = false, nullable = true)
    protected String code;

    @ManyToMany(targetEntity = SportImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "sports_tournaments_sports", joinColumns = { @JoinColumn(name = "tournament_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "sport_id", referencedColumnName = "id") })
    protected List<Sport> sports;

    @OneToMany(targetEntity = TournamentSeasonImpl.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tournament")
    protected List<TournamentSeason> seasons;

    public TournamentImpl() {
        super();
    }

    public TournamentImpl(String name, long uuid) {
        super();
        this.name = name;
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
    public long getId() {
        return id;
    }

    @Override
    public long getUUID() {
        return uuid;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setUUID(long uuid) {
        this.uuid = uuid;

    }

    @Override
    public List<Sport> getSports() {
        if (sports == null) {
            sports = new ArrayList<Sport>();
        }
        return sports;
    }

    @Override
    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    @Override
    public List<TournamentSeason> getSeasons() {
        if (seasons == null) {
            seasons = new ArrayList<TournamentSeason>();
        }
        return seasons;
    }

    @Override
    public void setSeasons(List<TournamentSeason> seasons) {
        this.seasons = seasons;
    }

    @Override
    public void addSeason(TournamentSeason season) {
        if (season != null && !getSeasons().contains(season)) {
            getSeasons().add(season);
        }
    }

    @Override
    public void addSport(Sport sport) {
        if (sport != null && !getSports().contains(sport)) {
            getSports().add(sport);
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
        if (obj instanceof Tournament) {
            Tournament t = (Tournament) obj;
            if (t.getUUID() != 0 && getUUID() != 0) {
                return t.getUUID() == getUUID() ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(getUUID()).hashCode();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return TournamentImpl.class.getName() + ": uuid=" + String.valueOf(getUUID()) + " name=" + getName();
    }

}
