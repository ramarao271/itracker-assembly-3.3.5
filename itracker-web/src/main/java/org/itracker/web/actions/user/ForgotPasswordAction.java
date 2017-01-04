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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.PasswordException;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class ForgotPasswordAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(ForgotPasswordAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();

            if (!configurationService.getBooleanProperty("allow_forgot_password", true)) {
                throw new PasswordException(PasswordException.FEATURE_DISABLED);
            }

            String login = (String) PropertyUtils.getSimpleProperty(form, "login");
            String lastName = (String) PropertyUtils.getSimpleProperty(form, "lastName");

            if (login != null && lastName != null && !login.equals("") && !lastName.equals("")) {
                User user = null;
                Locale locale = null;
                try {
                    user = userService.getUserByLogin(login);
                    if (user == null) {
                        throw new PasswordException(PasswordException.UNKNOWN_USER);
                    }
                    try {
                        locale = ITrackerResources.getLocale(user.getPreferences().getUserLocale());
                    } catch (RuntimeException e) {
                        locale = ITrackerResources.getLocale();
                    }

                    if (user.getLastName() == null || !user.getLastName().equalsIgnoreCase(lastName)) {
                        throw new PasswordException(PasswordException.INVALID_NAME);
                    }
                    if (user.getEmail() == null || user.getEmail().equals("")) {
                        throw new PasswordException(PasswordException.INVALID_EMAIL);
                    }
                    if (user.getStatus() != UserUtilities.STATUS_ACTIVE) {
                        throw new PasswordException(PasswordException.INACTIVE_ACCOUNT);
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("ForgotPasswordHandler found matching user: " + user.getFirstName() + " " + user.getLastName() + "(" + user.getLogin() + ")");
                    }

                    String subject = ITrackerResources.getString("itracker.email.forgotpass.subject", locale);
                    StringBuffer msgText = new StringBuffer();
                    msgText.append(ITrackerResources.getString("itracker.email.forgotpass.body", locale));
                    String newPass = userService.generateUserPassword(user);
                    userService.updateUser(user);
                    msgText.append(ITrackerResources.getString("itracker.web.attr.password", locale)).append(": ").append(newPass);

                    ServletContextUtils.getItrackerServices().getEmailService()
                            .sendEmail(user.getEmail(), subject, msgText.toString());
                } catch (PasswordException pe) {
                    if (log.isDebugEnabled()) {
                        log.debug("Password Exception for user " + login + ". Type = " + pe.getType());
                    }
                    if (pe.getType() == PasswordException.INVALID_NAME) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.lastname"));
                    } else if (pe.getType() == PasswordException.INVALID_EMAIL) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.invalidemail"));
                    } else if (pe.getType() == PasswordException.INACTIVE_ACCOUNT) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.inactive"));
                    } else if (pe.getType() == PasswordException.UNKNOWN_USER) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.unknown"));
                    }
                }
            }
        } catch (PasswordException pe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.notenabled"));
            log.error("Forgot Password function has been disabled.", pe);
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.forgotpass.system"));
            log.error("Error during password retrieval.", e);
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return (mapping.findForward("forgotpassword"));
        }

        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.message.forgotpass"));
        saveErrors(request, errors);
        return mapping.findForward("success");
    }

}
