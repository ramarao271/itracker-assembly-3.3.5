package org.itracker.web.actions;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.ptos.IssuePTO;
import org.itracker.web.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

//  TODO: Action Cleanup

public class PortalHomeAction extends ItrackerBaseAction {

    static final Logger log = LoggerFactory.getLogger(PortalHomeAction.class);
    
    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        log.debug("Stepping up into the loginRouter method");

		// maybe wrong the next line... setting a default forward...
		ActionForward forward = mapping.findForward("portalhome");

		if (forward == null) {
			return null;
		} else {
            
            log.debug("Found forward, let's go and check if this forward is portalhome...");

            if (forward.getName().equals("portalhome")
					|| forward.getName().equals("index")) {
                
                final IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
                final ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();
                final UserService userService = ServletContextUtils.getItrackerServices().getUserService();
                final User currUser = (User)request.getSession().getAttribute("currUser");
                final Locale locale = super.getLocale(request);

                final Map<Integer, Set<PermissionType>> permissions =
                        (Map<Integer, Set<PermissionType>>)request.getSession().getAttribute("permissions");

                // GETTING AND SETTING USER PREFS AND HIDDEN SECTIONS ACCORDINGLY
                UserPreferences userPrefs = currUser.getPreferences();
                if(userPrefs == null) userPrefs = new UserPreferences();
                
                int hiddenSections = 0;
                Boolean allSections = null == request.getSession().getAttribute("allSections") ? false: Boolean.valueOf(request.getSession().getAttribute("allSections").toString());
                if (null != request.getParameter("allSections"))
                {
                	allSections = Boolean.valueOf(request.getParameter("allSections"));
                }
                
                if(!allSections) {
                    hiddenSections = userPrefs.getHiddenIndexSections();
                }
                
                final List<IssuePTO> createdIssuePTOs;
                final List<IssuePTO> ownedIssuePTOs;
                final List<IssuePTO> unassignedIssuePTOs;
                final List<IssuePTO> watchedIssuePTOs;
                
                // POPULATING ISSUE MODELS
                final List<Issue> createdIssues;
                final List<Issue> ownedIssues;
                final List<Issue> unassignedIssues;
                final List<Issue> watchedIssues;
                
                // PUTTING PREFERENCES INTO THE REQUEST SCOPE
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED, hiddenSections)) {
                    createdIssues  = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_CREATED", Boolean.TRUE);
                } else {
                    createdIssues = issueService.getIssuesCreatedByUser(currUser.getId());
                    request.setAttribute("UserUtilities_PREF_HIDE_CREATED", Boolean.FALSE);
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED, hiddenSections)) {
                    ownedIssues = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_ASSIGNED", Boolean.TRUE);
                } else {
                    ownedIssues = issueService.getIssuesOwnedByUser(currUser.getId());
                    request.setAttribute("UserUtilities_PREF_HIDE_ASSIGNED", Boolean.FALSE);
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED, hiddenSections)) {
                    unassignedIssues = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_UNASSIGNED", Boolean.TRUE);
                } else {
                    unassignedIssues = issueService.getUnassignedIssues();
                    request.setAttribute("UserUtilities_PREF_HIDE_UNASSIGNED", Boolean.FALSE);
                }
                
                if(UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED, hiddenSections)) {
                    watchedIssues = new ArrayList<Issue>();
                    request.setAttribute("UserUtilities_PREF_HIDE_WATCHED", Boolean.TRUE);
                } else {
                    watchedIssues = issueService.getIssuesWatchedByUser(currUser.getId());
                    request.setAttribute("UserUtilities_PREF_HIDE_WATCHED", Boolean.FALSE);
                }
                
                // SORTING ISSUES ACCORDING TO USER PREFS
                String order;
                if (null != request.getParameter("sortKey")) {
                    order = request.getParameter("sortKey");
                } else {
                    order = userPrefs.getSortColumnOnIssueList();
                }
                Comparator sort_id = Issue.ID_COMPARATOR;
                 //TODO: since repeating code, set a common Comparator variable to contain the Comparator to use and
                 //      execute the sort pre issue type only once.

                if("id".equals(order)) {
                    sort_id = Issue.ID_COMPARATOR;
                } else if("sev".equals(order)) {
                    sort_id = Issue.SEVERITY_COMPARATOR;
                } else if("stat".equals(order)) {
                    sort_id = Issue.STATUS_COMPARATOR;
                } else if("lm".equals(order)) {
                    sort_id = Issue.LAST_MODIFIED_DATE_COMPARATOR;
                } else if("own".equals(order)) {
                    sort_id = Issue.OWNER_AND_STATUS_COMPARATOR;
                }

                Collections.sort(createdIssues, sort_id);
                Collections.sort(ownedIssues, sort_id);
                unassignedIssues.removeAll(CollectionUtils.select(unassignedIssues,
                    new Predicate() {
                        @Override
                        public boolean evaluate(Object object) {
                            return object instanceof Issue
                                    && !IssueUtilities.canViewIssue((Issue) object, currUser.getId(), permissions);
                        }
                    }
                ));
                Collections.sort(unassignedIssues, sort_id);
                Collections.sort(watchedIssues, sort_id);

                
                // COPYING MODELS INTO PTOS
                
                // SETTING USER PERMISSIONS ON THE ISSUES
                ownedIssuePTOs = buildIssueList( ownedIssues, request, permissions );
                unassignedIssuePTOs = buildIssueList( unassignedIssues, request, permissions );
                createdIssuePTOs = buildIssueList( createdIssues, request, permissions );
                watchedIssuePTOs = buildIssueList( watchedIssues, request, permissions );
                // setup unassigned watched issues has notification
                if ( watchedIssuePTOs != null && watchedIssuePTOs.size() > 0 && unassignedIssuePTOs != null && unassignedIssuePTOs.size() > 0 ) {
                    for (IssuePTO watchedIssue : watchedIssuePTOs) {
                        for (IssuePTO unassignedIssuePTO : unassignedIssuePTOs) {
                            if (watchedIssue.getIssue().getId() == unassignedIssuePTO.getIssue().getId()) {
                                unassignedIssuePTO.setUserHasIssueNotification(true);
                            }
                        }
                    }
                }
                // POSSIBLE OWNERS CODE...
                
                // Because of the potentially large number of issues, and a multitude of projects, the
                // possible owners for a project are stored in a Map.  This doesn't take into account the
                // creator of the issue though since they may only have EDIT_USERS permission.  So if the
                // creator isn't already in the project list, check to see if the creator has EDIT_USERS
                // permissions, if so then add them to the list of owners and resort.

                HttpSession session = request.getSession(true);
                Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);

                for (IssuePTO issuePTO : unassignedIssuePTOs) {
                    List<User> possibleIssueOwners = new ArrayList<User>();
                    boolean creatorPresent = false;
                    final Issue issue = issuePTO.getIssue();
                    final Project project = issueService.getIssueProject(issue.getId());

                    final List<NameValuePair> ownersList;

                    ownersList = EditIssueActionUtil.getAssignableIssueOwnersList(issue, project, currUser, locale, userService, userPermissions);

                    for (NameValuePair owner : ownersList) {
                        possibleIssueOwners.add(userService.getUser(Integer.parseInt(owner.getValue())));
                        if (owner.getValue().equals(String.valueOf(issue.getCreator().getId()))) {
                            creatorPresent = true;
                        }
                    }

                    if (!creatorPresent) {
                        for (Permission creatorPermission : issue.getCreator().getPermissions()) {
                            if (creatorPermission.getPermissionType() == PermissionType.ISSUE_EDIT_USERS) {
                                possibleIssueOwners.add(userService.getUser(issue.getCreator().getId()));
                                break;
                            }
                        }
                    }
                    issuePTO.setPossibleOwners(possibleIssueOwners);

                }
                


                // PUTTING ISSUES INTO THE REQUEST SCOPE
				log.debug("ownedIssues Size: {}", ownedIssuePTOs.size());
				request.setAttribute("ownedIssues", ownedIssuePTOs);

				log.debug("unassignedIssues Size: {}", unassignedIssuePTOs.size());
				request.setAttribute("unassignedIssues", unassignedIssuePTOs);

				log.debug("createdIssues Size: {}", createdIssuePTOs.size());
				request.setAttribute("createdIssues", createdIssuePTOs);

				log.debug("watchedIssues Size: {}", watchedIssuePTOs.size());
				request.setAttribute("watchedIssues", watchedIssuePTOs);
                
                
                
                log.debug("Found forward: {} and stepped into action method that's populating portalhome",
                        forward.getName());

                request.setAttribute("ih", issueService);
                request.setAttribute("ph",projectService);
                request.setAttribute("uh",userService);
                request.setAttribute("userPrefs",userPrefs);
                //TODO: set the next value based on the request attribute!
                String showAllAtt = String.valueOf(request.getSession().getAttribute(Constants.SHOW_ALL_KEY));
                Boolean showAll = null == showAllAtt? false
                        : Boolean.valueOf(showAllAtt);
                showAllAtt = request.getParameter(Constants.SHOW_ALL_KEY);
                if (null != showAllAtt) {
                	showAll = Boolean.valueOf(showAllAtt);
                }
                if (!showAll && userPrefs.getNumItemsOnIndex() < 1) {
                	showAll=true;
                }
                
                log.debug("userPrefs.getNumItemsOnIndex(): {}, showAll: {}", userPrefs.getNumItemsOnIndex(), showAll);

                request.getSession().setAttribute(Constants.SHOW_ALL_KEY, showAll);

                request.getSession().setAttribute(Constants.ALL_SECTIONS_KEY, allSections);

                log.debug("Action is trying to forward portalhome");
            }
            return forward;
        }
    }
    
    // this function is used to load the issue type PTOs List with issue/owner/project data.  It will return this the the main
    // function for further processing.
    
    @SuppressWarnings("unchecked")
	public List<IssuePTO> buildIssueList(List<Issue> issues, HttpServletRequest request, Map<Integer, Set<PermissionType>> permissions) {
        User currUser = LoginUtilities.getCurrentUser(request);
        Locale locale = getLocale(request);
        
        List<IssuePTO> issuePTOs = new ArrayList<IssuePTO>();

        for (Issue issue : issues) {
            IssuePTO issuePTO = new IssuePTO(issue);
            issuePTO.setSeverityLocalizedString(IssueUtilities.getSeverityName(issue.getSeverity(), locale));
            issuePTO.setStatusLocalizedString(IssueUtilities.getStatusName(issue.getStatus(), locale));
            issuePTO.setUnassigned((issuePTO.getIssue().getOwner() == null));
            issuePTO.setUserCanEdit(IssueUtilities.canEditIssue(issue, currUser.getId(), permissions));
            issuePTO.setUserCanViewIssue(IssueUtilities.canViewIssue(issue, currUser.getId(), permissions));
            issuePTO.setUserHasPermission_PERMISSION_ASSIGN_SELF(UserUtilities.hasPermission(permissions, issue.getProject().getId(), UserUtilities.PERMISSION_ASSIGN_SELF));
            issuePTO.setUserHasPermission_PERMISSION_ASSIGN_OTHERS(UserUtilities.hasPermission(permissions, issue.getProject().getId(), UserUtilities.PERMISSION_ASSIGN_OTHERS));
            issuePTO.setUserHasIssueNotification(IssueUtilities.hasIssueNotification(issue, currUser.getId()));
            issuePTOs.add(issuePTO);
        }
        return issuePTOs;
    }
    
    
    public PortalHomeAction() {
        super();
  
    }
    
}
