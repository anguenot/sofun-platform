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

package org.sofun.core.community;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.community.CommunityService;
import org.sofun.core.api.local.CommunityServiceLocal;
import org.sofun.core.api.remote.CommunityServiceRemote;

/**
 * Community Service Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(CommunityServiceLocal.class)
@Remote(CommunityServiceRemote.class)
public class CommunityServiceImpl implements CommunityService {

    private static final long serialVersionUID = 909745833449518859L;

    private static final Log log = LogFactory
            .getLog(CommunityServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    public CommunityServiceImpl() {
        super();
    }

    public CommunityServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public Community addCommunity(Community community) {
        final String name = community.getName();
        if (!exists(name)) {
            em.persist(community);
        }
        return community;
    }

    @Override
    public Community addCommunity(String name) {
        return addCommunity(new CommunityImpl(name));
    }

    @Override
    public long count() {
        final String queryStr = "SELECT COUNT(c.id) FROM "
                + CommunityImpl.class.getSimpleName() + " c";
        final Query query = createQuery(queryStr);
        return (Long) query.getSingleResult();
    }

    private Query createQuery(String queryStr) {
        final Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public Community delCommunity(Community community) {
        if (community == null) {
            return null;
        }
        final String name = community.getName();
        if (exists(name)) {
            // Fresh up
            community = getCommunityByName(name);
            if (community != null) {
                // XXX has not been tested.
                // We need to ensure that teams and members part of several
                // communities are not being removed.
                log.error("Deleting community is not implemented.");
            } else {
                return null;
            }
        }
        return community;
    }

    @Override
    public Community delCommunity(String name) {
        return delCommunity(getCommunityByName(name));
    }

    @Override
    public boolean exists(String name) {
        return getCommunityByName(name) != null ? true : false;
    }

    @Override
    public Community getCommunityById(long id) {
        final String queryStr = "from " + CommunityImpl.class.getSimpleName()
                + " c where c.id =:id";
        final Query query = createQuery(queryStr);
        query.setParameter("id", id);
        try {
            return (Community) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public Community getCommunityByName(String name) {
        if (name == null) {
            return null;
        }
        final String queryStr = "from " + CommunityImpl.class.getSimpleName()
                + " c where c.name =:name";
        final Query query = createQuery(queryStr);
        query.setParameter("name", name);
        try {
            return (Community) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public List<String> getCommunityNames() {
        final String queryStr = "SELECT name from "
                + CommunityImpl.class.getSimpleName();
        final TypedQuery<String> query = em.createQuery(queryStr, String.class);
        return query.getResultList();
    }

}
