package org.itracker.web.actions.admin.workflow;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ListWorkflowScriptAction extends ItrackerBaseAction {


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ConfigurationService configurationService = this.getITrackerServices().getConfigurationService();
        request.setAttribute("sc", configurationService);

        String pageTitleKey = "itracker.web.admin.listworkflow.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        return mapping.findForward("listworkflowscripts");

    }
}
