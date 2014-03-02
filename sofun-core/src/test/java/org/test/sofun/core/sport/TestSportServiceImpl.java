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

package org.test.sofun.core.sport;

import org.junit.Test;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.sport.SportServiceImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestSportServiceImpl extends SofunCoreTestCase {

    SportService service;

    public TestSportServiceImpl(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new SportServiceImpl(em);
    }

    @Override
    protected void tearDown() throws Exception {
        service = null;
        super.tearDown();
    }

    @Test
    public void testTourmanetCompletedGames() {
        // Smoke test.
        service.getTournamentGamesByStatus(TournamentGameStatus.TERMINATED);
    }

}
