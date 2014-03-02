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

package org.sofun.core.kup.stat;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.local.KupServiceLocal;

/**
 * Kup Stats Timer.
 * 
 * <p>
 * Clock triggering points computations.
 * </p>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class KupStatsTimer {

    private static final Log log = LogFactory.getLog(KupStatsTimer.class);

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    private boolean available = true;

    @Timeout
    @Schedule(minute = "15", hour = "2", persistent = false)
    @Lock(LockType.READ)
    public void check() throws Exception {
        if (!available) {
            return;
        } else {
            available = false;
        }
        try {
            kups.updateKupsStats();
        } catch (Throwable t) {
            t.printStackTrace();
            log.error(t.getMessage());
        } finally {
            available = true;
        }
    }

}
