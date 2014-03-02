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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.SportContestantType;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.sport.SportContestantImpl;
import org.sofun.core.sport.SportServiceImpl;
import org.test.sofun.core.testing.SofunCoreTestCase;

/**
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestTournamentContestants extends SofunCoreTestCase {

    private SportService service; 
    
    public TestTournamentContestants(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.service = new SportServiceImpl(em);
    }
    
    @Override
    protected void tearDown() throws Exception {
        this.service = null;
        super.tearDown();
    }

    @Test
    public void testContestantsProperties() throws CoreException {

        SportContestant c1 = new SportContestantImpl("c1", SportContestantType.INDIVIDUAL);
        SportContestant c2 = new SportContestantImpl("c2", SportContestantType.INDIVIDUAL);

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
    
    @Test
    public void testComparison() throws Exception {
        
        SportContestant c1 = new SportContestantImpl("c1", SportContestantType.INDIVIDUAL);
        SportContestant c2 = new SportContestantImpl("c2", SportContestantType.INDIVIDUAL);
        
        assertFalse(c1.equals(c2));
        assertFalse(c2.equals(c1));
        
        c1 = new SportContestantImpl("a", SportContestantType.INDIVIDUAL);
        c2 = new SportContestantImpl("a", SportContestantType.INDIVIDUAL);
        
        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c1));
        
        em.persist(c1);
        em.flush();
        
        SportContestant retrieved = service.getSportContestantPlayer("a");
        assertNotNull(retrieved);
        
        assertEquals(c1,retrieved);
        assertEquals(c2,retrieved);
        
        List<SportContestant> contestants = new ArrayList<SportContestant>();
        contestants.add(c1);
        if (!contestants.contains(c2)) {
            contestants.add(c2);
        }
        assertEquals(1, contestants.size());
    }

}
