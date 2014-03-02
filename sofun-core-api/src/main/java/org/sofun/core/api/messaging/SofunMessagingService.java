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

package org.sofun.core.api.messaging;

import java.io.Serializable;

/**
 * Sofun Messaging Service.
 * 
 * <p/>
 * 
 * Acts as a single point of access to existing platform queues.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface SofunMessagingService extends Serializable {

    /**
     * Sends a message to a particular destination.
     * 
     * @see {@link SofunMessagingDestination} for existing destination.
     * 
     * @param message: a {@link Serializable}
     * @param destination: an existing Sofun destination.
     */
    void sendMessage(Serializable message, String destination);

}
