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

package org.sofun.platform.legigame;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.platform.legigame.api.LegigameService;
import org.sofun.platform.legigame.api.ejb.LegigameServiceLocal;
import org.sofun.platform.legigame.api.exception.LegigameException;

/**
 * Legigame Member Status manager.
 * 
 * <p>
 * 
 * Singleton with timer dealing with member status and legigame on regular
 * basis.
 * 
 * @see LegigameService
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class MemberLegigameStatusManager {

    private static final Log log = LogFactory
            .getLog(MemberLegigameStatusManager.class);

    /** Lock in case computing takes more than 5 minutes */
    private boolean LOCK_SYNC_MEMBERS = false;

    /** Lock in case computing takes more than 5 minutes */
    private boolean LOCK_SYNC_STATUS = false;

    /** Member's lookup offset */
    private int offset = 0;

    /** Member's lookup batch size */
    private final int batchSize = 200;

    @EJB(
            beanName = "LegigameServiceImpl",
            beanInterface = LegigameServiceLocal.class)
    private LegigameService legigame;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService members;

    public MemberLegigameStatusManager() {
    }

    public MemberLegigameStatusManager(LegigameService legigame) {
        // Mostly for testing purpose.
        this.legigame = legigame;
    }

    @Schedule(minute = "15", hour = "3", persistent = false)
    @Lock(LockType.READ)
    public void syncMembers() {
        if (LOCK_SYNC_MEMBERS) {
            return;
        } else {
            LOCK_SYNC_MEMBERS = true;
        }
        try {
            if (legigame != null) {
                log.debug("Syncing our players with remote Legigame.");
                legigame.syncMembers(offset, batchSize);
                if (offset + batchSize >= members.countMembers()) {
                    log.debug("Offset back to zero (0)");
                    offset = 0;
                } else {
                    offset += batchSize;
                }
            }
        } catch (LegigameException e) {
            log.error("An error occured while syncing with legigame "
                    + "·list :" + e.getMessage());
        } finally {
            LOCK_SYNC_MEMBERS = false;
        }
    }

    @Schedule(minute = "*/59", hour = "*", persistent = false)
    @Lock(LockType.READ)
    public void syncStatus() {
        if (LOCK_SYNC_STATUS) {
            return;
        } else {
            LOCK_SYNC_STATUS = true;
        }
        try {
            if (legigame != null) {
                log.debug("Check members status legigame side.");
                legigame.syncStatus();
            }
        } catch (LegigameException e) {
            log.error("An error occured while syncing with legigame "
                    + "·list :" + e.getMessage());
        } finally {
            LOCK_SYNC_STATUS = false;
        }
    }
}
