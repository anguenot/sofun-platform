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

package org.sofun.platform.web.rest.api.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.sofun.core.api.member.MemberTransaction;
import org.sofun.platform.web.rest.api.kup.ReSTKup;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.team.ReSTTeam;

/**
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public class ReSTTransaction implements Serializable {

    private static final long serialVersionUID = -7178295112508812074L;

    protected String txnId;

    protected long txnUUID;

    protected String txnStatus;

    protected String txnStatusCode;

    protected String txnLabel;

    protected String type;

    protected float txnAmount;

    protected String txnCurrency;

    protected boolean isDebit;

    protected boolean isCredit;

    protected boolean isBonus;

    protected ReSTMember member;

    protected ReSTTeam team;

    protected long kupId;

    protected String kupName;

    protected ReSTKup kup;

    protected Date txnDate;

    protected float creditBefore;

    protected float creditAfter;

    public ReSTTransaction() {
        super();
    }

    public ReSTTransaction(MemberTransaction coreTransaction) {
        this();
        if (coreTransaction != null) {
            setTxnId(coreTransaction.getTransactionId());
            setTxnUUID(coreTransaction.getId());
            setTxnStatus(coreTransaction.getStatus());
            setTxnStatusCode(coreTransaction.getStatusCode());
            setType(coreTransaction.getType());
            setTxnAmount(coreTransaction.getAmount());
            setTxnCurrency(coreTransaction.getCurrency());
            setTxnLabel(coreTransaction.getLabel());
            setTxnDate(coreTransaction.getDate());
            setCredit(coreTransaction.isCredit());
            setDebit(coreTransaction.isDebit());
            setBonus(coreTransaction.isBonus());
            setMember(new ReSTMember(coreTransaction.getMember()));

            final BigDecimal before = new BigDecimal(
                    coreTransaction.getMemberCreditBefore());
            before.setScale(1, BigDecimal.ROUND_HALF_DOWN);
            setCreditBefore(before.floatValue());

            final BigDecimal after = new BigDecimal(
                    coreTransaction.getMemberCreditAfter());
            after.setScale(1, BigDecimal.ROUND_HALF_DOWN);
            setCreditAfter(after.floatValue());
            
            setKup(null);
            setTeam(null);
        }
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnLabel() {
        return txnLabel;
    }

    public void setTxnLabel(String txnLabel) {
        this.txnLabel = txnLabel;
    }

    public float getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(float txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getTxnCurrency() {
        return txnCurrency;
    }

    public void setTxnCurrency(String txnCurrency) {
        this.txnCurrency = txnCurrency;
    }

    public boolean isDebit() {
        return isDebit;
    }

    public void setDebit(boolean isDebit) {
        this.isDebit = isDebit;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean isCredit) {
        this.isCredit = isCredit;
    }

    public boolean isBonus() {
        return isBonus;
    }

    public void setBonus(boolean isBonus) {
        this.isBonus = isBonus;
    }

    public ReSTMember getMember() {
        return member;
    }

    public void setMember(ReSTMember member) {
        this.member = member;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ReSTTeam getTeam() {
        return team;
    }

    public void setTeam(ReSTTeam team) {
        this.team = team;
    }

    public ReSTKup getKup() {
        return kup;
    }

    public void setKup(ReSTKup kup) {
        this.kup = kup;
    }

    public Date getTxnDate() {
        if (txnDate != null) {
            return (Date) txnDate.clone();
        }
        return null;
    }

    public void setTxnDate(Date txnDate) {
        if (txnDate != null) {
            this.txnDate = (Date) txnDate.clone();
        } else {
            this.txnDate = null;
        }
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnStatusCode() {
        return txnStatusCode;
    }

    public void setTxnStatusCode(String txnStatusCode) {
        this.txnStatusCode = txnStatusCode;
    }

    public long getTxnUUID() {
        return txnUUID;
    }

    public void setTxnUUID(long txnUUID) {
        this.txnUUID = txnUUID;
    }

    public long getKupId() {
        return kupId;
    }

    public void setKupId(long kupId) {
        this.kupId = kupId;
    }

    public String getKupName() {
        return kupName;
    }

    public void setKupName(String kupName) {
        this.kupName = kupName;
    }

    public float getCreditBefore() {
        return creditBefore;
    }

    public void setCreditBefore(float creditBefore) {
        this.creditBefore = creditBefore;
    }

    public float getCreditAfter() {
        return creditAfter;
    }

    public void setCreditAfter(float creditAfter) {
        this.creditAfter = creditAfter;
    }

}
