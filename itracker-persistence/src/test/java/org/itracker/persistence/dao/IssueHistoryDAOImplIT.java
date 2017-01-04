package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.IssueHistory;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class IssueHistoryDAOImplIT extends AbstractDependencyInjectionTest {

    private IssueHistoryDAO issueHistoryDAO;

    @Test
    public void testFindByPrimaryKey() {
        IssueHistory issueHistory = issueHistoryDAO.findByPrimaryKey(1);
        assertNotNull(issueHistory);
        assertNotNull("issueHistory.id", issueHistory.getId());
        assertEquals("issueHistory.id", new Integer(1), issueHistory.getId());
        assertEquals("issueHistory.description", "hello..", issueHistory.getDescription());
        assertEquals("issueHistory.status", 1, issueHistory.getStatus());
    }

    @Test
    public void testFindByIssueId() {
        List<IssueHistory> issueHistories = issueHistoryDAO.findByIssueId(2);
        assertNotNull(issueHistories);
        assertEquals("issueHistories for issue#2", 1, issueHistories.size());

        issueHistories = issueHistoryDAO.findByIssueId(3);
        assertNotNull(issueHistories);
        assertEquals("issueHistories for issue#3", 0, issueHistories.size());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueHistoryDAO = (IssueHistoryDAO) applicationContext.getBean("issueHistoryDAO");
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/projectbean_dataset.xml",
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issuehistorybean_dataset.xml",
        };
    }

}
