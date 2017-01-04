package org.itracker.web.actions;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ListProjectsAction extends ItrackerBaseAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();
        request.setAttribute("ph", projectService);
        return new ActionForward("listprojects");
    }

    public ListProjectsAction() {
        super();

    }


}
