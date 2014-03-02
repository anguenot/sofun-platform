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

package org.sofun.core.kup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.community.table.MemberRankingTableEntry;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupRankingTable;
import org.sofun.core.api.kup.KupRoles;
import org.sofun.core.api.kup.KupSearchResults;
import org.sofun.core.api.kup.KupService;
import org.sofun.core.api.kup.KupStatus;
import org.sofun.core.api.kup.KupType;
import org.sofun.core.api.kup.bet.KupMemberBet;
import org.sofun.core.api.kup.bet.KupWinningsRepartitionRuleType;
import org.sofun.core.api.kup.prediction.KupPredictionPointsRule;
import org.sofun.core.api.local.KupServiceLocal;
import org.sofun.core.api.local.NotificationServiceLocal;
import org.sofun.core.api.local.PredictionServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberCredit;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionStatus;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.notification.NotificationService;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.prediction.PredictionService;
import org.sofun.core.api.question.Question;
import org.sofun.core.api.question.QuestionKupTiebreaker;
import org.sofun.core.api.remote.KupServiceRemote;
import org.sofun.core.api.sport.SportContestant;
import org.sofun.core.api.sport.tournament.Tournament;
import org.sofun.core.api.sport.tournament.TournamentGame;
import org.sofun.core.api.sport.tournament.TournamentGameStatus;
import org.sofun.core.api.sport.tournament.TournamentRound;
import org.sofun.core.api.sport.tournament.TournamentRoundStatus;
import org.sofun.core.api.sport.tournament.TournamentSeason;
import org.sofun.core.api.sport.tournament.TournamentSeasonStatus;
import org.sofun.core.api.sport.tournament.TournamentStage;
import org.sofun.core.api.sport.tournament.TournamentStageStatus;
import org.sofun.core.api.team.Team;
import org.sofun.core.api.team.TeamPrivacy;
import org.sofun.core.api.team.TeamRoles;
import org.sofun.core.kup.bet.KupMemberBetImpl;
import org.sofun.core.kup.points.rule.GenericRule;
import org.sofun.core.kup.table.KupRankingTableImpl;
import org.sofun.core.member.MemberTransactionImpl;

