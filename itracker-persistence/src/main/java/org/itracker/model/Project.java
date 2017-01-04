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
import java.util.*;

/**
 * This is a POJO Business Domain Object modelling a project.
 * <p/>
 * <p>
 * Hibernate Bean.
 * </p>
 *
 * @author ready
 */
public class Project extends AbstractEntity implements Comparable<Entity> {

    public static final ProjectComparator PROJECT_COMPARATOR = new ProjectComparator();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    /**
     * Project's current status.
     * <p/>
     * <p>
     * Invariant : never <tt>null</tt>.
     * </p>
     */
    private Status status;

    private int options;

    /**
     * The list of Components that belong to this project.
     * <p/>
     * <p>
     * Project - Component is a 1-N relationship.
     * </p>
     */
    private List<Component> components = new ArrayList<Component>();

    /**
     * The list of Versions of this Project.
     * <p/>
     * <p>
     * Project - Version is a 1-N relationship.
     * </p>
     */
    private List<Version> versions = new ArrayList<Version>();

    /**
     * The Permissions of all Users on this Project.
     * <p/>
     * <p>
     * Project - Permission is a 1-N relationship.
     * </p>
     * <p/>
     * PENDING: Does this relationship need to be navigatable ?
     */
    // TODO: it would be a Set, not list
    private Set<Permission> permissions = new TreeSet<Permission>(Permission.PERMISSION_PROPERTIES_COMPARATOR);

    /**
     * The Users who are responsible for this Project.
     * <p/>
     * Project - User (owners) is a M-N relationship.
     */
    private List<User> owners = new ArrayList<User>();

    /**
     * The custom fields associated to this Project.
     * <p/>
     * <p>
     * Project - CustomField is a M-N relationship.
     * </p>
     * <p/>
     * <p>
     * All Issues of this Project will have these custom fields.
     * </p>
     */
    private List<CustomField> customFields = new ArrayList<CustomField>();

    /**
     * Project - ProjectScript is a 1-N relationship.
     */
    private List<ProjectScript> scripts = new ArrayList<ProjectScript>();

    /*
      * This class used to have a <code>issues</code> attribute, which was a
      * Collection<Issue>. This has been removed because the association Project -
      * Issue doesn't need to be navigatable in this direction.
      */

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public Project() {
    }

    public Project(String name) {
        setName(name);
        this.status = Status.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return project's current status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @throws IllegalArgumentException status is <tt>null</tt>
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("null status");
        }
        this.status = status;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> getComponents) {
        this.components = getComponents;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> getVersions) {
        this.versions = getVersions;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomField> getCustomFields) {
        this.customFields = getCustomFields;
    }

    public List<User> getOwners() {
        return owners;
    }

    public void setOwners(List<User> getOwners) {
        this.owners = getOwners;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> getPermissions) {
        this.permissions = getPermissions;
    }

    public List<ProjectScript> getScripts() {
        return scripts;
    }

    public void setScripts(List<ProjectScript> getScripts) {
        this.scripts = getScripts;
    }

    /**
     * @return <tt>Project [id=id, name=name]</tt>
     */
    @Override
    public String toString() {

        return new ToStringBuilder(this).append("id", this.getId()).append("name",
                this.getName()).append("description", getDescription()).append("owners", getOwners()).toString();
    }

    /**
     * Comparator for comparing projects by name
     */
    public static final class ProjectComparator implements Comparator<Project>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Project o1, Project o2) {
            return new CompareToBuilder().append(o1.getName(), o2.getName()).append(
                    o1.getId(), o2.getId()).toComparison();
        }
    }
}
