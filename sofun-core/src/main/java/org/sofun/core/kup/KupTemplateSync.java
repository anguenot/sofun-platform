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

package org.sofun.core.kup;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.KupTemplateSyncLocal;

/**
 * Kup Template Sync.
 * 
 * <p>
 * 
 * Trigger the sync of Kups with their respective templates.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Startup
@Singleton
@Lock(LockType.READ)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(KupTemplateSyncLocal.class)
public class KupTemplateSync implements KupTemplateSyncLocal {

    private static final Log log = LogFactory.getLog(KupTemplateSync.class);

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    /* Processing lock for sync from templates */
    private boolean LOCK_FROM_TEMPLATE = false;

    /* Processing lock for template's sync */
    private boolean LOCK_TEMPLATES = false;

    @Override
    @Schedule(minute = "*/15", hour = "*", persistent = false)
    public void syncKupsWithTemplate() {
        if (LOCK_FROM_TEMPLATE) {
            return;
        }
        try {
            LOCK_FROM_TEMPLATE = true;
            kups.syncKupsWithTemplate();
        } catch (CoreException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_FROM_TEMPLATE = false;
        }
    }

    @Override
    @Schedule(minute = "*/15", hour = "*", persistent = false)
    public void syncKupTemplates() {
        if (LOCK_TEMPLATES) {
            return;
        }
        try {
            LOCK_TEMPLATES = true;
            kups.syncTemplates();
        } catch (CoreException e) {
            log.error(e.getMessage());
        } finally {
            LOCK_TEMPLATES = false;
        }
    }

}
