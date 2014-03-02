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

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.platform.legigame.api.Configuration;

/**
 * Test access to legigame configuration
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestConfiguration extends TestCase {

    @Test
    public void testAccess() {

        assertNotNull(Configuration.getProperties().get("httpauth.username"));
        assertNotNull(Configuration.getProperties().get("httpauth.password"));
        assertNotNull(Configuration.getProperties().get("api.login"));
        assertNotNull(Configuration.getProperties().get("api.password"));
        assertNotNull(Configuration.getProperties().get(
                "api.url.createOrUpdate"));
        assertNotNull(Configuration.getProperties().get("api.url.read"));
        assertNotNull(Configuration.getProperties().get("email"));
        assertNotNull(Configuration.getProperties().get("betkup.community.id"));

    }

}
