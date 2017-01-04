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

package org.itracker.web.actions.admin.workflow;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.SystemConfigurationException;
import org.itracker.model.NameValuePair;
import org.itracker.model.WorkflowScript;
import org.itracker.model.util.UserUtilities;
import org.itracker.model.util.WorkflowUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.WorkflowScriptForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class EditWorkflowScriptFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditWorkflowScriptFormAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        boolean isUpdate = false;

        try {
            WorkflowScriptForm workflowScriptForm = (WorkflowScriptForm) form;

            if (workflowScriptForm == null) {
                workflowScriptForm = new WorkflowScriptForm();
            }
            String action = workflowScriptForm.getAction();


            WorkflowScript workflowScript = new WorkflowScript();
            if ("update".equals(action)) {
                ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();


                Integer id = (Integer) PropertyUtils.getSimpleProperty(workflowScriptForm, "id");
                workflowScript = configurationService.getWorkflowScript(id);

                if (workflowScript == null) {
                    throw new SystemConfigurationException("Invalid workflow script id " + id);
                }

                workflowScriptForm.setAction("update");
                workflowScriptForm.setId(workflowScript.getId());
                workflowScriptForm.setName(workflowScript.getName());
                workflowScriptForm.setEvent(workflowScript.getEvent());
                workflowScriptForm.setScript(workflowScript.getScript());
                workflowScriptForm.setLanguage(workflowScript.getLanguage().name());


            } else {
                workflowScriptForm.setLanguage(WorkflowScript.ScriptLanguage.BeanShell.name());
            }


            if (errors.isEmpty()) {
                HttpSession session = request.getSession(true);
                request.setAttribute("workflowScriptForm", workflowScriptForm);
                session.setAttribute(Constants.WORKFLOW_SCRIPT_KEY, workflowScript);
                request.setAttribute("action", action);
                saveToken(request);

                setupFormEventTypes(workflowScriptForm, getLocale(request));

                return mapping.getInputForward();
            }
        } catch (SystemConfigurationException sce) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidworkflowscript"));
        } catch (Exception e) {
            log.error("Exception while creating edit workflowScript form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

    public static void setupFormEventTypes(WorkflowScriptForm form, Locale locale) {
        form.initEventOptions(locale);
    }
}
  