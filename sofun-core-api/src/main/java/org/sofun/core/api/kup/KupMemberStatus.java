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

package org.sofun.core.api.kup;

/**
 * Kup's member status.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class KupMemberStatus {

    /**
     * Kup has not prediction and not bet yet.
     */
    public static final byte NOT_VALIDATED = 0;

    /**
     * Member has prediction(s).
     */
    public static final byte HAS_PREDICTION = 1;

    /**
     * Member has bet.
     */
    public static final byte HAS_BET = 2;

    /**
     * Member has prediction(s) and bet.
     */
    public static final byte VALIDATED = 3;

}
