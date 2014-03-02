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

package org.sofun.core.api.kup.bet;

import java.io.Serializable;
import java.util.Date;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberTransaction;

/**
 * Kup Bet.
 * 
 * <p/>
 * 
 * A unique member (debit) transaction is associated to a Kup bet.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface KupMemberBet extends Serializable {

    /**
     * Returns the {@link Kup} linked to this bet.
     * 
     * @return a {@link Kup} instance.
     */
    Kup getKup();

    /**
     * Sets the corresponding {@link Kup}
     * 
     * @param kup: a {@link Kup} instance.
     */
    void setKup(Kup kup);

    /**
     * Returns the member who placed the bets.
     * 
     * <p/>
     * Also available from the {@link MemberTransaction}.
     * 
     * @return a {@link Member} instance.
     */
    Member getMember();

    /**
     * Sets the member who place the bet.
     * 
     * @param member: a {@link Member} instance.
     */
    void setMember(Member member);

    /**
     * Returns the corresponding member transaction.
     * 
     * <p/>
     * Date of the transcation is the date of the bet. We don't do credit.
     * 
     * @return a {@link MemberTransaction}
     */
    MemberTransaction getMemberTransaction();

    /**
     * Sets the corresponding the member transaction.
     * 
     * <p/>
     * Date of the transcation is the date of the bet. We don't do credit.
     * 
     * @param transaction: a {@link MemberTransaction}
     */
    void setMemberTransaction(MemberTransaction transaction);

    /**
     * Returns the bet effective date.
     * 
     * <p/>
     * 
     * The bet effective date is when the participation of the {@link Member} to
     * a {@link Kup} has been validated: when member placed a bet
     * <strong>and</strong> recoded a prediction.
     * 
     * @return a {@link Date} in UTC
     */
    Date getEffectiveDate();

    /**
     * Sets the bet effective date.
     * 
     * <p/>
     * 
     * The bet effective date is when the participation of the {@link Member} to
     * a {@link Kup} has been validated: when member placed a bet
     * <strong>and</strong> recorded a prediction.
     * 
     * @param effectiveDate: a {@link Date} in UTC.
     */
    void setEffectiveDate(Date effectiveDate);

}
