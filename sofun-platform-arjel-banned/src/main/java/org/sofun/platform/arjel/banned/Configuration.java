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

package org.sofun.platform.arjel.banned;

import java.io.InputStream;
import java.util.Properties;

/**
 * ARJEL Banned Component Configuration.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class Configuration {

    private static Properties properties = new Properties();

    /**
     * Returns component level properties.
     * 
     * <p>
     * 
     * arjel.properties file is deployed at package level.
     * 
     * @return a {@link Properties} instance.
     */
    public synchronized static Properties getProperties() {
        final String propertiesFile = "arjel.properties";
        if (properties.size() == 0) {
            InputStream raw = null;
            try {
                raw = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(propertiesFile);
                if (raw != null) {
                    properties.load(raw);
                    raw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

}
