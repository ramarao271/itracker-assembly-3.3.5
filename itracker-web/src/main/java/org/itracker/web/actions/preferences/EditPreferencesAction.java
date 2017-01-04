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

package org.itracker.web.actions.preferences;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.UserException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.PasswordException;
import org.itracker.core.AuthenticationConstants;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.UserForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * This class performas an update of the user's profile information based on their input.
 * Only the users core profile information, password, and preferences are updated, no permissions
 * can be updated from here.  Also each type of information is only updated, if it is allowed
 * by the current systems plugable authentication.
 */
public class EditPreferencesAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditPreferencesAction.class);

    public EditPreferencesAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Starting pref mod");
        ActionMessages errors = new ActionMessages();
        //  TODO: Action Cleanup

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing user preferences.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("index");
        }
        resetToken(request);

        User user = null;
        try {
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();

            // TODO: the following checks make no sense from my perspective.
            // This check should happen in the ExecuteAlways filter maybe
            // Shall we remove it?

            HttpSession session = request.getSession();
//            user = (User) session.getAttribute(Constants.USER_KEY);
//            if(user == null) {
//                return mapping.findForward("login");
//            }
//
//            User existingUser = userService.getUser(user.getId());
//            if(existingUser == null || user.getId() != existingUser.getId()) {
//            	if (log.isDebugEnabled()) {
//            		log.debug("execute: Unauthorized edit preferences request from " + user.getLogin() + "(" + user.getId() + ") for " + existingUser.getLogin() + "(" + existingUser.getId() + ")");
//            	}
//                return mapping.findForward("unauthorized");
//            }
            UserForm userForm = (UserForm) form;

            if (LoginUtilities.getCurrentUser(request) != null) {
                user = LoginUtilities.getCurrentUser(request);
            }

            if (log.isInfoEnabled()) {
                log.info("execute: found user " + user);
            }
            errors = form.validate(mapping, request);

//            User existingUser = userService.getUser(user.getId());
            // edit user-object
            if (errors.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("execute: updating user-attributes.");
                }

                if (userService.allowPasswordUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    if (userForm.getPassword() != null && userForm.getPassword().trim().length() > 1) {
                        if (userForm.getCurrPassword() == null || "".equals(userForm.getCurrPassword())) {
                            log.error("execute: current password was not set");
                            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingpassword"));
                        } else {
                            try {
                                User passwordCheck = userService.checkLogin(user.getLogin(), userForm.getCurrPassword(), AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN, AuthenticationConstants.REQ_SOURCE_WEB);
                                if (passwordCheck == null) {
                                    throw new AuthenticatorException(AuthenticatorException.INVALID_DATA);
                                }
                                if (log.isDebugEnabled()) {
                                    log.debug("execute: setting new user password");
                                }
                                user.setPassword(UserUtilities.encryptPassword(userForm.getPassword()));
                            } catch (AuthenticatorException ae) {
                                log.error("execute: current password was wrong, AuthenticatorException", ae);
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.wrongpassword"));
                            } catch (PasswordException e) {
                                log.error("execute: current password was wrong", e);
                                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.wrongpassword"));
                            }
                        }
                    }
                } else {
//                  itracker.web.error.noprofileupdates
                    log.info("execute: passwords can not be changed in preferences due to incapable authenticator");
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.nopasswordupdates"));
                    saveErrors(request, errors);
                    return mapping.findForward("error");
                }

                // TODO: should this check happen earlier?
                if (userService.allowProfileUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    if (log.isInfoEnabled()) {
                        log.info("execute: allowing profile updates for " + user);
                    }
                    user.setFirstName(userForm.getFirstName());
                    user.setLastName(userForm.getLastName());
                    user.setEmail(userForm.getEmail());
                } else {
                    log.error("execute: profile updates are not allowed for " + user);
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprofileupdates"));
                    saveErrors(request, errors);
                    return mapping.findForward("error");
                }
            } else {
                // validation errors
                if (log.isInfoEnabled()) {
                    log.info("execute: got actions errors from validation: " + errors);
                }
            }

            if (errors.isEmpty()) {
                log.debug("Passed required checks.  Updating user info for " + user.getLogin());
                user = userService.updateUser(user);

                UserPreferences userPrefs = user.getPreferences();
                if (userPrefs == null) userPrefs = new UserPreferences();

                if (userService.allowPreferenceUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB)) {
                    //userPrefs.setUser(existingUser);
                    userPrefs.setUser(user);

                    userPrefs.setUserLocale(userForm.getUserLocale());

                    try {
                        userPrefs.setNumItemsOnIndex(Integer.valueOf(userForm.getNumItemsOnIndex()));
                    } catch (NumberFormatException nfe) {
                        userPrefs.setNumItemsOnIndex(-1);
                    }
                    try {
                        userPrefs.setNumItemsOnIssueList(Integer.valueOf(userForm.getNumItemsOnIssueList()));
                    } catch (NumberFormatException nfe) {
                        userPrefs.setNumItemsOnIssueList(-1);
                    }
                    userPrefs.setShowClosedOnIssueList(Boolean.valueOf(userForm.getShowClosedOnIssueList()));
                    userPrefs.setSortColumnOnIssueList(userForm.getSortColumnOnIssueList());

                    int hiddenSections = 0;
                    Integer[] hiddenSectionsArray = userForm.getHiddenIndexSections();
                    if (hiddenSectionsArray != null) {
                        for (int i = 0; i < hiddenSectionsArray.length; i++) {
                            hiddenSections += hiddenSectionsArray[i].intValue();
                        }
                    }
                    userPrefs.setHiddenIndexSections(hiddenSections);

                    userPrefs.setRememberLastSearch(Boolean.valueOf(userForm.getRememberLastSearch()));
                    userPrefs.setUseTextActions(Boolean.valueOf(userForm.getUseTextActions()));

                    userPrefs = userService.updateUserPreferences(userPrefs);
                }

                //session.setAttribute(Constants.USER_KEY, existingUser);
                session.setAttribute(Constants.USER_KEY, user);
                session.setAttribute(Constants.PREFERENCES_KEY, userPrefs);
                session.setAttribute(Constants.LOCALE_KEY, ITrackerResources.getLocale(userPrefs.getUserLocale()));

                request.setAttribute(Constants.LOCALE_KEY, ITrackerResources.getLocale(userPrefs.getUserLocale()));

                session.removeAttribute(Constants.EDIT_USER_KEY);
                session.removeAttribute(Constants.EDIT_USER_PREFS_KEY);
            } else {
                // validation errors
                if (log.isInfoEnabled()) {
                    log.info("execute: got actions errors from user manipulation: " + errors);
                }

            }
        } catch (RuntimeException e) {
            log.error("execute", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
        } catch (UserException e) {
            log.error("execute", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
        }

        if (!errors.isEmpty()) {

            if (log.isInfoEnabled()) {
                log.info("execute: got actions errors: " + errors);
            }

            saveErrors(request, errors);
            saveToken(request);
        }

        if (log.isDebugEnabled()) {
            log.debug("execute: done, forward to input forward");
        }
        return mapping.getInputForward();
    }
}
  