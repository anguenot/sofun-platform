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

package org.sofun.platform.opta.parser.bb;

import java.io.File;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.sport.SportService;
import org.sofun.platform.opta.parser.AbstractOptaParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * BB9 Match Statistic
 * 
 * <p>
 * 
 * This feed offers subscribers a complete match statistics package. For each
 * player and team there is a full breakdown of the match statistics; which can
 * be found in the BB54 xml file.
 * 
 * <p>
 * 
 * The file naming convention used for this feed is the following:
 * 
 * BB9-{competition_id}-season_id}-{sport_id}-{game_id}.xml
 * 
 * @author @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class BB9Parser extends AbstractOptaParser {

    private static final Log log = LogFactory.getLog(BB1Parser.class);

    public static final String SPORT_NAME = "Basket";

    public static final String TEAM_PREFIX = "bbt";

    public static final String GAME_PREFIX = "bb";

    private SportService sports;

    private EntityManager em;

    public BB9Parser(String path) {
        super(path);
        parseXmlFile();
    }

    public BB9Parser(File file) {
        super(file);
        parseXmlFile();
    }

    public BB9Parser(File file, SportService sports, EntityManager em) {
        super(file);
        this.sports = sports;
        this.em = em;
        parseXmlFile();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub

    }

}
