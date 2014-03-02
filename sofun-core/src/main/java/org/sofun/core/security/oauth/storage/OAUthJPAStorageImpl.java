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

package org.sofun.core.security.oauth.storage;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.OAuthJPAStorage;
import org.sofun.core.api.local.OAuthJPAStorageLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.remote.OAuthJPAStorageRemote;
import org.sofun.core.security.oauth.OAuthSofunAccessToken;
import org.sofun.core.security.oauth.OAuthSofunConsumer;

/**
 * OAuth JPA storage implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(OAuthJPAStorageLocal.class)
@Remote(OAuthJPAStorageRemote.class)
public class OAUthJPAStorageImpl implements OAuthJPAStorage {

    private static final long serialVersionUID = 3476721773636246570L;

    private static final Log log = LogFactory.getLog(OAUthJPAStorageImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    private transient EntityManager em;

    public OAUthJPAStorageImpl() {
        super();

    }

    public OAUthJPAStorageImpl(EntityManager em) {
        this();
        this.em = em;
    }

    private Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public OAuthSofunConsumer getConsumer(String key) {

        OAuthSofunConsumer consumer = null;

        String queryStr = "from " + OAuthSofunConsumer.class.getSimpleName()
                + " c where c.key=:key";
        Query query = createQuery(queryStr);
        query.setParameter("key", key);

        try {
            consumer = (OAuthSofunConsumer) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

        return consumer;

    }

    @Override
    public void addConsumer(OAuthSofunConsumer consumer) {
        em.persist(consumer);
    }

    @Override
    public OAuthSofunAccessToken getAccessToken(String key) {

        OAuthSofunAccessToken token = null;

        String queryStr = "from " + OAuthSofunAccessToken.class.getSimpleName()
                + " t where t.token=:token";
        Query query = createQuery(queryStr);
        query.setParameter("token", key);

        try {
            token = (OAuthSofunAccessToken) query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("Cannot find token with key=" + key);
            return null;
        }

        return token;

    }

    @Override
    public void addAccessToken(OAuthSofunAccessToken token) {
        em.persist(token);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OAuthSofunAccessToken> getExpiredAccessTokens() {
        String queryStr = "from " + OAuthSofunAccessToken.class.getSimpleName();
        Query query = createQuery(queryStr);
        return query.getResultList();
    }

    @Override
    public OAuthSofunAccessToken addAccessToken(String token, String secret,
            long timeToLive, OAuthSofunConsumer consumer) {
        consumer = getConsumer(consumer.getKey());
        OAuthSofunAccessToken t = new OAuthSofunAccessToken(token, secret,
                consumer.getScopes(), consumer.getPermissions(), timeToLive,
                consumer);
        addAccessToken(t);
        return t;
    }

    @Override
    public OAuthSofunAccessToken getAccessTokenFor(Member member) {

        OAuthSofunAccessToken token = null;

        String queryStr = "from " + OAuthSofunAccessToken.class.getSimpleName()
                + " t where t.member=:member";
        Query query = createQuery(queryStr);
        query.setParameter("member", member);

        try {
            token = (OAuthSofunAccessToken) query.getSingleResult();
        } catch (NoResultException nre) {
            log.debug("Cannot find token for member=" + member.getEmail());
            return null;
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        return token;

    }

}
