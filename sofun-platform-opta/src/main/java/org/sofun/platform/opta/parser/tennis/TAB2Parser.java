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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.sport.tournament.TournamentGameImpl;
import org.sofun.core.sport.tournament.TournamentRoundImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * TAB2 Tennis Competition
 * 
 * <p>
 * 
 * The purpose of this feed is to give each customer a competition schedule list
 * for a given ATP/WTA/Grand Slam tournament.
 * 
 * <p>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta will deliver this feed once this information is officially
 * released by the competition organizers. This will normally be available at
 * the end of each day’s play. The feed will subsequently be triggered again if
 * there is a change in proceedings
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following: - TAB2-
 * (competition_id)-(season_id).xml e.g. TAB2-7106-2010.xml
 * 
 * <p>
 * 
 * Based on version specification documentation dated from 05 January 2011
 * 
 * @author @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TAB2Parser extends AbstractOptaParser {

    public static final String sportName = "Tennis";

    /* Prefix for game UUID */
    public static final String PREFIX = "TAB";

    private SportService sports;

    private Tournament tournament;

    private TournamentSeason tournamentSeason;

    private TournamentStage tournamentStage;

    private TournamentRound tournamentRound;

    private TournamentGame tournamentGame;

    /* The tournament type */
    private String type;

    /* The tournament unique tournament ID number. */
    private String id;

    /* The tournament end date. YYYY-MM-DD */
    private String end_date;

    /* The tournament start time of the tournament. YYYYMM-DD-HH-MM-SS */
    private String start_time;

    public TAB2Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public TAB2Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public TAB2Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.sports = sports;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        if (qName.equals("tournament")) {

            type = attributes.getValue("type");
            end_date = attributes.getValue("end_date");
            start_time = attributes.getValue("start_time");
            id = attributes.getValue("id");

            tournament = sports.getSportTournamentByCode(type);
            tournamentSeason = sports.getSeasonByUUID(tournament,
                    Long.valueOf(id));

            final String tzoffset = "UTC"
                    + attributes.getValue("time_zone_offset");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone(tzoffset));
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

            final String cid = attributes.getValue("id");
            tournamentStage = sports.getTournamentStage(Long.valueOf(cid));

        } else if (qName.equals("round")) {

            final String id = attributes.getValue("id");
            final String name = attributes.getValue("name");
            final String number = attributes.getValue("number");

            tournamentRound = tournamentStage.getRoundByUUID(Long.valueOf(id));
            if (tournamentRound == null) {
                tournamentRound = new TournamentRoundImpl(Long.valueOf(id));
                tournamentStage.addRound(tournamentRound);
                tournamentRound.setStage(tournamentStage);
            }
            tournamentRound.setName(name);
            tournamentRound.setDescription(tournamentStage.getName());
            tournamentRound.setRoundLabel(name);
            tournamentRound.setRoundNumber(Integer.valueOf(number));
        } else if (qName.equals("match")) {

            final String id = attributes.getValue("id");
            final String start = attributes.getValue("start_time");

            final String gameUUID = PREFIX + id;
            tournamentGame = sports.getTournamentGame(gameUUID);
            if (tournamentGame == null) {
                tournamentGame = new TournamentGameImpl(gameUUID);
                tournamentGame.setRound(tournamentRound);
                tournamentRound.addGame(tournamentGame);
            }

            // XXX FIX TZ
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date startDate;
            try {
                startDate = sdf.parse(start);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            tournamentGame.setStartDate(startDate);

            Date now = new Date();
            if (now.compareTo(startDate) < 0) {
                tournamentGame.setGameStatus(TournamentGameStatus.SCHEDULED);
            } else if (now.compareTo(startDate) > 0) {
                tournamentGame.setGameStatus(TournamentGameStatus.TERMINATED);
            }
            tournamentGame.setDescription(tournamentRound.getDescription()
                    + " - " + tournamentRound.getName());

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    }

}
