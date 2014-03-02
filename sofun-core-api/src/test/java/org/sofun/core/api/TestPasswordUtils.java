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

package org.sofun.core.api;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Password Util Test Case.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class TestPasswordUtils extends TestCase {

    @Test
    public void testHash() throws Exception {
        final String password = "azerty";
        final String hash = PasswordUtil.getHashFor(password);
        assertEquals(
                hash,
                "df6b9fb15cfdbb7527be5a8a6e39f39e572c8ddb943fbc79a943438e9d3d85ebfc2ccf9e0eccd9346026c0b6876e0e01556fe56f135582c05fbdbb505d46755a");
    }

    @Test
    public void testPasswordGenerator() throws Exception {
        final String pwd = PasswordUtil.generatePassword();
        assertNotNull(pwd);
        assertEquals(5, pwd.length());
        assertTrue(PasswordUtil.verifyPassword(pwd));
    }
    
    @Test
    public void testPasswordValidation() throws Exception {
        
        // Empty not allowed
        assertFalse(PasswordUtil.verifyPassword(""));
        
        // Too short
        assertFalse(PasswordUtil.verifyPassword("Za./"));
        
        // Missing Uppercase
        //assertFalse(PasswordUtil.verifyPassword("azertyuiop"));
        
        // Missing Uppercase and too short
        //assertFalse(PasswordUtil.verifyPassword("Azertyuiop"));
        
        // Repetition
        //assertFalse(PasswordUtil.verifyPassword("Azzertyuiop"));
        
        //assertFalse(PasswordUtil.verifyPassword("azzertyuiop8"));
        
        // Does not end with a number
        //assertFalse(PasswordUtil.verifyPassword("Azertyuiop/"));
        
        assertTrue(PasswordUtil.verifyPassword("Azertyuiop8"));
        
    }

}
