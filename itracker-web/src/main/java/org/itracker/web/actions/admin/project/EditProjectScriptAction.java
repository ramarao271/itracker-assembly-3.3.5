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

package org.itracker.web.actions.admin.project;

//import java.io.ByteArrayInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectScriptForm;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


public class EditProjectScriptAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditProjectScriptAction.class);

    public EditProjectScriptAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        if (!LoginUtilities.hasPermission(PermissionType.USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing workflow script.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listprojectscript");
        }
        resetToken(request);
        ProjectScriptForm projectScriptForm = (ProjectScriptForm) form;

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            Integer projectId = projectScriptForm.getProjectId();
            Project project = projectService.getProject(projectId);
            HashMap<String, String> fieldIds = projectScriptForm.getFieldId();
            HashMap<String, String> priorities = projectScriptForm.getPriority();
            HashMap<String, String> scriptItems = projectScriptForm.getScriptItems();
            for (Iterator<String> siIterator = scriptItems.keySet().iterator(); siIterator.hasNext(); ) {
                String key = siIterator.next();
                if (key != null) {
                    String scriptItemsvalue =  scriptItems.get(key);
                    if ( (!StringUtils.isBlank(scriptItemsvalue))
                            && StringUtils.equalsIgnoreCase(StringUtils.trim(scriptItemsvalue), "on") ) {
                        Integer wfsIds = Integer.valueOf(key);
                        Integer fieldId = null;
                        Configuration.Type fieldType = Configuration.Type.customfield;
                        if (NumberUtils.isNumber(fieldIds.get(key))) {
                            fieldId = Integer.valueOf(fieldIds.get(key));
                        } else {
                            fieldType = Configuration.Type.valueOf(fieldIds.get(key));
                            fieldId = fieldType.getCode();
                        }

                        Integer priority = Integer.valueOf(priorities.get(key));
                        WorkflowScript workflowScript = configurationService.getWorkflowScript(wfsIds);
                        ProjectScript projectScript = new ProjectScript();

                        projectScript.setFieldId(fieldId);
                        projectScript.setFieldType(fieldType);
                        projectScript.setPriority(priority);
                        projectScript.setProject(project);
                        projectScript.setScript(workflowScript);
                        projectService.addProjectScript(projectId, projectScript);

                    }
                }
            }

            saveToken(request);
            return new ActionForward(
                    mapping.findForward("editproject").getPath()
                            + "?id=" + project.getId() + "&action=update");

        } catch (RuntimeException e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.details", e.getMessage()));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }


}
