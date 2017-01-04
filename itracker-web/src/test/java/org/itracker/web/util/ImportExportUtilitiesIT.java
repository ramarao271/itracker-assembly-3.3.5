/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.web.util;

import org.dom4j.*;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.ImportExportException;
import org.itracker.model.*;
import org.itracker.model.CustomField.Type;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.itracker.Assert.*;


/**
 * FIXME: reimplement this test as soon we got an XML Import/Export
 *
 * @author seas
 */
public class ImportExportUtilitiesIT extends AbstractDependencyInjectionTest {

    public static final long TEST_TIMESTAMP_EXPORT1 = 50000l;
    public static final long TEST_TIMESTAMP_EXPORT2 = 8000000l;

    private String flatXml(final String xml) {
        return xml.replace("\n", "").replace("\r", "").replaceAll("> +<", "><").trim();
    }

    public void doTestImportIssues(final Reader xml,
                                   final AbstractEntity[] expected) {
        try {
            final AbstractEntity[] actual = ImportExportUtilities.importIssues(xml);
            final List<AbstractEntity> actualList = new Vector<AbstractEntity>(Arrays.asList(actual));
            final List<AbstractEntity> expectedList = new Vector<AbstractEntity>(Arrays.asList(expected));
            assertEquals(expected.length, actual.length);
            for (final AbstractEntity aeExpected : expectedList) {
                boolean found = false;
                for (final AbstractEntity aeActual : actualList) {
                    if (aeExpected instanceof Issue && aeActual instanceof Issue) {
                        final Issue issueExpected = (Issue) aeExpected;
                        final Issue issueActual = (Issue) aeActual;
                        if (issueExpected.getId().equals(issueActual.getId())) {
                            found = true;
                        }
                    } else if (aeExpected instanceof Project && aeActual instanceof Project) {
                        final Project projectExpected = (Project) aeExpected;
                        final Project projectActual = (Project) aeActual;
                        if (projectExpected.getId().equals(projectActual.getId())) {
                            found = true;
                        }
                    } else if (aeExpected instanceof User && aeActual instanceof User) {
                        final User userExpected = (User) aeExpected;
                        final User userActual = (User) aeActual;
                        if (userExpected.getId().equals(userActual.getId())) {
                            found = true;
                        }
                    } else if (aeExpected instanceof SystemConfiguration && aeActual instanceof SystemConfiguration) {
                        @SuppressWarnings("unused")
                        final SystemConfiguration configExpected =
                                (SystemConfiguration) aeExpected;
                        @SuppressWarnings("unused")
                        final SystemConfiguration configActual =
                                (SystemConfiguration) aeActual;
                        found = true;
                    }
                    if (found) {
                        actualList.remove(aeActual);
                        break;
                    }
                }
                assertTrue(found);
            }
        } catch (final ImportExportException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testImportIssue() {
        final List<Issue> issues = new Vector<Issue>();
        final Project project = new Project("project");
        project.setId(1);
        final User creator = new User();
        creator.setId(1);
        final User owner = new User();
        owner.setId(2);
        final Date dateCreate = new Date();
        final Date dateModify = new Date();
        final Issue issue1 = new Issue();
        issue1.setId(1);
        issue1.setProject(project);
        issue1.setDescription("issue description");
        issue1.setCreator(creator);
        issue1.setOwner(owner);
        issue1.setCreateDate(dateCreate);
        issue1.setLastModifiedDate(dateModify);
        issues.add(issue1);

        final Issue issue2 = new Issue();
        issue2.setId(2);
        issue2.setProject(project);
        issue2.setDescription("issue description");
        issue2.setCreator(creator);
        issue2.setOwner(owner);
        issue2.setCreateDate(dateCreate);
        issue2.setLastModifiedDate(dateModify);
        issues.add(issue2);

        final SystemConfiguration systemConfiguration =
                new SystemConfiguration();
        final String xml = "<itracker>" +
                "<configuration>" +
                "<configuration-version><![CDATA[]]></configuration-version>" +
                "<custom-fields>" +
                "</custom-fields>" +
                "<resolutions>" +
                "</resolutions>" +
                "<severities>" +
                "</severities>" +
                "<statuses>" +
                "</statuses>" +
                "</configuration>" +
                "<users>" +
                "<user id=\"user1\" systemid=\"1\">" +
                "<login><![CDATA[]]></login>" +
                "<first-name><![CDATA[]]></first-name>" +
                "<last-name><![CDATA[]]></last-name>" +
                "<email><![CDATA[]]></email>" +
                "<user-status>0</user-status>" +
                "<super-user>false</super-user>" +
                "</user>" +
                "<user id=\"user2\" systemid=\"2\">" +
                "<login><![CDATA[]]></login>" +
                "<first-name><![CDATA[]]></first-name>" +
                "<last-name><![CDATA[]]></last-name>" +
                "<email><![CDATA[]]></email>" +
                "<user-status>0</user-status>" +
                "<super-user>false</super-user>" +
                "</user>" +
                "</users>" +
                "<projects>" +
                "<project id=\"project1\" systemid=\"1\">" +
                "<project-name><![CDATA[project]]></project-name>" +
                "<project-description><![CDATA[]]></project-description>" +
                "<project-status>" + ProjectUtilities.getStatusName(project.getStatus(), ImportExportUtilities.EXPORT_LOCALE) + "</project-status>" +
                "<project-options>0</project-options>" +
                "</project>" +
                "</projects>" +
                "<issues>" +
                "<issue id=\"issue1\" systemid=\"1\">" +
                "<issue-project><![CDATA[project1]]></issue-project>" +
                "<issue-description><![CDATA[issue description]]></issue-description>" +
                "<issue-severity>3</issue-severity>" +
                "<issue-status>100</issue-status>" +
                "<issue-resolution><![CDATA[]]></issue-resolution>" +
                "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                "<creator>user1</creator>" +
                "<owner>user2</owner>" +
                "</issue>" +
                "<issue id=\"issue2\" systemid=\"2\">" +
                " <issue-project><![CDATA[project1]]></issue-project>" +
                "<issue-description><![CDATA[issue description]]></issue-description>" +
                "<issue-severity>3</issue-severity>" +
                "<issue-status>100</issue-status>" +
                "<issue-resolution><![CDATA[]]></issue-resolution>" +
                "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                "<creator>user1</creator>" +
                "<owner>user2</owner>" +
                "</issue>" +
                "</issues>" +
                "</itracker>";
        doTestImportIssues(new StringReader(xml),
                new AbstractEntity[]{
                        systemConfiguration,
                        creator,
                        owner,
                        project,
                        issue1,
                        issue2
                });

        //test throwing exception
        try {
            ImportExportUtilities.importIssues(null);
            fail("should throw ImportExportException");
        } catch (final ImportExportException e) {

        }

        try {
            ImportExportUtilities.importIssues(new StringReader(""));
            fail("should throw ImportExportException");
        } catch (final ImportExportException e) {

        }

        try {
            ImportExportUtilities.importIssues(new StringReader("Test"));
            fail("should throw ImportExportException");
        } catch (final ImportExportException e) {

        }
    }


    // TODO not stable
    @Ignore
    @Test
    public void testExportIssues() {
        final List<Issue> issues = new Vector<Issue>();
        final Project project = new Project("project");
        project.setId(1);
        final User creator = new User();
        creator.setId(1);
        final User owner = new User();
        owner.setId(2);
        final User user = new User();
        user.setId(3);
        final User attachmentCreator = new User();
        attachmentCreator.setId(4);
        final Date dateCreate = new Date(TEST_TIMESTAMP_EXPORT1);
        final Date dateModify = new Date(TEST_TIMESTAMP_EXPORT2);
        Date created = null;
        List<User> users = new ArrayList<User>();
        users.add(creator);
        project.setOwners(users);
        try {
            created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2008-11-11 12:11:10");
        } catch (Exception e) {

        }
        {
            final Issue issue = new Issue();
            issue.setId(1);
            issue.setProject(project);
            issue.setDescription("issue description");
            issue.setCreator(creator);
            issue.setOwner(owner);
            issue.setCreateDate(dateCreate);
            issue.setLastModifiedDate(dateModify);

            IssueHistory issueHistory = new IssueHistory();
            issueHistory.setUser(user);
            issueHistory.setStatus(1);
            issueHistory.setCreateDate(created);
            issueHistory.setDescription("Test issue history entry.");
            List<IssueHistory> histories = new ArrayList<IssueHistory>();
            histories.add(issueHistory);
            issue.setHistory(histories);

            IssueAttachment attachment = new IssueAttachment();
            attachment.setUser(attachmentCreator);
            attachment.setFileName("proj1_issue801_attachment1");
            attachment.setOriginalFileName("ITracker.jmx");
            attachment.setSize(192521);
            attachment.setType("text/plain");

            List<IssueAttachment> attachments = new ArrayList<IssueAttachment>();
            attachments.add(attachment);
            issue.setAttachments(attachments);

            issues.add(issue);
        }
        {
            final Issue issue = new Issue();
            issue.setId(2);
            issue.setProject(project);
            issue.setDescription("issue description");
            issue.setCreator(creator);
            issue.setOwner(owner);
            issue.setCreateDate(dateCreate);
            issue.setLastModifiedDate(dateModify);
            issues.add(issue);

        }
        final SystemConfiguration systemConfiguration =
                new SystemConfiguration();
        try {
            final String expected = readXmlString("org/itracker/services/util/testExportIssuesExpected.xml");
            String xml = ImportExportUtilities.exportIssues(issues,
                    systemConfiguration);

            assertEquals("xml", flatXml(expected),
                    flatXml(xml));
        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }

        try {
            ImportExportUtilities.exportIssues(null, systemConfiguration);
            fail("should throw ImportExportException");
        } catch (ImportExportException e) {

        }

        try {
            ImportExportUtilities.exportIssues(new ArrayList<Issue>(), systemConfiguration);
            fail("should throw ImportExportException");
        } catch (ImportExportException e) {

        }
    }

    @Test
    public void testExportModel() throws DocumentException {
        try {
            final User creator = new User();
            creator.setId(1);
            final User owner = new User();
            owner.setId(2);
            final Project project = new Project("project");
            project.setId(1);
            project.getOwners().add(creator);
            project.getOwners().add(owner);
            final Component component = new Component(project, "component");
            project.getComponents().add(component);
            final Issue issue = new Issue();
            issue.setProject(project);
            issue.setId(1);
            issue.setDescription("issue description");
            issue.setSeverity(1);
            issue.setResolution("fixed");
            issue.setTargetVersion(new Version(project, "1.1.1"));
            final Date dateCreate = new Date();
            issue.setCreateDate(dateCreate);
            final Date dateModify = new Date();
            issue.setLastModifiedDate(dateModify);
            issue.setCreator(creator);
            issue.setOwner(owner);
            final IssueHistory issueHistory = new IssueHistory(issue, creator, "some description", IssueUtilities.STATUS_NEW);
            issueHistory.setCreateDate(dateCreate);
            issueHistory.setLastModifiedDate(dateModify);
            issue.getHistory().add(issueHistory);
            issue.getComponents().add(component);
            final IssueAttachment attachment = new IssueAttachment(issue, "file.txt");
            attachment.setUser(creator);
            issue.getAttachments().add(attachment);
            issue.getVersions().add(new Version(project, "1.1.1"));
            issue.getVersions().add(new Version(project, "1.1.2"));
            final String expected = "<issue id=\"issue1\" systemid=\"1\">" +
                    "<issue-project><![CDATA[project1]]></issue-project>" +
                    "<issue-description><![CDATA[issue description]]></issue-description>" +
                    "<issue-severity>1</issue-severity>" +
                    "<issue-status>null</issue-status>" +
                    "<issue-resolution><![CDATA[fixed]]></issue-resolution>" +
                    "<target-version-id>versionnull</target-version-id>" +
                    "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                    "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                    "<creator>user1</creator>" +
                    "<owner>user2</owner>" +
                    "<issue-components>" +
                    "<component-id>componentnull</component-id>" +
                    "</issue-components>" +
                    "<issue-versions>" +
                    "<version-id>versionnull</version-id>" +
                    "<version-id>versionnull</version-id>" +
                    "</issue-versions>" +
                    "<issue-attachments>" +
                    "<issue-attachment>      <issue-attachment-description><![CDATA[]]></issue-attachment-description>" +
                    "<issue-attachment-filename><![CDATA[]]></issue-attachment-filename>" +
                    "<issue-attachment-origfile><![CDATA[file.txt]]></issue-attachment-origfile>" +
                    "<issue-attachment-size><![CDATA[0]]></issue-attachment-size>" +
                    "<issue-attachment-type><![CDATA[application/octet-stream]]></issue-attachment-type>" +
                    "<issue-attachment-creator><![CDATA[user1]]></issue-attachment-creator>" +
                    "</issue-attachment>" +
                    "</issue-attachments>" +
                    "<issue-history>" +
                    "<history-entry creator-id=\"user1\" date=\"" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "\" status=\"100\"><![CDATA[some description]]></history-entry>" +
                    "</issue-history>" +
                    "</issue>";

            final String xml = ImportExportUtilities.exportModel(issue);
            assertNotNull("xml", xml);
            assertContainsAll("xml", DocumentHelper.parseText(flatXml(expected)),
                    DocumentHelper.parseText(flatXml(xml)));

        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }

        try {
            final Project project = new Project("project");
            project.setId(1);
            final Component component = new Component(project, "component");
            component.setId(1);
            project.getComponents().add(component);
            final String expected = "<project id=\"project1\" systemid=\"1\">" +
                    "<project-name><![CDATA[project]]></project-name>" +
                    "<project-description><![CDATA[]]></project-description>" +
                    "<project-status>" + ProjectUtilities.getStatusName(project.getStatus(), ImportExportUtilities.EXPORT_LOCALE) + "</project-status>" +
                    "<project-options>0</project-options>" +
                    "<components>" +
                    "<component id=\"component1\" systemid=\"1\">" +
                    "<component-name><![CDATA[component]]></component-name>" +
                    "<component-description><![CDATA[]]></component-description>" +
                    "</component>" +
                    "</components>" +
                    "</project>";

            final String xml = ImportExportUtilities.exportModel(project);
            assertNotNull("xml", xml);
            assertContainsAll("xml", DocumentHelper.parseText(expected),
                    DocumentHelper.parseText(xml));

        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }

        try {
            final User user = new User();
            user.setId(1);
            user.setFirstName("firstName");
            user.setLastName("lastName");
            final String expected = "<user id=\"user1\" systemid=\"1\">" +
                    "<login><![CDATA[]]></login>" +
                    "<first-name><![CDATA[firstName]]></first-name>" +
                    "<last-name><![CDATA[lastName]]></last-name>" +
                    "<email><![CDATA[]]></email>" +
                    "<user-status>0</user-status>" +
                    "<super-user>false</super-user>" +
                    "</user>";
            final String xml = ImportExportUtilities.exportModel(user);
            assertNotNull("xml", xml);
            assertContainsAll("xml", DocumentHelper.parseText(expected),
                    DocumentHelper.parseText(xml));

        } catch (final ImportExportException ex) {
            assertTrue(ex.getMessage(), false);
        }

        try {
            ImportExportUtilities.exportModel(null);
            fail("should throw ImportExportException");
        } catch (ImportExportException e) {
        }

        try {
            ImportExportUtilities.exportModel(null);
            fail("should throw ImportExportException");
        } catch (ImportExportException e) {
            assertEquals("The bean to export was null.", e.getMessage());
        }

        try {
            ImportExportUtilities.exportModel(new Component());
            fail("should throw ImportExportException");
        } catch (ImportExportException e) {
            assertEquals("This bean type can not be exported.", e.getMessage());
        }
    }

    /**
     * TODO: Not a valid XML..
     */
    @Test
    public void testGetConfigurationXML() throws DocumentException {

        String got = ImportExportUtilities.getConfigurationXML(null);
        assertNotNull(got);
        assertEquals("", got);

        final SystemConfiguration config = new SystemConfiguration();
        config.setId(1);
        config.setVersion("1/1");
        final CustomField customField1 = new CustomField();
        customField1.setFieldType(Type.STRING);
        customField1.setId(1);
        config.getCustomFields().add(customField1);
        final CustomField customField2 = new CustomField();
        customField2.setFieldType(Type.LIST);
        customField2.setId(2);
        CustomFieldValue customFieldValue = new CustomFieldValue();
        customFieldValue.setId(2);
        customFieldValue.setValue("value2");
        final CustomField customField3 = new CustomField();
        customField3.setFieldType(Type.LIST);
        customFieldValue.setCustomField(new CustomField());

        customField2.getOptions().add(customFieldValue);
        config.getCustomFields().add(customField2);


        config.getResolutions().add(new Configuration(Configuration.Type.resolution, "resolution"));

        config.getSeverities().add(new Configuration(Configuration.Type.severity, "severity"));

        config.getStatuses().add(new Configuration(Configuration.Type.status, "status"));

        final String expected = "<root>"
                + readXmlString("org/itracker/services/util/testGetConfigurationXMLExpected.xml")
                + "</root>";

        final String xml = "<root>" + ImportExportUtilities.getConfigurationXML(config) + "</root>";
        assertNotNull("xml", xml);
        assertContainsAll("xml", DocumentHelper.parseText(flatXml(expected)),
                DocumentHelper.parseText(flatXml(xml)));

    }

    private String readXmlString(String filename) {
        InputStreamReader is = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filename));
        StringBuilder sb = new StringBuilder();
        try {
            while (is.ready()) {
                sb.append((char) is.read());
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return sb.toString();
    }

    @Test
    public void testConstructor() {
        ImportExportUtilities importExportUtilities = new ImportExportUtilities();

        assertNotNull(importExportUtilities);

    }

    @Test
    // Cannot test this it's not a real test, String is not XML anyway! (XML is structured, String is pain)
    public void testGetIssueXML() throws DocumentException {

        String got = ImportExportUtilities.getIssueXML(null);
        assertNotNull(got);
        assertEquals("", got);


        final User creator = new User();
        creator.setId(1);
        final User owner = new User();
        owner.setId(2);
        final Project project = new Project("project");
        project.setId(1);
        project.getOwners().add(creator);
        project.getOwners().add(owner);
        final Component component = new Component(project, "component");
        project.getComponents().add(component);
        final Issue issue = new Issue();
        issue.setProject(project);
        issue.setId(1);
        issue.setDescription("issue description");
        issue.setSeverity(1);
        issue.setResolution("fixed");
        issue.setTargetVersion(new Version(project, "1.1.1"));
        final Date dateCreate = new Date();
        issue.setCreateDate(dateCreate);
        final Date dateModify = new Date();
        issue.setLastModifiedDate(dateModify);
        issue.setCreator(creator);
        issue.setOwner(owner);
        final IssueHistory issueHistory = new IssueHistory(issue, creator, "some description", IssueUtilities.STATUS_NEW);
        issueHistory.setCreateDate(dateCreate);
        issueHistory.setLastModifiedDate(dateModify);
        issue.getHistory().add(issueHistory);
        issue.getComponents().add(component);
        final IssueAttachment attachment = new IssueAttachment(issue, "file.txt");
        attachment.setUser(creator);
        issue.getAttachments().add(attachment);
        issue.getVersions().add(new Version(project, "1.1.1"));
        issue.getVersions().add(new Version(project, "1.1.2"));
        IssueField issueField = new IssueField();
        issueField.setId(1);
        issueField.setStringValue("issue Field");
        CustomField customField = new CustomField();
        customField.setId(2);
//        customField.setName("customFieldName");
        customField.setFieldType(Type.STRING);
        issueField.setCustomField(customField);


        issue.getFields().add(issueField);

        final String expected = "<issue id=\"issue1\" systemid=\"1\">" +
                "<issue-project><![CDATA[project1]]></issue-project>" +
                "<issue-description><![CDATA[issue description]]></issue-description>" +
                "<issue-severity>1</issue-severity>" +
                "<issue-status>null</issue-status>" +
                "<issue-resolution><![CDATA[fixed]]></issue-resolution>" +
                "<target-version-id>versionnull</target-version-id>" +
                "<create-date>" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "</create-date>" +
                "<last-modified>" + ImportExportTags.DATE_FORMATTER.format(dateModify) + "</last-modified>" +
                "<creator>user1</creator>" +
                "<owner>user2</owner>" +
                "<issue-components>" +
                "<component-id>componentnull</component-id>" +
                "</issue-components>" +
                "<issue-versions>" +
                "<version-id>versionnull</version-id>" +
                "<version-id>versionnull</version-id>" +
                "</issue-versions>" +
                "<issue-fields><issue-field id=\"custom-field2\">" +
                "<![CDATA[issue Field]]></issue-field></issue-fields>" +
                "<issue-attachments>" +
                "<issue-attachment>      <issue-attachment-description><![CDATA[]]></issue-attachment-description>" +
                "<issue-attachment-filename><![CDATA[]]></issue-attachment-filename>" +
                "<issue-attachment-origfile><![CDATA[file.txt]]></issue-attachment-origfile>" +
                "<issue-attachment-size><![CDATA[0]]></issue-attachment-size>" +
                "<issue-attachment-type><![CDATA[application/octet-stream]]></issue-attachment-type>" +
                "<issue-attachment-creator><![CDATA[user1]]></issue-attachment-creator>" +
                "</issue-attachment>" +
                "</issue-attachments>" +
                "<issue-history>" +
                "<history-entry creator-id=\"user1\" date=\"" + ImportExportTags.DATE_FORMATTER.format(dateCreate) + "\" status=\"100\"><![CDATA[some description]]></history-entry>" +
                "</issue-history>" +
                "</issue>";


        final String xml = ImportExportUtilities.getIssueXML(issue);
        assertNotNull("xml", xml);
        assertContainsAll("xml", DocumentHelper.parseText(flatXml(expected)),
                DocumentHelper.parseText(flatXml(xml)));



    }

    @Test
    public void testGetProjectXML() throws DocumentException {

        String got = ImportExportUtilities.getProjectXML(null);
        assertNotNull(got);
        assertEquals("", got);

        final Project project = new Project("project");
        project.setId(1);
        final Component component = new Component(project, "component");
        component.setId(1);
        project.getComponents().add(component);

        CustomField customField = new CustomField();
        customField.setId(1);
        project.getCustomFields().add(customField);
        Version version = new Version(project, "1.1.1");
        version.setId(1);
        project.getVersions().add(version);


        final String expected = "<project id=\"project1\" systemid=\"1\">" +
                "<project-name><![CDATA[project]]></project-name>" +
                "<project-description><![CDATA[]]></project-description>" +
                "<project-status>" + ProjectUtilities.getStatusName(project.getStatus(), ImportExportUtilities.EXPORT_LOCALE) + "</project-status>" +
                "<project-options>0</project-options>" +
                "<project-custom-fields><project-custom-field>custom-field1</project-custom-field></project-custom-fields>" +
                "<components>" +
                "<component id=\"component1\" systemid=\"1\">" +
                "<component-name><![CDATA[component]]></component-name>" +
                "<component-description><![CDATA[]]></component-description>" +
                "</component>" +
                "</components>" +
                "<versions><version id=\"version1\" systemid=\"1\"><version-number><![CDATA[1.1.1]]>" +
                "</version-number><version-description><![CDATA[]]></version-description></version></versions>" +
                "</project>";

        final String xml = ImportExportUtilities.getProjectXML(project);
        assertNotNull("xml", xml);
        assertContainsAll("xml", DocumentHelper.parseText(expected),
                DocumentHelper.parseText(xml));
    }

    @Test
    public void testGetUserXML() throws DocumentException {
        String got = ImportExportUtilities.getUserXML(null);
        assertNotNull(got);
        assertEquals("", got);

        final User user = new User();
        user.setId(1);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        final String expected = "<user id=\"user1\" systemid=\"1\">" +
                "<login><![CDATA[]]></login>" +
                "<first-name><![CDATA[firstName]]></first-name>" +
                "<last-name><![CDATA[lastName]]></last-name>" +
                "<email><![CDATA[]]></email>" +
                "<user-status>0</user-status>" +
                "<super-user>false</super-user>" +
                "</user>";
        final String xml = ImportExportUtilities.getUserXML(user);
        assertNotNull("xml", xml);
        assertContainsAll("xml", DocumentHelper.parseText(expected),
                DocumentHelper.parseText(xml));

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

    /**
     * Defines a simple configuration, required for running tests.
     *
     * @return an array of references to configuration files.
     */
    protected String[] getConfigLocations() {
        return new String[]{"src/main/resources/application-context.xml"};
    }
}