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
 * Response received while invoking a Create Or Update POST request.
 * 
 * <p>
 * 
 * Response is received after a POST request as XML
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ResponseCreateOrUpdate extends AbstractResponse {

    /**
     * An error list
     * 
     * <p>
     * 
     * It is void and useless if the create or update operation succeed.
     * 
     * <p>
     * 
     * In case of errors, where each key is a parameter and its value the
     * detailed error cause if the create or update operation failed.
     * 
     * <ul>
     * <li>-1 column is required</li>
     * <li>-2 wrong format value</li>
     * <li>-3 value is too long</li>
     * <li>-4 unknown key</li>
     * </ul>
     */
    private Map<String, String> errors = null;

    public ResponseCreateOrUpdate(String xml) {
        super(xml);
    }

    public Map<String, String> getErrors() {
        if (errors == null) {
            errors = new HashMap<String, String>();
            final String xpath = "//Root/Error";
            final Node node = getDocument().selectSingleNode(xpath);
            if (node != null) {
                // Always the case
                Element el = (Element) node;
                for (@SuppressWarnings("unchecked")
                Iterator<Element> i = el.elementIterator(); i.hasNext();) {
                    Element child = i.next();
                    errors.put(child.getName(), child.getText());
                }
                // One level iteration is enough.
            }
        }
        return errors;
    }

}
