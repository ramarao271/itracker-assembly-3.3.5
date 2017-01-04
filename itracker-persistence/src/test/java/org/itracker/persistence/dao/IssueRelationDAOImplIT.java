package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.IssueRelation;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class IssueRelationDAOImplIT extends AbstractDependencyInjectionTest {

    private IssueRelationDAO issueRelationDAO;


    @Test
    public void testFindByPrimaryKey() {

        Integer issueId = 1;
        Integer relatedIssueId = 2;

        IssueRelation issueRelation = issueRelationDAO.findByPrimaryKey(1);
        assertNotNull(issueRelation);
        assertEquals(issueId, issueRelation.getIssue().getId());
        assertEquals(relatedIssueId, issueRelation.getRelatedIssue().getId());

    }

    @Test
    public void testFindByIssue() {
        Integer issueId = 1;
        Integer relatedIssueId = 2;

        List<IssueRelation> results = issueRelationDAO.findByIssue(issueId);

        assertNotNull(results);

        IssueRelation issueRelation = results.get(0);

        assertNotNull(issueRelation);
        assertEquals(issueId, issueRelation.getIssue().getId());
        assertEquals(relatedIssueId, issueRelation.getRelatedIssue().getId());

    }


    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();


        issueRelationDAO = (IssueRelationDAO) applicationContext
                .getBean("issueRelationDAO");

    }

    protected String[] getDataSetFiles() {
        return new String[]{"dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml",
                "dataset/issuerelationbean_dataset.xml"};
    }

}
