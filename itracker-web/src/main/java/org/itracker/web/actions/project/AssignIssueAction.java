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

package org.itracker.web.actions.project;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Status;
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;


public class AssignIssueAction extends ItrackerBaseAction {

    private static final Logger log = Logger.getLogger(AssignIssueAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            Integer defaultValue = -1;
            IntegerConverter converter = new IntegerConverter(defaultValue);
            Integer issueId = (Integer) converter.convert(Integer.class, (String) PropertyUtils.getSimpleProperty(form, "issueId"));
            Integer projectId = (Integer) converter.convert(Integer.class, (String) PropertyUtils.getSimpleProperty(form, "projectId"));
            Integer userId = (Integer) converter.convert(Integer.class, (String) PropertyUtils.getSimpleProperty(form, "userId"));

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);
            Integer currUserId = currUser.getId();

            Project project = projectService.getProject(projectId);
            if (project == null) {
                return mapping.findForward("unauthorized");
            }

            if (!userId.equals(currUserId) && !UserUtilities.hasPermission(userPermissions, projectId, UserUtilities.PERMISSION_ASSIGN_OTHERS)) {
                return mapping.findForward("unauthorized");
            } else if (!UserUtilities.hasPermission(userPermissions, projectId, UserUtilities.PERMISSION_ASSIGN_SELF)) {
                return mapping.findForward("unauthorized");
            }

            if (project.getStatus() != Status.ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.projectlocked"));
            } else {
                issueService.assignIssue(issueId, userId, currUserId);
            }
        } catch (RuntimeException e) {
            log.warn("execute: caught exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (IllegalAccessException e) {
            log.warn("execute: caught exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (InvocationTargetException e) {
            log.warn("execute: caught exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        } catch (NoSuchMethodException e) {
            log.warn("execute: caught exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
        }
        return mapping.findForward("index");
    }

}
  