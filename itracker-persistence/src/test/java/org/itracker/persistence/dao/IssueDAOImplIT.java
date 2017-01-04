package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.junit.Test;

import java.util.*;

import static org.itracker.Assert.*;
public class IssueDAOImplIT extends AbstractDependencyInjectionTest {

    private IssueDAO issueDAO;
    private UserDAO userDAO;

    @Test
    public void testCountByProjectAndLowerStatus() {

        Long issueCount = issueDAO.countByProjectAndLowerStatus(2, 300);

        assertEquals(Long.valueOf(2), issueCount);
    }

    @Test
    public void testFindByProjectAndHigherStatus() {

        List<Issue> issues = issueDAO.findByProjectAndHigherStatus(2, 200);

        assertEquals(3, issues.size());

        assertContainsIssue(issueDAO.findByPrimaryKey(2), issues);
        assertContainsIssue(issueDAO.findByPrimaryKey(3), issues);

    }

    @Test
    public void testIssueDetach() throws Exception {

        Issue issueDetach = issueDAO.findByPrimaryKey(1);
        issueDAO.detach(issueDetach);

        issueDetach.setDescription("newDescription");
        issueDetach.setSeverity(issueDetach.getSeverity() + 1);

        Issue issue = issueDAO.findByPrimaryKey(1);

        assertFalse("issueDetach.description equals", issueDetach.getDescription().equals(issue.getDescription()));
        assertFalse("issueDetach.severity equals", issueDetach.getSeverity().equals(issue.getSeverity()));


        issueDetach = issueDAO.findByPrimaryKey(1);
        issueDetach.setDescription("newDescription");
        issueDetach.setSeverity(issueDetach.getSeverity() + 1);

        issueDAO.detach(issueDetach);

        issue = issueDAO.findByPrimaryKey(1);
        issueDAO.refresh(issue);

        assertFalse("issueDetach.description equals - 2", issueDetach.getDescription().equals(issue.getDescription()));
        assertFalse("issueDetach.severity equals - 2", issueDetach.getSeverity().equals(issue.getSeverity()));


    }

    @Test
    public void testCountByProjectAndHigherStatus() {

        Long issueCount = issueDAO.countByProjectAndHigherStatus(2, 200);

        assertEquals(Long.valueOf(3), issueCount);
    }

    @Test
    public void testCountByProject() {

        Long projectCount = issueDAO.countByProject(2);

        assertEquals(Long.valueOf(4), projectCount);
    }

    @Test
    public void testQuery() {

        IssueSearchQuery searchQuery = new IssueSearchQuery();

        List<Integer> projectsIDs = new ArrayList<Integer>();
        projectsIDs.add(2);

        searchQuery.setProjects(projectsIDs);

        User user = userDAO.findByPrimaryKey(2);
        // TODO Mock?
        Map<Integer, Set<PermissionType>> permissions = new HashMap<>();
        HashSet<PermissionType> set = new HashSet(1);
        set.add(PermissionType.USER_ADMIN);
        permissions.put(null, set);
        List<Issue> issues = issueDAO.query(searchQuery, user, permissions);

        assertEquals(4, issues.size());

    }

    @Test
    public void testFindUnassignedIssues() {

        List<Issue> issues = issueDAO.findUnassignedIssues(100);
        assertNotNull(issues);
        // all issues are assigned (have owner)
        assertEquals("total unassigned issues, status < 100", 0, issues.size());

    }

    @Test
    public void testFindByComponent() {

        List<Issue> issues = issueDAO.findByComponent(1);
        assertNotNull(issues);
        assertEquals("total issues for component#1", 3, issues.size());

        issues = issueDAO.findByComponent(2);
        assertNotNull(issues);
        assertEquals("total issues for component#2", 0, issues.size());

    }

    @Test
    public void testCountByComponent() {

        assertEquals("total issues for component#1", new Long(3), issueDAO.countByComponent(1));
        assertEquals("total issues for component#2", new Long(0), issueDAO.countByComponent(2));

    }

    @Test
    public void testFindByVersion() {
        List<Issue> issues = issueDAO.findByVersion(1);
        assertNotNull(issues);
        assertEquals("total issues for version#1", 3, issues.size());

        issues = issueDAO.findByVersion(2);
        assertNotNull(issues);
        assertEquals("total issues for version#2", 0, issues.size());

    }

    @Test
    public void testCountByVersion() {
        assertEquals("total issues for version#1", new Long(3), issueDAO.countByVersion(1));
        assertEquals("total issues for version#2", new Long(0), issueDAO.countByVersion(2));

    }


    private void assertContainsIssue(Issue issue, List<Issue> issues) {

        boolean found = false;

        for (Issue issueItem : issues) {
            if (issueItem.getId().equals(issue.getId())) {
                found = true;
                break;
            }
        }

        if (!found) {
            fail("Issue not found in the list.");
        }

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
        userDAO = (UserDAO) applicationContext.getBean("userDAO");


    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml",
                "dataset/componentbean_dataset.xml",
                "dataset/issue_component_rel_dataset.xml",
                "dataset/issue_version_rel_dataset.xml",
        };
    }


}
