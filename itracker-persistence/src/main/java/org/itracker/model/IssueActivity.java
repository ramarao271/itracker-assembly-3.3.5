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

package org.itracker.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An issue activity.
 * <p/>
 * <p>
 * An IssueActivity can only belong to 1 Issue (composition).
 * </p>
 * <p/>
 * <p>
 * The natural key of an IssueActivity is issue + user + type + createDate.
 * </p>
 *
 * @author ready
 */
public class IssueActivity extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Issue to which this activity is related.
     */
    private Issue issue;

    /**
     * The User who generated this activity.
     */
    private User user;

    /**
     * Optional activity description.
     */
    private String description = "";

    /**
     * Whether a notification has been sent for this activity.
     */
    private boolean notificationSent = false;

    private IssueActivityType activityType = IssueActivityType.ISSUE_CREATED;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that <code>issue</code>, <code>user</code> and
     * <code>type</code>, which form an instance's identity, are always
     * initialized.
     * </p>
     */
    public IssueActivity() {

    }

    /**
     * Creates a new instance with a <code>notificationSent</code> flag set to
     * <tt>false</tt> and a creation and last modified time stamp set to the
     * current time.
     */
    public IssueActivity(Issue issue, User user, IssueActivityType type) {
        setIssue(issue);
        setUser(user);
        setActivityType(type);
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("null issue");
        }
        this.issue = issue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("null user");
        }
        this.user = user;
    }

    public void setActivityType(IssueActivityType type) {

        // this.type = type.code;
        this.activityType = type;
    }

    public IssueActivityType getActivityType() {
        return this.activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean sent) {
        this.notificationSent = sent;
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", getId())
                .append("issue", getIssue()).append("user", getUser()).append("type",
                        getActivityType()).append("createDate", getCreateDate())
                .toString();

    }

}
