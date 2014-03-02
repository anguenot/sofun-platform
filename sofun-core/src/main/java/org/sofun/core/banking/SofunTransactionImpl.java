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

package org.sofun.core.banking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sofun.core.api.banking.CurrencyType;
import org.sofun.core.api.banking.SofunTransaction;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.kup.KupImpl;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "sofun_transactions")
public class SofunTransactionImpl implements SofunTransaction {

    private static final long serialVersionUID = -7892529918266733708L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "txn_date", nullable = false)
    protected Date date;

    @Column(name = "txn_amount", nullable = false)
    protected float amount;

    @Column(name = "txn_currency", nullable = false)
    protected String currency = CurrencyType.EURO;

    @Column(name = "txn_credit", nullable = false)
    protected boolean credit = false;

    @Column(name = "txn_debit", nullable = false)
    protected boolean debit = false;

    @Column(name = "label", nullable = false)
    protected String label;

    @Column(name = "txn_type", nullable = false)
    protected String type;

    @ManyToOne(targetEntity = KupImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "kup_id")
    protected Kup kup;

    public SofunTransactionImpl() {
    }

    public SofunTransactionImpl(Kup kup, Date date, float amount,
            String currency, String label, String type) {
        setKup(kup);
        setDate(date);
        setAmount(amount);
        setCurrency(currency);
        setLabel(label);
        setType(type);
    }

    @Override
    public Date getDate() {
        if (date != null) {
            return (Date) date.clone();
        }
        return null;
    }

    @Override
    public void setDate(Date date) {
        if (date != null) {
            this.date = (Date) date.clone();
        } else {
            this.date = null;
        }
    }

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public void setAmount(float amount) {
        this.amount = amount;
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
    public boolean isCredit() {
        return credit;
    }

    @Override
    public void setCredit(boolean credit) {
        this.credit = credit;
        this.debit = !credit;
    }

    @Override
    public boolean isDebit() {
        return debit;
    }

    @Override
    public void setDebit(boolean debit) {
        this.debit = debit;
        this.credit = !debit;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Kup getKup() {
        return kup;
    }

    @Override
    public void setKup(Kup kup) {
        this.kup = kup;
    }

}
