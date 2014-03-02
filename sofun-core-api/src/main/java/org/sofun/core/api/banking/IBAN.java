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

package org.sofun.core.api.banking;

import java.math.BigInteger;

/**
 * IBAN.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class IBAN {

    private static final BigInteger BD_97 = new BigInteger("97");

    private static final BigInteger BD_98 = new BigInteger("98");

    private String invalidCause = null;

    private String iban;

    public IBAN(String iban) {
        this.iban = iban;
    }

    public String getIBAN() {
        return iban;
    }

    public void setIBAN(String iban) {
        this.iban = iban;
    }

    public boolean validate(String IBAN) {

        if (this.iban == null) {
            throw new IllegalStateException("iban is null");
        }

        invalidCause = null;

        final String code = removeNonAlpha(this.iban);
        final int len = code.length();

        if (len < 4) {
            this.invalidCause = "Too short (expected at least 4, got " + len + ")";
            return false;
        }

        final String country = code.substring(0, 2);
        if (!ISOCountries.getInstance().isValidCode(country)) {
            this.invalidCause = "Invalid ISO country code: " + country;
            return false;
        }

        try {
            new Integer(code.substring(2, 4)).intValue();
        } catch (NumberFormatException e) {
            this.invalidCause = "Bad verification code: " + e;
            return false;
        }

        final StringBuffer bban = new StringBuffer(code.substring(4));
        if (bban.length() == 0) {
            this.invalidCause = "Empty Basic Bank Account Number";
            return false;
        }
        bban.append(code.substring(0, 4));

        String workString = translateChars(bban);
        int mod = modulo97(workString);
        if (mod != 1) {
            this.invalidCause = "Verification failed (expected 1 and obtained " + mod + ")";
            return false;
        }

        return true;
    }

    private String translateChars(final StringBuffer bban) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < bban.length(); i++) {
            char c = bban.charAt(i);
            if (Character.isLetter(c)) {
                result.append(Character.getNumericValue(c));
            } else {
                result.append((char) c);
            }
        }
        return result.toString();
    }

    private String removeNonAlpha(final String iban) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < iban.length(); i++) {
            char c = iban.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c)) {
                result.append((char) c);
            }
        }
        return result.toString();
    }

    private int modulo97(String bban) {
        BigInteger b = new BigInteger(bban);
        b = b.divideAndRemainder(BD_97)[1];
        b = BD_98.min(b);
        b = b.divideAndRemainder(BD_97)[1];
        return ((int) (98 - (Long.parseLong(bban) * 100) % 97L)) % 97;
    }

    public String getInvalidCause() {
        return invalidCause;
    }

}
