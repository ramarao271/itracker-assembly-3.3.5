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

/**
 * A user permission on a project.
 * <p/>
 * <p>
 * The permission type tells what kind of action the user is allowed perform.
 * </p>
 *
 * @author ready
 */
public class Permission extends AbstractEntity {

    /**
     * Comparator for comparing the main properties type, user, project
     */
    public static final PermissionPropertiesComparator PERMISSION_PROPERTIES_COMPARATOR = new PermissionPropertiesComparator();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of permission granted. TODO: use PermissionType enum
     */
    private PermissionType type;

    /**
     * The project on which this permission is granted. May be <tt>null</tt>
     * to indicate the permission is granted on all projects.
     */
    private Project project;

    /**
     * The user who's granted this permission.
     */
    private User user;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public Permission() {
    }

    /**
     * Grants permissions on all projects to the given user.
     *
     * @param type permission type
     * @param user grantee
     */
    public Permission(PermissionType type, User user) {
        this(type, user, null);
    }

    /**
     * Grants permissions on all projects to the given user.
     *
     * @param type    permission type
     * @param user    grantee
     * @param project on which permission is granted, or <tt>null</tt> for all
     *                projects
     */
    public Permission(PermissionType type, User user, Project project) {
        setPermissionType(type);
        setUser(user);
        setProject(project);
    }

    public PermissionType getPermissionType() {
        return type;
    }

    public void setPermissionType(PermissionType type) {
        this.type = type;
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

    public Project getProject() {
        return project;
    }

    /**
     * May be null to indicate a permission on all projects.
     */
    public void setProject(Project project) {
        this.project = project;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("type", getPermissionType())
                .append("user", getUser()).append("project", getProject()).toString();
    }


    public static final class PermissionPropertiesComparator implements java.util.Comparator<Permission> {
        public int compare(Permission lhs, Permission rhs) {
            return new CompareToBuilder().append(lhs.type, rhs.type).append(lhs.user, rhs.user, User.NAME_COMPARATOR).append(lhs.project, rhs.project, Project.PROJECT_COMPARATOR).toComparison();
        }
    }
}
