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

package org.sofun.platform.arjel.banned;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.platform.arjel.banned.api.ARJELBannedService;
import org.sofun.platform.arjel.banned.api.MemberARJELBannedCheck;
import org.sofun.platform.arjel.banned.api.ejb.ARJELBannedServiceLocal;
import org.sofun.platform.arjel.banned.api.ejb.ARJELBannedServiceRemote;
import org.sofun.platform.arjel.banned.api.exception.ARJELBannedException;

/**
 * ARJEL Banned Service implementation.
 * 
 * <p>
 * 
 * Implements ARJEL DET version 1.1
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(ARJELBannedServiceLocal.class)
@Remote(ARJELBannedServiceRemote.class)
public class ARJELBannedServiceImpl implements ARJELBannedService {

    private static final long serialVersionUID = 7987937170866548952L;

    private static final Log log = LogFactory
            .getLog(ARJELBannedServiceImpl.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    /** Characters translation table */
    private static final Map<String, String> CHARS_TR = new HashMap<String, String>();

    static {
        CHARS_TR.put("À", "A");
        CHARS_TR.put("Â", "A");
        CHARS_TR.put("Ä", "A");
        CHARS_TR.put("Ç", "C");
        CHARS_TR.put("É", "E");
        CHARS_TR.put("È", "E");
        CHARS_TR.put("Ê", "E");
        CHARS_TR.put("Ë", "E");
        CHARS_TR.put("Î", "I");
        CHARS_TR.put("Ï", "I");
        CHARS_TR.put("Ô", "O");
        CHARS_TR.put("Ö", "O");
        CHARS_TR.put("Ù", "U");
        CHARS_TR.put("Û", "U");
        CHARS_TR.put("Ü", "U");
        CHARS_TR.put("Ÿ", "Y");
        CHARS_TR.put("Æ", "AE");
        CHARS_TR.put("æ", "OE");
    }

    /** Allowed characters in canonical expression for given and last names */
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** Allowed numbers in date format */
    private static final String ALLOWED_NUMBERS = "0123456789";

    /** DNS BL utility */
    private DNSBL dnsBl;

    public ARJELBannedServiceImpl() {
    }

    public ARJELBannedServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean isBanned(Member member, boolean doLog)
            throws ARJELBannedException {
        boolean banned = false;
        boolean match = false;
        final String secret = Configuration.getProperties().getProperty(
                "shared.op.secret");
        final String canonical = getCanonicalFor(member);
        final String hash = getHashFor(canonical, secret);
        final String record = getRecordsFor(hash);
        if (record != null) {
            match = true;
            String[] infos = record.split(";");
            if (infos.length == 2) {
                // Player not born in France
                final String city = infos[0];
                final String country = infos[1];
                if (country.equals(member.getBirthPlace())) {
                    final String normalized = getNormalizedForeignCity(city);
                    if (normalized.equals(member.getBirthPlace())) {
                        banned = true;
                    }
                }
            } else if (infos.length == 3) {
                // Player born in France
                final String city = infos[0];
                final String department = infos[1];
                final String country = infos[2];
                if (("FR".equals(member.getBirthCountry()) || ("FRANCE"
                        .equals(member.getBirthCountry())))
                        && "FRANCE".equals(country)) {
                    if (city.equals(member.getBirthPlace())
                            && department.equals(member.getBirthArea())) {
                        banned = true;
                    }
                }
            }
        }
        if (match && !banned) {
            log.warn("!!!NAMESAKE!!! - member with id=" + member.getId()
                    + " need to be double checked manually in case of.");
        }
        if (banned
                && !MemberAccountStatus.BANNED_FR.equals(member
                        .getAccountStatus())) {
            log.info("!!!BANNED!!! - member with id=" + member.getId()
                    + " is on the ARJEL banned players list."
                    + " Marking player as BANNED.");
            member.setAccountStatus(MemberAccountStatus.BANNED_FR);
        }
        if (doLog && em != null) {
            // This case is for unit testing purpose. em is always initialized
            // within container or nothing would be working at all.
            MemberARJELBannedCheck status = getMemberCheckStatus(member);
            if (status == null) {
                status = new MemberARJELBannedCheckImpl(member);
                em.persist(status);
            }
            status.setLastCheckTime(new Date());
            if (banned) {
                status.setBanned(true);
            } else {
                if (status.isBanned() && !banned) {
                    // Member has been removed from the ARJEL list of banned
                    // players. Back in the game.
                    status.setBanned(false);
                    member.setAccountStatus(MemberAccountStatus.CONFIRMED);
                    log.info("!!!BANNED!!! - member with id="
                            + member.getId()
                            + " is not on the ARJEL banned players list anymore"
                            + " Marking player back as CONFIRMED.");
                }
            }
        }
        return banned;
    }

    @Override
    public String getCanonicalFor(Member member) throws ARJELBannedException {
        final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String base = member.getFirstName() + member.getLastName()
                + sf.format(member.getBirthDate());
        base = base.toUpperCase();
        final StringBuilder buf = new StringBuilder();
        int offset = base.length();
        for (int idx = 0; idx < base.length(); ++idx) {
            final char ch = base.charAt(idx);
            final String chStr = String.valueOf(ch);
            final String tStr = CHARS_TR.get(chStr);
            if (tStr == null) {
                if ((offset > 8 && ALLOWED_CHARS.indexOf(ch) != -1)
                        || (offset <= 8 && ALLOWED_NUMBERS.indexOf(ch) != -1)) {
                    buf.append(ch);
                }
            } else {
                for (int tidx = 0; tidx < tStr.length(); ++tidx) {
                    final char tch = tStr.charAt(tidx);
                    buf.append(tch);
                }
            }
            --offset;
        }
        return buf.toString();
    }

    @Override
    public String getHashFor(String canonical, String secret)
            throws ARJELBannedException {
        try {
            final Mac mac = Mac.getInstance("HmacSHA1");
            final SecretKeySpec key = new SecretKeySpec(secret.getBytes(),
                    "HmacSHA1");
            mac.init(key);
            byte[] digest = mac.doFinal(canonical.getBytes());
            final StringBuffer buf = new StringBuffer();
            for (byte b : digest) {
                buf.append(String.format("%02x", b));
            }
            return buf.toString();
        } catch (Exception e) {
            throw new ARJELBannedException(e);
        }
    }

    @Override
    public String getRecordsFor(String hash) throws ARJELBannedException {
        DNSBL dnsbl;
        try {
            dnsbl = getDNSBL();
        } catch (NamingException e) {
            throw new ARJELBannedException(e);
        }
        final String zone = Configuration.getProperties().getProperty(
                "dns.zone");
        return dnsbl.getTXTAttributeRecordFor(hash + zone);
    }

    /**
     * Initialize and lazy init of the DNSBL utility.
     * 
     * @return a {@link DNSBL} instance
     * @throws NamingException
     */
    private DNSBL getDNSBL() throws NamingException {
        if (dnsBl == null) {
            final String dns = Configuration.getProperties().getProperty(
                    "dns.service");
            dnsBl = new DNSBL(dns);
        }
        return dnsBl;
    }

    /**
     * The conventions described below is applied to forgen cities (similar to
     * that applied to French municipalities)
     * 
     * <p>
     * 
     * <ul>
     * <li>removal of diacritics</li>
     * <li>upper case</li>
     * <li>removal of determinants</li>
     * <li>replacement of spaces (single) by dashes.</li>
     * </ul>
     * 
     * @param city: the actual city
     * @return a normalized {@link String}
     * @throws ARJELBannedException
     */
    private String getNormalizedForeignCity(String city)
            throws ARJELBannedException {
        // XXX how am I supposed to remove all adjectives in all languages?
        final String base = city.toUpperCase();
        final StringBuilder buf = new StringBuilder();
        for (int idx = 0; idx < base.length(); ++idx) {
            final char ch = base.charAt(idx);
            final String chStr = String.valueOf(ch);
            final String tStr = CHARS_TR.get(chStr);
            if (tStr == null) {
                if (ch == ' ') {
                    buf.append('-');
                } else {
                    buf.append(ch);
                }
            } else {
                for (int tidx = 0; tidx < tStr.length(); ++tidx) {
                    final char tch = tStr.charAt(tidx);
                    buf.append(tch);
                }
            }
        }
        return buf.toString();
    }

    @Override
    public MemberARJELBannedCheck getMemberCheckStatus(Member member)
            throws ARJELBannedException {
        final String queryStr = "from "
                + MemberARJELBannedCheckImpl.class.getSimpleName()
                + " c where c.member.id=:member_id";
        final Query query = em.createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        try {
            return (MemberARJELBannedCheck) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MemberARJELBannedCheck> getMembersToVerify()
            throws ARJELBannedException {

        // Get all members we have not checked status it's been 30 days

        final Date today = new Date();
        final Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        final Date today30 = cal.getTime();

        final String queryStr = "from "
                + MemberARJELBannedCheckImpl.class.getSimpleName()
                + " m WHERE m.lastChecked < :date";
        final Query query = em.createQuery(queryStr);
        query.setParameter("date", today30);
        return query.getResultList();

    }

}
