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
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class UnlockUserAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(UnlockUserAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            log.info("execute: forwarding to unauthorized for " + LoginUtilities.getCurrentUser(request));
            return mapping.findForward("unauthorized");
        }

        try {
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();

            Integer userId = Integer.valueOf((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
            User user = userService.getUser(userId);
            if (log.isInfoEnabled()) {
                log.info("Unlocking user " + user + " (id: " + userId + ")");
            }
            user.setStatus(UserUtilities.STATUS_ACTIVE);
            userService.updateUser(user);
        } catch (Exception e) {
            log.error("execute: failed with unexpected exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("listusers");
    }

}
  