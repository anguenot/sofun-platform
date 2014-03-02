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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * RU6 Live Key Events parser.
 * 
 * <p/>
 * 
 * This feed is essentially a match list for a given subscribed Competition &
 * Season.
 * 
 * <p/>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta will deliver RU6 files upon The announcement of the teams
 * The start and end of each period of play After every score After every card
 * The confirmation of the result Additionally substitutions events will be
 * included with these updates. In some circumstances substitution events may be
 * added after the result has been declared.
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following:
 * ru6_wapresults.-{match_id}.{YYYYMMDDxxxx}-.xml
 * 
 * where the date represents the date on which the file was generated and xxxx
 * represents a counter showing the nth generation of the file on that day.
 * 
 * <p/>
 * 
 * Implementation based on version specification documentation of 04 October
 * 2010
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class RU6Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(RU6Parser.class);

    public static final String PLAYER_PREFIX = "rup";

    public static final String TEAM_PREFIX = "rut";

    public static final String GAME_PREFIX = "ru";

    private SportService sports;

    private TournamentGame game;

    private String gameStatus = "";

    private int team = 1; // 1 or 2

    private int team1Score;

    private String team1UUID;

    private int team1NbTries = 0;

    private int team1NbDrops = 0;

    private int team1NbPenks = 0;

    private int team1NbConvs = 0;

    private int team2Score;

    private String team2UUID;

    private int team2NbTries = 0;

    private int team2NbDrops = 0;

    private int team2NbPenks = 0;

    private int team2NbConvs = 0;

    private String teamUUIDFirstTry = "-1";

    private String playerUUIDFirstTry = "-1";

    private String teamUUIDFirstDrop = "-1";

    private String playerUUIDFirstDrop = "-1";

    private String teamUUIDFirstConv = "-1";

    private String playerUUIDFirstConv = "-1";

    private String teamUUIDFirstPenk = "-1";

    private String playerUUIDFirstPenk = "-1";

    private boolean isTry = false;

    private boolean isDrop = false;

    private boolean isPenk = false;

    private boolean isConv = false;

    private boolean isEvent = false;

    private String tryTeamUUID = "0";

    private String tryPlayerUUID = "0";

    private String dropTeamUUID = "0";

    private String dropPlayerUUID = "0";

    private String penkTeamUUID = "0";

    private String penkPlayerUUID = "0";

    private String convTeamUUID = "0";

    private String convPlayerUUID = "0";

    private List<String> team1PlayersTried = new ArrayList<String>();

    private List<String> team2PlayersTried = new ArrayList<String>();

    private List<String> team1PlayersDroped = new ArrayList<String>();

    private List<String> team2PlayersDroped = new ArrayList<String>();

    private List<String> team1PlayersPenk = new ArrayList<String>();

    private List<String> team2PlayersPenk = new ArrayList<String>();

    private List<String> team1PlayersConv = new ArrayList<String>();

    private List<String> team2PlayersConv = new ArrayList<String>();

    private static final String STATUS_GAME_FIRST_HALF = "Halftime";

    private static final String STATUS_GAME_TERMINATED = "Result";

    private static final String STATUS_GAME_POSTPONED = "Postponed";

    private static final String STATUS_GAME_ABANDONNED = "Abandoned";

    public static final String GAME_PROP_TEAM1_NB_TRIES_KEY = "GAME_PROP_TEAM1_NB_TRIES";

    public static final String GAME_PROP_TEAM2_NB_TRIES_KEY = "GAME_PROP_TEAM2_NB_TRIES";

    public static final String GAME_PROP_TEAM1_NB_DROPS_KEY = "GAME_PROP_TEAM1_NB_DROPS";

    public static final String GAME_PROP_TEAM2_NB_DROPS_KEY = "GAME_PROP_TEAM2_NB_DROPS";

    public static final String GAME_PROP_TEAM1_NB_CONVS_KEY = "GAME_PROP_TEAM1_NB_CONVS";

    public static final String GAME_PROP_TEAM2_NB_CONVS_KEY = "GAME_PROP_TEAM2_NB_CONVS";

    public static final String GAME_PROP_TEAM1_NB_PENKS_KEY = "GAME_PROP_TEAM1_NB_PENKS";

    public static final String GAME_PROP_TEAM2_NB_PENKS_KEY = "GAME_PROP_TEAM2_NB_PENKS";

    public static final String GAME_PROP_TEAM_UUID_FIRST_TRY_KEY = "GAME_PROP_TEAM_UUID_FIRST_TRY";

    public static final String GAME_PROP_PLAYER_UUID_FIRST_TRY_KEY = "GAME_PROP_PLAYER_UUID_FIRST_TRY";

    public static final String GAME_PROP_TEAM_UUID_FIRST_DROP_KEY = "GAME_PROP_TEAM_UUID_FIRST_DROP";

    public static final String GAME_PROP_PLAYER_UUID_FIRST_DROP_KEY = "GAME_PROP_PLAYER_UUID_FIRST_DROP";

    public static final String GAME_PROP_TEAM_UUID_FIRST_CONV_KEY = "GAME_PROP_TEAM_UUID_FIRST_CONV";

    public static final String GAME_PROP_PLAYER_UUID_FIRST_CONV_KEY = "GAME_PROP_PLAYER_UUID_FIRST_CONV";

    public static final String GAME_PROP_TEAM_UUID_FIRST_PENK_KEY = "GAME_PROP_TEAM_UUID_FIRST_PENK";

    public static final String GAME_PROP_PLAYER_UUID_FIRST_PENK_KEY = "GAME_PROP_PLAYER_UUID_FIRST_PENK";

    public static final String GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY = "GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY";

    public static final String GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY = "GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY";

    public static final String GAME_PROP_TEAM1_PLAYERS_UUID_CONVS_KEY = "GAME_PROP_TEAM1_PLAYERS_UUID_CONVS";

    public static final String GAME_PROP_TEAM2_PLAYERS_UUID_CONVS_KEY = "GAME_PROP_TEAM2_PLAYERS_UUID_CONVS";

    public static final String GAME_PROP_TEAM1_PLAYERS_UUID_PENKS_KEY = "GAME_PROP_TEAM1_PLAYERS_UUID_PENKS";

    public static final String GAME_PROP_TEAM2_PLAYERS_UUID_PENKS_KEY = "GAME_PROP_TEAM2_PLAYERS_UUID_PENKS";

    public static final String GAME_PROP_TEAM1_PLAYERS_UUID_DROPS_KEY = "GAME_PROP_TEAM1_PLAYERS_UUID_DROPS";

    public static final String GAME_PROP_TEAM2_PLAYERS_UUID_DROPS_KEY = "GAME_PROP_TEAM2_PLAYERS_UUID_DROPS";

    public static final String GAME_PROP_SCORE_FIRST_HALF_KEY = "GAME_PROP_SCORE_FIRST_HALF";

    public static final String GAME_PROP_SCORE_SECOND_HALF_KEY = "GAME_PROP_SCORE_SECOND_HALF";

    public static final String GAME_PROP_WINNER_FIRST_HALF_KEY = "GAME_PROP_WINNER_FIRST_HALF";

    public static final String GAME_PROP_WINNER_SECOND_HALF_KEY = "GAME_PROP_WINNER_SECOND_HALF";

    public RU6Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public RU6Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public RU6Parser(File file, SportService sports, EntityManager em) {
        // Note, we do not need the entity manager here since this parser will
        // not be creating top level graph elements. League
        // tables insertion into existing graph elements will trigger
        // persistency. RU1 and RU10 are responsible for creation of
        // top level graph elements
        super(file);
        this.sports = sports;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("match")) {

            final String gameUUID = GAME_PREFIX
                    + attributes.getValue("game-id");
            gameStatus = attributes.getValue("status");

            game = sports.getTournamentGame(gameUUID);

            if (game != null) {
                // We only deal with end of game. No live score yet.
                if (RU6Parser.STATUS_GAME_TERMINATED.equals(gameStatus)
                        && !TournamentGameStatus.TERMINATED.equals(game
                                .getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.TERMINATED);
                    log.info("Game w/ UUID=" + game.getUUID() + " marked as "
                            + TournamentGameStatus.TERMINATED + " by Opta.");
                } else if (RU6Parser.STATUS_GAME_POSTPONED.equals(gameStatus)
                        && !TournamentGameStatus.POSTPONED.equals(game
                                .getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.POSTPONED);
                    log.info("Game w/ UUID=" + game.getUUID() + " marked as "
                            + TournamentGameStatus.POSTPONED + " by Opta.");
                } else if (RU6Parser.STATUS_GAME_ABANDONNED.equals(gameStatus)
                        && !TournamentGameStatus.CANCELLED.equals(game
                                .getGameStatus())) {
                    game.setGameStatus(TournamentGameStatus.CANCELLED);
                    log.info("Game w/ UUID=" + game.getUUID() + " marked as "
                            + TournamentGameStatus.CANCELLED + " by Opta.");
                }
            }

        } else if (qName.equalsIgnoreCase("away-team")) {

            team = 2;

        } else if (qName.equalsIgnoreCase("home-team")) {

            team = 1;

        } else if (qName.equalsIgnoreCase("event")) {

            isEvent = true;

            if ("TRY".equals(attributes.getValue("type"))) {
                isTry = true;
            } else {
                isTry = false;
            }

            if ("CONV".equals(attributes.getValue("type"))) {
                isConv = true;
            } else {
                isConv = false;
            }

            if ("DROPG".equals(attributes.getValue("type"))) {
                isDrop = true;
            } else {
                isDrop = false;
            }

            if ("PENK".equals(attributes.getValue("type"))) {
                isPenk = true;
            } else {
                isPenk = false;
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("score")) {

            if (team == 1) {

                if (!"".equals(tempVal)) {
                    team1Score = Integer.valueOf(tempVal);
                } else {
                    team1Score = 0;
                }

            } else if (team == 2) {

                if (!"".equals(tempVal)) {
                    team2Score = Integer.valueOf(tempVal);
                } else {
                    team2Score = 0;
                }
            }

        } else if (qName.equalsIgnoreCase("team-id")) {

            if (!isEvent) {

                if (team == 1) {

                    team1UUID = TEAM_PREFIX + tempVal;

                } else if (team == 2) {

                    team2UUID = TEAM_PREFIX + tempVal;

                }

            } else {

                if (isTry) {

                    tryTeamUUID = TEAM_PREFIX + tempVal;

                    if ("-1".equals(teamUUIDFirstTry)) {
                        teamUUIDFirstTry = tryTeamUUID;
                    }

                } else if (isDrop) {

                    dropTeamUUID = TEAM_PREFIX + tempVal;

                    if ("-1".equals(teamUUIDFirstDrop)) {
                        teamUUIDFirstDrop = dropTeamUUID;
                    }

                } else if (isPenk) {

                    penkTeamUUID = TEAM_PREFIX + tempVal;

                    if ("-1".equals(teamUUIDFirstPenk)) {
                        teamUUIDFirstPenk = penkTeamUUID;
                    }

                } else if (isConv) {

                    convTeamUUID = TEAM_PREFIX + tempVal;

                    if ("-1".equals(teamUUIDFirstConv)) {
                        teamUUIDFirstConv = convTeamUUID;
                    }

                }

            }

        } else if (qName.equalsIgnoreCase("player-code")) {

            if (isTry) {

                tryPlayerUUID = PLAYER_PREFIX + tempVal;

                if ("-1".equals(playerUUIDFirstTry)) {
                    playerUUIDFirstTry = tryPlayerUUID;
                }

            } else if (isDrop) {

                dropPlayerUUID = PLAYER_PREFIX + tempVal;

                if ("-1".equals(playerUUIDFirstDrop)) {
                    playerUUIDFirstDrop = dropPlayerUUID;
                }

            } else if (isPenk) {

                penkPlayerUUID = PLAYER_PREFIX + tempVal;

                if ("-1".equals(playerUUIDFirstPenk)) {
                    playerUUIDFirstPenk = penkPlayerUUID;
                }

            } else if (isConv) {

                convPlayerUUID = PLAYER_PREFIX + tempVal;

                if ("-1".equals(playerUUIDFirstConv)) {
                    playerUUIDFirstConv = convPlayerUUID;
                }

            }

        } else if (qName.equalsIgnoreCase("player-id")) {

            if (isTry) {

                tryPlayerUUID = PLAYER_PREFIX + tempVal;

            } else if (isDrop) {

                dropPlayerUUID = PLAYER_PREFIX + tempVal;

            } else if (isPenk) {

                penkPlayerUUID = PLAYER_PREFIX + tempVal;

            } else if (isConv) {

                convPlayerUUID = PLAYER_PREFIX + tempVal;

            }

        } else if (qName.equalsIgnoreCase("away-team")) {

            if (game != null) {
                game.getScore().setScoreTeam2(team2Score);
                game.getScore().setLastUpdated(new Date());
            }

        } else if (qName.equalsIgnoreCase("home-team")) {

            // Home -> left
            if (game != null) {
                game.getScore().setScoreTeam1(team1Score);
                game.getScore().setLastUpdated(new Date());
            }

        } else if (qName.equalsIgnoreCase("match")) {

            if (game != null) {
                if (game.getScore().getScoreTeam1() > game.getScore()
                        .getScoreTeam2()) {
                    game.setWinner(game.getContestants().get(0));
                } else if (game.getScore().getScoreTeam1() < game.getScore()
                        .getScoreTeam2()) {
                    game.setWinner(game.getContestants().get(1));
                } else if (game.getScore().getScoreTeam1() == game.getScore()
                        .getScoreTeam2()) {
                    game.setWinner(null);
                }

                Map<String, String> gameProps = game.getProperties();

                gameProps.put(GAME_PROP_TEAM1_NB_TRIES_KEY,
                        String.valueOf(team1NbTries));
                gameProps.put(GAME_PROP_TEAM2_NB_TRIES_KEY,
                        String.valueOf(team2NbTries));

                gameProps.put(GAME_PROP_TEAM1_NB_DROPS_KEY,
                        String.valueOf(team1NbDrops));
                gameProps.put(GAME_PROP_TEAM2_NB_DROPS_KEY,
                        String.valueOf(team2NbDrops));

                gameProps.put(GAME_PROP_TEAM1_NB_CONVS_KEY,
                        String.valueOf(team1NbConvs));
                gameProps.put(GAME_PROP_TEAM2_NB_CONVS_KEY,
                        String.valueOf(team2NbConvs));

                gameProps.put(GAME_PROP_TEAM1_NB_PENKS_KEY,
                        String.valueOf(team1NbPenks));
                gameProps.put(GAME_PROP_TEAM2_NB_PENKS_KEY,
                        String.valueOf(team2NbPenks));

                gameProps.put(GAME_PROP_TEAM_UUID_FIRST_TRY_KEY,
                        teamUUIDFirstTry);
                gameProps.put(GAME_PROP_PLAYER_UUID_FIRST_TRY_KEY,
                        playerUUIDFirstTry);

                gameProps.put(GAME_PROP_TEAM_UUID_FIRST_DROP_KEY,
                        teamUUIDFirstDrop);
                gameProps.put(GAME_PROP_PLAYER_UUID_FIRST_DROP_KEY,
                        playerUUIDFirstDrop);

                gameProps.put(GAME_PROP_TEAM_UUID_FIRST_CONV_KEY,
                        teamUUIDFirstConv);
                gameProps.put(GAME_PROP_PLAYER_UUID_FIRST_CONV_KEY,
                        playerUUIDFirstConv);

                gameProps.put(GAME_PROP_TEAM_UUID_FIRST_PENK_KEY,
                        teamUUIDFirstPenk);
                gameProps.put(GAME_PROP_PLAYER_UUID_FIRST_PENK_KEY,
                        playerUUIDFirstPenk);

                gameProps.put(GAME_PROP_TEAM1_PLAYERS_UUID_TRIES_KEY,
                        StringUtils.join(team1PlayersTried, ","));
                gameProps.put(GAME_PROP_TEAM2_PLAYERS_UUID_TRIES_KEY,
                        StringUtils.join(team2PlayersTried, ","));

                gameProps.put(GAME_PROP_TEAM1_PLAYERS_UUID_DROPS_KEY,
                        StringUtils.join(team1PlayersDroped, ","));
                gameProps.put(GAME_PROP_TEAM2_PLAYERS_UUID_DROPS_KEY,
                        StringUtils.join(team2PlayersDroped, ","));

                gameProps.put(GAME_PROP_TEAM1_PLAYERS_UUID_CONVS_KEY,
                        StringUtils.join(team1PlayersConv, ","));
                gameProps.put(GAME_PROP_TEAM2_PLAYERS_UUID_CONVS_KEY,
                        StringUtils.join(team2PlayersConv, ","));

                gameProps.put(GAME_PROP_TEAM1_PLAYERS_UUID_PENKS_KEY,
                        StringUtils.join(team1PlayersPenk, ","));
                gameProps.put(GAME_PROP_TEAM2_PLAYERS_UUID_PENKS_KEY,
                        StringUtils.join(team2PlayersPenk, ","));

                if (gameStatus.equals(STATUS_GAME_FIRST_HALF)) {
                    String score = String.valueOf(team1Score) + ","
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

                } else if (gameStatus.equals(STATUS_GAME_TERMINATED)) {

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

                }

                game.setProperties(gameProps);
            }

        } else if (qName.equalsIgnoreCase("event")) {

            if (isTry) {

                if (team2UUID.equals(tryTeamUUID)) {
                    team2NbTries += 1;
                    team2PlayersTried.add(tryPlayerUUID);
                } else {
                    team1NbTries += 1;
                    team1PlayersTried.add(tryPlayerUUID);
                }

                isTry = false;
            }

            if (isDrop) {

                if (team2UUID.equals(dropTeamUUID)) {
                    team2NbDrops += 1;
                    team2PlayersDroped.add(dropPlayerUUID);
                } else {
                    team1NbDrops += 1;
                    team1PlayersDroped.add(dropPlayerUUID);
                }

                isDrop = false;

            }

            if (isConv) {

                if (team2UUID.equals(convTeamUUID)) {
                    team2NbConvs += 1;
                    team2PlayersConv.add(convPlayerUUID);
                } else {
                    team1NbConvs += 1;
                    team1PlayersConv.add(convPlayerUUID);
                }

                isConv = false;

            }

            if (isPenk) {

                if (team2UUID.equals(penkTeamUUID)) {
                    team2NbPenks += 1;
                    team2PlayersPenk.add(penkPlayerUUID);
                } else {
                    team1NbPenks += 1;
                    team1PlayersPenk.add(penkPlayerUUID);
                }

                isPenk = false;

            }

            isEvent = false;

        }

    }

    public TournamentGame getGame() {
        return game;
    }

}
