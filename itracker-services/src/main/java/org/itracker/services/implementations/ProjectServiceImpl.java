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

package org.itracker.services.implementations;

import org.apache.log4j.Logger;
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.persistence.dao.*;
import org.itracker.services.ProjectService;

import java.util.*;

public class ProjectServiceImpl implements ProjectService {

    //TODO: Cleanup this file, go through all issues, todos, etc.
    //TODO: Add Javadocs here: document this whole class.

    private static final Logger logger = Logger.getLogger(ProjectService.class);

    private ComponentDAO componentDAO;
    private CustomFieldDAO customFieldDAO;
    private ProjectDAO projectDAO;
    private ProjectScriptDAO projectScriptDAO;
    private UserDAO userDAO;
    private VersionDAO versionDAO;
    private IssueDAO issueDAO;

    public ProjectServiceImpl(ComponentDAO componentDAO,
                              CustomFieldDAO customFieldDAO, ProjectDAO projectDAO,
                              ProjectScriptDAO projectScriptDAO, UserDAO userDAO,
                              VersionDAO versionDAO, IssueDAO issueDAO) {
        this.componentDAO = componentDAO;
        this.customFieldDAO = customFieldDAO;
        this.projectDAO = projectDAO;
        this.projectScriptDAO = projectScriptDAO;
        this.userDAO = userDAO;
        this.versionDAO = versionDAO;
        this.issueDAO = issueDAO;
    }

    public Project getProject(Integer projectId) {
        Project project = getProjectDAO().findByPrimaryKey(projectId);
        return project;
    }

    public List<Project> getAllProjects() {
        return getProjectDAO().findAll();
    }

    public List<Project> getAllAvailableProjects() {
        List<Project> projects = getProjectDAO().findAllAvailable();
        return projects;
    }

    public List<Project> getListOfAllAvailableProjects() {
        return getAllAvailableProjects();
    }

    public Component updateProjectComponent(Component component) {
        getComponentDAO().saveOrUpdate(component);
        return component;
    }

    public Component addProjectComponent(Integer projectId, Component component) {
        Project project = getProjectDAO().findByPrimaryKey(projectId);

        component.setProject(project);
        project.getComponents().add(component);
        getComponentDAO().save(component);
        getProjectDAO().save(project);

        return component;
    }

    public boolean removeProjectComponent(Integer projectId, Integer componentId) {

        Component component = getComponentDAO().findById(componentId);
        if (component == null) {
            return false; //component doesn't exist
        }

        if (!component.getProject().getId().equals(projectId)) {
            return false;
        }

        getComponentDAO().delete(component);

        return true;

    }

    public Component getProjectComponent(Integer componentId) {

        Component component = getComponentDAO().findById(componentId);

        return component;

    }

    public Version addProjectVersion(Integer projectId, Version version) {
        version.setCreateDate(new Date());

        Project project = getProjectDAO().findByPrimaryKey(projectId);
        version.setProject(project);

        Collection<Version> versions = project.getVersions();
        versions.add(version);
        getVersionDAO().save(version);

        return version;

    }

    public boolean removeProjectVersion(Integer projectId, Integer versionId) {

        Version version = getVersionDAO().findByPrimaryKey(versionId);
        if (version == null) {
            return false; // version doesn't exist
        }

        if (!version.getProject().getId().equals(projectId)) {
            return false;
        }

        List<Issue> issues = getIssueDAO().findByTargetVersion(version.getId());
        Iterator<Issue> iterator = issues.iterator();
        while (iterator.hasNext()) {
            Issue issue = (Issue) iterator.next();
            issue.setTargetVersion(null);
            getIssueDAO().save(issue);
        }

        issues = getIssueDAO().findByVersion(version.getId());
        iterator = issues.iterator();
        while (iterator.hasNext()) {
            Issue issue = (Issue) iterator.next();
            if (issue.getVersions().remove(version)) {
                getIssueDAO().save(issue);
            }
        }

        getVersionDAO().delete(version);
        return true;
    }

    public Version updateProjectVersion(Version version) {
        getVersionDAO().saveOrUpdate(version);
        return version;
    }

    public Version getProjectVersion(Integer versionId) {
        Version version = getVersionDAO().findByPrimaryKey(versionId);

        return version;

    }

    public List<User> getProjectOwners(Integer projectId) {
        Project project = getProjectDAO().findByPrimaryKey(projectId);
        List<User> users = project.getOwners();
        return users;
    }

    public boolean setProjectOwners(Project project,
                                    Set<Integer> setOfNewOwnerIds) {
        if (null == project) {
            logger.warn("setProjectOwners, project was null");
            throw new IllegalArgumentException("Project must not be null.");
        }
        if (null == setOfNewOwnerIds) {
            setOfNewOwnerIds = new HashSet<Integer>(0);
        }

        List<User> owners = new ArrayList<User>(setOfNewOwnerIds.size());
        if (!setOfNewOwnerIds.isEmpty()) {

            for (Iterator<Integer> iterator = setOfNewOwnerIds.iterator(); iterator
                    .hasNext(); ) {
                Integer ownerId = iterator.next();
                User owner = getUserDAO().findByPrimaryKey(ownerId);
                owners.add(owner);
            }
        }
        project.setOwners(owners);

        return true;
    }

    public List<CustomField> getProjectFields(Integer projectId) {
        return getProjectFields(projectId, null);
    }

    /**
     * TODO: implement Locale-aware ProjectFields.
     */
    public List<CustomField> getProjectFields(Integer projectId, Locale locale) {

        Project project = projectDAO.findByPrimaryKey(projectId);
        List<CustomField> fields = project.getCustomFields();

        return fields;
    }

