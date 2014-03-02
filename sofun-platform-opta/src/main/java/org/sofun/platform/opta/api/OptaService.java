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

package org.sofun.platform.opta.api;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.sofun.platform.opta.parser.bb.BB1Parser;
import org.sofun.platform.opta.parser.bb.BB9Parser;
import org.sofun.platform.opta.parser.cy.CSFParser;
import org.sofun.platform.opta.parser.cy.CY1Parser;
import org.sofun.platform.opta.parser.cy.CY40Parser;
import org.sofun.platform.opta.parser.f1.MR1Parser;
import org.sofun.platform.opta.parser.f1.MR2Parser;
import org.sofun.platform.opta.parser.f1.MR4Parser;
import org.sofun.platform.opta.parser.f1.MR5Parser;
import org.sofun.platform.opta.parser.f1.MR6Parser;
import org.sofun.platform.opta.parser.football.F01Parser;
import org.sofun.platform.opta.parser.football.F03Parser;
import org.sofun.platform.opta.parser.football.F07Parser;
import org.sofun.platform.opta.parser.football.F40Parser;
import org.sofun.platform.opta.parser.rugby.RU01Parser;
import org.sofun.platform.opta.parser.rugby.RU02Parser;
import org.sofun.platform.opta.parser.rugby.RU10Parser;
import org.sofun.platform.opta.parser.rugby.RU6Parser;
import org.sofun.platform.opta.parser.tennis.TAB1Parser;
import org.sofun.platform.opta.parser.tennis.TAB2Parser;
import org.sofun.platform.opta.parser.tennis.TAB7Parser;

