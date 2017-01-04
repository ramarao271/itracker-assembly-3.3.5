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
import org.itracker.model.util.IssueUtilities;

/**
 * An issue history entry.
 * <p/>
 * <p>
 * An IssueHistory can only belong to 1 Issue (composition).
 * </p>
 * <p/>
 * <p>
 * PENDING : what's the difference with an IssueActivity ?
 * </p>
 *
 * @author ready
 */
public class IssueHistory extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Issue issue;

    private String description;

    private int status;

    /**
     * The User who generated this history entry.
     */
    private User creator;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public IssueHistory() {
    }

    public IssueHistory(Issue issue, User creator) {
        setIssue(issue);
        setUser(creator);
        setStatus(IssueUtilities.HISTORY_STATUS_AVAILABLE);
    }

    public IssueHistory(Issue issue, User creator, String description,
                        int status) {
        setIssue(issue);
        setUser(creator);
        setDescription(description);
        setStatus(status);
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
        return creator;
    }

    public void setUser(User creator) {
        if (creator == null) {
            throw new IllegalArgumentException("null creator");
        }
        this.creator = creator;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId())
                .append("issue", issue).append("creator", getUser()).append(
                        "createDate", getCreateDate()).toString();
    }

    public static enum Status {

        STATUS_REMOVED(-1),

        STATUS_AVAILABLE(1);

        @SuppressWarnings("unused")
        private final int code;

        private Status(int code) {
            this.code = code;
        }

    }

}
