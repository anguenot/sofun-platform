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
import java.util.Date;

/**
 * Password reset token.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface PasswordResetToken extends Serializable {

    /**
     * Returns the token value.
     * 
     * @return a string token value.
     */
    String getToken();

    /**
     * Returns the member that requested a reset token.
     * 
     * @return a {@link Member} instance.
     */
    Member getMember();

    /**
     * Returns the token creation date.
     * 
     * @return a {@link Date} in UTC
     */
    Date getCreated();

    /**
     * Returns the token expiration date.
     * 
     * @return a {@link Date} in UTC
     */
    Date getExpired();

    /**
     * Has the token expired?
     * 
     * @return true or false.
     */
    boolean isExpired();

}
