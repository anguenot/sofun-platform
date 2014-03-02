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

package org.sofun.platform.web.rest.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * JSON utils
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class JSONUtil {

    public static final Map<String, String> getMapFromJSON(String json) {

        Gson gson = new Gson();

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();

        try {
            return gson.fromJson(json, type);
        } catch (JsonParseException e) {
            return new HashMap<String, String>();
        }

    }

    public static final List<String> getListFromJSON(String json) {

        Gson gson = new Gson();

        Type type = new TypeToken<List<String>>() {
        }.getType();

        try {
            return gson.fromJson(json, type);
        } catch (JsonParseException e) {
            return new ArrayList<String>();
        }

    }

}
