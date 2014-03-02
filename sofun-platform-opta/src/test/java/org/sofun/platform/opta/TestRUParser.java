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

package org.sofun.platform.opta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.platform.opta.parser.rugby.RU01Parser;

/**
 * RU1 parser unit tests.
 * 
 * <p/>
 * 
 * RU1 is responsible for the creation of base graph elements. (tournment,
 * season, stages ad rounds)
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestRUParser extends TestCase {

    private static final String ru01FeedPath = "feeds/ru/ru1_compfixtures.210.2012.20110831070016.xml";

    private final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyyMMdd HH:mm:ss");

    @Test
    public void testRU1Parser() throws ParseException {

        RU01Parser parser = new RU01Parser(ru01FeedPath);

        Tournament tournament = parser.getTournament();
        assertEquals(210, tournament.getUUID());
        assertEquals("Rugby World Cup", tournament.getName());

        assertEquals(1, tournament.getSports().size());
        assertEquals(RU01Parser.SPORT_NAME, tournament.getSports().get(0)
                .getName());

        TournamentSeason season = parser.getSeason();
        assertNotNull(season);
        assertEquals("Rugby World Cup - 2012", season.getName());
        assertEquals("2012", season.getYearLabel());
        assertTrue(season.getUUID() != 210);

        List<TournamentStage> stages = season.getStages();
        assertEquals(8, stages.size());

        List<TournamentRound> rounds = season.getRounds();
        assertEquals(2, rounds.size());

        List<TournamentGame> games = season.getGames();
        assertEquals(48, games.size());

        for (TournamentGame game : games) {

            assertNotNull(game.getStartDate());

            if (game.getUUID().equals("ru6660")) {

                final Date startDate = game.getStartDate();
                String date = "20110909" + " " + "8:30:00";

                Date expected = sdf.parse(date);
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
                assertEquals(expected, startDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);

                assertEquals(9, cal.get(Calendar.DATE));
                assertEquals(Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
                assertEquals(2011, cal.get(Calendar.YEAR));

                assertEquals(8, cal.get(Calendar.HOUR));
                assertEquals(30, cal.get(Calendar.MINUTE));

                assertEquals("1", game.getRound().getName());
                assertEquals("A", game.getRound().getStage().getName());

                List<SportContestant> teams = game.getContestants();
                assertEquals(2, teams.size());
                assertEquals("rut850", teams.get(0).getUUID());
                assertEquals("rut750", teams.get(1).getUUID());
            }

        }

    }

}
