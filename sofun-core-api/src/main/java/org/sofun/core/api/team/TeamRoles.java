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
 * Team roles.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class TeamRoles {

    /**
     * Member is an Team administrator
     */
    public static final String ADMINISTRATOR = "Administrator";

    /**
     * Member simply joined the team.
     */
    public static final String MEMBER = "Member";

    /**
     * Member joined the team and depending on the type (free or gambling) has:
     * 
     * <li>
     * <ul>
     * <b>free:</b> at least one (1) prediction.
     * </ul>
     * <ul>
     * <b>gambling:</b> 1 prediction and a bet.
     * </ul>
     * </li>
     */
    public static final String PARTICIPANT = "Participant";
    
    /**
     * Member is none of the above.
     */
    public static final String ANONYMOUS = "Anonymous";

}
