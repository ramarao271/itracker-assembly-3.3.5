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
 * Models a project component.
 * <p/>
 * <p>
 * A Component is a project subdivision, like a sub-project or functional area,
 * ... <br>
 * It is identified by a unique name within the project to which it belongs
 * (composition). <br>
 * e.g.: core, web-ui, swing-ui, help, ...
 * </p>
 * <p/>
 * <p>
 * A component cannot have sub-components, unlike categories and sub-categories
 * that exist in some issue tracking systems.
 * </p>
 *
 * @author Jason
 * @author Johnny
 */
public class Component extends AbstractEntity implements Comparable<Entity> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final Comparator<Component> NAME_COMPARATOR = new NameComparator();
    public static final Comparator<Component> PROJECTNAME_COMPARATOR = new ProjectNameComparator();

    /**
     * Project to which this component belongs. Invariant: never <tt>null</tt>.
     */

    private Project project;

    /**
     * Unique name identifying this component within its project. Invariant:
     * never <tt>null</tt>.
     */
    private String name;

    /**
     * Component description.
     */
    private String description;

    /**
     * Component status.
     * <p>
     * Invariant: never <tt>null</tt>.
     * </p>
     */
    private Status status;

    /*
      * This class used to have a <code>issues</code> attribute, which was a
      * Collection<Issue>. This has been removed because the association
      * Component - Issue doesn't need to be navigatable in this direction.
      */

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that <code>project</code> and <code>name</code>,
     * which form an instance's identity, are never <tt>null</tt>.
     * </p>
     */
    public Component() {
    }

    /**
     * Creates a new active Component of the given name for the given Project.
     *
     * @param project owning this component
     * @param name    unique component name within the project
     */
    public Component(Project project, String name) {
        setProject(project);
        setName(name);

        // A new component is active by default.
        this.status = Status.ACTIVE;
    }

    /**
     * Returns the project owning this component.
     *
     * @return parent project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project owning this component.
     * <p/>
     * <p>
     * PENDING: The project shouldn't be modified because it is part of a
     * component's natural key and is used in the equals method!
     * </p>
     *
     * @param project parent project
     * @throws IllegalArgumentException null project
     */
    public void setProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("null project");
        }
        this.project = project;
    }

    /**
     * Returns this component's name.
     *
     * @return unique name within the parent project
     */
    public String getName() {
        return name;
    }

    /**
     * Sets this component's name.
     * <p/>
     * <p>
     * PENDING: The name shouldn't be modified because it is part of a
     * component's natural key and is used in the equals method!
     * </p>
     *
     * @param name unique name within the parent project
     * @throws IllegalArgumentException null name
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        this.name = name;
    }

    /**
     * Returns this component's description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets this component's description.
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns this component's status.
     *
     * @return enum value
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets this component's status.
     *
     * @param status enum value
     * @throws IllegalArgumentException <code>status</code> is <tt>null</tt>
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("null status");
        }
        this.status = status;
    }

    // /**
    // * Two component instances are equal if they belong to the same project
    // * and have the same name.
    // */
    // @Override
    // public boolean equals(Object obj) {
    // if (this == obj) {
    // return true;
    // }
    //
    // if (obj instanceof Component) {
    // final Component other = (Component)obj;
    //
    // return this.project.equals(other.project)
    // && this.name.equals(other.name);
    // }
    // return false;
    // }

    // /**
    // * Overridden to match implementation of method {@link #equals(Object)}.
    // */
    // @Override
    // public int hashCode() {
    // return this.project.hashCode() + this.name.hashCode();
    // }

    /**
     * Returns contatanation of system ID and object natural key.
     *
     * @return <tt>Component [id=this.id, project=this.project, name=this.name]</tt>
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("project",
                getProject()).append("name", getName()).toString();
    }

    // /**
    // * Compares 2 Components by project and name (natural key).
    // */
    // public int compareTo(Component other) {
    //
    // final int projectComparison = this.project.compareTo(other.project);
    //
    // if (projectComparison == 0) {
    // return this.name.compareTo(other.name);
    // }
    // return projectComparison;
    // }
    //

    /**
     * Compares 2 Components by name.
     * <p/>
     * <p>
     * It should only be used to compare components of the same project, because
     * it doesn't take the project into account.
     * </p>
     */
    private static class NameComparator implements Comparator<Component>, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Component a, Component b) {
            return new CompareToBuilder().append(a.getName(), b.getName()).append(a.getId(), b.getId()).toComparison();

        }

    }

    public static final class ProjectNameComparator implements
            Comparator<Component>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Component o1, Component o2) {
            if (null != o1.getProject() && null != o2.getProject()) {
                return new CompareToBuilder().append(o1.getProject().getName(), o2.getProject().getName())
                        .append(o1.getName(), o2.getName()).toComparison();
            }
            return NAME_COMPARATOR.compare(o1, o2);
        }
    }

}
