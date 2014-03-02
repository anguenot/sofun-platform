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

package org.sofun.platform.opta.parser.f1;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.api.sport.tournament.table.TournamentSeasonLeagueTable;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableColumnImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableKeyImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableRowImpl;
import org.sofun.core.sport.tournament.table.TournamentSeasonLeagueTableImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * MR4 Formula One Driver Standings Specification
 * 
 * <p/>
 * 
 * This is essentially a live driver standings feed cumulatively updated after
 * each Grand Prix.
 * 
 * <p/>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta will deliver a post race standings feed upon the following
 * events:
 * <ul>
 * <li>Driver final position Driver</li>
 * <li>final standings points</li>
 * <ul>
 * 
 * Opta can also deliver a live standings feed upon the following events:
 * 
 * <ul>
 * <li>Changing driver position during race</li>
 * </ul>
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following:
 * 
 * <p>
 * Non live
 * </p>
 * <ul>
 * <li>F1_STANDINGS_DRIVER_YEAR_NT.xml (e.g. F1_STANDINGS_DRIVER_2010_NT.xml)</li>
 * <ul>
 * 
 * <p>
 * Live
 * </p>
 * <ul>
 * <li>F1_STANDINGS_DRIVER_LIVE_YEAR.xml (e.g.
 * F1_STANDINGS_DRIVER_LIVE_2010.xml)</li>
 * </ul>
 * 
 * <p/>
 * 
 * Based on version specification documentation dated from 28 February 2012
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class MR4Parser extends AbstractOptaParser {

    public static final String TABLE_TYPE_LIVE = "DRIVERS_LIVE";

    public static final String TABLE_TYPE_POST_RACE = "DRIVERS_POST_RACE";

    public static final String sportName = "F1";

    public static final String PREFIX_DRIVER = "f1d";

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentSeasonLeagueTable table;

    private String tableType = TABLE_TYPE_POST_RACE; // default

    private String seasonLabel;

    private SportService sports;

    private EntityManager em;

    /* The driver unique ID number we have assigned. */
    private String didx;

    /* Current/finished position in the race */
    private String pos;

    /* Starting number on the grid */
    protected String stn;

    /* Driver name */
    private String dname;

    /* Driver forename name */
    private String driver_first_name;

    /* Driver last name */
    private String driver_last_name;

    /* 3 letter driver code value. E.g. Hamilton= “HAM” Vettel= “VET” */
    private String driver_code;

    /* Nationality */
    private String nat;

    /* Unique Opta team ID which can be attained from the MR9 file. */
    private String tid;

    /* Team name */
    private String team;

    /* Car make */
    private String team_carbike;

    /* Engine name */
    private String team_engine;

    /* Total season points */
    private String points;

    /* XXX */
    private String victories;

    private final SecureRandom randomGenerator = new SecureRandom();

    public MR4Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public MR4Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public MR4Parser(File file, SportService sports, EntityManager em,
            String seasonLabel, String type) {
        super(file);
        this.em = em;
        this.sports = sports;
        this.seasonLabel = seasonLabel;
        parseXmlFile();
        this.tableType = type;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("Block")) {

            tournament = sports.getSportTournamentByCode(sportName);
            season = sports.getSeasonByYearLabel(tournament, seasonLabel);
            table = season.getTableByType(tableType);
            if (table == null) {
                table = new TournamentSeasonLeagueTableImpl(season.getName(),
                        tableType);
                table.setUUID(randomGenerator.nextInt(1000000));
                table.setKeys(getTableKeys());
                table.setTournamentSeason(season);
                season.addTable(table);
            }

        }

    }

    private List<TournamentLeagueTableKey> getTableKeys() {
        final List<TournamentLeagueTableKey> keys = new ArrayList<TournamentLeagueTableKey>();
        keys.add(new TournamentLeagueTableKeyImpl("pos", "pos"));
        keys.add(new TournamentLeagueTableKeyImpl("points", "points"));
        keys.add(new TournamentLeagueTableKeyImpl("victories", "victories"));
        return keys;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equals("didx")) {
            didx = tempVal;
        } else if (qName.equalsIgnoreCase("pos")) {
            pos = tempVal;
        } else if (qName.equalsIgnoreCase("stn")) {
            stn = tempVal;
        } else if (qName.equalsIgnoreCase("driver")) {
            dname = tempVal;
        } else if (qName.equalsIgnoreCase("driver_first_name")) {
            driver_first_name = tempVal;
        } else if (qName.equalsIgnoreCase("driver_last_name")) {
            driver_last_name = tempVal;
        } else if (qName.equalsIgnoreCase("driver_code")) {
            driver_code = tempVal;
        } else if (qName.equalsIgnoreCase("nat")) {
            nat = tempVal;
        } else if (qName.equalsIgnoreCase("tid")) {
            tid = tempVal;
        } else if (qName.equalsIgnoreCase("team")) {
            team = tempVal;
        } else if (qName.equalsIgnoreCase("team_carbike")) {
            team_carbike = tempVal;
        } else if (qName.equalsIgnoreCase("team_engine")) {
            team_engine = tempVal;
        } else if (qName.equalsIgnoreCase("points")) {
            points = tempVal;
        } else if (qName.equalsIgnoreCase("victories")) {
            victories = tempVal;
        } else if (qName.equalsIgnoreCase("standings")) {

            final String dUUID = PREFIX_DRIVER + didx;
            SportContestant contestant = sports.getSportContestantPlayer(dUUID);
            if (contestant == null) {
                contestant = new SportContestantImpl(dUUID,
                        SportContestantType.INDIVIDUAL);
                em.persist(contestant);
            }
            contestant.setName(dname);
            contestant.setGivenName(driver_first_name);
            contestant.setLastName(driver_last_name);

            Map<String, String> props = new HashMap<String, String>();
            props.put("nat", nat);
            props.put("driver_code", driver_code);
            contestant.setProperties(props);

            final String tUUID = MR5Parser.PREFIX_TEAM + tid;
            SportContestant tDriver = sports.getSportContestantTeam(tUUID);
            if (tDriver == null) {
                tDriver = new SportContestantImpl(tUUID,
                        SportContestantType.TEAM);
                em.persist(tDriver);
            }
            tDriver.setName(team);
            props = new HashMap<String, String>();
            props.put("team_carbike", team_carbike);
            props.put("team_engine", team_engine);
            tDriver.setProperties(props);

            if (!contestant.getTeams().contains(tDriver)) {
                contestant.addTeam(tDriver);
                tDriver.addPlayer(contestant);
            }

            TournamentLeagueTableRow row = table.getRowFor(contestant);
            if (row == null) {
                row = new TournamentLeagueTableRowImpl();
                row.setSportContestant(contestant);
                row.setLeagueTable(table);
                table.addRow(row);
            }

            TournamentLeagueTableColumn column = row.getColumn("pos");
            if (column == null) {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey("pos");
                row.addColumn(column);
                column.setRow(row);
            }
            column.setColumnValue(pos);

            column = row.getColumn("points");
            if (column == null) {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey("points");
                row.addColumn(column);
                column.setRow(row);
            }
            column.setColumnValue(points);

            column = row.getColumn("victories");
            if (column == null) {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey("victories");
                row.addColumn(column);
                column.setRow(row);
            }
            column.setColumnValue(victories);

        }

    }

    public TournamentSeason getSeason() {
        return season;
    }

}
