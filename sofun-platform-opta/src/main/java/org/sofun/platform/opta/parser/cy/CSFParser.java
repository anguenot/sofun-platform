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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableColumn;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableKey;
import org.sofun.core.api.sport.tournament.table.TournamentLeagueTableRow;
import org.sofun.core.api.sport.tournament.table.TournamentRoundLeagueTable;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableColumnImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableKeyImpl;
import org.sofun.core.sport.tournament.table.TournamentLeagueTableRowImpl;
import org.sofun.core.sport.tournament.table.TournamentRoundLeagueTableImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 
 * (CSF) Cycling standings feed description
 * 
 * <p>
 * 
 * The file Classifications contains information about the positions of the
 * cyclists in a specific stage of the race.
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following:
 * 
 * <ul>
 * <li>CSF_CCCCC_JJ.Xml</li>
 * <li>CSF: A prefix that identify this type of file</li>
 * <li>CCCCC: ID of the competition (5 digits)</li>
 * <li>JJ: Stage number in two digits</li>
 * </ul>
 * 
 * <p>
 * 
 * Implementation based on version from 07/05/2012
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class CSFParser extends AbstractOptaParser {

    public static final String sportName = "Cycling";

    /* Prefix for UUID */
    public static final String PREFIX = "CY";

    private SportService sports;

    private Tournament tournament;

    private TournamentSeason tournamentSeason;

    private TournamentStage tournamentStage;

    private TournamentRound tournamentRound;

    private TournamentRoundLeagueTable table;

    public static final String TABLE_TYPE_INDIVIDUAL_STAGE = "INDIVIDUAL_STAGE";

    public static final String TABLE_TYPE_MOUNTAIN_STAGE = "MOUNTAIN_STAGE";

    public static final String TABLE_TYPE_YOUNG_STAGE = "YOUNG_STAGE";

    public static final String TABLE_TYPE_GREEN_STAGE = "GREEN_STAGE";

    public static final String TABLE_TYPE_TEAM_STAGE = "TEAM_STAGE";

    public static final String TABLE_TYPE_INDIVIDUAL_AGGREGATED = "INDIVIDUAL_AGGREGATED";

    public static final String TABLE_TYPE_MOUNTAIN_AGGREGATED = "MOUNTAIN_AGGREGATED";

    public static final String TABLE_TYPE_YOUNG_AGGREGATED = "YOUNG_AGGREGATED";

    public static final String TABLE_TYPE_GREEN_AGGREGATED = "GREEN_AGGREGATED";

    public static final String TABLE_TYPE_TEAM_AGGREGATED = "TEAM_AGGREGATED";

    /* ID of the general standings in this file */
    private String CLFGRAL;

    /* Stage (day) numbers */
    private String JOR;

    private String JOR_CLF;

    @SuppressWarnings("unused")
    private EntityManager em;

    private final SecureRandom randomGenerator = new SecureRandom();

    public CSFParser(String path) {
        super(path);
        parseXmlFile();
    }

    public CSFParser(File file) {
        super(file);
        parseXmlFile();
    }

    public CSFParser(File file, SportService sports, EntityManager em) {
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

            /* Stage (day) number */
            JOR = attributes.getValue("JOR");
            if (JOR.length() == 1) {
                JOR = "0" + JOR;
            }

            /*
             * Unique competition code across the seasons. Together the
             * attribute TEMP can be the ID of the competition
             */
            final String IDUNICO = attributes.getValue("IDUNICO");

            /* ID of the general standings in this file */
            CLFGRAL = attributes.getValue("CLFGRAL");

            tournament = sports.getSportTournamentByCode(IDUNICO);
            tournamentSeason = sports.getSeasonByUUID(tournament,
                    Long.valueOf(ID));

        } else if (qName.equals("JOR")) {

            /* The ID of the stage. */
            String ID = JOR_CLF = attributes.getValue("ID");
            if (ID.length() == 1) {
                ID = "0" + ID;
            }
            if (!ID.equals(CLFGRAL)) {
                tournamentStage = tournamentSeason.getStageByName(ID);
            } else {
                // General aggregated ranking after stage.
                tournamentStage = tournamentSeason.getStageByName(JOR);
            }

        } else if (qName.equals("CLF")) {
            
            if (tournamentStage == null) {
                return;
            }

            /* The ID of the stage */
            String ID = attributes.getValue("ID");

            /*
             * ID of the standing 1 = Individual stage 2 = Mountain stage 3 =
             * Points (green jersey in the stage) 4 = Teams in the stage 5 =
             * Young cyclists standing in the stage
             */
            final int PrbID = Integer.valueOf(attributes.getValue("PrbID"));

            String currentTable = null;
            if (!JOR_CLF.equals(CLFGRAL)) {
                tournamentRound = tournamentStage.getRoundByLabel(ID);
                if (PrbID == 1) {
                    currentTable = CSFParser.TABLE_TYPE_INDIVIDUAL_STAGE;
                } else if (PrbID == 2) {
                    currentTable = CSFParser.TABLE_TYPE_MOUNTAIN_STAGE;
                } else if (PrbID == 3) {
                    currentTable = CSFParser.TABLE_TYPE_GREEN_STAGE;
                } else if (PrbID == 4) {
                    currentTable = CSFParser.TABLE_TYPE_TEAM_STAGE;
                } else if (PrbID == 5) {
                    currentTable = CSFParser.TABLE_TYPE_YOUNG_STAGE;
                }
            } else {
                tournamentRound = tournamentStage.getRounds().get(0);
                if (PrbID == 1) {
                    currentTable = CSFParser.TABLE_TYPE_INDIVIDUAL_AGGREGATED;
                } else if (PrbID == 2) {
                    currentTable = CSFParser.TABLE_TYPE_MOUNTAIN_AGGREGATED;
                } else if (PrbID == 3) {
                    currentTable = CSFParser.TABLE_TYPE_GREEN_AGGREGATED;
                } else if (PrbID == 4) {
                    currentTable = CSFParser.TABLE_TYPE_TEAM_AGGREGATED;
                } else if (PrbID == 5) {
                    currentTable = CSFParser.TABLE_TYPE_YOUNG_AGGREGATED;
                }
            }

            if (currentTable != null) {
                
                table = tournamentRound.getTableByType(currentTable);
                if (table == null) {
                    table = new TournamentRoundLeagueTableImpl(
                            tournamentRound.getName(), currentTable);
                    table.setUUID(randomGenerator.nextInt(1000000));
                    table.setKeys(getTableKeys());
                    table.setTournamentRound(tournamentRound);
                    tournamentRound.addTable(table);
                }

            }

        } else if (qName.equalsIgnoreCase("Puesto")) {
            
            /* Order in the ranking */
            final String ID = attributes.getValue("ID");

            /* Id of the cyclist */
            final String CJ = attributes.getValue("CJ");

            /* Team code */
            final String CE = attributes.getValue("CE");

            SportContestant contestant = null;
            if (CJ != null && !CJ.isEmpty() && !CJ.equals("0")) {
                contestant = sports.getSportContestantPlayer(PREFIX + "c" + CJ);
            } else {
                contestant = sports.getSportContestantTeam(PREFIX + "t" + CE);
            }

            if (contestant != null && table != null) {
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
                column.setColumnValue(ID);
            }

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equals("CLF")) {
            table = null;
        }

    }

    private List<TournamentLeagueTableKey> getTableKeys() {
        final List<TournamentLeagueTableKey> keys = new ArrayList<TournamentLeagueTableKey>();
        keys.add(new TournamentLeagueTableKeyImpl("pos", "pos"));
        return keys;
    }

}
