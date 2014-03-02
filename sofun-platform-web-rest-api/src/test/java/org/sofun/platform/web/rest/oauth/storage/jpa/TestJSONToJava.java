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

package org.sofun.platform.web.rest.oauth.storage.jpa;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.platform.web.rest.util.JSONUtil;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestJSONToJava extends TestCase {

    @Test
    public void testJSONToMap() {
        
        String json = "{'key1':'123','key2':'456'}";
        Map<String, String> m = JSONUtil.getMapFromJSON(json);
        
        assertEquals("123", m.get("key1"));
        assertEquals("456", m.get("key2"));
        
    }

}
