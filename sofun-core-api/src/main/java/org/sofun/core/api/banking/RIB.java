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
import java.util.HashMap;
import java.util.Map;

/**
 * RIB Utils.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class RIB {

    /** Characters translation table */
    private static final Map<String, String> CHARS_TR = new HashMap<String, String>();

    static {
        CHARS_TR.put("A", "10");
        CHARS_TR.put("B", "11");
        CHARS_TR.put("C", "12");
        CHARS_TR.put("D", "13");
        CHARS_TR.put("E", "14");
        CHARS_TR.put("F", "15");
        CHARS_TR.put("G", "16");
        CHARS_TR.put("H", "17");
        CHARS_TR.put("I", "18");
        CHARS_TR.put("J", "19");
        CHARS_TR.put("K", "20");
        CHARS_TR.put("L", "21");
        CHARS_TR.put("M", "22");
        CHARS_TR.put("N", "23");
        CHARS_TR.put("O", "24");
        CHARS_TR.put("P", "25");
        CHARS_TR.put("Q", "26");
        CHARS_TR.put("R", "27");
        CHARS_TR.put("S", "28");
        CHARS_TR.put("T", "29");
        CHARS_TR.put("U", "30");
        CHARS_TR.put("V", "31");
        CHARS_TR.put("W", "32");
        CHARS_TR.put("X", "33");
        CHARS_TR.put("Y", "34");
        CHARS_TR.put("Z", "35");

    }

    /**
     * Validates a RIB
     * 
     * @param RIB: a RIB as A {@link String}
     * @return true or false.
     */
    public static final boolean validateRIB(String RIB) {
        // TODO implement me
        return true;
    }

    public static final String toIBAN(String bank, String branch,
            String number, String key) {

        String tmpIban = bank + branch + number + key;
        tmpIban = tmpIban.toUpperCase() + "FR00";
        for (Map.Entry<String, String> e : CHARS_TR.entrySet()) {
            tmpIban = tmpIban.replace(e.getKey(), e.getValue());
        }

        BigInteger big = new BigInteger(tmpIban);

        String ibanKey = String.valueOf(98 - Integer.valueOf(String.valueOf(big
                .mod(BigInteger.valueOf(97)))));

        if (Integer.valueOf(ibanKey) == 1) {
            ibanKey = "0" + ibanKey;
        }

        return "FR" + ibanKey + bank + branch + number + key;

    }
}
