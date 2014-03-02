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

/**
 * Member account status.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class MemberAccountType implements Serializable {

    private static final long serialVersionUID = -8045884588131256588L;

    /**
     * Simple account type allowing one to use community features only.
     * 
     * <p />
     * Allowed to play only if verified. No gambling allowed for these accounts.
     * <p />
     * See {@link MemberAccountStatus}
     */
    public static final String SIMPLE = "SIMPLE";

    /**
     * French Gambling account: account compliant with French regulations.
     * 
     * <p />
     * Account type allowed to gamble only if verified. See {@link MemberAccountStatus}
     */
    public static final String GAMBLING_FR = "GAMBLING_FR";

}
