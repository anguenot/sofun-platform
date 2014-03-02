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

/**
 * Member transaction types.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public final class MemberTransactionType {

    /**
     * Member credits its account using a credit card.
     */
    public static final String CC_CREDIT = "CC Credit";

    /**
     * Member wires cash.
     */
    public static final String WIRE_DEBIT = "Wire Debit";

    /**
     * Member places a bet and debit from his account.
     */
    public static final String BET_DEBIT = "Bet Debit";

    /**
     * Member gets credit from winning a bet.
     */
    public static final String BET_CREDIT = "Bet Credit";

    /**
     * Member gets credit from a bonus credit.
     */
    public static final String BONUS_CREDIT = "Bonus Credit";

}
