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

package org.sofun.core.api.member;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.session.Session;

/**
 * Member service
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface MemberService extends Serializable {

    /**
     * Create a new {@link Member} giving its email. <br />
     * Emails are unique. It will return an existing Member if someone with the
     * email given as a parameter already exists.
     * 
     * @param status: Member's status at creation time.
     * @param type: Member's acccount type.
     * 
     * @param email: valid email address.
     * @return a {@link Member} instance.
     */
    Member createMember(String email, String status, String type)
            throws CoreException;

    Member createMember(Member member) throws CoreException;

    /**
     * Delete a {@link Member} given its email.
     * 
     * @param email: a valid email address.
     * @return the just deleted email instance or null if not found.
     */
    Member deleteMember(String email);

    /**
     * Returns a {@link Member} given its email.
     * 
     * @param email: a valid email address.
     * @return a {@link Member} instance or null if not found.
     */
    Member getMember(String email);

    Member getMember(long memberId);

    Member getMemberByFacebookId(String facebookId);

    /**
     * Count the total number of {@link Member} on the platform.
     * 
     * @return a postivee {@link Long}.
     */
    long countMembers();

    /**
     * List all members.
     * 
     * @param offset TODO
     * @param batchSize TODO
     * 
     * @return an {@link Iterator}
     */
    Iterator<Member> listMembers(int offset, int batchSize);

    /**
     * Used and define to close up all accounts
     * 
     * @return
     */
    List<Member> getGamblingMembers();

    boolean nickNameExists(String nickName);

    boolean emailExists(String email);

    boolean facebookIdExists(String facebookId);

    List<MemberTransaction> getTransactionHistory(Member member);

    List<MemberTransaction> getCreditHistory(Member member);

    List<MemberTransaction> getBonusCreditHistory(Member member);

    List<MemberTransaction> getDebitHistory(Member member);

    List<MemberTransaction> getWireHistory(Member member);

    List<MemberTransaction> getBetDebitHistory(Member member);

    List<MemberTransaction> getBetCreditHistory(Member member);

    /**
     * Member request to reset its password.
     * 
     * Sends an email to the user with a confirmation link so that it can reset
     * its password.
     * 
     * @param redirectUrl: URL to include in notification email. Typically a
     *        application level (different than Platform URL)
     * 
     * @param member: a {@link Member} instance.
     */
    void passwordForgotten(Member member, URL redirectUrl) throws CoreException;

    /**
     * Verifies a reset password token for a given member.
     * 
     * @param member: a {@link Member} instance.
     * @param token: a {@link String} token to verify.
     * @return true if valid / false if not.
     */
    boolean passwordVerifyResetToken(Member member, String token);

    /**
     * Member resets its password given its new password and reset token.
     * 
     * @param member: a {@link Member} instance.
     * @param token: a token string.
     * @throws CoreException
     */
    void passwordReset(Member member, String password, String token)
            throws CoreException;

    /**
     * Verify a password given a {@link Member} and a password's hash
     * representation.
     * 
     * @param member: a {@link Member} instance
     * @param hash: a hash password
     * @return true or false
     * @throws CoreException
     */
    boolean passwordVerify(Member member, String hash) throws CoreException;

    /**
     * Verify existing password reset tokens and destroy them if expired.
     */
    void passwordDestroyExpiredTokens();

    /**
     * Adds a Member Connection log.
     * 
     * @param member: a {@link Member} instance.
     * @param session: a Sofun {@link Session}
     * @param loginTime: the actual login time
     * @param remoteAddress: the remote address from which the member logged in
     */
    void addConnectionLog(Member member, Session session, Date loginTime,
            String remoteAddress);

    /**
     * Returns a connection log giving a Memner.
     * 
     * @param session: a {@link MemberConnectionLog}
     * @return a {@link MemberConnectionLog} instance.
     */
    MemberConnectionLog getConnectionLogFor(Member member, Session session);

    /**
     * Returns the last login time for a given member.
     * 
     * @param member: a {@link Member} instance.
     * @return a {@link Date} in UTC.
     */
    Date getLastLoginFor(Member member);

    /**
     * Returns the total number of connections for a given member.
     * 
     * @param member: a {@link Member}
     * @return the number of connection as an integer.
     */
    int getNumberOfConnectionFor(Member member);

    /**
     * Returns the total amount of money a member has placed as bets at a given
     * time.
     * 
     * @param member: a {@link Member} instance
     * @return a float amount > 0
     */
    float getTotalCurrentBetAmountFor(Member member);

    /**
     * Does a given member must accept a new policy
     * 
     * @param member: a {@link Member} instance
     * @return true or false
     */
    boolean mustMemberAcceptPolicy(Member member);

    /**
     * Returns the amount a player can transfer.
     * 
     * <p>
     * 
     * The transferable amount is the amount of the credit minus the bonus that
     * has not been placed as bets.
     * 
     * @return a {@link Float} value
     */
    float getTransferableAmountFor(Member member);

    List<MemberTransaction> getUnAckWinningsTransactionsFor(Member member);

    List<MemberTransaction> getUnAckBonusTransactionFor(Member member);

    MemberTransaction getMemberTransactionById(Member member, long txnId);

}
