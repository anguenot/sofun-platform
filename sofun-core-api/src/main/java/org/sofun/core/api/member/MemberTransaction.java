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

import org.sofun.core.api.banking.Transaction;

/**
 * Member Originated Transaction.
 * 
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberTransaction extends Transaction {

    long getId();

    /**
     * Returns the {@link Member} orginating the transaction.
     * 
     * @return a {@link Member} instance.
     */
    Member getMember();

    /**
     * Sets the {@link Member} originating the transaction.
     * 
     * @param member: a {@link Member} instance.
     */
    void setMember(Member member);

    /**
     * Does this transaction corresponds to a bonus?
     * 
     * @return a {@link Boolean}
     */
    boolean isBonus();

    /**
     * Sets if whether or not this transactions corresponds to a bonus.
     * 
     * @param bonus: a {@link Boolean};
     */
    void setBonus(boolean bonus);

    /**
     * returns the actual bank transaction identifier.
     * 
     * @return a {@link String}
     */
    String getTransactionId();

    /**
     * Sets the actual transaction identifier.
     * 
     * @param tid: a {@link String}
     */
    void setTransactionId(String tid);

    /**
     * Returns the transaction oauth number.
     * 
     * @return: a {@link String}
     */
    String getAuthorization();

    /**
     * Sets the transaction oauth number.
     * 
     * @param auth: the authorization as a {@link String}
     */
    void setAuthorization(String auth);

    /**
     * Returns the transaction status.
     * 
     * @return one of {@link MemberTransactionStatus}
     */
    String getStatus();

    /**
     * Sets the transaction status.
     * 
     * @param status: one of {@link MemberTransactionStatus}
     */
    void setStatus(String status);

    /**
     * Gets the transaction status code.
     * 
     * @return a code, vendor dependent.
     */
    String getStatusCode();

    /**
     * Sets the transaction status code.
     * 
     * @param code: a code, vendor dependent.
     */
    void setStatusCode(String code);

    /**
     * Is the transaction acknowledged by player ?
     * 
     * <p>
     * 
     * Player acknowledgment is mandatory for bonus and credit by ARJEL.
     * 
     * <p>
     * 
     * Default to false.
     * 
     * @return true or false
     */
    boolean isAck();

    /**
     * Is the transaction acknowledged by player ?
     * 
     * <p>
     * 
     * Player acknowledgment is mandatory for bonus and credit by ARJEL.
     * 
     * <p>
     * 
     * Default value is false.
     * 
     * @param true or false
     */
    void setAck(boolean ack);

    /**
     * Get the member credit before the actual transaction.
     * 
     * <p>
     * 
     * We need this because of ARJEL requirements for traces.
     * 
     * @return a float (default to 0 (zero))
     */
    float getMemberCreditBefore();

    /**
     * Set member credit before the actual transaction.
     * 
     * <p>
     * 
     * We need this because of ARJEL requirements for traces.
     * 
     * @parameter a float (default to 0 (zero))
     */
    void setMemberCreditBefore(float memberCreditBefore);
    
    /**
     * Get the member credit after the actual transaction.
     * 
     * <p>
     * 
     * We need this because of ARJEL requirements for traces.
     * 
     * @return a float (default to 0 (zero))
     */
    float getMemberCreditAfter();

    /**
     * Set member credit after the actual transaction.
     * 
     * <p>
     * 
     * We need this because of ARJEL requirements for traces.
     * 
     * @parameter a float (default to 0 (zero))
     */
    void setMemberCreditAfter(float memberAfterBefore);

}
