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

package org.sofun.platform.opta.impl;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.platform.opta.api.OptaException;
import org.sofun.platform.opta.api.OptaService;
import org.sofun.platform.opta.api.ejb.OptaServiceLocal;
import org.sofun.platform.opta.api.ejb.OptaSyncTimerLocal;

/**
 * Opta Sync Timer
 * 
 * <p>
 * 
 * This timer will trigger the processing of various Opta files on regular
 * basis.
 * 
 * <p>
 * 
 * All business logic shall <b>not</b> be defined within this singleton but
 * within the underlying {@link OptaService} instance.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(OptaSyncTimerLocal.class)
public class OptaSyncTimer implements OptaSyncTimerLocal {

    private static final Log log = LogFactory.getLog(OptaSyncTimer.class);

    @EJB(beanName = "OptaServiceImpl", beanInterface = OptaServiceLocal.class)
    private OptaService optaService;

    /* F feeds processing lock */
    private boolean LOCK_F = false;

    /* F live feeds processing lock */
    private boolean LOCK_LIVE_F = false;

    /* RU feeds processing lock */
    private boolean LOCK_RU = false;

    /* RU live feeds processing lock */
    private boolean LOCK_LIVE_RU = false;

    /* F1 feeds processing lock */
    private boolean LOCK_F1 = false;

    /* Tennis feeds processing lock */
    private boolean LOCK_T = false;

    /* Tennis live feeds processing lock */
    private boolean LOCK_LIVE_T = false;

    /* Cycling feeds processing lock */
    private boolean LOCK_CY = false;

    /* Basket feeds processing lock */
    private boolean LOCK_BB = false;

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncF() throws Exception {
        if (LOCK_F) {
            return;
        }
        try {
            LOCK_F = true;
            optaService.syncF();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_F = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncFLive() throws Exception {
        if (LOCK_LIVE_F) {
            return;
        }
        try {
            LOCK_LIVE_F = true;
            optaService.syncFLiveFeeds();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_LIVE_F = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncRU() throws Exception {
        if (LOCK_RU) {
            return;
        }
        try {
            LOCK_RU = true;
            optaService.syncRU();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_RU = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncRULive() throws Exception {
        if (LOCK_LIVE_RU) {
            return;
        }
        try {
            LOCK_LIVE_RU = true;
            optaService.syncRULiveFeeds();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_LIVE_RU = false;
        }
    }

    @Override
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncFormula1() throws Exception {
        if (LOCK_F1) {
            return;
        }
        try {
            LOCK_F1 = true;
            optaService.syncFormula1();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_F1 = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncTennis() throws Exception {
        if (LOCK_T) {
            return;
        }
        try {
            LOCK_T = true;
            optaService.syncTennis();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_T = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncTennisLive() throws Exception {
        if (LOCK_LIVE_T) {
            return;
        }
        try {
            LOCK_LIVE_T = true;
            optaService.syncTennisLiveFeeds();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_LIVE_T = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncCycling() throws Exception {
        if (LOCK_CY) {
            return;
        }
        try {
            LOCK_CY = true;
            optaService.syncCycling();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_CY = false;
        }
    }

    @Override
    // XXX DISABLED
    // @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void syncBasket() throws Exception {
        if (LOCK_BB) {
            return;
        }
        try {
            LOCK_BB = true;
            optaService.syncBasket();
        } catch (OptaException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_BB = false;
        }
    }

}
