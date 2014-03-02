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

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Abstract Legigame response.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public abstract class AbstractResponse {

    private static final Log log = LogFactory.getLog(AbstractResponse.class);

    /**
     * The operation status.
     * 
     * <p>
     * 
     * A status which can be considered as:
     * 
     * <ul>
     * <li>-1 authentication failed</li>
     * <li>-2 one or more parameter is missing or incorrect</li>
     * <li>The player’s folder status if positive (Create or update operation
     * succeed)</li>
     * </ul>
     * 
     */
    private Integer status = null;

    /**
     * The actual iteration reached within notification program if the create or
     * update operation succeed and zero otherwise
     */
    private Integer iteration = null;

    /** XML response */
    private final String xml;

    /** DOM4J internal XML representation */
    private transient Document doc;

    public AbstractResponse(String xml) {
        this.xml = xml;
    }

    protected Document getDocument() {
        SAXReader reader = new SAXReader();
        if (doc == null) {
            try {
                doc = reader.read(new StringReader(xml));
            } catch (DocumentException e) {
                log.error("An error occured trying to parse XML:"
                        + e.getMessage());
                if (log.isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }
        return doc;
    }

    public int getStatus() {
        if (status == null) {
            final String xpath = "//Root/Status";
            final Node node = getDocument().selectSingleNode(xpath);
            if (node != null) {
                status = Integer.valueOf(node.getText());
            }
        }
        return status;
    }

    public int getIteration() {
        if (iteration == null) {
            final String xpath = "//Root/Iteration";
            final Node node = getDocument().selectSingleNode(xpath);
            if (node != null) {
                iteration = Integer.valueOf(node.getText());
            }
        }
        return iteration;
    }

}
