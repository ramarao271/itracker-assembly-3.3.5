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

package org.itracker.web.actions.user;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.UserException;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.core.AuthenticationConstants;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SelfRegisterAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(SelfRegisterAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        resetToken(request);

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
                    .getConfigurationService();

            boolean allowSelfRegister = configurationService
                    .getBooleanProperty("allow_self_register", false);

            if (!allowSelfRegister) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.notenabled"));
            } else {
                UserForm regForm = (UserForm) form;

                User user = new User(regForm.getLogin(), UserUtilities
                        .encryptPassword(regForm.getPassword()), regForm
                        .getFirstName(), regForm.getLastName(), regForm
                        .getEmail(), UserUtilities.REGISTRATION_TYPE_SELF,
                        false);

                if (!user.hasRequiredData()) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(
                                    "itracker.web.error.missingfields"));
                } else {
                    UserService userService = ServletContextUtils.getItrackerServices().getUserService();


                    try {
                        if (userService
                                .allowRegistration(
                                        user,
                                        regForm.getPassword(),
                                        AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                                        AuthenticationConstants.REQ_SOURCE_WEB)) {
                            user = userService.createUser(user);

                            // TODO: remove this hack, this should be handled central, there are other
                            // instances of this hack
                            UserPreferences userPrefs = user.getPreferences();
                            if (userPrefs == null) {
                                userPrefs = new UserPreferences();
                                user.setPreferences(userPrefs);
                                userPrefs.setUser(user);
                            }
                            user.getPreferences().setUserLocale(String.valueOf(LoginUtilities.getCurrentLocale(request)));


                            Notification notification = new Notification();
                            notification.setUser(user);
                            notification.setRole(Role.ANY);
                            getITrackerServices().getNotificationService()
                                    .sendNotification(notification,
                                            Type.SELF_REGISTER,
                                            getBaseURL(request));
                        } else {
                            errors
                                    .add(
                                            ActionMessages.GLOBAL_MESSAGE,
                                            new ActionMessage(
                                                    "itracker.web.error.register.unauthorized"));
                        }
                    } catch (UserException ue) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage(
                                        "itracker.web.error.existinglogin"));
                    }
                }
            }
        } catch (Exception e) {
            log.info("Error during self registration.  " + e.getMessage());
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.register.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        return mapping.findForward("arrivalforward");
    }

}
