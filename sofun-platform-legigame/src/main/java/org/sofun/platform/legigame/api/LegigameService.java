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

package org.sofun.platform.legigame.api;

import java.io.Serializable;

import org.sofun.platform.legigame.api.exception.LegigameException;

/**
 * Legigame Service
 * 
 * <p>
 * 
 * Legigame is used as part of a global process for new player account
 * validation.
 * 
 * <p>
 * 
 * The data to be exchanged between LegiGame and the operator is the basic data
 * of a player (name, address, data to be checked...). Declaration of a new
 * player could be done with a simple form (not recommended – use for test
 * purpose only) or preferably with an HTTP or HTTPS Client posting player’s
 * data to the CreateOrUpdate web page. The operator can then use, in a pull
 * mode, POST a player ID to the Read web page to retrieve in XML a full player
 * record (including its status) from the LegiGame referential database. As an
 * alternative to the pull mode, LegiGame may POST the player’s data at each
 * status change to a similar Notify web page hosted by the operator.
 * 
 * <p>
 * 
 * Access to the LegiGame server is restricted to a limited list of declared IP.
 * A .htaccess also ensures additional security level. In the form, the operator
 * provides its specific login / password credentials.
 * 
 * <p>
 * 
 * Only valid for French players of www.betkup.fr.
 * 
 * @see legigame.properties
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface LegigameService extends Serializable {

    /**
     * Sync up Sofun players with remote legigame backend
     * 
     * @param batchSize
     * @param offset
     * @throws Exception
     */
    void syncMembers(int offset, int batchSize) throws LegigameException;

    /**
     * Checks status legigame side.
     * 
     * @throws LegigameException
     */
    void syncStatus() throws LegigameException;

}
