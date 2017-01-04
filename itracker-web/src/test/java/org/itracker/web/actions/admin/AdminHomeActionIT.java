package org.itracker.web.actions.admin;

import org.apache.struts.action.ActionForward;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.web.struts.mock.MockActionMapping;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static junit.framework.Assert.*;
public class AdminHomeActionIT extends AbstractDependencyInjectionTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionMapping actionMapping;

    @Test
    public void testBasicCall() throws Exception {

        AdminHomeAction adminHomeAction = new AdminHomeAction();

        ActionForward actionForward = adminHomeAction.execute(actionMapping, null, request, response);

        assertNotNull(actionForward);

    }

    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();

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
