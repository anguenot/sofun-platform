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

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.core.CoreUtil;
import org.sofun.core.api.exception.CoreException;

/**
 * Core Utils Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestUtils extends TestCase {

    @Test
    public void testStringToDate() throws Exception {

        String d1 = "2011-02-15T02:00:00+01:00";

        Date D1 = CoreUtil.getDateFromISO8601DateString(d1);
        assertNotNull(D1);

        String strDateFormat = "HHmmss:zzz";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        if (sdf.format(D1).endsWith("CET")) {
            assertEquals("020000:CET", sdf.format(D1));
        } else if (sdf.format(D1).endsWith("UTC")) {
            assertEquals("010000:UTC", sdf.format(D1));
        }

        strDateFormat = "HHmmss:zzzz";
        sdf = new SimpleDateFormat(strDateFormat);
        if (sdf.format(D1).endsWith("Central European Time")) {
            assertEquals("020000:Central European Time", sdf.format(D1));
        } else if (sdf.format(D1).endsWith("UTC")) {
            assertEquals("010000:UTC", sdf.format(D1));
        }

        strDateFormat = "HHmmss:Z";
        sdf = new SimpleDateFormat(strDateFormat);
        if (sdf.format(D1).endsWith("+0100")) {
            assertEquals("020000:+0100", sdf.format(D1));
        } else if (sdf.format(D1).endsWith("0000")) {
            assertEquals("010000:+0000", sdf.format(D1));
        }

        // Incorrect
        String d3 = "2011-02-15T00:00:00+01";

        boolean raised = false;
        try {
            CoreUtil.getDateFromISO8601DateString(d3);
        } catch (CoreException ce) {
            raised = true;
        }
        assertTrue(raised);

    }

}
