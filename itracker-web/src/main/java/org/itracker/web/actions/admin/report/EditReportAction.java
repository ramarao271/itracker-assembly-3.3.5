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
import org.apache.struts.upload.FormFile;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Language;
import org.itracker.model.PermissionType;
import org.itracker.model.Report;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ReportForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


public class EditReportAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditReportAction.class);

    public EditReportAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final ActionMessages errors = new ActionMessages();

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing report.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listreports");
        }
        resetToken(request);

        final ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();
        final ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();
        final ReportForm reportForm = (ReportForm) form;
        if (reportForm == null)
            return mapping.findForward("listreportsadmin");

        HttpSession session = request.getSession(true);
        Report editreport;
        try {

            if (null != reportForm.getId() && reportForm.getId() != -1) {
                // TODO cleanup the service.
                editreport = reportService.getReportDAO().findByPrimaryKey(reportForm.getId());
            } else {
                editreport = new Report();
            }
            editreport.setName(reportForm.getName());
            editreport.setNameKey(reportForm.getNameKey());
            editreport.setDescription(reportForm.getDescription());
            FormFile fileData = reportForm.getFileDataFile();
            try {
                if (fileData.getFileData() == null ||
                        fileData.getFileData().length == 0) {
                    // should be validated already-
                    if (null == reportForm.getId()) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingdatafile"));
                    }
                } else {
                    EditReportFormAction.getAsString(fileData.getFileData(), errors);
                    editreport.setFileData(fileData.getFileData());
                }

            } catch (Exception e) {
                log.error("Exception while verifying import data.", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.missingdatafile"));
            }


            String action = request.getParameter("action");
            if (errors.isEmpty()) {
                reportService.getReportDAO().saveOrUpdate(editreport);
            }

            if ("create".equals(action) && editreport != null) {
                // If it was a create, add a new language key in the base for it.
                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, editreport.getNameKey(), editreport.getName()));
                ITrackerResources.clearKeyFromBundles(editreport.getNameKey(), true);
            }
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.save"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        session.removeAttribute(Constants.REPORT_KEY);
        return mapping.findForward("listreportsadmin");
    }

}
  