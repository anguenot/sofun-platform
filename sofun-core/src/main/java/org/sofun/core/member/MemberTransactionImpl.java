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

import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionStatus;

/**
 * Member originated transaction implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "members_credit_transactions")
public class MemberTransactionImpl implements MemberTransaction {

    private static final long serialVersionUID = -1226978276382882310L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    protected long id;

    @Column(name = "txn_id", nullable = false)
    protected String transactionId;

    @Column(name = "txn_auth")
    protected String authorization;

    @Column(name = "txn_status", nullable = false)
    protected String status = MemberTransactionStatus.UNKNOWN;

    @Column(name = "txn_status_code", nullable = false)
    protected String statusCode = "-1";

    @Column(name = "txn_date", nullable = false)
    protected Date date;

    @Column(name = "txn_amount", nullable = false)
    protected float amount;

    @Column(name = "member_credit_before", nullable = false, columnDefinition = "float default 0")
    protected float memberCreditBefore;

    @Column(name = "member_credit_after", nullable = false, columnDefinition = "float default 0")
    protected float memberCreditAfter;

    @Column(name = "txn_currency", nullable = false)
    protected String currency;

    @Column(name = "txn_credit", nullable = false)
    protected boolean credit = false;

    @Column(name = "txn_debit", nullable = false)
    protected boolean debit = false;

    @Column(name = "txn_type", nullable = false)
    protected String type;

    @Column(name = "txn_bonus", nullable = false)
    protected boolean bonus = false;

    @Column(name = "label", nullable = false)
    protected String label;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    protected Member member;

    @Column(name = "ack", nullable = false, columnDefinition = "boolean default false")
    protected boolean ack = false;

    public MemberTransactionImpl() {
        super();
    }

    public MemberTransactionImpl(Date txDate, float txAmount,
            String txCurrency, String txType) {

        this();

        if (txAmount >= 0) {
            setCredit(true);
            setDebit(false);
        } else {
            setCredit(false);
            setDebit(true);
        }

        setBonus(false);
        setAmount(txAmount);
        setDate(txDate);
        setCurrency(txCurrency);
        setType(txType);
        setLabel(txType);

        setTransactionId("0");
        setAuthorization("0");

    }

    public MemberTransactionImpl(String txId, String auth, Date txDate,
            float txAmount, String txCurrency, String txType) {

        this(txDate, txAmount, txCurrency, txType);

        setTransactionId(txId);
        setAuthorization(auth);

    }

    @Override
    public long getId() {
        return id;
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
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String tid) {
        this.transactionId = tid;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public void setMember(Member member) {
        this.member = member;
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
    public boolean isBonus() {
        return bonus;
    }

    @Override
    public void setBonus(boolean bonus) {
        this.bonus = bonus;
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
    public String getAuthorization() {
        return authorization;
    }

    @Override
    public void setAuthorization(String auth) {
        this.authorization = auth;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatusCode(String code) {
        this.statusCode = code;
    }

    @Override
    public boolean isAck() {
        return ack;
    }

    @Override
    public void setAck(boolean ack) {
        this.ack = ack;
    }

    @Override
    public float getMemberCreditBefore() {
        return memberCreditBefore;
    }

    @Override
    public void setMemberCreditBefore(float memberCreditBefore) {
        this.memberCreditBefore = memberCreditBefore;
    }

    @Override
    public float getMemberCreditAfter() {
        return memberCreditAfter;
    }

    @Override
    public void setMemberCreditAfter(float memberCreditAfter) {
        this.memberCreditAfter = memberCreditAfter;
    }

}
