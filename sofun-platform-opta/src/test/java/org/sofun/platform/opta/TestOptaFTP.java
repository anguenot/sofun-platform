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

import java.io.FileOutputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;
import org.sofun.platform.opta.ftp.FTPClientWrapper;
import org.sofun.platform.opta.ftp.FTPFileFilterImpl;

/**
 * FTP file filters.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestOptaFTP extends TestCase {

    @Test
    public void testGet() throws Exception {

        FTPClientWrapper ftp = new FTPClientWrapper(
                "uk.hosting.sofungaming.com", 21, "opta", "bRAvE38jukE=");
        ftp.connect();

        OutputStream out = new FileOutputStream("/tmp/testing.txt");
        ftp.getFile(out, "ru1_compfixtures.210.2012.20110812163111.xml");

        ftp.disconnect();
    }

    @Test
    public void testFilter() throws Exception {
        final String liveFeedPattern = "srml-.*-.*-f.*-matchresults.xml";
        FTPClientWrapper ftp = new FTPClientWrapper(
                "uk.hosting.sofungaming.com", 21, "opta", "bRAvE38jukE=");
        ftp.connect();
        FTPFile[] files = ftp.getClient().listFiles(null,
                new FTPFileFilterImpl(liveFeedPattern));
        assertTrue(files.length > 0);

        for (int i = 0; i < files.length; i++) {
            assertTrue(!"ru1_compfixtures.210.2012.20110812163111.xml"
                    .equals(files[i].getName()));
        }

        ftp.disconnect();

    }

}
