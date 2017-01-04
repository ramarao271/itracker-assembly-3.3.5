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

package org.itracker.web.actions.project;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class RemoveHistoryEntryAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(RemoveHistoryEntryAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();

            Integer historyId = (Integer) PropertyUtils.getSimpleProperty(form, "historyId");
            String caller = (String) PropertyUtils.getSimpleProperty(form, "caller");
            if (caller == null) {
                caller = "";
            }

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            if (!currUser.isSuperUser()) {
                return mapping.findForward("unauthorized");
            } else {
                Integer issueId = issueService.removeIssueHistoryEntry(historyId, currUser.getId());
                return new ActionForward(mapping.findForward("editissue").getPath() + "?id=" + issueId + "&caller=" + caller);
            }
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
  