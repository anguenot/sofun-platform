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

package org.test.sofun.core;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.core.CoreConstants;
import org.sofun.core.CoreImpl;
import org.sofun.core.api.Core;

/**
 * Core Implementation Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestCoreImpl extends TestCase {

    private static final Core core = new CoreImpl();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testVersion() {
        assertEquals(CoreConstants.VERSION, core.getVersion());
    }

    @Test
    public void testGetPoweredBy() {
        assertEquals(CoreConstants.POWERED_BY, core.getPoweredBy());
    }

    @Test
    public void testGetCopyright() {
        assertEquals(CoreConstants.COPYRIGHT, core.getCopyright());
    }

    @Test
    public void testGetCopyrightURL() {
        assertEquals(CoreConstants.COPYRIGHT_URL,
                core.getCopyrightURL().toString());
    }

    @Test
    public void testGetEmailContact() {
        assertEquals(CoreConstants.SOFUN_EMAIL_CONTACT, core.getEmailContact());
    }

    @Test
    public void testServerURL() {
        assertEquals(CoreConstants.API_DOMAIN, core.getServerURL().getHost());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
