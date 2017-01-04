package org.itracker.web.actions.admin.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.PermissionType;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.actions.project.ListProjectsAction;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;


/**
 * Action for preparing request for list_projects.jsp.
 *
 * @author ranks
 */
public class ListProjectsAdminAction extends ListProjectsAction {

    private static final Logger log = Logger.getLogger(ListProjectsAdminAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        final Map<Integer, Set<PermissionType>> permissions =
                RequestHelper.getUserPermissions(request.getSession());
        Boolean isSuperUser = UserUtilities.isSuperUser(permissions);
        Boolean showAll = Boolean.valueOf(request.getParameter("showAll"));

        // filter projects, so only administrated projects remain
        if (showAll) {
            request.setAttribute("projects", getAllPTOs(ServletContextUtils.getItrackerServices().getProjectService(), new PermissionType[]{PermissionType.PRODUCT_ADMIN}, permissions));
        } else {
            request.setAttribute("projects", getPTOs(ServletContextUtils.getItrackerServices().getProjectService(), new PermissionType[]{PermissionType.PRODUCT_ADMIN}, permissions));
        }
        if (log.isDebugEnabled()) {
            log.debug("execute: project-ptos set to request-attribute: " + request.getAttribute("projects"));
        }
        request.setAttribute("isSuperUser", isSuperUser);
        request.setAttribute("showAll", showAll);

        String pageTitleKey = "itracker.web.admin.listprojects.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        return mapping.findForward("listprojectsadmin");
    }


}
