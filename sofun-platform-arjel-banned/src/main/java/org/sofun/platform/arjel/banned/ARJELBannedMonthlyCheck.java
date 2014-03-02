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

package org.sofun.platform.arjel.banned;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.platform.arjel.banned.api.ARJELBannedService;
import org.sofun.platform.arjel.banned.api.MemberARJELBannedCheck;
import org.sofun.platform.arjel.banned.api.ejb.ARJELBannedServiceLocal;
import org.sofun.platform.arjel.banned.api.exception.ARJELBannedException;

/**
 * ARJEL Banned Players list checks on regular basis
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class ARJELBannedMonthlyCheck {

    private static final Log log = LogFactory
            .getLog(ARJELBannedMonthlyCheck.class);

    /** Lock in case computing takes more than 5 minutes */
    private boolean LOCK = false;

    @EJB(
            beanName = "ARJELBannedServiceImpl",
            beanInterface = ARJELBannedServiceLocal.class)
    private ARJELBannedService arjel;

    public ARJELBannedMonthlyCheck() {
    }

    // XXX disable since ops are over.
    // @Schedule(minute = "15", hour = "4", persistent = false)
    // @Lock(LockType.READ)
    public void check() {
        if (LOCK) {
            return;
        } else {
            LOCK = true;
        }
        try {
            log.info("Checking players banned status for members that did not"
                    + " get checked during the past 30 days.");
            for (MemberARJELBannedCheck mstatus : arjel.getMembersToVerify()) {
                // isBanned() will transition and log member if needed.
                arjel.isBanned(mstatus.getMember(), true);
            }
        } catch (ARJELBannedException e) {
            log.error("An error occured while checking ARJEL banned players "
                    + "·list :" + e.getMessage());
        } finally {
            LOCK = false;
        }
    }
}
