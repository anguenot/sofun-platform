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
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 
 * CY40 (CMP) Cycling Competition, Teams & Riders
 * 
 * <p>
 * 
 * This feed provides all the necessary information and id’s for a given
 * competition including:
 * 
 * <ul>
 * <li>Teams</li>
 * <li>Cyclists</li>
 * <li>Officials</li>
 * <li>Coaches</li>
 * </ul>
 * 
 * <p>
 * 
 * The File naming convention used for this feed is the following: CMP_
 * -{competition_id}.xml
 * 
 * <p>
 * 
 * Implementation based on version 1.1 from May 2012
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class CY40Parser extends AbstractOptaParser {

    public static final String sportName = "Cycling";

    /* Prefix for UUID */
    public static final String PREFIX = "CY";

    private SportService sports;

    private Tournament tournament;

    private TournamentSeason tournamentSeason;

    private final List<SportContestant> teams = new ArrayList<SportContestant>();

    private final List<SportContestant> cyclists = new ArrayList<SportContestant>();

    private EntityManager em;

    public CY40Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public CY40Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public CY40Parser(File file, SportService sports, EntityManager em) {
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

            /*
             * Unique competition code across the seasons. Together the
             * attribute TEMP can be the ID of the competition
             */
            final String IDUNICO = attributes.getValue("IDUNICO");

            tournament = sports.getSportTournamentByCode(IDUNICO);
            tournamentSeason = sports.getSeasonByUUID(tournament,
                    Long.valueOf(ID));

        } else if (qName.equals("Eq")) {

            /* Id of the team */
            final String ID = attributes.getValue("ID");
            final String tUUID = PREFIX + "t" + ID;

            /* Name of the team */
            final String NB = attributes.getValue("NB");

            SportContestant team = sports.getSportContestantTeam(tUUID);
            Map<String, String> props = new HashMap<String, String>();
            if (team == null) {
                team = new SportContestantImpl(tUUID, SportContestantType.TEAM);
                em.persist(team);
            }
            team.setName(NB);

            /* Country code (3 characters) */
            final String PA = attributes.getValue("PA");

            /* ICO country codes (3 characters) */
            final String PACOI = attributes.getValue("PACOI");

            /* State/region of the team */
            final String PR = attributes.getValue("PR");

            /* Short name of the team (3 characters) */
            final String COR = attributes.getValue("COR");

            /* Web site URL of the club */
            final String URL = attributes.getValue("URL");

            props.put("nat", PA);
            props.put("PACOI", PACOI);
            props.put("PR", PR);
            props.put("COR", COR);
            props.put("URL", URL);

            team.setProperties(props);

            teams.add(team);

        } else if (qName.equals("Jg")) {

            /* Cyclist ID */
            final String ID = attributes.getValue("ID");
            final String cUUID = PREFIX + "c" + ID;

            /* Name of the team */
            final String NB = attributes.getValue("NB");

            SportContestant cyclist = sports.getSportContestantPlayer(cUUID);
            Map<String, String> props = new HashMap<String, String>();
            new HashMap<String, String>();
            if (cyclist == null) {
                cyclist = new SportContestantImpl(cUUID,
                        SportContestantType.INDIVIDUAL);
                em.persist(cyclist);
            }
            cyclist.setName(NB);

            /* Cyclist nationality (3 characters) */
            final String PA = attributes.getValue("PA");

            /* ICO country codes (3 characters) */
            final String PACOI = attributes.getValue("PACOI");

            /* ICO country codes (3 characters) */
            final String FCH = attributes.getValue("FCH");

            /* Cyclist height (0 = unknown) */
            final String AL = attributes.getValue("AL");

            /* Cyclist height (0 = unknown) */
            final String PE = attributes.getValue("PE");

            /* Cyclist jersey number in this competition */
            final String DO = attributes.getValue("DO");

            /* Team of the cyclist */
            final String EQ = attributes.getValue("EQ");

            props.put("nat", PA);
            props.put("PACOI", PACOI);
            props.put("FCH", FCH);
            props.put("AL", AL);
            props.put("PE", PE);
            props.put("DO", DO);
            props.put("EQ", EQ);
            cyclist.setProperties(props);

            /* Team ID of the cyclist */
            final String CE = attributes.getValue("CE");
            final String tUUID = PREFIX + "t" + CE;
            SportContestant team = sports.getSportContestantTeam(tUUID);

            if (team != null && !cyclist.getTeams().contains(team)) {
                cyclist.addTeam(team);
                team.addPlayer(cyclist);
            }

            cyclists.add(cyclist);

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        if (qName.equals("Competicion")) {
            for (SportContestant team : teams) {
                if (!tournamentSeason.getConstestants().contains(team)) {
                    tournamentSeason.addContestant(team);
                }
            }
        }
    }

}
