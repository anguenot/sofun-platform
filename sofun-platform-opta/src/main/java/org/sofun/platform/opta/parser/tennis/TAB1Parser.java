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

package org.sofun.platform.opta.parser.tennis;

import java.io.File;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.sport.SportImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * TAB1 Tennis Season Schedule
 * 
 * <p>
 * 
 * The purpose of the season schedule feed is to provide the customer with a
 * schedule of the WTA/ATP and Grand Slam events in the upcoming season.
 * 
 * <p>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta will deliver a feed at the start of the season and where
 * necessary should there be a change in tournament information/date etc. This
 * information will become available after the official dates are announced
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following: -
 * TAB1-(season ID).xml
 * 
 * <p>
 * 
 * Based on version specification documentation dated from 05 January 2011
 * 
 * @author @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TAB1Parser extends AbstractOptaParser {

    public static final String sportName = "Tennis";

    private SportService sports;

    private Tournament tournament;

    private TournamentSeason tournamentSeason;

    private TournamentStage tournamentStage;

    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    /* The relevant year of that season */
    private String season;

    /* The tournament type */
    private String type;

    /* The tournament end date. YYYY-MM-DD */
    private String end_date;

    /* The tournament start time of the tournament. YYYYMM-DD-HH-MM-SS */
    private String start_time;

    /*
     * The tournament specific tournament name i.e. ‘Wimbledon 2010’ or ‘Chennai
     * 2010’ etc.
     */
    private String name;

    /* The tournament unique tournament ID number. */
    private String id;

    /* The tournament type of surface (carpet, hard, clay and grass) */
    private String surface;

    /*
     * The tournament location around the world e.g. London, England or
     * Brisbane, Australia etc.
     */
    private String location;

    /* The gender of the competing players. ‘Mens’ or ‘Womens’ */
    private String competitionSex;

    /*
     * This relates to the event type. This can be Men’s singles, women’s
     * singles or both. Please note doubles matches are not available.
     */
    private String competitionName;

    /* The unique ID number related to that event and that competition name */
    private String competitionId;

    public TAB1Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public TAB1Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public TAB1Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.em = em;
        this.sports = sports;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("season")) {

            season = attributes.getValue("year");

        } else if (qName.equals("tournament")) {

            type = attributes.getValue("type");
            end_date = attributes.getValue("end_date");
            surface = attributes.getValue("surface");
            start_time = attributes.getValue("start_time");
            name = attributes.getValue("name");
            id = attributes.getValue("id");
            location = attributes.getValue("location");

            if (sports == null || sports.getSportTournamentByCode(type) == null) {
                final long tUUID = randomGenerator.nextInt(1000000);
                final String tournamentName = sportName + " - " + type;
                tournament = new TournamentImpl(tournamentName, tUUID);
                tournament.setCode(type);
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
                tournament = sports.getSportTournamentByCode(type);
            }

            final String seasonName = tournament.getName() + " - " + name;
            if (sports == null
                    || sports.getSeasonByUUID(tournament, Long.valueOf(id)) == null) {
                if (sports.getTournamentSeason(Long.valueOf(id)) == null) {
                    tournamentSeason = new TournamentSeasonImpl(
                            Long.valueOf(id));
                    tournamentSeason.setYearLabel(season);
                    tournamentSeason.setTournament(tournament);
                    tournament.addSeason(tournamentSeason);
                } else {
                    tournamentSeason = sports.getTournamentSeason(Long
                            .valueOf(id));
                }
            } else {
                tournamentSeason = sports.getSeasonByUUID(tournament,
                        Long.valueOf(id));
            }
            tournamentSeason.setName(seasonName);

            // XXX FIX TZ
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date startDate;
            try {
                startDate = sdf.parse(start_time);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            tournamentSeason.setStartDate(startDate);

            sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate;
            try {
                endDate = sdf.parse(end_date);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            cal = Calendar.getInstance();
            cal.setTime(endDate);
            tournamentSeason.setEndDate(endDate);

            Date now = new Date();
            if (now.compareTo(endDate) > 0) {
                tournamentSeason.setStatus(TournamentSeasonStatus.TERMINATED);
            } else if (now.compareTo(startDate) < 0) {
                tournamentSeason.setStatus(TournamentSeasonStatus.SCHEDULED);
            } else if (now.compareTo(startDate) >= 0
                    && now.compareTo(endDate) < 0) {
                tournamentSeason.setStatus(TournamentSeasonStatus.ON_GOING);
            }

        } else if (qName.equals("competition")) {

            competitionSex = attributes.getValue("sex");
            competitionName = attributes.getValue("name");
            competitionId = attributes.getValue("id");

            final String stageName = tournamentSeason.getName() + " - "
                    + competitionName;
            tournamentStage = tournamentSeason.getStageByName(stageName);
            if (tournamentStage == null) {
                tournamentStage = new TournamentStageImpl(
                        Long.valueOf(competitionId));
                tournamentStage.setName(stageName);
                tournamentStage.setStatus(TournamentStageStatus.SCHEDULED);
                tournamentSeason.addStage(tournamentStage);
                tournamentStage.setSeason(tournamentSeason);
            }
            tournamentStage.setDescription(competitionSex);
            Map<String, String> properties = tournamentStage.getProperties();
            properties.put("location", location);
            properties.put("surface", surface);
            properties.put("sex", competitionSex);
            tournamentStage.setProperties(properties);

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("tournament")) {
            type = "";
            end_date = "";
            surface = "";
            start_time = "";
            name = "";
            id = "";
            location = "";
            tournamentSeason = null;
            tournament = null;
        } else if (qName.equalsIgnoreCase("tournament")) {
            competitionSex = "";
            competitionName = "";
            competitionId = "";
            tournamentStage = null;
        }
    }

}
