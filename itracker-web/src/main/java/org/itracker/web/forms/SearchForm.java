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

package org.itracker.web.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class SearchForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String action;
    private Integer[] components;
    private Integer creator;
    private String orderBy;
    private Integer owner;
    private Integer project;
    private Integer[] projects;
    private String resolution;
    private Integer[] severities;
    private Integer[] statuses;
    private Integer targetVersion;
    private String textphrase;
    private Integer type;
    private Integer[] versions;

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        action = null;
        components = null;
        creator = null;
        orderBy = null;
        owner = null;
        project = null;
        projects = null;
        resolution = null;
        severities = null;
        statuses = null;
        targetVersion = null;
        textphrase = null;
        type = null;
        versions = null;

    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        return errors;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer[] getComponents() {
        if (null == components)
            return null;
        return components.clone();
    }

    public void setComponents(Integer[] components) {
        if (null == components)
            this.components = null;
        else
            this.components = components.clone();
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    public Integer[] getProjects() {
        if (null == projects)
            return null;
        return projects.clone();
    }

    public void setProjects(Integer[] projects) {
        if (null == projects)
            this.projects = null;
        else
            this.projects = projects.clone();
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer[] getSeverities() {
        if (null == severities)
            return null;
        return severities.clone();
    }

    public void setSeverities(Integer[] severities) {
        if (null == severities)
            this.severities = null;
        else
            this.severities = severities.clone();
    }

    public Integer[] getStatuses() {
        if (null == statuses)
            return null;
        return statuses.clone();
    }

    public void setStatuses(Integer[] statuses) {
        if (null == statuses)
            this.statuses = null;
        else
            this.statuses = statuses.clone();
    }

    public Integer getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(Integer targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getTextphrase() {
        return textphrase;
    }

    public void setTextphrase(String textphrase) {
        this.textphrase = textphrase;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer[] getVersions() {
        if (null == this.versions)
            return null;
        return versions.clone();
    }

    public void setVersions(Integer[] versions) {
        if (null == versions)
            this.versions = null;
        else
            this.versions = versions.clone();
    }

}
