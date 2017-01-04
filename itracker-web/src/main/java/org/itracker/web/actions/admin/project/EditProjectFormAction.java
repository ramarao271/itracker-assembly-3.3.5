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
import org.itracker.model.CustomField;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ProjectForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class EditProjectFormAction extends ItrackerBaseAction {

    private static final Logger log = Logger.getLogger(EditProjectFormAction.class);


    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        try {
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            HttpSession session = request.getSession(true);


            Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);
            User user = (User) session.getAttribute(Constants.USER_KEY);

            ProjectForm projectForm = (ProjectForm) form;

            if (projectForm == null) {
                return mapping.getInputForward();
            }

            Project project;
            if (projectForm.getAction() != null && projectForm.getAction().equals("update")) {
                project = null;
                if (null == projectForm.getId()) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
                } else {
                    project = projectService.getProject(projectForm.getId());
                    if (null == project) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
                    }
                }

            } else {
                project = new Project();
            }


            if ("create".equals(projectForm.getAction())) {
                if (!user.isSuperUser()) {
                    return mapping.findForward("unauthorized");
                }
                projectForm.setId(project.getId());
            } else if ("update".equals(projectForm.getAction())) {


                if (errors.isEmpty()) {

                    if (!UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                        return mapping.findForward("unauthorized");
                    } else if (errors.isEmpty()) {
                        projectForm.setId(project.getId());
                        projectForm.setName(project.getName());
                        projectForm.setDescription(project.getDescription());
                        projectForm.setStatus(project.getStatus().getCode());
                        int currentOptions = project.getOptions();
                        projectForm.setOptions(ProjectUtilities.getOptions(currentOptions));

                        List<CustomField> fields = project.getCustomFields();
                        Integer[] fieldIds = new Integer[fields.size()];
                        for (int i = 0; i < fields.size(); i++) {
                            fieldIds[i] = fields.get(i).getId();
                        }
                        projectForm.setFields(fieldIds);

                        List<User> owners = project.getOwners();
                        Integer[] ownerIds = new Integer[owners.size()];

                        for (int i = 0; i < owners.size(); i++) {
                            ownerIds[i] = owners.get(i).getId();
                        }

                        projectForm.setOwners(ownerIds);
                    }
                }

            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if (errors.isEmpty()) {
                request.setAttribute("projectForm", projectForm);
                session.setAttribute(Constants.PROJECT_KEY, project);
                saveToken(request);
                ActionForward af = projectForm.init(mapping, request);
                if (af != null) {
                    return af;
                }
                return mapping.getInputForward();
            }

        } catch (Exception e) {
            log.error("Exception while creating edit project form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");

    }

}
