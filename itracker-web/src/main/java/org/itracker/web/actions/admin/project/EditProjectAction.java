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
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EditProjectAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditProjectAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing project.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            saveToken(request);
            return mapping.getInputForward();

        }
        resetToken(request);

        try {
            ProjectService projectService = ServletContextUtils.getItrackerServices()
                    .getProjectService();
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();

            HttpSession session = request.getSession(true);
            User user = LoginUtilities.getCurrentUser(request);

            String action = request.getParameter("action");

            if ("update".equals(action)) {

                Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);


                Project project = projectService.getProject((Integer) PropertyUtils
                        .getSimpleProperty(form, "id"));
                if (!UserUtilities.hasPermission(userPermissions, project
                        .getId(), PermissionType.PRODUCT_ADMIN)) {
                    return mapping.findForward("unauthorized");
                }
                AdminProjectUtilities.setFormProperties(project,
                        projectService, form, errors);
                if (!errors.isEmpty()) {
                    saveErrors(request, errors);
                    return mapping.getInputForward();
                } else {
                    Integer[] ownersArray = (Integer[]) PropertyUtils
                            .getSimpleProperty(form, "owners");
                    Set<Integer> ownerIds = null == ownersArray ? new HashSet<Integer>()
                            : new HashSet<Integer>(Arrays.asList(ownersArray));
                    AdminProjectUtilities.updateProjectOwners(project,
                            ownerIds, projectService, userService);

                    if (log.isDebugEnabled()) {
                        log.debug("execute: updating existing project: "
                                + project);
                    }
                    projectService.updateProject(project, user
                            .getId());
                }
            } else if ("create".equals(action)) {
                if (!user.isSuperUser()) {
                    return mapping.findForward("unauthorized");
                }

                Project project = new Project();
                AdminProjectUtilities.setFormProperties(project,
                        projectService, form, errors);
                if (!errors.isEmpty()) {
                    saveErrors(request, errors);
                    return mapping.getInputForward();
                }
                project = projectService.createProject(project, user.getId());

                if (log.isDebugEnabled()) {
                    log.debug("execute: created new project: " + project);
                }

                Integer[] users = (Integer[]) PropertyUtils.getSimpleProperty(
                        form, "users");
                if (users != null) {
                    // get the initial project members from create-form
                    Set<Integer> userIds = new HashSet<Integer>(Arrays
                            .asList(users));
                    // get the permissions-set for initial project members
                    Integer[] permissionArray = (Integer[]) PropertyUtils
                            .getSimpleProperty(form, "permissions");
                    Set<Integer> permissions = null == permissionArray ? new HashSet<Integer>(
                            0)
                            : new HashSet<Integer>(Arrays
                            .asList(permissionArray));

                    Integer[] ownersArray = (Integer[]) PropertyUtils
                            .getSimpleProperty(form, "owners");
                    Set<Integer> ownerIds = null == ownersArray ? new HashSet<Integer>()
                            : new HashSet<Integer>(Arrays.asList(ownersArray));

                    // if admin-permission is selected, all permissions will be
                    // granted and users added as project owners
                    if (permissions
                            .contains(UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                        ownerIds.addAll(userIds);
                    } else {
                        // handle special initial user-/permissions-set
                        AdminProjectUtilities.handleInitialProjectMembers(
                                project, userIds, permissions, projectService,
                                userService);
                    }

                    // set project owners with all permissions
                    AdminProjectUtilities.updateProjectOwners(project,
                            ownerIds, projectService, userService);
                }

                if (log.isDebugEnabled()) {
                    log.debug("execute: updating new project: " + project);
                }
                session.removeAttribute(Constants.PROJECT_KEY);
            }
        } catch (RuntimeException e) {
            log.error("execute: Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } catch (IllegalAccessException e) {
            log.error("execute: Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } catch (InvocationTargetException e) {
            log.error("execute: Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } catch (NoSuchMethodException e) {
            log.error("execute: Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            if (log.isDebugEnabled()) {
                log.debug("execute: got errors in action-messages: " + errors);
            }
            return mapping.findForward("error");
        }

        return mapping.findForward("listprojectsadmin");
    }

}
