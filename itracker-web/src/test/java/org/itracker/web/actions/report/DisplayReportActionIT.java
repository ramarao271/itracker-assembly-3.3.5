package org.itracker.web.actions.report;

import org.apache.struts.action.ActionForward;
import org.apache.struts.util.TokenProcessor;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.UserService;
import org.itracker.core.AuthenticationConstants;
import org.itracker.web.forms.DisplayReportForm;
import org.itracker.web.struts.mock.MockActionMapping;
import org.itracker.web.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.*;
public class DisplayReportActionIT extends AbstractDependencyInjectionTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionMapping actionMapping;
    private UserService userService;

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

    @Before
    public void init() {
        userService = (UserService) applicationContext.getBean("userService");

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        actionMapping = new MockActionMapping();
    }

    @Test
    public void testDisplayReport() throws ServletException, IOException {
        DisplayReportAction action = new DisplayReportAction();

        String token = TokenProcessor.getInstance().generateToken(request);

        HttpSession session = request.getSession(false);
        session.setAttribute("org.apache.struts.action.TOKEN", token);
        request.setParameter("org.apache.struts.taglib.html.TOKEN", token);

        User currentUser = new User();
        currentUser.setId(4);
        currentUser.setLogin("project_admin1");
        request.getSession().setAttribute(Constants.USER_KEY, currentUser);

        Map<Integer, Set<PermissionType>> permissions = userService.getUsersMapOfProjectIdsAndSetOfPermissionTypes(currentUser, AuthenticationConstants.REQ_SOURCE_WEB);
        request.getSession().setAttribute(Constants.PERMISSIONS_KEY, permissions);

        DisplayReportForm form = new DisplayReportForm();

        form.setType("project");
        form.setProjectIds(new Integer[]{2});
        form.setReportId(-1);
        form.setReportOutput("HTML");

        ActionForward forward = action.execute(actionMapping, form, request, response);

        assertNull("forward when reportId == ReportUtilities.REPORT_EXPORT_XML", forward);
        assertEquals("unexpected response header", "attachment; filename=\"issue_export.xml\"", response.getHeader("Content-Disposition"));
    }

}
