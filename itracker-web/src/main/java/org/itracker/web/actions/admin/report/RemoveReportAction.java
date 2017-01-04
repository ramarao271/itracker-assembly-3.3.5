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

package org.itracker.web.actions.admin.report;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.PermissionType;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class RemoveReportAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(RemoveReportAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        try {
            HttpSession session = request.getSession(true);
            Map<Integer, Set<PermissionType>> userPermissionsMap = RequestHelper.getUserPermissions(session);
            if (!UserUtilities.hasPermission(userPermissionsMap, UserUtilities.PERMISSION_USER_ADMIN)) {
                return mapping.findForward("unauthorized");
            }

            ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();

            Integer reportId = new Integer((request.getParameter("id") == null ? "-1" : request.getParameter("id")));
            reportService.getReportDAO().delete(reportService.getReportDAO().findByPrimaryKey(reportId));
            return mapping.findForward("listreportsadmin");
        } catch (NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
            log.debug("Invalid report id " + request.getParameter("id") + " specified.");
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
