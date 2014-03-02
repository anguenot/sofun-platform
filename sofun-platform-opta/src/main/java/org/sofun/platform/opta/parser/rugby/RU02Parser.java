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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTable;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.api.sport.tournament.table.TournamentStageLeagueTable;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableColumnImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableKeyImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableRowImpl;
import org.sofun.core.sport.tournament.table.TournamentStageLeagueTableImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * RU2 - League Standings
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
 * The File naming convention used for this feed is the following: ru2_tables.
 * -{competition_id}.{season_id}-.xml
 * 
 * <p/>
 * 
 * Implementation based on version specification documentation of 04 October
 * 2010
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class RU02Parser extends AbstractOptaParser {

    private static final String TEAM_PREFIX = "rut";

    private SportService sports;

    private SecureRandom randomGenerator = new SecureRandom();

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentStage stage;

    private SportContestant team;

    private TournamentLeagueTable table;

    private List<TournamentLeagueTableRow> rows = new ArrayList<TournamentLeagueTableRow>();

    private List<TournamentLeagueTable> tables = new ArrayList<TournamentLeagueTable>();

    public RU02Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public RU02Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public RU02Parser(File file, SportService sports, EntityManager em) {
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

        if (qName.equalsIgnoreCase("comp")) {

            final long uuid = Long.valueOf(attributes.getValue("id"));
            final String yearLabel = attributes.getValue("season_id");
            tournament = sports.getSportTournament(uuid);
            if (tournament != null) {
                season = sports.getSeasonByYearLabel(tournament, yearLabel);
            }

        } else if (qName.equalsIgnoreCase("group")) {

            final String name = attributes.getValue("name");
            if (season != null) {
                stage = season.getStageByName(getStageNameFor(name));
                if (stage != null) {
                    final String tableType = "main_ranking";
                    if (stage.getTableByType(tableType) == null) {
                        table = new TournamentStageLeagueTableImpl(name,
                                tableType);
                        table.setUUID(randomGenerator.nextInt(1000000));
                        table.setKeys(getTableKeys());
                        ((TournamentStageLeagueTable) table)
                                .setTournamentStage(stage);
                        stage.addTable((TournamentStageLeagueTable) table);
                        tables.add(table);
                        rows = new ArrayList<TournamentLeagueTableRow>();
                    } else {
                        table = stage.getTableByType(tableType);
                        rows = table.getRows();
                    }
                }
            }

        } else if (qName.equalsIgnoreCase("team")) {

            if (table != null) {

                String teamUUID = attributes.getValue("id");
                if (teamUUID != null) {
                    teamUUID = TEAM_PREFIX + teamUUID;
                }
                if (sports != null
                        && sports.getSportContestantTeam(teamUUID) != null) {
                    team = sports.getSportContestantTeam(teamUUID);
                }
                if (team != null) {
                    TournamentLeagueTableRow row = null;
                    if (table.getRowFor(team) != null) {
                        row = table.getRowFor(team);
                    } else {
                        row = new TournamentLeagueTableRowImpl();
                        row.setSportContestant(team);
                        row.setLeagueTable(table);
                    }
                    rows.add(row);
                    List<TournamentLeagueTableKey> keys = getTableKeys();
                    for (TournamentLeagueTableKey key : keys) {
                        final String k = key.getKey();
                        final String v = attributes.getValue(k);
                        TournamentLeagueTableColumn column = null;
                        if (row.getColumn(k) != null) {
                            column = row.getColumn(k);
                            column.setColumnValue(v);
                        } else {
                            column = new TournamentLeagueTableColumnImpl();
                            column.setColumnKey(k);
                            column.setColumnValue(v);
                            row.addColumn(column);
                            column.setRow(row);
                        }
                    }
                }
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("group")) {
            if (table != null) {
                table.setRows(rows);
            }
        }

    }

    private List<TournamentLeagueTableKey> getTableKeys() {

        List<TournamentLeagueTableKey> keys = new ArrayList<TournamentLeagueTableKey>();

        keys.add(new TournamentLeagueTableKeyImpl("against", "against"));
        keys.add(new TournamentLeagueTableKeyImpl("bonus", "bonus"));
        keys.add(new TournamentLeagueTableKeyImpl("drawn", "drawn"));
        keys.add(new TournamentLeagueTableKeyImpl("for", "for"));
        keys.add(new TournamentLeagueTableKeyImpl("losingbonus", "losingbonus"));
        keys.add(new TournamentLeagueTableKeyImpl("lost", "lost"));
        keys.add(new TournamentLeagueTableKeyImpl("name", "name"));
        keys.add(new TournamentLeagueTableKeyImpl("played", "played"));
        keys.add(new TournamentLeagueTableKeyImpl("points", "points"));
        keys.add(new TournamentLeagueTableKeyImpl("pointsdiff", "pointsdiff"));
        keys.add(new TournamentLeagueTableKeyImpl("rank", "rank"));
        keys.add(new TournamentLeagueTableKeyImpl("triesagainst",
                "triesagainst"));
        keys.add(new TournamentLeagueTableKeyImpl("triesbonus", "triesbonus"));
        keys.add(new TournamentLeagueTableKeyImpl("triesfor", "triesfor"));
        keys.add(new TournamentLeagueTableKeyImpl("won", "won"));

        return keys;

    }

    public Tournament getTournament() {
        return tournament;
    }

    public TournamentSeason getSeason() {
        return season;
    }

    public List<TournamentLeagueTable> getTables() {
        return tables;
    }

    /**
     * Hack to link tables to round since RU1 and RU2 do not provide the same
     * name.
     * 
     * @param name: name within RU2
     * @return
     */
    private String getStageNameFor(String name) {

        // Work around wrong binding in between RU1 and RU2 for WC 2011
        if (season.getTournament().getUUID() == 210) {
            if (name.equals("Pool A")) {
                return "A";
            } else if (name.equals("Pool B")) {
                return "B";
            } else if (name.equals("Pool C")) {
                return "C";
            } else if (name.equals("Pool D")) {
                return "D";
            }
        }

        return name;
    }
}
