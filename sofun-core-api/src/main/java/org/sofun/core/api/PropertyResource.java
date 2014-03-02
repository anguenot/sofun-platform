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

package org.sofun.core.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Property resource abstract class.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public abstract class PropertyResource {

    private static final Log log = LogFactory.getLog(PropertyResource.class);

    protected static final Properties prop = new Properties();

    protected PropertyResource() {
        load();
    }

    private void load() {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(getResourceName());
        if (is != null) {
            try {
                prop.load(is);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    protected abstract String getResourceName();

    public boolean isValidCode(String code) {
        Validate.notNull(code);
        return prop.containsKey(code.toUpperCase());
    }

}
