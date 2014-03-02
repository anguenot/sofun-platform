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

package org.sofun.platform.facebook.async;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.messaging.SofunMessagingCredentials;
import org.sofun.core.api.messaging.SofunMessagingDestination;
import org.sofun.platform.facebook.api.FacebookService;
import org.sofun.platform.facebook.api.local.FacebookServiceLocal;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = SofunMessagingDestination.DESTINATION_TYPE),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "50"),
        @ActivationConfigProperty(propertyName = "minSession", propertyValue = "15"),
        @ActivationConfigProperty(propertyName = "user", propertyValue = SofunMessagingCredentials.USERNAME),
        @ActivationConfigProperty(propertyName = "password", propertyValue = SofunMessagingCredentials.PASSWORD),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = SofunMessagingDestination.SOFUN_FACEBOOK) })
public class FacebookGraphListener implements MessageListener {

    private static final Log log = LogFactory.getLog(FacebookGraphListener.class);

    @EJB(beanName = "FacebookServiceImpl", beanInterface = FacebookServiceLocal.class)
    private FacebookService facebookService;

    @EJB(beanName = "MemberServiceImpl", beanInterface = MemberServiceLocal.class)
    private MemberService memberService;

    private FacebookService getFacebookService() {
        return facebookService;
    }

    private MemberService getMemberService() {
        return memberService;
    }

    private Member getMember(String email) throws Exception {
        return getMemberService().getMember(email);
    }

    @Override
    public void onMessage(Message message) {

        try {
            if (message instanceof ObjectMessage) {

                final String email = (String) ((ObjectMessage) message).getObject();
                log.debug("Reconnecting to Facebook Graph to collect information about Member with email=" + email);

                Member member = getMember(email);
                if (member != null) {
                    log.debug("Finding friends and connection for member with email=" + email);
                    getFacebookService().updateMemberInfoFromFacebook(member);
                } else {
                    log.error("Cannot find member with email=" + email);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
