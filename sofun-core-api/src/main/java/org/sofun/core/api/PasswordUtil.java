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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.sofun.core.api.exception.CoreException;

/**
 * Core password utility.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class PasswordUtil {

    /**
     * Default hashing algorithm used by the plateform.
     * 
     */
    public static final String HASH_ALGO = "SHA-512";

    /**
     * Generates a password meeting Sofun plateform policy.
     * 
     * @return a clear text password.
     */
    public static final String generatePassword() {
        return PasswordGenerator.generate();
    }

    /**
     * Returns a hash using {@link PasswordUtil.HASH_ALGO} hashing algorithm for
     * a given clear text password.
     * 
     * @param password: a clear text password.
     * @return hash value using a {@link PasswordUtil.HASH_ALGO} hashing
     *         algorithm.
     * @throws CoreException
     */
    public static final String getHashFor(String password) throws CoreException {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance(PasswordUtil.HASH_ALGO);
        } catch (NoSuchAlgorithmException e) {
            throw new CoreException(e.getMessage());
        }

        md.update(password.getBytes());
        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(
                    1));
        }

        return sb.toString();
    }

    /**
     * Verifies that the a clear test password meets platform policy.
     * 
     * @param password: a clear text password.
     * @return true or false.
     */
    public static final boolean verifyPassword(String password) {
        return PasswordGenerator.verify(password);
    }

}
