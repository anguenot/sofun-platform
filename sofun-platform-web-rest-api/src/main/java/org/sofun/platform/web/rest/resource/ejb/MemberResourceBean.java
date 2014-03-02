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

package org.sofun.platform.web.rest.resource.ejb;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.CoreUtil;
import org.sofun.core.api.banking.CurrencyType;
import org.sofun.core.api.community.Community;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.feed.FeedEntryType;
import org.sofun.core.api.kup.Kup;
import org.sofun.core.api.kup.KupSearchResults;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.api.member.MemberCredit;
import org.sofun.core.api.member.MemberPostalAddress;
import org.sofun.core.api.member.MemberTransaction;
import org.sofun.core.api.member.MemberTransactionStatus;
import org.sofun.core.api.member.MemberTransactionType;
import org.sofun.core.api.member.bank.MemberIBAN;
import org.sofun.core.api.member.bank.MemberRIB;
import org.sofun.core.api.messaging.SofunMessagingDestination;
import org.sofun.core.api.prediction.Prediction;
import org.sofun.core.api.session.Session;
import org.sofun.core.member.MemberCreditImpl;
import org.sofun.core.member.MemberIBANImpl;
import org.sofun.core.member.MemberImpl;
import org.sofun.core.member.MemberPostalAddressImpl;
import org.sofun.core.member.MemberRIBImpl;
import org.sofun.core.member.MemberTransactionImpl;
import org.sofun.platform.facebook.api.FacebookMemberInfo;
import org.sofun.platform.web.rest.api.exception.ReSTException;
import org.sofun.platform.web.rest.api.exception.ReSTRuntimeException;
import org.sofun.platform.web.rest.api.exception.ReSTValidationException;
import org.sofun.platform.web.rest.api.kup.ReSTMemberKup;
import org.sofun.platform.web.rest.api.member.ReSTMember;
import org.sofun.platform.web.rest.api.member.post.PasswordLogin;
import org.sofun.platform.web.rest.api.member.post.PostAdvancedRegisterMember;
import org.sofun.platform.web.rest.api.member.post.PostRegisterMember;
import org.sofun.platform.web.rest.api.member.post.PostSimpleRegisterMember;
import org.sofun.platform.web.rest.api.prediction.get.ReSTPrediction;
import org.sofun.platform.web.rest.api.transaction.ReSTTransaction;
import org.sofun.platform.web.rest.resource.ejb.api.MemberResource;

