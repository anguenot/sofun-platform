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

package org.sofun.core.sport;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sofun.core.api.country.Country;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.country.CountryImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableRowImpl;

/**
 * Sport contestant implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sports_contestants")
public class SportContestantImpl implements SportContestant {

    private static final long serialVersionUID = 177224188706304711L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private long id;

    @Column(name = "uuid", unique = true, nullable = false)
    protected String uuid;

    @Column(name = "name", unique = false, nullable = true)
    protected String name;

    @Column(name = "given_name", unique = false, nullable = true)
    protected String givenName;

    @Column(name = "last_name", unique = false, nullable = true)
    protected String lastName;

    @Column(name = "type", unique = false, nullable = false)
    protected String type;

    @ManyToMany(
            targetEntity = SportContestantImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "sports_contestants_teams", joinColumns = { @JoinColumn(
            name = "contestant_team_id",
            referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(
            name = "contestant_team_rev_id",
            referencedColumnName = "id") })
    protected List<SportContestant> teams;

    @ManyToMany(
            targetEntity = SportContestantImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "sports_contestants_players",
            joinColumns = { @JoinColumn(
                    name = "contestant_team_id",
                    referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(
                    name = "contestant_team_rev_id",
                    referencedColumnName = "id") })
    protected List<SportContestant> players;

    @ManyToOne(
            targetEntity = CountryImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    protected Country country;

    @OneToMany(
            targetEntity = TournamentLeagueTableRowImpl.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "contestant")
    protected List<TournamentLeagueTableRow> rows;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(
            name = "sports_contestants_properties",
            joinColumns = @JoinColumn(name = "id"))
    protected Map<String, String> properties = new HashMap<String, String>();

    public SportContestantImpl() {
        super();
    }

    public SportContestantImpl(String uuid, String type) {
        this();
        this.uuid = uuid;
        this.type = type;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setGivenName(String giveName) {
        this.givenName = giveName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public List<SportContestant> getTeams() {
        if (teams == null) {
            teams = new ArrayList<SportContestant>();
        }
        return teams;
    }

    @Override
    public void setTeams(List<SportContestant> teams) {
        this.teams = teams;
    }

    @Override
    public void addTeam(SportContestant team) {
        if (team != null) {
            if (getTeams() == null) {
                List<SportContestant> teams = new ArrayList<SportContestant>();
                teams.add(team);
                setTeams(teams);
            } else {
                if (!getTeams().contains(team)) {
                    getTeams().add(team);
                }
            }
        }
    }

    @Override
    public String getSportContestantType() {
        return type;
    }

    @Override
    public void setSportContestantType(String sportContestantType) {
        this.type = sportContestantType;

    }

    @Override
    public List<SportContestant> getPlayers() {
        if (players == null) {
            players = new ArrayList<SportContestant>();
        }
        return players;
    }

    @Override
    public void setPlayers(List<SportContestant> players) {
        this.players = players;
    }

    @Override
    public void addPlayer(SportContestant player) {
        if (player != null) {
            if (getPlayers() == null) {
                List<SportContestant> players = new ArrayList<SportContestant>();
                players.add(player);
                setPlayers(players);
            } else {
                if (!getPlayers().contains(player)) {
                    getPlayers().add(player);
                }
            }
        }
    }

    @Override
    public Country getCountry() {
        return country;
    }

    @Override
    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public List<TournamentLeagueTableRow> getLeagueTableRows() {
        return rows;
    }

    @Override
    public void setLeagueTableRows(List<TournamentLeagueTableRow> rows) {
        this.rows = rows;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof SportContestant) {
            SportContestant c = (SportContestant) obj;
            if (c.getUUID() != null && getUUID() != null) {
                return c.getUUID().equals(getUUID()) ? true : false;
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
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
    public void delTeam(SportContestant team) {
        if (getTeams().contains(team)) {
            getTeams().remove(team);
        }
    }

    @Override
    public void delPlayer(SportContestant player) {
        if (getPlayers().contains(player)) {
            getPlayers().remove(player);
        }

    }

}
