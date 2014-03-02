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

import java.util.ListIterator;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.jboss.resteasy.auth.oauth.OAuthUtils;
import org.jboss.resteasy.auth.oauth.OAuthValidator;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.OAuthJPAStorage;
import org.sofun.core.api.OAuthService;
import org.sofun.core.api.local.OAuthJPAStorageLocal;
import org.sofun.core.api.local.OAuthServiceLocal;
import org.sofun.core.api.local.SessionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.remote.OAuthServiceRemote;
import org.sofun.core.api.session.Session;
import org.sofun.core.api.session.SessionService;
import org.sofun.core.security.oauth.exception.AccessTokenNotFoundException;
import org.sofun.core.security.oauth.exception.ConsumerNotFoundException;

/**
 * OAuth Service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(OAuthServiceLocal.class)
@Remote(OAuthServiceRemote.class)
public class OAuthServiceImpl implements OAuthService {

    private static final long serialVersionUID = -1889738304139616593L;

    public static final String REQUEST_PARAM_EMAIL = "email";

    public static final String REQUEST_PARAM_FB_ID = "facebookId";

    public static final Log log = LogFactory.getLog(OAuthServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    private transient EntityManager em;

    @EJB(
            beanName = "OAUthJPAStorageImpl",
            beanInterface = OAuthJPAStorageLocal.class)
    private OAuthJPAStorage storage;

    @EJB(
            beanName = "SessionServiceImpl",
            beanInterface = SessionServiceLocal.class)
    private SessionService sessions;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService members;

    @Override
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void cleanAccessTokens() throws Exception {
        log.debug("Cleaning up OAuth expired tokens...");
        ListIterator<OAuthSofunAccessToken> tokens = storage
                .getExpiredAccessTokens().listIterator();
        while (tokens.hasNext()) {
            OAuthSofunAccessToken token = tokens.next();
            if (token.isExpired()) {
                log.debug("Token of member=" + getMemberEmailFor(token)
                        + " expired. Deleting...");
                em.remove(token);
            }
        }
    }

    private String getMemberEmailFor(OAuthSofunAccessToken token) {
        final Member member = token.getMember();
        String email = "UNKNOWN";
        if (member != null) {
            email = member.getEmail();
        }
        return email;
    }

    @Override
    public HttpServletRequest filterRequest(HttpServletRequest request,
            OAuthProvider provider, OAuthValidator validator) throws Exception {

        OAuthMessage message = OAuthUtils.readMessage(request);
        message.requireParameters(OAuth.OAUTH_CONSUMER_KEY,
                OAuth.OAUTH_SIGNATURE_METHOD, OAuth.OAUTH_SIGNATURE,
                OAuth.OAUTH_TIMESTAMP, OAuth.OAUTH_NONCE);

        final String consumerKey = message
                .getParameter(OAuth.OAUTH_CONSUMER_KEY);
        OAuthConsumer consumer = provider.getConsumer(consumerKey);
        if (consumer == null) {
            throw new ConsumerNotFoundException();
        }

        OAuthToken accessToken = null;
        final String accessTokenString = message
                .getParameter(OAuth.OAUTH_TOKEN);

        if (accessTokenString != null) {
            try {
                accessToken = provider.getAccessToken(consumer.getKey(),
                        accessTokenString);
            } catch (OAuthException o) {
                // Will be caught and throw back below.
            }
            if (accessToken == null) {
                throw new AccessTokenNotFoundException();
            }
            OAuthUtils.validateRequestWithAccessToken(request, message,
                    accessToken, validator, consumer);
        } else {
            OAuthUtils.validateRequestWithoutAccessToken(request, message,
                    validator, consumer);
        }

        // Initialize the security context now that the token has been cleared.
        if (accessToken != null) {
            request = createSecurityContext(request, consumer, accessToken);
        }

        return request;

    }

    protected HttpServletRequest createSecurityContext(
            HttpServletRequest request, OAuthConsumer consumer,
            OAuthToken accessToken) throws Exception {

        // Update token permission depending on bound consumer.
        OAuthSofunAccessToken token = (OAuthSofunAccessToken) accessToken;
        token.setPermissions(consumer.getPermissions());

        // Check if this is a request for login and create a session.
        // The user will *not* be logged in at this point!
        final String pathInfo = request.getPathInfo();
        Member member = null;
        if (pathInfo.startsWith("/member/login/facebook")) {
            final String facebookId = request.getParameter(REQUEST_PARAM_FB_ID);
            member = members.getMemberByFacebookId(facebookId);
        } else if (pathInfo.startsWith("/member/login")) {
            final String email = request.getParameter(REQUEST_PARAM_EMAIL);
            member = members.getMember(email);
        }

        if (member != null) {
            // Check if we already have a active session corresponding to the
            // token.
            Session session = sessions.getSession(token.getToken());
            if (session == null) {
                // Check if we do have another active session for this user
                // already.
                if (storage.getAccessTokenFor(member) != null) {
                    session = sessions.getSession(member);
                    // FIXME : tmp because there is a bug with token gneration
                    // log.warn("Attempt to reopen a new session for member w/ email="
                    // + member.getEmail()
                    // +
                    // " although an active one already exists. Destroying previous one.");
                    // sessions.removeSession(session);
                } else {
                    session = sessions.addSession(token.getToken());
                    session.setRemoteAddress(request.getRemoteAddr());
                }
            } else {
                sessions.renew(session);
            }
            sessions.bind(session, member);
        }

        return request;
    }
}
