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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A notification to a user about an Issue.
 * <p/>
 * <p>
 * An Notification can only belong to 1 Issue (composition).
 * </p>
 *
 * @author ready
 */
public class Notification extends AbstractEntity implements Comparable<Entity> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final Comparator<Notification> TYPE_COMPARATOR = new RoleComparator();

    public static final Comparator<Notification> USER_COMPARATOR = new UserComparator();

    public static final Comparator<Notification> ISSUE_USER_ROLE_COMPARATOR = new IssueUserRoleComparator();

    private Issue issue;

    private User user;

    private Role role;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public Notification() {
    }

    /**
     * @deprecated use Role instead int for role
     */
    public Notification(User user, Issue issue, int role) {
        this.setUser(user);
        this.setIssue(issue);
        this.setNotificationRole(role);

    }

    public Notification(User user, Issue issue, Role role) {
        this.setUser(user);
        this.setIssue(issue);
        this.setRole(role);
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

    /**
     * @deprecated use getRole instead
     */
    public int getNotificationRole() {
        Role r = getRole();
        if (null == r) {
            r = Role.ANY;
        }
        return r.code;
    }

    /**
     * @deprecated
     */
    public void setNotificationRole(int role) {
        Role r = getRoleForCode(role);
        if (null == r) {
            r = Role.ANY;
        }
        this.setRole(r);
    }

    private Role getRoleForCode(int code) {
        for (int i = 0; i < Role.values().length; i++) {
            if (Role.values()[i].code == code) {
                return Role.values()[i];
            }
        }
        return Role.ANY;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("issue",
                getIssue()).append("user", getUser()).append("role", getRole())
                .toString();
    }

    /**
     * Compares by properties issue, user, role.
     *
     * @author ranks
     */
    public static final class IssueUserRoleComparator implements
            Comparator<Notification>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Notification o1, Notification o2) {
            return new CompareToBuilder().append(o1.getIssue(), o2.getIssue())
                    .append(o1.getUser(), o2.getUser(), User.NAME_COMPARATOR)
                    .append(o1.getRole().getCode(), o2.getRole().getCode())
                    .toComparison();
        }
    }

    private static class UserComparator implements Comparator<Notification>,
            Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Notification a, Notification b) {
            return new CompareToBuilder().append(a.getUser(), b.getUser(),
                    User.NAME_COMPARATOR).append(a.getRole(), b.getRole(),
                    Notification.TYPE_COMPARATOR).toComparison();
        }

    }

    private static class RoleComparator implements Comparator<Notification>,
            Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Notification a, Notification b) {
            if (null == a.getRole()) {
                return null == b.getRole() ? 0 : -1;
            } else if (b.getRole() == null) {
                return 1;
            }
            return new CompareToBuilder().append(a.getRole().getCode(),
                    b.getRole().getCode()).toComparison();
        }

    }

    public static enum Role {

        ANY(-1),

        CREATOR(1),

        OWNER(2),

        CONTRIBUTER(3),

        QA(4),

        PM(5),

        PO(6),

        CO(7),

        VO(8),

        IP(9);

        private final int code;

        private Role(int code) {
            this.code = code;
        }

        public Integer getCode() {
            return this.code;
        }

    }

    public static enum Type {

        CREATED(1),

        UPDATED(2),

        ASSIGNED(3),

        CLOSED(4),

        SELF_REGISTER(5),

        ISSUE_REMINDER(6);

        private final int code;

        private Type(int code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

    }

}
