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

package org.sofun.core.api.banking;

import java.io.Serializable;
import java.util.Date;

/**
 * Base Transaction.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface Transaction extends Serializable {

    /**
     * Returns the transaction date.
     * 
     * @return a {@link Date} in UTC
     */
    Date getDate();

    /**
     * Sets the transaction date.
     * 
     * @param date: a {@link Date} in UTC.
     */
    void setDate(Date date);

    /**
     * Returns the transaction amount.
     * 
     * @return a positive {@link Float} value.
     */
    float getAmount();

    /**
     * Sets the transaction amount.
     * 
     * @param amount: a positive {@link Float} value.
     */
    void setAmount(float amount);

    /**
     * Returns the currency involved in this transaction.
     * 
     * @return a {@link String} representing the currency in use for this
     *         transaction.
     */
    String getCurrency();

    /**
     * Sets the currency involved in this transaction.
     * 
     * @param currency: a {@link String} representing the currency in use for
     *        this transaction.
     */
    void setCurrency(String currency);

    /**
     * Is it a credit?
     * 
     * @return true if credit.
     */
    boolean isCredit();

    /**
     * Sets if whether or not this is a credit transaction.
     * 
     * @param credit: a {@link Boolean}
     */
    void setCredit(boolean credit);

    /**
     * Is it a debit?
     * 
     * @return true if debit.
     */
    boolean isDebit();

    /**
     * Sets if whether or not this is a debit transaction.
     * 
     * @param debit: a {@link Boolean}
     */
    void setDebit(boolean debit);

    /**
     * Returns the transaction label.
     * 
     * @return a label as a {@link String}
     */
    String getLabel();

    /**
     * Sets the transaction label.
     * 
     * @param label: a label as a {@link String}
     */
    void setLabel(String label);

    /**
     * Returns the transaction type.
     * 
     * @return: a type as a {@link String}
     */
    String getType();

    /**
     * Sets the transaction type.
     * 
     * @param type: a type as a {@link String}
     */
    void setType(String type);

}
