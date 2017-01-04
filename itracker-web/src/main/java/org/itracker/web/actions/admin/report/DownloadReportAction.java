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
import org.itracker.model.Report;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class DownloadReportAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(DownloadReportAction.class);

    public DownloadReportAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        String pageTitleKey;
        String pageTitleArg = "";

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            Integer reportId = new Integer((request.getParameter("id") == null ? "-1" : request.getParameter("id")));
            if (reportId == null || reportId.intValue() < 0) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
            } else {
                ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();

                Report report = reportService.getReportDAO().findByPrimaryKey(reportId);
                if (report != null) {
                    response.setContentType("application/x-itracker-report-export");
                    response.setHeader("Content-Disposition", "attachment; filename=\"ITracker_report_" + report.getId() + ".def\"");
                    ServletOutputStream out = response.getOutputStream();
                    log.debug("Attempting export for: " + report);
                    out.write(report.getFileData());
                    out.flush();
                    out.close();
                    return null;
                }
                log.debug("Unknown report " + reportId + " specified for export");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
            }
        } catch (Exception e) {
            pageTitleKey = "itracker.web.error.title";
            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);
            log.error("Exception while exporting a report.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }

}
  