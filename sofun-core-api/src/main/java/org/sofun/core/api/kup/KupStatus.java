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
 * Kup status.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class KupStatus {

    /**
     * Kup is created but not started. Not opened for predictions.
     */
    public static final byte CREATED = 0;

    /**
     * First sport's event did not start yet. Kup is opened for predictions and
     * bets.
     */
    public static final byte OPENED = 1;

    /**
     * First sport's event did start. Kup might be opened for predictions and
     * bets on <strong>others</strong> sports events part of the Kup at a date
     * in the future.
     */
    public static final byte ON_GOING = 2;

    /**
     * Last sport's event of the Kup did start. The Kup is now close. No
     * predictions and no bets anymore.
     */
    public static final byte CLOSED = 3;

    /**
     * Kup's ranking table is final.
     */
    public static final byte SETTLED = 4;

    /**
     * Kup's winners have been paid out.
     */
    public static final byte PAID_OUT = 5;

    /**
     * Kup is canceled. Member have been reimbursed.
     */
    public static final byte CANCELED = -1;

    /**
     * Returns Kup's active status only.
     * 
     * @return: an array of bytes.
     */
    public static final byte[] getActiveStatus() {
        byte[] status = new byte[2];
        status[0] = OPENED;
        status[1] = ON_GOING;
        return status;
    }

    public static final byte[] getAllStatus() {
        byte[] status = new byte[6];
        status[0] = OPENED;
        status[1] = ON_GOING;
        status[2] = CLOSED;
        status[3] = SETTLED;
        status[4] = PAID_OUT;
        status[5] = CANCELED;
        return status;
    }

}
