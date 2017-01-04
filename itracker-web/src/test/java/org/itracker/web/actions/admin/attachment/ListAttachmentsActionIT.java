package org.itracker.web.actions.admin.attachment;

import org.apache.struts.action.ActionForward;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.IssueAttachment;
import org.itracker.persistence.dao.IssueAttachmentDAO;
import org.itracker.web.ptos.ListAttachmentsPTO;
import org.itracker.web.struts.mock.MockActionMapping;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static junit.framework.Assert.*;

public class ListAttachmentsActionIT extends AbstractDependencyInjectionTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockActionMapping actionMapping;
    private IssueAttachmentDAO issueAttachmentDAO;

    @Test
    public void testBasicCall() throws Exception {

        ListAttachmentsAction listAttachmentsAction = new ListAttachmentsAction();
        ActionForward actionForward = listAttachmentsAction.execute(actionMapping, null, request, response);

        assertNotNull(actionForward);
        assertEquals("listattachments", actionForward.getPath());

    }

    @Test
    public void testAttributeValues() throws Exception {

        ListAttachmentsAction listAttachmentsAction = new ListAttachmentsAction();
        listAttachmentsAction.execute(actionMapping, null, request, response);

        assertEquals("itracker.web.admin.listattachments.title", request.getAttribute("pageTitleKey"));
        assertEquals("", request.getAttribute("pageTitleArg"));
        ListAttachmentsPTO pto = (ListAttachmentsPTO) request.getAttribute("pto");
        assert (pto != null);
        assert (pto.getAttachments() != null);
        assertTrue(pto.isHasAttachments());
        assertEquals(pto.getAttachments().size(), 4);
    }

    @Test
    public void testAttachmentDetails() throws Exception {

        ListAttachmentsAction listAttachmentsAction = new ListAttachmentsAction();
        listAttachmentsAction.execute(actionMapping, null, request, response);
        ListAttachmentsPTO pto = (ListAttachmentsPTO) request.getAttribute("pto");
        List<IssueAttachment> attachments = (List<IssueAttachment>) pto.getAttachments();

        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(1), attachments);
        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(2), attachments);
        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(3), attachments);
        assertContainsAttachment(issueAttachmentDAO.findByPrimaryKey(4), attachments);

    }

    private void assertContainsAttachment(IssueAttachment attachment, List<IssueAttachment> attachments) {

        if (!attachments.contains(attachment)) {
            fail("Attachment not found in the list.");
        }

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueAttachmentDAO = (IssueAttachmentDAO) applicationContext.getBean("issueAttachmentDAO");

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
                "dataset/issuebean_dataset.xml",
                "dataset/issueattachmentbean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml"
        };
    }

    protected String[] getConfigLocations() {
        return new String[]{"src/main/resources/application-context.xml"};
    }

}
