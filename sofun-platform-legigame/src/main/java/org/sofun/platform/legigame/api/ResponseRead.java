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

package org.sofun.platform.legigame.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;
import org.sofun.platform.legigame.AbstractResponse;

/**
 * Response received while invoking a Read POST request.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ResponseRead extends AbstractResponse {

    /** A list of xml values with their entry key. */
    private Map<String, String> properties;

    public ResponseRead(String xml) {
        super(xml);
    }

    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, String>();
            final String xpath = "//Root/Player";
            final Node node = getDocument().selectSingleNode(xpath);
            if (node != null) {
                // Always the case
                Element el = (Element) node;
                for (@SuppressWarnings("unchecked")
                Iterator<Element> i = el.elementIterator(); i.hasNext();) {
                    Element child = i.next();
                    properties.put(child.getName(), child.getText());
                }
                // One level iteration is enough.
            }
        }
        return properties;
    }

}
