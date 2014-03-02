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

package org.sofun.platform.opta.parser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Abstract Opta Parser.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public abstract class AbstractOptaParser extends DefaultHandler {

    private static final Log log = LogFactory.getLog(AbstractOptaParser.class);

    protected File f;

    protected String tempVal;

    public AbstractOptaParser() {
        super();
    }

    public AbstractOptaParser(String path) {
        this();
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(path);
        if (url != null) {
            this.f = new File(url.getFile());
        } else {
            if (path != null) {
                log.error("Cannot find resource with path=" + path
                        + " Cannot parse");
            } else {
                log.error("null path given. Cannot parse.");
            }
        }
    }

    public AbstractOptaParser(File file) {
        this();
        this.f = file;
    }

    public void parseXmlFile() {
        SAXParserFactory factory = SAXParserFactory.newInstance();

        try {
            SAXParser parser = factory.newSAXParser();
            if (f != null) {
                parser.parse(f, this);
            } else {
                log.error("File is null. Cannot parse.");
            }
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    abstract public void startElement(String uri, String localName,
            String qName, Attributes attributes) throws SAXException;

    @Override
    abstract public void endElement(String uri, String localName, String qName)
            throws SAXException;

}
