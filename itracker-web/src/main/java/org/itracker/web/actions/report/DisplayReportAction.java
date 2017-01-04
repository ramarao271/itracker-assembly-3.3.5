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

package org.itracker.web.actions.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.ReportException;
import org.itracker.core.resources.ITrackerResourceBundle;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.ReportUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DisplayReportAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(DisplayReportAction.class);

    public DisplayReportAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        try {
            HttpSession session = request.getSession(false);
            Locale userLocale = LoginUtilities.getCurrentLocale(request);

            List<Issue> reportingIssues = new ArrayList<Issue>();
            String reportType = (String) PropertyUtils.getSimpleProperty(form, "type");
            log.info("execute: report type was " + reportType);

            final Integer[] projectIds = (Integer[]) PropertyUtils.getSimpleProperty(form, "projectIds");
            final IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            final ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();
            final ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();

            if ("all".equalsIgnoreCase(reportType)) {
                // Export all of the issues in the system
                User currUser = (User) session.getAttribute(Constants.USER_KEY);
                if (!currUser.isSuperUser()) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.unauthorized"));
                    throw new ReportException();
                }
                reportingIssues = issueService.getAllIssues();
                Collections.sort(reportingIssues, Issue.ID_COMPARATOR);
            } else if ("project".equalsIgnoreCase(reportType)) {
                if (projectIds != null && projectIds.length > 0) {
                    // This wasn't a regular search.  So instead, take all the selected projects and find all the
                    // issues for them, check which ones the user can see, and then create a new array of issues
                    List<Issue> reportDataList = new ArrayList<>();

                    List<Issue> issues;
                   for (Integer projectId : projectIds) {
                      issues = issueService.getIssuesByProjectId(projectId);
                      for (Issue issue: issues) {
                         if (LoginUtilities.canViewIssue(issue))
                             reportDataList.add(issue);

                      }
                   }
                    reportingIssues = reportDataList;
                    Collections.sort(reportingIssues, Issue.ID_COMPARATOR);

                } else {
                    throw new ReportException("", "itracker.web.error.projectrequired");
                }
            } else {
                // This must be a regular search, look for a search query result.
                // must be loaded with current session (lazy loading)
                IssueSearchQuery isqm = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);
                for (Issue issue : isqm.getResults()) {
                    reportingIssues.add(issueService.getIssue(issue.getId()));
                }
            }

            log.debug("Report data contains " + reportingIssues.size() + " elements.");

            if (reportingIssues.isEmpty()) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noreportdata"));
                throw new ReportException();
            }

            Integer reportId = (Integer) PropertyUtils.getSimpleProperty(form, "reportId");
            String reportOutput = (String) PropertyUtils.getSimpleProperty(form, "reportOutput");
            if (null == reportId) {
                log.debug("Invalid report id: " + reportId + " requested.");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
            } else if (ReportUtilities.REPORT_EXPORT_XML == reportId.intValue()) {
                log.debug("Issue export requested.");

                SystemConfiguration config = configurationService.getSystemConfiguration(ImportExportTags.EXPORT_LOCALE);

                if (!ImportExportUtilities.exportIssues(reportingIssues, config, request, response)) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
                } else {
                   return null;
                }
            } else if (reportId.intValue() > 0) {
                log.debug("Defined report (" + reportId + ") requested.");

                Report reportModel = reportService.getReportDAO().findByPrimaryKey(reportId);

                log.debug("Report " + reportModel + " found.");

                Project project = null;
                log.debug("Processing report.");
                outputReport(reportingIssues, project, reportModel, userLocale, reportOutput, response);
                return null;

            }
        } catch (ReportException re) {
            log.debug("Error for report", re);
            if (!StringUtils.isEmpty(re.getErrorKey())) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(re.getErrorKey()));
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.details", re.getMessage()));
            }

        } catch (Exception e) {
            log.warn("Error in report processing: " + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.details", e.getMessage()));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }





    private static JasperPrint generateReport(Report report,
                                       Map<String, Object> parameters, JRDataSource datasource)
    throws ReportException{
        try {

            JasperReport jasperReport = JasperCompileManager.compileReport(new ByteArrayInputStream(report.getFileData()));
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    datasource);


            return jasperPrint;
        } catch (JRException e) {
            throw new ReportException(e);
        }

    }

    /**
     *
     */
    public static void outputReport(List<Issue> reportDataArray, Project project, Report report,
                          Locale userLocale, String reportOutput,
                          HttpServletResponse response) throws ReportException {

        try {
            // hack, we have to find a more structured way to support
            // various types of queries
            final JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(
                    reportDataArray);

            final Map<String, Object> parameters = new HashMap<String, Object>();
            String reportTitle = report.getName();
            if (project != null) {
                reportTitle += " - " + project.getName();
                if (report.getNameKey() != null) {
                    reportTitle = ITrackerResources.getString(report.getNameKey(), project.getName());
                }
            } else if (report.getNameKey() != null) {
                reportTitle = ITrackerResources.getString(report.getNameKey());
            }
            parameters.put("ReportTitle", reportTitle);
            parameters.put("BaseDir", new File("."));
            parameters.put("REPORT_LOCALE", userLocale);
            parameters.put("REPORT_RESOURCE_BUNDLE", ITrackerResourceBundle.getBundle(userLocale));

            final JasperPrint jasperPrint = generateReport(report, parameters,
                    beanCollectionDataSource);


            final ServletOutputStream out = response.getOutputStream();
            final JRExporter x;
            if (ReportUtilities.REPORT_OUTPUT_PDF.equals(reportOutput)) {

                response.setHeader("Content-Type", "application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getName()
                        + new SimpleDateFormat("-yyyy-MM-dd").format(new Date())
                        + ".pdf\"");
                x = new JRPdfExporter();

            } else if (ReportUtilities.REPORT_OUTPUT_XLS.equals(reportOutput)) {
                response.setHeader("Content-Type", "application/xls");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getName()
                           + new SimpleDateFormat("-yyyy-MM-dd").format(new Date())
                           + ".xls\"");
                x = new JRXlsExporter();

            } else if (ReportUtilities.REPORT_OUTPUT_CSV.equals(reportOutput)) {

                response.setHeader("Content-Type", "text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getName()
                        + new SimpleDateFormat("-yyyy-MM-dd").format(new Date())
                        + ".csv\"");
                x = new JRCsvExporter();

            } else if (ReportUtilities.REPORT_OUTPUT_HTML.equals(reportOutput)) {
                response.setHeader("Content-Type", "text/html");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getName()
                        + new SimpleDateFormat("-yyyy-MM-dd").format(new Date())
                        + ".html\"");
                x = new JRHtmlExporter();

            } else {
                log.error("Invalid report output format: " + reportOutput);
                throw new ReportException("Invalid report type.", "itracker.web.error.invalidreportoutput");
            }
            x.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            x.setParameter(JRExporterParameter.OUTPUT_STREAM, out);

            x.exportReport();

            out.flush();
            out.close();
        } catch (JRException e) {
            throw new ReportException(e);
        } catch (IOException e) {
            throw new ReportException(e);
        }

    }

}
  