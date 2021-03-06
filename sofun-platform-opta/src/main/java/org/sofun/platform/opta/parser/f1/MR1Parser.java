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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
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
 * MR1 Formula One Live Race
 * 
 * <p>
 * 
 * This feed is essentially a live race overview feed for a given Grand prix.
 * 
 * <p>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta will deliver a feed live upon the following events:
 * 
 * <ul>
 * 
 * <li>Driver position</li>
 * <li>Laps so far</li>
 * <li>Pit stops</li>
 * <li>Individual driver status (on track, In pit, Retired etc)</li>
 * <li>Status of race (Pre, Live, Finished)</li>
 * <li>Points per race</li>
 * <ul>
 * 
 * This feed can also be a taken at the end of each race and will show:
 * 
 * <ul>
 * <li>Driver position</li>
 * <li>Total number of laps</li>
 * <li>Final time of winning driver and then difference between leader and every
 * other driver</li>
 * <li>Individual driver with the fastest lap</li>
 * <li>Total number of pit stops</li>
 * <li>Individual driver status (on track, In pit, Retired etc)</li>
 * <li>Status of race (Pre, Live, Finished)</li>
 * <ul>
 * 
 * Please note that live timings are available per lap but only to clients who
 * have official broadcast rights. This was a policy brought in by the FIA in
 * the 2010 season.
 * 
 * The File naming convention used for this feed is the following:
 * 
 * <p>
 * Official rights holders
 * </p>
 * <ul>
 * <li>F1_RACE <comp_race_number>year_month_day>.xml E.g.
 * F1_RACE_23_20100212.xml</li>
 * </ul>
 * 
 * <p>
 * Non official rights holders
 * </p>
 * <ul>
 * <li>F1_RACE <comp _race_number>year_month_day>_NT.xml E.g.
 * F1_RACE_23_20100212_NT.xml</li>
 * </ul>
 * 
 * Based on version specification documentation dated from 16 April 2012
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class MR1Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(MR1Parser.class);

    public static final String FAST_TIME_DRIVER_UUID = "fast_time_driver_uuid";

    public static final String SESSION_TYPE_RACE = "RACE";

    public static final String SESSION_TYPE_FP1 = "FP1";

    public static final String SESSION_TYPE_FP2 = "FP2";

    public static final String SESSION_TYPE_FP3 = "FP3";

    public static final String SESSION_TYPE_QUALI = "QUALI";

    public static final String TABLE_TYPE_DRIVERS = "DRIVERS";

    public static final String TRACK_STATUS_FINISHED = "FINISHED";

    public static final String sportName = "F1";

    public static final String PREFIX_DRIVER = "f1d";

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentStage stage;

    private TournamentRound round;

    private TournamentRoundLeagueTable table;

    private SportService sports;

    @SuppressWarnings("unused")
    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    /* Grand prix number */
    private String gpno;

    /* Weekend race stage (Race, Quali, FP1/2/3) */
    private String session;

    /* Race date */
    private String date;

    /* Race start time */
    private String start;

    /* Race end time */
    private String end;

    /*
     * universal coordinated time. This will be the time difference around the
     * world compared to GMT.
     */
    private String utc;

    /* Country code */
    private String iso_country;

    /* Country name where race is held */
    private String country;

    /* Race track name */
    private String racetrack;

    /* Track length in kilometers */
    private String track_length;

    /* Number of race laps */
    private String laps;

    /* Distance of race in kilometers */
    private String race_distance;

    /* Status of the race */
    private String track_status;

    /* Language */
    private String lang;

    /* The driver unique ID number we have assigned. */
    private String didx;

    /* Current/finished position in the race */
    private String pos;

    /*
     * The driver with the fastest lap time of the race always tagged along with
     * that driver.
     */
    private String fast_time;

    /*
     * Number of points accumulated for that race before penalty or bonus points
     * have been taken account of.
     */
    private String racePoints;

    /*
     * Total number of points accumulated after penalty or bonus points have
     * been taken into account.
     */
    private String points;

    /*
     * Number of bonus points accumulated from that race.
     */
    private String bonus;

    /*
     * Number of penalty points accumulated from that race.
     */
    private String penalty;

    public MR1Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public MR1Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public MR1Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.em = em;
        this.sports = sports;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("Block")) {
            tournament = sports.getSportTournamentByCode(sportName);
            season = sports.getActiveSeasonFor(tournament);
        } else if (qName.equals("param")) {
            final String name = attributes.getValue("name");
            final String value = attributes.getValue("value");
            if (name != null && value != null) {
                if (name.equals("gpno")) {
                    gpno = value;
                } else if (name.equals("session")) {
                    session = value;
                } else if (name.equals("date")) {
                    date = computeDate(value.trim());
                } else if (name.equals("start")) {
                    start = value;
                } else if (name.equals("end")) {
                    end = value;
                } else if (name.equals("utc")) {
                    utc = value;
                } else if (name.equals("iso_country")) {
                    iso_country = value;
                } else if (name.equals("country")) {
                    country = value;
                } else if (name.equals("racetrack")) {
                    racetrack = value;
                } else if (name.equals("track_length")) {
                    track_length = value;
                } else if (name.equals("track_status")) {
                    track_status = value;
                } else if (name.equals("lang")) {
                    lang = value;
                }
            }
        } else if (qName.equals("points")) {
            racePoints = attributes.getValue("race");
            bonus = attributes.getValue("bonus");
            penalty = attributes.getValue("penalty");
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("param")) {

            // Lang is the last one in spec for nested <param><param/></param>
            if (lang != null) {
                stage = season.getStageByName(gpno);
                if (stage != null) {
                    round = stage.getRoundByLabel(session);
                    table = round.getTableByType(TABLE_TYPE_DRIVERS);
                    if (table == null) {
                        table = new TournamentRoundLeagueTableImpl(
                                season.getName(), TABLE_TYPE_DRIVERS);
                        table.setUUID(randomGenerator.nextInt(1000000));
                        table.setKeys(getTableKeys());
                        table.setTournamentRound(round);
                        round.addTable(table);
                    }

                    if (TRACK_STATUS_FINISHED.equals(track_status)) {
                        if (!TournamentRoundStatus.TERMINATED.equals(round
                                .getStatus())) {
                            round.setStatus(TournamentRoundStatus.TERMINATED);
                            log.info("Round with uuid=" + round.getUUID()
                                    + " marked as terminated by Opta");
                        }
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "dd.MM.yyyy HH:mm");
                    if (TournamentRoundStatus.SCHEDULED.equals(round
                            .getStatus())) {
                        if (!start.isEmpty() && !date.isEmpty()) {
                            Date startDate;
                            try {
                                startDate = sdf.parse(date + " " + start);
                            } catch (ParseException e) {
                                throw new SAXException(e);
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(startDate);
                            if (utc.contains(".")) {
                                final String[] utcF = utc.split("\\.");
                                cal.add(Calendar.HOUR,
                                        -Integer.valueOf(utcF[0]));
                                if (utcF.length == 2) {
                                    cal.add(Calendar.MINUTE,
                                            -60 * Integer.valueOf(utcF[1]) / 10);
                                }
                            } else {
                                cal.add(Calendar.HOUR, -Integer.valueOf(utc));
                            }
                            round.setStartDate(cal.getTime());
                        }
                    }

                    if (!end.isEmpty() && !date.isEmpty()) {
                        Date endDate;
                        try {
                            endDate = sdf.parse(date + " " + end);
                        } catch (ParseException e) {
                            throw new SAXException(e);
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        if (utc.contains(".")) {
                            final String[] utcF = utc.split("\\.");
                            cal.add(Calendar.HOUR, -Integer.valueOf(utcF[0]));
                            if (utcF.length == 2) {
                                cal.add(Calendar.MINUTE,
                                        -60 * Integer.valueOf(utcF[1]) / 10);
                            }
                        } else {
                            cal.add(Calendar.HOUR, -Integer.valueOf(utc));
                        }
                        round.setEndDate(cal.getTime());
                    }

                    Map<String, String> props = round.getProperties();
                    props.put("iso_country", iso_country);
                    props.put("country", country);
                    props.put("track_length", track_length);
                    props.put("laps", laps);
                    props.put("race_distance", race_distance);
                    props.put("status", track_status);
                    props.put("racetrack", racetrack);
                    props.put("lang", lang);
                    round.setProperties(props);
                } else {
                    log.warn("Can't find stage with with name=" + gpno
                            + " for season with name=" + season.getName());
                }

            }

        } else if (qName.equals("didx")) {
            didx = tempVal;
        } else if (qName.equalsIgnoreCase("pos")) {
            pos = tempVal;
        } else if (qName.equalsIgnoreCase("fast_time")) {
            fast_time = tempVal;
        } else if (qName.equalsIgnoreCase("driver")) {
            if (!didx.isEmpty() && table != null) {
                final String dUUID = PREFIX_DRIVER + didx;
                SportContestant contestant = sports
                        .getSportContestantPlayer(dUUID);
                if (contestant != null) {
                    if (fast_time != null && !fast_time.isEmpty()) {
                        Map<String, String> props = round.getProperties();
                        props.put(FAST_TIME_DRIVER_UUID, contestant.getUUID());
                        round.setProperties(props);
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
                    column = row.getColumn("penalty");
                    if (column == null) {
                        column = new TournamentLeagueTableColumnImpl();
                        column.setColumnKey("penalty");
                        row.addColumn(column);
                        column.setRow(row);
                    }
                    column.setColumnValue(penalty);
                    column = row.getColumn("bonus");
                    if (column == null) {
                        column = new TournamentLeagueTableColumnImpl();
                        column.setColumnKey("bonus");
                        row.addColumn(column);
                        column.setRow(row);
                    }
                    column.setColumnValue(bonus);
                    column = row.getColumn("race_points");
                    if (column == null) {
                        column = new TournamentLeagueTableColumnImpl();
                        column.setColumnKey("race_points");
                        row.addColumn(column);
                        column.setRow(row);
                    }
                    column.setColumnValue(racePoints);
                } else {
                    log.warn("Contestant with uuid=" + dUUID + " not found...");
                }
            }
            // Reset: only one driver has it
            fast_time = null;
        } else if (qName.equals("points")) {
            points = tempVal;
        }

    }

    private String computeDate(String date) {
        String computed = date;
        String[] split = date.split("\\.");
        if (split.length == 3) {
            // Opta bug (.12 vs .2012)
            if (split[2].length() == 2) {
                computed = split[0] + "." + split[1] + ".20" + split[2];
            }
        }
        return computed;
    }

    private List<TournamentLeagueTableKey> getTableKeys() {
        final List<TournamentLeagueTableKey> keys = new ArrayList<TournamentLeagueTableKey>();
        keys.add(new TournamentLeagueTableKeyImpl("pos", "pos"));
        keys.add(new TournamentLeagueTableKeyImpl("points", "points"));
        keys.add(new TournamentLeagueTableKeyImpl("penalty", "penalty"));
        keys.add(new TournamentLeagueTableKeyImpl("bonus", "bonus"));
        keys.add(new TournamentLeagueTableKeyImpl("race_points", "race_points"));
        return keys;
    }

    public TournamentRound getRound() {
        return round;
    }

}
