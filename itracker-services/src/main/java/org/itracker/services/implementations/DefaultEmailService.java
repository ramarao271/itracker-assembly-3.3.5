/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.services.implementations;

import org.apache.log4j.Logger;
import org.itracker.services.ConfigurationService;
import org.itracker.services.EmailService;
import org.itracker.util.EmailAuthenticator;
import org.itracker.util.NamingUtilites;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class DefaultEmailService implements EmailService {

    private ConfigurationService configurationService;

    @Deprecated
    public static final String DEFAULT_FROM_ADDRESS = "itracker@localhost";
    @Deprecated
    public static final String DEFAULT_FROM_TEXT = "ITracker Notification System";
    @Deprecated
    public static final String DEFAULT_REPLYTO_ADDRESS = "itracker@localhost";
    @Deprecated
    public static final String DEFAULT_SMTP_HOST = "localhost";

    public static final String DEFAULT_SMTP_CHARSET = "ISO-8859-1";


    private Session session;

    private final Logger logger = Logger.getLogger(DefaultEmailService.class);
    private InternetAddress replyTo;
    private InternetAddress from;

    public void setConfigurationService(
            ConfigurationService configurationService) {
        if (null == configurationService) {
            throw new IllegalArgumentException("configuration service must not be null.");
        }
        if (null != this.configurationService) {
            throw new IllegalStateException("configuration service was already set.");
        }
        this.configurationService = configurationService;
    }

    /**
     * @deprecated use mailssession from JNDI instead
     */
    private void initMailsession(String smtpHost, String smtpUserid, String smtpPassword) {
        Authenticator smtpAuth = null;
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        // props.put("mail.mime.charset", this.smtpCharset);

        if (smtpUserid != null && smtpPassword != null) {
            smtpAuth = new EmailAuthenticator(
                    new PasswordAuthentication(smtpUserid,
                            smtpPassword));
        }
        this.session = Session.getInstance(props, smtpAuth);
    }

    private void initCharset(String smtpCharset) {
        if (null != smtpCharset) {
            try {
                new String(new byte[0], smtpCharset);
                this.session.getProperties().put("mail.mime.charset",
                        smtpCharset);
                logger.info("initCharset: cherset was initialized to "
                        + session.getProperty("mail.mime.charset"));
            } catch (UnsupportedEncodingException use) {
                logger
                        .warn(
                                "initCharset: unsupported smtp charset configured, ignoring",
                                use);
            }
        }
    }

    private void initFrom(String fromAddress, String fromText) {
        if (null != fromAddress) {
            try {
                this.from = new InternetAddress(fromAddress, fromText, readSmtpCharset(this.session));
                logger.info("initFrom: initialized from-address: " + this.from);
            } catch (UnsupportedEncodingException e) {
                logger.warn("initReplyTo: could not initialize reply-to (configured: " +
                        fromText + " <" + fromAddress + ">", e);
            }
        }
    }

    private void initReplyTo(String replyToAddress, String replyToText) {
        try {
            this.replyTo = new InternetAddress(replyToAddress, replyToText, readSmtpCharset(this.session));
            logger.info("initFrom: initialized reply-to: " + this.replyTo);
        } catch (UnsupportedEncodingException e) {
            logger.warn("initReplyTo: could not initialize reply-to (configured: " + replyToText + " <" + replyToAddress + ">", e);
        }
    }


    public void init() {

        if (this.configurationService == null) {
            throw new IllegalStateException("configuration service was not set.");
        }

        String fromAddress = configurationService.getProperty(
                "notification_from_address", DEFAULT_FROM_ADDRESS);
        String fromText = configurationService.getProperty("notification_from_text",
                DEFAULT_FROM_TEXT);
        String replyToAddress = configurationService.getProperty(
                "notification_replyto_address", DEFAULT_REPLYTO_ADDRESS);

        String smtpCharset = configurationService.getProperty(
                "notification_smtp_charset", DEFAULT_SMTP_CHARSET);

        String mailSessionLookupName = configurationService.getMailSessionLookupName();

        try {
            InitialContext ctx = new InitialContext();
            logger.debug("init: got Mailsession from Naming Context:" + NamingUtilites.lookup(ctx, mailSessionLookupName));


            this.session = (Session) NamingUtilites.lookup(ctx, mailSessionLookupName);
        } catch (NamingException e) {
            logger.warn("init: failed to get Mailsession from initial context.", e);
        }
        if (null == this.session) {
            logger
                    .warn("init: failed to lookup Session from JNDI lookup " + mailSessionLookupName + ", using manual session");
            String smtpHost = configurationService.getProperty(
                    "notification_smtp_host", DEFAULT_SMTP_HOST);
            String smtpUserid = configurationService.getProperty(
                    "notification_smtp_userid", null);
            String smtpPassword = configurationService.getProperty(
                    "notification_smtp_password", null);

            logger
                    .warn("init: setting up SMTP manually, no session-lookup found in configuration!");
            if ("".equals(smtpUserid) || "".equals(smtpPassword)) {
                smtpUserid = null;
                smtpPassword = null;
            }
            initMailsession(smtpHost, smtpUserid, smtpPassword);
            initCharset(smtpCharset);
            initFrom(fromAddress, fromText);

            if (logger.isDebugEnabled()) {
                logger.debug("init: From Address set to: " + fromAddress);
                logger.debug("init: From Text set to: " + fromText);
                logger.debug("init: ReplyTo Address set to: " + replyToAddress);
                logger.debug("init: SMTP server set to: " + smtpHost);
                logger.debug("init: SMTP userid set to: " + smtpUserid);
                logger.debug("init: SMTP password set to: " + smtpPassword);
            }
        } else {
            initCharset(smtpCharset);
            // use mailsession to initialize from.
            initFrom(fromAddress, fromText);
        }
        initReplyTo(replyToAddress, fromText);


    }

    /**
     * @param address
     * @param subject
     * @param msgText
     */
    public void sendEmail(String address, String subject, String msgText) {
        try {
            InternetAddress[] recipients = new InternetAddress[1];
            recipients[0] = new InternetAddress(address);

            sendEmail(recipients, subject, msgText);
        } catch (AddressException ex) {
            logger.warn(
                    "AddressException while sending email. caught. address was "
                            + address, ex);
        }
    }

    public void sendEmail(Set<InternetAddress> recipients, String subject,
                          String message) {

        InternetAddress[] recipientsArray = new ArrayList<InternetAddress>(recipients).toArray(new InternetAddress[new ArrayList<InternetAddress>(recipients).size()]);

        if (recipientsArray.length > 0) {
            this.sendEmail(recipientsArray, subject, message);
        }

    }

    /**
     * @deprecated use method with InetAddress[] addresses instead.
     */
    public void sendEmail(HashSet<String> addresses, String subject,
                          String msgText) {

        if (null == addresses || 0 == addresses.size()) {
            throw new IllegalArgumentException(
                    "No addresses in recipients set.");
        }

        try {

            ArrayList<InternetAddress> recipients = new ArrayList<InternetAddress>(
                    addresses.size());

            for (String address : addresses) {
                recipients.add(new InternetAddress(address));
            }

            sendEmail(recipients.toArray(new InternetAddress[recipients.size()]), subject,
                    msgText);
        } catch (AddressException ex) {
            logger.warn("AddressException while sending email.", ex);
        }
    }

    /**
     * @param address
     * @param subject
     * @param msgText
     */
    public void sendEmail(InternetAddress address, String subject,
                          String msgText) {
        this.sendEmail(new InternetAddress[]{address}, subject, msgText);
    }

    /**
     * @param recipients
     * @param subject
     * @param msgText
     */
    public void sendEmail(InternetAddress[] recipients, String subject,
                          String msgText) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("sendEmail: called with recipients: " + Arrays.asList(recipients) + ", subject: " + subject + ", msgText: " + subject);
            }
            if (null == this.session) {
                throw new IllegalStateException("session was not initialized.");
            }
            if (null == recipients || recipients.length < 1) {
                throw new IllegalArgumentException(
                        "at least one recipient must be specified.");
            }
            if (null == subject || subject.length() < 1) {
                throw new IllegalArgumentException("subject must be specified.");
            }
            if (null == msgText) {
                msgText = "Empty message";
            }

            MimeMessage msg = new MimeMessage(this.session);

            if (null != this.from) {
                msg.setFrom(this.from);
            }
            if (null != this.replyTo) {
                msg.setReplyTo(new InternetAddress[]{this.replyTo});
            }
            for (InternetAddress internetAddress : recipients) {
                try {
                    internetAddress.setPersonal(internetAddress.getPersonal(), readSmtpCharset(session));
                } catch (UnsupportedEncodingException e) {
                    logger.info("sendEmail: could not encode " + internetAddress + " using " + readSmtpCharset(session));
                }
            }
            msg.setRecipients(javax.mail.Message.RecipientType.TO, recipients);
            msg.setSubject(subject, readSmtpCharset(session));

            msg.setSentDate(new Date());

            msg.setContent(msgText, "text/plain; charset=\""
                    + readSmtpCharset(session) + "\"");

            Transport.send(msg);

        } catch (MessagingException me) {
            logger.warn("MessagingException while sending email, caught.", me);
        }
    }

    private String readSmtpCharset(Session session) {
        String charset = null == session ? null : session.getProperty("mail.mime.charset");
        if (null == charset) {
            charset = configurationService.getProperty(
                    "notification_smtp_charset", DEFAULT_SMTP_CHARSET);
        }
        return charset;
    }
}