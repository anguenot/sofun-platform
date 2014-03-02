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

package org.sofun.platform.notification.api;

import java.io.Serializable;
import java.util.Map;

import org.sofun.core.api.member.Member;
import org.sofun.core.api.notification.Notification;

/**
 * Member related notifications.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberNotification extends Serializable {
    
    /**
     * Returns the member bound to the notification scheme.
     * 
     * @return a {@link Member} instance.
     */
    Member getMember();
    
    /**
     * Sets the member bound to the notification scheme.
     * 
     * @param member: a {@link Member} instance.
     */
    void setMember(Member member);
    
    /**
     * Returns the notification scheme.
     * 
     * @see {@link Notification} for constants.
     * 
     * @return a {@link Map} from notification id (string) to active or passive (true or false)
     */
    Map<String, Boolean> getScheme();
    
    /**
     * Sets the notification scheme.
     * 
     * @see {@link Notification} for constants.
     * 
     * @param scheme: a {@link Map} from notification id (string) to active or passive (true or false)
     */
    void setScheme(Map<String, Boolean> scheme);

}
