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

package org.sofun.platform.facebook;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class FacebookUtils {

    public static final String GRAPH_URL = "http://graph.facebook.com";

    public static final String getFacebookTokenFromURL(String url)
            throws MalformedURLException {
        if (url == null) {
            return null;
        }
        URL graphUrl = new URL(url);
        return getQueryMap(graphUrl.getQuery()).get("access_token");
    }

    public static final Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    public static final URL getPictureFor(Long fbId, String type) {
        if (type == null) {
            try {
                return new URL(GRAPH_URL + '/' + fbId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return new URL(GRAPH_URL + '/' + fbId + "/picture?type=" + type);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
