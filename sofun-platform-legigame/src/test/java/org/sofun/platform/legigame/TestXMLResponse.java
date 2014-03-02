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

package org.sofun.platform.legigame;

import java.io.File;
import java.net.URL;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.sofun.platform.legigame.api.ResponseCreateOrUpdate;
import org.sofun.platform.legigame.api.ResponseRead;

/**
 * XML Response parser test case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestXMLResponse extends TestCase {

    private Document getDocument(String path) throws Exception {
        SAXReader reader = new SAXReader();
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(path);
        File file = new File(url.getFile());
        return reader.read(file);
    }

    private String getXMLFrom(String path) throws Exception {
        Document document = getDocument(path);
        return document.asXML();
    }

    @Test
    public void testCreateOrUpdateResponseNoError() throws Exception {

        String xml = getXMLFrom("response01.xml");
        ResponseCreateOrUpdate resp = new ResponseCreateOrUpdate(xml);
        assertEquals(0, resp.getStatus());
        assertEquals(0, resp.getIteration());
        assertEquals(0, resp.getErrors().size());

    }

    @Test
    public void testCreateOrUpdateResponseNoError_2() throws Exception {

        String xml = getXMLFrom("response02.xml");
        ResponseCreateOrUpdate resp = new ResponseCreateOrUpdate(xml);
        assertEquals(2, resp.getStatus());
        assertEquals(2, resp.getIteration());
        assertEquals(0, resp.getErrors().size());

    }

    @Test
    public void testCreateOrUpdateResponseWithErrors() throws Exception {

        String xml = getXMLFrom("response03.xml");
        ResponseCreateOrUpdate resp = new ResponseCreateOrUpdate(xml);
        assertEquals(-2, resp.getStatus());
        assertEquals(0, resp.getIteration());
        assertEquals(5, resp.getErrors().size());

        for (Entry<String, String> e : resp.getErrors().entrySet()) {
            assertEquals("-1", e.getValue());
        }

    }

    @Test
    public void testCreateOrUpdateResponseWithErrors_2() throws Exception {

        String xml = getXMLFrom("response04.xml");
        ResponseCreateOrUpdate resp = new ResponseCreateOrUpdate(xml);
        assertEquals(-2, resp.getStatus());
        assertEquals(0, resp.getIteration());
        assertEquals(5, resp.getErrors().size());

        for (Entry<String, String> e : resp.getErrors().entrySet()) {
            if (e.getKey().equals("Lastname") || e.getKey().equals("PlayerID")) {
                assertEquals("-2", e.getValue());
            } else if (e.getKey().equals("ActivationCode")) {
                assertEquals("ABC", e.getValue());
            } else {
                assertEquals("-1", e.getValue());
            }
        }

    }

    @Test
    public void testReadResponseNoError() throws Exception {

        String xml = getXMLFrom("response05.xml");
        ResponseRead resp = new ResponseRead(xml);
        assertEquals(0, resp.getStatus());
        assertEquals(1, resp.getIteration());
        assertEquals(16, resp.getProperties().size());

        assertEquals("JC", resp.getProperties().get("Firstname"));
        assertEquals("DUMORTIER", resp.getProperties().get("Lastname"));
        assertEquals("24/03/1964", resp.getProperties().get("BirthDate"));
        assertEquals("SAINT DENIS EN BUGEY",
                resp.getProperties().get("CityOfBirth"));
        assertEquals("12", resp.getProperties().get("DepartmentOfBirth"));
        assertEquals("FRANCE", resp.getProperties().get("CountryOfBirth"));
        assertEquals("FR7630066107610001032000100",
                resp.getProperties().get("IBAN"));
        assertEquals("ben.gdm@gmail.com", resp.getProperties().get("Email"));
        assertEquals("J83TLM", resp.getProperties().get("ActivationCode"));
        assertEquals("57 BD DE GRENELLE", resp.getProperties().get("Address"));
        assertEquals("75015", resp.getProperties().get("ZipCode"));
        assertEquals("PARIS", resp.getProperties().get("City"));
        assertEquals("FRANCE", resp.getProperties().get("Country"));
        assertEquals("1", resp.getProperties().get("Attribut2"));
        assertEquals("12/10/2010", resp.getProperties()
                .get("DateLastIteration"));
        assertEquals("0", resp.getProperties().get("ActivationStatus"));

    }

    @Test
    public void testReadResponseWithErrors() throws Exception {

        String xml = getXMLFrom("response06.xml");
        ResponseRead resp = new ResponseRead(xml);
        assertEquals(-2, resp.getStatus());
        assertEquals(0, resp.getIteration());

    }

}
