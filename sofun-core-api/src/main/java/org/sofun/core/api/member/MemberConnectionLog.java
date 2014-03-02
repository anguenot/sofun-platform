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

package org.sofun.core.api.member;

import java.io.Serializable;
import java.util.Date;

/**
 * Member connection.
 * 
 * Holds information about a member connection.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberConnectionLog extends Serializable {

    /**
     * Returns the associated {@link Member}
     * 
     * @return a {@link Member} instance.
     */
    Member getMember();

    /**
     * Sets the associated {@link Member}
     * 
     * @param member: a {@link Member} instance.
     */
    void setMember(Member member);

    /**
     * Returns the login connection time.
     * 
     * @return a {@link Date} in UTC
     */
    Date getLoginDate();

    /**
     * Sets the login connection time.
     * 
     * @param login: a {@link Date} in UTC
     */
    void setLoginDate(Date login);

    /**
     * Returns the logout connection time.
     * 
     * @return a {@link Date} in UTC
     */
    Date getLogoutDate();

    /**
     * Sets the logout connection time.
     * 
     * @param logout: a {@link Date} in UTC
     */
    void setLogoutDate(Date logout);

    /**
     * Returns the IP address from which the member logged in.
     * 
     * @return a {@link String} holding the IP address.
     */
    String getRemoteAddress();

    /**
     * Sets the IP address from which the member logged in.
     * 
     * @param remoteAddress: a {@link String} holding the IP address.
     */
    void setRemoteAddress(String remoteAddress);

    /**
     * Returns the associated sofun session id.
     * 
     * <p/>
     * 
     * Remember a session is transient so the session might be destroy. Useful to set logout time upon session expiration or
     * logout.
     * 
     * @return a sofun session identifier
     */
    String getSessionKey();

    /**
     * Sets he associated sofun session id.
     * 
     * <p/>
     * 
     * Remember a session is transient so the session might be destroy. Useful to set logout time upon session expiration or
     * logout.
     * 
     * @param sessionKey: a sofun session key
     */
    void setSessionId(String sessionKey);

}
