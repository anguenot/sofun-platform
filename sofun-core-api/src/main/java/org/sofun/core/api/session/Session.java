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

package org.sofun.core.api.session;

import java.io.Serializable;
import java.util.Date;

/**
 * Core Session.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Session extends Serializable {

    /**
     * Returns the session's token key.
     * 
     * <p/>
     * 
     * Typically an OAuth Access Token key is used here.
     * 
     * @return a token key unique for this session.
     */
    String getKey();

    /**
     * Sets the session's token key.
     * 
     * @param tokenKey: a session key.
     */
    void setKey(String key);

    /**
     * Returns the session creation time.
     * 
     * @return a {@link Date} in UTC.
     */
    Date getCreated();

    /**
     * Returns the date at which the session will expire.
     * 
     * @return a {@link Date} object.
     */
    Date getExpired();

    /**
     * Has the session expired?
     * 
     * @return true or false.
     */
    boolean isExpired();

    /**
     * Returns the date at which the session has been renewed.
     * 
     * @return a {@link Date} object in UTC.
     */
    Date getRenewed();

    /**
     * Extends session validity. In case of user interaction with the platform.
     */
    void renew();

    /**
     * Returns the Internet Protocol (IP) address of the client or last proxy that sent the request.
     * 
     * @return a String containing the IP address of the client that sent the request
     */
    String getRemoteAddress();

    /**
     * Sets the Internet Protocol (IP) address of the client or last proxy that sent the request.
     * 
     * @param remoteAddress: a String containing the IP address of the client that sent the request
     */
    void setRemoteAddress(String remoteAddress);

}
