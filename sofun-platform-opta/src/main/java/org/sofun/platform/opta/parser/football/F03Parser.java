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

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableColumnImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableKeyImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableRowImpl;
import org.sofun.core.sport.tournament.table.TournamentRoundLeagueTableImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * F3 Standings
 * 
 * <p/>
 * 
 * This feed essentially powers any Live League Tables Content. The feed
 * contains the league table standings; detailing table position, points scored,
 * games played, goals scored, goals conceded and goal difference, games won,
 * games drawn and games lost. Home and away breakdowns of points scored, goals
 * scored and conceded are also available.
 * 
 * <p/>
 * 
 * Opta deliver standings updates every time there is a change, even during the
 * live games. Therefore, subscribers should expect a new SRML Standings file to
 * arrive on the following occasions:
 * 
 * <ul>
 * <li>Game Starts</li>
 * <li>Game Ends</li>
 * <li>Goal is scored</li>
 * <li>A team is deducted points</li>
 * </ul>
 * 
 * The File naming convention used for this feed is the following :
 * srml-{competition_id}-{season_id}-standings.xml
 * 
 * <p/>
 * 
 * Implementation based on version specification documentation 1.6
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class F03Parser extends AbstractOptaParser {

    private SportService sports;

    private boolean isCompetition;

    private Tournament tournament;

    private TournamentSeason season;

    private String matchDay;

    private String groupName;

    private SportContestant team;

    private TournamentStage stage;

    private TournamentRound round;

    private TournamentRoundLeagueTable table;

    private TournamentLeagueTableRow row;

    private SecureRandom randomGenerator = new SecureRandom();

    public F03Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public F03Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public F03Parser(File file, SportService sports, EntityManager em) {
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

        if (qName.equalsIgnoreCase("SoccerDocument")) {

            // If tournament and/or season do not exist yet then do not do
            // anything. F1 and F40 are responsible for graph top
            // level elements creation.

            if (sports == null) {
                return;
            }

            final long tUUID = Long.valueOf(attributes
                    .getValue("competition_id"));
            tournament = sports.getSportTournament(tUUID);
            if (tournament == null) {
                return;
            }

            if (sports != null && sports.getSportTournament(tUUID) != null) {
                tournament = sports.getSportTournament(tUUID);
            }

            final String seasonId = attributes.getValue("season_id");
            season = sports.getSeasonByYearLabel(tournament, seasonId);

            if (season == null) {
                return;
            }

        } else if (qName.equalsIgnoreCase("Competition")) {

            isCompetition = true;

        } else if (qName.equalsIgnoreCase("TeamStandings")) {

            matchDay = attributes.getValue("Matchday");

        } else if (qName.equalsIgnoreCase("Name")) {

            if (isCompetition) {
                groupName = attributes.getValue("id");
            }

        } else if (qName.equalsIgnoreCase("TeamRecord")) {

            // If tournament and/or season do not exist yet then do not do
            // anything. F1 and F40 are responsible for graph top
            // level elements creation.

            if (season == null) {
                return;
            }

            if (stage == null && round == null) {
                if (groupName != null) { // Cup type of championship
                    stage = season.getStageByName("Round");
                    if (stage != null) {
                        round = stage.getRoundByName(groupName);
                        if (round != null) {
                            final String tableType = "main_ranking";
                            if (round.getTableByType(tableType) == null) {
                                table = new TournamentRoundLeagueTableImpl(
                                        groupName, tableType);
                                table.setUUID(randomGenerator.nextInt(1000000));
                                table.setKeys(getTableKeys());
                                table.setTournamentRound(round);
                                round.addTable(table);
                            } else {
                                table = round.getTableByType(tableType);
                            }
                        }
                    }
                } else if (matchDay != null) { // Championship type
                    round = season.getRoundByName(matchDay);
                    if (round != null) {
                        final String tableType = "main_ranking";
                        if (round.getTableByType(tableType) == null) {
                            table = new TournamentRoundLeagueTableImpl(
                                    matchDay, tableType);
                            table.setUUID(randomGenerator.nextInt(1000000));
                            table.setKeys(getTableKeys());
                            table.setTournamentRound(round);
                            round.addTable(table);
                        } else {
                            table = round.getTableByType(tableType);
                        }
                    }
                }
            }

            if (table == null) {
                return;
            }

            final String teamUUID = attributes.getValue("TeamRef");
            if (sports != null) {
                team = sports.getSportContestantTeam(teamUUID);
            }

            if (team == null) {
                return;
            }

            if (table.getRowFor(team) != null) {
                row = table.getRowFor(team);
            } else {
                row = new TournamentLeagueTableRowImpl();
                row.setSportContestant(team);
                row.setLeagueTable(table);
                table.addRow(row);
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("Competition")) {
            isCompetition = false;
        } else if (getQNameKeys().contains(qName)) {

            // If tournament and/or season do not exist yet then do not do
            // anything. F1 and F40 are responsible for graph top
            // level elements creation.

            if (team == null) {
                return;
            }

            TournamentLeagueTableColumn column = null;
            if (row.getColumn(qName) != null) {
                column = row.getColumn(qName);
                column.setColumnValue(tempVal);
            } else {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey(qName);
                column.setColumnValue(tempVal);
                row.addColumn(column);
                column.setRow(row);
            }

        } else if (qName.equalsIgnoreCase("TeamRecord")) {
            stage = null;
            round = null;
            table = null;
            team = null;
        }

    }

    private List<TournamentLeagueTableKey> getTableKeys() {

        final List<TournamentLeagueTableKey> keys = new ArrayList<TournamentLeagueTableKey>();

        keys.add(new TournamentLeagueTableKeyImpl("Against", "Against"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayAgainst", "AwayAgainst"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayDrawn", "AwayDrawn"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayFor", "AwayFor"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayLost", "AwayLost"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayPlayed", "AwayPlayed"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayPoints", "AwayPoints"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayPosition",
                "AwayPosition"));
        keys.add(new TournamentLeagueTableKeyImpl("AwayWon", "AwayWon"));
        keys.add(new TournamentLeagueTableKeyImpl("Drawn", "Drawn"));
        keys.add(new TournamentLeagueTableKeyImpl("For", "For"));
        keys.add(new TournamentLeagueTableKeyImpl("HomeAgainst", "HomeAgainst"));
        keys.add(new TournamentLeagueTableKeyImpl("HomeDrawn", "HomeDrawn"));
        keys.add(new TournamentLeagueTableKeyImpl("HomeFor", "HomeFor"));
        keys.add(new TournamentLeagueTableKeyImpl("HomeLost", "HomeLost"));
        keys.add(new TournamentLeagueTableKeyImpl("HomePlayed", "HomePlayed"));
        keys.add(new TournamentLeagueTableKeyImpl("HomePoints", "HomePoints"));
        keys.add(new TournamentLeagueTableKeyImpl("HomePosition",
                "HomePosition"));
        keys.add(new TournamentLeagueTableKeyImpl("HomeWon", "HomeWon"));
        keys.add(new TournamentLeagueTableKeyImpl("Lost", "Lost"));
        keys.add(new TournamentLeagueTableKeyImpl("Played", "Played"));
        keys.add(new TournamentLeagueTableKeyImpl("Points", "Points"));
        keys.add(new TournamentLeagueTableKeyImpl("StartDayPosition",
                "StartDayPosition"));

        return keys;

    }

    private List<String> getQNameKeys() {
        final List<String> qKeys = new ArrayList<String>();
        for (TournamentLeagueTableKey key : getTableKeys()) {
            qKeys.add(key.getKey());
        }
        return qKeys;
    }

    public TournamentSeason getSeason() {
        return season;
    }

}
