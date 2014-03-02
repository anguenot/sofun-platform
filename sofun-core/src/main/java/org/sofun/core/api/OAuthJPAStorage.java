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

import java.io.Serializable;
import java.util.List;

import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.sofun.core.api.member.Member;
import org.sofun.core.security.oauth.OAuthSofunAccessToken;
import org.sofun.core.security.oauth.OAuthSofunConsumer;

/**
 * OAuth token JPA storage.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface OAuthJPAStorage extends Serializable {

    /**
     * Returns a outh consumer given its key.
     * 
     * @param key: a consumer key
     * @return a {@link OAuthSofunConsumer} instance or null if does not exist.
     */
    OAuthSofunConsumer getConsumer(String key);

    /**
     * Adds a new oauth consumer.
     * 
     * @param consumer: a {@link OAuthSofunConsumer} instance.
     */
    void addConsumer(OAuthSofunConsumer consumer);

    /**
     * Returns a oauth access token given its key.
     * 
     * @param key: a key as a {@link String}
     * @return a {@link OAuthSofunAccessToken} instance.
     */
    OAuthSofunAccessToken getAccessToken(String key);

    /**
     * Adds a oauth access token.
     * 
     * @param token: a {@link OAuthSofunAccessToken} instance.Œ
     */
    void addAccessToken(OAuthSofunAccessToken token);

    /**
     * Adds a oauth access token.
     * 
     * @param token
     * @param secret
     * @param timeToLive
     * @param consumer
     */
    OAuthSofunAccessToken addAccessToken(String token, String secret, long timeToLive, OAuthSofunConsumer consumer);

    /**
     * Returns all expired access tokens.
     * 
     * @return a {@link List} over {@link OAuthToken} instances.
     */
    List<OAuthSofunAccessToken> getExpiredAccessTokens();

    /**
     * Returns an access token given a member.
     * 
     * @param member: a {@link Member} instance
     * @return an access token or null if none.
     */
    OAuthSofunAccessToken getAccessTokenFor(Member member);

}
