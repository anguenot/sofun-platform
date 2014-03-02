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
 * Member Platform Credit.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberCredit extends Serializable {

    /**
     * Returns the {@link Member} total credit on the gambling platform.
     * 
     * @return the total credit amount.
     */
    float getCredit();

    /**
     * Sets the {@link Member} total credit on the gambling platform.
     * 
     * @param credit: a float.
     */
    void setCredit(float credit);

    /**
     * Returns the currency corresponding to the member's credit.
     * 
     * @return a currency code as a {@link String}
     */
    String getCurrency();

    /**
     * Sets the currency corresponding to the member's credit.
     * 
     * @param currency: a currency code as a {@link String}
     */
    void setCurrency(String currency);

    /**
     * Returns the maximum amount (in the member's credit currency) a member can bet per week.
     * 
     * @return: a {@link Float} value.
     */
    float getMaxAmountPerWeekForBets();

    /**
     * Sets the maximum amount (in the member's credit currency) a member can bet per week.
     * 
     * @param max: a {@link Float} value.
     */
    void setMaxAmountPerWeekForBets(float max);

    /**
     * Returns the maximum amount (in the member's credit currency) a member can credit per week.
     * 
     * @return a {@link Float} value.
     */
    float getMaxAmountPerWeekCredit();

    /**
     * Sets the maximum amount (in the member's credit currency) a member can credit per week.
     * 
     * @param max: a {@link Float} value.
     */
    void setMaxAmountPerWeekCredit(float max);

    /**
     * Returns the amount (in the member's credit currency) at which an automatic wire should be send to the member's bank
     * account.
     * 
     * <p/>
     * 
     * If zero (0) no automatic wire will even occur. Only manual ones.
     * 
     * @return a positive {@link Integer} amount.
     */
    int getAutomaticWireAmount();

    /**
     * Sets the amount (in the member's credit currency) at which an automatic wire should be send to the member's bank account.
     * 
     * @param amount: an positive {@link Integer} amount.
     */
    void setAutomaticWireAmount(int amount);

}
