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

package org.itracker.web.actions.admin.user;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EditUserFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditUserFormAction.class);

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        HttpSession session = request.getSession(true);
        String action = request.getParameter("action");
        String pageTitleKey;
        String pageTitleArg = "";
        boolean isUpdate = ( action != null && action.equals("update") );


        try {

            UserService userService = ServletContextUtils.getItrackerServices().getUserService();
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            List<Project> projects;
            User editUser = null;
            HashMap<Integer, HashMap<String, Permission>> userPermissions = new HashMap<Integer, HashMap<String, Permission>>();

            List<NameValuePair> permissionNames = UserUtilities.getPermissionTypeNames(getLocale(request));
            UserForm userForm = (UserForm) form;

            if (userForm == null) {
                userForm = new UserForm();
            }

            if ("create".equals(action)) {

                if (!userService.allowProfileCreation(null, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofilecreates"));
                    saveErrors(request, errors);

                    return mapping.findForward("error");
                }

                editUser = new User();
                editUser.setId(-1);
                editUser.setStatus(UserUtilities.STATUS_ACTIVE);
                userForm.setAction("create");
                userForm.setId(editUser.getId());

            } else if ("update".equals(action)) {

                Integer userId = userForm.getId();

                if (userId == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invaliduser"));
                } else {

                    editUser = userService.getUser(userId);

                    if (editUser == null) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invaliduser"));
                    } else {

                        userForm.setAction("update");
                        userForm.setId(editUser.getId());
                        userForm.setLogin(editUser.getLogin());
                        userForm.setFirstName(editUser.getFirstName());
                        userForm.setLastName(editUser.getLastName());
                        userForm.setEmail(editUser.getEmail());
                        userForm.setSuperUser(editUser.isSuperUser());

                        List<Permission> permissionList = userService.getPermissionsByUserId(editUser.getId());
                        HashMap<String, Boolean> formPermissions = new HashMap<>();

                        boolean allowProfileUpdate = userService.allowProfileUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
                        request.setAttribute("allowProfileUpdate", allowProfileUpdate);

                        boolean allowPasswordUpdate = userService.allowPasswordUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
                        request.setAttribute("allowPasswordUpdate", allowPasswordUpdate);

                        boolean allowPermissionUpdate = userService.allowPermissionUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
                        request.setAttribute("allowPermissionUpdate", allowPermissionUpdate);

                        if (editUser.getId() > 0) {
                            request.setAttribute("isUpdate", true);
                        }

                        for (int i = 0; i < permissionList.size(); i++) {

                            log.debug("Processing permission type: " + permissionList.get(i).getPermissionType());

                            //if getPermissionType returned -1, this is a SuperUser. He will still be able to set project permissions.  

                            if (permissionList.size() > 0 && permissionList.get(0).getPermissionType() == PermissionType.USER_ADMIN) {

                                if (permissionList.size() > 1 && i != 0) {

                                    Integer projectId = permissionList.get(i).getProject().getId();

                                    if (userPermissions.get(projectId) == null) {
                                        HashMap<String, Permission> projectPermissions = new HashMap<>();
                                        userPermissions.put(permissionList.get(i).getProject().getId(), projectPermissions);
                                    }

                                    formPermissions.put(permissionList.get(i).getPermissionType().name(permissionList.get(i).getProject()), true);

                                    PermissionType permissionType = permissionList.get(i).getPermissionType();

                                    Permission thisPermission = permissionList.get(i);
                                    HashMap<String, Permission> permissionHashMap = userPermissions.get(projectId);
                                    permissionHashMap.put(String.valueOf(permissionType), thisPermission);

                                }

                            } else {

                                Integer projectId = permissionList.get(i).getProject().getId();

                                if (userPermissions.get(projectId) == null) {
                                    HashMap<String, Permission> projectPermissions = new HashMap<>();
                                    userPermissions.put(permissionList.get(i).getProject().getId(), projectPermissions);
                                }

                                formPermissions.put(permissionList.get(i).getPermissionType().name(permissionList.get(i).getProject()), true);

                                PermissionType permissionType = permissionList.get(i).getPermissionType();

                                Permission thisPermission = permissionList.get(i);
                                HashMap<String, Permission> permissionHashMap =  userPermissions.get(projectId);
                                permissionHashMap.put(String.valueOf(permissionType), thisPermission);

                            }

                        }

                        userForm.setPermissions(formPermissions);
                    }
                }

            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if (editUser == null) {
                return mapping.findForward("unauthorized");
            }
            if (isUpdate) {
                pageTitleKey = "itracker.web.admin.edituser.title.update";
                pageTitleArg = editUser.getLogin();

            } else {
                pageTitleKey = "itracker.web.admin.edituser.title.create";
            }

            request.setAttribute("isUpdate", isUpdate);
            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);
            if (errors.isEmpty()) {

                String userStatus = UserUtilities.getStatusName(editUser.getStatus());
                request.setAttribute("userStatus", userStatus);

                projects = projectService.getAllAvailableProjects();
                Collections.sort(projects, Project.PROJECT_COMPARATOR);
                request.setAttribute(Constants.PROJECTS_KEY, projects);

                request.setAttribute("userForm", userForm);
                session.setAttribute(Constants.EDIT_USER_KEY, editUser);
                session.setAttribute(Constants.EDIT_USER_PERMS_KEY, userPermissions);
                request.setAttribute("permissionNames", permissionNames);
                request.setAttribute("permissionRowColIdxes", new Integer[]{0, 1});
                saveToken(request);

                return mapping.findForward("edituserform");

            }

        } catch (Exception e) {
            log.error("Exception while creating edit user form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");

    }

}
