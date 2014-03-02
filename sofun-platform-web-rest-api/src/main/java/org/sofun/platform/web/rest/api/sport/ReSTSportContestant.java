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

package org.sofun.platform.web.rest.api.sport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.platform.web.rest.api.ReSTCountry;

/**
 * Sport Player Web API.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTSportContestant implements Serializable {

    private static final long serialVersionUID = -4792716469789822104L;

    protected String uuid;

    protected String name;

    protected String givenName;

    protected String lastName;

    protected ReSTCountry country;

    protected String seasonId;

    protected String type;

    protected Collection<ReSTSportContestant> players = new ArrayList<ReSTSportContestant>();

    protected List<ReSTSportContestant> teams = new ArrayList<ReSTSportContestant>();

    protected Map<String, String> properties = new HashMap<String, String>();

    public ReSTSportContestant() {
        super();
    }

    public ReSTSportContestant(SportContestant coreContestant, long seasonId,
            Boolean withPlayers) {

        this();

        if (coreContestant != null) {

            setUuid(coreContestant.getUUID());
            setName(coreContestant.getName());
            setGivenName(coreContestant.getGivenName());
            setLastName(coreContestant.getLastName());
            setType(coreContestant.getSportContestantType());
            setSeasonId(String.valueOf(seasonId));

            if (coreContestant.getCountry() != null) {
                setCountry(new ReSTCountry(coreContestant.getCountry(),
                        seasonId));
            }

            setProperties(coreContestant.getProperties());

            if (withPlayers && SportContestantType.TEAM.equals(getType())
                    && coreContestant.getPlayers() != null) {
                for (SportContestant p : coreContestant.getPlayers()) {
                    players.add(new ReSTSportContestant(p, seasonId));
                }
            }

            if (SportContestantType.INDIVIDUAL.equals(getType())) {
                for (SportContestant coreTeam : coreContestant.getTeams()) {
                    getTeams().add(
                            new ReSTSportContestant(coreTeam, seasonId, false));
                }
            }
        }
    }

    public ReSTSportContestant(SportContestant corePlayer, long seasonId) {
        this(corePlayer, seasonId, false);
    }
    
    public ReSTSportContestant(SportContestant corePlayer) {
        this(corePlayer, 0, false);
    }

    public ReSTSportContestant(String uuid, String name, ReSTCountry country) {
        this();
        this.uuid = uuid;
        this.name = name;
        this.country = country;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ReSTCountry getCountry() {
        return country;
    }

    public void setCountry(ReSTCountry country) {
        this.country = country;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<ReSTSportContestant> getTeams() {
        return teams;
    }

    public void setTeams(List<ReSTSportContestant> teams) {
        this.teams = teams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public Collection<ReSTSportContestant> getPlayers() {
        return players;
    }

    public void setPlayers(Collection<ReSTSportContestant> players) {
        this.players = players;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
