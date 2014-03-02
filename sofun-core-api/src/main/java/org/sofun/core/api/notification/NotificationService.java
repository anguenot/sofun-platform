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

package org.sofun.core.api.notification;

import java.io.Serializable;
import java.util.Map;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;

/**
 * Notification service
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface NotificationService extends Serializable {

    /**
     * Returns the notifications scheme for a given {@link Member}
     * 
     * @see {@link Notification} for constants.
     * 
     * @param member: a {@link Member} instance
     * @return a {@link Map} from notification id (string) to active or passive (true or false)
     */
    Map<String, Boolean> getNotificationsSchemeFor(Member member);

    /**
     * Sets the notification scheme for a given {@link Member}
     * 
     * @see {@link Notification} for constants.
     * 
     * @param member: a {@link Member} instance
     * @param scheme : a {@link Map} from notification id (string) to active or passive (true or false)
     */
    void setNotificationSchemeFor(Member member, Map<String, Boolean> scheme);

    /**
     * Sends an email to given recipients.
     * 
     * <p/>
     * 
     * `params` contains the list of key value args that will be forwarded to the template engine.
     * 
     * <p/>
     * 
     * In addition, the service will be expecting the following key, value pairs:
     * 
     * <ul>
     *     <li>templateId: id of the template to use (format: templateId + "_" + locale)</li>
     *     <li>format: format of the email</li>
     *     <li>subject: subject of the email</li>
     * </ul>
     * 
     * @param member: a {@link Member} instance
     * @param params: map from string to string.
     * @throws CoreException
     */
    void sendEmail(Member member, Map<String, String> params) throws CoreException;

}
