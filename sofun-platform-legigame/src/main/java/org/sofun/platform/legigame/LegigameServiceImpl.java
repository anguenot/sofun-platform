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

package org.sofun.platform.legigame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.member.MemberAccountStatus;
import org.sofun.core.api.member.MemberAccountType;
import org.sofun.core.api.member.MemberService;
import org.sofun.core.api.member.ejb.MemberServiceLocal;
import org.sofun.core.member.MemberImpl;
import org.sofun.platform.legigame.api.Configuration;
import org.sofun.platform.legigame.api.LegigameService;
import org.sofun.platform.legigame.api.MemberLegigameStatus;
import org.sofun.platform.legigame.api.ResponseCreateOrUpdate;
import org.sofun.platform.legigame.api.ResponseRead;
import org.sofun.platform.legigame.api.ejb.LegigameServiceLocal;
import org.sofun.platform.legigame.api.ejb.LegigameServiceRemote;
import org.sofun.platform.legigame.api.exception.LegigameException;

/**
 * Legigame service implementation.
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(LegigameServiceLocal.class)
@Remote(LegigameServiceRemote.class)
public class LegigameServiceImpl implements LegigameService {

    private static final long serialVersionUID = 4828048491713892415L;

    private static final Log log = LogFactory.getLog(LegigameServiceImpl.class);

    private transient HttpClient httpClient;

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    protected transient EntityManager em;

    @EJB(
            beanName = "MemberServiceImpl",
            beanInterface = MemberServiceLocal.class)
    private MemberService members;

    private static final int LEGIGAME_TIMEOUT = 10000;

    public LegigameServiceImpl() {
    }

    public LegigameServiceImpl(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns an HTTP client.
     * 
     * @return an {@link HttpClient}
     */
    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new HttpClient();
            httpClient.getParams()
                    .setConnectionManagerTimeout(LEGIGAME_TIMEOUT);
            httpClient.getParams().setSoTimeout(LEGIGAME_TIMEOUT);
            httpClient.getParams().setAuthenticationPreemptive(true);
            httpClient.getParams().setParameter("http.useragent",
                    "Sofun-Platform/" + CoreConstants.VERSION);
            Credentials creds = new UsernamePasswordCredentials(Configuration
                    .getProperties().getProperty("httpauth.username"),
                    Configuration.getProperties().getProperty(
                            "httpauth.password"));
            httpClient.getState().setCredentials(AuthScope.ANY, creds);
        }
        return httpClient;
    }

    /**
     * Executes a POST method.
     * 
     * @param method: {@link PostMethod} instance
     * @return a String containing the response body
     */
    private String executeMethod(PostMethod method) {

        StringBuffer sb = new StringBuffer();

        HttpClient client = getHttpClient();
        BufferedReader br = null;
        try {
            client.executeMethod(method);
            br = new BufferedReader(new InputStreamReader(
                    method.getResponseBodyAsStream()));
            String readLine;
            while (((readLine = br.readLine()) != null)) {
                sb.append(readLine + "\n");
            }
        } catch (Exception e) {
            log.error("An error occured while trying to contact Legigame : "
                    + e.getMessage());
            if (log.isDebugEnabled()) {
                e.printStackTrace();
            }
            httpClient = null;
            return null;
        } finally {
            method.releaseConnection();
            if (br != null)
                try {
                    br.close();
                } catch (Exception fe) {
                    log.error(fe.getMessage());
                    if (log.isDebugEnabled()) {
                        fe.printStackTrace();
                    }
                }
        }
        return sb.toString();

    }

    /**
     * Prepares a POST request to create or update a given a {@link Member}
     * 
     * @param member: a {@link Member} instance
     * @param method: a {@link PostMethod} instance
     * @return a {@link PostMethod} instance with all required parameters.
     */
    private PostMethod getPostForCreateOrUpdate(Member member, PostMethod method) {

        method.addParameter("Login",
                Configuration.getProperties().getProperty("api.login"));
        method.addParameter("Password", Configuration.getProperties()
                .getProperty("api.password"));

        method.addParameter("PlayerID", String.valueOf(member.getId()));
        method.addParameter("Firstname", member.getFirstName());
        method.addParameter("Lastname", member.getLastName());

        final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
        method.addParameter("BirthDate", sf.format(member.getBirthDate()));
        method.addParameter("Email", member.getEmail());

        // IBAN will be used in priority
        if (member.getIBAN() != null && member.getIBAN().getIBAN() != null
                && member.getIBAN().getSwift() != null
                && member.getIBAN().getIBAN().length() > 0
                && member.getIBAN().getSwift().length() > 0) {
            method.addParameter("IBAN", member.getIBAN().getIBAN());
            method.addParameter("BIC", member.getIBAN().getSwift());
        } else if (member.getRIB() != null && member.getRIB().getBank() != null
                && member.getRIB().getBranch() != null
                && member.getRIB().getKey() != null
                && member.getRIB().getNumber() != null
                && member.getRIB().getBank().length() > 0
                && member.getRIB().getBranch().length() > 0
                && member.getRIB().getKey().length() > 0
                && member.getRIB().getNumber().length() > 0) {
            // Legigame does not support RIB.
            // We use must convert it to IBAN.
            final String iban = member.getRIB().toIban();
            method.addParameter("IBAN", iban);
            method.addParameter("BIC", "");
        } else {
            if (member.getAccountStatus().equals(
                    MemberAccountStatus.PENDING_VERIFIED_FR)) {
                log.error("No IBAN not RIB are available. "
                        + "Account is pending activation though...");
            }
        }

        if (member.getBirthPlace() != null) {
            method.addParameter("CityOfBirth", member.getBirthPlace());
        }
        if (member.getBirthArea() != null) {
            method.addParameter("DepartmentOfBirth", member.getBirthArea());
        }
        if (member.getBirthCountry() != null) {
            method.addParameter("CountryOfBirth", member.getBirthCountry());
        }
        if (member.getMobilePhone() != null) {
            method.addParameter("Mobile", member.getMobilePhone());
        }
        if (member.getActivationKey() != null) {
            method.addParameter("ActivationCode", member.getActivationKey());
        }
        if (member.getPostalAddress().getStreet() != null) {
            method.addParameter("Address", member.getPostalAddress()
                    .getStreet());
        }
        if (member.getPostalAddress().getCity() != null) {
            method.addParameter("City", member.getPostalAddress().getCity());
        }
        if (member.getPostalAddress().getZipCode() != null) {
            method.addParameter("ZipCode", member.getPostalAddress()
                    .getZipCode());
        }
        if (member.getPostalAddress().getCountry() != null) {
            method.addParameter("Country", member.getPostalAddress()
                    .getCountry());
        }

        // Sofun specifics
        if (member.getAccountType().equals(MemberAccountType.GAMBLING_FR)) {
            method.addParameter("Attribut1", String.valueOf("1"));
        } else {
            method.addParameter("Attribut1", String.valueOf("0"));
        }

        if (member.getAccountStatus().equals(MemberAccountStatus.CLOSED)
                || member.getAccountStatus().equals(
                        MemberAccountStatus.MEMBER_SUSPENDED)
                || member.getAccountStatus().equals(
                        MemberAccountStatus.ADMIN_SUSPENDED)
                || member.getAccountStatus().equals(
                        MemberAccountStatus.BANNED_FR)) {
            method.addParameter("ClosingDate", sf.format(member.getModified()));
        } else {
            method.addParameter("ClosingDate", "");
        }

        return method;

    }

    /**
     * Prepares a POST request to read the status of a given {@link Member}
     * 
     * @param member: a {@link Member} instance
     * @param method: a {@link PostMethod} instance
     * @return a {@link PostMethod} instance with all required parameters.
     */

    private PostMethod getPostForRead(Member member, PostMethod method) {

        method.addParameter("Login",
                Configuration.getProperties().getProperty("api.login"));
        method.addParameter("Password", Configuration.getProperties()
                .getProperty("api.password"));

        method.addParameter("PlayerID", String.valueOf(member.getId()));

        return method;

    }

    /**
     * Returns all members candidates for creation or update.
     * 
     * @return a {@link List} of {@link Member} instance
     * @throws CoreException
     */
    private List<Member> getCandidatesForCreateOrUpdate(int offset,
            int batchSize) throws LegigameException {
        List<Member> candidates = new ArrayList<Member>();
        for (Iterator<Member> i = members.listMembers(offset, batchSize); i
                .hasNext();) {
            Member member = i.next();
            MemberLegigameStatus status = getStatusFor(member);
            boolean candidate = false;
            if (status != null) {
                if (status.getUpdated().compareTo(member.getModified()) < 0) {
                    candidate = true;
                }
            } else {
                candidate = true;
            }
            if (candidate) {
                candidates.add(member);
                log.debug("Member with email=" + member.getEmail()
                        + " is candidate for create or update");
            }
        }
        return candidates;
    }

    /**
     * Returns all members candidates for status check.
     * 
     * <p>
     * 
     * We are only interested in pending activation members here.
     * 
     * @return a {@link List} of {@link Member} instance
     */
    @SuppressWarnings("unchecked")
    private List<Member> getCandidatesForRead() throws LegigameException {

        final List<String> status = new ArrayList<String>();
        status.add(MemberAccountStatus.CONFIRMED);
        status.add(MemberAccountStatus.PENDING_VERIFIED_FR);

        final String queryStr = "from " + MemberImpl.class.getSimpleName()
                + " m where m.type=:type AND m.status IN (:status)";
        final Query query = createQuery(queryStr);
        query.setParameter("type", MemberAccountType.GAMBLING_FR);
        query.setParameter("status", status);

        return query.getResultList();

    }

    /**
     * Creates or update a member record legigame side.
     * 
     * @param member: a {@link Member} instance
     */
    private ResponseCreateOrUpdate createOrUpdate(Member member,
            boolean forceUpdate) throws LegigameException {

        if (member.getAccountStatus().equals(
                MemberAccountStatus.PENDING_VERIFIED_FR)
                && !forceUpdate) {
            // This case is covered by the other timer (checkStatus())
            return null;
        }

        log.debug("Member with uuid=" + member.getId()
                + " scheduled for create or update @ legigame.");

        // Let's generate an activation code if none assigned to user yet.
        if (MemberAccountType.GAMBLING_FR.equals(member.getAccountType())
                && member.getActivationKey() == null) {
            final String key = ActivationCodeGenerator.get();
            member.setActivationKey(key);
            log.info("Adding activation key for member with uuid="
                    + member.getId());
        }

        final String postUrl = Configuration.getProperties().getProperty(
                "api.url.createOrUpdate");

        PostMethod method = new PostMethod(postUrl);
        method = getPostForCreateOrUpdate(member, method);

        String xmlResponse = executeMethod(method);
        if (xmlResponse == null) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug(xmlResponse);
        }

        ResponseCreateOrUpdate resp = new ResponseCreateOrUpdate(xmlResponse);

        MemberLegigameStatus status = getStatusFor(member);
        if (status == null) {
            status = new MemberLegigameStatusImpl();
            status.setMember(member);
            em.persist(status);
        }
        status.setDocumentStatus(String.valueOf(resp.getStatus()));
        status.setIteration(resp.getIteration());
        status.setLastUpdated(Calendar.getInstance().getTime());
        log.debug("Legigame status for member: uuid=" + member.getId()
                + " document status=" + status.getDocumentStatus()
                + " iteration=" + status.getIteration());

        return resp;

    }

    /**
     * Request Legigame for a member record.
     * 
     * @param member: a {@link Member} instance
     * @return a {@link ResponseRead} instance
     * @throws LegigameException
     */
    private ResponseRead getLegiMember(Member member) throws LegigameException {

        final String postUrl = Configuration.getProperties().getProperty(
                "api.url.read");

        PostMethod method = new PostMethod(postUrl);
        method = getPostForRead(member, method);

        String xmlResponse = executeMethod(method);
        if (xmlResponse == null) {
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug(xmlResponse);
        }
        return new ResponseRead(xmlResponse);

    }

    /**
     * Checks the status for a given member and update properties.
     * 
     * @param member: a {@link Member} instance
     */
    private void checkStatus(Member member) throws LegigameException {

        log.debug("Member with uuid=" + member.getId()
                + " scheduled for update checks @ legigame.");

        MemberLegigameStatus status = getStatusFor(member);
        boolean candidate = false;
        if (status == null) {
            createOrUpdate(member, true);
        } else {
            /*
             * if ("44".equals(status.getDocumentStatus())) { // Document
             * validated no need to reupload return; }
             */
            if (status.getUpdated().compareTo(member.getModified()) < 0) {
                createOrUpdate(member, true);
            }
            candidate = true;
        }
        if (candidate) {
            final ResponseRead resp = getLegiMember(member);
            if (resp == null) {
                log.error("An error occurred while retrieving member with email="
                        + member.getEmail());
                return;
            }
            status.setDocumentStatus(String.valueOf(resp.getStatus()));
            final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                final Date legiLastModified = sf.parse(resp.getProperties()
                        .get("DateLastIteration"));
                status.setLastUpdated(legiLastModified);
            } catch (ParseException e) {
                throw new LegigameException(e);
            }
        }

    }

    @Override
    public void syncMembers(int offset, int batchSize) throws LegigameException {
        List<Member> members = getCandidatesForCreateOrUpdate(offset, batchSize);
        for (Member member : members) {
            createOrUpdate(member, false);
        }
    }

    @Override
    public void syncStatus() throws LegigameException {
        for (Member member : getCandidatesForRead()) {
            checkStatus(member);
        }
    }

    /**
     * Returns a JPA query instance form a query string.
     * 
     * @param queryStr: a query string
     * @return a {@link Query} instance
     */
    private Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    /**
     * Returns the legigame status for a given member.
     * 
     * @param member: a {@link Member} instance
     * @return a {@link MemberLegigameStatus} instance
     */
    private MemberLegigameStatus getStatusFor(Member member) {
        String queryStr = "from "
                + MemberLegigameStatusImpl.class.getSimpleName()
                + " s where s.member.id=:member_id";
        Query query = createQuery(queryStr);
        query.setParameter("member_id", member.getId());
        try {
            return (MemberLegigameStatus) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
