package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.IssueAttachment;
import org.junit.Test;

import java.util.List;
import static org.itracker.Assert.*;

public class IssueAttachmentDAOImplIT extends AbstractDependencyInjectionTest {

    private IssueAttachmentDAO issueAttachmentDAO;

    @Test
    public void testFindByPrimaryKey() {
        IssueAttachment issueAttachment = issueAttachmentDAO.findByPrimaryKey(1);
        assertNotNull(issueAttachment);
        assertNotNull("issueAttachment.id", issueAttachment.getId());
        assertEquals("issueAttachment.id", new Integer(1), issueAttachment.getId());
        assertEquals("issueAttachment.fileName", "Derived Filename 1", issueAttachment.getFileName());
        assertEquals("issueAttachment.originalFileName", "Original Filename 1", issueAttachment.getOriginalFileName());
    }

    @Test
    public void testFindByIssue() {
        List<IssueAttachment> issueAttachments = issueAttachmentDAO.findByIssue(1);
        assertNotNull(issueAttachments);
        assertEquals("issue attachemtns for issue#1", 4, issueAttachments.size());

        issueAttachments = issueAttachmentDAO.findByIssue(2);
        assertNotNull(issueAttachments);
        assertEquals("issue attachemtns for issue#2", 0, issueAttachments.size());

    }

    @Test
    public void testFindByFileName() {
        IssueAttachment issueAttachment = issueAttachmentDAO.findByFileName("Derived Filename 1");
        assertNotNull(issueAttachment);
        assertNotNull("issueAttachemnt.fileName", issueAttachment.getFileName());
        assertEquals("issueAttachemnt.fileName", "Derived Filename 1", issueAttachment.getFileName());


        assertNull("issueAttachemnt.fileName", issueAttachmentDAO.findByFileName("undefined_file_name"));
    }

    @Test
    public void testFindAll() {
        List<IssueAttachment> issueAttachments = issueAttachmentDAO.findAll();
        assertNotNull(issueAttachments);
        assertEquals("total issue attachemtns", 4, issueAttachments.size());
    }

    @Test
    public void testCountAll() {
        Long totalAttachments = issueAttachmentDAO.countAll();
        assertNotNull(totalAttachments);
        assertEquals("total issue attachemtns", new Long(4), totalAttachments);
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueAttachmentDAO = (IssueAttachmentDAO) applicationContext.getBean("issueAttachmentDAO");

    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueattachmentbean_dataset.xml",
        };
    }


}
