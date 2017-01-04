package org.itracker.web.actions.project;

import junit.framework.Assert;
import org.apache.struts.action.ActionForward;
import org.apache.struts.util.TokenProcessor;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.UserService;
import org.itracker.core.AuthenticationConstants;
import org.itracker.services.authentication.ITrackerUserDetails;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.struts.mock.MockActionMapping;
import org.itracker.web.util.Constants;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;

public class EditIssueActionIT extends AbstractDependencyInjectionTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionMapping actionMapping;
    private UserService userService;

    @Test
    public void testBasicCall() throws Exception {

        EditIssueAction editIssueAction = new EditIssueAction();
        createTemporaryToken(request);

        User currentUser = new User();
        currentUser.setId(4);
        currentUser.setLogin("project_admin1");
        request.getSession().setAttribute(Constants.USER_KEY, currentUser);
        request.setRemoteUser("project_admin1");

        Map<Integer, Set<PermissionType>> permissions = userService.getUsersMapOfProjectIdsAndSetOfPermissionTypes(currentUser, AuthenticationConstants.REQ_SOURCE_WEB);
        request.getSession().setAttribute(Constants.PERMISSIONS_KEY, permissions);

        UserDetails userDetails = new ITrackerUserDetails(currentUser,
                                userService.getPermissionsByUserId(4));
        Authentication authentication =
                new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities()
                        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.setUserPrincipal(authentication);


        IssueForm issueForm = new IssueForm();
        issueForm.setId(1);
        issueForm.setDescription("description");
        issueForm.setStatus(100);

        ActionForward actionForward = editIssueAction.execute(actionMapping, issueForm, request, response);

        assertNotNull(actionForward);
        // validation issue?
//        assertEquals("listissues?projectId=2", actionForward.getPath());

    }

    private void createTemporaryToken(MockHttpServletRequest request) {

        String token = TokenProcessor.getInstance().generateToken(request);

        HttpSession session = request.getSession(false);
        session.setAttribute("org.apache.struts.action.TOKEN", token);
        request.setParameter("org.apache.struts.taglib.html.TOKEN", token);

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        userService = (UserService) applicationContext.getBean("userService");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        actionMapping = new MockActionMapping();

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/permissionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueattachmentbean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"src/main/resources/application-context.xml"};
    }

}

