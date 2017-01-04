package org.itracker.web.util;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.web.ptos.IssuePTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public class ListIssuesActionUtil {
	private static final Logger log = Logger.getLogger(ListIssuesActionUtil.class);

	// TODO check for other occurences for this constants and maybe place somewhere else?
	public static final String LIST_ISSUES_PAGE_TITLE_KEY = "itracker.web.listissues.title";
	// request attribute names
	public static final String ATT_NAME_PAGE_TITLE_KEY = "pageTitleKey";
	public static final String ATT_NAME_PAGE_TITLE_ARG = "pageTitleArg";
	public static final String ATT_NAME_HAS_ORDER_PARAM = "hasOrderParam";

	public static final String ATT_NAME_ORDER_PARAM = "orderParam";

	public static final String ATT_NAME_ISSUE_PTOS = "issuePTOs";
	public static final String ATT_NAME_PROJECT = "project";
	public static final String ATT_NAME_PROJCET_ID = "projectId";
	public static final String ATT_NAME_HAS_ISSUES = "hasIssues";
	public static final String ATT_NAME_HAS_VIEW_ALL = "hasViewAll";

	public static final String ATT_NAME_UNASSIGNED = "itracker_web_generic_unassigned";
	
	public static final String SES_ATT_NAME_CURRENT__USER = "currUser";
	public static final String SES_ATT_NAME_PREFERENCES = "preferences";
	public static final String PARAM_NAME_PROJECT_ID = "projectId";

	
	public static final String PARAM_NAME_START = "start";
	public static final String PARAM_NAME_ORDER = "order";
	
	public static final String ORDER_KEY_ID = "id";
	public static final String ORDER_KEY_SEVERITY = "sev";
	public static final String ORDER_KEY_STATUS = "stat";
	public static final String ORDER_KEY_LAST_MODIFIED = "lm";
	public static final String ORDER_KEY_OWNER_AND_STATUS = "own";
	
	
	public static final String RES_KEY_UNASSIGNED = "itracker.web.generic.unassigned";
	public static final String RES_KEY_UNKNOWN = "itracker.web.generic.unknown";
	
	public static final String FWD_LIST_ISSUES = "list_issues";



	public static ActionForward init(Action action, ActionMessages messages, ActionMapping mapping, HttpServletRequest request) {
		
    Locale locale = LoginUtilities.getCurrentLocale(request);
    // get the services
    IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
    ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();
    User um = (User)request.getSession().getAttribute(ListIssuesActionUtil.SES_ATT_NAME_CURRENT__USER);
    Integer currUserId = um.getId();
    HttpSession session = request.getSession(true);
    Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);
    // get the request parameters
    UserPreferences userPrefs = (UserPreferences) request.getSession().getAttribute(ListIssuesActionUtil.SES_ATT_NAME_PREFERENCES);

    Integer projectId = NumberUtils.createInteger(request.getParameter(ListIssuesActionUtil.PARAM_NAME_PROJECT_ID));
    log.debug("execute: " + ListIssuesActionUtil.PARAM_NAME_PROJECT_ID + " was: " + projectId);

    if (null == projectId) {
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "itracker.web.error.invalidproject"));
        return mapping.findForward("error");

    }
    // get some values
    int status = (userPrefs.getShowClosedOnIssueList() ? IssueUtilities.STATUS_END : IssueUtilities.STATUS_CLOSED);
    
    // do some service calls
    Project project = projectService.getProject(projectId);
    if (null == project) {
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                "itracker.web.error.invalidproject"));
        return mapping.findForward("error");
    }
    log.debug("execute: projectModel_Name: " + project.getName());
    List<Issue> listIssues = issueService.getIssuesByProjectId(projectId, status);
    log.debug("execute: issues found for this project: " + listIssues.size());
    // prepare PTOs
    List<IssuePTO> issuePTOs = new ArrayList<IssuePTO>();
    
    // do some more order processing
    boolean hasOrderParam = false;
    String order = "";
    String orderParam = request.getParameter(ListIssuesActionUtil.PARAM_NAME_ORDER);
    if(orderParam == null || "".equals(orderParam)) {
        order = userPrefs.getSortColumnOnIssueList();
        orderParam = "";
    } else {
        hasOrderParam = true;
        order = orderParam;
    }
    
    if(ListIssuesActionUtil.ORDER_KEY_ID.equals(order)) {
        Collections.sort(listIssues, Issue.ID_COMPARATOR);
    } else if(ListIssuesActionUtil.ORDER_KEY_SEVERITY.equals(order)) {
        Collections.sort(listIssues, Issue.SEVERITY_COMPARATOR);
    } else if(ListIssuesActionUtil.ORDER_KEY_STATUS.equals(order)) {
        Collections.sort(listIssues, Issue.STATUS_COMPARATOR);
    } else if(ListIssuesActionUtil.ORDER_KEY_LAST_MODIFIED.equals(order)) {
        Collections.sort(listIssues, Collections.reverseOrder(
                Issue.LAST_MODIFIED_DATE_COMPARATOR));
    } else if(ListIssuesActionUtil.ORDER_KEY_OWNER_AND_STATUS.equals(order)) {
        Collections.sort(listIssues, Issue.OWNER_AND_STATUS_COMPARATOR);
    } else {
        Collections.sort(listIssues, Issue.STATUS_COMPARATOR);
    }
    
    int start = 0;
    String startString = request.getParameter(ListIssuesActionUtil.PARAM_NAME_START);
    if(startString != null) {
        try {
            start = Integer.parseInt(startString);
        } catch(NumberFormatException nfe) {
        }
    }
    int numViewable = 0;
    boolean hasIssues = false;
    boolean hasViewAll = UserUtilities.hasPermission(userPermissions, project.getId(), PermissionType.ISSUE_VIEW_ALL);

    if (!hasViewAll) {
        if (null == userPermissions.get(projectId)) {
            return mapping.findForward("unauthorized");
        }
    }
    
    if(hasViewAll) {
        numViewable = listIssues.size();
    } else {
        for (Issue listIssue : listIssues) {
            if (IssueUtilities.canViewIssue(listIssue, currUserId, userPermissions)) {
                numViewable++;
            }
        }
    }
    int row = 0;
    int k = 0;
    Iterator<Issue> issuesIt = listIssues.iterator();
    // start copying from Models to PTOs
    Issue issue;
    IssuePTO issuePTO;
    String statusLocalizedString, severityLocalizedString, componentsSize;
    while (issuesIt.hasNext()) {
    	
        issue = issuesIt.next();
        issuePTO = new IssuePTO(issue);
        
        statusLocalizedString=IssueUtilities.getStatusName(issue.getStatus(), locale);
        severityLocalizedString = IssueUtilities.getSeverityName(issue.getSeverity(), locale) ;
        if (issue.getComponents().size() == 0) {
		    componentsSize = ITrackerResources.getString(
				ListIssuesActionUtil.RES_KEY_UNKNOWN, locale);
        } else {
            componentsSize = issue.getComponents().get(0).getName()
                    + (issue.getComponents().size() > 1 ? " (+)" : "");
        }
        issuePTO.setStatusLocalizedString(statusLocalizedString);
        issuePTO.setSeverityLocalizedString(severityLocalizedString);
        issuePTO.setComponentsSize(componentsSize);
        if(issue.getOwner()==null) {
        	issuePTO.setUnassigned(true);
        }
        
        if(project.getStatus() == Status.ACTIVE && ! IssueUtilities.hasIssueNotification(issue, project, currUserId)) {
            issuePTO.setUserHasIssueNotification(true);
        }
        if(project.getStatus() == Status.ACTIVE) {
            if(IssueUtilities.canEditIssue(issue, currUserId, userPermissions)) {
                issuePTO.setUserCanEdit(true);
            }
        }

        if(! hasViewAll && ! IssueUtilities.canViewIssue(issue, currUserId, userPermissions)) {
            continue;
        }
        hasIssues = true;
        if(start > 0 && k < start) {
            k++;
            continue;
        }
        if(userPrefs.getNumItemsOnIssueList() > 0 && row >= userPrefs.getNumItemsOnIssueList()) {
            break;
        }
        row++;
        
        issuePTOs.add(issuePTO);
    }

    // populate the request$
    request.setAttribute("pagination", new Pagination(numViewable, start, userPrefs.getNumItemsOnIssueList()));
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_HAS_ORDER_PARAM, hasOrderParam);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_ORDER_PARAM, orderParam);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_ISSUE_PTOS, issuePTOs);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_PROJECT, project);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_PROJCET_ID, projectId);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_HAS_ISSUES, hasIssues);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_HAS_VIEW_ALL, hasViewAll);


    request.setAttribute("rssFeed", "/servlets/issues/p" + project.getId());

    request.setAttribute(ListIssuesActionUtil.ATT_NAME_UNASSIGNED, ITrackerResources.getString(ListIssuesActionUtil.RES_KEY_UNASSIGNED, locale));
    String pageTitleArg = project.getName();
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_PAGE_TITLE_KEY, ListIssuesActionUtil.LIST_ISSUES_PAGE_TITLE_KEY);
    request.setAttribute(ListIssuesActionUtil.ATT_NAME_PAGE_TITLE_ARG, pageTitleArg);
    final Map<Integer, Set<PermissionType>> permissions = RequestHelper.getUserPermissions(session);
    boolean canCreateIssue = false;
    if(project.getStatus() == Status.ACTIVE && UserUtilities.hasPermission(permissions, project.getId(), PermissionType.ISSUE_EDIT_ALL)) {
    	canCreateIssue = true;
    }
    request.setAttribute("canCreateIssue", canCreateIssue);
		
		return null;
	}
   /**
    * TODO temporary pagination helper
    */
   public static class Pagination {
      final private int total, start, perPage, pageCount, currentPage;
      private Pagination(final int total, final int start, final int perPage) {
         this.total = total;
         this.start = start;
         this.perPage = perPage;
         pageCount = perPage>0?Double.valueOf(Math.ceil((double)total/perPage)).intValue():1;
         currentPage = start>0?Double.valueOf(Math.ceil((double)start/perPage)).intValue()+1:1;
      }

      public int getTotal() {
         return total;
      }

      public int getStart() {
         return start;
      }

      public int getPerPage() {
         return perPage;
      }

      public int getPageCount() {
         return pageCount;
      }

      public int getCurrentPage() {
         return currentPage;
      }
   }

}
