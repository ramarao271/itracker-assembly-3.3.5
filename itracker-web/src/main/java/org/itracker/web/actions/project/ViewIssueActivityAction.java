package org.itracker.web.actions.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author ranks
 */
public class ViewIssueActivityAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(ViewIssueActivityAction.class);

    /**
     * executes this struts-actions processing
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("execute: called");
        }

        IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();

        ActionForward ret = checkPermission(request, issueService, mapping);
        if (null != ret) {
            if (log.isDebugEnabled()) {
                log.debug("checkPermission: user has no permission, forwarding to " + ret);
            }
            return ret;
        }

        Map<IssueActivity, String> activities = prepareActivitiesMap(issueService, request);
        if (log.isDebugEnabled()) {
            log.debug("execute: preparing with activities: " + activities);
        }
        setupJspEnv(request, activities);

        if (log.isDebugEnabled()) {
            log.debug("execute: forwarding to " + mapping.findForward("viewissueactivity"));
        }

        return mapping.findForward("viewissueactivity");
    }

    /**
     * check if user can view the issue-activites for the requested issue
     *
     * @return ActionForward: not-null if access is denied, null if user is granted to see issue activities
     */
    private ActionForward checkPermission(HttpServletRequest request, IssueService issueService, ActionMapping mapping) {
        final Map<Integer, Set<PermissionType>> permissions = RequestHelper
                .getUserPermissions(request.getSession());

        User user = RequestHelper.getCurrentUser(request.getSession());
        Integer issueId = getIssueId(request);

        Project project = issueService.getIssueProject(issueId);
        User owner = issueService.getIssueOwner(issueId);
        User creator = issueService.getIssueCreator(issueId);

        if ((project == null ||
                (!UserUtilities.hasPermission(permissions, project.getId(), PermissionType.ISSUE_VIEW_ALL)
                        && !(UserUtilities.hasPermission(permissions, project.getId(), PermissionType.ISSUE_VIEW_USERS)
                        && ((owner != null && owner.getId().equals(user.getId())) || (creator != null && creator.getId().equals(user.getId())))
                )))) {

            return mapping.findForward("unauthorized");
        }

        return null;
    }

    /**
     * read issue id from request
     *
     * @return Integer - issue id or -1 if not in request
     */
    private static Integer getIssueId(HttpServletRequest request) {
        try {
            return Integer.valueOf(request.getParameter("id"));
        } catch (RuntimeException re) {
            if (log.isDebugEnabled()) {
                log.debug("getIssueId: no issue-id in request, caught", re);
            }
        }
        return -1;
    }

    /**
     * Set the objects in request that are required for ui render
     */
    private static final void setupJspEnv(HttpServletRequest request, Map<IssueActivity, String> activities) {

        Integer issueId = getIssueId(request);
        request.setAttribute("activities", activities);
        request.setAttribute("issueId", issueId);
    }

    /**
     * @param issueService
     * @param request
     * @return
     */
    private static final Map<IssueActivity, String> prepareActivitiesMap(IssueService issueService, HttpServletRequest request) {
        SortedMap<IssueActivity, String> activities = new TreeMap<IssueActivity, String>(AbstractEntity.ID_COMPARATOR);

        Integer issueId = getIssueId(request);
        Iterator<IssueActivity> activityIt = issueService.getIssueActivity(issueId).iterator();
        IssueActivity issueActivity;
        while (activityIt.hasNext()) {
            issueActivity = activityIt.next();
            activities.put(issueActivity, IssueUtilities.getActivityName(issueActivity.getActivityType(),
                    LoginUtilities.getCurrentLocale(request)));
        }

        return activities;

    }

}
