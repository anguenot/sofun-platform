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

package org.sofun.platform.opta.parser.cy;

import java.io.File;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * CY1 Cycling Fixtures (CNC)
 * 
 * <p>
 * 
 * This feed offers stage by stage schedules / fixtures for cycling events.
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following: CNC_
 * -{competition_id}.xml
 * 
 * <p>
 * 
 * Based on version 1.1 specification documentation dated from May 2012
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class CY1Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(CY1Parser.class);

    public static final String sportName = "Cycling";

    /* Prefix for UUID */
    public static final String PREFIX = "CY";

    private SportService sports;

    private Tournament tournament;

    private TournamentSeason tournamentSeason;

    private TournamentStage tournamentStage;

    private final SecureRandom randomGenerator = new SecureRandom();

    private EntityManager em;

    public CY1Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public CY1Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public CY1Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.sports = sports;
        this.em = em;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        if (qName.equals("DG")) {

            /* Competition /race identifier */
            final String ID = attributes.getValue("ID");

            /* Competition /race name (i.e. Tour de France) */
            final String DES = attributes.getValue("DES");

            /* Competition Type. 7 = By stages */
            attributes.getValue("TIP");

            /* Sport Code. For Cycling this is 9. Female= 109 */
            attributes.getValue("DEP");

            /*
             * Unique competition code across the seasons. Together the
             * attribute TEMP can be the ID of the competition
             */
            final String IDUNICO = attributes.getValue("IDUNICO");

            /* Year in four digits in which the competition starts */
            final String TEMP = attributes.getValue("TEMP");

            final String tournamentName = sportName + " - " + DES;
            if (sports == null
                    || sports.getSportTournamentByCode(IDUNICO) == null) {
                final long tUUID = randomGenerator.nextInt(1000000);
                tournament = new TournamentImpl(tournamentName, tUUID);
                tournament.setCode(IDUNICO);
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
                tournament = sports.getSportTournamentByCode(IDUNICO);
                tournament.setName(tournamentName);
            }

            final String seasonName = tournament.getName() + " - " + TEMP;
            if (sports == null
                    || sports.getSeasonByUUID(tournament, Long.valueOf(ID)) == null) {
                tournamentSeason = new TournamentSeasonImpl(Long.valueOf(ID));
                tournamentSeason.setYearLabel(TEMP);
                tournamentSeason.setTournament(tournament);
                tournament.addSeason(tournamentSeason);
            } else {
                tournamentSeason = sports.getSeasonByUUID(tournament,
                        Long.valueOf(ID));
            }
            tournamentSeason.setName(seasonName);

            // Season status will be handled by the sports graph manager.

        } else if (qName.equals("Jor")) {

            /* The ID of the stage. */
            final String ID = attributes.getValue("ID");

            /* Date of the stage. */
            final String FEC = attributes.getValue("FEC");

            tournamentStage = tournamentSeason.getStageByName(ID);
            if (tournamentStage == null) {
                tournamentStage = new TournamentStageImpl(
                        randomGenerator.nextInt(1000000));
                // We use the uuid as the name to ease lookup
                tournamentStage.setName(ID);
                tournamentStage.setStatus(TournamentStageStatus.SCHEDULED);
                tournamentSeason.addStage(tournamentStage);
                tournamentStage.setSeason(tournamentSeason);
            }
            tournamentStage.setDescription(tournamentSeason.getName() + " - "
                    + ID);

            // i.e : FEC = 04.07.2012
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date startDate;
            try {
                startDate = sdf.parse(FEC);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            tournamentStage.setStartDate(startDate);

            // Stagte status will be handle by the sports graph manager.

        } else if (qName.equals("Par")) {

            /* The ID of the stage */
            final String ID = attributes.getValue("ID");

            /* Name of the stage */
            final String EL = attributes.getValue("EL");

            attributes.getValue("FEC");

            /* Date & time in UTC */
            final String TUTC = attributes.getValue("TUTC");

            /* Stage Status. This shows the current status of the stage. */
            /* 0 = not started, 1 = in progress, 7 = complete. */
            final String CES = attributes.getValue("CES");

            TournamentRound round = tournamentStage.getRoundByLabel(ID);
            if (round == null) {
                round = new TournamentRoundImpl(
                        randomGenerator.nextInt(1000000));
                round.setName(EL); // to ease lookup
                round.setRoundLabel(ID);
                round.setRoundNumber(Integer.valueOf(ID));
                tournamentStage.addRound(round);
                round.setStage(tournamentStage);
            }

            round.setDescription(tournamentSeason.getName() + " "
                    + tournamentStage.getName());

            /* 2012-07-05T10:30:00 */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date startDate;
            try {
                startDate = sdf.parse(TUTC);
            } catch (ParseException e) {
                throw new SAXException(e);
            }
            round.setStartDate(startDate);

            if ("0".equals(CES)) {
                if (!TournamentRoundStatus.SCHEDULED.equals(round.getStatus())) {
                    round.setStatus(TournamentRoundStatus.SCHEDULED);
                    log.info("Marking round w/ UUID="
                            + String.valueOf(round.getUUID()) + " as "
                            + TournamentRoundStatus.SCHEDULED);
                }
            } else if ("1".equals(CES)) {
                if (!TournamentRoundStatus.ON_GOING.equals(round.getStatus())) {
                    round.setStatus(TournamentRoundStatus.ON_GOING);
                    log.info("Marking round w/ UUID="
                            + String.valueOf(round.getUUID()) + " as "
                            + TournamentRoundStatus.ON_GOING);
                }
            } else if ("7".equals(CES)) {
                if (!TournamentRoundStatus.TERMINATED.equals(round.getStatus())) {
                    round.setStatus(TournamentRoundStatus.TERMINATED);
                    log.info("Marking round w/ UUID="
                            + String.valueOf(round.getUUID()) + " as "
                            + TournamentRoundStatus.TERMINATED);
                }
            } else {
                log.warn("Round w/ UUID=" + round.getUUID()
                        + " has unknown status");
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    }

}
