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
import org.itracker.model.Version;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.VersionForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.EditVersionFormActionUtil;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class EditVersionFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditVersionFormAction.class);

    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        String pageTitleKey = "";
        String pageTitleArg = "";


        try {
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            HttpSession session = request.getSession(true);
            String action = request.getParameter("action");
            Map<Integer, Set<PermissionType>> userPermissions = (Map<Integer, Set<PermissionType>>) session.getAttribute(Constants.PERMISSIONS_KEY);

            Version version;
            version = (Version) session.getAttribute(Constants.VERSION_KEY);

            Project project;

            VersionForm versionForm = (VersionForm) form;
            if (versionForm == null) {
                versionForm = new VersionForm();
            }

            Integer projectId = (Integer) PropertyUtils.getSimpleProperty(form, "projectId");

            if ("create".equals(action)) {

                if (action != null && action.equals("create")) {
                    pageTitleKey = "itracker.web.admin.editversion.title.create";
                }

                if (projectId == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage("itracker.web.error.invalidproject"));
                } else {
                    project = projectService.getProject(projectId);

                    if (project == null) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("itracker.web.error.invalidproject"));
                    } else if (!UserUtilities.hasPermission(userPermissions,
                            project.getId(), PermissionType.PRODUCT_ADMIN)) {
                        return mapping.findForward("unauthorized");
                    } else {
                        version = new Version();
                        version.setProject(project);
                        versionForm.setAction("create");
                        versionForm.setDescription(versionForm.getDescription());
                        versionForm.setId(-1);
                        versionForm.setProjectId(version.getProject().getId());
                    }
                }
            } else if ("update".equals(action)) {
                Integer versionId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                version = projectService.getProjectVersion(versionId);
                if (action != null && action.equals("update")) {
                    pageTitleKey = "itracker.web.admin.editversion.title.update";
                    pageTitleArg = version.getNumber();
                }
                if (version == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidversion"));
                } else {
                    if (!UserUtilities.hasPermission(userPermissions, version.getProject().getId(), PermissionType.PRODUCT_ADMIN)) {
                        return mapping.findForward("unauthorized");
                    } else {
                        versionForm.setAction("update");
                        versionForm.setId(version.getId());
                        versionForm.setProjectId(version.getProject().getId());
                        versionForm.setNumber(version.getNumber());
                        versionForm.setDescription(version.getDescription());
                    }
                }
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("itracker.web.error.invalidaction"));
            }

            if (errors.isEmpty()) {
                request.setAttribute("versionForm", versionForm);
                session.setAttribute(Constants.VERSION_KEY, version);
                saveToken(request);
                request.setAttribute("pageTitleKey", pageTitleKey);
                request.setAttribute("pageTitleArg", pageTitleArg);
                ActionForward af = EditVersionFormActionUtil.init(mapping, request);
                if (af != null) return af;
                return mapping.getInputForward();
            }
        } catch (Exception ex) {
            pageTitleKey = "itracker.web.error.title";

            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);

            log.error("Exception while creating edit version form.", ex);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);

            return mapping.findForward("error");
        }
        return mapping.getInputForward();
    }

}
