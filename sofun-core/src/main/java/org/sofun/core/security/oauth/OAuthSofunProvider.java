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

package org.sofun.core.security.oauth;

import java.net.HttpURLConnection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthRequestToken;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.OAuthJPAStorage;

/**
 * OAUth provider implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class OAuthSofunProvider implements OAuthProvider {

    private static Log log = LogFactory.getLog(OAuthSofunProvider.class);

    private final String realm;

    /**
     * Request tokens are only stored in memory. (Access tokens are stored using
     * JPA)
     */
    private final ConcurrentHashMap<String, OAuthRequestToken> requestTokens = new ConcurrentHashMap<String, OAuthRequestToken>();

    /**
     * Access tokens JPA storage.
     */
    private OAuthJPAStorage storage;

    public OAuthSofunProvider() {
        this("OAuth");
    }

    public OAuthSofunProvider(String realm) {
        this.realm = realm;
    }

    /**
     * Returns the OAuth token storage service.
     * 
     * @return a {@link OAuthJPAStorageImpl} instance.
     * @throws OAuthException
     */
    private OAuthJPAStorage getStorage() throws OAuthException {
        if (storage == null) {
            InitialContext ctx;
            try {
                ctx = new InitialContext();
                return (OAuthJPAStorage) ctx
                        .lookup("sofun/OAUthJPAStorageImpl/local");
            } catch (NamingException e) {
                throw new OAuthException(500, e.getMessage());
            }
        }
        return storage;
    }

    private boolean _accessTokenExists(String key) {
        return _getAccessToken(key) != null;
    }

    private OAuthSofunAccessToken _getAccessToken(String key) {
        try {
            return getStorage().getAccessToken(key);
        } catch (OAuthException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private OAuthSofunAccessToken doMakeAccessTokens(
            OAuthRequestToken requestToken) throws OAuthException {

        String newToken;
        do {
            newToken = makeRandomString();
        } while (_accessTokenExists(newToken));

        OAuthSofunConsumer consumer = getConsumer(requestToken.getConsumer()
                .getKey());

        return getStorage().addAccessToken(newToken, makeRandomString(),
                getTokenExpirationFromNow(), consumer);

    }

    private OAuthSofunAccessToken doGetAccessToken(String consumerKey,
            String accessKey) throws OAuthException {
        // get is atomic
        OAuthSofunAccessToken ret = _getAccessToken(accessKey);
        if (ret == null) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "Token is invalid");
        }
        if (!ret.getConsumer().getKey().equals(consumerKey)) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "Consumer is invalid");
        }
        return ret;
    }

    private OAuthRequestToken doMakeRequestToken(String consumerKey,
            String callback, String[] scopes, String[] permissions)
            throws OAuthException {
        OAuthSofunConsumer consumer = _getConsumer(consumerKey);
        String newToken;
        do {
            newToken = makeRandomString();
        } while (requestTokens.containsKey(newToken));
        OAuthRequestToken token = new OAuthRequestToken(newToken,
                makeRandomString(), callback, scopes, permissions, -1, consumer);
        requestTokens.put(token.getToken(), token);
        return token;
    }

    private OAuthRequestToken doGetRequestToken(String customerKey,
            String requestKey) throws OAuthException {
        // get is atomic
        OAuthRequestToken ret = requestTokens.get(requestKey);
        checkCustomerKey(ret, customerKey);
        if (ret == null)
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "No such request key " + requestKey);
        return ret;
    }

    public OAuthRequestToken verifyAndRemoveRequestToken(String customerKey,
            String requestToken, String verifier) throws OAuthException {
        OAuthRequestToken request = getRequestToken(requestToken);
        checkCustomerKey(request, customerKey);

        // We don't need to check for verifier here because user can NEVER
        // interact with the application and therefore get a
        // oauth verification id. Only allowed applications with credentials and
        // allowed routing can interact directly with a
        // given instance of the the sofun platform. A user will always interact
        // with a Sofun platfrom through an application.

        /*
         * if (verifier == null || !verifier.equals(request.getVerifier()))
         * throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
         * "Invalid verifier code for token " + requestToken);
         */

        // then let's go through and exchange this for an access token
        return requestTokens.remove(requestToken);
    }

    private static String makeRandomString() {
        return UUID.randomUUID().toString();
    }

    private void checkCustomerKey(OAuthToken token, String customerKey)
            throws OAuthException {
        if (customerKey != null
                && !customerKey.equals(token.getConsumer().getKey())) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "Invalid customer key");
        }
    }

    //
    // For subclassers
    //

    protected void addConsumer(String consumerKey, String consumerSecret) {
        OAuthSofunConsumer consumer = new OAuthSofunConsumer(consumerKey,
                consumerSecret, null, null);
        try {
            getStorage().addConsumer(consumer);
        } catch (OAuthException e) {
            log.error(e.getMessage());
        }
    }

    protected void addRequestKey(String consumerKey, String requestToken,
            String requestSecret, String callback, String[] scopes)
            throws OAuthException {
        OAuthConsumer consumer = _getConsumer(consumerKey);
        OAuthRequestToken token = new OAuthRequestToken(requestToken,
                requestSecret, callback, scopes, null, -1, consumer);
        requestTokens.put(requestToken, token);
    }

    protected void addAccessKey(String consumerKey, String accessToken,
            String accessSecret, String[] permissions) throws OAuthException {
        OAuthSofunConsumer consumer = _getConsumer(consumerKey);
        OAuthSofunAccessToken token = new OAuthSofunAccessToken(accessToken,
                accessSecret, consumer.getScopes(), permissions,
                getTokenExpirationFromNow(), consumer);

        getStorage().addAccessToken(token);
    }

    protected void authoriseRequestToken(String consumerKey,
            String requestToken, String verifier) throws OAuthException {
        doGetRequestToken(consumerKey, requestToken).setVerifier(verifier);
    }

    protected OAuthSofunConsumer _getConsumer(String consumerKey)
            throws OAuthException {

        OAuthSofunConsumer ret = getStorage().getConsumer(consumerKey);

        if (ret == null)
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "No such consumer key " + consumerKey);
        return ret;
    }

    //
    // OAuthProvider interface

    @Override
    public String getRealm() {
        return realm;
    }

    @Override
    public String authoriseRequestToken(String consumerKey, String requestToken)
            throws OAuthException {
        String verifier = makeRandomString();
        doGetRequestToken(consumerKey, requestToken).setVerifier(verifier);
        return verifier;
    }

    @Override
    public OAuthConsumer registerConsumer(String consumerKey,
            String displayName, String connectURI) throws OAuthException {
        OAuthConsumer consumer = getConsumer(consumerKey);
        if (consumer == null) {
            return consumer;
        }
        consumer = new OAuthSofunConsumer(consumerKey, "therealfrog",
                displayName, connectURI);
        getStorage().addConsumer((OAuthSofunConsumer) consumer);
        return consumer;

    }

    @Override
    public OAuthSofunConsumer getConsumer(String consumerKey)
            throws OAuthException {
        return _getConsumer(consumerKey);
    }

    @Override
    public OAuthRequestToken getRequestToken(String consumerKey,
            String requestToken) throws OAuthException {
        OAuthRequestToken token = getRequestToken(requestToken);
        if (consumerKey != null
                && !token.getConsumer().getKey().equals(consumerKey)) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "No such consumer key " + consumerKey);
        }
        return token;
    }

    @Override
    public OAuthToken getAccessToken(String consumerKey, String accessToken)
            throws OAuthException {
        return doGetAccessToken(consumerKey, accessToken);
    }

    @Override
    public void checkTimestamp(OAuthToken token, long timestamp)
            throws OAuthException {
        if (token.getTimestamp() > timestamp)
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "Invalid timestamp " + timestamp);
    }

    @Override
    public OAuthToken makeAccessToken(String consumerKey, String requestToken,
            String verifier) throws OAuthException {
        OAuthRequestToken token = verifyAndRemoveRequestToken(consumerKey,
                requestToken, verifier);
        return doMakeAccessTokens(token);
    }

    @Override
    public OAuthRequestToken makeRequestToken(String consumerKey,
            String callback, String[] scopes, String[] permissions)
            throws OAuthException {
        OAuthRequestToken token = doMakeRequestToken(consumerKey, callback,
                scopes, permissions);
        requestTokens.put(token.getToken(), token);
        return token;
    }

    public OAuthRequestToken getRequestToken(String requestToken)
            throws OAuthException {
        OAuthRequestToken token = requestTokens.get(requestToken);
        if (token == null) {
            throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
                    "No such request token " + requestToken);
        }
        return token;
    }

    @Override
    public void registerConsumerScopes(String consumerKey, String[] scopes)
            throws OAuthException {
        OAuthConsumer consumer = _getConsumer(consumerKey);
        consumer.setScopes(scopes);
    }

    @Override
    public void registerConsumerPermissions(String consumerKey,
            String[] permissions) throws OAuthException {
        // XXX register consumer permissions here. Done from the filter at the
        // moment
    }

    @Override
    public Set<String> convertPermissionsToRoles(String[] permissions) {
        // XXX convert permissons to roles here. Done from the filter at the
        // moment
        return null;
    }

    /**
     * Returns token expiration timestamp.
     * 
     * @return a timestamp in millisesonds
     */
    private long getTokenExpirationFromNow() {
        return CoreConstants.TOKEN_TIMEOUT * 1000;
    }

}
