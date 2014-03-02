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

package org.sofun.core.kup.bet;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.bet.KupMemberBet;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.kup.KupImpl;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.member.MemberTransactionImpl;

/**
 * KupMemberBet implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Entity
@Table(name = "kups_members_bets")
public class KupMemberBetImpl implements KupMemberBet {

    private static final long serialVersionUID = 3653811458928864503L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "integer")
    protected long id;

    @Column(name = "effective_date")
    protected Date effectiveDate;

    @ManyToOne(targetEntity = KupImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "kup_id")
    protected Kup kup;

    @OneToOne(targetEntity = MemberTransactionImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id")
    protected MemberTransaction transaction;

    @ManyToOne(targetEntity = MemberImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    protected Member member;

    public KupMemberBetImpl() {
        super();
    }

    public KupMemberBetImpl(Member member, Kup kup, MemberTransaction txn) {
        super();
        setMember(member);
        setKup(kup);
        setMemberTransaction(txn);
    }

    @Override
    public Kup getKup() {
        return kup;
    }

    @Override
    public void setKup(Kup kup) {
        this.kup = kup;
    }

    @Override
    public MemberTransaction getMemberTransaction() {
        return transaction;
    }

    @Override
    public void setMemberTransaction(MemberTransaction transaction) {
        this.transaction = transaction;
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
    public Date getEffectiveDate() {
        if (effectiveDate != null) {
            return (Date) effectiveDate.clone();
        }
        return null;
    }

    @Override
    public void setEffectiveDate(Date effectiveDate) {
        if (effectiveDate != null) {
            this.effectiveDate = (Date) effectiveDate.clone();
        }
        this.effectiveDate = null;
    }

}
