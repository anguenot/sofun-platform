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

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.Configuration;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.kup.bet.KupMemberBet;
import org.sofun.core.api.local.NotificationServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.api.member.MemberConnectionLog;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.member.PasswordResetToken;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.api.member.ejb.MemberServiceRemote;
import org.sofun.core.api.notification.NotificationService;
import org.sofun.core.api.session.Session;
import org.sofun.core.kup.bet.KupMemberBetImpl;

/**
 * Mermber Service Impl
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(MemberServiceLocal.class)
@Remote(MemberServiceRemote.class)
public class MemberServiceImpl implements MemberService {

    private static Log log = LogFactory.getLog(MemberServiceImpl.class);

    private static final long serialVersionUID = 4805045845124861253L;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @EJB(
            beanName = "NotificationServiceImpl",
            beanInterface = NotificationServiceLocal.class)
    private NotificationService notificationService;

    public MemberServiceImpl() {
    }

    public MemberServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    protected Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public Member createMember(Member member) throws CoreException {
        if (member == null) {
            return null;
        }
        Member existing = getMember(member.getEmail());
        if (existing == null) {
            em.persist(member);
        } else {
            return existing;
        }
        return getMember(member.getEmail());
    }

    @Override
    public Member createMember(String email, String status, String type)
            throws CoreException {
        Member member = getMember(email);
        if (member == null) {
            member = new MemberImpl(email, status, type);
            em.persist(member);
        }
        member = getMember(email);
        return member;
    }

    @Override
    public Member getMember(String email) {

        String queryStr = "from " + MemberImpl.class.getSimpleName()
                + " m where m.email=:email";
        Query query = createQuery(queryStr);
        query.setParameter("email", email);

        try {
            return (Member) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public Member getMember(long memberId) {
        String queryStr = "from " + MemberImpl.class.getSimpleName()
                + " m where m.id=:id";
        Query query = createQuery(queryStr);
        query.setParameter("id", memberId);

        try {
            return (Member) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public long countMembers() {
        String queryStr = "SELECT COUNT(m.id) FROM "
                + MemberImpl.class.getSimpleName() + " m";
        Query query = createQuery(queryStr);
        return (Long) query.getSingleResult();

    }

    @Override
    public Member deleteMember(String email) {
        Member member = getMember(email);
        if (member != null) {
            String queryStr = "delete from " + MemberImpl.class.getSimpleName()
                    + " m where m.email=:email";
            Query query = createQuery(queryStr);
            query.setParameter("email", email);
            query.setMaxResults(1);
            query.executeUpdate();
        }
        return member;

    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Member> listMembers(int offset, int batchSize) {
        String queryStr = "from " + MemberImpl.class.getSimpleName()
                + " ORDER BY id";
        Query query = createQuery(queryStr);
        query.setFirstResult(offset);
        query.setMaxResults(batchSize);
        return query.getResultList().iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Member> getGamblingMembers() {

        String queryStr = "from " + MemberImpl.class.getSimpleName()
                + " m where m.type=:type and m.status=:status";
        Query query = createQuery(queryStr);
        query.setParameter("type", MemberAccountType.GAMBLING_FR);
        query.setParameter("status", MemberAccountStatus.VERIFIED_FR);
        return query.getResultList();
    }

    @Override
    public Member getMemberByFacebookId(String facebookId) {
        String queryStr = "from " + MemberImpl.class.getSimpleName()
                + " m where m.facebookId=:facebookId";
        Query query = createQuery(queryStr);
        query.setParameter("facebookId", facebookId);

        try {
            return (Member) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public boolean nickNameExists(String nickName) {
        if (nickName == null || nickName.isEmpty()) {
            return false;
        }
        final String queryStr = "SELECT count(m.nickName) FROM "
                + MemberImpl.class.getSimpleName()
                + " m WHERE m.nickName=:nickName";
        final Query query = createQuery(queryStr);
        query.setParameter("nickName", nickName);

        try {
            int count = ((Long) query.getSingleResult()).intValue();
            if (count > 0) {
                return true;
            }
        } catch (NoResultException nre) {

        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        final String queryStr = "SELECT count(m.email) FROM "
                + MemberImpl.class.getSimpleName() + " m WHERE m.email=:email";
        final Query query = createQuery(queryStr);
        query.setParameter("email", email);

        try {
            int count = ((Long) query.getSingleResult()).intValue();
            if (count > 0) {
                return true;
            }
        } catch (NoResultException nre) {

        }
        return false;
    }

    @Override
    public boolean facebookIdExists(String facebookId) {
        if (facebookId == null || facebookId.isEmpty()) {
            return false;
        }
        final String queryStr = "SELECT count(m.facebookId) FROM "
                + MemberImpl.class.getSimpleName()
                + " m WHERE m.facebookId=:facebookId";
        final Query query = createQuery(queryStr);
        query.setParameter("facebookId", facebookId);

        try {
            int count = ((Long) query.getSingleResult()).intValue();
            if (count > 0) {
                return true;
            }
        } catch (NoResultException nre) {

        }
        return false;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getTransactionHistory(Member member) {

        String queryStr = "from " + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:id ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("id", member.getId());

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getCreditHistory(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.credit=:credit AND t.type=:type ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("type", MemberTransactionType.CC_CREDIT);
        query.setParameter("credit", true);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getBonusCreditHistory(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.bonus=:bonus ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("bonus", true);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getDebitHistory(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.debit=:debit ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("debit", true);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getWireHistory(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.debit=:debit AND t.type=:type ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("type", MemberTransactionType.WIRE_DEBIT);
        query.setParameter("debit", true);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getBetDebitHistory(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.type=:type ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("type", MemberTransactionType.BET_DEBIT);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getBetCreditHistory(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.type=:type ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("type", MemberTransactionType.BET_CREDIT);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    private PasswordResetToken createPasswordResetTokenFor(Member member) {
        final String token = UUID.randomUUID().toString();
        PasswordResetToken iToken = new PasswordResetTokenImpl(member, token);
        em.persist(iToken);
        return iToken;
    }

    private PasswordResetToken getPasswordResetTokenFor(Member member) {

        String queryStr = "from "
                + PasswordResetTokenImpl.class.getSimpleName()
                + " t where t.member.id=:member_id";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());

        try {
            return (PasswordResetToken) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @Override
    public void passwordForgotten(Member member, URL redirectUrl)
            throws CoreException {

        // Check if member already has a token and remove it.
        PasswordResetToken token = getPasswordResetTokenFor(member);
        if (token != null) {
            em.remove(token);
        }

        // Generate a new password reset token.
        token = createPasswordResetTokenFor(member);

        // List of params that will be forwarded to the template engine.
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject", "BetKup : demande de changement de mot de passe"); // XXX
                                                                                 // translate
        params.put("format", "text/html"); // XXX make this is a incoming app
                                           // level parameter in the future.
        params.put("redirect_url",
                redirectUrl.toString() + "?token=" + token.getToken()
                        + "&email=" + member.getEmail());
        params.put("templateId", "password-forgotten_fr"); // XXX make this is a
                                                           // incoming app level
                                                           // parameter in the
                                                           // future.

        SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
        params.put("hrBirthdate", sf.format(member.getBirthDate()));

        // Send an email to user with linke to reset URL w/ token.
        notificationService.sendEmail(member, params);

    }

    @Override
    public boolean passwordVerifyResetToken(Member member, String token) {
        PasswordResetToken iToken = getPasswordResetTokenFor(member);
        if (iToken != null && !iToken.isExpired()) {
            return iToken.getToken().equals(token);
        }
        return false;
    }

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @Override
    public void passwordDestroyExpiredTokens() {

        log.debug("Cleaning up password reset tokens...");

        String queryStr = "from "
                + PasswordResetTokenImpl.class.getSimpleName();
        Query query = createQuery(queryStr);

        try {
            @SuppressWarnings("unchecked")
            List<PasswordResetToken> tokens = query.getResultList();
            for (PasswordResetToken token : tokens) {
                if (token.isExpired()) {
                    log.debug("Deleting expired password reset token for member="
                            + token.getMember().getEmail());
                }
            }
        } catch (NoResultException nre) {
            log.debug("No password reset tokens stored. Nothing to do.");
        }

    }

    @Override
    public void passwordReset(Member member, String password, String token)
            throws CoreException {

        if (!passwordVerifyResetToken(member, token)) {
            throw new CoreException("Token is invalid!");
        }

        // Change password.
        member.setPassword(password);

        PasswordResetToken iToken = getPasswordResetTokenFor(member);
        em.remove(iToken);

        // List of params that will be forwarded to the template engine.
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject",
                "BetKup : confirmation de changement de mot de passe"); // XXX
                                                                        // translate
        params.put("format", "text/html"); // XXX make this is a incoming app
                                           // level parameter in the future.
        params.put("templateId", "password-reset_fr"); // XXX make this is a
                                                       // incoming app level
                                                       // parameter in the
                                                       // future.

        // Send an email to user with linke to reset URL w/ token.
        notificationService.sendEmail(member, params);

    }

    @Override
    public void addConnectionLog(Member member, Session session,
            Date loginTime, String remoteAddress) {
        MemberConnectionLog log = new MemberConnectionLogImpl(member,
                loginTime, session.getKey(), remoteAddress);
        em.persist(log);
    }

    @Override
    public MemberConnectionLog getConnectionLogFor(Member member,
            Session session) {

        String queryStr = "from "
                + MemberConnectionLogImpl.class.getSimpleName()
                + " l where l.member.id=:member_id AND l.sessionKey=:sessionKey";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("sessionKey", session.getKey());

        try {
            return (MemberConnectionLog) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public Date getLastLoginFor(Member member) {

        String queryStr = "from "
                + MemberConnectionLogImpl.class.getSimpleName()
                + " l where l.member.id=:member_id ORDER BY l.loginTime DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setMaxResults(2);

        List<MemberConnectionLog> logs;
        try {
            logs = query.getResultList();
        } catch (NoResultException nre) {
            return null;
        }

        for (MemberConnectionLog log : logs) {
            if (log.getLogoutDate() != null) {
                // The current one does not have logout date
                return log.getLoginDate();
            }
        }

        return null;

    }

    @Override
    public int getNumberOfConnectionFor(Member member) {

        String queryStr = "SELECT COUNT(l.id) FROM "
                + MemberConnectionLogImpl.class.getSimpleName()
                + " l where l.member.id=:member_id";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        return (Integer) query.getSingleResult();

    }

    @Override
    public boolean passwordVerify(Member member, String hash)
            throws CoreException {
        if (member != null && hash != null) {
            return hash.equals(member.getPassword());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public float getTotalCurrentBetAmountFor(Member member) {

        float total = 0;

        final String queryStr = "from "
                + KupMemberBetImpl.class.getSimpleName()
                + " b where b.member.id=:member_id AND b.kup.status < 5 AND b.kup.status >= 0";
        final Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());

        List<KupMemberBet> currentBets;
        try {
            currentBets = query.getResultList();
        } catch (NoResultException nre) {
            return total;
        }

        for (KupMemberBet bet : currentBets) {
            total += bet.getKup().getStake();
        }

        return total;
    }

    @Override
    public boolean mustMemberAcceptPolicy(Member member) {
        final String latestStr = (String) Configuration.getProperties().get(
                "betkup.policy.latest");
        final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        Date latest = null;
        try {
            latest = sf.parse(latestStr);
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            log.error("An error occured trying to parse latest policy date."
                    + " Check sofun.properties");
            return false;
        }
        final Date memberLatest = member.getPolicyAcceptanceDate();
        return memberLatest.compareTo(latest) < 0;
    }

    @Override
    public float getTransferableAmountFor(Member member) {
        float transferable = member.getMemberCredit().getCredit();
        float uncovered = 0;
        for (MemberTransaction bonusTxn : getBonusCreditHistory(member)) {
            float bonusValue = bonusTxn.getAmount();
            float covered = 0;
            for (MemberTransaction betTxn : getDebitHistory(member)) {
                if (MemberTransactionType.BET_DEBIT.equals(betTxn.getLabel())
                        && betTxn.getDate().compareTo(bonusTxn.getDate()) > 0) {
                    covered += betTxn.getAmount();
                }
            }
            float bonusUncovered = bonusValue - covered;
            if (bonusUncovered > 0) {
                uncovered += bonusUncovered;
            }
        }
        return transferable - uncovered;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getUnAckWinningsTransactionsFor(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.credit=:credit AND t.type=:type AND t.ack=:ack ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("type", MemberTransactionType.BET_CREDIT);
        query.setParameter("credit", true);
        query.setParameter("ack", false);

        // TMP
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberTransaction> getUnAckBonusTransactionFor(Member member) {

        String queryStr = "from "
                + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id AND t.bonus=:bonus AND t.ack=:ack ORDER BY txn_date DESC";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("bonus", true);
        query.setParameter("ack", false);

        // TMP
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            return query.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<MemberTransaction>();
        }

    }

    @Override
    public MemberTransaction getMemberTransactionById(Member member, long txnId) {

        String queryStr = "from " + MemberTransactionImpl.class.getSimpleName()
                + " t where t.member.id=:member_id and t.id=:id";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        query.setParameter("id", txnId);

        try {
            return (MemberTransaction) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

}
