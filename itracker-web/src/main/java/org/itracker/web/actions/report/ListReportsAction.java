package org.itracker.web.actions.report;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ListReportsAction extends ItrackerBaseAction {


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
        ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();
        ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();
        request.setAttribute("ih", issueService);
        request.setAttribute("ph", projectService);
        request.setAttribute("rh", reportService);

        String pageTitleKey = "itracker.web.listreports.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        return mapping.findForward("list_reports");
    }

    public ListReportsAction() {
        super();
    }

}