/**
 * Kup Service Implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(KupServiceLocal.class)
@Remote(KupServiceRemote.class)
public class KupServiceImpl implements KupService {

    private static final long serialVersionUID = 1008880715501446028L;

    private static final Log log = LogFactory.getLog(KupServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    private transient EntityManager em;

    @EJB(beanName = "PredictionServiceImpl", beanInterface = PredictionServiceLocal.class)
    private PredictionService predictions;

    @EJB(beanName = "NotificationServiceImpl", beanInterface = NotificationServiceLocal.class)
    private NotificationService notifications;

    @EJB(beanName = "MemberServiceImpl", beanInterface = MemberServiceLocal.class)
    private MemberService members;

    protected static final Map<String, KupPredictionPointsRule> rulePlugins = new HashMap<String, KupPredictionPointsRule>();

    public KupServiceImpl() {
        super();
    }

    public KupServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    protected Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public KupSearchResults search(Map<String, String> params)
            throws CoreException {

        List<Kup> results = new ArrayList<Kup>();
        long count = 0;
        List<KupImpl> kups = null;

        int offset = 0;
        final String offsetStr = params.get("offset");
        if (offsetStr != null) {
            offset = Integer.valueOf(offsetStr);
        }
        int batchSize = 10; // default batch size
        final String batchSizeStr = params.get("batchSize");
        if (batchSizeStr != null) {
            batchSize = Integer.valueOf(batchSizeStr);
        }

        String queryStr = "";
        TypedQuery<KupImpl> query = null;
        Query countQuery = null;

        List<Byte> kupStatus = new ArrayList<Byte>();
        final String kupStatusParam = params.get("status");
        if (kupStatusParam == null) {
            kupStatus = null;
        } else if (kupStatusParam.equals("ALL")) {
            kupStatus = null;
        } else if (kupStatusParam.equals("OPENED")) {
            kupStatus.add(Integer.valueOf(1).byteValue()); // CREATED
        } else if (kupStatusParam.equals("ON_GOING")) {
            kupStatus.add(Integer.valueOf(2).byteValue()); // ON_GOING
        } else if (kupStatusParam.equals("ALL_OPENED")) {
            kupStatus.add(Integer.valueOf(1).byteValue()); // CREATED
            kupStatus.add(Integer.valueOf(2).byteValue()); // ON_GOING
        } else if (kupStatusParam.equals("ALL_CLOSED")) {
            kupStatus.add(Integer.valueOf(3).byteValue()); // CLOSED
            kupStatus.add(Integer.valueOf(4).byteValue()); // SETTLED
            kupStatus.add(Integer.valueOf(5).byteValue()); // PAID OUT
            kupStatus.add(Integer.valueOf(4).byteValue()); // SETTLED
            kupStatus.add(Integer.valueOf(-1).byteValue()); // CANCELED
        }

        final String email = params.get("email");
        if (email != null) {

            Member member = members.getMember(email);
            if (member != null) {

                String template = params.get("template");
                if (template != null && template.equals("all")) {

                    queryStr = "SELECT k FROM " + KupImpl.class.getSimpleName()
                            + " k JOIN k.members m WHERE m.id=:member_id";
                    if (kupStatus != null) {
                        queryStr += " AND k.status IN (:status)";
                    }
                    queryStr += " ORDER BY k.created";

                    query = em.createQuery(queryStr, KupImpl.class);
                    countQuery = em.createQuery(
                            "SELECT count(*) "
                                    + queryStr.substring(
                                            queryStr.indexOf("FROM"),
                                            queryStr.indexOf("ORDER BY")),
                            Long.class);
                    query.setParameter("member_id", member.getId());
                    countQuery.setParameter("member_id", member.getId());
                    if (kupStatus != null) {
                        query.setParameter("status", kupStatus);
                        countQuery.setParameter("status", kupStatus);
                    }

                } else {

                    queryStr = "SELECT k FROM "
                            + KupImpl.class.getSimpleName()
                            + " k JOIN k.members m WHERE m.id=:member_id AND k.isTemplate=:isTemplate";
                    if (kupStatus != null && kupStatus.size() > 0) {
                        queryStr += " AND k.status IN (:status)";
                    }
                    queryStr += " ORDER BY k.created";

                    query = em.createQuery(queryStr, KupImpl.class);
                    countQuery = em.createQuery(
                            "SELECT count(*) "
                                    + queryStr.substring(
                                            queryStr.indexOf("FROM"),
                                            queryStr.indexOf("ORDER BY")),
                            Long.class);

                    query.setParameter("member_id", member.getId());
                    countQuery.setParameter("member_id", member.getId());

                    query.setParameter("isTemplate", true);
                    countQuery.setParameter("isTemplate", true);
                    if (kupStatus != null && kupStatus.size() > 0) {
                        query.setParameter("status", kupStatus);
                        countQuery.setParameter("status", kupStatus);
                    }

                }

                // pagination
                query.setFirstResult(offset);
                query.setMaxResults(batchSize);

                kups = query.getResultList();
                count = (Long) countQuery.getSingleResult();
                if (kups != null) {
                    results.addAll(kups);
                }

            }

        } else {

            if (kupStatusParam == null || kupStatusParam.isEmpty()) {
                return null;
            }

            boolean includeRoomKups = false;
            final String withRoomKups = params.get("withRoomKups");
            if (withRoomKups != null && "1".equals(withRoomKups)) {
                includeRoomKups = true;
            }

            if (!includeRoomKups) {
                queryStr = "from " + KupImpl.class.getSimpleName()
                        + " k where k.isTemplate=:isTemplate";
            } else {
                queryStr = "from " + KupImpl.class.getSimpleName()
                        + " k where k.team.privacy IN (:teamPrivacy)";
            }
            if (kupStatus != null) {
                queryStr += " AND k.status IN (:status)";
            }
            final String name = params.get("name");
            if (name != null && !"".equals(name)) {
                queryStr += " AND k.name IN (:name)";
            }

            boolean isTemplateParam = true;
            final String isTemplate = params.get("isTemplate");
            if (isTemplate != null && !"".equals(isTemplate)) {
                if (!isTemplate.equals("1")) {
                    isTemplateParam = false;
                }
            }

            final String kupStakeParam = params.get("stake");
            if (kupStakeParam != null) {
                if ("FREE_FREEROLL".equals(kupStakeParam)) {
                    queryStr += " AND k.stake=0";
                } else if ("FREEROLL".equals(kupStakeParam)) {
                    queryStr += " AND k.stake=0 AND k.type='GAMBLING_FR'";
                } else if ("GAMBLING".equals(kupStakeParam)) {
                    queryStr += " AND k.type='GAMBLING_FR' AND k.stake>0";
                } else if ("FREE".equals(kupStakeParam)) {
                    queryStr += " AND k.type='FREE'";
                } else if ("ALL_GAMBLING".equals(kupStakeParam)) {
                    queryStr += " AND k.type='GAMBLING_FR'";
                } else if (kupStakeParam.isEmpty()) {
                    return null;
                }
            }

            final String kupSportsParam = params.get("sports");
            List<String> sportsNameParams = null;
            if (kupSportsParam != null) {
                if (kupSportsParam.isEmpty()) {
                    return null;
                }
                List<String> sparams = Arrays.asList(kupSportsParam.split("#"));
                if (!sparams.contains("ALL")) {
                    queryStr += " AND UPPER(k.sport.name) IN (:sports)";
                    sportsNameParams = sparams;
                }

            }

            // Remove kups where player is a participant
            final String removeValidatedFor = params.get("removeValidatedfor");
            Member removeValidatedMember = null;
            if (removeValidatedFor != null && !removeValidatedFor.isEmpty()) {
                queryStr += " AND :member NOT MEMBER OF k.participants";
                removeValidatedMember = members.getMember(removeValidatedFor);
            }

            // Do not show up with no participants
            if (kupStatusParam != null && "ALL_CLOSED".equals(kupStatusParam)) {
                queryStr += " AND k.nbParticipants > 0";
            }

            // Sorting
            final String sortParams = params.get("sort");
            if (sortParams != null) {
                if (sortParams.isEmpty()) {
                    return null;
                }
                queryStr += " ORDER BY";
                List<String> sparams = Arrays.asList(sortParams.split("#"));
                boolean initialized = false;
                if (sparams.contains("START_DATE")) {
                    if ("ALL_CLOSED".equals(kupStatusParam)) {
                        queryStr += " k.endDate DESC";
                    } else {
                        queryStr += " k.status ASC, k.startDate ASC";
                    }
                    initialized = true;
                }
                if (sparams.contains("JACKPOT")) {
                    if (initialized) {
                        queryStr += " ,";
                    }
                    queryStr += " k.guaranteedPrice DESC";
                }
                if (sparams.contains("PARTICIPANTS")) {
                    if (initialized) {
                        queryStr += " ,";
                    }
                    queryStr += " k.nbParticipants DESC";
                }
                if (sparams.contains("KUP_DURATION")) {
                    if (initialized) {
                        queryStr += " ,";
                    }
                    queryStr += " k.duration DESC";
                }
            } else {
                if (kupStatusParam != null
                        && "ALL_CLOSED".equals(kupStatusParam)) {
                    queryStr += " ORDER BY k.endDate DESC";
                } else {
                    queryStr += " ORDER BY k.status ASC, k.startDate ASC";
                }
            }

            query = em.createQuery(queryStr, KupImpl.class);
            countQuery = em.createQuery(
                    "SELECT count(*) "
                            + queryStr.substring(0,
                                    queryStr.indexOf("ORDER BY")), Long.class);

            if (!includeRoomKups) {
                query.setParameter("isTemplate", isTemplateParam);
                countQuery.setParameter("isTemplate", isTemplateParam);
            } else {
                String[] teamPrivacy = new String[] { TeamPrivacy.PUBLIC,
                        TeamPrivacy.PUBLIC_GAMBLING_FR };
                query.setParameter("teamPrivacy", Arrays.asList(teamPrivacy));
                countQuery.setParameter("teamPrivacy",
                        Arrays.asList(teamPrivacy));
            }
            if (kupStatus != null) {
                query.setParameter("status", kupStatus);
                countQuery.setParameter("status", kupStatus);
            }
            if (sportsNameParams != null) {
                query.setParameter("sports", sportsNameParams);
                countQuery.setParameter("sports", sportsNameParams);
            }
            if (name != null && !"".equals(name)) {
                final String[] names = name.split(",");
                query.setParameter("name", Arrays.asList(names));
                countQuery.setParameter("name", Arrays.asList(names));
            }
            if (removeValidatedFor != null && !removeValidatedFor.isEmpty()) {
                query.setParameter("member", removeValidatedMember);
                countQuery.setParameter("member", removeValidatedMember);
            }

            // pagination
            query.setFirstResult(offset);
            query.setMaxResults(batchSize);

            kups = query.getResultList();
            count = (Long) countQuery.getSingleResult();
            if (kups != null) {
                results.addAll(kups);
            }

        }

        return new KupSearchResultsImpl(offset, batchSize, count, results);

    }

    @Override
    public Kup getKupById(long kupId) {

        String queryStr = "from " + KupImpl.class.getSimpleName()
                + " k where k.id=:kupId";
        Query query = createQuery(queryStr);
        query.setParameter("kupId", kupId);

        try {
            return (Kup) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Map<Integer, Float> getWinningsRepartitionRulesFor(Kup kup) {
        final Map<Integer, Float> rule = new HashMap<Integer, Float>();

        final byte type = kup.getRepartitionRuleType();
        if (type == KupWinningsRepartitionRuleType.TYPE_1) {

            // Type 1: One (1) winner. The {@link Member} ranking #1 in the
            // corresponding {@link KupRankingtable} gets 100% of {@link Kup}
            // jackpot.
            rule.put(1, 100f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_2) {

            // Type 2: Two (2) winners. The 2 {@link Member} ranking #1 and #2
            // in the corresponding {@link KupRankingtable} ge 50% each of the
            // {@link Kup} jackpot.

            rule.put(1, 70f);
            rule.put(2, 30f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_3) {

            // Type 3: Three (3) winners. The 3 {@link Member} ranking #1, #2
            // and #3 in the corresponding {@link KupRankingtable} get
            // respectively 70%,
            // 20% and 10% of the {@link Kup} jackpot.

            rule.put(1, 50f);
            rule.put(2, 30f);
            rule.put(3, 20f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_4) {

            // Type4: Ten (10) winners. The 10 {@link Member} ranking from #1 to
            // #10 in the corresponding {@link KupRankingTable} get the
            // following: 25%, 20%, 15%, 10%, 5%, 5%, 5%, 5%, 5%, 5%

            rule.put(1, 25f);
            rule.put(2, 20f);
            rule.put(3, 15f);
            rule.put(4, 10f);
            rule.put(5, 5f);
            rule.put(6, 5f);
            rule.put(7, 5f);
            rule.put(8, 5f);
            rule.put(9, 5f);
            rule.put(10, 5f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_5) {

            //
            // Type5: Thirteen (13) winners. The 13 {@link Member} ranking from
            // #1 to #13 in the corresponding {@link KupRankingTable} get the
            // following: 23%, 15%, 13%, 10%, 7%, 4%, 4%, 4%, 4%, 4%, 4%, 4%,
            // 4%,
            //

            rule.put(1, 23f);
            rule.put(2, 15f);
            rule.put(3, 13f);
            rule.put(4, 10f);
            rule.put(5, 7f);
            rule.put(6, 4f);
            rule.put(7, 4f);
            rule.put(8, 4f);
            rule.put(9, 4f);
            rule.put(10, 4f);
            rule.put(11, 4f);
            rule.put(12, 4f);
            rule.put(13, 4f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_6) {

            //
            // Type6: Twenty (20) winners. The 20 {@link Member} ranking from #1
            // to #20
            // in the corresponding {@link KupRankingTable} get the following:
            // 20%, 12%,
            // 10%, 8%, 5%, 5%, 5%, 5%, 5%, 5%, 2%, 2%, 2%, 2%, 2%, 2%, 2%, 2%,
            // 2%, 2%
            //

            rule.put(1, 20f);
            rule.put(2, 12f);
            rule.put(3, 10f);
            rule.put(4, 8f);
            rule.put(5, 5f);
            rule.put(6, 5f);
            rule.put(7, 5f);
            rule.put(8, 5f);
            rule.put(9, 5f);
            rule.put(10, 5f);
            rule.put(11, 2f);
            rule.put(12, 2f);
            rule.put(13, 2f);
            rule.put(14, 2f);
            rule.put(15, 2f);
            rule.put(16, 2f);
            rule.put(17, 2f);
            rule.put(18, 2f);
            rule.put(19, 2f);
            rule.put(20, 2f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_55) {

            //
            // Type55: Five (5) winners. The 5 {@link Member} ranking from #1 to
            // #5 in
            // the corresponding {@link KupRankingTable} get the following: 30%,
            // 25%,
            // 20%, 15%, 10%.
            //

            rule.put(1, 30f);
            rule.put(2, 25f);
            rule.put(3, 20f);
            rule.put(4, 15f);
            rule.put(5, 10f);

        } else if (type == KupWinningsRepartitionRuleType.TYPE_30) {

            //
            // Type30: Thirty (30) winners. The 30 {@link Member} ranking from
            // #1 to #30
            // in the corresponding {@link KupRankingTable} get the following:
            // <ul>
            // <li>1er : 15%</li>
            // <li>2e : 10 %</li>
            // <li>3e : 7%</li>
            // <li>4e up to 10e : 4%</li>
            // <li>11e up to 30e : 2%</li>
            // </ul>
            //

            rule.put(1, 15f);
            rule.put(2, 10f);
            rule.put(3, 7f);
            rule.put(4, 4f);
            rule.put(5, 4f);
            rule.put(6, 4f);
            rule.put(7, 4f);
            rule.put(8, 4f);
            rule.put(9, 4f);
            rule.put(10, 4f);
            rule.put(11, 2f);
            rule.put(12, 2f);
            rule.put(13, 2f);
            rule.put(14, 2f);
            rule.put(15, 2f);
            rule.put(16, 2f);
            rule.put(17, 2f);
            rule.put(18, 2f);
            rule.put(19, 2f);
            rule.put(20, 2f);
            rule.put(21, 5f);
            rule.put(22, 2f);
            rule.put(23, 2f);
            rule.put(24, 2f);
            rule.put(25, 2f);
            rule.put(26, 2f);
            rule.put(27, 2f);
            rule.put(28, 2f);
            rule.put(29, 2f);
            rule.put(30, 2f);

        } else {
            log.warn("Kup Winnings Type=" + String.valueOf(type)
                    + " does not exist.");
        }

        return rule;
    }

    @Override
    public void placeKupBet(Member member, Kup kup) throws CoreException {

        //
        // Verify that the member account status.
        //

        final String memberStatus = member.getAccountStatus();
        if (!MemberAccountStatus.getActiveStatus().contains(memberStatus)) {
            throw new CoreException("Member account is not active.");
        }

        //
        // Bets not allowed in Free Kups.
        //

        final String kupType = kup.getType();
        if (KupType.FREE.equals(kupType)) {
            throw new CoreException("Bets not allowed in free kups.");
        }

        //
        // Verify that the member does not have a bet for this kup already.
        //

        if (hasBet(member, kup)) {
            throw new CoreException("Member already placed a bet on this Kup.");
        }

        //
        // Verify the user already has prediction
        //

        if (!hasPrediction(member, kup)) {
            throw new CoreException(
                    "Member does not have any predictions in this Kup yet.");
        }

        //
        // Verify that member belongs to the team in which the kup is setup
        //

        /*
         * Team team = kup.getTeam(); if
         * (!member.getMemberTeams().contains(team)) { throw new
         * CoreException("Member does not belong to team."); }
         */

        //
        // Check if kup is opened but not started. This is a Sofun Gaming
        // policy.
        //

        if (kup.getStatus() != KupStatus.OPENED
                && kup.getStatus() != KupStatus.ON_GOING) {
            throw new CoreException("Kup is not opened for bets.");
        }

        if (KupType.GAMBLING_FR.equals(kupType)) {

            if (!MemberAccountStatus.getActiveGamblingFrStatus().contains(
                    memberStatus)) {
                throw new CoreException("Member not allowed to gamble.");
            }

            final float stake = kup.getStake();
            MemberCredit credit = member.getMemberCredit();
            if (stake > 0 && credit.getCredit() < stake) {
                throw new CoreException("Credit too low to place a bet.");
            }

            // Create transaction
            final Date now = new Date();
            MemberTransaction txn = new MemberTransactionImpl("0", "0", now,
                    kup.getStake(), kup.getStakeCurrency(),
                    MemberTransactionType.BET_DEBIT);
            txn.setDebit(true);
            txn.setStatusCode("00000");
            txn.setStatus(MemberTransactionStatus.INTERNAL);
            member.addTransaction(txn);
            txn.setMember(member);

            // Record
            KupMemberBet bet = new KupMemberBetImpl(member, kup, txn);
            bet.setEffectiveDate(now);
            em.persist(bet);

            if (!kup.getMembers().contains(member)) {
                kup.addMember(member);
            }

            // Validate
            if (hasPrediction(member, kup)) {
                kup.addParticipant(member);
                bet.setEffectiveDate(txn.getDate());
            }

            // Increase Jackpot
            kup.setJackpot(kup.getJackpot() + kup.getStake());

        } else {
            throw new CoreException("Kup type not recognized. Cancelling...");
        }

    }

    @Override
    public boolean hasPrediction(Member member, Kup kup) {
        return predictions.hasPredictions(member, kup);
    }

    @Override
    public boolean hasBet(Member member, Kup kup) throws CoreException {

        if (member == null || kup == null) {
            return false;
        }

        String queryStr = "from " + KupMemberBetImpl.class.getSimpleName()
                + " m where m.member.email=:email AND m.kup.id=:kup_id";

        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());
        query.setParameter("kup_id", kup.getId());

        @SuppressWarnings("unchecked")
        List<Prediction> results = query.getResultList();
        return results.size() > 0 ? true : false;
    }

    protected void checkForPredicton(Member member, Kup kup)
            throws CoreException {

        //
        // Verify that the member account status.
        //

        final String memberStatus = member.getAccountStatus();
        if (!MemberAccountStatus.getActiveStatus().contains(memberStatus)) {
            throw new CoreException("Member account is not active.");
        }

        //
        // Verify that member belongs to the team in which the kup is setup
        //

        Team team = kup.getTeam();
        if (!team.getMembers().contains(member)) {
            throw new CoreException("Member does not belong to team.");
        }

        //
        // Check if kup is active.
        //

        if (!(kup.getStatus() == KupStatus.OPENED || kup.getStatus() == KupStatus.ON_GOING)) {
            throw new CoreException("Kup is not opened for predictions.");
        }
    }

    @Override
    public void addPrediction(String type, Member member, Kup kup,
            TournamentGame game, List<SportContestant> contestants)
            throws CoreException {

        if (predictions.isPredictionAllowedOn(game)) {
            predictions.createPredictionFor(member, game, type, contestants,
                    kup);
        } else {
            throw new CoreException("Prediction not allowed on game with uuid="
                    + game.getUUID());
        }

    }

    @Override
    public void addPredictionScore(String type, Member member, Kup kup,
            TournamentGame game, List<Integer> score) throws CoreException {

        if (predictions.isPredictionAllowedOn(game)) {
            predictions
                    .createPredictionScoreFor(member, game, type, score, kup);
        } else {
            throw new CoreException("Prediction not allowed on game with uuid="
                    + game.getUUID());
        }

    }

    @Override
    public void addGameQuestionPrediction(String type, Member member, Kup kup,
            TournamentGame game, Question question, String choice)
            throws CoreException {

        if (predictions.isPredictionAllowedOn(game)) {
            predictions.createQuestionPredictionFor(member, game, type,
                    question, kup, choice);
        } else {
            throw new CoreException("Prediction not allowed on game with uuid="
                    + game.getUUID());
        }

    }

    @Override
    public void addPrediction(String type, Member member, Kup kup,
            TournamentRound round, List<SportContestant> contestants)
            throws CoreException {

        if (predictions.isPredictionAllowedOn(round)) {
            predictions.createPredictionFor(member, round, type, contestants,
                    kup);
        } else {
            throw new CoreException("Prediction not allowed on game with uuid="
                    + round.getUUID());
        }

    }

    @Override
    public void addPrediction(String type, Member member, Kup kup,
            TournamentStage stage, List<SportContestant> contestants)
            throws CoreException {

        if (predictions.isPredictionAllowedOn(stage)) {
            predictions.createPredictionFor(member, stage, type, contestants,
                    kup);
        } else {
            throw new CoreException("Prediction not allowed on game with uuid="
                    + stage.getUUID());
        }

    }

    @Override
    public void addPrediction(String type, Member member, Kup kup,
            TournamentSeason season, List<SportContestant> contestants)
            throws CoreException {

        if (predictions.isPredictionAllowedOn(season)) {
            predictions.createPredictionFor(member, season, type, contestants,
                    kup);
        } else {
            throw new CoreException("Prediction not allowed on game with uuid="
                    + season.getUUID());
        }

    }

    private static final List<Byte> toByteList(byte[] a) {
        final List<Byte> r = new ArrayList<Byte>();
        for (int i = 0; i < a.length; i++) {
            r.add(Byte.valueOf(a[i]));
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Kup> getKupsByStatus(byte[] status, String[] types)
            throws CoreException {

        final List<Byte> statusParm = toByteList(status);

        String queryStr = "from " + KupImpl.class.getSimpleName()
                + " k where k.status IN (:status)";

        if (types != null) {
            queryStr += " AND k.type IN (:types)";
        }

        Query query = createQuery(queryStr);
        query.setParameter("status", statusParm);
        if (types != null) {
            query.setParameter("types", Arrays.asList(types));
        }

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<Kup>();
        }

    }

    @Override
    public List<Kup> getActiveKups() throws CoreException {
        final byte[] status = KupStatus.getActiveStatus();
        return getKupsByStatus(status, null);
    }

    @Override
    public int getPointsPredictionFor(Kup kup, Prediction prediction)
            throws CoreException {
        // final KupPredictionPointsRule rule =
        // rulePlugins.get(kup.getMetaType());
        // FIXME use kup meta type to deduce points computing rules.
        KupPredictionPointsRule rule = new GenericRule();
        return rule.getPointsFor(kup, prediction, this);
    }

    @Override
    public void cancelKup(Kup kup) {

        final Date now = new Date();

        kup.setStatus(KupStatus.CANCELED);

        final float stake = kup.getStake();
        // Free roll nothing to reimbursed
        if (stake == 0) {
            return;
        }

        for (Member member : kup.getParticipants()) {
            MemberTransaction txn = new MemberTransactionImpl(now, stake,
                    kup.getStakeCurrency(), MemberTransactionType.BET_CREDIT);
            txn.setLabel(MemberTransactionType.BET_CREDIT);
            txn.setCredit(true);
            txn.setStatusCode("00000");
            txn.setStatus(MemberTransactionStatus.INTERNAL);
            member.addTransaction(txn);
            txn.setMember(member);
        }

    }

    @Override
    public Set<String> getCredentials(Kup kup, Member member) {
        Set<String> credentials = new HashSet<String>();

        if (kup.isAdmin(member)) {
            credentials.add(KupRoles.ADMINISTRATOR);
        }

        if (kup.isMember(member)) {
            credentials.add(KupRoles.MEMBER);
        }

        if (kup.isParticipant(member)) {
            credentials.add(KupRoles.PARTICIPANT);
        }

        if (credentials.size() == 0) {
            credentials.add(TeamRoles.ANONYMOUS);
        }

        return credentials;
    }

    @Override
    public Kup createKupFromMeta(Kup kup, Team team, float stake,
            byte repartitionType) throws CoreException {

        Kup newKup = new KupImpl(kup, team, stake, repartitionType);
        team.addKup(newKup);
        em.persist(newKup);
        return newKup;
    }

    @Override
    public List<Prediction> getPredictionsFor(Member member, Kup kup)
            throws CoreException {
        return predictions.getPredictionsFor(kup, member);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Kup> getKupsByName(String name) {

        String queryStr = "from " + KupImpl.class.getSimpleName()
                + " k where k.name=:name";
        Query query = createQuery(queryStr);
        query.setParameter("name", name);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<Kup>();
        }

    }

    @Override
    public Kup getTemplateFor(Kup kup) {
        if (kup == null) {
            return null;
        }
        if (kup.isTemplate()) {
            return null;
        }
        String queryStr = "from " + KupImpl.class.getSimpleName()
                + " k where k.name=:kupName AND k.isTemplate=:isTemplate";
        Query query = createQuery(queryStr);
        query.setParameter("kupName", kup.getName());
        query.setParameter("isTemplate", true);
        try {
            return (Kup) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public void syncKupsWithTemplate() throws CoreException {
        for (Kup kup : getActiveKups()) {

            // Update start date / end date (depends on events)
            kup.setStartDate(kup.getEffectiveStartDate());
            kup.setEndDate(kup.getEffectiveEndDate());
            // Update Kup search indexes.
            kup.setNbParticipants(kup.getParticipants().size());
            // Update Kup duration search index
            kup.setDuration(kup.computeDuration());

            Kup template = getTemplateFor(kup);
            if (template == null) {
                continue;
            }

            // Set sport
            kup.setSport(template.getSport());
            for (Tournament tournament : template.getSportTournaments()) {
                kup.addSportTournament(tournament);
            }

            log.debug("Syncing Kup w/ id=" + String.valueOf(kup.getId())
                    + " and template w/ name=" + template.getName());

            for (TournamentGame game : template.getBettableGames()) {
                if (TournamentGameStatus.SCHEDULED.equals(game.getGameStatus())
                        && !kup.getBettableGames().contains(game)) {
                    log.info("Adding game with uuid=" + game.getUUID()
                            + " to kup with id=" + kup.getId());
                    kup.addBettableGame(game);
                }
            }
            for (TournamentRound round : template.getBettableRounds()) {
                if (TournamentRoundStatus.SCHEDULED.equals(round.getStatus())
                        && !kup.getBettableRounds().contains(round)) {
                    log.info("Adding round with uuid=" + round.getUUID()
                            + " to kup with id=" + kup.getId());
                    kup.addBettableRound(round);
                }
            }
            for (TournamentStage stage : template.getBettableStages()) {
                if (TournamentStageStatus.SCHEDULED.equals(stage.getStatus())
                        && !kup.getBettableStages().contains(stage)) {
                    log.info("Adding stage with uuid=" + stage.getUUID()
                            + " to kup with id=" + kup.getId());
                    kup.addBettableStage(stage);
                }
            }
            for (TournamentSeason season : template.getBettableTournaments()) {
                if (TournamentSeasonStatus.SCHEDULED.equals(season.getStatus())
                        && !kup.getBettableTournaments().contains(season)) {
                    log.info("Adding season with uuid=" + season.getUUID()
                            + " to kup with id=" + kup.getId());
                    kup.addBettableTournament(season);
                }
            }
            for (Question question : template.getBettableQuestion()) {
                if (!kup.getBettableQuestion().contains(question)) {
                    log.info("Adding question with uuid=" + question.getId()
                            + " to kup with id=" + kup.getId());
                    kup.addBettableQuestion(question);
                }
            }
            for (QuestionKupTiebreaker question : template
                    .getQuestionsTiebreaker()) {
                if (!kup.getQuestionsTiebreaker().contains(question)) {
                    log.info("Adding question tie breaker with uuid="
                            + question.getId() + " to kup with id="
                            + kup.getId());
                    kup.addQuestionTiebreaker(question);
                }
            }

        }

    }

    @Override
    public void syncTemplates() throws CoreException {

        // We only interested in opened or to be opened kups.
        final List<Byte> kupStatus = new ArrayList<Byte>();
        kupStatus.add(Integer.valueOf(0).byteValue()); // CREATED
        kupStatus.add(Integer.valueOf(1).byteValue()); // OPENED
        kupStatus.add(Integer.valueOf(2).byteValue()); // ON_GOING

        final String queryStr = "from "
                + KupImpl.class.getSimpleName()
                + " k where k.isTemplate=:isTemplate AND k.status IN (:status) AND k.isFinal=:isFinal";

        final Query query = createQuery(queryStr);
        query.setParameter("isTemplate", true);
        query.setParameter("isFinal", false); // Only non final Kups should be
                                              // upgraded
        query.setParameter("status", kupStatus);

        // Code below will be applied only to non-final Kup's templates.
        @SuppressWarnings("unchecked")
        final List<Kup> kups = query.getResultList();
        for (Kup kup : kups) {
            // Check Round's games
            for (TournamentRound round : kup.getBettableRounds()) {
                for (TournamentGame game : round.getGames()) {
                    if (!kup.getBettableGames().contains(game)) {
                        kup.addBettableGame(game);
                        log.info("Adding game with uuid=" + game.getUUID()
                                + " to kup with id=" + kup.getId());
                    }
                }
            }
            // Check Round's stages
            for (TournamentStage stage : kup.getBettableStages()) {
                for (TournamentRound round : stage.getRounds()) {
                    if (!kup.getBettableRounds().contains(round)) {
                        kup.addBettableRound(round);
                        log.info("Adding round with uuid=" + round.getUUID()
                                + " to kup with id=" + kup.getId());
                    }
                }
            }
            // Check Stage's seasons
            for (TournamentSeason season : kup.getBettableTournaments()) {
                for (TournamentStage stage : season.getStages()) {
                    if (!kup.getBettableStages().contains(stage)) {
                        kup.addBettableStage(stage);
                        log.info("Adding stage with uuid=" + stage.getUUID()
                                + " to kup with id=" + kup.getId());
                    }
                }
            }
            // Update start / end date (depends on events)
            kup.setStartDate(kup.getEffectiveStartDate());
            kup.setEndDate(kup.getEffectiveEndDate());
            // Update Kup search indexes.
            kup.setNbParticipants(kup.getParticipants().size());
            // Update Kup duration search index
            kup.setDuration(kup.computeDuration());
            // Set Sports if not set
            // Lazy init.
            kup.getSport();
        }

    }

    @Override
    public Date getPredictionsLastModifiedFor(Member member, Kup kup)
            throws CoreException {
        return predictions.getPredictionsLastModifiedFor(member, kup);
    }

    @Override
    public void invite(Kup kup, Member inviter, Map<String, String> params)
            throws CoreException {
        params.put("templateId", "kup-invite_fr");
        params.put("format", "text/plain");
        notifications.sendEmail(inviter, params);
    }

    @Override
    public int countCorrectPredictionsFor(Kup kup, Member member)
            throws CoreException {
        int nb = 0;
        for (Prediction p : predictions.getPredictionsFor(kup, member)) {
            if (p.getPoints() > 0) {
                nb += 1;
            }
        }
        // Lazy update
        MemberRankingTableEntry entry = kup.getRankingTable()
                .getEntryForMember(member);
        if (entry != null
                && (entry.getCorrectPredictions() == null || entry
                        .getCorrectPredictions() != nb)) {
            entry.setCorrectPredictions(nb);
        }
        return nb;
    }

    @Override
    public KupRankingTable getKupRanking(Kup kup, int offset, int batchSize)
            throws CoreException {
        return kup.getRankingTable();
    }

    @Override
    public Date getFirstPredictionDateFor(Member member, Kup kup)
            throws CoreException {
        Date earliest = null;
        for (Prediction p : predictions.getPredictionsFor(kup, member)) {
            if (earliest == null) {
                earliest = p.getCreated();
            } else if (p.getCreated().compareTo(earliest) < 0) {
                earliest = p.getCreated();
            }
        }
        // Lazy update
        MemberRankingTableEntry entry = kup.getRankingTable()
                .getEntryForMember(member);
        if (entry != null && entry.getFirstPredictions() != null
                && earliest != null) {
            entry.setFirstPredictions(earliest);
        }
        return earliest;
    }

    @Override
    public void updateKupStats(Kup kup) throws CoreException {
        if (kup == null) {
            return;
        }
        log.info("Updating statistics of Kup w/ uuid=" + kup.getId());
        KupRankingTable table = kup.getRankingTable();
        for (Iterator<MemberRankingTableEntry> i = table.getEntries()
                .iterator(); i.hasNext();) {
            MemberRankingTableEntry entry = i.next();
            entry.setCorrectPredictions(countCorrectPredictionsFor(kup,
                    entry.getMember()));
            if (entry.getFirstPredictions() == null) {
                entry.setFirstPredictions(getFirstPredictionDateFor(
                        entry.getMember(), kup));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateKupsStats() throws CoreException {

        // Only kup ranking tables not marked as final should be recomputed.
        final String queryStr = "from "
                + KupRankingTableImpl.class.getSimpleName()
                + " k where k.fin=:final";
        final Query query = createQuery(queryStr);
        query.setParameter("final", false);

        List<KupRankingTable> tables;
        try {
            tables = query.getResultList();
        } catch (NoResultException nre) {
            // This is case should never be happening unless the shop is
            // closed...
            tables = new ArrayList<KupRankingTable>();
        }
        for (Iterator<KupRankingTable> i = tables.listIterator(); i.hasNext();) {
            KupRankingTable table = i.next();
            Kup kup = table.getKup();
            updateKupStats(kup);
            byte kupStatus = kup.getStatus();
            if (KupType.FREE.equals(kup.getType())) {
                if (KupStatus.CANCELED == kupStatus
                        || KupStatus.SETTLED == kupStatus) {
                    table.setFinal(true);
                }
            } else {
                if (KupStatus.CANCELED == kupStatus
                        || KupStatus.PAID_OUT == kupStatus) {
                    table.setFinal(true);
                }
            }
        }

    }

    @Override
    public void addQuestionKupTiebreaker(Member member, Kup kup,
            QuestionKupTiebreaker question, String choice) throws CoreException {

        if (predictions.isPredictionAllowedOn(kup)) {
            predictions.createQuestionTiebreakerPredictionFor(member, question,
                    kup, choice);
        } else {
            throw new CoreException("Prediction not allowed on Kup with uuid="
                    + kup.getId());
        }

    }

    @Override
    public Kup getKupByTransactionId(long txnId) {

        String queryStr = "from " + KupMemberBetImpl.class.getSimpleName()
                + " b where b.transaction.id=:txnId";
        Query query = createQuery(queryStr);
        query.setParameter("txnId", txnId);

        KupMemberBet bet;
        try {
            bet = (KupMemberBet) query.getSingleResult();
        } catch (NoResultException nre) {
            bet = null;
        }

        Kup kup = null;
        if (bet != null) {
            kup = bet.getKup();
        }
        return kup;

    }

}
