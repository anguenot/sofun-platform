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

package org.sofun.core.session;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.OAuthJPAStorage;
import org.sofun.core.api.local.OAuthJPAStorageLocal;
import org.sofun.core.api.local.SessionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberConnectionLog;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.remote.SessionServiceRemote;
import org.sofun.core.api.session.Session;
import org.sofun.core.api.session.SessionService;
import org.sofun.core.security.oauth.OAuthSofunAccessToken;

/**
 * Session service implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(SessionServiceLocal.class)
@Remote(SessionServiceRemote.class)
public class SessionServiceImpl implements SessionService {

    private static final long serialVersionUID = 1327169281348394759L;

    private static final Log log = LogFactory.getLog(SessionServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @EJB(
            beanName = "OAUthJPAStorageImpl",
            beanInterface = OAuthJPAStorageLocal.class)
    private OAuthJPAStorage storage;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService members;

    public SessionServiceImpl() {
        super();
    }

    public SessionServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public Session getSession(String tokenKey) {

        String queryStr = "from " + SessionImpl.class.getSimpleName()
                + " t where t.key =:key";
        Query query = em.createQuery(queryStr);
        query.setParameter("key", tokenKey);

        try {
            return (Session) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Session getSession(Member member) {

        Session session = null;
        OAuthSofunAccessToken token = storage.getAccessTokenFor(member);
        if (token != null) {
            return getSession(token.getToken());
        }
        return session;

    }

    @Override
    public Session addSession(String tokenKey) {

        Session session = getSession(tokenKey);
        if (getSession(tokenKey) == null) {
            session = new SessionImpl(tokenKey);
            em.persist(session);
        }
        return session;

    }

    @Override
    public void removeSession(Session session) {

        if (session == null) {
            return;
        }

        session = getSession(session.getKey());
        if (session != null) {
            OAuthSofunAccessToken token = storage.getAccessToken(session
                    .getKey());
            if (token != null && token.getMember() != null) {
                // Update logout time in record
                MemberConnectionLog cLog = members.getConnectionLogFor(
                        token.getMember(), session);
                if (cLog != null) {
                    cLog.setLogoutDate(new Date());
                }
                token.setScopes(null);
                token.setPermissions(null);
                em.remove(token);
                log.info("Member w/ email=" + token.getMember().getEmail()
                        + " has been logged out");
            } else {
                log.debug("Anonymous session has been removed.");
            }
            em.remove(session);
        }

    }

    @Override
    public Session updateSession(Session session) {

        Session existing = getSession(session.getKey());
        if (existing != null) {
            existing.renew();
        }
        return session;

    }

    @Override
    public Member getMemberFor(Session session) {

        OAuthSofunAccessToken token = storage.getAccessToken(session.getKey());
        if (token != null) {
            return token.getMember();
        }
        return null;

    }

    @Override
    public void bind(Session session, Member member) {

        OAuthSofunAccessToken token = storage.getAccessToken(session.getKey());
        if (token.getMember() == null && member != null) {
            token.setMember(member);
            members.addConnectionLog(member, session, session.getCreated(),
                    session.getRemoteAddress());
            log.info("Member " + member.getEmail() + " logged in.");
        } else {
            if (!token.getMember().equals(member)) {
                // We should never be in this situation as we are preventing
                // this at OAUth service level.
                // We take action here in case of.
                log.error("Cannot rebound session to another Member... Destroying session in case of...");
                removeSession(session);
            } else {
                log.debug("Member + " + member.getEmail()
                        + " already logged in");
            }
        }

    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @Override
    public void cleanExpiredSessions() {

        log.debug("Cleaning up expired sessions...");

        String queryStr = "from " + SessionImpl.class.getSimpleName();
        TypedQuery<SessionImpl> query = em.createQuery(queryStr,
                SessionImpl.class);

        List<SessionImpl> sessions = query.getResultList();
        for (Session session : sessions) {
            if (session.isExpired()) {
                removeSession(session);
            }
        }

    }

    @Override
    public void renew(Session session) {
        session.renew();
        OAuthSofunAccessToken token = storage.getAccessToken(session.getKey());
        if (token != null) {
            Date now = new Date();
            token.setTimestamp(now.getTime());
        }
    }

}
