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

package org.sofun.platform.arjel.banned;

import junit.framework.TestCase;

import org.junit.Test;
import org.sofun.platform.arjel.banned.api.exception.ARJELBannedException;

/**
 * Legigame Exception unit tests.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestARJELBannedException extends TestCase {

    @Test
    public void testConstructor() {

        ARJELBannedException e1 = new ARJELBannedException();
        assertTrue(e1 instanceof Exception);

        final String msg = "An error occured";
        ARJELBannedException e2 = new ARJELBannedException(msg);
        assertEquals(msg, e2.getMessage());

        ARJELBannedException e3 = new ARJELBannedException(new Exception(msg));
        assertEquals(msg, e3.getMessage());

    }

}
