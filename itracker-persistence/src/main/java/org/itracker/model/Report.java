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

import java.util.Comparator;

/**
 * This is a POJO Business Domain Object.
 * <p/>
 * <p>
 * Hibernate Bean.
 * </p>
 *
 * @author ready
 */
public class Report extends AbstractEntity {

    public static final Comparator<Report> NAME_COMPARATOR = new NameComparator();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String nameKey;

    private String description;

    private byte[] fileData;

    private String className;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public Report() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name",
                getName()).append("description", getDescription()).append(
                "nameKey", getNameKey())
                .toString();
    }

    private static final class NameComparator implements Comparator<Report> {
        public int compare(Report o1, Report o2) {
            return new CompareToBuilder().append(o1.getName(), o2.getName())
                    .append(o1.getNameKey(), o2.getNameKey()).append(
                            o1.getId(), o2.getId()).toComparison();
        }
    }

}
