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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Match Live Basic
 * 
 * <p/>
 * 
 * This feed represents a game from when the lineups are announced until the
 * game has ended.
 * 
 * <p/>
 * 
 * The following data is provided for subscribers:
 * <ul>
 * <li>Line Up (starting players with names, position, shirt number and subs)</li>
 * <li>Status of the match – (current score, time and period)</li>
 * <li>Goals with scorers and assisting players (with detail own goal / penalty
 * / normal / penalty shoot)</li>
 * <li>Card Events (with time, player, type and reason)</li>
 * <li>Substitution Events (with time, players and reason)</li>
 * <li>Venue</li>
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following :
 * srml-{competition_id}-{season_id}-f{game_id}-matchresults.xml
 * 
 * <p/>
 * 
 * Based on version specification documentation 1.92 (11/03/2010)
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class F07Parser extends AbstractOptaParser {

    public static final Log log = LogFactory.getLog(F07Parser.class);

    /* The game has official finished */
    public static final String DOCUMENT_TYPE_RESULT = "Result";

    /* Collection of data has not fully finished */
    public static final String DOCUMENT_TYPE_LATEST = "Latest";

    /* The game is currently in the 1st Half */
    public static final String STATUS_GAME_FIRST_HALF = "FirstHalf";

    /* Match is currently at HT */
    public static final String STATUS_GAME_HALF_TIME = "HalfTime";

    /* Match is currently in 2nd Period */
    public static final String STATUS_GAME_SECOND_HALF_TIME = "SecondHalf";

    /* Match is currently in the first period of Extra Time */
    public static final String STATUS_GAME_EXTRA_FIRST_HALF = "ExtraFirstHalf";

    /* Match is currently in the first period of Extra Time */
    public static final String STATUS_GAME_EXTRA_SECOND_HALF = "ExtraSecondHalf";

    /* The game is currently within a penalty shootout */
    public static final String STATUS_GAME_SHOOTOUT = "ShootOut";

    /* The referee has now stopped this game */
    public static final String STATUS_GAME_FULL_TIME = "FullTime";

    /* Typical 90 minute fixture – all league games will have this */
    public static final String MATCH_TYPE_REGULAR = "Regular";

    /* Cup game which can have all 5 periods of the match being played in */
    public static final String MATCH_TYPE_CUP = "Cup";

    /* Golden Goal can be played */
    public static final String MATCH_TYPE_CUP_GOLD = "Cup Gold";

    /* Cup game which was tied and is then replayed in a 2nd match */
    public static final String MATCH_TYPE_REPLAY = "Replay";

    /*
     * Game can go to the end of the 2nd Half of extra time but no penalty shoot
     * out
     */
    public static final String MATCH_TYPE_CUP_ENGLISH = "Cup English";

    /*
     * Game goes straight to penalties if teams are level after the 2nd Half
     */
    public static final String MATCH_TYPE_CUP_SHORT = "Cup Short";

    /* Game is the 1st Leg of a 2 legged tie */
    public static final String MATCH_TYPE_1ST_LEG = "1st Leg";

    /*
     * Game is the 2nd Leg of a 2 legged tie and will potentially go to
     * penalties if the teams are drawing after the 2nd half of the 2nd leg
     */
    public static final String MATCH_TYPE_2ND_LEG = "2nd Leg";

    /*
     * Game is the 2nd Leg of a 2 legged tie and will potentially go to
     * penalties if the two teams cannot be separated by the away goal rule
     */
    public static final String MATCH_TYPE_2ND_LEG_AWAY_GOAL = "2nd Leg Away Goal";

    /* A game finished in typical circumstances (90 minute match) */
    public static final String RESULT_TYPE_NORMAL_RESULT = "NormalResult";

    /*
     * This is displayed for matches played over 2 legs when there is a winner
     * based on the total score over the 2 games. Note: It will only appear if
     * Match Type = 2nd Leg
     */
    public static final String RESULT_TYPE_AGGREGATE = "Aggregate";

    /* A game’s result was decided after a Penalty Shootout had taken place */
    public static final String RESULT_TYPE_PENALTY_SHOOTOUT = "PenaltyShootout";

    /* A game’s result was decided after Extra had been played */
    public static final String RESULT_TYPE_AFTER_EXTRA_TIME = "AfterExtraTime";

    /* A game was decided on the Golden Goal Ruling */
    public static final String RESULT_TYPE_GOLDEN_GOAL = "GoldenGoal";

    /* A game had been abandoned midway through the play */
    public static final String RESULT_TYPE_ABDONDONED = "Abandoned";

    /* A game was postponed before the game was started */
    public static final String RESULT_TYPE_POSTPONED = "Postponed";

    private String documentType;

    private String matchType;

    private String resultType;

    private SportService sports;

    private TournamentGame game;

    /* In case of 2ng leg game */
    private TournamentGame previousGame;

    private String playerFirstGoalUUID = "-1";

    private Date firstGoalTimestamp;

    private String currentPeriod;

    private String teamFirstGoalUUID = "-1";

    private String firstGoalQuarter = "0";

    private String legWinnerTeamUUID = "-1";

    private SportContestant currentTeam;

    private final List<String> playersGoals = new ArrayList<String>();

    public static final String GAME_PROP_PLAYER_UUID_FIRST_GOAL_KEY = "GAME_PROP_PLAYER_UUID_FIRST_GOAL";

    public static final String GAME_PROP_TEAM_UUID_FIRST_GOAL_KEY = "GAME_PROP_TEAM_UUID_FIRST_GOAL";

    public static final String GAME_PROP_FIRST_GOAL_QUARTER_KEY = "GAME_PROP_FIRST_GOAL_QUARTER";

    public static final String GAME_PROP_SCORE_FIRST_HALF_KEY = "GAME_PROP_SCORE_FIRST_HALF";

    public static final String GAME_PROP_SCORE_SECOND_HALF_KEY = "GAME_PROP_SCORE_SECOND_HALF";

    public static final String GAME_PROP_WINNER_FIRST_HALF_KEY = "GAME_PROP_WINNER_FIRST_HALF";

    public static final String GAME_PROP_WINNER_SECOND_HALF_KEY = "GAME_PROP_WINNER_SECOND_HALF";

    public static final String GAME_PROP_WINNER_LEG_KEY = "GAME_PROP_WINNER_LEG";

    public static final String GAME_PROP_HAS_SHOOTOUT_KEY = "GAME_PROP_HAS_SHOOTOUT";

    public static final String GAME_PROP_HAS_EXTRA_TIME_KEY = "GAME_PROP_HAS_EXTRA_TIME";

    public static final String GAME_PROP_PLAYERS_UUID_GOALS_KEY = "GAME_PROP_PLAYERS_UUID_GOALS";

    public F07Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public F07Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public F07Parser(File file, SportService sports, EntityManager em) {
        // Note, we do not need the entity manager here since this parser will
        // not be creating top level graph elements. League
        // tables insertion into existing graph elements will trigger
        // persistence. F1 and F40 are responsible for creation of
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

            documentType = attributes.getValue("Type");
            final String gameUUID = attributes.getValue("uID");
            game = sports.getTournamentGame(gameUUID);
            if (game == null) {
                log.debug("Game w/ UUID=" + String.valueOf(gameUUID)
                        + " not found.");
            } else if (game.getContestants().size() == 2) {
                log.debug("Game w/ UUID=" + String.valueOf(gameUUID)
                        + " has no contestant teams.");
            }

        } else if (qName.equalsIgnoreCase("Result")) {

            if (game != null && game.getContestants().size() == 2) {
                resultType = attributes.getValue("Type");
                final String winnerTeamUUID = attributes.getValue("Winner");
                if (RESULT_TYPE_AGGREGATE.equals(resultType)) {
                    legWinnerTeamUUID = winnerTeamUUID;
                } else {
                    if (winnerTeamUUID != null) {
                        SportContestant winner = sports
                                .getSportContestantTeam(winnerTeamUUID);
                        game.setWinner(winner);
                    } else {
                        game.setWinner(null);
                    }
                }
            }

        } else if (qName.equalsIgnoreCase("TeamData")) {

            if (game != null && game.getContestants().size() == 2) {
                final String scoreStr = attributes.getValue("Score");
                final int score = Integer.valueOf(scoreStr);
                final String side = attributes.getValue("Side");
                if ("Home".equals(side)) { // Home versus Away
                    game.getScore().setScoreTeam1(score);
                } else {
                    game.getScore().setScoreTeam2(score);
                }
                final String teamUUID = attributes.getValue("TeamRef");
                currentTeam = sports.getSportContestantTeam(teamUUID);
            }

        } else if (qName.equalsIgnoreCase("MatchInfo")) {

            if (game != null && game.getContestants().size() == 2) {
                matchType = attributes.getValue("MatchType");
                currentPeriod = attributes.getValue("Period");
                if (DOCUMENT_TYPE_RESULT.equals(documentType)) {
                    if (STATUS_GAME_FULL_TIME.equals(currentPeriod)) {
                        if (game != null
                                && !TournamentGameStatus.TERMINATED.equals(game
                                        .getGameStatus())) {
                            log.info("Marking game w/ UUID=" + game.getUUID()
                                    + " as terminated by Opta.");
                            game.setGameStatus(TournamentGameStatus.TERMINATED);
                        }
                    }
                }
            }

        } else if (qName.equalsIgnoreCase("Goal")) {

            if (game != null && game.getContestants().size() == 2) {
                final String playerUUID = attributes.getValue("PlayerRef");

                // expected format: 20111015T180849+0100
                final String timeStamp = attributes.getValue("TimeStamp");
                final String minute = attributes.getValue("Time");
                final String period = attributes.getValue("Period");

                if (!STATUS_GAME_SHOOTOUT.equals(period)) {

                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "yyyyMMdd'T'HHmmssZ");

                    // First player to score
                    boolean isFirstGoal = false;
                    try {
                        final Date ts = formatter.parse(timeStamp);
                        if (firstGoalTimestamp == null) {
                            firstGoalTimestamp = ts;
                            playerFirstGoalUUID = playerUUID;
                            isFirstGoal = true;
                        } else {
                            if (firstGoalTimestamp.compareTo(ts) > 0) {
                                firstGoalTimestamp = ts;
                                playerFirstGoalUUID = playerUUID;
                                isFirstGoal = true;
                            }
                        }
                    } catch (ParseException e) {
                        throw new SAXException(e.getMessage());
                    }

                    if (isFirstGoal) {
                        firstGoalQuarter = getQuarterFor(
                                Integer.valueOf(minute), period);
                        teamFirstGoalUUID = currentTeam.getUUID();
                    }
                    playersGoals.add(playerUUID);

                }
            }

        } else if (qName.equalsIgnoreCase("PreviousMatch")) {

            if (game != null && game.getContestants().size() == 2) {
                final String previousGameUUID = attributes.getValue("MatchRef");
                previousGame = sports.getTournamentGame(previousGameUUID);
                if (previousGame == null) {
                    log.debug("Previous Game (1st Leg) w/ UUID="
                            + String.valueOf(previousGameUUID) + " not found.");
                }
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("MatchData")) {
            if (game != null && game.getContestants().size() == 2) {
                Map<String, String> gameProps = game.getProperties();

                gameProps.put(GAME_PROP_PLAYER_UUID_FIRST_GOAL_KEY,
                        playerFirstGoalUUID);
                gameProps.put(GAME_PROP_TEAM_UUID_FIRST_GOAL_KEY,
                        teamFirstGoalUUID);

                gameProps.put(GAME_PROP_PLAYERS_UUID_GOALS_KEY,
                        StringUtils.join(playersGoals, ","));

                gameProps.put(GAME_PROP_FIRST_GOAL_QUARTER_KEY,
                        firstGoalQuarter);

                // Handle leg winner if 2ng leg match types once this game is
                // terminated
                if (DOCUMENT_TYPE_RESULT.equals(documentType)) {
                    if (MATCH_TYPE_2ND_LEG.equals(matchType)) {
                        if (!legWinnerTeamUUID.equals("-1")) {
                            gameProps.put(GAME_PROP_WINNER_LEG_KEY,
                                    legWinnerTeamUUID);
                        } else {
                            log.error("Can't find Leg Winner for game with uuid="
                                    + game.getUUID());
                        }
                    } else if (MATCH_TYPE_2ND_LEG_AWAY_GOAL.equals(matchType)) {

                        if (previousGame != null) {

                            int scoreTeam1 = game.getScore().getScoreTeam1()
                                    + previousGame.getScore().getScoreTeam2();
                            int scoreTeam2 = game.getScore().getScoreTeam2()
                                    + previousGame.getScore().getScoreTeam1();

                            if (scoreTeam1 > scoreTeam2) {
                                legWinnerTeamUUID = game.getContestants()
                                        .get(0).getUUID();
                            } else if (scoreTeam1 < scoreTeam2) {
                                legWinnerTeamUUID = game.getContestants()
                                        .get(1).getUUID();
                            } else if (scoreTeam1 == scoreTeam2) {

                                // Compute # of away goal.

                                int awayGoalTeam1 = previousGame.getScore()
                                        .getScoreTeam2();
                                int awayGoalTeam2 = game.getScore()
                                        .getScoreTeam2();

                                if (awayGoalTeam1 > awayGoalTeam2) {
                                    legWinnerTeamUUID = game.getContestants()
                                            .get(0).getUUID();
                                } else if (awayGoalTeam1 < awayGoalTeam2) {
                                    legWinnerTeamUUID = game.getContestants()
                                            .get(1).getUUID();
                                } else {
                                    // Winner can never be null in this case.
                                    // Fails with NPE if not the case.
                                    legWinnerTeamUUID = game.getWinner()
                                            .getUUID();
                                }

                            }

                            gameProps.put(GAME_PROP_WINNER_LEG_KEY,
                                    legWinnerTeamUUID);
                        } else {
                            log.error("Can't find 1st leg game for game with uuid="
                                    + game.getUUID());
                        }

                    }

                }

                final int team1Score = game.getScore().getScoreTeam1();
                final int team2Score = game.getScore().getScoreTeam2();

                final String team1UUID = game.getContestants().get(0).getUUID();
                final String team2UUID = game.getContestants().get(1).getUUID();

                if (STATUS_GAME_HALF_TIME.equals(currentPeriod)) {

                    final String score = String.valueOf(team1Score) + ","
                            + String.valueOf(team2Score);
                    gameProps.put(GAME_PROP_SCORE_FIRST_HALF_KEY, score);
                    if (team1Score > team2Score) {
                        gameProps.put(GAME_PROP_WINNER_FIRST_HALF_KEY,
                                team1UUID);
                    } else if (team1Score < team2Score) {
                        gameProps.put(GAME_PROP_WINNER_FIRST_HALF_KEY,
                                team2UUID);
                    } else {
                        gameProps.put(GAME_PROP_WINNER_FIRST_HALF_KEY, "");
                    }

                } else if (STATUS_GAME_SECOND_HALF_TIME.equals(currentPeriod)) {

                    if (gameProps.get(GAME_PROP_SCORE_FIRST_HALF_KEY) != null) {

                        String[] scoreFirstHalf = gameProps.get(
                                GAME_PROP_SCORE_FIRST_HALF_KEY).split(",");
                        int scoreTeam1FirstHalf = Integer
                                .valueOf(scoreFirstHalf[0]);
                        int scoreTeam2FirstHalf = Integer
                                .valueOf(scoreFirstHalf[1]);

                        int scoreTeam1SecondHalf = team1Score
                                - scoreTeam1FirstHalf;
                        int scoreTeam2SecondHalf = team2Score
                                - scoreTeam2FirstHalf;

                        if (scoreTeam1SecondHalf < 0) {
                            scoreTeam1SecondHalf = 0;
                        }

                        if (scoreTeam2SecondHalf < 0) {
                            scoreTeam2SecondHalf = 0;
                        }

                        String score = String.valueOf(scoreTeam1SecondHalf)
                                + "," + String.valueOf(scoreTeam2SecondHalf);
                        gameProps.put(GAME_PROP_SCORE_SECOND_HALF_KEY, score);

                        if (scoreTeam1SecondHalf > scoreTeam2SecondHalf) {
                            gameProps.put(GAME_PROP_WINNER_SECOND_HALF_KEY,
                                    team1UUID);
                        } else if (scoreTeam1SecondHalf < scoreTeam2SecondHalf) {
                            gameProps.put(GAME_PROP_WINNER_SECOND_HALF_KEY,
                                    team2UUID);
                        } else {
                            gameProps.put(GAME_PROP_WINNER_SECOND_HALF_KEY, "");
                        }

                    }

                } else if (STATUS_GAME_EXTRA_FIRST_HALF.equals(currentPeriod)
                        || STATUS_GAME_EXTRA_SECOND_HALF.equals(currentPeriod)) {
                    gameProps.put(GAME_PROP_HAS_EXTRA_TIME_KEY, "1");
                } else if (STATUS_GAME_SHOOTOUT.equals(currentPeriod)) {
                    gameProps.put(GAME_PROP_HAS_SHOOTOUT_KEY, "1");
                }

                game.setProperties(gameProps);

            }
        } else if (qName.equalsIgnoreCase("Date")) {
            if (game != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyyMMdd'T'HHmmssZ");
                Date startDate = null;
                try {
                    startDate = formatter.parse(tempVal);
                } catch (ParseException e) {
                    // Wrong date format. Can happened apparently in some
                    // feeds...
                    // for instance: 04-11 19:45:00
                    log.warn("Cannot parse date=" + tempVal
                            + " for game with uuid=" + game.getUUID()
                            + " Expecting yyyyMMdd'T'HHmmssZ format");
                }
                if (startDate != null) {
                    if (startDate.compareTo(new Date()) > 0) {
                        game.setStartDate(startDate);
                    }
                }
            }
        }

    }

    public TournamentGame getGame() {
        return game;
    }

    public static int getGameMinuteFor(Date start, Date event) {
        if (start == null || event == null) {
            return 0;
        }
        long diff = event.getTime() - start.getTime();
        return Math.round(diff / (60 * 1000));
    }

    public static String getQuarterFor(int minute, String period) {
        if (minute >= 0 && minute <= 15) {
            return "first_quarter";
        } else if (minute > 15 && minute <= 30) {
            return "second_quarter";
        } else if (minute > 30 && STATUS_GAME_FIRST_HALF.equals(period)) {
            return "third_quarter";
        } else if (minute >= 45 && minute <= 60) {
            return "forth_quarter";
        } else if (minute > 60 && minute <= 75) {
            return "fifth_quarter";
        } else if (minute > 75 && !STATUS_GAME_SHOOTOUT.equals(period)) {
            return "sixth_quarter";
        } else {
            // For instance if goal outside first 90 minutes.
            return "0";
        }
    }

}
