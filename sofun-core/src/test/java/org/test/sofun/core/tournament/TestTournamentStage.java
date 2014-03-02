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

package org.test.sofun.core.tournament;

import org.junit.Test;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.sport.tournament.TournamentStageImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTournamentStage extends SofunCoreTestCase {

    public TestTournamentStage(String testName) {
        super(testName);
    }

    @Test
    public void testContestantsProperties() throws CoreException {

        TournamentStage c1 = new TournamentStageImpl(1);
        TournamentStage c2 = new TournamentStageImpl(2);

        assertEquals(0, c1.getProperties().size());
        assertEquals(0, c2.getProperties().size());

        c1.put("a", "b");

        em.persist(c1);
        em.flush();

        assertEquals(1, c1.getProperties().size());
        assertEquals(0, c2.getProperties().size());

        c2.put("c", "d");

        em.persist(c2);
        em.flush();

        assertEquals(1, c1.getProperties().size());
        assertEquals(1, c2.getProperties().size());

        assertEquals("b", c1.get("a"));
        assertEquals("d", c2.get("c"));

    }

}
