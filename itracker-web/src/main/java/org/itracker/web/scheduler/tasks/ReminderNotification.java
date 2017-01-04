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

package org.itracker.web.scheduler.tasks;


import org.apache.log4j.Logger;
import org.itracker.model.Issue;
import org.itracker.model.Notification;
import org.itracker.model.util.IssueUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.web.util.ServletContextUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * This class can be used to send reminder emails to owners/admins
 * that issues need their attention.
 *
 */
public class ReminderNotification extends BaseJob implements Runnable {

    public static final String DEFAULT_BASE_URL = "http://localhost:8080/itracker";
    public static final int DEFAULT_ISSUE_AGE = 30;

    private final Logger logger;

    public ReminderNotification() {
        this.logger = Logger.getLogger(getClass());
    }

    @Override
    @Transactional
    public void run() {
        this.performTask(new String[]{});
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        super.execute(context);
        this.performTask((String[]) context.get("args"));
    }

    /**
     * This method is called by the scheduler to send the reminder
     * notifications.  The arguments can be used to configure which issues
     * and projects are included in the notifications.  The args should
     * include as the first parameter the base url of the server including
     * the scheme, hostname, port, and context.  For example:
     * <br>
     * http://localhost:8080/itracker
     * <br>
     * If no other arguments are supplied it sends reminders to all
     * owners/admins of unresolved issues in all projects that have not been
     * modified in 30 days.  The second element of the array can be a number
     * that represents the number of days to use to check the last modified
     * date.  The third optional element is a number that represents the project
     * id to limit the notifications to. A fourth optional argument is the severity
     * to send the notification for.
     *
     * @param args optional arguments to configure the notification messages
     */
    public void performTask(String[] args) {
        final List<Issue> issues;
        final ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

        String baseURL = configurationService.getSystemBaseURL();
        int notificationDays = configurationService.getIntegerProperty("reminder_notification_days",
                        DEFAULT_ISSUE_AGE);

        int projectId = -1;
        int severity = -1;

        // Process arguments.
        if (args != null) {
            if (args.length > 0 && args[0] != null) {
                baseURL = args[0];
            }

            if (null == baseURL) {
                baseURL = DEFAULT_BASE_URL;
            }
            if (args.length > 1) {
                try {
                    notificationDays = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    logger.debug("Invalid issue age specified in ReminderNotification task.");
                }
            }
            if (args.length > 2) {
                try {
                    projectId = Integer.parseInt(args[2]);
                } catch (NumberFormatException nfe) {
                    logger.debug("Invalid projectId specified in ReminderNotification task.");
                }
            }
            if (args.length > 3) {
                try {
                    severity = Integer.parseInt(args[3]);
                } catch (NumberFormatException nfe) {
                    logger.debug("Invalid severity specified in ReminderNotification task.");
                }
            }
        }
        if (notificationDays < 1) {
            logger.info("Reminder notifications are disabled for project " + projectId );
            return;
        }
        logger.debug("Reminder notifications being sent for project " + projectId + " with issues over " + notificationDays + " days old with severity " + severity + ".  Base URL = " + baseURL);

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            NotificationService notificationService = ServletContextUtils.getItrackerServices().getNotificationService();
            GregorianCalendar cal = new GregorianCalendar();
            cal.add(Calendar.DAY_OF_MONTH, 0 - notificationDays);
            Date threshold = cal.getTime();

            if (projectId > 0) {
                issues = issueService.getIssuesByProjectId(projectId, IssueUtilities.STATUS_RESOLVED);
            } else {
                issues = issueService.getIssuesWithStatusLessThan(IssueUtilities.STATUS_RESOLVED);
            }
            if (issues != null && issues.size() > 0) {
                for (Issue issue : issues) {
                    if (severity >= 0 && issue.getSeverity() != severity) {
                        continue;
                    }
                    if (issue.getLastModifiedDate() != null && issue.getLastModifiedDate().before(threshold)) {
                        List<Notification> notifications = notificationService.getPrimaryIssueNotifications(issue);
                        for (Notification notification : notifications) {
                            if (notification.getUser().getEmail() != null
                                    && notification.getUser().getEmail().indexOf('@') >= 0) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Sending reminder notification for issue " + issue.getId() + " to " + notification.getUser() + " users.");
                                }
                                notificationService.sendReminder(issue, notification.getUser(), baseURL, notificationDays);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error sending reminder notifications. Message: ", e);
            throw new RuntimeException("failed to send reminder notifications.", e);
        }
    }
}
