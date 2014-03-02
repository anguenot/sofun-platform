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

package org.sofun.core.team;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.api.banking.CurrencyType;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.member.MemberTransactionImpl;

/**
 * Team points timer.
 * 
 * <p>
 * Clock triggering team ranking's update.
 * </p>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class WireThemAll {

    private static final Log log = LogFactory.getLog(WireThemAll.class);

    private boolean available = true;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService members;

    @Timeout
    // @Schedule(minute = "*/2", hour = "*", persistent = false)
    @Lock(LockType.READ)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            List<Member> gambling_members = members.getGamblingMembers();
            for (Member member : gambling_members) {
                float transferrable = members.getTransferableAmountFor(member);
                if (transferrable > 0.1) {
                    SecureRandom randomGenerator = new SecureRandom();
                    MemberTransaction txn = new MemberTransactionImpl(
                            new Date(), transferrable, CurrencyType.EURO,
                            MemberTransactionType.WIRE_DEBIT);
                    txn.setLabel(MemberTransactionType.WIRE_DEBIT);
                    txn.setDebit(true);
                    txn.setCredit(false);
                    txn.setTransactionId(String.valueOf(randomGenerator
                            .nextInt(1000000000)));
                    member.addTransaction(txn);
                    txn.setMember(member);
                    log.info("Wiring amount=" + transferrable
                            + " for member with email=" + member.getEmail());
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
            log.error(t.getMessage());
        } finally {
            available = true;
        }
    }
}
