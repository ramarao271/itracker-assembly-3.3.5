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
 * A Beanshell script configured to be executed for a specific Project field.
 *
 * @author ready
 */
public class ProjectScript extends AbstractEntity {

    public static final FieldPriorityComparator FIELD_PRIORITY_COMPARATOR = new FieldPriorityComparator();
    public static final FieldPriorityComparator PRIORITY_COMPARATOR = new FieldPriorityComparator();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Project for which the script must be executed.
     */
    private Project project;

    /**
     * The ID of the built-in or custom field for which the script must be
     * executed.
     * <p/>
     * <p>
     * If the ID represents a CustomField, then the CustomField should be
     * configured for the Project or the script will never be executed.
     * </p>
     */
    private Integer fieldId;
    private Configuration.Type fieldType;

    /**
     * The Beanshell script to execute.
     */
    private WorkflowScript script;

    private int priority;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public ProjectScript() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public WorkflowScript getScript() {
        return script;
    }

    public void setScript(WorkflowScript script) {
        this.script = script;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Configuration.Type getFieldType() {
        return fieldType;
    }

    public void setFieldType(Configuration.Type fieldType) {
        this.fieldType = fieldType;
    }

    public static class FieldPriorityComparator implements
            Comparator<ProjectScript>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(ProjectScript a, ProjectScript b) {

            return new CompareToBuilder().append(a.getFieldId(), b.getFieldId()).append(a.getPriority(), b.getPriority()).toComparison();
        }

    }
    public static class ScriptPriorityComparator implements
                Comparator<ProjectScript>, Serializable {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            public int compare(ProjectScript a, ProjectScript b) {

                return new CompareToBuilder().append(a.getPriority(), b.getPriority()).toComparison();
            }

        }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("script", getScript()).append(
                "fieldId", getFieldId()).append("priority", getPriority()).append(
                "project", getProject()).toString();
    }

}
