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

package org.sofun.core.api.member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Member account status.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class MemberAccountStatus implements Serializable {

    private static final long serialVersionUID = -6632186826294163268L;

    /**
     * Account has been created pending confirmation.
     */
    public static final String CREATED = "CREATED";

    /**
     * Account has been confirmed.
     */
    public static final String CONFIRMED = "CONFIRMED";

    /**
     * Account pending verification for French gambling
     */
    public static final String PENDING_VERIFIED_FR = "PENDING_VERIFIED_FR";

    /**
     * Account verified for French gambling
     */
    public static final String VERIFIED_FR = "VERIFIED_FR";

    /**
     * Account is closed
     */
    public static final String CLOSED = "CLOSED";

    /**
     * Account has been suspended by the member himself.
     */
    public static final String MEMBER_SUSPENDED = "MEMBER_SUSPENDED";

    /**
     * Account has been suspended by an administrator.
     */
    public static final String ADMIN_SUSPENDED = "ADMIN_SUSPENDED";

    /**
     * Account is listed as banned by the French state
     */
    public static final String BANNED_FR = "BANNED_FR";

    /**
     * Returns the list if status for an active account.
     * 
     * @return
     */
    public static final List<String> getActiveStatus() {
        final List<String> status = new ArrayList<String>();
        status.add(CONFIRMED);
        status.add(PENDING_VERIFIED_FR);
        status.add(VERIFIED_FR);
        return status;
    }

    public static final List<String> getActiveGamblingFrStatus() {
        final List<String> status = new ArrayList<String>();
        status.add(CONFIRMED);
        status.add(PENDING_VERIFIED_FR);
        status.add(VERIFIED_FR);
        return status;
    }

}
