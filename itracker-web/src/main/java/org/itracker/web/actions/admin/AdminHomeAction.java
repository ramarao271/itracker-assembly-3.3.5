package org.itracker.web.actions.admin;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.util.ReportUtilities;
import org.itracker.services.*;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ServletContextUtils;
import org.itracker.web.util.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class AdminHomeAction extends ItrackerBaseAction {

    private static final Logger log = Logger.getLogger(AdminHomeAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        execSetupJspEnv(request);

        return mapping.findForward("adminhome");
    }


    /**
     * This utility has to be called for any page forwarding to the admin-home, before forwarding. Else the page will contain no data.
     */
    public static void execSetupJspEnv(HttpServletRequest request) {
        Date time_millies = new Date(System.currentTimeMillis());

        IssueService issueService = ServletContextUtils.getItrackerServices()
                .getIssueService();
        ReportService reportService = ServletContextUtils.getItrackerServices()
                .getReportService();
        ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
                .getConfigurationService();
        UserService userService = ServletContextUtils.getItrackerServices().getUserService();


        ProjectService projectService2 = ServletContextUtils
                .getItrackerServices().getProjectService();

        String exportReport = "type=all&reportOutput=XML&reportId="
                + ReportUtilities.REPORT_EXPORT_XML;

        logTimeMillies("execute: looked up services", time_millies, log,
                Level.INFO);
        Integer numberOfWorkflowScripts = configurationService
                .getWorkflowScripts().size();
        request
                .setAttribute("numberOfWorkflowScripts",
                        numberOfWorkflowScripts);
        logTimeMillies("execute: looked up numberOfWorkflowScripts",
                time_millies, log, Level.INFO);

        ResourceBundle bundle = ITrackerResources.getBundle(ITrackerResources.BASE_LOCALE);
        Enumeration<String> keysEnum = bundle.getKeys();
        int i = 0;
        while (keysEnum.hasMoreElements()) {
            keysEnum.nextElement();
            i++;
        }

        request.setAttribute("numberDefinedKeys", i);
        logTimeMillies("execute: looked up numberDefinedKeys", time_millies,
                log, Level.INFO);

        Integer numberOfStatuses = configurationService
                .getConfigurationItemsByType(
                        Configuration.Type.status).size();
        request.setAttribute("numberOfStatuses", numberOfStatuses);
        logTimeMillies("execute: looked up numberOfStatuses", time_millies,
                log, Level.INFO);

        Integer numberOfSeverities = configurationService
                .getConfigurationItemsByType(
                        Configuration.Type.severity).size();
        request.setAttribute("numberOfSeverities", numberOfSeverities);
        logTimeMillies("execute: looked up numberOfSeverities", time_millies,
                log, Level.INFO);

        Integer numberOfResolutions = configurationService
                .getConfigurationItemsByType(
                        Configuration.Type.resolution).size();
        request.setAttribute("numberOfResolutions", numberOfResolutions);
        logTimeMillies("execute: looked up numberOfResolutions", time_millies,
                log, Level.INFO);

        Integer numberOfCustomProjectFields = configurationService
                .getCustomFields().size();
        request.setAttribute("numberOfCustomProjectFields",
                numberOfCustomProjectFields);
        logTimeMillies("execute: looked up numberOfCustomProjectFields",
                time_millies, log, Level.INFO);

        Integer numberofActiveSesssions = SessionManager.getNumActiveSessions();

        Integer numberUsers = userService.getAllUsers().size();

        request
                .setAttribute("numberofActiveSesssions",
                        numberofActiveSesssions);
        request
                .setAttribute("numberUsers",
                        numberUsers);

        logTimeMillies("execute: looked up numberofActiveSesssions",
                time_millies, log, Level.INFO);

        Long allIssueAttachmentsTotalNumber = issueService
                .getAllIssueAttachmentCount();
        request.setAttribute("allIssueAttachmentsTotalNumber",
                allIssueAttachmentsTotalNumber);
        logTimeMillies("execute: looked up allIssueAttachmentsTotalNumber",
                time_millies, log, Level.INFO);

        Integer numberReports = 0;
        try {
            numberReports = reportService.getNumberReports();
        } catch (Exception e) {
            log.warn("execSetupJspEnv", e);
        }
        request.setAttribute("numberReports",
                numberReports);
        Long numberIssues = issueService.getNumberIssues();

        // TODO: performance issue when attachments size needs to be calculated
        // over many issues !
        // select sum(size)
        // from IssueAttachment
        if (allIssueAttachmentsTotalNumber < 500) {
            Long allIssueAttachmentsTotalSize = issueService
                    .getAllIssueAttachmentSize();
            request.setAttribute("allIssueAttachmentsTotalSize",
                    allIssueAttachmentsTotalSize);
        } else {
            request.setAttribute("allIssueAttachmentsTotalSize", -1l);
        }
        logTimeMillies("execute: looked up allIssueAttachmentsTotalSize",
                time_millies, log, Level.INFO);

        /* set objects needed to render output in request object */
        request.setAttribute("projectService", projectService2);
        request.setAttribute("exportReport", exportReport);
        request.setAttribute("sizeps", projectService2.getAllProjects().size());
        request.setAttribute("numberIssues", numberIssues);


        request.setAttribute("numberAvailableLanguages", configurationService.getNumberAvailableLanguages());
        logTimeMillies("execute: put services to request", time_millies, log,
                Level.INFO);

        String pageTitleKey = "itracker.web.admin.index.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        logTimeMillies("execute: returning", time_millies, log, Level.INFO);
    }
}
