package org.itracker.web.util;

import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Mock-testing for the IssueNavigationUtil.
 */
public class IssueNavigationUtilTest {


    final List<Issue> allIssues = new ArrayList<>();

    final List<Issue> adminOwnedIssues = new ArrayList<>();
    final List<Issue> adminCreatedIssues = new ArrayList<>();
    final List<Issue> adminWatchedIssues = new ArrayList<>();
    final List<Issue> unassignedIssues = new ArrayList<>();
    final Map<Integer, Set<PermissionType>> adminPermissions = new HashMap<>();
    final Issue home1 = new Issue();
    final Issue home2 = new Issue();
    final Issue home3 = new Issue();
    final Issue home4 = new Issue();
    User admin;

    /**
     * Default caller testing
     */
    @Test
    public void project() {
        setupAdminUser();
        Project p = mock(Project.class);
        setupProjectIssues(p);

        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currUser")).thenReturn(admin);
        when(session.getAttribute(Constants.PERMISSIONS_KEY)).thenReturn(
                adminPermissions);

        final HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);
        when(req.getAttribute("currUser")).thenReturn(admin);
        when(req.getRemoteUser()).thenReturn(admin.getLogin());
        when(req.getParameter("caller")).thenReturn(null);

        final IssueService svc = mock(IssueService.class);
        when(svc.getPreviousIssues(anyInt()))
                .then(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return prevIssues((Integer) invocation.getArguments()[0]);
                    }
                });
        when(svc.getNextIssues(anyInt()))
                .then(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        return nextIssues((Integer) invocation.getArguments()[0]);
                    }
                });

        // first
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home1, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home2));
        verify(req, times(1)).setAttribute(eq("previousIssue"), isNull());

        // second
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home2, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home3));
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home1));

        // third
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home3, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home4));
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home2));

        // last
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home4, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), isNull());
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home3));


        // totals
        verify(req, times(4)).setAttribute(eq("nextIssue"), any());
        verify(req, times(4)).setAttribute(eq("previousIssue"), any());
        verify(req, times(1)).setAttribute(eq("nextIssue"), isNull());
        verify(req, times(1)).setAttribute(eq("previousIssue"), isNull());


    }

    private List<Issue> prevIssues(Integer issueId) {
        final List<Issue> is = splitIssues(allIssues, issueId)[0];
        Collections.sort(is, Issue.ID_COMPARATOR);
        Collections.reverse(is);
        return is;
    }

    private List<Issue> nextIssues(Integer issueId) {
        List<Issue> is = splitIssues(allIssues, issueId)[1];
        Collections.sort(is, Issue.ID_COMPARATOR);
        return is;
    }

    private List<Issue>[] splitIssues(Collection<Issue> is, Integer issueId) {
        final ArrayList[] lists = new ArrayList[2];
        lists[0] = new ArrayList<>();
        lists[1] = new ArrayList<>();
        for (Issue i : is) {
            if (i.getId() < issueId) lists[0].add(i);
            else if (i.getId() > issueId) lists[1].add(i);
        }
        return lists;
    }

    /**
     * When opened issue from index page.
     */
    @Test
    public void portalHome() {

        setupAdminUser();
        setupAdminIssues();

        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currUser")).thenReturn(admin);
        when(session.getAttribute(Constants.PERMISSIONS_KEY)).thenReturn(
                adminPermissions);

        final HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);
        when(req.getAttribute("currUser")).thenReturn(admin);
        when(req.getRemoteUser()).thenReturn(admin.getLogin());
        when(req.getParameter("caller")).thenReturn("index");

        final IssueService svc = mock(IssueService.class);
        when(svc.getUnassignedIssues()).thenReturn(unassignedIssues);
        when(svc.getIssuesCreatedByUser(admin.getId())).thenReturn(adminCreatedIssues);
        when(svc.getIssuesOwnedByUser(admin.getId())).thenReturn(adminOwnedIssues);
        when(svc.getIssuesWatchedByUser(admin.getId())).thenReturn(adminWatchedIssues);


        // first issue
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home1, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home3));
        verify(req, never()).setAttribute(eq("previousIssue"), any());

        // last issue
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home4, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), isNull());
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home3));
        verify(req, times(2)).setAttribute(eq("nextIssue"), any());
        verify(req, times(1)).setAttribute(eq("previousIssue"), any());

        // middle issue
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home3, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home4));
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home1));
        verify(req, times(3)).setAttribute(eq("nextIssue"), any());
        verify(req, times(2)).setAttribute(eq("previousIssue"), any());

        // not in list
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home2, svc);
        verify(req, times(3)).setAttribute(eq("nextIssue"), any());
        verify(req, times(2)).setAttribute(eq("previousIssue"), any());
    }

    /**
     * from searching
     */
    @Test
    public void search() {

        final Project p = mock(Project.class);
        setupAdminUser();
        setupProjectIssues(p);
        Collections.sort(allIssues, Issue.ID_COMPARATOR);
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currUser")).thenReturn(admin);
        when(session.getAttribute(Constants.PERMISSIONS_KEY)).thenReturn(
                adminPermissions);


        final HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);
        when(req.getAttribute("currUser")).thenReturn(admin);
        when(req.getRemoteUser()).thenReturn(admin.getLogin());
        when(req.getParameter("caller")).thenReturn("searchissue");

        final IssueService  svc = null;

        final IssueSearchQuery query = mock(IssueSearchQuery.class);

        when(query.getResults()).thenReturn(allIssues);

        when(session.getAttribute(Constants.SEARCH_QUERY_KEY)).thenReturn(query);

        // first
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home1, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home2));
        verify(req, times(0)).setAttribute(eq("previousIssue"), any());

        // second
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home2, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home3));
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home1));
        // totals
        verify(req, times(2)).setAttribute(eq("nextIssue"), any());
        verify(req, times(1)).setAttribute(eq("previousIssue"), any());

        // third
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home3, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), eq(home4));
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home2));
        // totals
        verify(req, times(3)).setAttribute(eq("nextIssue"), any());
        verify(req, times(2)).setAttribute(eq("previousIssue"), any());

        // last
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home4, svc);
        verify(req, times(1)).setAttribute(eq("nextIssue"), isNull());
        verify(req, times(1)).setAttribute(eq("previousIssue"), eq(home3));
        // totals
        verify(req, times(4)).setAttribute(eq("nextIssue"), any());
        verify(req, times(3)).setAttribute(eq("previousIssue"), any());
    }


    /**
     * from searching (no results in session)
     */
    @Test
    public void searchExpired() {
        final HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("currUser")).thenReturn(admin);
        when(session.getAttribute(Constants.PERMISSIONS_KEY)).thenReturn(
                adminPermissions);
        when(session.getAttribute(Constants.SEARCH_QUERY_KEY)).thenReturn(null);

        final HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);
        when(req.getAttribute("currUser")).thenReturn(admin);
        when(req.getRemoteUser()).thenReturn(null);
        when(req.getParameter("caller")).thenReturn("searchissue");

        final IssueService  svc = null;

        // not in list
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home1, svc);
        verify(req, times(0)).setAttribute(eq("nextIssue"), any());
        verify(req, times(0)).setAttribute(eq("previousIssue"), any());

        // not in list
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home2, svc);
        verify(req, times(0)).setAttribute(eq("nextIssue"), any());
        verify(req, times(0)).setAttribute(eq("previousIssue"), any());

        // not in list
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home3, svc);
        verify(req, times(0)).setAttribute(eq("nextIssue"), any());
        verify(req, times(0)).setAttribute(eq("previousIssue"), any());

        // not in list
        IssueNavigationUtil.setupNextPreviousIssueInRequest(req, home4, svc);
        verify(req, times(0)).setAttribute(eq("nextIssue"), any());
        verify(req, times(0)).setAttribute(eq("previousIssue"), any());
    }


    @After
    public void reset() {
        admin = null;
        adminCreatedIssues.clear();
        adminOwnedIssues.clear();
        adminWatchedIssues.clear();
        unassignedIssues.clear();
        adminPermissions.clear();
    }

    public User setupAdminUser() {
        admin = new User();
        admin.setLogin("admin_123");
        admin.setId(193);
        final Set<Permission> permissions = new HashSet<>();
        permissions.add(new Permission(PermissionType.USER_ADMIN, admin));
        admin.setPermissions(permissions);
        admin.setPreferences(new UserPreferences());
        Set<PermissionType> permissionsSet = new HashSet<>();
        permissionsSet.add(PermissionType.USER_ADMIN);
        adminPermissions.put(null, permissionsSet);
        return admin;
    }

    private void setupProjectIssues(Project p) {

        Issue i = home1;
        i.setId(4);
        i.setProject(p);
        i.setOwner(admin);
        i.setCreator(admin);
        allIssues.add(i);

        i = home2;
        i.setId(5);
        i.setProject(p);
        allIssues.add(i);

        i = home3;
        i.setId(6);
        i.setProject(p);
        allIssues.add(i);

        i = home4;
        i.setId(8);
        i.setProject(p);
        allIssues.add(i);

        Collections.shuffle(allIssues);
    }

    private void setupAdminIssues() {

        Project p = mock(Project.class);
        Project p2 = mock(Project.class);

        Issue i = home1;
        i.setId(4);
        i.setProject(p);
        i.setOwner(admin);
        i.setCreator(admin);
        adminCreatedIssues.add(i);
        adminOwnedIssues.add(i);

        i = home2;
        i.setId(5);
        i.setProject(p);

        i = home3;
        i.setId(6);
        i.setProject(p2);
        unassignedIssues.add(i);

        i = home4;
        i.setId(8);
        i.setProject(p);
        unassignedIssues.add(i);
    }

}
