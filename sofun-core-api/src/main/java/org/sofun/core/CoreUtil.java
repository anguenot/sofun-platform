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

package org.sofun.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.sofun.core.api.exception.CoreException;

/**
 * Core Util.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class CoreUtil {

    /**
     * Checks if whether or not an email address is Valid.
     * 
     * @param email: an email address as {@link String}
     * @return true or false.
     */
    public static final boolean isValidEmailAddress(String email) {
        final String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        final CharSequence inputStr = email;
        final Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    /**
     * Returns a {@link Date} instance from a {@link String}
     * 
     * <p />
     * 
     * The time zone formats available to SimpleDateFormat are not ISO8601 compliant. SimpleDateFormat understands time zone
     * strings like "GMT+01:00" or "+0100", the latter according to RFC822.
     * 
     * <p />
     * 
     * @see http://stackoverflow.com/questions/2201925/converting-iso8601-compliant -string-to-java-util-date
     * 
     * @param dateStr: a {@link String} following a ISO8601 pattern.
     * @return a {@link Date} instance preserving the timezone.
     * @throws CoreException: wrapped exception.
     */
    public static final Date getDateFromISO8601DateString(String dateStr) throws CoreException {

        if (dateStr == null) {
            return null;
        }

        try {
            return DatatypeConverter.parseDate(dateStr).getTime();
        } catch (IllegalArgumentException iae) {
            throw new CoreException(iae.getMessage());
        }

    }

    /**
     * Returns a date given year, month and day.
     * 
     * @param year: a {@link String}
     * @param month: a {@link String}
     * @param day: a {@link String}
     * @return a {@link Date} in UTC
     * @throws Exception: if the date is invalid.
     */
    public static final Date getDate(String year, String month, String day) throws Exception {
        final String date = year + "/" + month + "/" + day;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.parse(date);
    }

    /**
     * Pauses execution for given amount of time.
     * 
     * <p/>
     * 
     * Mostly here for testing purpose.
     * 
     * @param milliseconds: a {@link Long}
     */
    public static final void waitFor(final long milliseconds) {
        final long t0;
        long t1;
        t0 = System.currentTimeMillis();
        do {
            t1 = System.currentTimeMillis();
        } while (t1 - t0 < milliseconds);
    }

}
