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

package org.sofun.core.messaging;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.local.SofunMessagingServiceLocal;
import org.sofun.core.api.messaging.SofunMessagingCredentials;
import org.sofun.core.api.messaging.SofunMessagingDestination;
import org.sofun.core.api.messaging.SofunMessagingService;
import org.sofun.core.api.remote.SofunMessagingServiceRemote;

/**
 * Sofun Messaging Service.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(SofunMessagingServiceLocal.class)
@Remote(SofunMessagingServiceRemote.class)
public class SofunMessagingServiceImpl implements SofunMessagingService {

    public static final String CON_FACTORY = "java:/JmsXA";

    private static final Log log = LogFactory.getLog(SofunMessagingServiceImpl.class);

    private static final long serialVersionUID = 5182409324364567700L;

    @Resource(mappedName = CON_FACTORY)
    protected transient ConnectionFactory connFactory;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @Resource(mappedName = SofunMessagingDestination.SOFUN_FACEBOOK)
    protected transient Queue facebookQueue;

    @Resource(mappedName = SofunMessagingDestination.SOFUN_FEEDS)
    protected transient Queue feedQueue;

    public SofunMessagingServiceImpl() {
        super();
    }

    public SofunMessagingServiceImpl(EntityManager em) {
        this.em = em;
    }

    protected Queue getQueueFor(String destination) {
        if (SofunMessagingDestination.SOFUN_FACEBOOK.equals(destination)) {
            return facebookQueue;
        } else if (SofunMessagingDestination.SOFUN_FEEDS.equals(destination)) {
            return feedQueue;
        } else {
            return null;
        }
    }

    @Override
    public void sendMessage(Serializable message, String destination) {

        Connection connection = null;
        Session session = null;
        MessageProducer sender = null;
        Queue q = getQueueFor(destination);
        if (q == null) {
            log.error("Cannot find associated queue for destination=" + destination);
        }
        try {
            connection = connFactory.createConnection(SofunMessagingCredentials.USERNAME, SofunMessagingCredentials.PASSWORD);
            session = connection.createSession(true, 0);
            sender = session.createProducer(q);
            ObjectMessage msg = session.createObjectMessage(message);
            sender.send(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (sender != null) {
                    sender.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                log.error(e.getMessage());
            }

        }

    }
}
