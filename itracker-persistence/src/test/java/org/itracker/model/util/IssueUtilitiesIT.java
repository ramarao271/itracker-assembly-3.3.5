/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.model.util;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.*;
import org.itracker.model.CustomField.Type;
import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import static org.itracker.Assert.*;

/**
 * TODO: make tests using the database where appropriate (setup testdata from datasets)
 *
 * @author seas at andreysergievskiy.com
 */
@Ignore
public class IssueUtilitiesIT extends AbstractDependencyInjectionTest {

    @Test
    public void testGetFieldType() {
        assertEquals(IssueUtilities.FIELD_TYPE_SINGLE,
                IssueUtilities.getFieldType(IssueUtilities.FIELD_ID));
        assertEquals(IssueUtilities.FIELD_TYPE_SINGLE,
                IssueUtilities.getFieldType(IssueUtilities.FIELD_DESCRIPTION));
        assertEquals(IssueUtilities.FIELD_TYPE_MAP,
                IssueUtilities.getFieldType(1)); // custom field

    }

    @Test
    public void testGetFieldName() {
        assertEquals("id",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_ID));
        assertEquals("description",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_DESCRIPTION));
        assertEquals("status",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_STATUS));
        assertEquals("resolution",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_RESOLUTION));
        assertEquals("severity",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_SEVERITY));
        assertEquals("creatorId",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_CREATOR));
        assertEquals("createdate",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_CREATEDATE));
        assertEquals("ownerId",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_OWNER));
        assertEquals("lastmodified",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_LASTMODIFIED));
        assertEquals("projectId",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_PROJECT));
        assertEquals("targetVersion",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_TARGET_VERSION));
        assertEquals("components",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_COMPONENTS));
        assertEquals("versions",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_VERSIONS));
        assertEquals("attachmentDescription",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_ATTACHMENTDESCRIPTION));
        assertEquals("attachment",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_ATTACHMENTFILENAME));
        assertEquals("history",
                IssueUtilities.getFieldName(IssueUtilities.FIELD_HISTORY));
        assertEquals("",
                IssueUtilities.getFieldName(null));
        assertEquals("",
                IssueUtilities.getFieldName(-999));
        assertEquals("customFields",
                IssueUtilities.getFieldName(999));
    }

    @Test
    public void testGetFieldNameByLocale() {
        final List<CustomField> customFields = new Vector<CustomField>();
        final CustomField customField1 = new CustomField();
        customField1.setFieldType(Type.STRING);
        customField1.setId(1);
        customFields.add(customField1);
        assertEquals("test-field_custom",
                IssueUtilities.getFieldName(1, customFields, new Locale("test")));
    }

    @Test
    public void testGetStandardFieldKey() {
        assertEquals("itracker.web.attr.id",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_ID));
        assertEquals("itracker.web.attr.description",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_DESCRIPTION));
        assertEquals("itracker.web.attr.status",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_STATUS));
        assertEquals("itracker.web.attr.resolution",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_RESOLUTION));
        assertEquals("itracker.web.attr.severity",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_SEVERITY));
        assertEquals("itracker.web.attr.creator",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_CREATOR));
        assertEquals("itracker.web.attr.createdate",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_CREATEDATE));
        assertEquals("itracker.web.attr.owner",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_OWNER));
        assertEquals("itracker.web.attr.lastmodified",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_LASTMODIFIED));
        assertEquals("itracker.web.attr.project",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_PROJECT));
        assertEquals("itracker.web.attr.target",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_TARGET_VERSION));
        assertEquals("itracker.web.attr.components",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_COMPONENTS));
        assertEquals("itracker.web.attr.versions",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_VERSIONS));
        assertEquals("itracker.web.attr.attachmentdescription",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_ATTACHMENTDESCRIPTION));
        assertEquals("itracker.web.attr.attachmentfilename",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_ATTACHMENTFILENAME));
        assertEquals("itracker.web.attr.detaileddescription",
                IssueUtilities.getStandardFieldKey(IssueUtilities.FIELD_HISTORY));
        assertEquals("itracker.web.generic.unknown",
                IssueUtilities.getStandardFieldKey(-999));
        assertEquals("itracker.web.generic.unknown",
                IssueUtilities.getStandardFieldKey(999));
    }

    public void doTestGetStandardFields(final Locale locale,
                                        final NameValuePair[] expected) {
        final NameValuePair[] actual = IssueUtilities.getStandardFields(locale);
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                found = nvpExpected.getName().equals(nvpActual.getName())
                        && nvpExpected.getValue().equals(nvpActual.getValue());
                if (found) break;
            }
            assertTrue("IssueUtilities.getStandardFields(" + locale + ").contains(" +
                    "new NameValuePair(" + nvpExpected.getName() + "," +
                    nvpExpected.getValue() + "))",
                    found);
        }
        assertEquals("IssueUtilities.getStandardFields(" + locale + ").length",
                expected.length, actual.length);
    }

    @Test
    public void testGetStandardFields() {
        final NameValuePair[] expected = new NameValuePair[]{
                new NameValuePair("test-field_id",
                        Integer.toString(IssueUtilities.FIELD_ID)),
                new NameValuePair("test-field_description",
                        Integer.toString(IssueUtilities.FIELD_DESCRIPTION)),
                new NameValuePair("test-field_status",
                        Integer.toString(IssueUtilities.FIELD_STATUS)),
                new NameValuePair("test-field_resolution",
                        Integer.toString(IssueUtilities.FIELD_RESOLUTION)),
                new NameValuePair("test-field_severity",
                        Integer.toString(IssueUtilities.FIELD_SEVERITY)),
                new NameValuePair("test-field_creator",
                        Integer.toString(IssueUtilities.FIELD_CREATOR)),
                new NameValuePair("test-field_createdate",
                        Integer.toString(IssueUtilities.FIELD_CREATEDATE)),
                new NameValuePair("test-field_owner",
                        Integer.toString(IssueUtilities.FIELD_OWNER)),
                new NameValuePair("test-field_lastmodified",
                        Integer.toString(IssueUtilities.FIELD_LASTMODIFIED)),
                new NameValuePair("test-field_project",
                        Integer.toString(IssueUtilities.FIELD_PROJECT)),
                new NameValuePair("test-field_target",
                        Integer.toString(IssueUtilities.FIELD_TARGET_VERSION)),
                new NameValuePair("test-field_components",
                        Integer.toString(IssueUtilities.FIELD_COMPONENTS)),
                new NameValuePair("test-field_versions",
                        Integer.toString(IssueUtilities.FIELD_VERSIONS)),
                new NameValuePair("test-field_attachmentdescription",
                        Integer.toString(IssueUtilities.FIELD_ATTACHMENTDESCRIPTION)),
                new NameValuePair("test-field_attachmentfilename",
                        Integer.toString(IssueUtilities.FIELD_ATTACHMENTFILENAME)),
                new NameValuePair("test-field_detaileddescription",
                        Integer.toString(IssueUtilities.FIELD_HISTORY))
        };
        doTestGetStandardFields(new Locale("test"), expected);
    }

    @Test
    public void testGetRelationNameByLocaleWithInt() {
        assertEquals("test-cloned_c",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.CLONED_C,
                        new Locale("test")));
        assertEquals("test-cloned_p",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.CLONED_P,
                        new Locale("test")));
        assertEquals("test-dependent_c",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.DEPENDENT_C,
                        new Locale("test")));
        assertEquals("test-dependent_p",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.DEPENDENT_P,
                        new Locale("test")));
        assertEquals("test-duplicate_c",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.DUPLICATE_C,
                        new Locale("test")));
        assertEquals("test-duplicate_p",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.DUPLICATE_P,
                        new Locale("test")));
        assertEquals("test-related_c",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.RELATED_C,
                        new Locale("test")));
        assertEquals("test-related_p",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.RELATED_P,
                        new Locale("test")));
        assertEquals("test-split_c",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.SPLIT_C,
                        new Locale("test")));
        assertEquals("test-split_p",
                IssueUtilities.getRelationName(
                        IssueRelation.Type.SPLIT_P,
                        new Locale("test")));
    }


    @Test
    public void testGetMatchingRelationType() {
        assertEquals(IssueRelation.Type.RELATED_C,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.RELATED_P));
        assertEquals(IssueRelation.Type.RELATED_P,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.RELATED_C));
        assertEquals(IssueRelation.Type.DUPLICATE_C,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.DUPLICATE_P));
        assertEquals(IssueRelation.Type.DUPLICATE_P,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.DUPLICATE_C));
        assertEquals(IssueRelation.Type.CLONED_C,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.CLONED_P));
        assertEquals(IssueRelation.Type.CLONED_P,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.CLONED_C));
        assertEquals(IssueRelation.Type.SPLIT_C,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.SPLIT_P));
        assertEquals(IssueRelation.Type.SPLIT_P,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.SPLIT_C));
        assertEquals(IssueRelation.Type.DEPENDENT_C,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.DEPENDENT_P));
        assertEquals(IssueRelation.Type.DEPENDENT_P,
                IssueUtilities.getMatchingRelationType(
                        IssueRelation.Type.DEPENDENT_C));
    }

    @Test
    public void testComponentsToString() {
        final Issue issue = new Issue();
        final Project project = new Project("project");
        {
            final Component component = new Component(project, "component1");
            issue.getComponents().add(component);
        }
        {
            final Component component = new Component(project, "component2");
            issue.getComponents().add(component);
        }
        assertEquals("component1, component2",
                IssueUtilities.componentsToString(issue));
    }

    @Test
    public void testVersionsToString() {
        final Issue issue = new Issue();
        issue.getVersions().add(new Version(new Project("project"), "1.1.1"));
        issue.getVersions().add(new Version(new Project("project"), "1.2.3"));
        issue.getVersions().add(new Version(new Project("project"), "3.2.1"));
        assertEquals("1.1.1, 1.2.3, 3.2.1",
                IssueUtilities.versionsToString(issue));
    }

    @Test
    public void testHistoryToString() {
        final Issue issue = new Issue();
        final User user = new User("user");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        final IssueHistory issueHistory = new IssueHistory(issue,
                user, "description", IssueUtilities.STATUS_CLOSED);
        issue.getHistory().add(issueHistory);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        assertEquals("description,firstName lastName,"
                + dateFormat.format(new Date()),
                IssueUtilities.historyToString(issue,
                        dateFormat));
    }

    @Test
    public void testGetStatusNameByLocaleInt() {
        assertEquals("test-status_assigned",
                IssueUtilities.getStatusName(IssueUtilities.STATUS_ASSIGNED,
                        new Locale("test")));
        assertEquals("test-status_closed",
                IssueUtilities.getStatusName(IssueUtilities.STATUS_CLOSED,
                        new Locale("test")));
        assertEquals("test-status_end",
                IssueUtilities.getStatusName(IssueUtilities.STATUS_END,
                        new Locale("test")));
        assertEquals("test-status_new",
                IssueUtilities.getStatusName(IssueUtilities.STATUS_NEW,
                        new Locale("test")));
        assertEquals("test-status_resolved",
                IssueUtilities.getStatusName(IssueUtilities.STATUS_RESOLVED,
                        new Locale("test")));
        assertEquals("test-status_unassigned",
                IssueUtilities.getStatusName(IssueUtilities.STATUS_UNASSIGNED,
                        new Locale("test")));
    }

    public void doTestGetStatusesByLocale(final Locale locale,
                                          final List<NameValuePair> expected) {
        assertEquals(expected.size(), IssueUtilities.getNumberStatuses());
        final List<NameValuePair> actual = IssueUtilities.getStatuses(locale);
        assertEquals(expected.size(), actual.size());
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                found = nvpExpected.getName().equals(nvpActual.getName())
                        && nvpExpected.getValue().equals(nvpActual.getValue());
                if (found) break;
            }
            assertTrue(found);
        }
    }

    @Test
    public void testGetStatusesByLocale() {
        final List<NameValuePair> expected = new Vector<NameValuePair>();
        expected.add(new NameValuePair("test-status_new", "100"));
        expected.add(new NameValuePair("test-status_unassigned", "200"));
        expected.add(new NameValuePair("test-status_assigned", "300"));
        expected.add(new NameValuePair("test-status_resolved", "400"));
        expected.add(new NameValuePair("test-status_closed", "500"));
//        expected.add(new NameValuePair("test-status_end", "600"));
        doTestGetStatusesByLocale(new Locale("test"), expected);
    }

    @Test
    public void testSetStatuses() {
        final List<Configuration> statuses_ = IssueUtilities.getStatuses();
        final List<Configuration> statuses = new Vector<Configuration>();
        final Configuration configuration = new Configuration(Configuration.Type.status,
                new NameValuePair("key", "100"));
        statuses.add(configuration);
        IssueUtilities.setStatuses(statuses);
        final List<NameValuePair> expected = new Vector<NameValuePair>();
        expected.add(new NameValuePair("test-status_new", "100"));
        doTestGetStatusesByLocale(new Locale("test"), expected);

    }

    @Test
    public void testGetNumberStatuses() {

        assertEquals(5, IssueUtilities.getNumberStatuses());
    }

    @Test
    public void testGetSeverityNameByLocale() {
        assertEquals("test-severity_low",
                IssueUtilities.getSeverityName(1, new Locale("test")));
        assertEquals("test-severity_middle",
                IssueUtilities.getSeverityName(2, new Locale("test")));
        assertEquals("test-severity_high",
                IssueUtilities.getSeverityName(3, new Locale("test")));
        assertEquals("test-severity_critical",
                IssueUtilities.getSeverityName(4, new Locale("test")));
    }

    public void doTestGetSeverities(final Locale locale,
                                    final List<NameValuePair> expected) {
        final List<NameValuePair> actual = IssueUtilities.getSeverities(locale);
        assertEquals(expected.size(), actual.size());
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                found = nvpExpected.getName().equals(nvpActual.getName())
                        && nvpExpected.getValue().equals(nvpActual.getValue());
                if (found) break;
            }
            assertTrue(found);
        }
    }

    @Test
    public void testGetSeverities() {
        final List<Configuration> severities = new Vector<Configuration>();
        final Configuration severity1 = new Configuration(Configuration.Type.severity,
                new NameValuePair("1", "1"));
        severities.add(severity1);
        final Configuration severity2 = new Configuration(Configuration.Type.severity,
                new NameValuePair("2", "2"));
        severities.add(severity2);
        final Configuration severity3 = new Configuration(Configuration.Type.severity,
                new NameValuePair("3", "3"));
        severities.add(severity3);
        final Configuration severity4 = new Configuration(Configuration.Type.severity,
                new NameValuePair("4", "4"));
        severities.add(severity4);
        IssueUtilities.setSeverities(severities);
        final List<NameValuePair> expected = new Vector<NameValuePair>();
        expected.add(new NameValuePair("test-severity_low", "1"));
        expected.add(new NameValuePair("test-severity_middle", "2"));
        expected.add(new NameValuePair("test-severity_high", "3"));
        expected.add(new NameValuePair("test-severity_critical", "4"));
        doTestGetSeverities(new Locale("test"), expected);
    }

    @Test
    public void testSetSeverities() {
        final List<Configuration> severities = new Vector<Configuration>();
        final Configuration severity = new Configuration(Configuration.Type.severity,
                new NameValuePair("88", "1"));
        severities.add(severity);
        IssueUtilities.setSeverities(severities);
        final List<NameValuePair> expected = new Vector<NameValuePair>();
        expected.add(new NameValuePair("test-severity_low", "1"));
        doTestGetSeverities(new Locale("test"), expected);
    }

    @Test
    public void testCompareSeverities() {
        final Issue issueA = new Issue();
        issueA.setSeverity(1);
        final Issue issueB = new Issue();
        issueB.setSeverity(2);
        assertEquals(1, IssueUtilities.compareSeverity(issueA, issueB));
    }

    @Test
    public void testGetResolutionName() {
        assertEquals("test-resolution_fixed",
                IssueUtilities.getResolutionName(1, new Locale("test")));
        assertEquals("test-resolution_pending",
                IssueUtilities.getResolutionName(2, new Locale("test")));
    }

    @Test
    public void testCheckResolutionName() {
        assertEquals("test-resolution_fixed",
                IssueUtilities.checkResolutionName("1", new Locale("test")));
        assertEquals("test-resolution_pending",
                IssueUtilities.checkResolutionName("2", new Locale("test")));
    }

    public void doTestGetResolutionsByLocale(final Locale locale,
                                             final List<NameValuePair> expected) {
        final List<NameValuePair> actual = IssueUtilities.getResolutions(locale);
        assertEquals(expected.size(), actual.size());
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                found = nvpExpected.getName().equals(nvpActual.getName())
                        && nvpExpected.getValue().equals(nvpActual.getValue());
            }
            assertTrue(found);
        }
    }

    @Test
    public void testGetResolutionsByLocale() {
        final List<Configuration> resolutions = new Vector<Configuration>();
        final Configuration resolution = new Configuration(Configuration.Type.resolution,
                new NameValuePair("one", "1"));
        resolutions.add(resolution);
        IssueUtilities.setResolutions(resolutions);
        final List<NameValuePair> expected = new Vector<NameValuePair>();
        expected.add(new NameValuePair("test-resolution_fixed", "1"));
        doTestGetResolutionsByLocale(new Locale("test"), expected);
    }

    @Test
    public void testSetResolutions() {
        final List<Configuration> resolutions = new Vector<Configuration>();
        final Configuration resolution = new Configuration(Configuration.Type.resolution,
                new NameValuePair("key", "1"));
        resolutions.add(resolution);
        IssueUtilities.setResolutions(resolutions);
        final List<NameValuePair> expected = new Vector<NameValuePair>();
        expected.add(new NameValuePair("test-resolution_fixed", "1"));
        doTestGetResolutionsByLocale(new Locale("test"), expected);
    }

    @Test
    public void testGetActivityNameByLocale() {
        assertEquals("test-activity_issue_created",
                IssueUtilities.getActivityName(IssueActivityType.ISSUE_CREATED,
                        new Locale("test")));

    }

    public void doTestGetCustomFields(final List<CustomField> expected) {
        final List<CustomField> actual = IssueUtilities.getCustomFields();
        assertEquals(expected.size(), actual.size());
        for (final CustomField cf : expected) {
            boolean found = false;
            for (final CustomField cfActual : actual) {
                found = cf.equals(cfActual);
                if (found) break;
            }
            assertTrue(found);
        }
    }

    @Test
    public void testGetCustomFields() {
        final List<CustomField> expected = new Vector<CustomField>();
        final CustomField customField1 = new CustomField();
        customField1.setFieldType(Type.STRING);
        customField1.setId(1);
        expected.add(customField1);
        IssueUtilities.setCustomFields(expected);
        doTestGetCustomFields(expected);
    }

    @Test
    public void testSetCustomFields() {
        final List<CustomField> expected = new Vector<CustomField>();
        final CustomField customField1 = new CustomField();
        customField1.setFieldType(Type.STRING);
        customField1.setId(1);
        expected.add(customField1);
        IssueUtilities.setCustomFields(expected);
        assertEquals(expected, IssueUtilities.getCustomFields());
    }


    @Test
    public void testCanViewIssue() {
        final Project project = new Project("project");
        project.setId(1);
        final Issue issue = new Issue();
        issue.setProject(project);
        final User user1 = new User();
        user1.setId(1);
        issue.setCreator(user1);
        final User user2 = new User();
        user2.setId(2);
        final Map<Integer, Set<PermissionType>> permissions =
                new HashMap<Integer, Set<PermissionType>>();
        assertTrue(IssueUtilities.canViewIssue(issue, user1, permissions));
        assertFalse(IssueUtilities.canViewIssue(issue, user2, permissions));
        final Set<PermissionType> permissionSet = new HashSet<PermissionType>();
        permissionSet.add(PermissionType.ISSUE_VIEW_ALL);
        permissions.put(1, permissionSet);
        assertTrue(IssueUtilities.canViewIssue(issue, user2, permissions));
    }

    @Test
    public void testCanEditIssue() {
        final Project project = new Project("project");
        project.setId(1);
        final Issue issue = new Issue();
        issue.setProject(project);
        final User user1 = new User();
        user1.setId(1);
        issue.setCreator(user1);
        final User user2 = new User();
        user2.setId(2);
        final Map<Integer, Set<PermissionType>> permissions =
                new HashMap<Integer, Set<PermissionType>>();
        final Set<PermissionType> permissionSet = new HashSet<PermissionType>();
        permissions.put(1, permissionSet);
        permissionSet.add(PermissionType.ISSUE_EDIT_USERS);
        assertTrue(IssueUtilities.canEditIssue(issue, user1.getId(), permissions));
        assertFalse(IssueUtilities.canEditIssue(issue, user2.getId(), permissions));
        permissionSet.add(PermissionType.ISSUE_EDIT_ALL);
        assertTrue(IssueUtilities.canEditIssue(issue, user1.getId(), permissions));
        assertTrue(IssueUtilities.canEditIssue(issue, user2.getId(), permissions));
    }

    @Test
    public void testCanBeAssignedIssue() {
        final Project project = new Project("project");
        project.setId(1);
        final Issue issue = new Issue();
        issue.setProject(project);
        final Integer user1Id = 1;
        final Integer user2Id = 2;
        final Map<Integer, Set<PermissionType>> permissions =
                new HashMap<Integer, Set<PermissionType>>();
        final Set<PermissionType> permissionSet = new HashSet<PermissionType>();
        permissionSet.add(PermissionType.ISSUE_ASSIGNABLE);
        permissionSet.add(PermissionType.ISSUE_EDIT_ALL);
        permissions.put(1, permissionSet);
        assertTrue(IssueUtilities.canBeAssignedIssue(issue, user1Id, permissions));
        assertTrue(IssueUtilities.canBeAssignedIssue(issue, user2Id, permissions));
    }

    @Test
    public void testCanUnassigneIssue() {
        final Project project = new Project("project");
        project.setId(1);
        final Issue issue = new Issue();
        issue.setProject(project);
        final User user1 = new User();
        user1.setId(1);
        issue.setOwner(user1);
        final User user2 = new User();
        user2.setId(2);
        final Map<Integer, Set<PermissionType>> permissions =
                new HashMap<Integer, Set<PermissionType>>();
        final Set<PermissionType> permissionSet = new HashSet<PermissionType>();
        permissionSet.add(PermissionType.ISSUE_UNASSIGN_SELF);
        permissions.put(1, permissionSet);
        assertTrue(IssueUtilities.canUnassignIssue(issue, user1.getId(), permissions));
        assertFalse(IssueUtilities.canUnassignIssue(issue, user2.getId(), permissions));
    }

    @Test
    public void testHasIssueRelation() {
        final Project project = new Project("project");
        project.setId(1);
        final Issue issue = new Issue();
        final Issue related = new Issue();
        related.setId(1);
        final IssueRelation relation =
                new IssueRelation(issue, related,
                        IssueRelation.Type.CLONED_C);
        issue.getRelations().add(relation);
        assertTrue(IssueUtilities.hasIssueRelation(issue, 1));
        assertFalse(IssueUtilities.hasIssueRelation(issue, 2));
    }

    @Test
    public void testHasIssueNotification() {
        final Project project = new Project("project");
        project.setId(1);
        final Issue issue = new Issue();
        issue.setId(1);
        issue.setProject(project);
        final User user1 = new User();
        user1.setId(1);
        issue.setCreator(user1);
        issue.setOwner(user1);
        assertTrue(IssueUtilities.hasIssueNotification(issue, 1));
        //assertFalse(IssueUtilities.hasIssueNotification(issue, 2));
    }

    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();
    }

    /**
     * Defines a set of datafiles to be uploaded into database.
     *
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_dataset.xml"
        };
    }

}
