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

package org.test.sofun.core.kup;

import org.sofun.core.api.community.Community;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.community.CommunityImpl;
import org.sofun.core.kup.KupImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * Kup implementation TestCase.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 *
 */
public class TestKupImpl extends SofunCoreTestCase {

    public TestKupImpl(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateKup() throws CoreException {
        Kup k = new KupImpl("My Kup");
        assertEquals("My Kup", k.getName());
    }
    
    public void testKupPersistence() throws CoreException {
        Community k = new CommunityImpl("My Kup");
        em.persist(k);
        assertTrue(em.contains(k));
        em.flush();
        commitTransaction();
        assertTrue(em.contains(k));
        beginTransaction();
        em.clear();
        assertFalse(em.contains(k));
    }

}
