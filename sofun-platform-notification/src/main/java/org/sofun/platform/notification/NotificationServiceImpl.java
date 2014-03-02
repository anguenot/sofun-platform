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

package org.sofun.platform.notification;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.sofun.core.CoreConstants;
import org.sofun.core.api.exception.CoreException;
import org.sofun.core.api.local.NotificationServiceLocal;
import org.sofun.core.api.member.Member;
import org.sofun.core.api.notification.NotificationService;
import org.sofun.core.api.remote.NotificationServiceRemote;
import org.sofun.platform.notification.api.MemberNotification;

/**
 * Notification Service Implementation
 * 
 * @author <a href="mailto:julien@anguenot.org">Julien Anguenot</a>
 * 
 */
@Stateless
@Local(NotificationServiceLocal.class)
@Remote(NotificationServiceRemote.class)
public class NotificationServiceImpl implements NotificationService {

    private static final long serialVersionUID = 1515902405562945152L;

    private static final Log log = LogFactory
            .getLog(NotificationServiceImpl.class);

    private static final String TEMPLATE_PROPERTIES = "/mail-templates/templates.properties";

    private static final String[] LOCALES = new String[] { "en", "fr" };

    @PersistenceContext(unitName = CoreConstants.PERSISTENCE_UNIT)
    private transient EntityManager em;

    @Resource(mappedName = "java:Mail")
    private transient Session mailer;

    private transient VelocityEngine velocityEngine;

    private transient StringResourceRepository templateRepository;

    public NotificationServiceImpl() {
        super();
    }

    public NotificationServiceImpl(EntityManager em) {
        this();
        this.em = em;
    }

    protected Query createQuery(String queryStr) {
        Query query = em.createQuery(queryStr);
        return query;
    }

    @Override
    public Map<String, Boolean> getNotificationsSchemeFor(Member member) {

        String queryStr = "from "
                + MemberNotificationImpl.class.getSimpleName()
                + " l where l.member.email =:email";
        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());

        Map<String, Boolean> scheme = null;
        try {
            scheme = ((MemberNotification) query.getSingleResult()).getScheme();
        } catch (NoResultException nre) {
            MemberNotification conf = new MemberNotificationImpl(member);
            em.persist(conf);
            scheme = conf.getScheme();
        }

