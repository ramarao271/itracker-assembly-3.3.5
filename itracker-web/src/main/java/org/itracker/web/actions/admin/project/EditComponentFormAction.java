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
import org.itracker.model.Component;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ComponentForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.EditComponentFormActionUtil;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


public class EditComponentFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditComponentFormAction.class);


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
            Component component = (Component) session.getAttribute(Constants.COMPONENT_KEY);
            Project project;
            ComponentForm componentForm = (ComponentForm) form;

            if (componentForm == null) {
                componentForm = new ComponentForm();
            }

            if ("create".equals(action)) {
                Integer projectId = (Integer) PropertyUtils.getSimpleProperty(form, "projectId");

                if (action != null && action.equals("create")) {
                    pageTitleKey = "itracker.web.admin.editcomponent.title.create";
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
                            project.getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                        return mapping.findForward("unauthorized");
                    } else {
                        component = new Component();
                        component.setProject(project);
                        componentForm.setAction("create");
                        componentForm.setId(component.getId());
                        componentForm.setProjectId(component.getProject().getId());
                    }
                }
            } else if ("update".equals(action)) {
                Integer componentId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                component = projectService.getProjectComponent(componentId);
                if (action != null && action.equals("update")) {
                    pageTitleKey = "itracker.web.admin.editcomponent.title.update";
                    pageTitleArg = component.getName();
                }
                if (component == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcomponent"));
                } else {
                    project = component.getProject();
                    if (component == null) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
                    } else if (!UserUtilities.hasPermission(userPermissions, component.getProject().getId(), UserUtilities.PERMISSION_PRODUCT_ADMIN)) {
                        return mapping.findForward("unauthorized");
                    } else {
                        componentForm.setAction("update");
                        componentForm.setId(component.getId());

                        componentForm.setProjectId(project.getId());
                        componentForm.setName(component.getName());
                        componentForm.setDescription(component.getDescription());
                    }
                }
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                        new ActionMessage("itracker.web.error.invalidaction"));
            }

            if (errors.isEmpty()) {
                request.setAttribute("componentForm", componentForm);
                session.setAttribute(Constants.COMPONENT_KEY, component);
                saveToken(request);
                request.setAttribute("pageTitleKey", pageTitleKey);
                request.setAttribute("pageTitleArg", pageTitleArg);
                ActionForward af = EditComponentFormActionUtil.init(mapping, request);
                if (af != null) return af;
                return mapping.getInputForward();
            }
        } catch (Exception e) {
            pageTitleKey = "itracker.web.error.title";

            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);

            log.error("Exception while creating edit component form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);

            return mapping.findForward("error");
        }
        return mapping.getInputForward();
    }

}
  