    public boolean setProjectFields(Project project,
                                    Set<Integer> setOfNewsFieldIds) {
        List<CustomField> fields;
        fields = project.getCustomFields();
        fields.clear();

        if (setOfNewsFieldIds != null && !setOfNewsFieldIds.isEmpty()) {
            for (Iterator<Integer> iterator = setOfNewsFieldIds.iterator(); iterator
                    .hasNext(); ) {
                Integer fieldId = iterator.next();
                CustomField field = customFieldDAO.findByPrimaryKey(fieldId);
                fields.add(field);
            }
        }
        return true;
    }

    public ProjectScript getProjectScript(Integer scriptId) {
        ProjectScript projectScript = this.projectScriptDAO
                .findByPrimaryKey(scriptId);
        return projectScript;

    }

    public List<ProjectScript> getProjectScripts() {
        List<ProjectScript> projectScripts = this.projectScriptDAO.findAll();
        return projectScripts;
    }

    public ProjectScript addProjectScript(Integer projectId,
                                          ProjectScript projectScript) {
        ProjectScript addprojectScript = new ProjectScript();
        addprojectScript.setId(projectScript.getId());
        addprojectScript.setFieldId(projectScript.getFieldId());
        addprojectScript.setFieldType(projectScript.getFieldType());
        addprojectScript.setPriority(projectScript.getPriority());
        addprojectScript.setProject(projectScript.getProject());
        addprojectScript.setScript(projectScript.getScript());
        this.projectScriptDAO.save(addprojectScript);

        return addprojectScript;

    }

    public boolean removeProjectScript(Integer projectId, Integer scriptId) {
        ProjectScript script = projectScriptDAO.findByPrimaryKey(scriptId);
        this.projectScriptDAO.delete(script);


        return true;

    }

    public ProjectScript updateProjectScript(ProjectScript projectScript) {
        ProjectScript editprojectScript = projectScriptDAO
                .findByPrimaryKey(projectScript.getId());
        editprojectScript.setId(projectScript.getId());
        editprojectScript.setFieldId(projectScript.getFieldId());
        editprojectScript.setPriority(projectScript.getPriority());
        editprojectScript.setProject(projectScript.getProject());
        editprojectScript.setScript(projectScript.getScript());

        this.projectScriptDAO.saveOrUpdate(editprojectScript);

        return editprojectScript;
    }

    public Long getTotalNumberIssuesByProject(Integer projectId) {
        return issueDAO.countByProject(projectId);
    }

    public Long countIssuesByVersion(Integer versionId) {
        return issueDAO.countByVersion(versionId);

    }

    public Long countIssuesByComponent(Integer componentId) {
        return issueDAO.countByComponent(componentId);
    }

    public Long[] getProjectStats(Integer projectId) {
        final Long[] issueStats = new Long[2];

        Long openIssuesCount = issueDAO.countByProjectAndLowerStatus(projectId,
                IssueUtilities.STATUS_RESOLVED);

        issueStats[0] = openIssuesCount;

        Long resolvedIssuesCount = issueDAO.countByProjectAndHigherStatus(
                projectId, IssueUtilities.STATUS_RESOLVED);
        issueStats[1] = resolvedIssuesCount;

        return issueStats;
    }

    //TODO: Decide if this code is really needed and document for what
    @SuppressWarnings("unused")
    private IssueDAO getIssueDAO() {
        return issueDAO;
    }

    private ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    private ComponentDAO getComponentDAO() {
        return componentDAO;
    }

    private CustomFieldDAO getCustomFieldDAO() {
        return this.customFieldDAO;
    }

    private ProjectScriptDAO getProjectScriptDAO() {
        return this.projectScriptDAO;
    }

    private VersionDAO getVersionDAO() {
        return this.versionDAO;
    }

    private UserDAO getUserDAO() {
        return this.userDAO;
    }

    public List<CustomField> getListOfProjectFields(Integer projectId) {
        throw new UnsupportedOperationException();
    }

    public List<User> getListOfProjectOwners(Integer projectId) {
        throw new UnsupportedOperationException();
    }

    public Long getTotalNumberOpenIssuesByProject(Integer projectId) {
        return issueDAO.countByProjectAndLowerStatus(projectId,
                IssueUtilities.STATUS_RESOLVED);
    }

    public Long getTotalNumberResolvedIssuesByProject(Integer projectId) {
        return issueDAO.countByProjectAndHigherStatus(projectId,
                IssueUtilities.STATUS_RESOLVED);
    }

    public Date getLatestIssueUpdatedDateByProjectId(Integer projectId) {
        return issueDAO.latestModificationDate(projectId);
    }

    public Project createProject(Project project, Integer userId) {
        project.setId(null);
        User user = getUserDAO().findByPrimaryKey(userId);
        project.setOwners(Arrays.asList(new User[]{user}));
        getProjectDAO().save(project);

        return project;
    }

    public Project updateProject(Project project, Integer userId) {
        User user = getUserDAO().findByPrimaryKey(userId);

        getProjectDAO().saveOrUpdate(project);

        return project;
    }

    public Boolean isUniqueProjectName(String projectName,
                                       Integer updatedProjectId) {
        Project project = getProjectDAO().findByName(projectName);
        if (project != null) {
            // validate that the returned project is not the updated one.
            if (!project.getId().equals(updatedProjectId)) {
                return false;
            }
        }
        return true;
    }
}