        return scheme;

    }

    @Override
    public void setNotificationSchemeFor(Member member,
            Map<String, Boolean> scheme) {

        String queryStr = "from "
                + MemberNotificationImpl.class.getSimpleName()
                + " l where l.member.email =:email";
        Query query = createQuery(queryStr);
        query.setParameter("email", member.getEmail());

        MemberNotification notificationScheme = null;
        try {
            notificationScheme = (MemberNotification) query.getSingleResult();
        } catch (NoResultException nre) {
            notificationScheme = new MemberNotificationImpl(member);
            em.persist(notificationScheme);
        }

        Map<String, Boolean> current = notificationScheme.getScheme();
        for (Map.Entry<String, Boolean> entry : scheme.entrySet()) {
            current.put(entry.getKey(), entry.getValue());
        }
        notificationScheme.setScheme(current);

        log.debug("Notification scheme for member=" + member.getEmail()
                + " has been updated");

    }

    @Override
    public void sendEmail(Member member, Map<String, String> params)
            throws CoreException {

        final String emailsStr = params.get("emails");
        String[] emails = null;
        if (emailsStr != null) {
            emails = params.get("emails").split(",");
        }

        String format = params.get("format");
        if (format == null) {
            format = "text/html";
        }
        Message message = new MimeMessage(mailer);

        final String encodingOptions = format + "; charset=UTF-8";
        try {
            message.setHeader("Content-Type", encodingOptions);

            String from = params.get("from");
            if (from == null || from.equals("")) {
                message.setFrom(new InternetAddress(
                        CoreConstants.SOFUN_MAIL_FROM));
            } else {
                message.setFrom(new InternetAddress(from));
            }

            InternetAddress to[] = null;
            if (emails == null) {
                to = new InternetAddress[1];
                to[0] = new InternetAddress(member.getEmail());
            } else {
                to = new InternetAddress[emails.length];
                for (int i = 0; i < emails.length; i++) {
                    to[i] = new InternetAddress(emails[i]);
                }
            }

            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(params.get("subject"));

            String body = params.get("body");
            if (body != null) {
                body = body.replaceAll("(\\r|\\n)", "<p/>");
            }

            VelocityEngine ve = null;
            try {
                ve = getVelocityEngine();
            } catch (Exception e) {
                throw new CoreException(e.getMessage());
            }

            Template t = ve.getTemplate(params.get("templateId"));
            StringWriter writer = new StringWriter();

            VelocityContext context = new VelocityContext();

            // Add Member in velocity context.
            context.put("member", member);
            context.put("body", body);

            // Add all incoming params in velocity context.
            for (Map.Entry<String, String> entry : params.entrySet()) {
                context.put(entry.getKey(), entry.getValue());
            }

            t.merge(context, writer);

            message.setContent(writer.toString(), encodingOptions);
            Transport.send(message);
            String logEmails;
            if (emails != null) {
                logEmails = emailsStr;
            } else {
                logEmails = member.getEmail();
            }
            log.info("Sending an email to=" + logEmails);
        } catch (MessagingException e) {
            log.error("Failed to send an email. See error below.");
            throw new CoreException(e.getMessage());
        }

    }

    /**
     * Load all templates at post construction time.
     * 
     * <p/>
     * 
     * All templates will then be available in memory whenever needed.
     * 
     * @throws Exception
     */
    @PostConstruct
    protected void loadNotificationTemplates() throws Exception {

        log.debug("Loading email templates in memory...");
        InputStreamReader utf8Reader = null;
        InputStream raw = null;
        try {

            log.debug("Loading email templates in memory...");
            Properties p = new Properties();

            // Get the templates properties file referencing all available
            // templates.
            raw = getClass().getResourceAsStream(TEMPLATE_PROPERTIES);
            utf8Reader = new InputStreamReader(raw, "UTF-8");
            if (utf8Reader != null) {
                p.load(raw);
            }

            // Initialize VE engine.
            getVelocityEngine();

            // Initialize the string resource loader
            templateRepository = StringResourceLoader.getRepository();

            // Load each templates for each locale.
            for (Object entry : p.keySet()) {

                final String tid = (String) entry;
                final String tname = p.getProperty(tid);

                for (String locale : LOCALES) {

                    final String templatePath = "/mail-templates/" + locale
                            + "/" + tname;
                    final String templateId = tid + "_" + locale;

                    String template;
                    try {
                        template = getTemplateFromJar(templatePath);
                    } catch (Exception e) {
                        continue;
                    }

                    if (template != null) {
                        templateRepository.putStringResource(templateId,
                                template);
                        log.debug("Loading template with path=" + templatePath);
                    } else {
                        log.error("Cannot find template with path="
                                + templatePath);
                    }

                }

            }

        } finally {
            if (utf8Reader != null) {
                utf8Reader.close();
            }
            if (raw != null) {
                raw.close();
            }
        }

    }

    /**
     * Gets a mail template content from within the JAR file.
     * 
     * @param templatePath: relative path to the template within the JAR file.
     *        For instance, "/mail-templates/fr/betkup-welcome.vm".
     * @return the content of the template as a {@link String}
     * @throws Exception
     */
    private String getTemplateFromJar(String templatePath) throws Exception {
        InputStream inStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inStream = getClass().getResourceAsStream(templatePath);
            streamReader = new InputStreamReader(inStream, "UTF-8");
            bufferedReader = new BufferedReader(streamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (streamReader != null) {
                streamReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Initializes and return the {@link VelocityEngine}.
     * 
     * <p/>
     * 
     * Initializes the velocity engine with properties. We should specify the
     * resource loader as string and the class for string.resource.loader in
     * properties
     * 
     * 
     * @param engine: a {@link Velocity} engine.
     * @return a {@link Velocity} engine.
     * @throws Exception
     */
    private VelocityEngine getVelocityEngine() throws Exception {

        if (velocityEngine == null) {

            Properties p = new Properties();
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");

            p.setProperty("resource.loader", "string");
            p.setProperty("string.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.StringResourceLoader");

            velocityEngine = new VelocityEngine();
            velocityEngine.init(p);

        }

        return velocityEngine;

    }

}
