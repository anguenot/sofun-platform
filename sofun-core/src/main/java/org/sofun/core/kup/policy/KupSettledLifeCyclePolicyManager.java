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

package org.sofun.core.kup.policy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.banking.SofunTransaction;
import org.sofun.core.api.banking.SofunTransactionType;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.kup.bet.KupMemberBet;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionStatus;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.prediction.PredictionService;
import org.sofun.core.banking.SofunTransactionImpl;
import org.sofun.core.kup.bet.KupMemberBetImpl;
import org.sofun.core.member.MemberTransactionImpl;

/**
 * Kup Policy Timer.
 * 
 * <p/>
 * 
 * triggering Kup life cycle checks on regular basis out of process for active
 * Kups only.
 * 
 * <p/>
 * 
 * This singleton is responsible to perform Kups status transitions. This is not
 * responsible for any kind of computations whatsever.
 * 
 * <p/>
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Singleton
public class KupSettledLifeCyclePolicyManager extends
        AbstractKupLifeCycleManager {

    private static final Log log = LogFactory
            .getLog(KupSettledLifeCyclePolicyManager.class);

    @EJB(beanName = "KupServiceImpl", beanInterface = KupServiceLocal.class)
    private KupService kups;

    @EJB(beanName = "PredictionServiceImpl", beanInterface = PredictionServiceLocal.class)
    private PredictionService predictions;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    private transient EntityManager em;

    @Override
    public KupService getKups() {
        return kups;
    }

    @Override
    public PredictionService getPredictions() {
        return predictions;
    }

    /**
     * Kup life cycle management.
     * 
     * @throws Exception
     */
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void check() throws Exception {

        if (!available) {
            return;
        } else {
            available = false;
        }

        try {

            log.debug("Checking Kups and apply policy...");

            //
            // Deal with Settled Kups
            //

            final byte[] settledStatus = new byte[1];
            settledStatus[0] = KupStatus.SETTLED;
            String[] types = new String[] { KupType.GAMBLING_FR };

            ListIterator<Kup> kupsIter = kups.getKupsByStatus(settledStatus,
                    types).listIterator();
            while (kupsIter.hasNext()) {

                Kup kup = kupsIter.next();
                // Ensure no changes have been made by another transaction in
                // the mean time.
                em.refresh(kup);
                if (kup.getStatus() != KupStatus.SETTLED) {
                    continue;
                }

                Map<Integer, Float> repartition = kups
                        .getWinningsRepartitionRulesFor(kup);
                final int numberOfWinners = repartition.size();

                if (kup.getParticipants().size() < numberOfWinners) {
                    kups.cancelKup(kup);
                    log.info("Kup with id=" + String.valueOf(kup.getId())
                            + " has been cancelled because participants are "
                            + "below the amount of winners.");
                    continue;
                }

                KupRankingTable table = kup.getRankingTable();
                List<MemberRankingTableEntry> entries = new ArrayList<MemberRankingTableEntry>(
                        table.getEntries());
                entries = entries.subList(0, numberOfWinners);

                // Cancel if winnings < bet for one of the winners.
                boolean cancelled = false;
                for (int i = 0; i < numberOfWinners - 1; i++) {

                    final float jackpot = kup.getEffectiveJackpot();
                    final float amount = jackpot * repartition.get(i + 1) / 100;

                    if (kup.getStake() > amount) {
                        cancelled = true;
                        break;
                    }

                }

                if (!cancelled) {

                    // Credit winners.

                    final Date now = new Date();

                    final float jackpot = kup.getEffectiveJackpot();
                    if (kup.getJackpot() < kup.getGuaranteedPrice()) {
                        final float house = kup.getGuaranteedPrice()
                                - kup.getJackpot();
                        final BigDecimal houseAmount = new BigDecimal(house);
                        houseAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
                        SofunTransaction txn = new SofunTransactionImpl(kup,
                                now, houseAmount.floatValue(),
                                kup.getStakeCurrency(),
                                SofunTransactionType.TXN_GUARANTEED_PRICE,
                                SofunTransactionType.TXN_GUARANTEED_PRICE);
                        txn.setDebit(true);
                        em.persist(txn);
                        log.info("SOFUN TXN for kup w/ uuid="
                                + txn.getKup().getId() + " type="
                                + txn.getType() + " amount="
                                + houseAmount.floatValue());
                    } else {
                        final float rake = kup.getRakeAmount();
                        if (rake > 0) {
                            final BigDecimal rakeAmount = new BigDecimal(rake);
                            rakeAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            SofunTransaction txn = new SofunTransactionImpl(
                                    kup, now, rakeAmount.floatValue(),
                                    kup.getStakeCurrency(),
                                    SofunTransactionType.TXN_RACK,
                                    SofunTransactionType.TXN_RACK);
                            txn.setCredit(true);
                            em.persist(txn);
                            log.info("SOFUN TXN for kup w/ uuid="
                                    + txn.getKup().getId() + " type="
                                    + txn.getType() + " amount="
                                    + rakeAmount.floatValue());
                        }
                    }

                    int i = 0;
                    for (MemberRankingTableEntry entry : entries) {

                        final float amount = jackpot * repartition.get(i + 1)
                                / 100;
                        Member member = entry.getMember();

                        // Round down (inferior cent) as specified in rules.
                        BigDecimal wiredAmount = new BigDecimal(amount);
                        wiredAmount = wiredAmount.setScale(2,
                                BigDecimal.ROUND_HALF_DOWN);

                        MemberTransaction txn = new MemberTransactionImpl(now,
                                wiredAmount.floatValue(),
                                kup.getStakeCurrency(),
                                MemberTransactionType.BET_CREDIT);
                        txn.setLabel(MemberTransactionType.BET_CREDIT);
                        txn.setCredit(true);
                        txn.setStatusCode("00000");
                        txn.setStatus(MemberTransactionStatus.INTERNAL);
                        txn.setMemberCreditBefore(member.getMemberCredit()
                                .getCredit());
                        txn.setMemberCreditAfter(member.getMemberCredit()
                                .getCredit() + wiredAmount.floatValue());
                        member.addTransaction(txn);
                        txn.setMember(member);

                        log.info("Member with email=" + member.getEmail()
                                + " finished " + String.valueOf(i + 1)
                                + " out of " + kup.getParticipants().size()
                                + " in kup w/ uuid=" + kup.getId()
                                + " txn of type=" + txn.getType()
                                + " with amount of: "
                                + wiredAmount.floatValue());

                        // Record
                        KupMemberBet bet = new KupMemberBetImpl(member, kup,
                                txn);
                        bet.setEffectiveDate(now);
                        em.persist(bet);

                        entry.setWinnings(wiredAmount.floatValue());

                        i++;
                    }

                    log.info("Kup with id=" + String.valueOf(kup.getId())
                            + " has now status=" + KupStatus.PAID_OUT);
                    kup.setStatus(KupStatus.PAID_OUT);
                } else {
                    kups.cancelKup(kup);
                    log.info("Kup with id=" + String.valueOf(kup.getId())
                            + " has been cancelled because the winnings are "
                            + "below the stake for some of the winners.");

                }

            }
        } catch (Throwable t) {
            log.error(t.getMessage());
            t.printStackTrace();
        } finally {
            available = true;
        }

        // Cancel status in exceptional situations will be handled manually per
        // administrator request.
        // @see KupeService.cancelKup()

    }

}
