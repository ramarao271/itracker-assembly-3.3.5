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

package org.itracker.services;

import org.itracker.model.Issue;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.model.User;

import javax.mail.internet.InternetAddress;
import java.util.List;


public interface NotificationService {


    /**
     * Retrieves the primary issue notifications.  Primary notifications
     * are defined as the issue owner (or creator if not assigned), and any project owners.
     * This should encompass the list of people that should be notified so that action
     * can be taken on an issue that needs immediate attention.
     *
     * @param issue the issue to find notifications for
     */
    List<Notification> getPrimaryIssueNotifications(Issue issue);

    /**
     * Retrieves all notifications for an issue where the notification's user is also active.
     *
     * @param issue the issue to find notifications for
     */
    List<Notification> getIssueNotifications(Issue issue);

    /**
     * Retrieves an array of issue notifications.  The notifications by default
     * is the creator and owner of the issue, all project admins for the issue's project,
     * and anyone else that has a notfication on file.
     *
     * @param notificationId id of the notification
     */
    boolean removeIssueNotification(Integer notificationId);

    /**
     * @param issue
     * @param primaryOnly
     * @param activeOnly
     * @return
     */
    List<Notification> getIssueNotifications(Issue issue, boolean primaryOnly, boolean activeOnly);

    /**
     * @param issue
     * @param userId
     * @return
     */
    boolean hasIssueNotification(Issue issue, Integer userId);

    boolean hasIssueNotification(Issue issue, String login);
    boolean hasIssueNotification(Issue issue, String login, Role role);
    boolean hasIssueNotification(Issue issue, Integer userId, Role role);

    /**
     * @param notification
     * @param type
     * @param baseURL
     */
    void sendNotification(Notification notification, Type type, String baseURL);

    /**
     * @param issue
     * @param type
     * @param baseURL
     */
    void sendNotification(Issue issue, Type type, String baseURL);

    /**
     * @param issue
     * @param type
     * @param baseURL
     * @param receipients
     * @param lastModifiedDays
     * @deprecated
     */
    @Deprecated
    void sendNotification(Issue issue, Type type, String baseURL, InternetAddress[] receipients, Integer lastModifiedDays);

    /**
     * TODO: whats its use?
     * <p/>
     * Moved from {@link IssueService}, could not find out the purpose of this method. What will happen with this added {@link Notification}?
     */
    boolean addIssueNotification(Notification notification);

    void sendReminder(Issue issue, User user, String baseURL, int issueAge);
}