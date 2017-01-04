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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.CustomField;
import org.itracker.model.Project;
import org.itracker.model.ProjectScript;
import org.itracker.model.WorkflowScript;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectScriptForm;
import org.itracker.web.util.EditProjectFormActionUtil;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * TODO maybe intended to be an 'action' for the EditProjectScriptAction?
 */
public class EditProjectScriptFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditProjectScriptFormAction.class);

    public EditProjectScriptFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }
        boolean isUpdate = false;
        String pageTitleKey = "";
        String pageTitleArg = "";
        String action = "";
        Project project;

        try {

            ProjectScriptForm projectScriptForm = (ProjectScriptForm) form;
            final ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();
            final ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

            if (projectScriptForm == null) {
                projectScriptForm = new ProjectScriptForm();
            }
            final List<WorkflowScript> workflowScripts = configurationService.getWorkflowScripts();


            Integer projectId = (Integer) PropertyUtils.getSimpleProperty(projectScriptForm, "projectId");
            projectScriptForm.setProjectId(projectId);
            project = projectService.getProject(projectId);

            final List<ProjectScript> projectScripts = project.getScripts();
            pageTitleKey = "itracker.web.admin.editprojectscript.title.create";


            if (errors.isEmpty()) {
                request.setAttribute("workflowScripts", workflowScripts);
                request.setAttribute("projectScripts", projectScripts);

                final Locale locale = LoginUtilities.getCurrentLocale(request);
                Map<String, String> customFieldsMapped = new HashMap<String, String>(project.getCustomFields().size());
                for (CustomField field: project.getCustomFields()) {
                    String name = CustomFieldUtilities.getCustomFieldName(field.getId(), locale);
                    name += " ";
                    name += CustomFieldUtilities.getTypeString(field.getFieldType(), locale);
                    customFieldsMapped.put(String.valueOf(field.getId()), name);
                }

                request.setAttribute("customFields", customFieldsMapped);
                request.setAttribute("projectScriptForm", projectScriptForm);
                EditProjectFormActionUtil.setUpPrioritiesInEnv(request);
                request.setAttribute("project", project);

                saveToken(request);
                request.setAttribute("pageTitleKey", pageTitleKey);
                request.setAttribute("pageTitleArg", pageTitleArg);
                return mapping.getInputForward();
            }
        } catch (RuntimeException e) {
            log.error("Exception while the " + action + " of ProjectScript form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (IllegalAccessException e) {
            log.error("Exception while the " + action + " of ProjectScript form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (InvocationTargetException e) {
            log.error("Exception while the " + action + " of ProjectScript form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (NoSuchMethodException e) {
            log.error("Exception while the " + action + " of ProjectScript form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);
        request.setAttribute("isUpdate", isUpdate);
        return mapping.findForward("error");
    }

}
