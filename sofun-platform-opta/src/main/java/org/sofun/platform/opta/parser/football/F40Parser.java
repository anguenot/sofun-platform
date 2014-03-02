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

package org.sofun.platform.opta.parser.football;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.country.Country;
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.SportImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * F40 Squads Feed
 * 
 * <p/>
 * 
 * This feed contains information on each team‚Äôs registered playing squad for
 * a given competition.
 * 
 * <p/>
 * 
 * For each team
 * 
 * <ul>
 * <li>the file lists</li>
 * <li>Player first, last and known names Dates of birth</li>
 * <li>Height</li>
 * <li>Weight</li>
 * <li>Jersey number</li>
 * <li>Natural / default playing position</li>
 * </ul>
 * 
 * The File naming convention used for this feed is the following :
 * srml-{competition_id}-{season_id}-squads.xml
 * 
 * <p/>
 * J
 * 
 * Once a production environment is established between Opta Sportsdata and the
 * subscriber this feed is sent out each time Opta make a change to their player
 * database. Subscribers therefore will only get a new version when updates have
 * been made.
 * 
 * </p>
 * 
 * Based on version specification documentation 1.2.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class F40Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(F40Parser.class);

    private static final String sportName = "Soccer";

    private boolean inTeam = false;

    private boolean inStadium = false;

    private Tournament tournament;

    private TournamentSeason season;

    private SportService sports;

    private SportContestant team;

    private SportContestant player;

    private String statType;

    private final List<SportContestant> teams = new ArrayList<SportContestant>();

    private final List<SportContestant> players = new ArrayList<SportContestant>();

    private List<SportContestant> teamPlayers = new ArrayList<SportContestant>();

    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    public F40Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public F40Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public F40Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.sports = sports;
        this.em = em;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";
        if (qName.equalsIgnoreCase("SoccerDocument")) {

            final long tUUID = Long.valueOf(attributes
                    .getValue("competition_id"));
            final String tName = attributes.getValue("competition_name");
            final String tCode = attributes.getValue("competition_code");
            if (sports == null || sports.getSportTournament(tUUID) == null) {
                tournament = new TournamentImpl(tName, tUUID);
                Sport sport = null;
                if (sports != null) {
                    sport = sports.getSportByName(sportName);
                }
                if (sport == null) {
                    sport = new SportImpl(sportName,
                            randomGenerator.nextInt(1000000));
                }
                tournament.addSport(sport);
                if (em != null) {
                    em.persist(tournament);
                }
            } else {
                tournament = sports.getSportTournament(tUUID);
            }
            tournament.setName(tName);
            tournament.setCode(tCode);

            final String seasonId = attributes.getValue("season_id");
            final String seasonName = attributes.getValue("season_name");

            if (sports == null
                    || sports.getSeasonByYearLabel(tournament, seasonId) == null) {
                final long sUUID = randomGenerator.nextInt(1000000);
                season = new TournamentSeasonImpl(sUUID);
                season.setYearLabel(seasonId);
                season.setTournament(tournament);
                tournament.addSeason(season);
            } else {
                season = sports.getSeasonByYearLabel(tournament, seasonId);
            }
            final String name = tournament.getName() + " - " + seasonName;
            season.setName(name);

        } else if (qName.equalsIgnoreCase("Team")) {

            inTeam = true;

            final String teamUUID = attributes.getValue("uID");
            if (sports == null
                    || sports.getSportContestantTeam(teamUUID) == null) {
                team = new SportContestantImpl(teamUUID,
                        SportContestantType.TEAM);
                season.addContestant(team);
            } else {
                team = sports.getSportContestantTeam(teamUUID);
            }

            final String teamName = attributes.getValue("Name");
            if (teamName != null) {
                team.setName(teamName);
            }

            final String symid = attributes.getValue("SYMID");
            if (symid != null) {
                team.getProperties().put("SYMID", symid);
            }

            final String nickName = attributes.getValue("Nickname");
            if (nickName != null) {
                team.getProperties().put("nickname", nickName);
            }

            final String founded = attributes.getValue("Founded");
            if (founded != null) {
                team.getProperties().put("founded", founded);
            }

        } else if (qName.equalsIgnoreCase("Player")) {

            final String playerUUID = attributes.getValue("uID");
            if (sports == null
                    || sports.getSportContestantPlayer(playerUUID) == null) {
                player = new SportContestantImpl(playerUUID,
                        SportContestantType.INDIVIDUAL);
            } else {
                player = sports.getSportContestantPlayer(playerUUID);
            }

            if (!player.getTeams().contains(team)) {
                player.addTeam(team);
            }
            if (!team.getPlayers().contains(player)) {
                team.addPlayer(player);
            }

        } else if (qName.equalsIgnoreCase("Stat")) {

            statType = attributes.getValue("Type");

        }
        if (qName.equalsIgnoreCase("Stadium")) {

            inStadium = true;

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("Team")) {

            if (team != null) {

                List<SportContestant> toRemove = new ArrayList<SportContestant>();
                if (sports != null) {
                    // Checking if former players have been removed from the
                    // team
                    for (SportContestant player : sports
                            .getSportContestantTeam(team.getUUID())
                            .getPlayers()) {
                        if (!teamPlayers.contains(player)) {
                            toRemove.add(player);
                        }
                    }
                }
                // Add new players.
                for (SportContestant player : teamPlayers) {
                    if (!team.getPlayers().contains(player)) {
                        team.addPlayer(player);
                        player.addTeam(team);
                        log.info("Player with uuid=" + player.getUUID()
                                + " (name=" + player.getName() + ")"
                                + " has been added to team with uuid="
                                + team.getUUID() + " (name=" + team.getName()
                                + ")");
                    }
                }
                // Removing players from team in a 2nd iteration to avoid
                // concurrent modification error.
                /*
                 * for (SportContestant player : toRemove) {
                 * team.delPlayer(player); player.delTeam(team);
                 * log.info("Player with uuid=" + player.getUUID() + " (name=" +
                 * player.getName() + ")" +
                 * " has been removed from team with uuid=" + team.getUUID() +
                 * " (name=" + team.getName() + ")"); }
                 */

            }

            inTeam = false;
            teams.add(team);
            team = null;
            teamPlayers = new ArrayList<SportContestant>();

        } else if (qName.equalsIgnoreCase("Name")) {

            if (!inStadium && inTeam && team != null && player == null) {
                team.setName(tempVal);
            } else if (player != null) {
                player.setName(tempVal);
            }

        } else if (qName.equalsIgnoreCase("Player")) {

            player.setName(player.getGivenName() + " " + player.getLastName());

            players.add(player);
            teamPlayers.add(player);
            player = null;

        } else if (qName.equalsIgnoreCase("Stat")) {

            if ("first_name".equals(statType)) {
                player.setGivenName(tempVal);
            } else if ("last_name".equals(statType)) {
                player.setLastName(tempVal);
            } else if ("jersey_num".equals(statType)) {
                try {
                    player.getProperties().put(statType, tempVal);
                } catch (NumberFormatException e) {
                    player.getProperties().put(statType, "-1");
                }
            } else if ("weight".equals(statType) || "height".equals(statType)
                    || "real_position".equals(statType)
                    || "birth_date".equals(statType)) {
                player.getProperties().put(statType, tempVal);
            } else if ("country".equals(statType)) {
                if (sports != null) {
                    Country country = sports.getCountryByName(tempVal);
                    if (country != null) {
                        player.setCountry(country);
                    }
                }
            }

        } else if (qName.equalsIgnoreCase("Position")) {

            player.getProperties().put("position", tempVal);

        } else if (qName.equalsIgnoreCase("Stadium")) {

            inStadium = false;

        }

    }

    public Tournament getTournament() {
        return tournament;
    }

    public TournamentSeason getTournamentSeason() {
        return season;
    }

    public List<SportContestant> getTeams() {
        return teams;
    }

    public List<SportContestant> getPlayers() {
        return players;
    }

}
