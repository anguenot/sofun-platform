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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.sport.SportImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentRoundImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * MR6 - Formula Calendar
 * 
 * <p/>
 * 
 * This is essentially a race schedule feed detailing the times/dates of each
 * Grand Prix weekend.
 * 
 * <p/>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta will deliver a feed following the official announcement of
 * the Formula one Drivers and Teams. This will normally take place 2 months
 * before the commencement of the new season.
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following :
 * F1_CALENDAR_<YEAR>.xml
 * 
 * <p/>
 * 
 * Based on version specification documentation dated from 10 January 2011
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class MR6Parser extends AbstractOptaParser {

    public static final String SESSION_TYPE_RACE = "RACE";

    public static final String SESSION_TYPE_FP1 = "FP1";

    public static final String SESSION_TYPE_FP2 = "FP2";

    public static final String SESSION_TYPE_FP3 = "FP3";

    public static final String SESSION_TYPE_QUALI = "QUALI";

    private static final String sportName = "F1";

    private static final String tournamentName = "Formula 1";

    private String seasonLabel;

    private Tournament tournament;

    private TournamentSeason season;

    /* Session Internal identifier */
    private String scheduleId;

    /* Grand prix number in the season 1-10 */
    private String gpno;

    /* Feeds for relative sessions (official race, qualifying, free practice) */
    private String session;

    /* Official date of session */
    private String dateStr; // 11.03.2012

    /* Race start time */
    private String start;

    /* Race end time */
    private String end;

    /*
     * Universal coordinated time. This will be the time difference around the
     * world compared to GMT.
     */
    private String utc;

    /* Country code */
    private String iso_country;

    /* Name of country */
    private String country;

    /* Race track name */
    private String racetrack;

    /* Race track length in km */
    private String track_length;

    /* Total number of laps for the race */
    private String laps;

    /* Total race distance in km */
    private String race_distance;

    /*
     * <status id="1">everything is fine</status>
     * 
     * <status id="2">event has been cancelled</status>
     * 
     * <status id="3">event has to be announced (T.B.A)</status>
     * 
     * <status id="4">subject to circuit approval</status>
     */
    private String status;

    private EntityManager em;

    private SportService sports;

    private final SecureRandom randomGenerator = new SecureRandom();

    public MR6Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public MR6Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public MR6Parser(File file, SportService sports, EntityManager em,
            String seasonLabel) {
        super(file);
        this.sports = sports;
        this.em = em;
        this.seasonLabel = seasonLabel;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("Block")) {

            if (sports == null
                    || sports.getSportTournamentByCode(sportName) == null) {

                final long tUUID = randomGenerator.nextInt(1000000);
                tournament = new TournamentImpl(tournamentName, tUUID);
                tournament.setCode(sportName);
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
                tournament = sports.getSportTournamentByCode(sportName);
            }

            final String seasonName = tournamentName + " - " + seasonLabel;

            if (sports == null
                    || sports.getSeasonByYearLabel(tournament, seasonLabel) == null) {
                final long sUUID = randomGenerator.nextInt(1000000);
                season = new TournamentSeasonImpl(sUUID);
                season.setYearLabel(seasonLabel);
                season.setTournament(tournament);
                tournament.addSeason(season);
            } else {
                season = sports.getSeasonByYearLabel(tournament, seasonLabel);
            }
            final String name = seasonName;
            season.setName(name);

        } else if (qName.equalsIgnoreCase("status")) {
            status = attributes.getValue("id");
        } else if (qName.equalsIgnoreCase("Schedule")) {
            scheduleId = attributes.getValue("id");
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("Schedule")) {
            TournamentStage stage = season.getStageByName(gpno);
            if (stage == null) {
                stage = new TournamentStageImpl(
                        randomGenerator.nextInt(1000000));
                // We use the uuid as the name to ease lookup
                stage.setName(gpno);
                stage.setStatus(TournamentStageStatus.SCHEDULED);
                season.addStage(stage);
                stage.setSeason(season);
            }
            stage.setDescription(racetrack);

            TournamentRound round = stage.getRoundByName(scheduleId);
            if (round == null) {
                round = new TournamentRoundImpl(
                        randomGenerator.nextInt(1000000));
                round.setName(scheduleId); // to ease lookup
                round.setDescription(season.getName() + " " + racetrack + " "
                        + session);
                round.setRoundLabel(session);
                round.setRoundNumber(Integer.valueOf(gpno));
                if ("1".equals(status)) {
                    round.setStatus(TournamentRoundStatus.SCHEDULED);
                } else if ("2".equals(status)) {
                    round.setStatus(TournamentRoundStatus.CANCELLED);
                } else {
                    round.setStatus(TournamentRoundStatus.POSTPONED);
                }
                stage.addRound(round);
                round.setStage(stage);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date startDate;
            try {
                startDate = sdf.parse(dateStr + " " + start);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            if (utc.contains(".")) {
                final String[] utcF = utc.split("\\.");
                cal.add(Calendar.HOUR, -Integer.valueOf(utcF[0]));
                if (utcF.length == 2) {
                    cal.add(Calendar.MINUTE, -60 * Integer.valueOf(utcF[1])
                            / 10);
                }
            } else {
                cal.add(Calendar.HOUR, -Integer.valueOf(utc));
            }
            round.setStartDate(cal.getTime());

            Date endDate;
            try {
                endDate = sdf.parse(dateStr + " " + end);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            cal = Calendar.getInstance();
            cal.setTime(endDate);
            if (utc.contains(".")) {
                final String[] utcF = utc.split("\\.");
                cal.add(Calendar.HOUR, -Integer.valueOf(utcF[0]));
                if (utcF.length == 2) {
                    cal.add(Calendar.MINUTE, -60 * Integer.valueOf(utcF[1])
                            / 10);
                }
            } else {
                cal.add(Calendar.HOUR, -Integer.valueOf(utc));
            }
            round.setEndDate(cal.getTime());

            Map<String, String> props = new HashMap<String, String>();
            props.put("iso_country", iso_country);
            props.put("country", country);
            props.put("track_length", track_length);
            props.put("laps", laps);
            props.put("race_distance", race_distance);
            props.put("status", status);

            stage = null;
        } else if (qName.equalsIgnoreCase("gpno")) {
            gpno = tempVal;
        } else if (qName.equalsIgnoreCase("session")) {
            session = tempVal;
        } else if (qName.equalsIgnoreCase("date")) {
            dateStr = tempVal;
        } else if (qName.equalsIgnoreCase("racetrack")) {
            racetrack = tempVal;
        } else if (qName.equalsIgnoreCase("start")) {
            start = tempVal;
        } else if (qName.equalsIgnoreCase("end")) {
            end = tempVal;
        } else if (qName.equalsIgnoreCase("utc")) {
            utc = tempVal;
        } else if (qName.equalsIgnoreCase("iso_country")) {
            iso_country = tempVal;
        } else if (qName.equalsIgnoreCase("country")) {
            country = tempVal;
        } else if (qName.equalsIgnoreCase("track_length")) {
            track_length = tempVal;
        } else if (qName.equalsIgnoreCase("laps")) {
            laps = tempVal;
        } else if (qName.equalsIgnoreCase("race_distance")) {
            race_distance = tempVal;
        }
    }

    public Tournament getTournament() {
        return tournament;
    }

    public TournamentSeason getSeason() {
        return season;
    }

}
