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

package org.itracker.web.actions.admin.configuration;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.ProjectException;
import org.itracker.UserException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.PasswordException;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.ImportExportUtilities;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ImportDataProcessAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(ImportDataProcessAction.class);

    private static final class ActionException extends Exception {

    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {

            HttpSession session = request.getSession(false);
            User importer = LoginUtilities.getCurrentUser(request);
            if (importer == null || !importer.isSuperUser()) {
                return mapping.findForward("unauthorized");
            }

            ImportDataModel model = (ImportDataModel) session.getAttribute(Constants.IMPORT_DATA_KEY);
            if (null == model) {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("itracker.web.error.system.message", "No model in session.", "Request"));
            }
            checkErrors(errors);
            log.debug("Importing configuration data.");
            createConfig(model, importer, errors);

            checkErrors(errors);
            log.debug("Importing user data.");
            createUsers(model, importer, errors);

            checkErrors(errors);
            log.debug("Importing project data.");
            createProjects(model, importer, errors);

            checkErrors(errors);
            log.debug("Importing issue data.");
            createIssues(model, importer, errors);

            checkErrors(errors);
            log.debug("Import complete.");

        } catch (ActionException e) {
            if (errors.isEmpty()) {
                log.error("failed with empty errors", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage(), "Unexpected"));
            }
            return handleErrors(errors, request, mapping);
        } catch (Exception e) {
            log.error("Exception while importing data.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage(), "Unexpected"));
            return handleErrors(errors, request, mapping);
        }

        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.importexport.importcomplete"));
        saveMessages(request, errors);
        // reset import models
        request.getSession().removeAttribute(Constants.IMPORT_DATA_KEY);
        return mapping.findForward("adminindex");
    }

    private void checkErrors(ActionMessages errors) throws ActionException{
        if (!errors.isEmpty()) {
            throw new ActionException();
        }
    }

    private ActionForward handleErrors(ActionMessages errors, HttpServletRequest request, ActionMapping mapping) {

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("error");
        }
        return null;

    }
    private boolean createConfig(ImportDataModel model, User importer, ActionMessages errors) {
        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

            AbstractEntity[] importData = model.getData();
            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof Configuration && null == model.getExistingModel(i)) {
                    Configuration configItem = (Configuration) importData[i];
                    Configuration newConfigItem = configurationService.createConfigurationItem(configItem);
                    configItem.setId(newConfigItem.getId());

                    // Now add a new language key
                    String key = SystemConfigurationUtilities.getLanguageKey(configItem);
                    configurationService.updateLanguageItem(new Language(ImportExportUtilities.EXPORT_LOCALE_STRING, key, configItem.getName()));
                    ITrackerResources.clearKeyFromBundles(key, true);
                } else if (importData[i] instanceof CustomField && null == model.getExistingModel(i)) {
                    CustomField customField = (CustomField) importData[i];
                    CustomField newCustomField = configurationService.createCustomField(customField);
                    customField.setId(newCustomField.getId());

                    // Now add new language keys.  One for the field and then add one for for
                    // each option that exists.
                    String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
                    // TODO, removed name attribute, so it's defaulted to the key
                    configurationService.updateLanguageItem(new Language(ImportExportUtilities.EXPORT_LOCALE_STRING, key, key));
                    ITrackerResources.clearKeyFromBundles(key, true);
                    if (customField.getFieldType() == CustomField.Type.LIST) {
                        // TODO, removed name attribute, so it's defaulted to the key
                        for (int j = 0; j < customField.getOptions().size(); j++) {
                            String optionKey = CustomFieldUtilities.getCustomFieldOptionLabelKey(customField.getId(), customField.getOptions().get(j).getId());
                            configurationService.updateLanguageItem(new Language(ImportExportUtilities.EXPORT_LOCALE_STRING, optionKey, optionKey));
                            ITrackerResources.clearKeyFromBundles(optionKey, true);
                        }
                    }
                }
            }
            configurationService.resetConfigurationCache();
        } catch (RuntimeException e) {
            log.error("failed to import", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage()));
            return false;
        }

        return true;
    }

    private boolean createUsers(ImportDataModel model, User importer, ActionMessages errors) {
        try {
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();

            AbstractEntity[] importData = model.getData();
            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof User && null == model.getExistingModel(i)) {
                    User user = (User) importData[i];
                    user.setRegistrationType(UserUtilities.REGISTRATION_TYPE_IMPORT);
                    if (model.getCreatePasswords()) {
                        user.setPassword(UserUtilities.encryptPassword(user.getLogin()));
                    }
                    user.setLogin(user.getLogin());
                    int status = user.getStatus();
                    User newUser = userService.createUser(user);
                    user.setId(newUser.getId());
                    newUser.setStatus(status);
                    userService.updateUser(newUser);
                }
            }
        } catch (RuntimeException e) {
            log.error("failed to import", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage()));
            return false;
        } catch (PasswordException e) {
            log.error("failed to import", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage()));
            return false;
        } catch (UserException e) {
            log.error("failed to import", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage()));
            return false;
        }

        return true;
    }

    private boolean createProjects(ImportDataModel model, User importer, ActionMessages errors) {
        try {
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            AbstractEntity[] importData = model.getData();
            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof Project && null == model.getExistingModel(i)) {
                    final Project project = (Project) importData[i];

                    Project importProject = new Project(project.getName());
                    importProject.setStatus(project.getStatus());
                    importProject.setDescription(project.getDescription());

                    importProject = projectService.createProject(importProject, importer.getId());
                    project.setId(importProject.getId());

                    HashSet<Integer> setOfOwnerIDs = new HashSet<Integer>();
                    for (int j = 0; j < project.getOwners().size(); j++) {
                        setOfOwnerIDs.add(project.getOwners().get(j).getId());
                    }
                    projectService.setProjectOwners(importProject, setOfOwnerIDs);

                    HashSet<Integer> setOfFieldIds = new HashSet<Integer>();
                    for (CustomField field: project.getCustomFields()) {
                        setOfFieldIds.add(field.getId());
                    }
                    projectService.setProjectFields(importProject, setOfFieldIds);

                    List<Component> components = project.getComponents();
                    for (Component component: components) {
                        Component newComponent = projectService.addProjectComponent(importProject.getId(), component);
                        component.setId(newComponent.getId());
                    }

                    List<Version> versions = project.getVersions();
                    for (Version version:  versions) {
                        Version newVersion = projectService.addProjectVersion(importProject.getId(), version);
                        version.setId(newVersion.getId());
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error("createProjects: import failed.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage()));
            return false;
        }

        return true;
    }

    private boolean createIssues(ImportDataModel model, User importer, ActionMessages errors) {
        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();

            AbstractEntity[] importData = model.getData();
            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof Issue && null == model.getExistingModel(i)) {

                    Issue issue = (Issue) importData[i];
                    if (issue.getProject().getStatus() != Status.ACTIVE) {
                        log.warn("createIssues: could not create issue for NON-Active project: " + issue);
                        continue;
                    }

                    final List<IssueHistory> history = issue.getHistory();
                    issue.setHistory(new ArrayList<IssueHistory>());

                    List<Component> componentsList = issue.getComponents();
                    issue.setComponents(new ArrayList<Component>());

                    List<Version> versionsList = issue.getVersions();
                    issue.setVersions(new ArrayList<Version>());

                    List<IssueAttachment> attachments = issue.getAttachments();
                    issue.setAttachments(new ArrayList<IssueAttachment>());


                    Issue newIssue = issueService.createIssue(issue,
                            issue.getProject().getId(), issue.getCreator().getId(), importer.getId());
                    issue.setId(newIssue.getId());

                    // Assign the issue
                    if (issue.getOwner() != null) {
                        issueService.assignIssue(issue.getId(), issue.getOwner().getId(), importer.getId());
                    }

                    // Now set Issue Custom Fields
                    // TODO:?
                    List<IssueField> fields = issue.getFields();
                    if (!fields.isEmpty()) {
                        for (IssueField f : fields) {
                            f.setIssue(issue);
                        }
                        issueService.setIssueFields(issue.getId(), issue.getFields());
                    }


                    // Now add all the issue history
                    for (IssueHistory h: history) {
                        h.setIssue(newIssue);
                        issueService.addIssueHistory(h);
                     // needed?   issueService.updateIssue(newIssue, importer.getId());
                    }


                    // Now add components and versions
                    HashSet<Integer> components = new HashSet<Integer>();
                    for (Component c: componentsList) {
                        components.add(c.getId());
                    }
                    if (!components.isEmpty()) {
                        issueService.setIssueComponents(newIssue.getId(), components, importer.getId());
                    }

                    HashSet<Integer> versions = new HashSet<Integer>();
                    for (Version v: versionsList) {
                        versions.add(v.getId());
                    }
                    if (!versions.isEmpty()) {
                        issueService.setIssueVersions(newIssue.getId(), versions, importer.getId());
                    }


                    // Now add any attachments
                    for (IssueAttachment a : attachments) {
                        a.setIssue(newIssue);
                        issueService.addIssueAttachment(a, null);
                    }
                }
            }
        } catch (ProjectException e) {
            log.error("createIssues: import failed.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", e.getMessage()));
            return false;
        }

        return true;
    }

}
  