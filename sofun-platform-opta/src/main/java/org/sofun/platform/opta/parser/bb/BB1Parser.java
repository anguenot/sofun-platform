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

package org.sofun.platform.opta.parser.bb;

import java.io.File;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.SportImpl;
import org.sofun.core.sport.tournament.TournamentGameImpl;
import org.sofun.core.sport.tournament.TournamentImpl;
import org.sofun.core.sport.tournament.TournamentRoundImpl;
import org.sofun.core.sport.tournament.TournamentSeasonImpl;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * (BB1) Fixtures / Results
 * 
 * <p>
 * 
 * This feed is essentially a match list for a given subscribed Competition &
 * Season. This feed includes a reference to every match (past and present)
 * within a season.
 * 
 * <p>
 * 
 * It details the:
 * <ul>
 * <li>DateHome/Away teams (with id)</li>
 * <li>Venue (with id)</li>
 * <li>Score</li>
 * </ul>
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following:
 * 
 * BB1-{competition_id}-{season_id}-{sport_id}.xml
 * 
 * </p>
 * 
 * <p>
 * 
 * Implementation based on version from 2012/06/06
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class BB1Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(BB1Parser.class);

    public static final String SPORT_NAME = "Basket";

    public static final String TEAM_PREFIX = "bbt";

    public static final String GAME_PREFIX = "bb";

    private SportService sports;

    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentStage stage;

    private TournamentRound round;

    private TournamentGame game;

    private SportContestant contestant;

    private String period;

    /* Prefix for UUID */
    public static final String PREFIX = "BB";

    public BB1Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public BB1Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public BB1Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.sports = sports;
        this.em = em;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("OptaDocument")) {

            final long competition_id = Long.valueOf(attributes.getValue(""
                    + "competition_id"));
            final String competition_name = attributes
                    .getValue("competition_name");

            final String code = "BB_" + String.valueOf(competition_id);
            if (sports != null) {
                tournament = sports.getSportTournamentByCode(code);
            }

            if (tournament == null) {
                tournament = new TournamentImpl(competition_name,
                        randomGenerator.nextInt(1000000));
                tournament.setCode(code);
                if (em != null) {
                    em.persist(tournament);
                }
                Sport sport = null;
                if (sports != null) {
                    sport = sports.getSportByName(SPORT_NAME);
                }
                if (sport == null) {
                    final long sport_id = Long.valueOf(attributes
                            .getValue("sport_id"));
                    sport = new SportImpl(SPORT_NAME, sport_id);
                }
                tournament.addSport(sport);
            }

            final long season_id = Long.valueOf(attributes.getValue(""
                    + "season_id"));
            final String season_name = attributes.getValue("season_name");

            if (season == null) {
                final String name = tournament.getName() + " - " + season_name;
                if (sports != null) {
                    season = sports.getSeasonByYearLabel(tournament,
                            season_name);
                }
                if (season == null) {
                    season = new TournamentSeasonImpl(season_id);
                    season.setYearLabel(season_name);
                    if (em != null) {
                        em.persist(season);
                    }
                    season.setName(name);
                    tournament.addSeason(season);
                    season.setTournament(tournament);
                    season.setStatus(TournamentSeasonStatus.SCHEDULED);
                }

            }

            // NBA Regular => flat games.
            if (competition_id == 6) {

                final String seasonName = season.getName();
                stage = season.getStageByName(seasonName);
                if (stage == null) {
                    stage = new TournamentStageImpl(
                            randomGenerator.nextInt(1000000));
                    stage.setName(seasonName);
                    season.addStage(stage);
                    stage.setSeason(season);
                    stage.setStatus(TournamentStageStatus.SCHEDULED);
                }

                final String roundName = season.getName();
                round = stage.getRoundByName(roundName);
                if (round == null) {
                    round = new TournamentRoundImpl(
                            randomGenerator.nextInt(1000000));
                    round.setName(roundName);
                    round.setRoundLabel(roundName);
                    round.setRoundNumber(1);
                    stage.addRound(round);
                    round.setStage(stage);
                    round.setStatus(TournamentRoundStatus.SCHEDULED);
                }

            }

        } else if (qName.equalsIgnoreCase("MatchData")) {

            final String uuid = GAME_PREFIX + attributes.getValue("uID");
            if (sports != null && sports.getTournamentGame(uuid) != null) {
                game = sports.getTournamentGame(uuid);
            } else {
                game = new TournamentGameImpl(uuid);
                game.setRound(round);
                round.addGame(game);
            }

        } else if (qName.equalsIgnoreCase("MatchInfo")) {

            period = attributes.getValue("period");
            if ("FullTime".equals(period)) {
                if (!TournamentGameStatus.TERMINATED.equals(game
                        .getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.TERMINATED);
                    log.info("Marking game with uuid=" + game.getUUID()
                            + " as TERMINATED");
                }
            } else if ("PreMatch".equals(period) || "Postponed".equals(period)) {
                if (!TournamentGameStatus.SCHEDULED
                        .equals(game.getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.SCHEDULED);
                }
            } else if ("Abandoned".equals(period)) {
                if (!TournamentGameStatus.CANCELLED
                        .equals(game.getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.CANCELLED);
                    log.info("Marking game with uuid=" + game.getUUID()
                            + " as CANCELLED");
                }
            } else {
                if (!TournamentGameStatus.ON_GOING.equals(game.getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.ON_GOING);
                    log.info("Marking game with uuid=" + game.getUUID()
                            + " as SCHEDULED");

                }
            }

        } else if (qName.equalsIgnoreCase("TeamData")) {

            final String teamUUID = TEAM_PREFIX
                    + attributes.getValue("team_id");

            SportContestant team = sports.getSportContestantTeam(teamUUID);
            if (team == null) {
                // Here for unit testing purpose.
                team = new SportContestantImpl(teamUUID,
                        SportContestantType.TEAM);
                season.addContestant(team);
            }

            int scoreInt = 0;
            final String score = attributes.getValue("score");
            if (!score.isEmpty()) {
                scoreInt = Integer.valueOf(score);
            }

            final String side = attributes.getValue("side");
            List<SportContestant> contestants = game.getContestants();
            if ("Home".equals(side)) {
                if (contestants.size() >= 1) {
                    contestants.remove(0);
                }
                contestants.add(0, team);
                game.getScore().setScoreTeam1(scoreInt);
            } else if ("Away".equals(side)) {
                if (contestants.size() == 2) {
                    contestants.remove(1);
                }
                contestants.add(1, team);
                game.getScore().setScoreTeam2(scoreInt);
            }
            game.setContestants(contestants);

        } else if (qName.equalsIgnoreCase("Team")) {

            final String teamUUID = TEAM_PREFIX + attributes.getValue("uID");
            contestant = sports.getSportContestantTeam(teamUUID);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("Date")) {

            // 2012-10-31T00:00:00+00:00
            final String dt = tempVal;
            try {
                Date date = getDateTZ(dt, "UTC"); // XXX see below for TZ
                game.setStartDate(date);
            } catch (Exception e) {
                log.error("Error parsing date=" + dt + " for game with uuid="
                        + game.getUUID());
            }

        } else if (qName.equalsIgnoreCase("TZ")) {
            // XXX assuming this is always UTC which seems to be the case.
            // No consequences on bets since period will mark it as started
            // which will cause bets to be disallowed after that.

        } else if (qName.equalsIgnoreCase("Name")) {
            if (contestant != null) {
                contestant.setName(tempVal);
            } else {
                System.out.println("Can't find team for name=" + tempVal);
            }
        } else if (qName.equalsIgnoreCase("MatchData")) {
            // No odd results in NBA
            if ("FullTime".equals(period)) {
                if (game.getScore().getScoreTeam1() > game.getScore()
                        .getScoreTeam2()) {
                    game.setWinner(game.getContestants().get(0));
                } else {
                    game.setWinner(game.getContestants().get(1));
                }
            }
        }

    }

    /**
     * Returns a date given date and time.
     * 
     * Expecting date in UTC.
     * 
     * @param dateStr: date in format yyyyMMdd HH:mm:ss as a {@link String}
     * @param: tz: Timezone (Europe/London)
     * @return a {@link Date} instance
     * @throws DatatypeConfigurationException
     * @throws ParseException
     */
    private Date getDateTZ(String dateStr, String tz) throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(dateStr)
                .toGregorianCalendar().getTime();
    }

}
