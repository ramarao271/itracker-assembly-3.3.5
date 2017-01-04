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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.persistence.dao.IssueActivityDAO;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.NotificationDAO;
import org.itracker.services.EmailService;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.ProjectService;
import org.itracker.util.HTMLUtilities;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.mail.internet.InternetAddress;
import java.net.MalformedURLException;
import java.util.*;

public class NotificationServiceImpl implements NotificationService, ApplicationContextAware {
    public static final Integer DEFAULT_ISSUE_AGE = 30;


    private EmailService emailService;
    private NotificationDAO notificationDao;
    private ProjectService projectService;
    private IssueActivityDAO issueActivityDao;
    private IssueDAO issueDao;


    private String issueServiceName;

    private static final Logger logger = Logger
            .getLogger(NotificationServiceImpl.class);
    private IssueService issueService;
    private ApplicationContext applicationContext;

    public NotificationServiceImpl() {

        this.emailService = null;
        this.projectService = null;
        this.notificationDao = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public NotificationServiceImpl(EmailService emailService,
                                   ProjectService projectService, NotificationDAO notificationDao, IssueActivityDAO issueActivityDao, IssueDAO issueDao, IssueService issueService) {
        this();
        this.setEmailService(emailService);
        this.setProjectService(projectService);
        this.setNotificationDao(notificationDao);
        this.setIssueActivityDao(issueActivityDao);
        this.setIssueDao(issueDao);
        this.setIssueService(issueService);
    }

    public void sendNotification(Notification notification, Type type,
                                 String url) {

        if (logger.isDebugEnabled()) {
            logger.debug("sendNotification: called with notification: "
                    + notification + ", type: " + url + ", url: " + url);
        }
        if (null == notification) {
            throw new IllegalArgumentException("notification must not be null");
        }
        if (null == this.emailService || null == this.notificationDao) {
            throw new IllegalStateException("service not initialized yet");
        }
        if (type == Type.SELF_REGISTER) {
            this.handleSelfRegistrationNotification(notification.getUser()
                    .getLogin(), notification.getUser().getEmailAddress(), notification.getUser().getPreferences().getUserLocale(), url);
        } else {
            handleIssueNotification(notification.getIssue(), type, url);

        }

    }

    public void sendNotification(Issue issue, Type type, String baseURL) {
        if (logger.isDebugEnabled()) {
            logger.debug("sendNotification: called with issue: " + issue
                    + ", type: " + type + ", baseURL: " + baseURL);
        }
        handleIssueNotification(issue, type, baseURL);

    }

    public void setEmailService(EmailService emailService) {

        if (null == emailService)
            throw new IllegalArgumentException("email service must not be null");

        if (null != this.emailService) {
            throw new IllegalStateException("email service allready set");
        }
        this.emailService = emailService;

    }


    private void handleSelfRegistrationNotification(String login,
                                                    InternetAddress toAddress, String locale, String url) {
        if (logger.isDebugEnabled()) {
            logger
                    .debug("handleSelfRegistrationNotification: called with login: "
                            + login
                            + ", toAddress"
                            + toAddress
                            + ", url: "
                            + url);
        }
        try {

            if (toAddress != null && !"".equals(toAddress.getAddress())) {
                String subject = ITrackerResources
                        .getString("itracker.email.selfreg.subject", locale);
                String msgText = ITrackerResources.getString(
                        "itracker.email.selfreg.body", locale, new Object[]{login,
                                url + "/login.do"});
                emailService.sendEmail(toAddress, subject, msgText);
            } else {
                throw new IllegalArgumentException(
                        "To-address must be set for self registration notification.");
            }
        } catch (RuntimeException e) {
            logger.error("failed to handle self registration notification for "
                    + toAddress, e);
            throw e;
        }
    }

    /**
     * Method for internal sending of a notification of specific type.
     */
    private void handleIssueNotification(Issue issue, Type type, String url) {

        if (logger.isDebugEnabled()) {
            logger.debug("handleIssueNotification: called with issue: " + issue
                    + ", type: " + type + "url: " + url);
        }
        this.handleLocalizedIssueNotification(issue, type, url, null, null);
    }


    /**
     * Method for internal sending of a notification of specific type.
     */
    private void handleLocalizedIssueNotification(final Issue issue, final Type type, final String url,
                                                  final InternetAddress[] recipients, Integer lastModifiedDays) {
        try {

            if (logger.isDebugEnabled()) {
                logger
                        .debug("handleLocalizedIssueNotification: running as thread, called with issue: "
                                + issue
                                + ", type: "
                                + type
                                + "url: "
                                + url
                                + ", recipients: "
                                + (null == recipients ? "<null>" : String
                                .valueOf(Arrays.asList(recipients)))
                                + ", lastModifiedDays: " + lastModifiedDays);
            }

            final Integer notModifiedSince;

            if (lastModifiedDays == null || lastModifiedDays < 0) {
                notModifiedSince = DEFAULT_ISSUE_AGE;
            } else {
                notModifiedSince = lastModifiedDays;
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("handleLocalizedIssueNotification.run: running as thread, called with issue: "
                                    + issue
                                    + ", type: "
                                    + type
                                    + "url: "
                                    + url
                                    + ", recipients: "
                                    + (null == recipients ? "<null>" : String
                                    .valueOf(Arrays.asList(recipients)))
                                    + ", notModifiedSince: " + notModifiedSince);
                }
                final List<Notification> notifications;
                if (issue == null) {
                    logger
                            .warn("handleLocalizedIssueNotification: issue was null. Notification will not be handled");
                    return;
                }
                Map<InternetAddress, Locale> localeMapping;

                if (recipients == null) {
                    notifications = this.getIssueNotifications(issue);

                    localeMapping = new Hashtable<>(notifications.size());
                    Iterator<Notification> it = notifications.iterator();
                    User currentUser;
                    while (it.hasNext()) {
                        currentUser = it.next().getUser();
                        if (null != currentUser
                                && null != currentUser.getEmailAddress()
                                && null != currentUser.getEmail()
                                && (!localeMapping.keySet()
                                .contains(currentUser.getEmailAddress()))) {

                            try {
                                localeMapping.put(currentUser.getEmailAddress(), ITrackerResources.getLocale(currentUser.getPreferences().getUserLocale()));
                            } catch (RuntimeException re) {
                                localeMapping.put(currentUser.getEmailAddress(), ITrackerResources.getLocale());
                            }
                        }
                    }
                } else {
                    localeMapping = new Hashtable<>(1);
                    Locale locale = ITrackerResources.getLocale();
                    for (InternetAddress internetAddress : Arrays.asList(recipients)) {
                        localeMapping.put(internetAddress, locale);
                    }
                }

                this.handleNotification(issue, type, localeMapping, url, notModifiedSince);
            } catch (Exception e) {
                logger.error("run: failed to process notification", e);
            }

        } catch (Exception e) {
            logger
                    .error(
                            "handleLocalizedIssueNotification: unexpected exception caught, throwing runtime exception",
                            e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendReminder(Issue issue, User user, String baseURL, int issueAge) {
        Map<InternetAddress, Locale> recipient = new HashMap<>(1);
        recipient.put(user.getEmailAddress(), ITrackerResources.getLocale(user.getPreferences().getUserLocale()));
        handleNotification(issue, Type.ISSUE_REMINDER, recipient, baseURL, issueAge);
    }

    /**
     * Send notifications to mapped addresses by locale.
     */
    private void handleNotification(Issue issue, Type type, Map<InternetAddress, Locale> recipientsLocales, final String url, Integer notModifiedSince) {
        Set<InternetAddress> recipients;
        Map<Locale, Set<InternetAddress>> localeRecipients = new Hashtable<>();

        List<Component> components = issue.getComponents();

        List<IssueActivity> activity = getIssueService().getIssueActivity(
                issue.getId(), false);

        IssueHistory history;
        history = getIssueService().getLastIssueHistory(issue.getId());
        StringBuilder recipientsString = new StringBuilder();

        if (logger.isDebugEnabled() && null != history) {
            logger.debug("handleIssueNotification: got most recent history: "
                    + history
                    + " ("
                    + history.getDescription()
                    + ")");
        }

        for (InternetAddress internetAddress : recipientsLocales.keySet()) {
            recipientsString.append("\n  ");
            recipientsString.append(internetAddress.getPersonal());

            if (localeRecipients.keySet().contains(recipientsLocales.get(internetAddress))) {
                localeRecipients.get(recipientsLocales.get(internetAddress)).add(internetAddress);
            } else {
                Set<InternetAddress> addresses = new HashSet<>();
                addresses.add(internetAddress);
                localeRecipients.put(recipientsLocales.get(internetAddress), addresses);
            }
        }

        Iterator<Locale> localesIt = localeRecipients.keySet().iterator();
        try {
            while (localesIt.hasNext()) {
                Locale currentLocale = localesIt.next();
                recipients = localeRecipients.get(currentLocale);


                if (recipients.size() > 0) {
                    String subject;
                    if (type == Type.CREATED) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.created",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName()});
                    } else if (type == Type.ASSIGNED) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.assigned",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName()});
                    } else if (type == Type.CLOSED) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.closed",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName()});
                    } else if (type == Type.ISSUE_REMINDER) {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.reminder",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName(),
                                        notModifiedSince});
                    } else {
                        subject = ITrackerResources.getString(
                                "itracker.email.issue.subject.updated",
                                currentLocale,
                                new Object[]{issue.getId(),
                                        issue.getProject().getName()});
                    }

                    String activityString;
                    String componentString = "";
                    StringBuilder sb = new StringBuilder();
                    if (activity.size() == 0) {
                        sb.append("-");
                    } else {
                        for (IssueActivity anActivity : activity) {
                            sb.append("\n ").append(
                                    IssueUtilities.getActivityName(anActivity
                                            .getActivityType(), currentLocale)).append(": ").append(
                                    anActivity.getDescription());

                        }
                    }
                    sb.append("\n");
                    activityString = sb.toString();
                    for (int i = 0; i < components.size(); i++) {
                        componentString += (i != 0 ? ", " : "")
                                + components.get(i).getName();
                    }

                    final String owner = IssueUtilities.getOwnerName(issue.getOwner(), currentLocale);
                    final User hUser = null == history ? null : history.getUser();
                    final String historyUser = (null != hUser) ? hUser.getFullName()
                            : ITrackerResources.getString("itracker.web.generic.notapplicable", currentLocale);

                    final String historyText = (history == null ? "-"
                            : HTMLUtilities
                            .removeMarkup(history
                                    .getDescription()));
                    final String status =
                            IssueUtilities.getStatusName(issue
                                    .getStatus(), currentLocale);
                    final String msgText;
                    if (type == Type.ISSUE_REMINDER) {
                        msgText = ITrackerResources
                                .getString(
                                        "itracker.email.issue.body.reminder",
                                        currentLocale,
                                        new Object[]{
                                                IssueUtilities.getIssueURL(issue, url).toExternalForm(),
                                                issue.getProject().getName(),
                                                issue.getDescription(),
                                                IssueUtilities.getStatusName(issue
                                                        .getStatus(), currentLocale),
                                                IssueUtilities
                                                        .getSeverityName(issue
                                                        .getSeverity(), currentLocale),
                                                owner,
                                                componentString,
                                                historyUser,
                                                historyText,
                                                notModifiedSince,
                                                activityString});
                    } else {
                        String resolution = (issue.getResolution() == null ? ""
                                : issue.getResolution());
                        if (!resolution.equals("")
                                && ProjectUtilities
                                .hasOption(
                                        ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
                                        issue.getProject().getOptions())) {
                            resolution = IssueUtilities.getResolutionName(
                                    resolution, currentLocale);
                        }
                        msgText = ITrackerResources
                                .getString(
                                        "itracker.email.issue.body."
                                                + (type == Type.CREATED ? "created" : "standard"),
                                        currentLocale,
                                        new Object[]{
                                                url + "/module-projects/view_issue.do?id=" + issue.getId(),
                                                issue.getProject().getName(),
                                                issue.getDescription(),
                                                status,
                                                resolution,
                                                IssueUtilities
                                                        .getSeverityName(issue
                                                        .getSeverity(), currentLocale),
                                                owner,
                                                componentString,
                                                historyUser,
                                                historyText,
                                                activityString,
                                                recipientsString});
                    }

                    if (logger.isInfoEnabled()) {
                        logger.info("handleNotification: sending notification for " + issue + " (" + type + ") to " + currentLocale + "-users (" + recipients + ")");

                    }
                    for (InternetAddress iadr : recipients) {
                        emailService.sendEmail(iadr, subject, msgText);
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("handleNotification: sent notification for " + issue
                                + ": " + subject + "\n  " + msgText);
                    }
                }

                updateIssueActivityNotification(issue, true);
                if (logger.isDebugEnabled()) {
                    logger.debug("handleNotification: sent notification for locales " + localeRecipients.keySet() + " recipients: " + localeRecipients.values());
                }
            }
        } catch (RuntimeException e) {
            logger.error("handleNotification: failed to notify: " + issue + " (locales: " + localeRecipients.keySet() + ")", e);

        } catch (MalformedURLException e) {
            logger.error("handleNotification: URL was not well-formed", e);
        }


    }

    private IssueService getIssueService() {
        if (null == issueService) {
            setIssueService((IssueService) applicationContext.getBean("issueService"));
        }

        return issueService;
    }

    public void setIssueService(IssueService issueService) {
        this.issueService = issueService;
    }

    public void updateIssueActivityNotification(Issue issue,
                                                Boolean notificationSent) {
        if (logger.isDebugEnabled()) {
            logger.debug("updateIssueActivityNotification: called with "
                    + issue + ", notificationSent: " + notificationSent);
        }

        Collection<IssueActivity> activities = getIssueActivityDao()
                .findByIssueId(issue.getId());
        for (IssueActivity activity : activities) {
            activity.setNotificationSent(notificationSent);
        }
    }

    /**
     */
    public boolean addIssueNotification(Notification notification) {
        if (logger.isDebugEnabled()) {
            logger.debug("addIssueNotification: called with notification: "
                    + notification);
        }
        Issue issue = notification.getIssue();
        if (!issue.getNotifications().contains(notification)) {
            if (notification.getCreateDate() == null) {
                notification.setCreateDate(new Date());
            }
            if (notification.getLastModifiedDate() == null) {
                notification.setLastModifiedDate(new Date());
            }

            getNotificationDao().save(notification);

            issue.getNotifications().add(notification);
            getIssueDao().merge(issue);

            return true;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("addIssueNotification: attempted to add duplicate notification " + notification + " for issue: " + issue);
        }
        return false;
    }

    /**
     *
     */
    public List<Notification> getIssueNotifications(Issue issue,
                                                    boolean primaryOnly, boolean activeOnly) {
        if (logger.isDebugEnabled()) {
            logger.debug("getIssueNotifications: called with issue: " + issue
                    + ", primaryOnly: " + primaryOnly + ", activeOnly: "
                    + activeOnly);
        }
        List<Notification> issueNotifications = new ArrayList<>();
        if (issue == null) {
            logger.warn("getIssueNotifications: no issue, throwing exception");
            throw new IllegalArgumentException("issue must not be null");
        }
        if (!primaryOnly) {
            List<Notification> notifications = getNotificationDao()
                    .findByIssueId(issue.getId());

            for (Notification notification : notifications) {
                User notificationUser = notification.getUser();
                if (!activeOnly
                        || notificationUser.getStatus() == UserUtilities.STATUS_ACTIVE) {
                    issueNotifications.add(notification);
                }
            }
        }

        // Now add in other notifications like owner, creator, project owners,
        // etc...

        boolean hasOwner = false;
        if (issue.getOwner() != null) {
            User ownerModel = issue.getOwner();

            if (ownerModel != null
                    && (!activeOnly || ownerModel.getStatus() == UserUtilities.STATUS_ACTIVE)) {
                issueNotifications.add(new Notification(ownerModel, issue,
                        Role.OWNER));
                hasOwner = true;
            }
        }

        if (!primaryOnly || !hasOwner) {
            User creatorModel = issue.getCreator();

            if (creatorModel != null
                    && (!activeOnly || creatorModel.getStatus() == UserUtilities.STATUS_ACTIVE)) {
                issueNotifications.add(new Notification(creatorModel,
                        issue, Role.CREATOR));
            }
        }

        Project project = getProjectService().getProject(
                issue.getProject().getId());

        for (User projectOwner : project.getOwners()) {
            if (projectOwner != null
                    && (!activeOnly || projectOwner.getStatus() == UserUtilities.STATUS_ACTIVE)) {
                issueNotifications.add(new Notification(projectOwner,
                        issue, Role.PO));
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getIssueNotifications: returning "
                    + issueNotifications);
        }
        return issueNotifications;
    }

    public List<Notification> getIssueNotifications(Issue issue) {
        if (logger.isDebugEnabled()) {
            logger.debug("getIssueNotifications: called with: " + issue);
        }
        return this.getIssueNotifications(issue, false, true);
    }

    public List<Notification> getPrimaryIssueNotifications(Issue issue) {
        if (logger.isDebugEnabled()) {
            logger.debug("getPrimaryIssueNotifications: called with: " + issue);
        }
        return this.getIssueNotifications(issue, true, false);
    }

    public boolean hasIssueNotification(Issue issue, Integer userId) {
        if (logger.isDebugEnabled()) {
            logger.debug("hasIssueNotification: called with: " + issue
                    + ", userId: " + userId);
        }
        return hasIssueNotification(issue, userId, Role.ANY);
    }

    @Override
    public boolean hasIssueNotification(Issue issue, String login) {

        return hasIssueNotification(issue, login, Role.ANY);
    }

    @Override
    public boolean hasIssueNotification(Issue issue, String login, Role role) {

        if (issue != null && StringUtils.isNotBlank(login)) {

            List<Notification> notifications = getIssueNotifications(issue,
                    false, false);

            for (Notification notification : notifications) {

                if (role == Role.ANY || notification.getRole() == role) {

                    if (StringUtils.equals(login, notification.getUser().getLogin())) {

                        return true;

                    }

                }

            }

        }

        return false;
    }

    public boolean hasIssueNotification(Issue issue, Integer userId, Role role) {

        if (issue != null && userId != null) {

            List<Notification> notifications = getIssueNotifications(issue,
                    false, false);

            for (Notification notification : notifications) {

                if (role == Role.ANY || notification.getRole() == role) {

                    if (notification.getUser().getId().equals(userId)) {

                        return true;

                    }

                }

            }

        }

        return false;

    }

    public boolean removeIssueNotification(Integer notificationId) {
        Notification notification = this.getNotificationDao().findById(
                notificationId);
        getNotificationDao().delete(notification);
        return true;
    }

    public void sendNotification(Issue issue, Type type, String baseURL,
                                 InternetAddress[] receipients, Integer lastModifiedDays) {
        this.handleLocalizedIssueNotification(issue, type, baseURL, receipients,
                lastModifiedDays);

    }


    /**
     * @return the emailService
     */
    public EmailService getEmailService() {
        return emailService;
    }

    /**
     * @return the notificationDao
     */
    private NotificationDAO getNotificationDao() {
        return notificationDao;
    }

    /**
     * @return the projectService
     */
    public ProjectService getProjectService() {
        return projectService;
    }

    /**
     * @param projectService the projectService to set
     */
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * @param notificationDao the notificationDao to set
     */
    public void setNotificationDao(NotificationDAO notificationDao) {
        if (null == notificationDao) {
            throw new IllegalArgumentException(
                    "notification dao must not be null");
        }
        if (null != this.notificationDao) {
            throw new IllegalStateException("notification dao allready set");
        }
        this.notificationDao = notificationDao;
    }


    /**
     * @return the issueActivityDao
     */
    public IssueActivityDAO getIssueActivityDao() {
        return issueActivityDao;
    }

    /**
     * @param issueActivityDao the issueActivityDao to set
     */
    public void setIssueActivityDao(IssueActivityDAO issueActivityDao) {
        this.issueActivityDao = issueActivityDao;
    }

    /**
     * @return the issueDao
     */
    public IssueDAO getIssueDao() {
        return issueDao;
    }

    /**
     * @param issueDao the issueDao to set
     */
    public void setIssueDao(IssueDAO issueDao) {
        this.issueDao = issueDao;
    }

    public String getIssueServiceName() {
        return issueServiceName;
    }

    public void setIssueServiceName(String issueServiceName) {
        this.issueServiceName = issueServiceName;
    }

}