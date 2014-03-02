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

package org.sofun.platform.opta;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.sofun.core.api.sport.SportService;
import org.sofun.core.sport.SportServiceImpl;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.impl.OptaServiceImpl;
import org.sofun.platform.opta.testing.SofunCoreTestCase;

/**
 * TAB Feed Parsers Integration test case.
 * 
 * @author @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestCyclingParser extends SofunCoreTestCase {

    public SportService sports;

    public OptaService opta;

    private static final String cy40Feed = "feeds/cy/CY40.Xml";

    private static final String cy1Feed = "feeds/cy/CY1.Xml";
    
    private static final String csfFeed = "feeds/cy/CSF_90002_13.Xml";
    
    private static final String csfFeed2 = "feeds/cy/CSF_90002_09.Xml";

    public TestCyclingParser(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        sports = new SportServiceImpl(this.em);
        opta = new OptaServiceImpl(this.em, sports);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        sports = null;
        opta = null;
    }

    /**
     * Returns a {@link File} given its path.
     * 
     * @param path: path of the file within the test resources.
     * @return a {@link File} instance
     */
    private File getFile(String path) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(path);
        return new File(url.getFile());
    }

    @Test
    public void testCY40() throws Exception {

        File f = this.getFile(cy1Feed);
        opta.cy1Sync(f);

        f = this.getFile(cy40Feed);
        opta.cy40sync(f);

        // XXX implement actual tests. Smoke tests only right now.

    }

    @Test
    public void testCY1() throws Exception {

        File f = this.getFile(cy1Feed);
        opta.cy1Sync(f);

        // XXX implement actual tests. Smoke tests only right now.

    }

    @Test
    public void testCSF() throws Exception {
        
        File f = this.getFile(cy1Feed);
        opta.cy1Sync(f);

        f = this.getFile(cy40Feed);
        opta.cy40sync(f);
        
        f = this.getFile(csfFeed2);
        opta.cyCSFCync(f);
        
        f = this.getFile(csfFeed);
        opta.cyCSFCync(f);
        
        // XXX implement actual tests. Smoke tests only right now.

    }

}
