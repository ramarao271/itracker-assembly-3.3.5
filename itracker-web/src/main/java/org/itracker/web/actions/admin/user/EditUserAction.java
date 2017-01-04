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
import org.itracker.UserException;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.ServletContextUtils;
import org.itracker.web.util.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class EditUserAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditUserAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing component.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listusers");
        }
        resetToken(request);

        UserForm userForm = (UserForm) form;
        if (userForm == null) {
            return mapping.findForward("listusers");
        }

        ActionForward forward = setupJspEnv(request, userForm, errors, mapping);


        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return forward;
    }


    public static ActionForward setupJspEnv(HttpServletRequest request, UserForm userForm, ActionMessages errors, ActionMapping mapping) {

        try {
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            String previousLogin = userForm.getLogin();
            User editUser;
            // if userForm.getID returns -1, then this is a new user.. 
            if (userForm.getId() != -1) {
                editUser = userService.getUser(userForm.getId());
                previousLogin = editUser.getLogin();
            } else {
                editUser = new User();
            }


            editUser.setLogin(userForm.getLogin());
            editUser.setFirstName(userForm.getFirstName());
            editUser.setLastName(userForm.getLastName());
            editUser.setEmail(userForm.getEmail());
            editUser.setSuperUser(userForm.isSuperUser());

            try {
                if ("create".equals(userForm.getAction())) {
                    if (!userService.allowProfileCreation(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofilecreates"));
                        return mapping.findForward("error");
                    }

                    log.debug("Creating new userid.");
                    editUser.setRegistrationType(UserUtilities.REGISTRATION_TYPE_ADMIN);
                    if (null != userForm.getPassword() && userForm.getPassword().length() > 0) {
                        if (userService.allowPasswordUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                            editUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));
                        } else {
                            // Passwort was attempted to set, but authenticator is not able to. Exception
//	                    	itracker.web.error.nopasswordupdates
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.nopasswordupdates"));
                            return mapping.findForward("error");
                        }
                    }
                    editUser = userService.createUser(editUser);
                } else if ("update".equals(userForm.getAction())) {
                    User existingUser = editUser;//userService.getUser(editUser.getId());
                    if (log.isDebugEnabled()) {
                        log.debug("execute: updating existingUser " + existingUser);
                    }

                     previousLogin = existingUser.getLogin();
                     if (!userService.allowProfileUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                         editUser = existingUser;
//                            itracker.web.error.noprofileupdates
                         errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofileupdates"));
                         return mapping.findForward("error");
                     }


                     if (null != userForm.getPassword() && !userForm.getPassword().equals("")) {
                         if (userService.allowPasswordUpdates(existingUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {

                             editUser.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));


                         } else {
                             // Passwort was attempted to set, but authenticator is not able to. Exception
                             editUser = existingUser;
//		                            itracker.web.error.nopasswordupdates
                             errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.nopasswordupdates"));
                             return mapping.findForward("error");
                         }
                     }

                     if (log.isDebugEnabled()) {
                         log.debug("execute: applying updates on user " + editUser);
                     }
                     editUser = userService.updateUser(editUser);
                     if (log.isDebugEnabled()) {
                         log.debug("execute: applied updates on user " + editUser);
                     }

                } else {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
                }
            } catch (UserException ue) {
                ue.printStackTrace();
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.existinglogin"));

                mapping.findForward("error");
            }

            if (errors.isEmpty() && userService.allowPermissionUpdates(editUser, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                Map<String, Boolean> permissionsMap = userForm.getPermissions();
                List<Permission> newPermissions = new ArrayList<Permission>();


                Iterator<String> iter = permissionsMap.keySet().iterator();
                while (iter.hasNext()) {
                    String paramName = iter.next();
                    Integer projectIntValue = new Integer(paramName.substring(paramName.lastIndexOf('#') + 1));
                    Project project = projectService.getProject(projectIntValue);
                    PermissionType permissionType = PermissionType.valueOf(paramName.substring(0, paramName.lastIndexOf('#')));
                    Permission newPermission = new Permission(permissionType, editUser, project);
                    newPermission.setCreateDate(new Date());
                    newPermissions.add(newPermission);
                }

                boolean successful = userService.setUserPermissions(editUser.getId(), newPermissions);
                if (successful) {
                    log.debug("User Permissions have been nicely set.");

                } else {
                    log.debug("No good. User Permissions have not been nicely set.");
                }
            }

            if (errors.isEmpty()) {
                if (!previousLogin.equals(editUser.getLogin())) {
                    if (SessionManager.getSessionStart(previousLogin) != null) {
                        SessionManager.addRenamedLogin(previousLogin, editUser.getLogin());
                        SessionManager.setSessionNeedsReset(previousLogin);
                    }
                } else {
                    if (SessionManager.getSessionStart(editUser.getLogin()) != null) {
                        SessionManager.setSessionNeedsReset(editUser.getLogin());
                    }
                }

                log.debug("Forwarding to list users.");
                return mapping.findForward("listusers");
            }
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        return mapping.getInputForward();
    }
}
  