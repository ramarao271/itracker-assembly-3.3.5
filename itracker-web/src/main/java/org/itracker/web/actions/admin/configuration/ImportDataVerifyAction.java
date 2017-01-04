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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.itracker.ImportExportException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.ImportExportUtilities;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Performs a verification on the import data to ensure that it contains no errors,
 * applies any import options, and also updates the data to reuse any current system
 * data if needed.  It also collects statistics on the import data to display to the user
 * before the import is actually processed.
 * <br><br>
 * When reusing existing system data.  The following criteria is used to determine if
 * a piece of data matches an existing system resource:<br>
 * User - the login<br>
 * Project - the project name<br>
 * Status, Severity, Resolution - the name of the item as defined in the language root/base locale<br>
 * Custom Fields - the label name of the custom field as defined in the language root/base locale<br>
 */
public class ImportDataVerifyAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(ImportDataVerifyAction.class);

    private static final int UPDATE_STATUS = 1;
    private static final int UPDATE_SEVERITY = 2;
    private static final int UPDATE_RESOLUTION = 3;

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!LoginUtilities.hasPermission(PermissionType.USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            FormFile file = (FormFile) PropertyUtils.getSimpleProperty(form, "importFile");
            ImportDataModel model = new ImportDataModel();
            AbstractEntity[] importData = ImportExportUtilities.importIssues(new InputStreamReader(file.getInputStream()));
            AbstractEntity[] existingModel = new AbstractEntity[importData.length];

            model.setReuseUsers((Boolean) PropertyUtils.getSimpleProperty(form, "optionreuseusers"));
            model.setReuseProjects((Boolean) PropertyUtils.getSimpleProperty(form, "optionreuseprojects"));
            model.setReuseConfig((Boolean) PropertyUtils.getSimpleProperty(form, "optionreuseconfig"));
            model.setCreatePasswords((Boolean) PropertyUtils.getSimpleProperty(form, "optioncreatepasswords"));
            model.setData(importData, existingModel);

            InitialContext ic = new InitialContext();
            checkConfig(model, ic);
            log.debug(model.toString());
            checkUsers(model, ic);
            log.debug(model.toString());
            checkProjects(model, ic);
            log.debug(model.toString());
            checkIssues(model, ic);
            log.debug(model.toString());

            HttpSession session = request.getSession(true);
            session.setAttribute(Constants.IMPORT_DATA_KEY, model);
        } catch (ImportExportException iee) {
            if (iee.getType() == ImportExportException.TYPE_INVALID_LOGINS) {
                log.error("Invalid logins found while verifying import data.");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.importexport.invalidlogins", iee.getMessage()));
            } else if (iee.getType() == ImportExportException.TYPE_INVALID_STATUS) {
                log.error("Invalid status found while verifying import data.");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.importexport.invalidstatus", iee.getMessage()));
            } else {
                log.error("Exception while verifying import data.", iee);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            }
        } catch (Exception e) {
            log.error("Exception while verifying import data.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        return mapping.findForward("importdataverify");
    }

    private void checkConfig(ImportDataModel model, InitialContext ic) throws ImportExportException {
        try {
            int maxSeverityValue = 1;
            int maxResolutionValue = 1;

            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            List<Configuration> statuses = configurationService.getConfigurationItemsByType(Configuration.Type.status, ImportExportUtilities.EXPORT_LOCALE);
            List<Configuration> severities = configurationService.getConfigurationItemsByType(Configuration.Type.severity, ImportExportUtilities.EXPORT_LOCALE);
            List<Configuration> resolutions = configurationService.getConfigurationItemsByType(Configuration.Type.resolution, ImportExportUtilities.EXPORT_LOCALE);
            List<CustomField> fields = configurationService.getCustomFields();

            for (int i = 0; i < severities.size(); i++) {
                maxSeverityValue = Math.max(maxSeverityValue, Integer.parseInt(severities.get(i).getValue()));
            }
            for (int i = 0; i < resolutions.size(); i++) {
                maxResolutionValue = Math.max(maxResolutionValue, Integer.parseInt(resolutions.get(i).getValue()));
            }

            AbstractEntity[] importData = model.getData();
            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof Configuration) {
                    // Need to check to see if it finds a matching name.  If so change value.
                    // For status, if it finds a matching value but not name, this is an error.
                    // Otherwise, just change the value for the resolution and severity.  Then iterate
                    // through the issues and update the old value to the new one since they are all stored
                    // as strings/ints, not the id to the config item.
                    Configuration configItem = (Configuration) importData[i];
                    if (configItem.getType() == Configuration.Type.status) {
                        boolean found = false;
                        for (int j = 0; j < statuses.size(); j++) {
                            if (model.getReuseConfig() && statuses.get(j).getName().equalsIgnoreCase(configItem.getName())) {
                                // Matching status, update issues
                                updateIssues(importData, UPDATE_STATUS, configItem.getValue(), statuses.get(j).getValue());
                                model.setExistingModel(i, configItem);
                                model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_STATUSES, ImportExportUtilities.IMPORT_STAT_REUSED);
                                found = true;
                                break;
                            } else if (statuses.get(j).getValue().equalsIgnoreCase(configItem.getValue())) {
                                // Found a matching status value, and the name didn't match
                                throw new ImportExportException(configItem.getValue(), ImportExportException.TYPE_INVALID_STATUS);
                            }
                        }
                        if (!found) {
                            model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_STATUSES, ImportExportUtilities.IMPORT_STAT_NEW);
                        }
                    } else if (configItem.getType() == Configuration.Type.severity) {
                        boolean found = false;
                        if (model.getReuseConfig()) {
                            for (int j = 0; j < severities.size(); j++) {
                                if (severities.get(j).getName().equalsIgnoreCase(configItem.getName())) {
                                    // Matching severity, update issues
                                    updateIssues(importData, UPDATE_SEVERITY, configItem.getValue(), severities.get(j).getValue());
                                    model.setExistingModel(i, configItem);
                                    model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_SEVERITIES, ImportExportUtilities.IMPORT_STAT_REUSED);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            updateIssues(importData, UPDATE_SEVERITY, configItem.getValue(), Integer.toString(++maxSeverityValue));
                            configItem.setValue(Integer.toString(maxSeverityValue));
                            model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_SEVERITIES, ImportExportUtilities.IMPORT_STAT_NEW);
                        }
                    } else if (configItem.getType() == Configuration.Type.resolution) {
                        boolean found = false;
                        if (model.getReuseConfig()) {
                            for (int j = 0; j < resolutions.size(); j++) {
                                if (resolutions.get(j).getName().equalsIgnoreCase(configItem.getName())) {
                                    // Matching resolution, update issues
                                    updateIssues(importData, UPDATE_RESOLUTION, configItem.getValue(), resolutions.get(j).getValue());
                                    model.setExistingModel(i, configItem);
                                    model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_RESOLUTIONS, ImportExportUtilities.IMPORT_STAT_REUSED);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            updateIssues(importData, UPDATE_RESOLUTION, configItem.getValue(), Integer.toString(++maxResolutionValue));
                            configItem.setValue(Integer.toString(maxResolutionValue));
                            model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_RESOLUTIONS, ImportExportUtilities.IMPORT_STAT_NEW);
                        }
                    }
                } else if (importData[i] instanceof CustomField) {
                    boolean found = false;
                    CustomField field = (CustomField) importData[i];
                    if (model.getReuseFields()) {
                        for (int j = 0; j < fields.size(); j++) {
                            if (fields.get(j).getFieldType() == field.getFieldType() /*&& fields.get(j).getName().equalsIgnoreCase(field.getName())*/) {
                                // Matching custom field.  Set id, but don't need to update issues
                                // since it contains the customfield model
                                field.setId(fields.get(j).getId());
                                model.setExistingModel(i, field);
                                model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_FIELDS, ImportExportUtilities.IMPORT_STAT_REUSED);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_FIELDS, ImportExportUtilities.IMPORT_STAT_NEW);
                    }
                }
            }
        } catch (ImportExportException iee) {
            throw iee;
        } catch (Exception e) {
            log.error("Error verifiying import data.", e);
            throw new ImportExportException(e.getMessage());
        }
    }

    private void checkUsers(ImportDataModel model, InitialContext ic) throws ImportExportException {
        String invalidLogins = null;

        try {
            UserService userService = getITrackerServices().getUserService();

            AbstractEntity[] importData = model.getData();

            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof User) {
                    User user = (User) importData[i];
                    User existingUser;
                    try {
                        existingUser = userService.getUserByLogin(user.getLogin());
                    } catch (NoSuchEntityException e) {
                        existingUser = null;
                    }
                    if (existingUser != null) {
                        if (model.getReuseUsers()) {
                            user.setId(existingUser.getId());
                            model.setExistingModel(i, existingUser);
                            model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_USERS, ImportExportUtilities.IMPORT_STAT_REUSED);
                            log.debug("Reusing existing user " + user.getLogin() + "(" + user.getId() + ") during import.");
                        } else {
                            log.debug("Existing user " + existingUser.getLogin() + "(" + existingUser.getId() + ") during import.  Adding to invalid login list.");
                            invalidLogins = (invalidLogins == null ?
                                    existingUser.getLogin() :
                                    new StringBuffer(invalidLogins).append(", ").append(existingUser.getLogin()).toString());
                        }
                    } else {
                        model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_USERS, ImportExportUtilities.IMPORT_STAT_NEW);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error verifying import data.", e);
            throw new ImportExportException(e.getMessage());
        }

        if (invalidLogins != null) {
            throw new ImportExportException(invalidLogins, ImportExportException.TYPE_INVALID_LOGINS);
        }
    }

    private void checkProjects(ImportDataModel model, InitialContext ic) throws ImportExportException {
        try {
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            List<Project> existingProjects = projectService.getAllProjects();

            AbstractEntity[] importData = model.getData();

            for (int i = 0; i < importData.length; i++) {
                if (importData[i] instanceof Project) {
                    if (!model.getReuseProjects()) {
                        model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_PROJECTS, ImportExportUtilities.IMPORT_STAT_NEW);
                        continue;
                    }

                    Project project = (Project) importData[i];
                    for (int j = 0; j < existingProjects.size(); j++) {
                        log.debug("Project Name: " + project.getName() + "  Existing Project: " + existingProjects.get(j).getName());
                        log.debug("Project Name: " + ITrackerResources.escapeUnicodeString(project.getName(), false) + "  Existing Project: " + ITrackerResources.escapeUnicodeString(existingProjects.get(j).getName(), false));
                        if (project.getName() != null && project.getName().equalsIgnoreCase(existingProjects.get(j).getName())) {
                            project.setId(existingProjects.get(j).getId());
                            model.setExistingModel(i, project);
                            model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_PROJECTS, ImportExportUtilities.IMPORT_STAT_REUSED);

                            log.debug("Reusing existing project " + project.getName() + "(" + project.getId() + ") during import.");
                            break;
                        }
                    }
                    model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_PROJECTS, ImportExportUtilities.IMPORT_STAT_NEW);

                }
            }
        } catch (Exception e) {
            log.error("Error verifiying import data.", e);
            throw new ImportExportException(e.getMessage());
        }
    }

    private void checkIssues(ImportDataModel model, InitialContext ic) throws ImportExportException {
        AbstractEntity[] importData = model.getData();

        for (int i = 0; i < importData.length; i++) {
            if (importData[i] instanceof Issue) {
                model.addVerifyStatistic(ImportExportUtilities.IMPORT_STAT_ISSUES, ImportExportUtilities.IMPORT_STAT_NEW);
            }
        }
    }

    private void updateIssues(AbstractEntity[] models, int updateType, String currentValue, String newValue) throws ImportExportException {
        if (models == null || currentValue == null || newValue == null) {
            return;
        }

        try {
            for (int i = 0; i < models.length; i++) {
                if (models[i] instanceof Issue) {
                    Issue issue = (Issue) models[i];
                    if (updateType == UPDATE_STATUS && issue.getStatus() == Integer.parseInt(currentValue)) {
                        issue.setStatus(Integer.parseInt(newValue));
                    } else if (updateType == UPDATE_SEVERITY && issue.getSeverity() == Integer.parseInt(currentValue)) {
                        issue.setSeverity(Integer.parseInt(newValue));
                    } else if (updateType == UPDATE_RESOLUTION && currentValue.equalsIgnoreCase(issue.getResolution())) {
                        issue.setResolution(newValue);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Unable to update configuration data in issues.", e);
            throw new ImportExportException("Unable to update configuration data in issues: " + e.getMessage());
        }
    }
}
  