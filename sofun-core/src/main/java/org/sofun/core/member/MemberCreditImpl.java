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

package org.sofun.core.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sofun.core.api.banking.CurrencyType;
import org.sofun.core.api.member.MemberCredit;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_credit")
public class MemberCreditImpl implements MemberCredit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "credit")
    protected float credit = 0;

    @Column(name = "currency", nullable = false)
    protected String currency = CurrencyType.EURO;

    @Column(name = "max_amount_bet_weekly")
    protected float maxAmountBetWeekly = 0;

    @Column(name = "max_amount_credit_weekly")
    protected float maxAmountCreditWeekly = 0;

    @Column(name = "automatic_wire_amount", columnDefinition = "int default 0")
    protected int automaticWireAmount = 0;

    public MemberCreditImpl() {
        super();
    }

    @Override
    public float getCredit() {
        return credit;
    }

    @Override
    public void setCredit(float credit) {
        this.credit = credit;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public float getMaxAmountPerWeekForBets() {
        return maxAmountBetWeekly;
    }

    @Override
    public void setMaxAmountPerWeekForBets(float max) {
        this.maxAmountBetWeekly = max;

    }

    @Override
    public float getMaxAmountPerWeekCredit() {
        return maxAmountCreditWeekly;
    }

    @Override
    public void setMaxAmountPerWeekCredit(float max) {
        this.maxAmountCreditWeekly = max;
    }

    @Override
    public int getAutomaticWireAmount() {
        return automaticWireAmount;
    }

    @Override
    public void setAutomaticWireAmount(int amount) {
        this.automaticWireAmount = amount;
    }

}
