/**
 *
 */
package org.itracker.services;

import org.apache.log4j.Logger;
import org.itracker.IssueException;
import org.itracker.core.AuthenticationConstants;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.CustomField.Type;
import org.itracker.model.util.IssueUtilities;
import org.itracker.persistence.dao.*;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.itracker.Assert.*;
/**
 * @author ranks
 */
public class IssueServiceIT extends AbstractServicesIntegrationTest {

    private static final Logger logger = Logger
            .getLogger(IssueServiceIT.class);
    private IssueService issueService;

    private UserDAO userDAO;
    private IssueDAO issueDAO;
    private IssueRelationDAO issueRelationDAO;
    private IssueHistoryDAO issueHistoryDAO;
    private IssueAttachmentDAO issueAttachmentDAO;
    private UserService userService;

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssue(java.lang.Integer)}.
     */
    @Test
    public void testGetIssue() {

        Issue issue = this.issueService.getIssue(1);

        assertNotNull("issue#1", issue);

        this.issueService.getIssue(2);
        assertNotNull("issue#2", issue);

        this.issueService.getIssue(3);
        assertNotNull("issue#3", issue);

        this.issueService.getIssue(4);
        assertNotNull("issue#4", issue);

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getNumberIssues()}.
     */
    @Test
    public void testGetNumberIssues() {

        Long nrOfIssues = issueService.getNumberIssues();
        assertEquals("allissues", (Long) 4l, nrOfIssues);

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesWithStatus(int)}.
     */
    @Test
    public void testGetIssuesWithStatus() {

        Collection<Issue> issues = issueService.getIssuesWithStatus(100);
        assertEquals("status 1 issues", 1, issues.size());
        issues = issueService.getIssuesWithStatus(200);
        assertEquals("status 2 issues", 1, issues.size());
        issues = issueService.getIssuesWithStatus(300);
        assertEquals("status 3 issues", 2, issues.size());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesWithStatusLessThan(int)}
     * .
     */
    @Test
    public void testGetIssuesWithStatusLessThan() {
        Collection<Issue> issues = issueService.getIssuesWithStatusLessThan(200);
        assertEquals("status less 2 issues", 1, issues.size());

        issues = issueService.getIssuesWithStatusLessThan(300);
        assertEquals("status less 3 issues", 2, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesWithSeverity(int)}.
     */
    @Test
    public void testGetIssuesWithSeverity() {
        Collection<Issue> issues = issueService.getIssuesWithSeverity(1);

        assertEquals("issues severity#1", 4, issues.size());
        assertTrue("issue#1 countained", issues.contains(issueService
                .getIssue(1)));
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesByProjectId(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssuesByProjectIdInteger() throws Exception {
        Collection<Issue> issues = issueService.getIssuesByProjectId(2);

        assertEquals("issues by project#2", 4, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesByProjectId(java.lang.Integer, int)}
     * .
     */
    @Test
    public void testGetIssuesByProjectIdIntegerInt() throws Exception {
        Collection<Issue> issues = issueService.getIssuesByProjectId(2, 200);
        assertEquals("issues count", 1, issues.size());
        issues = issueService.getIssuesByProjectId(2, 300);
        assertEquals("issues count", 2, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesCreatedByUser(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssuesCreatedByUserInteger() throws Exception {
        Collection<Issue> issues = issueService.getIssuesCreatedByUser(3);
        assertEquals("issues count createdBy#3", 0, issues.size());

        issues = issueService.getIssuesCreatedByUser(2);
        assertEquals("issues count createdBy#2", 4, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesCreatedByUser(java.lang.Integer, boolean)}
     * .
     */
    @Test
    public void testGetIssuesCreatedByUserIntegerBoolean() throws Exception {
        // TODO test function for unavailable projects
        Collection<Issue> issues = issueService
                .getIssuesCreatedByUser(2, false);
        assertEquals("issues count createdBy#3", 4, issues.size());

        issues = issueService.getIssuesCreatedByUser(2, true);
        assertEquals("issues count createdBy#2", 4, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesOwnedByUser(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssuesOwnedByUserInteger() throws Exception {
        Collection<Issue> issues = issueService.getIssuesOwnedByUser(2);
        assertEquals("issues count owner#2", 4, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesOwnedByUser(java.lang.Integer, boolean)}
     * .
     */
    @Test
    public void testGetIssuesOwnedByUserIntegerBoolean() throws Exception {
        // TODO test function for unavailable projects
        Collection<Issue> issues = issueService.getIssuesOwnedByUser(2, false);
        assertEquals("issues count owner#2", 4, issues.size());

        issues = issueService.getIssuesOwnedByUser(2, true);
        assertEquals("issues count owner#2", 4, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssuesWatchedByUser(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssuesWatchedByUser() throws Exception {
        Collection<Issue> issues = issueService.getIssuesWatchedByUser(2);
        assertNotNull(issues);
        assertEquals("issues watched by#2", 1, issues.size());

        issues = issueService.getIssuesWatchedByUser(2, false);
        assertNotNull(issues);
        assertEquals("issues watched by#2 regardless of project status", 1, issues.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getUnassignedIssues()}.
     */
    @Test
    public void testGetUnassignedIssues() throws Exception {
        List<Issue> issues = issueService.getUnassignedIssues();
        assertNotNull(issues);

        // unassigned issues, status <= 200
        assertEquals("2 unassigned issues", 2, issues.size());

        issues = issueService.getUnassignedIssues(false);
        assertNotNull(issues);

        // unassigned issues, status <= 200
        assertEquals("2 unassigned issues", 2, issues.size());

        // TODO: test getUnassignedIssues(true)

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#createIssue(org.itracker.model.Issue, java.lang.Integer, java.lang.Integer, java.lang.Integer)}
     * .
     */
    @Test
    public void testCreateIssue() throws Exception {
        Issue issue = new Issue();
        issue.setStatus(1);
        issue.setDescription("hi");
        issue.setSeverity(1);
        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(2);
        assertNotNull("user#2", user);
        IssueHistory history = new IssueHistory(issue, user);
        history.setDescription("hello");
        history.setStatus(1);

        Issue newIssue = issueService.createIssue(issue, 2, user.getId(),
                user.getId());
        assertNotNull("new issue", newIssue);
        assertNotNull("model issue id", issue.getId());
        assertNotNull("new issue id", issue.getId());
        assertTrue("new issue id == model issue id",
                newIssue.getId() == issue.getId());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#updateIssue(org.itracker.model.Issue, java.lang.Integer)}
     * .
     */
    @Test
    public void testUpdateIssue() throws Exception {
        Issue updateIssue = issueService.getIssue(2);
        assertNotNull("issue", updateIssue);

        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(2);
        assertNotNull("user#2", user);

        IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);
        Integer histCount = updateIssue.getHistory().size();

        updateIssue.getHistory().add(history);

        updateIssue = issueService.updateIssue(updateIssue, 2);
        assertEquals("new history size", histCount + 1, updateIssue
                .getHistory().size());


    }

    @Test
    public void testUpdateIssueSeverity() throws Exception {
        Issue updateIssue = issueService.getIssue(2);
        assertNotNull("issue", updateIssue);

        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(2);
        assertNotNull("user#2", user);

        IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);
        // Integer histCount = updateIssue.getHistory().size();
        Integer actCount = updateIssue.getActivities().size();

        updateIssue.getHistory().add(history);
        int severity = updateIssue.getSeverity() + 1;

        updateIssue.setSeverity(severity);

        updateIssue = issueService.updateIssue(updateIssue, 2);

        assertEquals("new activity size", actCount + 1, updateIssue
                .getActivities().size());

        assertEquals("new issue severity", severity, updateIssue
                .getSeverity().intValue());

        assertEquals("new added activity type",
                IssueActivityType.SEVERITY_CHANGE, issueService.getIssue(
                updateIssue.getId()).getActivities().get(
                updateIssue.getActivities().size() - 1)
                .getActivityType());

    }

    @Test
    public void testUpdateIssueDescription() throws Exception {
        Issue updateIssue = issueService.getIssue(2);
        assertNotNull("issue", updateIssue);

        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(2);
        assertNotNull("user#2", user);

        IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);

        Integer actCount = updateIssue.getActivities().size();

        updateIssue.getHistory().add(history);
        String description = "new issue description";

        updateIssue.setDescription(description);


        updateIssue = issueService.updateIssue(updateIssue, 2);

        assertEquals("updateIssue.activities.size", actCount + 1,
                updateIssue.getActivities().size());

        assertEquals("updateIssue.description", description, updateIssue
                .getDescription());

        assertEquals("updateIssue.activity.last.type",
                IssueActivityType.DESCRIPTION_CHANGE, updateIssue
                .getActivities().get(
                        updateIssue.getActivities().size() - 1)
                .getActivityType());
        // test reloaded issue values
        Issue reloadedIssue = issueService.getIssue(updateIssue.getId());

        assertEquals("reloadedIssue.activities.size", actCount + 1,
                updateIssue.getActivities().size());

        assertEquals("reloadedIssue.description", description, updateIssue
                .getDescription());

        assertEquals("reloadedIssue.activity.last.type",
                IssueActivityType.DESCRIPTION_CHANGE, reloadedIssue
                .getActivities().get(
                        reloadedIssue.getActivities().size() - 1)
                .getActivityType());

    }

    @Test
    public void testUpdateIssueResolution() throws Exception {
        Issue updateIssue = issueService.getIssue(2);
        assertNotNull("issue", updateIssue);

        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(2);
        assertNotNull("user#2", user);

        IssueHistory history = new IssueHistory(updateIssue, user, "hi", 1);

        Integer actCount = updateIssue.getActivities().size();

        updateIssue.getHistory().add(history);
        String resolution = "new issue resolution";

        updateIssue.setResolution(resolution);


        updateIssue = issueService.updateIssue(updateIssue, 2);

        assertEquals("new activity size", actCount + 1, updateIssue
                .getActivities().size());

        assertEquals("new issue resolution", resolution, updateIssue
                .getResolution());

        assertEquals("new added activity type",
                IssueActivityType.RESOLUTION_CHANGE, issueService.getIssue(
                updateIssue.getId()).getActivities().get(
                updateIssue.getActivities().size() - 1)
                .getActivityType());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#moveIssue(org.itracker.model.Issue, java.lang.Integer, java.lang.Integer)}
     * .
     */
    @Test
    public void testMoveIssue() {

        Issue issue = issueService.getIssue(1);
        assertNotNull("issue", issue);
        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(2);
        Integer actCount = issue.getActivities().size();
        assertNotNull("user#2", user);
        issue = issueService.moveIssue(issue, 3, user.getId());

        Issue reloaded = issueService.getIssue(1);

        assertEquals("issue.project.id", Integer.valueOf(3), issue.getProject()
                .getId());
        assertEquals("reloaded.project.id", Integer.valueOf(3), reloaded
                .getProject().getId());

        assertEquals("reloaded.activities.size", actCount + 1, reloaded
                .getActivities().size());

        // org.itracker.model.IssueActivityType.ISSUE_MOVE

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#assignIssue(java.lang.Integer, java.lang.Integer)}
     * .
     */
    @Test
    public void testAssignIssueIntegerInteger() {

        Issue issue = issueService.getIssue(2);
        assertNotNull("issue", issue);
        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(4);
        assertNotNull("user#2", user);

        assertTrue("assigned", issueService.assignIssue(issue.getId(), user
                .getId()));

        assertEquals("owner", user, issue.getOwner());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#assignIssue(java.lang.Integer, java.lang.Integer, java.lang.Integer)}
     * .
     */
    @Test
    public void testAssignIssueIntegerIntegerInteger() {
        Issue issue = issueService.getIssue(2);
        assertNotNull("issue", issue);
        User user = ((UserService) applicationContext.getBean("userService"))
                .getUser(4);
        User assignerUser = ((UserService) applicationContext
                .getBean("userService")).getUser(2);
        assertNotNull("user#2", user);

        assertTrue("assigned", issueService.assignIssue(issue.getId(), user
                .getId(), assignerUser.getId()));

        assertEquals("owner", user, issue.getOwner());

        try {
            assertTrue("unassigned", issueService.assignIssue(issue.getId(), null, assignerUser.getId()));
            fail("null user allowed");
        } catch (Exception e) { /* ok */ }
    }

    /**
     * TODO: please somebody do tests on populate (multiple?) custom fields on
     * an issue Test method for
     * {@link org.itracker.services.IssueService#setIssueFields(java.lang.Integer, java.util.List)}
     * .
     */
    @Test
    public void testSetIssueFields() throws Exception {
        Issue issue = issueService.getIssue(2);
        assertNotNull("issue", issue);
        assertEquals("issue.fields.size", 2, issue.getProject().getCustomFields().size());

        assertEquals("issue.fields[0].customField", issue.getProject().getCustomFields().get(0), issue.getFields().get(0).getCustomField());

        IssueField field = issue.getFields().get(0);
        assertEquals("issue.fields[0].fieldType", Type.STRING, field.getCustomField().getFieldType());

        try {
            field.setValue("1", ITrackerResources.getBundle(Locale.US));
        } catch (IssueException e) {
            logger.error("testSetIssueFields: failed to set value", e);
            fail(e.getMessage());
        }

        issueService.setIssueFields(issue.getId(), issue.getFields());

        CustomField dateField = issue.getProject().getCustomFields().get(1);
        IssueField dateFieldValue = new IssueField(issue, dateField);

        // 1973-11-16
        dateFieldValue.setDateValue(new Date(122255164431l));

//		issue.getFields().add(dateFieldValue);
        ArrayList<IssueField> issueFields = new ArrayList<IssueField>(issue.getFields().size() + 1);
        issueFields.add(dateFieldValue);

        issueService.setIssueFields(issue.getId(), issueFields);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        issue = issueService.getIssue(2);

        assertEquals("issue.fields[0]", field, issue.getFields().get(0));
        assertEquals("issue.fields[1]", df.format(dateFieldValue.getDateValue()), df.format(issue.getFields().get(1).getDateValue().getTime()));

        boolean added = issueService.setIssueFields(issue.getId(), new ArrayList<IssueField>());
        assertTrue(added);

    }

    @Test
    public void testUpdateIssueCustomFields() throws Exception {

        Issue issue = issueService.getIssue(2);
        assertNotNull("issue", issue);
        assertEquals("issue.fields.size", 2, issue.getProject().getCustomFields().size());

        assertEquals("issue.fields[0].customField", issue.getProject().getCustomFields().get(0), issue.getFields().get(0).getCustomField());

        IssueField field = issue.getFields().get(0);
        assertEquals("issue.fields[0].fieldType", Type.STRING, field.getCustomField().getFieldType());

        try {
            field.setValue("1", ITrackerResources.getBundle(Locale.US));

        } catch (IssueException e) {
            logger.error("testSetIssueFields: failed to set value", e);
            fail(e.getMessage());
        }

        issueService.updateIssue(issue, issue.getOwner().getId());


        CustomField dateField = issue.getProject().getCustomFields().get(1);
        IssueField dateFieldValue = new IssueField(issue, dateField);

        // 1973-11-16
        dateFieldValue.setDateValue(new Date(122255164431l));

        issue.getFields().add(dateFieldValue);

        issueService.updateIssue(issue, issue.getOwner().getId());


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        issue = issueService.getIssue(2);

        assertEquals("issue.fields[0]", field, issue.getFields().get(0));
        assertEquals("issue.fields[1]", df.format(dateFieldValue.getDateValue()), df.format(issue.getFields().get(1).getDateValue().getTime()));


    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#setIssueComponents(java.lang.Integer, java.util.HashSet, java.lang.Integer)}
     * .
     */
    @Test
    public void testSetIssueComponents() throws Exception {
        HashSet<Integer> componentIds = new HashSet<Integer>();
        componentIds.add(1);
        boolean updated = issueService.setIssueComponents(3, componentIds, 2);
        assertTrue(updated);
        issueService.getIssueComponentIds(3).contains(1);

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#setIssueVersions(java.lang.Integer, java.util.HashSet, java.lang.Integer)}
     * .
     */
    @Test
    public void testSetIssueVersions() throws Exception {
        HashSet<Integer> versionIds = new HashSet<Integer>();
        versionIds.add(1);
        boolean updated = issueService.setIssueVersions(3, versionIds, 2);
        assertTrue(updated);
        issueService.getIssueVersionIds(3).contains(1);
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#addIssueHistory(org.itracker.model.IssueHistory)}
     * .
     */
    @Test
    public void testAddIssueHistory() throws Exception {

        Issue issue = issueDAO.findByPrimaryKey(1);
        User user = userDAO.findByPrimaryKey(2);

        IssueHistory history = new IssueHistory(issue, user, "", IssueUtilities.STATUS_NEW);

        history.setIssue(issue);
        issueService.addIssueHistory(history);

        assertNotNull(issueHistoryDAO.findByPrimaryKey(history.getId()));
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#addIssueRelation(java.lang.Integer, java.lang.Integer, org.itracker.model.IssueRelation.Type, java.lang.Integer)}
     * .
     */
    @Test
    public void testAddIssueRelation() throws Exception {
        // connect issues 2,3
        boolean added = issueService.addIssueRelation(2, 3, IssueRelation.Type.DUPLICATE_C, 2);
        assertTrue(added);

        // find all issue relations involving issue 2
        List<IssueRelation> issueRelations = issueRelationDAO.findByIssue(2);
        assertNotNull(issueRelations);
        assertEquals("issueRelations.size()", 1, issueRelations.size());
        IssueRelation issueRelation = issueRelations.get(0);
        assertNotNull(issueRelation);
        assertNotNull("issueRelation.id", issueRelation.getId());
        assertEquals("issueRelation.relatedIssue.id", new Integer(3), issueRelation.getRelatedIssue().getId());
        assertEquals("issueRelation.relationType", IssueRelation.Type.DUPLICATE_C, issueRelation.getRelationType());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#addIssueAttachment(org.itracker.model.IssueAttachment, byte[])}
     * .
     */
    @Test
    public void testAddIssueAttachment() throws Exception {
        Issue issue = issueDAO.findByPrimaryKey(1);
        assertNotNull(issue.getAttachments());
        int attachments = issue.getAttachments().size();
        IssueAttachment attachment = new IssueAttachment(issue, "my_file", "text/xml", "", 0);
        attachment.setUser(userDAO.findByPrimaryKey(2));
        boolean added = issueService.addIssueAttachment(attachment, new byte[]{});
        assertTrue("attachment added", added);

        issue = issueDAO.findByPrimaryKey(1);
        assertNotNull(issue.getAttachments());
        assertEquals("atachment added", attachments + 1, issue.getAttachments().size());

    }

    @Test
    public void testSetIssueAttachmentData() throws Exception {
        boolean modified = issueService.setIssueAttachmentData(1, new byte[]{9, 8, 7});
        assertTrue("attachment modified", modified);

        IssueAttachment attachment = issueAttachmentDAO.findByPrimaryKey(1);
        assertNotNull(attachment.getFileData());
        assertTrue("updated data", Arrays.equals(new byte[]{9, 8, 7}, attachment.getFileData()));


        modified = issueService.setIssueAttachmentData("Derived Filename 1", new byte[]{7, 8, 9});
        assertTrue("attachment modified", modified);

        attachment = issueAttachmentDAO.findByPrimaryKey(1);
        assertNotNull(attachment.getFileData());
        assertTrue("updated data", Arrays.equals(new byte[]{7, 8, 9}, attachment.getFileData()));


    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#removeIssueAttachment(java.lang.Integer)}
     * .
     */
    @Test
    public void testRemoveIssueAttachment() throws Exception {
        boolean removed = issueService.removeIssueAttachment(1);
        assertTrue("attachment removed", removed);
        assertNull("no db attachment", issueAttachmentDAO.findByPrimaryKey(1));
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#removeIssueHistoryEntry(java.lang.Integer, java.lang.Integer)}
     * .
     */
    @Test
    // FIXME: what's the purpose of passing userId to removeIssueHistoryEntry() ?
    public void testRemoveIssueHistoryEntry() throws Exception {
        IssueHistory issueHistory = issueHistoryDAO.findByPrimaryKey(1);
        assertNotNull(issueHistory);
        issueService.removeIssueHistoryEntry(1, 2);
        issueHistory = issueHistoryDAO.findByPrimaryKey(1);
        assertNull(issueHistory);
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#removeIssueRelation(java.lang.Integer, java.lang.Integer)}
     * .
     */
    @Test
    public void testRemoveIssueRelation() throws Exception {
        IssueRelation issueRelation = issueRelationDAO.findByPrimaryKey(1); // issue 1-2 connection
        assertNotNull("issueRelation", issueRelation);

        issueService.removeIssueRelation(1, 2);

        issueRelation = issueRelationDAO.findByPrimaryKey(1); // issue 1-2 connection
        assertNull("issueRelation", issueRelation);
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueProject(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssueProject() throws Exception {
        Issue issue = issueService.getIssue(2);

        assertEquals("issue project", issue.getProject(), issueService
                .getIssueProject(issue.getId()));
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueVersions(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssueVersions() throws Exception {

        List<Version> versions = issueService.getIssueVersions(1);
        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertEquals("version id", new Integer(1), versions.get(0).getId());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueVersionIds(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssueVersionIds() throws Exception {
        Set<Integer> versions = issueService.getIssueVersionIds(1);
        assertNotNull(versions);
        assertEquals(1, versions.size());
        assertTrue("version id", versions.contains(new Integer(1)));
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueCreator(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssueCreator() throws Exception {

        Collection<Issue> issues = issueService.getIssuesCreatedByUser(2);

        Iterator<Issue> issuesIt = issues.iterator();
        while (issuesIt.hasNext()) {
            Issue issue = (Issue) issuesIt.next();
            assertEquals("creator", (Integer) 2, issue.getCreator().getId());
        }

        User creator = issueService.getIssueCreator(1);
        assertNotNull(creator);
        assertEquals(new Integer(2), creator.getId());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueOwner(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssueOwner() throws Exception {

        Collection<Issue> issues = issueService.getIssuesOwnedByUser(2);

        Iterator<Issue> issuesIt = issues.iterator();
        while (issuesIt.hasNext()) {
            Issue issue = (Issue) issuesIt.next();
            assertEquals("creator", (Integer) 2, issue.getOwner().getId());
        }

        User owner = issueService.getIssueOwner(1);
        assertNotNull(owner);
        assertEquals(new Integer(2), owner.getId());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueActivity(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetIssueActivityInteger() throws Exception {
        List<IssueActivity> issueActivities = issueService.getIssueActivity(1);
        assertNotNull(issueActivities);
        assertEquals("issue activities for issue#1", 1, issueActivities.size());

        issueActivities = issueService.getIssueActivity(4);
        assertNotNull(issueActivities);
        assertEquals("issue activities for issue#4", 1, issueActivities.size());

    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getIssueActivity(java.lang.Integer, boolean)}
     * .
     */
    @Test
    public void testGetIssueActivityIntegerBoolean() throws Exception {
        List<IssueActivity> issueActivities = issueService.getIssueActivity(1, true);
        assertNotNull(issueActivities);
        assertEquals("issue activities for issue#1 (with notification)", 1, issueActivities.size());

        issueActivities = issueService.getIssueActivity(1, false);
        assertNotNull(issueActivities);
        assertEquals("issue activities for issue#1 (without notification)", 0, issueActivities.size());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getAllIssueAttachmentCount()}.
     */
    @Test
    public void testGetAllIssueAttachmentCount() throws Exception {
        assertEquals("total attachments", new Long(4), issueService.getAllIssueAttachmentCount());
    }

    @Test
    public void testGetAllIssueAttachmentSize() throws Exception {
        Long size = 0l;
        Iterator<IssueAttachment> attIt = issueAttachmentDAO.findAll().iterator();
        while (attIt.hasNext()) {
            IssueAttachment issueAttachment = (IssueAttachment) attIt.next();
            assertNotNull(issueAttachment);
            size += issueAttachment.getSize();
        }
        size = size / 1024;
        assertEquals("total attachmentsSize", size, issueService.getAllIssueAttachmentSize());
    }


    /**
     * Test method for
     * {@link org.itracker.services.IssueService#getLastIssueHistory(java.lang.Integer)}
     * .
     */
    @Test
    public void testGetLastIssueHistory() throws Exception {
        IssueHistory issueHistory = issueService.getLastIssueHistory(2);
        assertNotNull("issueHistory", issueHistory);
        assertEquals("issueHistory id", new Integer(1), issueHistory.getId());
    }

    /**
     * Test method for
     * {@link org.itracker.services.IssueService#canViewIssue(java.lang.Integer, org.itracker.model.User)}
     * .
     */
    @Test
    public void testCanViewIssue() throws Exception {

        Issue issue1 = issueDAO.findByPrimaryKey(1);

        assertTrue("view issue#1 permission for user#2",
                issueService.canViewIssue(1, userDAO.findByPrimaryKey(2)));
        assertTrue("view issue#1 permission for user#2",
                issueService.canViewIssue(issue1, userDAO.findByPrimaryKey(2)));

        assertFalse("view issue#1 permission for user#3",
                issueService.canViewIssue(1, userDAO.findByPrimaryKey(3)));
        assertFalse("view issue#1 permission for user#3",
                issueService.canViewIssue(issue1, userDAO.findByPrimaryKey(3)));

        assertTrue("view issue#1 permission for user#4",
                issueService.canViewIssue(1, userDAO.findByPrimaryKey(4)));
        assertTrue("view issue#1 permission for user#4",
                issueService.canViewIssue(issue1, userDAO.findByPrimaryKey(4)));

    }

    /**
     * Simple test to search for text. Test method for
     * {@link org.itracker.services.IssueService#searchIssues(org.itracker.model.IssueSearchQuery, org.itracker.model.User, java.util.Map)}
     * .
     */
    @Test
    public void testSearchIssues() throws Exception {
        Issue expected = issueService.getIssue(2);
        assertNotNull("expected", expected);
        assertEquals("expected.history[0].description", "hello..", expected
                .getHistory().get(0).getDescription());

        IssueSearchQuery query = new IssueSearchQuery();

        query.setText("hello");

        ArrayList<Integer> projectIds = new ArrayList<Integer>();
        projectIds.add(2);
        query.setProjects(projectIds);

        User user = expected.getOwner();

        Map<Integer, Set<PermissionType>> permissionsMap = userService
                .getUsersMapOfProjectIdsAndSetOfPermissionTypes(user,
                        AuthenticationConstants.REQ_SOURCE_WEB);

        List<Issue> result = issueService.searchIssues(query, user,
                permissionsMap);
        assertTrue("result.contains(expected)", result.contains(expected));

    }


    @Test
    public void testGetIssueComponents() throws Exception {
        List<Component> components = issueService.getIssueComponents(1);
        assertNotNull(components);
        assertEquals(1, components.size());

        components = issueService.getIssueComponents(4);
        assertNotNull(components);
        assertEquals(0, components.size());

    }

    @Test
    public void testGetIssueComponentIds() throws Exception {
        Set<Integer> componentIds = issueService.getIssueComponentIds(1);
        assertNotNull(componentIds);
        assertEquals("component ids for issue#1", 1, componentIds.size());

        componentIds = issueService.getIssueComponentIds(4);
        assertNotNull(componentIds);
        assertEquals("component ids for issue#4", 0, componentIds.size());

    }

    @Test
    public void testGetIssueAttachments() throws Exception {
        List<IssueAttachment> attachments = issueService.getIssueAttachments(1);
        assertNotNull(attachments);
        assertEquals(4, attachments.size());

        attachments = issueService.getIssueAttachments(2);
        assertNotNull(attachments);
        assertEquals(0, attachments.size());

    }

    @Test
    public void testGetIssueAttachment() throws Exception {
        IssueAttachment attachment = issueService.getIssueAttachment(1);
        assertNotNull(attachment);
        assertEquals("attachment id", new Integer(1), attachment.getId());
        assertEquals("attachment file name", "Derived Filename 1", attachment.getFileName());

    }

    @Test
    public void testGetIssueAttachmentData() throws Exception {
        byte[] data = issueService.getIssueAttachmentData(1);
        assertNotNull(data);
        assertTrue("attachment1.xml size", 0< data.length);

    }

    @Test
    public void testGetIssueHistory() throws Exception {
        List<IssueHistory> historyItems = issueService.getIssueHistory(1);
        assertNotNull(historyItems);
        assertEquals(0, historyItems.size());

        historyItems = issueService.getIssueHistory(2);
        assertNotNull(historyItems);
        assertEquals(1, historyItems.size());

    }

    @Test
    public void testGetIssueAttachmentCount() throws Exception {
        assertEquals("attachment count for issue#1", 4, issueService.getIssueAttachmentCount(1));
        assertEquals("attachment count for issue#4", 0, issueService.getIssueAttachmentCount(4));

    }

    @Test
    public void testGetOpenIssueCountByProjectId() throws Exception {
        assertEquals("open issues for project#2", 4, issueService.getOpenIssueCountByProjectId(2));
        assertEquals("open issues for project#3", 0, issueService.getOpenIssueCountByProjectId(3));

    }

    @Test
    public void testGetResolvedIssueCountByProjectId() throws Exception {
        assertEquals("resolved issues for project#2", 0, issueService.getResolvedIssueCountByProjectId(2));
        assertEquals("resolved issues for project#3", 0, issueService.getResolvedIssueCountByProjectId(3));

    }

    @Test
    public void testGetTotalIssueCountByProjectId() throws Exception {
        assertEquals("total issues for project#2", 4, issueService.getTotalIssueCountByProjectId(2));
        assertEquals("total issues for project#3", 0, issueService.getTotalIssueCountByProjectId(3));

    }

    @Test
    public void testGetLatestIssueDateByProjectId() throws Exception {
        Date date = issueService.getLatestIssueDateByProjectId(2);
        assertEquals("latest issue date for project#2", "2008-01-01", new SimpleDateFormat("yyyy-MM-dd").format(date));
        assertNull("latest issue date for project#3", issueService.getLatestIssueDateByProjectId(3));

    }


    @Test
    public void testSystemUpdateIssue() throws Exception {
        Issue issue = issueDAO.findByPrimaryKey(1);

        issueService.systemUpdateIssue(issue, 2);
        issue = issueDAO.findByPrimaryKey(1);
        assertNotNull(issue);
        assertNotNull(issue.getActivities());
        boolean hasSystemTypeActivity = false;
        for (IssueActivity activity : issue.getActivities()) {
            if (IssueActivityType.SYSTEM_UPDATE.equals(activity.getActivityType())) {
                hasSystemTypeActivity = true;
                break;
            }
        }
        assertTrue("has SYSTEM_UPDATE activity", hasSystemTypeActivity);


    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetNotificationService() throws Exception {
        List<Issue> issues = issueService.getAllIssues();
        assertNotNull(issues);
        assertEquals("4 issues", 4, issues.size());
    }

    @Test
    public void testGetIssueRelation() throws Exception {
        IssueRelation issueRelation = issueService.getIssueRelation(1);
        assertNotNull(issueRelation);
        assertNotNull("issue", issueRelation.getIssue());
        assertNotNull("related issue", issueRelation.getRelatedIssue());
        assertEquals("issue 1", new Integer(1), issueRelation.getIssue().getId());
        assertEquals("issue 2", new Integer(2), issueRelation.getRelatedIssue().getId());

    }


    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();
        this.issueService = (IssueService) applicationContext
                .getBean("issueService");

        this.userDAO = (UserDAO) applicationContext.getBean("userDAO");
        this.userService = (UserService) applicationContext.getBean("userService");
        this.issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
        this.issueRelationDAO = (IssueRelationDAO) applicationContext.getBean("issueRelationDAO");
        this.issueHistoryDAO = (IssueHistoryDAO) applicationContext.getBean("issueHistoryDAO");
        this.issueHistoryDAO = (IssueHistoryDAO) applicationContext.getBean("issueHistoryDAO");
        this.issueAttachmentDAO = (IssueAttachmentDAO) applicationContext.getBean("issueAttachmentDAO");

    }

    protected String[] getDataSetFiles() {
        return new String[]{"dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/customfieldbean_dataset.xml",
                "dataset/customfieldvaluebean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/projectbean_field_rel_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/permissionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issuefieldbean_dataset.xml",
                "dataset/issueattachmentbean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml",
                "dataset/issuehistorybean_dataset.xml",
                "dataset/notificationbean_dataset.xml",
                "dataset/componentbean_dataset.xml",
                "dataset/issue_component_rel_dataset.xml",
                "dataset/issue_version_rel_dataset.xml",
                "dataset/issuerelationbean_dataset.xml",
        };
    }


}
