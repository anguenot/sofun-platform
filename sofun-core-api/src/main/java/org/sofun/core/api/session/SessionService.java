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

import org.sofun.core.api.member.Member;

/**
 * Service dealing with core {@link Session}
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface SessionService extends Serializable {

    /**
     * Returns a {@link Session} given it's token key.
     * 
     * @param tokenKey: a token key
     * @return a {@link Session} instance or null if not found.
     */
    Session getSession(String tokenKey);

    /**
     * Returns a {@link Session} given a member.
     * 
     * @param member: a {@link Member} instance
     * @return an active session or null of not found.
     */
    Session getSession(Member member);

    /**
     * Adds a {@link Session} given a token key.
     * 
     * @param tokenKey: a token key.
     * @return
     */
    Session addSession(String tokenKey);

    /**
     * Removes a {@link Session}.
     * 
     * @param session: a {@link Session} object.
     */
    void removeSession(Session session);

    /**
     * Explicitly update a session.
     * 
     * @param session: a {@link Session} object
     * @return
     */
    Session updateSession(Session session);

    /**
     * Renew a session.
     * 
     * <p>
     * Member interacting with plateform.
     * </p>
     * 
     * @param session: a {@link Session} instance.
     */
    void renew(Session session);

    /**
     * Returns the member owning the session.
     * 
     * <p/>
     * 
     * Member can be null right after the OAuth token exchange.
     * 
     * @param session: a {@link Session} instance.
     * @return a {@link Member} instance or null if not bound yet.
     */
    Member getMemberFor(Session session);

    /**
     * Sets the member owning a particular {@link Session}
     * 
     * <p/>
     * 
     * Implementation should forbid rebound to prevent stealing sessions.
     * 
     * @param member: a {@link Member} instance.
     * @param session: a {@link Session} instance.
     */
    void bind(Session session, Member member);

    /**
     * Cleanup expired sessions.
     */
    void cleanExpiredSessions();

}
