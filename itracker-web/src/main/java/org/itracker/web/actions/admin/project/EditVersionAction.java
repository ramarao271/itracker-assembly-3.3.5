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

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.Version;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.VersionForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Action for edit a version.
 *
 * @author ranks
 */
public class EditVersionAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditVersionAction.class);

    public EditVersionAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing version.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listprojectsadmin");
        }
        resetToken(request);

        Version version;
        Project project;

        try {
            VersionForm versionForm = (VersionForm) form;
            ProjectService projectService = ServletContextUtils.getItrackerServices()
                    .getProjectService();

            HttpSession session = request.getSession(true);
            Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);

            Integer projectId = versionForm.getProjectId();

            if (projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidproject"));
            } else {
                project = projectService.getProject(projectId);

                if (project == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(
                                    "itracker.web.error.invalidproject"));
                } else {
                    boolean authorised = UserUtilities.hasPermission(
                            userPermissions, project.getId(),
                            UserUtilities.PERMISSION_PRODUCT_ADMIN);

                    if (!authorised) {
                        return mapping.findForward("unauthorised");
                    } else {

                        String action = (String) request.getParameter("action");

                        if ("create".equals(action)) {
                            version = new Version(project, versionForm
                                    .getNumber());
                            version
                                    .setDescription(versionForm
                                            .getDescription());
                            version = projectService.addProjectVersion(project
                                    .getId(), version);
                        } else if ("update".equals(action)) {
                            version = projectService
                                    .getProjectVersion(versionForm.getId());
                            version.setNumber(versionForm.getNumber());
                            version.setProject(project);
                            version
                                    .setDescription(versionForm
                                            .getDescription());
                            version = projectService
                                    .updateProjectVersion(version);
                        }
                        session.removeAttribute(Constants.VERSION_KEY);

                        return new ActionForward(mapping.findForward(
                                "editproject").getPath()
                                + "?id=" + project.getId() + "&action=update");
                    }
                }
            }
        } catch (RuntimeException ex) {
            log.error("Exception processing form data", ex);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
