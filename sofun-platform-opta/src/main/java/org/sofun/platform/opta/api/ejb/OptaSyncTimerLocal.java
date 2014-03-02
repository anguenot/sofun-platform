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

package org.sofun.platform.opta.api.ejb;

/**
 * Local business interface for Opta FTP Sync timer.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface OptaSyncTimerLocal {

    /**
     * Synchronize RU feeds
     * 
     * <p/>
     * 
     * Not live feed parsing here.
     * 
     * @throws Exception
     */
    void syncRU() throws Exception;

    /**
     * Synchronize RU feeds
     * 
     * @throws Exception
     */
    void syncRULive() throws Exception;

    /**
     * Synchronize F feeds.
     * 
     * <p/>
     * 
     * Not live feed parsing here.
     * 
     * @throws Exception
     */
    void syncF() throws Exception;

    /**
     * Synchronize F live feeds.
     * 
     * @throws Exception
     */
    void syncFLive() throws Exception;

    /**
     * Synchronize F1 feeds.
     * 
     * @throws Exception
     */
    void syncFormula1() throws Exception;

    /**
     * Synchronize Tennis feeds.
     * 
     * <p/>
     * 
     * Not live feed parsing here.
     * 
     * @throws Exception
     */
    void syncTennis() throws Exception;

    /**
     * Synchronize Tennis live feeds.
     * 
     * @throws Exception
     */
    void syncTennisLive() throws Exception;

    /**
     * Synchronize Cycling Feeds.
     * 
     * <p>
     * 
     * Not live feed parsing here.
     * 
     * @throws Exception
     */
    void syncCycling() throws Exception;

    /**
     * Synchronize Basket Feeds.
     * 
     * <p>
     * 
     * Not live feed parsing here.
     * 
     * @throws Exception
     */
    void syncBasket() throws Exception;

}
