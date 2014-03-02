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

package org.sofun.platform.arjel.banned.api;

import java.io.Serializable;
import java.util.Date;

import org.sofun.core.api.member.Member;

/**
 * Member ARJEL Banned check.
 * 
 * <p>
 * 
 * Holds information about the first and last check for a given {@link Member}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberARJELBannedCheck extends Serializable {

    /**
     * Returns the corresponding member.
     * 
     * @return a {@link Member} instance
     */
    Member getMember();

    /**
     * Returns the date at which the first check occured
     * 
     * @return a {@link Date} instance
     */
    Date getFirstCheckTime();

    /**
     * Returns the date at which the last check occured
     * 
     * @return a {@link Date} instance
     */
    Date getLastCheckTime();

    /**
     * Sets last check time.
     * 
     * @param date: a {@link Date} instance
     */
    void setLastCheckTime(Date date);

    /**
     * Returns if wether or the member is actually banned.
     * 
     * @return true or false
     */
    boolean isBanned();

    /**
     * Sets if wether or the member is actually banned.
     * 
     * @param banned: true or false
     */
    void setBanned(boolean banned);

}
