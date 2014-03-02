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

/**
 * Sofun Messaging Destination.
 * 
 * <p/>
 * 
 * This has to be in sync w/ the file defining the actual queues. Currently it uses Hornet (default in JBoss AS).
 * 
 * @see: sofun-ear/src/main/application/META-INF/sofun-hornetq-jms.xml
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class SofunMessagingDestination {

    public static final String DESTINATION_TYPE = "javax.jms.Queue";

    /**
     * Queue used to perform asynchronous Facebook API calls.
     */
    public static final String SOFUN_FACEBOOK = "queue/sofun-facebook";

    /**
     * Queue used to handle asynchronous events and create corresponding feed entries.
     */
    public static final String SOFUN_FEEDS = "/queue/sofun-feeds";

}
