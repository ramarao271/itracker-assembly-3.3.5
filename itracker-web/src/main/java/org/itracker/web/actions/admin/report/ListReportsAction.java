package org.itracker.web.actions.admin.report;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Report;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class ListReportsAction extends ItrackerBaseAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();
        ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();
        request.setAttribute("ph", projectService);
        request.setAttribute("rh", reportService);

        List<Report> reports = reportService.getAllReports();
        request.setAttribute("reports", reports);

        return mapping.findForward("listreportsadmin");
    }

    public ListReportsAction() {
        super();
    }

}
