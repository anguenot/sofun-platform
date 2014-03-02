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

package org.sofun.core.api.security;

/**
 * Sofun Roles
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 *
 */
public final class SofunRoles {
    
    /**
     * Administrator level role.
     */
    public static final String ADMIN = "Administrator";
    
    /**
     * Member (player) level role.
     */
    public static final String MEMBER = "Member";
    
    /**
     * Roles for anonymous (or not yet authenticated) operations.
     */
    public static final String ANONYMOUS = "Anonymous";
    
}
