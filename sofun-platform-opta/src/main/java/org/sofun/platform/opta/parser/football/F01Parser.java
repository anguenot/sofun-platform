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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;

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
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
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
 * F1 Fixtures / Results
 * 
 * <p/>
 * 
 * This feed is essentially a match list for a given subscribed Competition &
 * Season. This feed includes a reference to every match (past and present)
 * within a season.
 * 
 * <p/>
 * 
 * It details the
 * 
 * <ul>
 * 
 * <li>Date</li>
 * <li>Home/Away teams (with id)</li>
 * <li>Venue (with id)</li>
 * <li>Score it also references the scorers (as ids).</li>
 * <li>An automated file is sent each day at 11am GMT and at the end of each
 * game day.</li>
 * 
 * </ul>>
 * 
 * The File naming convention used for this feed is the following :
 * srml-{competition_id}-{season_id}-results.xml
 * 
 * <p/>
 * 
 * Based on version specification documentation 1.8
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class F01Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(F01Parser.class);

    private static final String sportName = "Soccer";

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentGame game;

    private String date;

    private TournamentRound round;

    private TournamentStage stage;

    private SportService sports;

    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    public F01Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public F01Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public F01Parser(File file, SportService sports, EntityManager em) {
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

        } else if (qName.equalsIgnoreCase("MatchData")) {

            String gameUUID = attributes.getValue("uID");
            // F01 and F07 are not consistent...
            gameUUID = "f" + gameUUID.substring(1);
            if (sports == null || sports.getTournamentGame(gameUUID) == null) {
                game = new TournamentGameImpl(gameUUID);
            } else {
                game = sports.getTournamentGame(gameUUID);
                round = game.getRound();
                stage = round.getStage();
            }

        } else if (qName.equalsIgnoreCase("MatchInfo")) {

            /*
             * "FullTime": This game has officially finished
             * 
             * "PreMatch": This game has yet to Kick Off
             * 
             * "Postponed": Appears if the fixture has been postponed (see Stat
             * Type for the reason why). The period will remain like this until
             * a new date is announced for the fixture at which point this new
             * date will appear and the old one shall be removed. The game_id
             * will remain the same.
             * 
             * "Abandoned": Appears if a game is abandoned (see Stat Type for
             * the reason why). This will remain like this until the fixture is
             * rearranged. Once the game is rearranged the original will be
             * removed and the new date added, period will then = prematch. The
             * game_id will remain the same.
             */
            final String period = attributes.getValue("Period");
            if ("FullTime".equals(period)) {
                if (!TournamentGameStatus.TERMINATED.equals(game
                        .getGameStatus())) {
                    log.info("Marking game w/ UUID=" + game.getUUID()
                            + " as terminated by Opta");
                    game.setGameStatus(TournamentGameStatus.TERMINATED);
                }
            } else if ("PreMatch".equals(period) || "Postponed".equals(period)) {
                game.setGameStatus(TournamentGameStatus.SCHEDULED);
            } else if ("Abandoned".equals(period)) {
                game.setGameStatus(TournamentGameStatus.CANCELLED);
            }

            /*
             * This value is the n’th game of the season for the team’s
             * involved.
             */
            String matchDay = attributes.getValue("MatchDay");
            if (matchDay == null) {
                matchDay = "0";
            }

            /*
             * An integer used for Cup competitions to denote how far into the
             * tournament this match is
             */
            String roundNumber = attributes.getValue("RoundNumber");
            if (roundNumber == null) {
                roundNumber = "0";
            }

            /*
             * - Qualifier Round - Round - Quarter-Finals - Semi-Finals - 3rd
             * and 4th Place - Final
             * 
             * This is the current list of different round types that Opta
             * Sportsdata use to describe the fixtures. In a cup competition,
             * “Round” will be used up until the moment that the Quarter- Finals
             * stage is reached. So use the RoundNumber field to help work out
             * the stage in the competition as well.
             */
            final String roundType = attributes.getValue("RoundType");

            /*
             * Indicates the competition group which the match belongs to.
             * Values will be alphabetical (A,B,C,D..). Only appears in
             * competitions which have a group system such as Champions League
             * or World Cup.
             */
            final String groupName = attributes.getValue("GroupName");

            if (stage == null) {
                if (roundType != null) { // Cup type
                    stage = season.getStageByName(roundType);
                    if (stage == null) {
                        stage = new TournamentStageImpl(
                                randomGenerator.nextInt(1000000));
                        stage.setName(roundType);
                        season.addStage(stage);
                        stage.setSeason(season);
                    }
                } else { // Championship type
                    stage = season.getStageByName(season.getName());
                    if (stage == null) {
                        stage = new TournamentStageImpl(
                                randomGenerator.nextInt(1000000));
                        stage.setName(season.getName());
                        season.addStage(stage);
                        stage.setSeason(season);
                    }
                }
            }

            if (round == null) {
                if (roundType != null && groupName != null
                        && "Round".equals(roundType)) {
                    round = stage.getRoundByName(groupName);
                    if (round == null) {
                        round = new TournamentRoundImpl(
                                randomGenerator.nextInt(1000000));
                        round.setName(groupName);
                        round.setRoundLabel(groupName);
                        round.setRoundNumber(Integer.valueOf(roundNumber));
                        stage.addRound(round);
                        round.setStage(stage);
                    }
                } else if (roundType != null) {
                    round = stage.getRoundByName(roundType);
                    if (round == null) {
                        round = new TournamentRoundImpl(
                                randomGenerator.nextInt(1000000));
                        round.setRoundLabel(roundType);
                        round.setRoundNumber(Integer.valueOf(roundNumber));
                        round.setName(roundType);
                        stage.addRound(round);
                        round.setStage(stage);
                    }
                } else {
                    round = stage.getRoundByName(matchDay);
                    if (round == null) {
                        round = new TournamentRoundImpl(
                                randomGenerator.nextInt(1000000));
                        round.setRoundLabel(matchDay);
                        round.setRoundNumber(Integer.valueOf(matchDay));
                        round.setName(matchDay);
                        stage.addRound(round);
                        round.setStage(stage);
                    }
                }
            }

            game.setRound(round);
            round.addGame(game);

        } else if (qName.equalsIgnoreCase("TeamData")) {

            final String teamUUID = attributes.getValue("TeamRef");

            SportContestant team;
            if (sports != null) {
                team = sports.getSportContestantTeam(teamUUID);
            } else {
                // Here for unit testing purpose.
                team = new SportContestantImpl(teamUUID,
                        SportContestantType.TEAM);
            }

            int scoreInt = 0;
            final String score = attributes.getValue("Score");
            if (!"".equals(score)) {
                scoreInt = Integer.valueOf(score);
            }

            final String side = attributes.getValue("Side");
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

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("MatchData")) {
            game = null;
            round = null;
            stage = null;
        } else if (qName.equalsIgnoreCase("Date")) {

            date = tempVal;

        } else if (qName.equalsIgnoreCase("TZ")) {

            Date gameStartDate = null;
            try {
                if (!TournamentGameStatus.TERMINATED.equals(game
                        .getGameStatus()) || game.getStartDate() == null) {
                    gameStartDate = getDateTZ(date, "Europe/London");
                    if (gameStartDate.compareTo(new Date()) > 0) {
                        game.setStartDate(gameStartDate);
                    }
                }
            } catch (ParseException e) {
                // Wrong date format. Can happened apparently in some feeds...
                // for instance: 04-11 19:45:00
                log.warn("Cannot parse date=" + date + " for game with uuid="
                        + game.getUUID()
                        + " Expecting YYYY-MM-DD HH:MM:SS format");
            }

        }

    }

    public Tournament getTournament() {
        return tournament;
    }

    public TournamentSeason getTournamentSeason() {
        return season;
    }

    /**
     * Returns a date given date and time.
     * 
     * Expecting date in UTC.
     * 
     * @param dateStr: date in format yyyyMMdd HH:mm:ss as a {@link String}
     * @param: tz: Timezone (Europe/London)
     * @return a {@link Date} instance
     * @throws ParseException
     */
    private Date getDateTZ(String dateStr, String tz) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(tz));
        return formatter.parse(dateStr);
    }

}
