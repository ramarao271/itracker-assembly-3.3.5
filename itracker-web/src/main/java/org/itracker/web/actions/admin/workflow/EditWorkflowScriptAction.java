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
import org.itracker.model.WorkflowScript;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.WorkflowScriptForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;
import org.springframework.dao.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class EditWorkflowScriptAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditWorkflowScriptAction.class);


    public ActionForward execute(ActionMapping mapping, final ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing workflow script.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listworkflow");
        }
        resetToken(request);

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

            WorkflowScript workflowScript = new WorkflowScript();
            WorkflowScriptForm form = (WorkflowScriptForm)actionForm;
            workflowScript.setId(form.getId());
            workflowScript.setName(form.getName());
            workflowScript.setEvent(form.getEvent());
            workflowScript.setScript(form.getScript());
            workflowScript.setLanguage(WorkflowScript.ScriptLanguage.valueOf(form.getLanguage()));

            String action = form.getAction();


            if ("create".equals(action)) {
                workflowScript = configurationService.createWorkflowScript(workflowScript);
            } else if ("update".equals(action)) {
                workflowScript = configurationService.updateWorkflowScript(workflowScript);
            }

            if (log.isDebugEnabled()) {
                log.debug("updated workflowscript was: " + workflowScript);
            }
            if (workflowScript == null) {
                throw new Exception("Error creating/updating workflow script.");
            }
            HttpSession session = request.getSession(true);
            session.removeAttribute(Constants.WORKFLOW_SCRIPT_KEY);
            request.setAttribute("action", action);
            saveToken(request);
            return new ActionForward(mapping.findForward("listworkflow").getPath() + "?id=" + workflowScript.getId() + "&action=update");
        } catch (DataAccessException dae) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message",
                    dae.getRootCause().getMessage(), "Data"));
            saveErrors(request, errors);
            return toInputForward(request, mapping);
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }



    private ActionForward toInputForward(HttpServletRequest request, ActionMapping mapping) {
        saveToken(request);
        return mapping.getInputForward();
    }

}
  