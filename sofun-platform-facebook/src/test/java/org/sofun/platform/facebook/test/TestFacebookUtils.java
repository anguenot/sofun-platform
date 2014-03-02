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

package org.sofun.platform.facebook.test;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.platform.facebook.FacebookUtils;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestFacebookUtils extends TestCase {

    @Test
    public void testTokenStr() throws Exception {
        String url = "https://graph.facebook.com/me?access_token=182858561743452|2.SCI6nbJJcjUrL_52mDATUA__.3600.1296162000-723188363|rtFQcfyjcym-QIKxd8KQ3eMVDNg&expires=6606";
        String token = FacebookUtils.getFacebookTokenFromURL(url);
        assertEquals("182858561743452|2.SCI6nbJJcjUrL_52mDATUA__.3600.1296162000-723188363|rtFQcfyjcym-QIKxd8KQ3eMVDNg",token);
    }

}
