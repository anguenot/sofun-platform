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

package org.sofun.core.security.oauth;

/**
 * OAuth consumer permissions.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class ConsumerPermission {

    /**
     * Application level permission.
     * 
     * <p/>
     * 
     * Facebook, Web Sites, Mobile applications would be granted this
     * permission.
     * 
     */
    public static final String APP = "APP";

    /**
     * Admin level consumer.
     * 
     * <p/>
     * 
     * Sofun Gaming platform backend application (external to the platform)
     * would be granted this permission.
     */
    public static final String ADMIN = "ADMIN";

}
