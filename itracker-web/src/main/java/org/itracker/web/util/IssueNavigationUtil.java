package org.itracker.web.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author ranks
 */
public class IssueNavigationUtil {
    private static final Logger log = LoggerFactory.getLogger(IssueNavigationUtil.class);

    public static void setupNextPreviousIssueInRequest(HttpServletRequest request,
                                                     Issue issue,
                                                     IssueService issueService) {
        String caller = request.getParameter("caller");

        User user = LoginUtilities.getCurrentUser(request);
        Map<Integer, Set<PermissionType>> permissions = RequestHelper.getUserPermissions(request.getSession());

        if (StringUtils.equals(caller, "searchissue")) {
            IssueSearchQuery isqm = (IssueSearchQuery)request.getSession().getAttribute(Constants.SEARCH_QUERY_KEY);
            if (null != isqm && !CollectionUtils.isEmpty(isqm.getResults())) {
                log.debug("setupNextPreviousIssueInRequest: found search results from session");
                setupPrevNextByListing(request, issue, isqm.getResults(), user, permissions);
            }
        } else if (StringUtils.equals(caller, "index")) {
            setupPrevNextByListing(request, issue,
                    findAllIssuesForPortalhomeSections(issueService, user),
                    user, permissions);
        } else {
            setupPrevNextByProject(request, issue, issueService, user, permissions);
        }
    }

    private static List<Issue> findAllIssuesForPortalhomeSections(IssueService issueService, User user) {
        Set<Issue> issues = new TreeSet<>(Issue.ID_COMPARATOR);
        final int hiddenSections = user.getPreferences().getHiddenIndexSections();
        if(!UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_CREATED,
                hiddenSections)) {
            issues.addAll(issueService.getIssuesCreatedByUser(user.getId()));
        }
        if(!UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_ASSIGNED,
                hiddenSections)) {
            issues.addAll(issueService.getIssuesOwnedByUser(user.getId()));
        }
        if(!UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_UNASSIGNED,
                hiddenSections)) {
            issues.addAll(issueService.getUnassignedIssues());
        }
        if(!UserUtilities.hideIndexSection(UserUtilities.PREF_HIDE_WATCHED,
                hiddenSections)) {
            issues.addAll(issueService.getIssuesWatchedByUser(user.getId()));
        }
        return new ArrayList<>(issues);
    }

    private static void setupPrevNextByProject(HttpServletRequest request, Issue issue, IssueService issueService,
                                               User user,         Map<Integer, Set<PermissionType>> permissions) {
        List<Issue> nextIssues = issueService.getNextIssues(issue.getId());
        List<Issue> prevIssues = issueService.getPreviousIssues(issue.getId());

        if (log.isDebugEnabled()) {
            log.debug("setupPrevNextByProject: found {} next and () previous issues",
                    new Object[]{nextIssues.size(), prevIssues.size()});
        }
        request.setAttribute("nextIssue", firstAvailableIssue(user, permissions, nextIssues));
        request.setAttribute("previousIssue", firstAvailableIssue(user, permissions, prevIssues));
    }

    private static Issue firstAvailableIssue(User user, Map<Integer, Set<PermissionType>> permissions, List<Issue> nextIssues) {
        for (Issue next : nextIssues) {
            if (IssueUtilities.canViewIssue(next, user, permissions)) {
                if (log.isDebugEnabled()) {
                    log.debug("firstAvailableIssue: found issue {}", next.getId());
                }
                return next;
            }
        }
        return null;
    }

    private static void setupPrevNextByListing(HttpServletRequest request, Issue issue, List<Issue> issues,
                                               User user, Map<Integer, Set<PermissionType>> permissions) {
        if (CollectionUtils.isEmpty(issues) || !issues.contains(issue)) {
            return;
        }
        for (Issue i: issues) {
            if (i.equals(issue)) {
                Integer x = issues.indexOf(i);

                if (x > 0) {
                    List<Issue> sub = new ArrayList<>(issues.subList(0, x));
                    Collections.reverse(sub);
                    request.setAttribute("previousIssue",
                            firstAvailableIssue(user, permissions, sub));
                }
                if (x+1 <= issues.size())
                request.setAttribute("nextIssue",
                        firstAvailableIssue(user, permissions, issues.subList(x+1, issues.size())));
                return;
            }
        }
    }
}
