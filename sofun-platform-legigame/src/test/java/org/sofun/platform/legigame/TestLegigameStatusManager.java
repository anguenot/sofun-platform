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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests against the Legigame status manager singleton.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestLegigameStatusManager extends TestCase {

    private MemberLegigameStatusManager manager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        manager = new MemberLegigameStatusManager();
    }

    @Override
    protected void tearDown() throws Exception {
        manager = null;
        super.tearDown();
    }

    @Test
    public void testSyncStatus() throws Exception {
        // smoke test (no underlying legigame service)
        manager.syncStatus();

    }

    @Test
    public void testSyncMembers() throws Exception {
        // smoke test (no underlying legigame service)
        manager.syncMembers();
    }

    @Test
    public void testSyncStatusConcurrency() throws Exception {

        ExecutorService exec = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 10000; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    runSyncStatus();
                }
            });
        }
        exec.shutdown();
        exec.awaitTermination(50, TimeUnit.SECONDS);

    }

    private void runSyncStatus() {
        try {
            manager.syncStatus();
        } catch (Throwable t) {
            // assert error
            assertFalse(true);
        }
    }

    @Test
    public void testSyncMembersConcurrency() throws Exception {

        ExecutorService exec = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 10000; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    runSyncMembers();
                }
            });
        }
        exec.shutdown();
        exec.awaitTermination(50, TimeUnit.SECONDS);

    }

    private void runSyncMembers() {
        try {
            manager.syncMembers();
        } catch (Throwable t) {
            // assert error
            assertFalse(true);
        }
    }

}
