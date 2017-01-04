/* This software was designed and created by Jason Carroll.
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
 * A Project version.
 * <p/>
 * <p>
 * A Version can only belong to 1 Project (composition).
 * </p>
 *
 * @author ready
 */
public class Version extends AbstractEntity implements Comparable<Entity> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Invariant: never <tt>null</tt>.
     */
    private Project project;

    /**
     * Invariant: never <tt>null</tt>.
     */
    private String number;

    /*
      * major and minor are only used to compare Versions. They can be computed
      * from the <code>number</code> attribute, PENDING: We should also allow
      * to specify them separately in order not to impose any constraint on the
      * format of the version number.
      */
    private int major;
    private int minor;

    private String description;

    /**
     * Version status.
     * <p>
     * Invariant: never <tt>null</tt>.
     * </p>
     */
    private Status status;

    /*
      * This class used to have a <code>issues</code> attribute, which was a
      * Collection<Issue>. This has been removed because the association Version -
      * Issue doesn't need to be navigatable in this direction.
      */

    public static final Comparator<Version> VERSION_COMPARATOR = new VersionComparator();

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that <code>project</code> and <code>number</code>,
     * which form an instance's identity, are never <tt>null</tt>.
     * </p>
     */
    public Version() {
    }

    /**
     * Creates a new active Version for the given Project.
     *
     * @param project project to which this version belongs
     * @param number  unique within the project
     */
    public Version(Project project, String number) {
        setProject(project);
        setVersionInfo(number);

        // A new version is active by default.
        this.status = Status.ACTIVE;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int getMajor) {
        this.major = getMajor;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int getMinor) {
        this.minor = getMinor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String getNumber) {
        this.number = getNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String getDescription) {
        this.description = getDescription;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("null project");
        }
        this.project = project;
    }

    /**
     * Returns this version's status.
     *
     * @return enum constant
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets this version's status.
     *
     * @param status enum constant
     * @throws IllegalArgumentException <code>status</code> is <tt>null</tt>
     */
    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("null status");
        }
        this.status = status;
    }

    /**
     * Convience method to set the number, major and minor fields with a single
     * call. It will first set the number to the provided data, and then attempt
     * to parse the info if in the form major.minor into parts to set the other
     * information.
     *
     * @param versionInfo the version number string to use
     */
    public void setVersionInfo(String versionInfo) {
        setNumber(versionInfo);

        if (null == versionInfo) {
            throw new IllegalArgumentException("version info must not be null.");
        }
        String versionNumber = this.number.trim();
        int firstDot = versionNumber.indexOf('.');
        String major = "0";
        major = (firstDot > 0 ? versionNumber.substring(0, firstDot).trim()
                : versionNumber.trim());

        try {
            setMajor(Integer.parseInt(major));
        } catch (NumberFormatException ex) {
            setMajor(0);
        }

        int secondDot = (firstDot > -1 ? versionNumber.indexOf('.',
                firstDot + 1) : -1);
        String minor = (secondDot > -1 ? versionNumber.substring(firstDot + 1,
                secondDot).trim() : versionNumber.substring(firstDot + 1)
                .trim());
        try {
            setMinor(Integer.parseInt(minor));
        } catch (NumberFormatException ex) {
            setMinor(0);
        }
    }

    /**
     * @return <tt>Version [id=<id>, project=<project>, number=<number>]</tt>
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("number",
                getNumber()).append("project", getProject()).append(getMajor()).append(getMinor())
                .append("status", getStatus()).toString();
    }

    /**
     * Compares 2 Versions by major and minor number.
     */
    public static final class VersionComparator implements Comparator<Version>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private boolean ascending = true;

        public VersionComparator() {
        }

        public VersionComparator(boolean ascending) {
            setAscending(ascending);
        }

        private boolean isAscending() {
            return ascending;
        }

        private void setAscending(boolean ascending) {
            this.ascending = ascending;
        }

        public int compare(Version a, Version b) {
            int result = new CompareToBuilder().append(a.getNumber(), b.getNumber())
                    .append(a.getMajor(), b.getMajor()).append(a.getMinor(), b.getMinor()).toComparison();

            return (isAscending() ? result : -result);
        }

    }

}
