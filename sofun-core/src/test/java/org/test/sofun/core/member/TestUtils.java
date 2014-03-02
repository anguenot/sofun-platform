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

package org.test.sofun.core.member;

import org.sofun.core.CoreUtil;

import junit.framework.TestCase;

/**
 * Utils Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestUtils extends TestCase {

    public void testValidateEmail() {
        String email = "julien@anguenot.org";
        assertTrue(CoreUtil.isValidEmailAddress(email));

        email = "anguenot";
        assertFalse(CoreUtil.isValidEmailAddress(email));

        email = "@sofungamin.com";
        assertFalse(CoreUtil.isValidEmailAddress(email));

        email = "@sofun";
        assertFalse(CoreUtil.isValidEmailAddress(email));

        email = "anguenot@sofungaming";
        assertFalse(CoreUtil.isValidEmailAddress(email));
        
        email = "ééééé@sofungaming";
        assertFalse(CoreUtil.isValidEmailAddress(email));
        
        email = "aaaa@sofungaéééééng.com";
        assertFalse(CoreUtil.isValidEmailAddress(email));
        
        email = "aaaa@sofungaminng.coééééé";
        assertFalse(CoreUtil.isValidEmailAddress(email));

    }

}
