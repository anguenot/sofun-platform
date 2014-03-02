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

package org.sofun.platform.opta.parser.rugby;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * RU10 Competition Profiles parser
 * 
 * <p/>
 * 
 * This feed provides the list of players allocated to each team in a
 * competition at any one time. It also provides biographical details such as
 * height and weight and basic appearance information. The feed is triggered on
 * an ‘as required’ basis when players move between teams or when additional
 * biographical information becomes available. Also included is information
 * about venues and match officials attached to the competition
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following:
 * ru10_comp-{competition_id}.xml
 * 
 * <p/>
 * 
 * Implementation based on version specification documentation of 04 October
 * 2010
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class RU10Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(RU10Parser.class);

    public static final String SPORT_NAME = "Rugby";

    private static final String PLAYER_PREFIX = "rup";

    private static final String TEAM_PREFIX = "rut";

    private SportService sports;

    private Tournament tournament;

    private TournamentSeason season;

    private SportContestant team;

    private SportContestant player;

    private final List<SportContestant> teams = new ArrayList<SportContestant>();

    /** List of all current team players according to feed */
    private List<SportContestant> teamPlayers = new ArrayList<SportContestant>();

    /*
     * To keep track of already processed players since it seems that some feeds
     * contains the same player twice
     */
    private final List<String> playerUUIDs = new ArrayList<String>();

    public RU10Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public RU10Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public RU10Parser(File file, SportService sports, EntityManager em) {
        // Note, we do not need the entity manager here since this parser will
        // not be creating top level graph elements. League
        // tables insertion into existing graph elements will trigger
        // persistency. F1 and F40 are responsible for creation of
        // top level graph elements
        super(file);
        this.sports = sports;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("competition")) {

            final long uuid = Long.valueOf(attributes.getValue("id"));
            tournament = sports.getSportTournament(uuid);
            if (tournament != null) {
                season = sports.getActiveSeasonFor(tournament);
            }

        } else if (qName.equalsIgnoreCase("team")) {

            teamPlayers = new ArrayList<SportContestant>();

            team = null;
            String uuid = attributes.getValue("id");
            if (uuid != null) {
                uuid = TEAM_PREFIX + uuid;
                final String name = attributes.getValue("name");
                if (sports != null) {
                    team = sports.getSportContestantTeam(uuid);
                }
                if (team == null) {
                    team = new SportContestantImpl(uuid,
                            SportContestantType.TEAM);
                }
                team.setName(name);
                teams.add(team);
            }

        } else if (qName.equalsIgnoreCase("players")) {

            player = null;
            String uuid = attributes.getValue("player_id");
            if (uuid != null) {

                uuid = PLAYER_PREFIX + uuid;

                final String firstName = attributes
                        .getValue("player_first_name");
                final String lastName = attributes.getValue("player_last_name");
                final String name = attributes.getValue("player_known_name");

                if (sports != null) {
                    player = sports.getSportContestantPlayer(uuid);
                }
                if (player == null) {
                    if (!playerUUIDs.contains(uuid)) {
                        player = new SportContestantImpl(uuid,
                                SportContestantType.INDIVIDUAL);
                    }
                }

                if (player != null) {
                    player.setName(name);
                    player.setLastName(lastName);
                    player.setGivenName(firstName);

                    if (!player.getTeams().contains(team)) {
                        player.addTeam(team);
                    }
                    if (!team.getPlayers().contains(player)) {
                        team.addPlayer(player);
                    }
                    playerUUIDs.add(uuid);
                    teamPlayers.add(player);
                }

            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("RU10_Profile")) {
            if (season != null) {
                season.setContestants(teams);
            }
        } else if (qName.equalsIgnoreCase("team")) {
            if (team != null) {
                // Checking if former players have been removed from the team
                List<SportContestant> toRemove = new ArrayList<SportContestant>();
                for (SportContestant player : team.getPlayers()) {
                    if (!teamPlayers.contains(player)) {
                        toRemove.add(player);
                    }
                }
                // Removing players from team in a 2nd iteration to avoid
                // concurrent modification error.
                for (SportContestant player : toRemove) {
                    team.delPlayer(player);
                    player.delTeam(team);
                    log.info("Player with uuid=" + player.getUUID() + " (name="
                            + player.getName() + ")"
                            + " has been removed from team with uuid="
                            + team.getUUID() + " (name=" + team.getName() + ")");
                }
            }
            teamPlayers = new ArrayList<SportContestant>();
        }

    }

    public TournamentSeason getSeason() {
        return season;
    }

    public List<SportContestant> getTeams() {
        return teams;
    }

}
