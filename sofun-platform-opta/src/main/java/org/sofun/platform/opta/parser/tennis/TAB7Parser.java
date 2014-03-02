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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * TAB7 Tennis Live Match Feed Specification
 * 
 * <p>
 * 
 * The purpose of this feed is to provide the customer with a live match feed
 * updated per game for ATP/WTA and Grand Slam events.
 * 
 * <p>
 * 
 * Note that in doubles matches, a "match tiebreak" is sometimes played instead
 * of a deciding set. This is a tiebreak to 10 points. A match tiebreak is
 * represented as a set with a score of 1 game to nil to the winner of the
 * tiebreak, and the number of points scored in the tiebreak indicated by the
 * first_entry_tie_break_score and second_entry_tie_break_score attributes. A
 * match tiebreak will only ever occur in the final (3rd or 5th) set of a
 * doubles match.
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following: - TAB7-(match
 * id).xml e.g. TAB7-285487.xml
 * 
 * <p>
 * 
 * Based on version specification documentation dated from 05 January 2011
 * 
 * @author @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TAB7Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(TAB7Parser.class);

    @SuppressWarnings("unused")
    private EntityManager em;

    public static final String sportName = "Tennis";

    /* Prefix for game UUID */
    public static final String PREFIX = "TAB";

    /* Prefix for player UUID */
    public static final String PREFIX_PLAYER = "TABP";

    private SportService sports;

    private TournamentGame tournamentGame;

    private String first_entry_id;

    private String second_entry_id;

    private String scoreSetsFirstEntryId;

    private String scoreSetsSecondEntryId;

    private int scoreFirstEntryId = 0;

    private int scoreSecondEntryId = 0;

    public TAB7Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public TAB7Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public TAB7Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.em = em;
        this.sports = sports;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        if (qName.equals("match")) {

            final String id = attributes.getValue("id");
            final String gameUUID = PREFIX + id;
            tournamentGame = sports.getTournamentGame(gameUUID);

            if (tournamentGame == null) {
                log.warn("Cannot find game with uuid=" + gameUUID);
                return;
            }

            final String start_time = attributes.getValue("start_time");
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
            tournamentGame.setStartDate(startDate);

            final String status = attributes.getValue("status");
            if ("Not Started".equals(status)) {
                tournamentGame.setGameStatus(TournamentGameStatus.SCHEDULED);
            } else if ("In Play".equals(status)) {
                tournamentGame.setGameStatus(TournamentGameStatus.ON_GOING);
            } else if ("Stopped".equals(status)) {
                log.info("Game with uuid" + tournamentGame.getUUID()
                        + " has been STOPPED. Real life check required.");
            } else if ("Finished".equals(status)) {
                if (!TournamentGameStatus.TERMINATED.equals(tournamentGame
                        .getGameStatus())) {
                    tournamentGame
                            .setGameStatus(TournamentGameStatus.TERMINATED);
                    log.info("Marking game with UUID="
                            + tournamentGame.getUUID() + " as TERMINATED");
                }
            }

        } else if (qName.equals("first_entry")) {

            if (tournamentGame == null) {
                return;
            }

            final String first_entry_draw_position = attributes
                    .getValue("draw_position");
            final String first_entry_seed_position = attributes
                    .getValue("seed_position");

            tournamentGame.getProperties().put("first_entry_draw_position",
                    first_entry_draw_position);
            tournamentGame.getProperties().put("first_entry_seed_position",
                    first_entry_seed_position);

            first_entry_id = attributes.getValue("id");

        } else if (qName.equals("second_entry")) {

            if (tournamentGame == null) {
                return;
            }

            final String second_entry_draw_position = attributes
                    .getValue("draw_position");
            final String second_entry_seed_position = attributes
                    .getValue("seed_position");

            tournamentGame.getProperties().put("second_entry_draw_position",
                    second_entry_draw_position);
            tournamentGame.getProperties().put("second_entry_seed_position",
                    second_entry_seed_position);

            second_entry_id = attributes.getValue("id");

        } else if (qName.equals("player")) {

            if (tournamentGame == null) {
                return;
            }

            final String first_name = attributes.getValue("first_name");
            final String last_name = attributes.getValue("last_name");
            final String display_name = attributes.getValue("display_name");
            final String nationality = attributes.getValue("nationality");
            final String id = attributes.getValue("id");

            SportContestant c = sports.getSportContestantPlayer(PREFIX_PLAYER
                    + id);
            if (c == null) {
                c = new SportContestantImpl(PREFIX_PLAYER + id,
                        SportContestantType.INDIVIDUAL);
            }
            c.setLastName(last_name);
            c.setName(display_name);
            c.setGivenName(first_name);
            c.getProperties().put("nationality", nationality);

            if (second_entry_id == null) {
                if (tournamentGame.getContestants().size() == 0) {
                    tournamentGame.getContestants().add(0, c);
                }
            } else {
                if (tournamentGame.getContestants().size() == 1) {
                    tournamentGame.getContestants().add(1, c);
                }
            }

        } else if (qName.equals("score")) {

            if (tournamentGame == null) {
                return;
            }

            final String winning_entry_id = attributes
                    .getValue("winning_entry_id");
            final String result_type = attributes.getValue("result_type");
            final String number_of_sets = attributes.getValue("number_of_sets");
            tournamentGame.getProperties()
                    .put("number_of_sets", number_of_sets);
            if ("Win".equals(result_type)) {
                if (first_entry_id.equals(winning_entry_id)) {
                    tournamentGame.setWinner(tournamentGame.getContestants()
                            .get(0));
                } else if (second_entry_id.equals(winning_entry_id)) {
                    tournamentGame.setWinner(tournamentGame.getContestants()
                            .get(1));
                }
                tournamentGame.setGameStatus(TournamentGameStatus.TERMINATED);
            }

        } else if (qName.equals("set")) {

            if (tournamentGame == null) {
                return;
            }

            @SuppressWarnings("unused")
            final String number = attributes.getValue("number");

            final String winning_entry_id = attributes
                    .getValue("winning_entry_id");

            if (first_entry_id.equals(winning_entry_id)) {
                scoreFirstEntryId += 1;
            } else if (second_entry_id.equals(winning_entry_id)) {
                scoreSecondEntryId += 1;
            }

            @SuppressWarnings("unused")
            final String first_entry_tie_break_score = attributes
                    .getValue("first_entry_tie_break_score");
            @SuppressWarnings("unused")
            final String second_entry_tie_break_score = attributes
                    .getValue("second_entry_tie_break_score");

            final String first_entry_games = attributes
                    .getValue("first_entry_games");
            final String second_entry_games = attributes
                    .getValue("second_entry_games");

            if (scoreSetsFirstEntryId == null) {
                scoreSetsFirstEntryId = first_entry_games;
            } else {
                scoreSetsFirstEntryId += first_entry_games;
            }
            if (scoreSetsSecondEntryId == null) {
                scoreSetsSecondEntryId = second_entry_games;
            } else {
                scoreSetsSecondEntryId += second_entry_games;
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equals("tournament")) {

            if (tournamentGame == null) {
                return;
            }

            if (TournamentGameStatus.TERMINATED.equals(tournamentGame
                    .getGameStatus())) {
                tournamentGame.getProperties().put("scoreFirstEntryId",
                        scoreSetsFirstEntryId);
                tournamentGame.getProperties().put("scoreSecondEntryId",
                        scoreSetsSecondEntryId);
                tournamentGame.getScore().setScoreTeam1(scoreFirstEntryId);
                tournamentGame.getScore().setScoreTeam2(scoreSecondEntryId);
            }
        }

    }

    public TournamentGame getGame() {
        return tournamentGame;
    }

}
