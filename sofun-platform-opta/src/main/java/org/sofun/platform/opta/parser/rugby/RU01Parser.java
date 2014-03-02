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
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.Sport;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
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
 * RU1 Competition Fixtures and Results
 * 
 * <p/>
 * 
 * This feed is essentially a match list for a given subscribed Competition &
 * Season.
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following:
 * Ru1.-{competition_id }.{seasonid}.yyyymmddhhmmss.xml
 * 
 * Where seasonid represents the season in which the fixtures take place. For
 * rugby union the season id is the year in which the season ends.
 * 
 * <p/>
 * 
 * Implementation based on version specification documentation of 04 October
 * 2010
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class RU01Parser extends AbstractOptaParser {

    public static final String SPORT_NAME = "Rugby";

    public static final String TEAM_PREFIX = "rut";

    public static final String GAME_PREFIX = "ru";

    private SportService sports;

    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentStage stage;

    private TournamentRound round;

    private SportContestant team1;

    private SportContestant team2;

    private TournamentGame game;

    private final List<TournamentStage> stages = new ArrayList<TournamentStage>();

    private final List<TournamentRound> rounds = new ArrayList<TournamentRound>();

    private final List<TournamentGame> games = new ArrayList<TournamentGame>();

    public RU01Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public RU01Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public RU01Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.sports = sports;
        this.em = em;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("fixture")) {

            if (tournament == null) {
                final long uuid = Long.valueOf(attributes.getValue("comp_id"));
                final String name = attributes.getValue("comp_name");
                if (sports != null) {
                    tournament = sports.getSportTournament(uuid);
                }
                if (tournament == null) {
                    tournament = new TournamentImpl(name, uuid);
                    if (em != null) {
                        em.persist(tournament);
                    }
                    Sport sport = null;
                    if (sports != null) {
                        sport = sports.getSportByName(SPORT_NAME);
                    }
                    if (sport == null) {
                        sport = new SportImpl(SPORT_NAME,
                                randomGenerator.nextInt(1000000));
                    }
                    tournament.addSport(sport);
                }
            }

            if (season == null) {
                final String yearLabel = attributes.getValue("season_id");
                final String name = tournament.getName() + " - " + yearLabel;
                if (sports != null) {
                    season = sports.getSeasonByYearLabel(tournament, yearLabel);
                }
                if (season == null) {
                    season = new TournamentSeasonImpl(
                            randomGenerator.nextInt(1000000));
                    season.setYearLabel(yearLabel);
                    if (em != null) {
                        em.persist(season);
                    }
                    season.setName(name);
                    tournament.addSeason(season);
                    season.setTournament(tournament);
                }

            }

            final String stageName = attributes.getValue("group_name");
            if (season.getStageByName(stageName) == null) {
                stage = getStageByName(stageName);
            } else {
                stage = season.getStageByName(stageName);
            }

            final String roundName = attributes.getValue("round");
            if (stage.getRoundByName(roundName) != null) {
                round = stage.getRoundByName(roundName);
            } else {
                round = getRoundByName(roundName);
            }

            final String uuid = GAME_PREFIX + attributes.getValue("id");
            if (sports != null && sports.getTournamentGame(uuid) != null) {
                game = sports.getTournamentGame(uuid);
            } else {
                game = new TournamentGameImpl(uuid);
                game.setRound(round);
                round.addGame(game);
            }

            final String dateStr = attributes.getValue("game_date");
            final String timeStr = attributes.getValue("time");

            try {
                Date date = getDateTZ(dateStr + " " + timeStr, "Europe/London");
                game.setStartDate(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Calendar now = Calendar.getInstance();
                if (cal.compareTo(now) > 0 && game.getGameStatus() == null) {
                    game.setGameStatus(TournamentRoundStatus.SCHEDULED);
                } else if (cal.compareTo(now) < 0
                        && game.getGameStatus() == null) {
                    game.setGameStatus(TournamentRoundStatus.TERMINATED);
                }
            } catch (ParseException e) {
                throw new SAXException(e);
            }

        } else if (qName.equalsIgnoreCase("team")) {

            if (round != null) {

                String uuid = attributes.getValue("team_id");
                if (uuid != null) {
                    uuid = TEAM_PREFIX + uuid;
                }

                if (team1 == null) {

                    if (sports != null
                            && sports.getSportContestantTeam(uuid) != null) {
                        team1 = sports.getSportContestantTeam(uuid);
                    } else {
                        team1 = new SportContestantImpl(uuid,
                                SportContestantType.TEAM);
                    }

                } else {

                    if (sports != null
                            && sports.getSportContestantTeam(uuid) != null) {
                        team2 = sports.getSportContestantTeam(uuid);
                    } else {
                        team2 = new SportContestantImpl(uuid,
                                SportContestantType.TEAM);
                    }

                }

            } else {

                // <teams /> after fixtures

                final String uuid = attributes.getValue("id");
                final String name = attributes.getValue("name");

                if (sports != null
                        && sports.getSportContestantTeam(uuid) != null) {
                    SportContestant team = sports.getSportContestantTeam(uuid);
                    team.setName(name);
                }

            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("fixture")) {

            List<SportContestant> teams = new ArrayList<SportContestant>();

            // Opta specifics. They add 2 times the same team on future games.
            if (!team1.getUUID().equals(team2.getUUID())) {
                teams.add(team1);
                teams.add(team2);
            }
            game.setContestants(teams);

            game.setRound(round);
            games.add(game);

            team1 = null;
            team2 = null;
            round = null;

        }

    }

    public Tournament getTournament() {
        return tournament;
    }

    public TournamentSeason getSeason() {
        return season;
    }

    public List<TournamentStage> getStages() {
        return stages;
    }

    public List<TournamentRound> getRounds() {
        return rounds;
    }

    public List<TournamentGame> getGames() {
        return games;
    }

    private TournamentStage getStageByName(String name) {

        TournamentStage one = null;

        for (TournamentStage s : stages) {
            if (s.getName().equals(name)) {
                one = s;
                break;
            }
        }

        if (one == null) {
            final long stageUUID = randomGenerator.nextInt(1000000);
            one = new TournamentStageImpl(stageUUID);
            one.setName(name);
            one.setSeason(season);
            season.addStage(one);
            stages.add(one);
        }

        return one;
    }

    private TournamentRound getRoundByName(String name) {

        TournamentRound one = null;

        for (TournamentRound r : rounds) {
            if (r.getName().equals(name)) {
                one = r;
                break;
            }
        }

        if (one == null) {
            final long roundUUID = randomGenerator.nextInt(1000000);
            one = new TournamentRoundImpl(roundUUID);
            one.setName(name);
            one.setRoundLabel(name);
            one.setStage(stage);
            stage.addRound(one);
            rounds.add(one);
        }

        return one;

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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(tz));
        return formatter.parse(dateStr);
    }

}
