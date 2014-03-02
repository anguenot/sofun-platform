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

package org.sofun.platform.arjel.banned.api;

import java.io.Serializable;
import java.util.List;

import org.sofun.core.api.member.Member;
import org.sofun.platform.arjel.banned.api.exception.ARJELBannedException;

/**
 * ARJEL Banned Players Service.
 * 
 * <p>
 * 
 * The operator must use the infrastructure made available by ARJEL for players
 * banned from gambling. Operator's requests get a binary response: absence or
 * presence in the list.
 * 
 * <p>
 * 
 * The following procedures must be implemented by the operator:
 * 
 * <ul>
 * <li>every player's request to open an account or log in: the operator must
 * check if the player is banned from gambling. If the answer is positive the
 * request is blocked.
 * <li>periodically (monthly) each operator control for each player with a
 * account opened if the player ended up in the list. If a player was
 * registered, the player account is blocked.</li>
 * </ul>
 * 
 * <p>
 * 
 * The interrogation protocol is a DNS blacklist (DNSBL) query mechanism. The
 * key is a cryptographic hmac-sha1 hash calculated from the following fields:
 * first name, last name and date of birth of the player.
 * 
 * <p>
 * 
 * DNS exchanges are authenticated with TSIG DNS extension. If the answer is
 * positive, when the player is present in the list, a record of type A is
 * defined along with a TXT record of the birth location of the player.
 * 
 * <p>
 * 
 * The DNS zone in use for the queries: interdits-arjel.fr.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
public interface ARJELBannedService extends Serializable {

    /**
     * Returns the interrogation key for a given member.
     * 
     * <p>
     * 
     * Checking the status of a player is based on a key question constructed
     * from the following parameters:
     * 
     * <ul>
     * <li>First given name of the player</li>
     * <li>Surname of the player</li>
     * <li>Date of birth of the player</li>
     * </ul>
     * 
     * <p>
     * 
     * To ensure the confidentiality of such information, the key is the result
     * of a hash function indexed by a shared secret between the gaming operator
     * and ARJEL
     * 
     * <p>
     * 
     * The function is HMAC-SHA1. The shared secret is exchanged securely
     * between the operator and the ARJEL.
     * 
     * <p>
     * 
     * This key is suffixed with the domain : interdits-arjel.fr.
     * 
     * @param canonical: {@link Member} canonical expression
     * @param secret: shared secret
     * @return a {@link String}
     */
    String getHashFor(String canonical, String secret)
            throws ARJELBannedException;

    /**
     * Returns the canonical form
     * 
     * The above parameters are set in canonical form, in particular:
     * 
     * <p>
     * 
     * <ul>
     * <li>the surname and first name are concatenated, are subject to a
     * normalization, with: removal of diacritical case insensitive (acute
     * accents, grave and circumflex, cedilla and diaeresis) transition to the
     * case above, removing characters outside of [AZ]</li>
     * <li>date of birth is standardized in the format "YYYYMMDD" (string)</li>
     * <li>the name, surname and date of birth set canonical form are
     * concatenated</li>
     * </ul>
     * 
     * @param member: a {@link Member} instance
     * @return a {@link String}
     * @throws ARJELBannedException
     */
    String getCanonicalFor(Member member) throws ARJELBannedException;

    /**
     * Returns the record, if any, given an hash key.
     * 
     * <p>
     * 
     * If the player is not in the list of prohibited gambling, an NXDOMAIN
     * error type is returned DNS side transmitted.
     * 
     * <p>
     * 
     * If the player is in the list:
     * 
     * <ul>
     * <li>The resource record type A is always associated 127.0.0.42</li>
     * <li>The resource record type TXT contains a string formatted as follows:
     * if the country of birth of the person is France, the format of the answer
     * is "CITY;DEPARTMENT;FRANCE;" for example,
     * "Trouville;SEINE-MARITIME;FRANCE"; if the country of birth of the person
     * is a foreign territory, the format of the answer is "CITY;COUNTRY;" for
     * example, "MADRID;SPAIN;".</li>
     * </ul>
     * 
     * @param hash: hash key.
     * @return
     * @throws ARJELBannedException
     */
    String getRecordsFor(String hash) throws ARJELBannedException;

    /**
     * Is a given {@link Member} banned from gaming?
     * 
     * @param log TODO
     * 
     * @param member: a {@link Member} instance
     * @param doLog: records check in DB
     * @return: true or false
     * @throws ARJELBannedException
     */
    boolean isBanned(Member member, boolean doLog) throws ARJELBannedException;

    /**
     * Returns the ARJEL banned list status check.
     * 
     * @param member: a {@link Member} instance
     * @return a {@link MemberARJELBannedCheck} instance
     * @throws ARJELBannedException
     */
    MemberARJELBannedCheck getMemberCheckStatus(Member member)
            throws ARJELBannedException;

    /**
     * Returns the list of members we have not checked it's been more than 1
     * month.
     * 
     * @return a {@link List} of {@link MemberARJELBannedCheck} instances
     * @throws ARJELBannedException
     */
    List<MemberARJELBannedCheck> getMembersToVerify()
            throws ARJELBannedException;

}
