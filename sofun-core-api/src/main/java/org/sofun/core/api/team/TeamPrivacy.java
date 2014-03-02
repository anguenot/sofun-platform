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

package org.sofun.core.api.team;


/**
 * Team Privacy.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class TeamPrivacy {

    /**
     * A team that can be accessed by all members.
     */
    public static final String PUBLIC = "PUBLIC";

    /**
     * A team that can be accessed by invited members or using a password.
     */
    public static final String PRIVATE = "PRIVATE";

    /**
     * A public team that can be accessed only by french gambling accounts.
     * 
     * @see TeamPrivacy.PUBLIC
     */
    public static final String PUBLIC_GAMBLING_FR = "PUBLIC_GAMBLING_FR";

    /**
     * A private team that can be accessed only by french gambling accounts.
     * 
     * @see TeamPrivacy.PRIVATE
     */
    public static final String PRIVATE_GAMBLING_FR = "PRIVATE_GAMBLING_FR";

}