/**
 * Opta Service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface OptaService extends Serializable {

    // Below are opta properties file keys
    static final String PROP_FTP_HOST_KEY = "opta.ftp.host";

    static final String PROP_FTP_PORT_KEY = "opta.ftp.port";

    static final String PROP_FTP_USER_KEY = "opta.ftp.username";

    static final String PROP_FTP_PWD_KEY = "opta.ftp.password";

    /**
     * Synchronize RU feeds: RU1, RU2 and RU10
     * 
     * @throws OptaException
     */
    void syncRU() throws OptaException;

    /**
     * Synchronize RU live feeds (RU6)
     * 
     * @throws OptaException
     */
    void syncRULiveFeeds() throws OptaException;

    /**
     * Synchronize F1, F3 and F40 given Opta competition and season identifiers
     * for all competitions and seasons.
     * 
     * <p/>
     * 
     * This method does not sync live feeds.
     * 
     * @throws OptaException
     */
    void syncF() throws OptaException;

    /**
     * Synchronize F live feeds. (F7)
     * 
     * @throws OptaException
     */
    void syncFLiveFeeds() throws OptaException;

    /**
     * Rugby Competition fixtures.
     * 
     * @param file: an open {@link File} instance containing a RU10 feed.
     * @throws OptaException
     */
    RU10Parser ru10Sync(File file) throws OptaException;

    /**
     * Rugby Competition fixtures and results.
     * 
     * @param file: an open {@link File} instance containing a RU1 feed.
     * @throws OptaException
     */
    RU01Parser ru1Sync(File file) throws OptaException;

    /**
     * Rugby League standings.
     * 
     * @param file: an open {@link File} instance containing a RU2 feed.
     * @throws OptaException
     */
    RU02Parser ru2Sync(File file) throws OptaException;

    /**
     * Rugby Key events feeds.
     * 
     * @param file: an open {@link File} instance containing a RU6 feed.
     * @throws OptaException
     */
    RU6Parser ru6Sync(File file) throws OptaException;

    /**
     * Soccer Fixtures / Results
     * 
     * @param file: an open {@link File} instance containing a F1 feed.
     * @return a {@link F01Parser} instance
     * @throws OptaException
     */
    F01Parser f1Sync(File file) throws OptaException;

    /**
     * Soccer Standings
     * 
     * @param file: an open {@link File} instance containing a F3 feed.
     * @return a {@link F03Parser} instance
     * @throws OptaException
     */
    F03Parser f3Sync(File file) throws OptaException;

    /**
     * Soccer Match Live Basic
     * 
     * @param file: an open {@link File} instance containing a F7 feed.
     * @return a {@link F07Parser} instance
     * @throws OptaException
     */
    F07Parser f7Sync(File file) throws OptaException;

    /**
     * Soccer Squads
     * 
     * @param file: an open {@link File} instance containing a F40 feed.
     * @return a {@link F40Parser} instance
     * @throws OptaException
     */
    F40Parser f40Sync(File file) throws OptaException;

    /**
     * Get a processed feed given a filename
     * 
     * @param filename: a filename.
     * @return a {@link OptaProcessedFeed} or null if not found.
     */
    OptaProcessedFeed getProcessedFeedFor(String filename);

    /**
     * Returns the value associated to a given property key.
     * 
     * <p>
     * 
     * The properties are located within a opta.properties.
     * 
     * @return a {@link String} containing the value.
     */
    String getOptaProperty(String key);

    /**
     * Returns information about which seasons is available for a given football
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    Map<Integer, String[]> getFootballSeasonsByCompetitions();

    /**
     * Returns information about which seasons is available for a given rugby
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    Map<Integer, String[]> getRugbySeasonsByCompetitions();

    /**
     * Returns information about which seasons are available for F1.
     * 
     * @return a {@link List} of string (year)
     */
    List<String> getFormula1Seasons();

    /**
     * Synchronize F1 feeds
     * 
     * @throws OptaException
     */
    void syncFormula1() throws OptaException;

    /**
     * MR1 Formula One Live Race
     * 
     * @param file: an open {@link File} instance containing a MR1 feed.
     * @return a {@link MR1Parser} instance
     * @throws OptaException
     */
    MR1Parser mr1Sync(File file) throws OptaException;

    /**
     * MR2 Formula One Free Practice and Qualification Specification
     * 
     * @param file: an open {@link File} instance containing a MR2 feed.
     * @return a {@link MR2Parser} instance
     * @throws OptaException
     */
    MR2Parser mr2Sync(File file) throws OptaException;

    /**
     * MR4 Formula One Driver, Standings Specification
     * 
     * @param file: an open {@link File} instance containing a MR4 feed.
     * @return {@link MR4Parser} instance
     * @throws OptaException
     */
    MR4Parser mr4Sync(File file) throws OptaException;

    /**
     * MR5 Formula One Teams, Standings Specification
     * 
     * @param file: an open {@link File} instance containing a MR5 feed.
     * @return a {@link MR5Parser} instance
     * @throws OptaException
     */
    MR5Parser mr5Sync(File file) throws OptaException;

    /**
     * MR6 - Formula Calendar
     * 
     * @param file: an open {@link File} instance containing a MR6 feed.
     * @return a {@link MR6Parser} instance
     * @throws OptaException
     */
    MR6Parser mr6Sync(File file) throws OptaException;

    /**
     * Returns information about which seasons is available for a given tennis
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    Map<Integer, String[]> getTennisSeasonsByCompetitions();

    /**
     * TAB1 - Tennis Season Schedule
     * 
     * @param file: an open {@link File} instance containing a MR6 feed.
     * @return a {@link TAB1Parser} instance
     * @throws OptaException
     */
    TAB1Parser tab1Sync(File file) throws OptaException;

    /**
     * TAB2 - Tennis Competition
     * 
     * @param file: an open {@link File} instance containing a MR6 feed.
     * @return a {@link TAB2Parser} instance
     * @throws OptaException
     */
    TAB2Parser tab2Sync(File file) throws OptaException;

    /**
     * TAB7 -Tennis Live Match Feed Specification
     * 
     * 
     * @param file: an open {@link File} instance containing a MR6 feed.
     * @return a {@link TAB7Parser} instance
     * @throws OptaException
     */
    TAB7Parser tab7Sync(File file) throws OptaException;

    /**
     * Synchronize Tennis feeds: TAB1, TAB2
     * 
     * @throws OptaException
     */
    void syncTennis() throws OptaException;

    /**
     * Synchronize Tennis live feeds (TAB7)
     * 
     * @throws OptaException
     */
    void syncTennisLiveFeeds() throws OptaException;

    /**
     * Returns information about which seasons is available for a given cycling
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    Map<Integer, String[]> getCyclingSeasonsByCompetitions();

    /**
     * CY1 Cycling Fixtures (CNC)
     * 
     * @param file: an open {@link File} instance containing a CY1 feed.
     * @return a {@link CY1Parser} instance
     * @throws OptaException
     */
    CY1Parser cy1Sync(File file) throws OptaException;

    /**
     * CY40 (CMP) Cycling Competition, Teams & Riders
     * 
     * @param file: an open {@link File} instance containing a CY40 feed.
     * @return a {@link CY40Parser} instance
     * @throws OptaException
     */
    CY40Parser cy40sync(File file) throws OptaException;

    /**
     * (CSF) Cycling standings feed description.
     * 
     * @param file: an open {@link File} instance containing a CSF feed.
     * @return a {@link CSFParser} instance
     * @throws OptaException
     */
    CSFParser cyCSFCync(File file) throws OptaException;

    /**
     * Synchronize cy40 feeds: CY1, CY40
     * 
     * @throws OptaException
     */
    void syncCycling() throws OptaException;

    /**
     * BB1 Fixtures / Results.
     * 
     * @param file: an open {@link File} instance containing a BB1 feed.
     * @return a {@link BB1Parser} instance
     * @throws OptaException
     */
    BB1Parser bb1Sync(File file) throws OptaException;

    /**
     * BB9 Match Statistic
     * 
     * @param file: an open {@link File} instance containing a BB9 feed.
     * @return a {@link BB9Parser} instance
     * @throws OptaException
     */
    BB9Parser bb9Sync(File file) throws OptaException;

    /**
     * Synchronize Basket feeds: BB1 and BB3
     * 
     * @throws OptaException
     */
    void syncBasket() throws OptaException;

    /**
     * Returns information about which seasons is available for a given basket
     * competition.
     * 
     * @return a mapping from comp_id -> seasons
     */
    Map<Integer, String[]> getBasketSeasonsByCompetitions();

}
