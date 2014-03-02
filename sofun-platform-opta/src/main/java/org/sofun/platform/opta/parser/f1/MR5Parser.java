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

package org.sofun.platform.opta.parser.f1;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.api.sport.tournament.table.TournamentSeasonLeagueTable;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableColumnImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableKeyImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableRowImpl;
import org.sofun.core.sport.tournament.table.TournamentSeasonLeagueTableImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * MR5- Formula One Teams, Standings Specification
 * 
 * <p/>
 * 
 * This is essentially a live constructor standings feed cumulatively updated
 * after each Grand Prix.
 * 
 * <p/>
 * 
 * Subscribers are advised that we put files with a .tmp file extension until
 * the file is fully deployed and then rename to the actual described filename
 * (for example: F1_STANDINGS_TEAMS_2012.tmp to F1_STANDINGS_TEAMS_2012.xml).
 * 
 * <p/>
 * 
 * Once a production environment is established between Opta Sports and the
 * subscriber, Opta can deliver a live or post race standings feed upon the
 * following events:
 * 
 * <p>
 * Live
 * </p>
 * <ul>
 * <li>Changing constructor position during race</li>
 * <li>Changing constructor points during the race</li>
 * </ul>
 * <p>
 * Post race
 * </p>
 * <ul>
 * <li>Constructor final position</li>
 * <li>Constructor final standings points</li>
 * </ul>
 * 
 * <p/>
 * 
 * The File naming convention used for this feed is the following:
 * 
 * <p>
 * Non live
 * </p>
 * <ul>
 * <li>F1_STANDINGS_TEAMS_YEAR_.xml (e.g. F1_STANDINGS_TEAMS_2012.xml)</li>
 * </ul>
 * 
 * <p>
 * Live
 * </p>
 * <ul>
 * <li>(official timings rights) F1_STANDINGS_TEAMS_LIVE_YEAR.xml (e.g.
 * F1_STANDINGS_TEAMS_LIVE_2012.xml)</li>
 * </ul>
 * 
 * <p/>
 * 
 * Based on version specification documentation dated from 29 February 2012
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class MR5Parser extends AbstractOptaParser {

    public static final String TABLE_TYPE_LIVE = "TEAMS_LIVE";

    public static final String TABLE_TYPE_POST_RACE = "TEAMS_POST_RACE";

    public static final String sportName = "F1";

    public static final String PREFIX_TEAM = "f1t";

    private Tournament tournament;

    private TournamentSeason season;

    private TournamentSeasonLeagueTable table;

    private String tableType = TABLE_TYPE_POST_RACE; // default

    private String seasonLabel;

    private EntityManager em;

    private SportService sports;

    /* Current/finished position in the race */
    private String pos;

    /* Unique constructor ID number assigned by Opta */
    private String tid;

    /* Constructor /Team name */
    private String team;

    /* Car make */
    private String team_carbike;

    /* Engine name */
    private String team_engine;

    /* Total season points. */
    private String points;

    /* Number of victories in the season to date */
    private String victories;

    private final SecureRandom randomGenerator = new SecureRandom();

    public MR5Parser(String path) {
        super(path);
        parseXmlFile();
        this.tableType = TABLE_TYPE_POST_RACE;
    }

    public MR5Parser(File file) {
        super(file);
        parseXmlFile();
        this.tableType = TABLE_TYPE_POST_RACE;
    }

    public MR5Parser(File file, SportService sports, EntityManager em,
            String seasonLabel, String type) {
        super(file);
        this.em = em;
        this.sports = sports;
        this.seasonLabel = seasonLabel;
        parseXmlFile();
        this.tableType = type;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        tempVal = "";

        if (qName.equalsIgnoreCase("Block")) {

            tournament = sports.getSportTournamentByCode(sportName);
            season = sports.getSeasonByYearLabel(tournament, seasonLabel);
            table = season.getTableByType(tableType);
            if (table == null) {
                table = new TournamentSeasonLeagueTableImpl(season.getName(),
                        tableType);
                table.setUUID(randomGenerator.nextInt(1000000));
                table.setKeys(getTableKeys());
                table.setTournamentSeason(season);
                season.addTable(table);
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equalsIgnoreCase("pos")) {
            pos = tempVal;
        } else if (qName.equalsIgnoreCase("tid")) {
            tid = tempVal;
        } else if (qName.equalsIgnoreCase("team")) {
            team = tempVal;
        } else if (qName.equalsIgnoreCase("team_carbike")) {
            team_carbike = tempVal;
        } else if (qName.equalsIgnoreCase("team_engine")) {
            team_engine = tempVal;
        } else if (qName.equalsIgnoreCase("points")) {
            points = tempVal;
        } else if (qName.equalsIgnoreCase("victories")) {
            victories = tempVal;
        } else if (qName.equalsIgnoreCase("standings")) {

            final String tUUID = PREFIX_TEAM + tid;
            SportContestant contestant = sports.getSportContestantTeam(tUUID);
            if (contestant == null) {
                contestant = new SportContestantImpl(tUUID,
                        SportContestantType.TEAM);
                em.persist(contestant);
            }
            contestant.setName(team);
            Map<String, String> props = new HashMap<String, String>();
            props.put("team_carbike", team_carbike);
            props.put("team_engine", team_engine);
            contestant.setProperties(props);

            if (!season.getConstestants().contains(contestant)) {
                season.addContestant(contestant);
            }

            TournamentLeagueTableRow row = table.getRowFor(contestant);
            if (row == null) {
                row = new TournamentLeagueTableRowImpl();
                row.setSportContestant(contestant);
                row.setLeagueTable(table);
                table.addRow(row);
            }

            TournamentLeagueTableColumn column = row.getColumn("pos");
            if (column == null) {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey("pos");
                row.addColumn(column);
                column.setRow(row);
            }
            column.setColumnValue(pos);

            column = row.getColumn("points");
            if (column == null) {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey("points");
                row.addColumn(column);
                column.setRow(row);
            }
            column.setColumnValue(points);

            column = row.getColumn("victories");
            if (column == null) {
                column = new TournamentLeagueTableColumnImpl();
                column.setColumnKey("victories");
                row.addColumn(column);
                column.setRow(row);
            }
            column.setColumnValue(victories);

        }

    }

    private List<TournamentLeagueTableKey> getTableKeys() {
        final List<TournamentLeagueTableKey> keys = new ArrayList<TournamentLeagueTableKey>();
        keys.add(new TournamentLeagueTableKeyImpl("pos", "pos"));
        keys.add(new TournamentLeagueTableKeyImpl("points", "points"));
        keys.add(new TournamentLeagueTableKeyImpl("victories", "victories"));
        return keys;
    }

    public TournamentSeason getSeason() {
        return season;
    }

}