/**
 * Member resources.
 * 
 * <p/>
 * Provides member ReST APIs.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
public class MemberResourceBean extends AbstractResource implements
        MemberResource {

    private static final long serialVersionUID = -7923832563812680496L;

    private static final Log log = LogFactory.getLog(MemberResourceBean.class);

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @Resource
    protected transient SessionContext context;

    public MemberResourceBean() {
        super();
    }

    @Override
    public Response register(Map<String, String> params) throws ReSTException {

        final long communityId = Long.valueOf(params.get("communityId"));
        Community c = getCoreCommunityById(communityId);

        //
        // Let's find out what kind of account we need to create.
        //

        final String type = params.get("account_type");
        PostRegisterMember post = null;
        if (MemberAccountType.SIMPLE.equals(type)) {
            post = new PostSimpleRegisterMember(params);
        } else if (MemberAccountType.GAMBLING_FR.equals(type)) {
            post = new PostAdvancedRegisterMember(params);
        } else {
            if (type != null) {
                return Response.status(400)
                        .entity("Account type=" + type + " not recognized.")
                        .build();
            } else {
                return Response.status(400).entity("Account type missing.")
                        .build();
            }
        }

        //
        // Verify required information.
        //

        if (!post.verify()) {

            return Response.status(400)
                    .entity("Missing informations to register member.").build();

        } else {

            final String email = params.get("email");
            final String status = params.get("account_status");

            Member member = null;
            boolean memberExists = false;
            try {
                member = getCoreMemberByEmail(email);
                memberExists = true;
                log.debug("Found existing Sofun member with email=" + email);
                if (MemberAccountType.GAMBLING_FR.equals(member
                        .getAccountType())) {
                    return Response
                            .status(400)
                            .entity("Member exists and has already a gambing account setup")
                            .build();
                }
            } catch (ReSTException rve) {

                //
                // Member not found at application level. We create one.
                //

                if (400 == rve.getStatusCode()) {
                    try {
                        log.info("New Member with email=" + email + " created!");
                        member = new MemberImpl(email, status, type);
                        member.setAccountStatus(MemberAccountStatus.CONFIRMED);
                    } catch (CoreException e) {
                        throw new ReSTRuntimeException(e.getMessage());
                    }
                } else {
                    throw rve;
                }
            }

            if (!memberExists) {
                // Verify if pseudo and email are not already in use.
                if (getMemberService().nickNameExists(params.get("pseudo"))
                        || getMemberService().emailExists(params.get("email"))) {
                    return Response.status(400)
                            .entity("Pseudo or email already taken!").build();
                }
            } else {
                if (!member.getMemberTeams().contains(c.getDefaultTeam())) {
                    try {
                        log.info("New Member with email=" + email
                                + " joined community with name=" + c.getName());
                        // member = c.addMember(member);
                    } catch (Exception e) {
                        throw new ReSTRuntimeException(500, e.getMessage());
                    }
                } else {
                    log.debug("Member with email="
                            + email
                            + " is already a membre of the community with name="
                            + c.getName());
                }
            }
            member.setNickName(params.get("pseudo"));

            final String password = params.get("password");
            // 128Bytes = 512 bits
            if (password != null && password.getBytes().length < 128) {
                return Response.status(400)
                        .entity("128 bytes hashed passwords are mandatory.")
                        .build();
            }
            member.setPassword(password);

            //
            // Basic information
            //

            member.setLastName(params.get("lastName"));
            member.setFirstName(params.get("givenName"));

            final String birthYear = params.get("birthYear");
            final String birthMonth = params.get("birthMonth");
            final String birthDay = params.get("birthDay");

            Date today = new Date();
            try {
                final Date birthDate = CoreUtil.getDate(birthYear, birthMonth,
                        birthDay);
                final long diffTo = ((today.getTime() - birthDate.getTime()) / (1000 * 60 * 60 * 24)) / 365;
                if ((MemberAccountType.SIMPLE.equals(type) && diffTo < CoreConstants.SIMPLE_ACCOUNT_MIN_AGE)
                        || (MemberAccountType.GAMBLING_FR.equals(type) && diffTo < CoreConstants.GAMBLING_FR_ACCOUNT_MIN_AGE)) {
                    return Response
                            .status(400)
                            .entity("You are not allowed to register because of you age.")
                            .build();
                }
                member.setBirthDate(birthDate);
            } catch (Exception e) {
                return Response.status(400).entity("Birthdate is invalid")
                        .build();
            }

            //
            // Advanced information.
            //

            if (MemberAccountType.GAMBLING_FR.equals(type)) {

                member.setAccountType(MemberAccountType.GAMBLING_FR);

                final String gender = params.get("gender");
                try {
                    member.setGender(Integer.valueOf(gender));
                } catch (Exception e) {
                    return Response.status(400).entity("Gender is missing.")
                            .build();
                }

                final String title = params.get("title");
                if (title == null) {
                    return Response.status(400).entity("Title is missing.")
                            .build();
                }
                member.setTitle(title);

                //
                // Address
                //

                final String street = params.get("street");
                final String zip = params.get("zip");
                final String city = params.get("city");
                final String state = params.get("state");

                // Move verification at post level.
                if (street != null && zip != null && city != null
                        && state != null) {
                    MemberPostalAddress addr = new MemberPostalAddressImpl(
                            street, city, zip, null, state);
                    member.setPostalAddress(addr);
                } else {
                    return Response.status(400)
                            .entity("Address must be a valid French address.")
                            .build();
                }

                //
                // Birth information
                //

                final String birthCountry = params.get("birthCountry");
                final String birthArea = params.get("birthArea");
                final String birthPlace = params.get("birthPlace");

                if (birthCountry != null && birthArea != null
                        && birthPlace != null) {
                    member.setBirthArea(birthArea);
                    member.setBirthPlace(birthPlace);
                    member.setBirthCountry(birthCountry);
                } else {
                    return Response.status(400)
                            .entity("Birth information missing.").build();
                }

                // Check ARJEL banned players list
                // checkBanned(member, false);

                //
                // Banking information.
                //

                final String ribBank = params.get("ribBank");
                final String ribBranch = params.get("ribBranch");
                final String ribNumber = params.get("ribNumber");
                final String ribKey = params.get("ribKey");

                MemberRIB rib = new MemberRIBImpl(ribBank, ribBranch,
                        ribNumber, ribKey);
                member.setRIB(rib);

                final String ibanNumber = params.get("ibanNumber");
                final String ibanSwift = params.get("ibanSwift");

                MemberIBAN iban = new MemberIBANImpl(ibanNumber, ibanSwift);
                member.setIBAN(iban);

                if (member.getRIB() == null && member.getIBAN() == null) {
                    return Response
                            .status(400)
                            .entity("At least one bank account information shall be given.")
                            .build();
                }

                MemberCredit credit = new MemberCreditImpl();
                credit.setCurrency("EURO");
                credit.setCredit(0);

                final String maxAmountBetWeekly = params
                        .get("maxAmountBetWeekly");
                final String maxAmountCreditWeekly = params
                        .get("maxAmountCreditWeekly");

                try {
                    credit.setMaxAmountPerWeekCredit(Float
                            .valueOf(maxAmountCreditWeekly));
                    credit.setMaxAmountPerWeekForBets(Float
                            .valueOf(maxAmountBetWeekly));
                } catch (Exception e) {
                    return Response.status(400)
                            .entity("Credit limits not provided.").build();
                }

                member.setMemberCredit(credit);

                //
                // All information are there : we can switch the account as
                // confirmed.
                //

                member.setAccountStatus(MemberAccountStatus.CONFIRMED);
                member.setGamblingFRAccountCreationDate(new Date());

            }

            if (!memberExists) {
                try {
                    getMemberService().createMember(member);
                    log.info("New Member with email=" + email
                            + " joined community with name=" + c.getName());
                    // member = c.addMember(member);
                } catch (CoreException e) {
                    throw new ReSTRuntimeException(500, e.getMessage());
                }
            }

            // Track member creation (partners)
            trackMember(params, member);

            ReSTMember rMember = new ReSTMember(member);
            // XXX move this at lower level
            List<String> args = new ArrayList<String>();
            pushToFeed(c.getDefaultTeam(), member,
                    FeedEntryType.NEW_COMMUNITY_MEMBER, args);
            return Response.status(202).entity(rMember).build();
        }

    }

    private void trackMember(Map<String, String> params, Member member) {
        // Partner tracking code
        final String trackingCode = params.get("trackingCode");
        if (trackingCode != null && !trackingCode.isEmpty()) {
            log.info("Member w/ email=" + member.getEmail()
                    + " has a tracking code with value=" + trackingCode);
            // XXX record tracking
        }
    }

    @Override
    public Response existsPseudo(String pseudo) throws ReSTException {
        String decoded;
        try {
            decoded = URLDecoder.decode(pseudo, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return Response.status(400).entity(false).build();
        }
        if (getMemberService().nickNameExists(decoded)) {
            return Response.status(202).entity(true).build();
        } else {
            return Response.status(400).entity(false).build();
        }
    }

    @Override
    public Response existsEmail(String email) throws ReSTException {
        if (getMemberService().emailExists(email)) {
            return Response.status(202).entity(true).build();
        } else {
            return Response.status(400).entity(false).build();
        }
    }

    /*
     * private Response checkBanned(Member member, boolean doLog) throws
     * ReSTException {
     * 
     * if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())) { try
     * { final boolean banned = getArjelService() .isBanned(member, doLog); if
     * (banned) { return Response.status(403).entity("Banned by ARJEL")
     * .build(); } } catch (ARJELBannedException e) {
     * log.error("Impossible to access ARJEL banned players " + " (member id=" +
     * member.getId() + ") " + e.getMessage()); } } else if
     * (MemberAccountStatus.BANNED_FR.equals(member .getAccountStatus())) {
     * return Response.status(403).entity("Banned by ARJEL").build(); }
     * 
     * return null; }
     */

    @Override
    public Response login(PasswordLogin login) throws ReSTException {

        //
        // Validate credentials
        //

        if (!login.validate()) {
            return Response.status(400)
                    .entity("Missing mandatory login information.").build();
        }

        //
        // Get credentials
        //

        final String email = login.getEmail();
        final String password = login.getPassword();
        final int birthDay = login.getBirthDay();
        final int birthMonth = login.getBirthMonth();
        final int birthYear = login.getBirthYear();
        final long communityId = login.getCommunityId();

        //
        // Get member and check if it belongs to the right community.
        //

        Community c;
        try {
            c = getCoreCommunityById(communityId);
        } catch (ReSTException re) {
            return Response
                    .status(400)
                    .entity("Community not found id="
                            + String.valueOf(communityId)).build();
        }

        Member member;
        try {
            member = getCoreMemberByEmail(email);
        } catch (ReSTException e) {
            return Response.status(400)
                    .entity("Member not found email=" + email).build();
        }

        // Check ARJEL banned players list in case of a Gambling player.
        if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())) {
            // checkBanned(member, true);
        }

        //
        // Check account status
        //

        final String status = member.getAccountStatus();
        if (MemberAccountStatus.BANNED_FR.equals(status)
                || MemberAccountStatus.MEMBER_SUSPENDED.equals(status)
                || MemberAccountStatus.CLOSED.equals(status)
                || MemberAccountStatus.CREATED.equals(status)) {
            return Response.status(403).entity("Your account is locked.")
                    .build();
        }

        //
        // Check birth date
        //

        final Date birthDate = member.getBirthDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(birthDate);
        if (cal.get(Calendar.DATE) != birthDay
                // Java counts months from 0
                || cal.get(Calendar.MONTH) + 1 != birthMonth
                || cal.get(Calendar.YEAR) != birthYear) {
            return Response.status(400).entity("Birthdate does not match.")
                    .build();
        }

        //
        // Check if this is an SHA-256 hashed password. If not raise error as we
        // do not allow plain text login here.
        //

        // 128Bytes = 512 bits
        if (password.getBytes().length < 128) {
            return Response.status(400)
                    .entity("128 bytes hashed passwords are mandatory.")
                    .build();
        }

        // Compare hashes.
        if (password.equals(member.getPassword())) {
            // XXX move this at lower level
            List<String> args = new ArrayList<String>();
            pushToFeed(c.getDefaultTeam(), member,
                    FeedEntryType.MEMBER_LOGGED_IN, args);
            return Response.status(202).entity(new ReSTMember(member)).build();
        } else {
            return Response.status(401).entity("Password does not match.")
                    .build();
        }

    }

    @Override
    public Response logout(String email) throws ReSTException {

        Member member = getCoreMemberByEmail(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        Session session = getSessionService().getSession(member);
        getSessionService().removeSession(session);

        return Response.status(202).entity(new ReSTMember(member)).build();
    }

    @Override
    public Response logoutFacebook(String facebookId) throws ReSTException {

        Member member = getCoreMemberByFacebookId(facebookId);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        Session session = getSessionService().getSession(member);
        getSessionService().removeSession(session);

        return Response.status(202).entity(new ReSTMember(member)).build();

    }

    @Override
    public Response edit(Map<String, String> params) throws Exception {

        final String email = params.get("email");
        // final long communityId = Long.valueOf(params.get("communityId"));

        Member member = getCoreMemberByEmail(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        /*
         * if (!member.getMemberTeams().contains(
         * getCoreCommunityById(communityId).getDefaultTeam())) { return
         * Response .status(400) .entity("Member not found in community=" +
         * String.valueOf(communityId)).build(); }
         */

        // We need to track this for legal purpose.
        boolean addressUpdated = false;
        boolean bankingUpdated = false;

        final Set<Map.Entry<String, String>> set = params.entrySet();
        for (Map.Entry<String, String> param : set) {
            final String k = param.getKey();
            final String value = params.get(param.getKey());
            if ("pseudo".equals(k)) {
                if (!getMemberService().nickNameExists(value)) {
                    member.setNickName(value);
                } else {
                    return Response.status(400)
                            .entity("Pseudo already in use.").build();
                }
            } else if ("password".equals(k)) {
                final String oldPassword = params.get("oldPassword");
                // Hash comparison
                if (member.getPassword() != null
                        && !member.getPassword().equals(oldPassword)) {
                    return Response.status(403)
                            .entity("Old Password does not match.").build();
                }
                member.setPassword(value);
            } else if ("firstName".equals(k)) {
                member.setFirstName(value);
            } else if ("lastName".equals(k)) {
                member.setLastName(value);
            } else if ("avatar_url".equals(k)) {
                URL url;
                try {
                    url = new URL(value);
                } catch (MalformedURLException e) {
                    return Response.status(400)
                            .entity("Avatar URL is malformed.").build();
                }
                member.setAvatar(url);
            } else if ("street".equals(k)) {
                MemberPostalAddress addr = member.getPostalAddress();
                if (!value.equals(addr.getStreet())) {
                    addr.setStreet(value);
                    member.setPostalAddress(addr);
                    em.merge(addr);
                    em.merge(member);
                    addressUpdated = true;
                }
            } else if ("city".equals(k)) {
                MemberPostalAddress addr = member.getPostalAddress();
                if (!value.equals(addr.getCity())) {
                    addr.setCity(value);
                    member.setPostalAddress(addr);
                    em.merge(addr);
                    em.merge(member);
                    addressUpdated = true;
                }
            } else if ("zip".equals(k)) {
                MemberPostalAddress addr = member.getPostalAddress();
                if (!value.equals(addr.getZipCode())) {
                    addr.setZip(value);
                    member.setPostalAddress(addr);
                    em.merge(addr);
                    em.merge(member);
                    addressUpdated = true;
                }
            } else if ("country".equals(k)) {
                MemberPostalAddress addr = member.getPostalAddress();
                if (!value.equals(addr.getCountry())) {
                    addr.setCountry(value);
                    member.setPostalAddress(addr);
                    em.merge(addr);
                    em.merge(member);
                    addressUpdated = true;
                }
            } else if ("ribBank".equals(k)) {
                MemberRIB rib = member.getRIB();
                if (!value.equals(rib.getBank())) {
                    rib.setBank(value);
                    member.setRIB(rib);
                    em.merge(rib);
                    em.merge(member);
                    bankingUpdated = true;
                }
            } else if ("ribBranch".equals(k)) {
                MemberRIB rib = member.getRIB();
                if (!value.equals(rib.getBranch())) {
                    rib.setBranch(value);
                    member.setRIB(rib);
                    em.merge(rib);
                    em.merge(member);
                    bankingUpdated = true;
                }
            } else if ("ribNumber".equals(k)) {
                MemberRIB rib = member.getRIB();
                if (!value.equals(rib.getNumber())) {
                    rib.setNumber(value);
                    member.setRIB(rib);
                    em.merge(rib);
                    em.merge(member);
                    bankingUpdated = true;
                }
            } else if ("ribKey".equals(k)) {
                MemberRIB rib = member.getRIB();
                if (!value.equals(rib.getKey())) {
                    rib.setKey(value);
                    member.setRIB(rib);
                    em.merge(rib);
                    em.merge(member);
                    bankingUpdated = true;
                }
            } else if ("ibanNumber".equals(k)) {
                MemberIBAN iban = member.getIBAN();
                if (!value.equals(iban.getIBAN())) {
                    iban.setIBAN(value);
                    member.setIBAN(iban);
                    em.merge(iban);
                    em.merge(member);
                    bankingUpdated = true;
                }
            } else if ("ibanSwift".equals(k)) {
                MemberIBAN iban = member.getIBAN();
                if (!value.equals(iban.getSwift())) {
                    iban.setSwift(value);
                    member.setIBAN(iban);
                    em.merge(iban);
                    em.merge(member);
                    bankingUpdated = true;
                }
            } else if ("maxAmountBetWeekly".equals(k)) {
                MemberCredit credit = member.getMemberCredit();
                credit.setMaxAmountPerWeekForBets(Float.valueOf(value));
                member.setMemberCredit(credit);
                em.merge(credit);
                em.merge(member);
            } else if ("maxAmountCreditWeekly".equals(k)) {
                MemberCredit credit = member.getMemberCredit();
                credit.setMaxAmountPerWeekCredit(Float.valueOf(value));
                member.setMemberCredit(credit);
                em.merge(credit);
                em.merge(member);
            } else if ("idFilename".equals(k)) {
                if (value != null && !"".equals(value)) {
                    member.setIDFileName(value);
                }
            } else if ("ribFilename".equals(k)) {
                if (value != null && !"".equals(value)) {
                    member.setRIBFileName(value);
                }
            } else if ("activationKey".equals(k)) {
                // Gambling account pending activation code.
                if (MemberAccountType.GAMBLING_FR.equals(member
                        .getAccountType())
                        && MemberAccountStatus.PENDING_VERIFIED_FR
                                .equals(member.getAccountStatus())) {
                    // Check activation key.
                    if (value != null
                            && value.equals(member.getActivationKey())) {
                        log.info("Activation key is matching: account with email="
                                + member.getEmail() + " has been verified!");
                        member.setAccountStatus(MemberAccountStatus.VERIFIED_FR);
                        member.setGamblingFRAccountActivationDate(new Date());

                    }
                }
            } else if ("autoExclusion".equals(k)) {
                if (value != null) {
                    final int days = Integer.valueOf(value);
                    member.setMemberSuspendedDay(days);
                    if (days == -1) {
                        // Member is suspending himself definitely.
                        member.setAccountStatus(MemberAccountStatus.MEMBER_SUSPENDED);
                    } else if (days > 0) {
                        // Member is suspending himself for a certain amount of
                        // days.
                        member.setAccountStatus(MemberAccountStatus.MEMBER_SUSPENDED);
                    }
                    log.info("Member with email=" + member.getEmail()
                            + " suspended himeself for =" + days);
                }
            } else if ("maxAmountAutomaticWire".equals(k)) {
                if (value != null) {
                    MemberCredit credit = member.getMemberCredit();
                    credit.setAutomaticWireAmount(Integer.valueOf(value));
                    member.setMemberCredit(credit);
                    em.merge(credit);
                    em.merge(member);
                }
            } else if ("title".equals(k)) {
                if (value != null) {
                    member.setTitle(value);
                }
            } else if ("gender".equals(k)) {
                if (value != null) {
                    member.setGender(Integer.valueOf(value));
                }
            } else if ("policy_acceptance".equals(k)) {
                if (value != null) {
                    member.setPolicyAcceptanceDate(new Date());
                }
            } else {
                log.debug("Member field " + k + " not recognized.");
            }
        }

        //
        // We need to change the status if the account is of French gambling
        // account and the address and / or banking information changed since we
        // need to send a new activation key to the new address.
        //

        if (addressUpdated || bankingUpdated) {
            if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())
                    && MemberAccountStatus.VERIFIED_FR.equals(member
                            .getAccountStatus())) {
                member.setAccountStatus(MemberAccountStatus.PENDING_VERIFIED_FR);
                // Reset activation code.
                member.setActivationKey(null);
                log.info("Banking or Address information changed from member "
                        + " with id=" + member.getId()
                        + " status changed back to PENDING_VERIFIED_FR."
                        + " A new activation code will be sent out.");
            }
        }

        //
        // Switch account status to pending verified if member submitted both
        // files in the case if a French gambling account.
        //

        if (member.getIDFileName() != null && member.getRIBFileName() != null) {
            if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())
                    && MemberAccountStatus.CONFIRMED.equals(member
                            .getAccountStatus())) {
                member.setAccountStatus(MemberAccountStatus.PENDING_VERIFIED_FR);
            }
        }

        member.setLastModified(new Date());
        return Response.status(202).entity(new ReSTMember(member)).build();
    }

    @Override
    public Response registerFacebook(Map<String, String> params)
            throws ReSTException {

        final String facebookId = params.get("facebookId");
        final String accessToken = params.get("accessToken");
        final String birthDate = params.get("birthdate");
        final String email = params.get("email");
        final long communityId = Long.valueOf(params.get("communityId"));

        boolean memberExists = true;

        // Lookup using Facebook ID
        Member member = null;
        try {
            member = getCoreMemberByFacebookId(facebookId);
        } catch (ReSTException re) {
            // 400 not found
            if (400 != re.getStatusCode()) {
                throw re;
            }
        }

        if (member == null) {
            // Lookup using email
            try {
                member = getCoreMemberByEmail(email);
            } catch (ReSTException re) {
                // 400 not found
                if (400 != re.getStatusCode()) {
                    throw re;
                }
            }
            if (member == null) {
                try {
                    member = new MemberImpl(email,
                            MemberAccountStatus.CONFIRMED,
                            MemberAccountType.SIMPLE);
                    memberExists = false;
                } catch (CoreException e) {
                    throw new ReSTRuntimeException(500, e.getMessage());
                }
            }
        }

        member.setFacebookId(facebookId);

        Community c = getCoreCommunityById(communityId);

        if (memberExists
                && !member.getMemberTeams().contains(c.getDefaultTeam())) {
            // c.addMember(member);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
            final Date facebookBirthdate = formatter.parse(birthDate);
            if (facebookBirthdate != null) {
                final Date today = new Date();
                final long diffTo16 = ((today.getTime() - facebookBirthdate
                        .getTime()) / (1000 * 60 * 60 * 24)) / 365;
                if (diffTo16 < CoreConstants.SIMPLE_ACCOUNT_MIN_AGE) {
                    return Response.status(400)
                            .entity("You must be at least 16 to register!")
                            .build();
                }
                member.setBirthDate(facebookBirthdate);
            } else {
                return Response.status(400)
                        .entity("You must be at least 16 to register!").build();
            }
        } catch (ParseException e) {
            throw new ReSTValidationException(500, e.getMessage());
        }

        if (email != null) {
            // Update email if facebook member updated it.
            try {
                member.setEmail(email);
            } catch (CoreException e) {
                throw new ReSTRuntimeException(500, e.getMessage());
            }
        }

        if (!memberExists) {
            try {
                getMemberService().createMember(member);
                log.info("New Member with email=" + email
                        + " joined community with name=" + c.getName());
                // member = c.addMember(member);
                // Track member creation (partners)
                trackMember(params, member);
            } catch (CoreException e) {
                throw new ReSTRuntimeException(500, e.getMessage());
            }
        }

        if (accessToken != null) {
            member.setFacebookToken(accessToken);
            getMessagingService().sendMessage(member.getEmail(),
                    SofunMessagingDestination.SOFUN_FACEBOOK);

        }

        // XXX move this at lower level
        List<String> args = new ArrayList<String>();
        pushToFeed(c.getDefaultTeam(), member,
                FeedEntryType.NEW_COMMUNITY_MEMBER, args);
        return Response.status(202).entity(new ReSTMember(member)).build();

    }

    @Override
    public Response loginFacebook(Map<String, String> params)
            throws ReSTException {

        final String facebookId = params.get("facebookId");
        final String accessToken = params.get("accessToken");
        final String birthDate = params.get("birthdate");
        final long communityId = Long.valueOf(params.get("communityId"));

        Member member = getCoreMemberByFacebookId(facebookId);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        // Check ARJEL banned players list in case of a Gambling player.
        if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())) {
            // checkBanned(member, true);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
            final Date facebookBirthdate = formatter.parse(birthDate);
            if (facebookBirthdate != null) {
                if (member.getBirthDate() == null) {
                    // We only update if no date have been entered previously.
                    // Case of gambling account linked to FB afterwords.
                    // Verification has been already performed at registration
                    // time.
                    member.setBirthDate(facebookBirthdate);
                }
            }
        } catch (ParseException e) {
            return Response
                    .status(500)
                    .entity("An error occurred trying to reach Facebook."
                            + " Please try again.").build();
        }

        if (accessToken != null) {
            member.setFacebookToken(accessToken);
            // XXX fix messaging performances before enabling this.
            // getMessagingService().sendMessage(member.getEmail(),
            // SofunMessagingDestination.SOFUN_FACEBOOK);

        }

        // XXX move this at lower level
        Community c = getCoreCommunityById(communityId);
        List<String> args = new ArrayList<String>();
        pushToFeed(c.getDefaultTeam(), member, FeedEntryType.MEMBER_LOGGED_IN,
                args);
        return Response.status(202).entity(new ReSTMember(member)).build();

    }

    @Override
    public Response creditMember(Map<String, String> params)
            throws ReSTException {

        final String email = params.get("email");
        Member member = getCoreMemberByEmail(email);

        final float txnAmount = Float.valueOf(params.get("txn_amount"));
        final String txnCurrency = params.get("txn_currency");
        final String txnDate = params.get("txn_date");

        if (txnAmount == 0 || txnCurrency == null || txnDate == null) {
            return Response.status(401).entity("Missing information.").build();
        }

        // XXX verify user specified credit limit here. Application level
        // responsibility for the moment
        final String txnId = params.get("txn_id");
        final String auth = params.get("auth_num");
        final String status = params.get("txn_status");
        final String statusCode = params.get("txn_status_code");
        MemberTransaction txn = new MemberTransactionImpl(txnId, auth,
                new Date(), txnAmount, txnCurrency,
                MemberTransactionType.CC_CREDIT);
        txn.setStatusCode(statusCode);
        txn.setStatus(status);

        // Always CC OP when using API. Other transactions @ backend side.
        txn.setLabel(MemberTransactionType.CC_CREDIT);
        txn.setCredit(true);
        member.addTransaction(txn);
        txn.setMember(member);

        //
        // Bonus L1 (x2)
        //

        final Calendar createdGb = Calendar.getInstance();
        createdGb.setTime(member.getGamblingFRAccountCreationDate());
        final Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        long milliseconds1 = createdGb.getTimeInMillis();
        long milliseconds2 = now.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (txnAmount >= 5 && diffDays <= 11) {
            final String label = "L1 : Welcome Credit";
            boolean exists = false;
            // Check if this is the first one.
            if (getMemberService().getCreditHistory(member).size() < 1) {
                exists = true;
            }
            if (!exists) {
                // Ensure the player did not get it already
                for (MemberTransaction tx : getMemberService()
                        .getBonusCreditHistory(member)) {
                    if (label.equals(tx.getLabel())) {
                        exists = true;
                        break;
                    }
                }
            }
            if (!exists) {
                float bonusAmount = txnAmount;
                if (bonusAmount > 50) {
                    bonusAmount = 50;
                }
                MemberTransaction tx = new MemberTransactionImpl("0", "0",
                        new Date(), bonusAmount, CurrencyType.EURO,
                        MemberTransactionType.BONUS_CREDIT);
                tx.setStatusCode("00000");
                tx.setStatus(MemberTransactionStatus.APPROVED);
                tx.setLabel(label);
                tx.setCredit(true);
                tx.setBonus(true);
                member.addTransaction(tx);
                tx.setMember(member);
                log.info(label + " " + " for member with email="
                        + member.getEmail());
            }
        }

        return Response.status(202).entity("OK").build();
    }

    @Override
    public Response getMemberCreditHistory(String email) throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService()
                .getCreditHistory(member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response getMemberDebitHistory(String email) throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService().getDebitHistory(member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response getMemberBonusCreditHistory(String email)
            throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService().getBonusCreditHistory(
                member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response getMemberWireHistory(String email) throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService().getWireHistory(member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response getMemberTransactionHistory(String email)
            throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService().getTransactionHistory(
                member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response getMemberProperties(String email) throws ReSTException {
        Member member = getCoreMemberByEmail(email);

        Map<String, Serializable> properties = new HashMap<String, Serializable>();

        properties.put("email", member.getEmail());
        properties.put("fisrtName", member.getFirstName()); // BBB
        properties.put("firstName", member.getFirstName());
        properties.put("middleName", member.getMiddleNames());
        properties.put("lastName", member.getLastName());
        properties.put("nickName", member.getNickName());
        properties.put("birthDate", member.getBirthDate());
        properties.put("created", member.getCreated());
        properties.put("modified", member.getModified());
        properties.put("facebookId", member.getFacebookId());
        properties.put("gender", member.getGender());
        properties.put("title", member.getTitle());
        properties.put("locale", member.getLocale());
        properties.put("location", member.getLocation());
        properties.put("account_type", member.getAccountType());
        properties.put("account_status", member.getAccountStatus());
        properties
                .put("policyAcceptanceDate", member.getPolicyAcceptanceDate());
        properties
                .put("last_login", getMemberService().getLastLoginFor(member));

        if (member.getFacebookId() != null) {
            FacebookMemberInfo fbInfo = getFacebookService()
                    .getFacebookMemberInfo(member);
            properties.put("facebookName", fbInfo.getFullName());
        }

        // SHA-512 hash
        properties.put("password", member.getPassword());

        if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())) {
            properties.put("birth_area", member.getBirthArea());
            properties.put("birth_country", member.getBirthCountry());
            properties.put("birth_place", member.getBirthPlace());
            properties.put("address_city", member.getPostalAddress().getCity());
            properties.put("address_zip", member.getPostalAddress()
                    .getZipCode());
            properties.put("address_street", member.getPostalAddress()
                    .getStreet());
            properties.put("address_country", member.getPostalAddress()
                    .getCountry());
            properties.put("address_state", member.getPostalAddress()
                    .getState());
            properties.put("iban_number", member.getIBAN().getIBAN());
            properties.put("iban_swift", member.getIBAN().getSwift());
            properties.put("rib_bank", member.getRIB().getBank());
            properties.put("rib_branch", member.getRIB().getBranch());
            properties.put("rib_key", member.getRIB().getKey());
            properties.put("rib_number", member.getRIB().getNumber());
            properties.put("credit", member.getMemberCredit().getCredit());
            properties.put("transferable_credit", getMemberService()
                    .getTransferableAmountFor(member));
            properties.put("credit_currency", member.getMemberCredit()
                    .getCurrency());
            properties.put("max_amount_bet_weekly", member.getMemberCredit()
                    .getMaxAmountPerWeekForBets());
            properties.put("max_amount_credit_weekly", member.getMemberCredit()
                    .getMaxAmountPerWeekCredit());
            properties.put("max_amount_automatic_wire", member
                    .getMemberCredit().getAutomaticWireAmount());
            properties.put("auto_exclusion", member.getMemberSuspendedDays());
            properties.put("filename_id", member.getIDFileName());
            properties.put("filename_rib", member.getRIBFileName());

            final float currentTotalBets = getMemberService()
                    .getTotalCurrentBetAmountFor(member);
            properties.put("bets", currentTotalBets);

            // Must member accept policy?
            final boolean mustAcceptPolicy = getMemberService()
                    .mustMemberAcceptPolicy(member);
            if (mustAcceptPolicy == true) {
                properties.put("mustAcceptPolicy", "true");
            }

            // Check for unacked bonus
            List<MemberTransaction> txns = getMemberService()
                    .getUnAckBonusTransactionFor(member);
            if (txns.size() > 0) {
                properties.put("bonusTxn", "true");
            }

            // Check for unacked winnings
            txns = getMemberService().getUnAckWinningsTransactionsFor(member);
            if (txns.size() > 0) {
                properties.put("winningsTxn", "true");
            }
        }

        return Response.status(202).entity(properties).build();
    }

    @Override
    public Response closeRequest(String email) throws ReSTException {
        Member member = getCoreMemberByEmail(email);
        member.setAccountStatus(MemberAccountStatus.MEMBER_SUSPENDED);
        member.setLastModified(new Date());
        return Response.status(202).entity(new ReSTMember(member)).build();
    }

    @Override
    public Response debitMember(Map<String, String> params)
            throws ReSTException {

        final String email = params.get("email");

        Member member = getCoreMemberByEmail(email);
        if (!MemberAccountStatus.VERIFIED_FR.equals(member.getAccountStatus())) {
            return Response.status(401)
                    .entity("Account has not been verified.").build();
        }

        final float txnAmount = Float.valueOf(params.get("txn_amount"));
        final String txnCurrency = params.get("txn_currency");

        if (txnAmount == 0 || txnCurrency == null) {
            return Response.status(401).entity("Missing information.").build();
        }

        if (getMemberService().getTransferableAmountFor(member) < txnAmount) {
            return Response.status(401)
                    .entity("Balance is too low for such a wire.").build();
        }

        SecureRandom randomGenerator = new SecureRandom();
        MemberTransaction txn = new MemberTransactionImpl(new Date(),
                txnAmount, txnCurrency, MemberTransactionType.WIRE_DEBIT);
        txn.setLabel(MemberTransactionType.WIRE_DEBIT);
        txn.setDebit(true);
        txn.setCredit(false);
        // txnId will be updated after the actual bank transaction will be
        // performed out of process.
        txn.setTransactionId(String.valueOf(randomGenerator.nextInt(1000000000)));
        member.addTransaction(txn);
        txn.setMember(member);

        return Response.status(202).entity("OK").build();

    }

    @Override
    public Response getMemberDebitBetHistory(String email) throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService().getBetDebitHistory(
                member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response getMemberDebitCurrentBetAmount(String email)
            throws ReSTException {
        Member member = getCoreMemberByEmail(email);
        float amount = getMemberService().getTotalCurrentBetAmountFor(member);
        return Response.status(202).entity(amount).build();
    }

    @Override
    public Response getMemberCreditBetHistory(String email)
            throws ReSTException {

        Member member = getCoreMemberByEmail(email);

        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService().getBetCreditHistory(
                member)) {
            txns.add(new ReSTTransaction(txn));
        }

        return Response.status(202).entity(txns).build();

    }

    @Override
    public Response existsFacebookId(String facebookId) throws ReSTException {
        if (getMemberService().facebookIdExists(facebookId)) {
            return Response.status(202).entity(true).build();
        } else {
            return Response.status(400).entity(false).build();
        }
    }

    @Override
    public Response getMemberNotificationsScheme(String email)
            throws ReSTException {
        Member member = getCoreMemberByEmail(email);
        final Map<String, Boolean> scheme = getNotificationService()
                .getNotificationsSchemeFor(member);
        return Response.status(202).entity(scheme).build();
    }

    @Override
    public Response setMemberNotificationsScheme(String email,
            Map<String, Boolean> scheme) throws ReSTException {
        Member member = getCoreMemberByEmail(email);
        getNotificationService().setNotificationSchemeFor(member, scheme);
        return getMemberNotificationsScheme(email);
    }

    @Override
    public Response passwordForgotten(String email, Map<String, String> params)
            throws ReSTException {

        if (email == null) {
            return Response.status(400).entity("Email is not valid.").build();
        }

        Member member;
        try {
            member = getCoreMemberByEmail(email);
        } catch (ReSTRuntimeException e) {
            if (e.getStatusCode() == 400) {
                return Response.status(400).entity("Member not found.").build();
            } else {
                throw e;
            }
        }

        URL redirect = null;
        try {
            redirect = new URL(params.get("redirect_url"));
        } catch (MalformedURLException e1) {
            return Response.status(400).entity("Redirect URL is not valid")
                    .build();
        }

        try {
            getMemberService().passwordForgotten(member, redirect);
        } catch (CoreException e) {
            throw new ReSTRuntimeException(e.getMessage());
        }

        return Response.status(202).entity("OK").build();
    }

    @Override
    public Response passwordReset(String email, Map<String, String> params)
            throws ReSTException {

        Member member;
        try {
            member = getCoreMemberByEmail(email);
        } catch (ReSTRuntimeException e) {
            if (e.getStatusCode() == 400) {
                return Response.status(400).entity("Member not found.").build();
            } else {
                throw e;
            }
        }

        final String token = params.get("token");
        if (!getMemberService().passwordVerifyResetToken(member, token)) {
            return Response.status(401)
                    .entity("Password Reset Token Is Invalid.").build();
        }

        final String password = params.get("password");
        // 128Bytes = 512 bits
        if (password.getBytes().length < 128) {
            return Response.status(400)
                    .entity("128 bytes hashed passwords are mandatory.")
                    .build();
        }

        try {
            getMemberService().passwordReset(member, password, token);
        } catch (CoreException e) {
            throw new ReSTRuntimeException(e.getMessage());
        }

        return Response.status(202).entity("Password has been reset.").build();
    }

    @Override
    public Response linkFacebook(Map<String, String> params)
            throws ReSTException {

        final String facebookId = params.get("facebookId");
        final String accessToken = params.get("accessToken");
        final String email = params.get("email");
        final long communityId = Long.valueOf(params.get("communityId"));

        boolean memberExists = true;

        // Lookup using Facebook ID
        Member member = null;
        try {
            member = getCoreMemberByFacebookId(facebookId);
        } catch (ReSTException re) {
            // 400 not found
            if (400 != re.getStatusCode()) {
                throw re;
            }
        }

        if (member == null) {
            // Lookup using email
            try {
                member = getCoreMemberByEmail(email);
            } catch (ReSTException re) {
                // 400 not found
                if (400 != re.getStatusCode()) {
                    throw re;
                }
            }
            if (member == null) {
                try {
                    member = new MemberImpl(email,
                            MemberAccountStatus.CONFIRMED,
                            MemberAccountType.SIMPLE);
                    memberExists = false;
                } catch (CoreException e) {
                    throw new ReSTRuntimeException(500, e.getMessage());
                }
            }
        }

        member.setFacebookId(facebookId);

        Community c = getCoreCommunityById(communityId);

        if (memberExists
                && !member.getMemberTeams().contains(c.getDefaultTeam())) {
            // c.addMember(member);
        }

        if (!memberExists) {
            try {
                getMemberService().createMember(member);
                log.info("New Member with email=" + email
                        + " joined community with name=" + c.getName());
                // member = c.addMember(member);
            } catch (CoreException e) {
                throw new ReSTRuntimeException(500, e.getMessage());
            }
        }

        if (accessToken != null) {
            member.setFacebookToken(accessToken);
            getMessagingService().sendMessage(member.getEmail(),
                    SofunMessagingDestination.SOFUN_FACEBOOK);

        }

        return Response.status(202).entity(new ReSTMember(member)).build();
    }

    @Override
    public Response unlinkFacebook(Map<String, String> params)
            throws ReSTException {

        final String facebookId = params.get("facebookId");
        final String email = params.get("email");

        // Lookup using Facebook ID
        Member member = null;
        try {
            member = getCoreMemberByFacebookId(facebookId);
            if (!member.getEmail().equals(email)) {
                return Response.status(400)
                        .entity("Email and Facebook ID do not match.").build();
            }
        } catch (ReSTException re) {
            // 400 not found
            if (400 != re.getStatusCode()) {
                throw re;
            }
            return Response.status(400)
                    .entity("No member found using this Facebook ID").build();
        }

        member.setFacebookId(null);
        member.setFacebookToken(null);

        return Response.status(202).entity(new ReSTMember(member)).build();

    }

    @Override
    public Response getMemberPredictionsHistory(String email, String type,
            int offset, int batch) throws ReSTException {

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        // XXX handle type as a query parameter
        List<Prediction> corePredictions = getPredictionService()
                .getPredictionsFor(member, offset, batch);
        List<ReSTPrediction> predictions = new ArrayList<ReSTPrediction>();
        for (Prediction corePrediction : corePredictions) {
            predictions.add(new ReSTPrediction(corePrediction));
        }

        return Response.status(202).entity(predictions).build();

    }

    @Override
    public Response getMemberKupsParticipationHistory(String email,
            String status, int offset, int batch) throws ReSTException {

        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("template", "all");
        params.put("status", status);
        params.put("offset", String.valueOf(offset));
        params.put("batchSize", String.valueOf(batch));

        List<Kup> coreKups = new ArrayList<Kup>();
        try {
            KupSearchResults res = getKupService().search(params);
            if (res != null) {
                coreKups.addAll(res.getResults());
            }
        } catch (CoreException e) {
            log.error(e.getMessage());
            return Response.status(500).entity("Internal error...").build();
        }

        List<ReSTMemberKup> kups = new ArrayList<ReSTMemberKup>();
        for (Kup coreKup : coreKups) {
            final ReSTMemberKup kup = new ReSTMemberKup(coreKup, member);
            if (kup.getRanking() == 0) {
                // Entry might be null in case of gambling Kups where player did
                // record a prediction but did not place bet.
                continue;
            }
            kups.add(kup);
        }

        return Response.status(202).entity(kups).build();

    }

    @Override
    public Response passwordVerify(String email, String hash)
            throws ReSTException {
        Member member = getMemberService().getMember(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }
        boolean verified;
        try {
            verified = getMemberService().passwordVerify(member, hash);
        } catch (CoreException e) {
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            return Response.status(500).entity(e.getMessage()).build();
        }
        if (verified) {
            return Response.status(202).entity("OK").build();
        }
        return Response.status(401).entity("NOTOK").build();
    }

    @Override
    public Response getMemberUnAckBonusTransactions(String email)
            throws ReSTException {
        Member member = getCoreMemberByEmail(email);
        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService()
                .getUnAckBonusTransactionFor(member)) {
            ReSTTransaction rTxn = new ReSTTransaction(txn);
            Kup kup = getKupService().getKupByTransactionId(txn.getId());
            if (kup != null) {
                rTxn.setKupId(kup.getId());
                rTxn.setKupName(kup.getName());
            }
            txns.add(rTxn);
        }
        return Response.status(202).entity(txns).build();
    }

    @Override
    public Response getMemberUnAckWinningsTransactions(String email)
            throws ReSTException {
        Member member = getCoreMemberByEmail(email);
        List<ReSTTransaction> txns = new ArrayList<ReSTTransaction>();
        for (MemberTransaction txn : getMemberService()
                .getUnAckWinningsTransactionsFor(member)) {
            ReSTTransaction rTxn = new ReSTTransaction(txn);
            Kup kup = getKupService().getKupByTransactionId(txn.getId());
            if (kup != null) {
                rTxn.setKupId(kup.getId());
                rTxn.setKupName(kup.getName());
            }
            txns.add(rTxn);
        }
        return Response.status(202).entity(txns).build();
    }

    @Override
    public Response ackTransaction(Map<String, String> params) throws Exception {
        final String email = params.get("email");
        Member member = getCoreMemberByEmail(email);
        if (member == null) {
            return Response.status(400).entity("Member not found").build();
        }
        final long txnId = Long.valueOf(params.get("txnId"));
        MemberTransaction txn = getMemberService().getMemberTransactionById(
                member, txnId);
        txn.setAck(true);
        log.info("Member with email=" + email + " acknowledged txn with id="
                + txnId);
        return Response.status(202).entity(new ReSTTransaction(txn)).build();
    }

}
