package org.itracker.web.util;

import junit.framework.TestCase;
import org.itracker.model.*;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ImportHandlerIT extends TestCase {


    @Test
    public void testConstructor() {
        ImportHandler handler = new ImportHandler();
        assertNotNull(handler);
        assertEquals(0, handler.getModels().length);
    }

    @Test
    public void testStartDocument() {
        ImportHandler handler = new ImportHandler();
        handler.startDocument();
    }

    @Test
    public void testEndDocument() {
        ImportHandler handler = new ImportHandler();
        handler.endDocument();
    }

    @Test
    public void testProjectElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        //test throwing SAXException
        try {
            handler.startElement("", "project", "project", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "project", "project");
        } catch (SAXException e) {
            assertEquals("Attribute " + ImportExportTags.ATTR_SYSTEMID + " was null for project.", e.getMessage());
        } catch (Exception e) {
            fail("should throw SAXException");
        }

        handler = new ImportHandler();

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "project", "project", attributes);

        handler.startElement("", "project-name", "project-name", new AttributesImpl());
        handler.endElement("", "project-name", "project-name");
        handler.startElement("", "project-status", "project-status", new AttributesImpl());
        handler.characters("Active".toCharArray(), 0, 6);
        handler.endElement("", "project-status", "project-status");
        handler.startElement("", "project-description", "project-description", new AttributesImpl());
        handler.endElement("", "project-description", "project-description");
        handler.startElement("", "project-options", "project-options", new AttributesImpl());
        handler.characters("2".toCharArray(), 0, 1);
        handler.endElement("", "project-options", "project-options");

        handler.endElement("", "project", "project");

        AbstractEntity[] models = handler.getModels();

        assertNotNull("expected not null models", models);
        assertEquals("expect first element in models: Project.class", Project.class, models[0].getClass());

        Project project = (Project) models[0];
        assertEquals("Expect project id", Integer.valueOf(1), project.getId());

    }

    @Test
    public void testConfigurationElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        handler.startElement("", "configuration", "configuration", new AttributesImpl());
        handler.startElement("", "configuration-version", "configuration-version", new AttributesImpl());

        handler.endElement("", "configuration-version", "configuration-version");
        handler.endElement("", "configuration", "configuration");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

        assertEquals(SystemConfiguration.class, models[0].getClass());

    }

    @Test
    public void testCustomField() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        try {
            handler.startElement("", "custom-field", "custom-field", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "custom-field", "custom-field");
        } catch (SAXException e) {
            // expected
        } catch (Exception e) {
            fail("should throw SAXException");
        }

        handler = new ImportHandler();
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");




        try {
            AttributesImpl projectAttrs = new AttributesImpl();
            projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
            handler.startElement("", "configuration", "configuration", new AttributesImpl());

            handler.startElement("", "custom-fields", "custom-fields", null);

            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "", ImportHandler.ATTR_VALUE, "", "fieldoptionvalue");
            handler.startElement("", "custom-field", "custom-field", attributes);
            handler.startElement("", "custom-field-option", "custom-field-option", attrs);
            handler.endElement("", "custom-field-option", "custom-field-option");
            handler.startElement("", "custom-field-dateformat", "custom-field-dateformat", attrs);
            handler.endElement("", "custom-field-dateformat", "custom-field-dateformat");
            handler.startElement("", "custom-field-label", "custom-field-label", attrs);
            handler.endElement("", "custom-field-label", "custom-field-label");
            handler.startElement("", "custom-field-required", "custom-field-required", attrs);
            handler.endElement("", "custom-field-required", "custom-field-required");
            handler.startElement("", "custom-field-sortoptions", "custom-field-sortoptions", attrs);
            handler.endElement("", "custom-field-sortoptions", "custom-field-sortoptions");
            handler.startElement("", "custom-field-type", "custom-field-type", attrs);
            handler.characters("INTEGER".toCharArray(), 0, 7);
            handler.endElement("", "custom-field-type", "custom-field-type");
            handler.endElement("", "custom-field", "custom-field");

            handler.endElement("", "custom-fields", "custom-fields");
            handler.endElement("", "configuration", "configuration");


            AbstractEntity[] models = handler.getModels();
            assertNotNull(models);

            assertEquals(CustomField.class, models[0].getClass());

            CustomField field = (CustomField) models[0];
            assertEquals(Integer.valueOf(1), field.getId());
            assertEquals(1, field.getOptions().size());

        } catch (SAXException e) {
            throw e;
        }

        // legacy int-type check
        try {
            AttributesImpl projectAttrs = new AttributesImpl();
            projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
            handler.startElement("", "configuration", "configuration", new AttributesImpl());

            handler.startElement("", "custom-fields", "custom-fields", null);

            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "", ImportHandler.ATTR_VALUE, "", "fieldoptionvalue");
            handler.startElement("", "custom-field", "custom-field", attributes);
            handler.startElement("", "custom-field-option", "custom-field-option", attrs);
            handler.endElement("", "custom-field-option", "custom-field-option");
            handler.startElement("", "custom-field-dateformat", "custom-field-dateformat", attrs);
            handler.endElement("", "custom-field-dateformat", "custom-field-dateformat");
            handler.startElement("", "custom-field-label", "custom-field-label", attrs);
            handler.endElement("", "custom-field-label", "custom-field-label");
            handler.startElement("", "custom-field-required", "custom-field-required", attrs);
            handler.endElement("", "custom-field-required", "custom-field-required");
            handler.startElement("", "custom-field-sortoptions", "custom-field-sortoptions", attrs);
            handler.endElement("", "custom-field-sortoptions", "custom-field-sortoptions");
            handler.startElement("", "custom-field-type", "custom-field-type", attrs);
            handler.characters("2".toCharArray(), 0, 1);
            handler.endElement("", "custom-field-type", "custom-field-type");
            handler.endElement("", "custom-field", "custom-field");

            handler.endElement("", "custom-fields", "custom-fields");
            handler.endElement("", "configuration", "configuration");


            AbstractEntity[] models = handler.getModels();
            assertNotNull(models);

            assertEquals(CustomField.class, models[0].getClass());

            CustomField field = (CustomField) models[0];
            assertEquals(Integer.valueOf(1), field.getId());
            assertEquals(1, field.getOptions().size());

        } catch (SAXException e) {
            throw e;
        }

    }

    @Test
    public void testThrowingExceptions() throws Exception {

        ImportHandler handler = new ImportHandler();
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        try {
            AttributesImpl projectAttrs = new AttributesImpl();
            projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
            handler.startElement("", "configuration", "configuration", new AttributesImpl());

            handler.startElement("", "custom-fields", "custom-fields", null);


            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "", ImportHandler.ATTR_VALUE, "", "fieldoptionvalue");
            handler.startElement("", "custom-field", "custom-field", attributes);

            handler.startElement("", "custom-field-type", "custom-field-type", attrs);
            handler.characters("A".toCharArray(), 0, 1);
            handler.endElement("", "custom-field-type", "custom-field-type");
            fail("should throw SAXException");

        } catch (SAXException e) {
            assertEquals("Could not convert string buffer to type value.", e.getMessage());
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }

        try {
            AttributesImpl projectAttrs = new AttributesImpl();
            projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
            handler.startElement("", "configuration", "configuration", new AttributesImpl());

            handler.startElement("", "custom-fields", "custom-fields", null);


            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "", ImportHandler.ATTR_VALUE, "", "fieldoptionvalue");
            handler.startElement("", "custom-field", "custom-field", attributes);

            handler.startElement("", "custom-field-type", "custom-field-type", attrs);
            handler.characters("11".toCharArray(), 0, 2);
            handler.endElement("", "custom-field-type", "custom-field-type");
            fail("should throw SAXException");

        } catch (SAXException e) {
            assertEquals("Could not convert string buffer to type value.", e.getMessage());
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }

        try {
            handler = new ImportHandler();
            AttributesImpl issueAttrs = new AttributesImpl();
            issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

            handler.startElement("", "issue", "issue", issueAttrs);

            handler.startElement("", "issue-attachments", "issue-attachments", new AttributesImpl());
            handler.startElement("", "issue-attachment", "issue-attachment", new AttributesImpl());

            handler.startElement("", "issue-attachment-size", "issue-attachment-size", new AttributesImpl());
            handler.characters("A".toCharArray(), 0, 1);
            handler.endElement("", "issue-attachment-size", "issue-attachment-size");

            handler.endElement("", "issue-attachment", "issue-attachment");
            handler.endElement("", "issue-attachments", "issue-attachments");

            handler.endElement("", "issue", "issue");
        } catch (SAXException e) {
            assertEquals("Could not convert string buffer to long value.", e.getMessage());
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }

        try {
            handler = new ImportHandler();

            AttributesImpl issueAttrs = new AttributesImpl();
            issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

            handler.startElement("", "issue", "issue", issueAttrs);
            handler.startElement("", "create-date", "create-date", new AttributesImpl());
            handler.characters("A".toCharArray(), 0, 1);
            handler.endElement("", "create-date", "create-date");
            fail("should throw SAXException not ");
            handler.endElement("", "issue", "issue");
        } catch (SAXException e) {
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }


    }


    @Test
    public void testComponentElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        //test throwing SAXException
        try {
            handler.startElement("", "component", "component", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "component", "component");
        } catch (SAXException e) {
            assertEquals("Attribute " + ImportExportTags.ATTR_SYSTEMID + " was null for component.", e.getMessage());
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }


        handler = new ImportHandler();

        //create project parent
        AttributesImpl projectAttrs = new AttributesImpl();
        projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        handler.startElement("", "project", "project", projectAttrs);


        handler.startElement("", "components", "components", null);

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        attributes.addAttribute("", "", ImportHandler.ATTR_ID, "", "1");

        handler.startElement("", "component", "component", attributes);

        handler.startElement("", "component-name", "component-name", new AttributesImpl());
        handler.endElement("", "component-name", "component-name");
        handler.startElement("", "component-description", "component-description", new AttributesImpl());
        handler.endElement("", "component-description", "component-description");

        handler.endElement("", "component", "component");

        handler.endElement("", "components", "components");
        handler.endElement("", "project", "project");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

        //created project and component elements
        assertEquals(2, models.length);

        assertEquals(Component.class, models[0].getClass());
        assertEquals(Project.class, models[1].getClass());

        Project project = (Project) models[1];
        assertEquals(1, project.getComponents().size());
        Component component = (Component) models[0];
        assertEquals(Integer.valueOf(1), component.getId());

    }

    @Test
    public void testIssueElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        try {
            handler.startElement("", "issue", "issue", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "issue", "issue");
        } catch (SAXException e) {
            assertEquals("Attribute " + ImportExportTags.ATTR_SYSTEMID + " was null for issue.", e.getMessage());
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }


        handler = new ImportHandler();

        AttributesImpl issueAttrs = new AttributesImpl();
        issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "issue", "issue", issueAttrs);
        handler.startElement("", "create-date", "create-date", new AttributesImpl());
        handler.endElement("", "create-date", "create-date");
        handler.startElement("", "issue-description", "issue-description", new AttributesImpl());
        handler.endElement("", "issue-description", "issue-description");
        handler.startElement("", "issue-project", "issue-project", new AttributesImpl());
        handler.endElement("", "issue-project", "issue-project");
        handler.startElement("", "issue-resolution", "issue-resolution", new AttributesImpl());
        handler.endElement("", "issue-resolution", "issue-resolution");
        handler.startElement("", "last-modified", "last-modified", new AttributesImpl());
        handler.endElement("", "last-modified", "last-modified");
        handler.startElement("", "owner", "owner", new AttributesImpl());
        handler.endElement("", "owner", "owner");
        handler.startElement("", "target-version-id", "target-version-id", new AttributesImpl());
        handler.endElement("", "target-version-id", "target-version-id");
        handler.startElement("", "issue-severity", "issue-severity", new AttributesImpl());
        handler.characters("2".toCharArray(), 0, 1);
        handler.endElement("", "issue-severity", "issue-severity");
        handler.startElement("", "issue-status", "issue-status", new AttributesImpl());
        handler.characters("2".toCharArray(), 0, 1);
        handler.endElement("", "issue-status", "issue-status");
        handler.startElement("", "creator", "creator", new AttributesImpl());
        handler.endElement("", "creator", "creator");
        handler.endElement("", "issue", "issue");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }

    @Test
    public void testUserElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        try {
            handler.startElement("", "user", "user", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "user", "user");
        } catch (SAXException e) {

        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }

        handler = new ImportHandler();

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "users", "users", new AttributesImpl());
        handler.startElement("", "user", "user", attributes);

        handler.startElement("", "email", "email", attributes);
        handler.endElement("", "email", "email");
        handler.startElement("", "login", "login", attributes);
        handler.endElement("", "login", "login");
        handler.startElement("", "first-name", "first-name", attributes);
        handler.endElement("", "first-name", "first-name");
        handler.startElement("", "last-name", "last-name", attributes);
        handler.endElement("", "last-name", "last-name");
        handler.startElement("", "super-user", "super-user", attributes);
        handler.endElement("", "super-user", "super-user");
        handler.startElement("", "user-status", "user-status", attributes);
        handler.characters("Active".toCharArray(), 0, 6);
        handler.endElement("", "user-status", "user-status");

        handler.endElement("", "user", "user");
        handler.endElement("", "users", "users");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

        assertEquals(User.class, models[0].getClass());
        User user = (User) models[0];
        assertEquals(Integer.valueOf(1), user.getId());


    }

    @Test
    public void testProjectOwners() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        AttributesImpl userAttrs = new AttributesImpl();
        userAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "users", "users", new AttributesImpl());
        handler.startElement("", "user", "user", userAttrs);

        handler.endElement("", "user", "user");

        handler.endElement("", "users", "users");

        AttributesImpl projectAttrs = new AttributesImpl();
        projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        handler.startElement("", "project", "project", projectAttrs);

        handler.startElement("", "project-owners", "project-owners", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "project-owner", "project-owner", attributes);

        handler.endElement("", "project-owner", "project-owner");
        handler.endElement("", "project-owners", "project-owners");

        handler.endElement("", "project", "project");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }

    @Test
    public void testHistoryElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        AttributesImpl attributes = new AttributesImpl();

        try {
            handler.startElement("", "history-entry", "history-entry", attributes);
            fail("should throw SAXException");
            handler.endElement("", "history-entry", "history-entry");
        } catch (SAXException e) {
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }


        attributes.addAttribute("", "", ImportHandler.ATTR_CREATOR_ID, "", "user1");
        try {
            handler.startElement("", "history-entry", "history-entry", attributes);
            fail("should throw SAXException");
            handler.endElement("", "history-entry", "history-entry");
        } catch (SAXException e) {
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }


        handler = new ImportHandler();

        AttributesImpl userAttrs = new AttributesImpl();
        userAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "users", "users", new AttributesImpl());
        handler.startElement("", "user", "user", userAttrs);

        handler.endElement("", "user", "user");

        handler.endElement("", "users", "users");


        AttributesImpl issueAttrs = new AttributesImpl();
        issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "issue", "issue", issueAttrs);

        attributes.addAttribute("", "", ImportHandler.ATTR_DATE, "", "12/12/2008 12:12:12");
        attributes.addAttribute("", "", ImportHandler.ATTR_STATUS, "", "1");

        handler.startElement("", "issue-history", "issue-history", new AttributesImpl());
        handler.startElement("", "history-entry", "history-entry", attributes);
        handler.endElement("", "history-entry", "history-entry");
        handler.endElement("", "issue-history", "issue-history");

        handler.endElement("", "issue", "issue");


        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }

    @Test
    public void testIssueAttachment() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        AttributesImpl issueAttrs = new AttributesImpl();
        issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "issue", "issue", issueAttrs);

        handler.startElement("", "issue-attachments", "issue-attachments", new AttributesImpl());
        handler.startElement("", "issue-attachment", "issue-attachment", new AttributesImpl());

        handler.startElement("", "issue-attachment-creator", "issue-attachment-creator", new AttributesImpl());
        handler.endElement("", "issue-attachment-creator", "issue-attachment-creator");
        handler.startElement("", "issue-attachment-description", "issue-attachment-description", new AttributesImpl());
        handler.endElement("", "issue-attachment-description", "issue-attachment-description");
        handler.startElement("", "issue-attachment-filename", "issue-attachment-filename", new AttributesImpl());
        handler.endElement("", "issue-attachment-filename", "issue-attachment-filename");
        handler.startElement("", "issue-attachment-filename", "issue-attachment-filename", new AttributesImpl());
        handler.endElement("", "issue-attachment-filename", "issue-attachment-filename");
        handler.startElement("", "issue-attachment-origfile", "issue-attachment-origfile", new AttributesImpl());
        handler.endElement("", "issue-attachment-origfile", "issue-attachment-origfile");
        handler.startElement("", "issue-attachment-type", "issue-attachment-type", new AttributesImpl());
        handler.endElement("", "issue-attachment-type", "issue-attachment-type");
        handler.startElement("", "issue-attachment-size", "issue-attachment-size", new AttributesImpl());
        handler.characters("200".toCharArray(), 0, 3);
        handler.endElement("", "issue-attachment-size", "issue-attachment-size");

        handler.endElement("", "issue-attachment", "issue-attachment");
        handler.endElement("", "issue-attachments", "issue-attachments");

        handler.endElement("", "issue", "issue");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }

    @Test
    public void testIssueComponentElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        AttributesImpl issueAttrs = new AttributesImpl();
        issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "issue", "issue", issueAttrs);

        handler.startElement("", "issue-components", "issue-components", new AttributesImpl());

        handler.startElement("", "component-id", "component-id", new AttributesImpl());
        handler.endElement("", "component-id", "component-id");

        handler.endElement("", "issue-components", "issue-components");

        handler.endElement("", "issue", "issue");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }

    @Test
    public void testIssueFieldElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        try {
            handler.startElement("", "issue-field", "issue-field", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "issue-field", "issue-field");

        } catch (SAXException e) {
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }


        handler = new ImportHandler();
        AttributesImpl fieldAttributes = new AttributesImpl();
        fieldAttributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");


        AttributesImpl projectAttrs = new AttributesImpl();
        projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        handler.startElement("", "configuration", "configuration", new AttributesImpl());

        handler.startElement("", "custom-fields", "custom-fields", null);


        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "", ImportHandler.ATTR_VALUE, "", "fieldoptionvalue");
        handler.startElement("", "custom-field", "custom-field", fieldAttributes);
        handler.startElement("", "custom-field-option", "custom-field-option", attrs);
        handler.endElement("", "custom-field-option", "custom-field-option");
        handler.endElement("", "custom-field", "custom-field");

        handler.endElement("", "custom-fields", "custom-fields");
        handler.endElement("", "configuration", "configuration");


        AttributesImpl issueAttrs = new AttributesImpl();
        issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "issue", "issue", issueAttrs);

        handler.startElement("", "issue-fields", "issue-fields", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_ID, "", "custom-field1");
        handler.startElement("", "issue-field", "issue-field", attributes);
        handler.endElement("", "issue-field", "issue-field");

        handler.endElement("", "issue-fields", "issue-fields");

        handler.endElement("", "issue", "issue");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);
    }

    @Test
    public void testVersionElement() throws Exception {

        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        try {
            handler.startElement("", "version", "version", new AttributesImpl());
            fail("should throw SAXException");
            handler.endElement("", "version", "version");
        } catch (SAXException e) {
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }

        handler = new ImportHandler();

        AttributesImpl projectAttrs = new AttributesImpl();
        projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        handler.startElement("", "project", "project", projectAttrs);

        handler.startElement("", "versions", "versions", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        attributes.addAttribute("", "", ImportHandler.ATTR_ID, "", "1");

        handler.startElement("", "version", "version", attributes);
        handler.startElement("", "version-description", "version-description", new AttributesImpl());
        handler.endElement("", "version-description", "version-description");
        handler.startElement("", "version-number", "version-number", new AttributesImpl());
        handler.endElement("", "version-number", "version-number");
        handler.startElement("", "version-id", "version-id", new AttributesImpl());
        handler.endElement("", "version-id", "version-id");


        handler.endElement("", "version", "version");

        handler.endElement("", "versions", "versions");

        handler.endElement("", "project", "project");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);
    }

    @Test
    public void testProjectFieldsElement() throws Exception {

        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        AttributesImpl projectAttrs = new AttributesImpl();
        projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        handler.startElement("", "project", "project", projectAttrs);

        handler.startElement("", "project-custom-fields", "project-custom-fields", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        attributes.addAttribute("", "", ImportHandler.ATTR_ID, "", "1");

        handler.startElement("", "project-custom-field", "project-custom-field", attributes);

        handler.endElement("", "project-custom-field", "project-custom-field");

        handler.endElement("", "project-custom-fields", "project-custom-fields");

        handler.endElement("", "project", "project");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }

    @Test
    public void testIssueVersionElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        AttributesImpl projectAttrs = new AttributesImpl();
        projectAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        handler.startElement("", "project", "project", projectAttrs);

        handler.startElement("", "versions", "versions", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");
        attributes.addAttribute("", "", ImportHandler.ATTR_ID, "", "1");

        handler.startElement("", "version", "version", attributes);

        handler.endElement("", "version", "version");

        handler.endElement("", "versions", "versions");

        handler.endElement("", "project", "project");


        AttributesImpl issueAttrs = new AttributesImpl();
        issueAttrs.addAttribute("", "", ImportHandler.ATTR_SYSTEMID, "", "1");

        handler.startElement("", "issue", "issue", issueAttrs);

        handler.startElement("", "issue-versions", "issue-versions", new AttributesImpl());

        handler.startElement("", "version-id", "version-id", new AttributesImpl());
        handler.endElement("", "version-id", "version-id");

        handler.endElement("", "issue-versions", "issue-versions");

        handler.endElement("", "issue", "issue");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }


    @Test
    public void testResolutionElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        handler.startElement("", "configuration", "configuration", new AttributesImpl());

        handler.startElement("", "resolutions", "resolutions", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "value", "", "resolutionValue");
        attributes.addAttribute("", "", "order", "", "1");

        handler.startElement("", "resolution", "resolution", attributes);
        handler.endElement("", "resolution", "resolution");

        handler.endElement("", "resolutions", "resolutions");

        handler.endElement("", "configuration", "configuration");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);
    }

    @Test
    public void testSeverityElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        handler.startElement("", "configuration", "configuration", new AttributesImpl());

        handler.startElement("", "severities", "severities", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "value", "", "severityValue");
        attributes.addAttribute("", "", "order", "", "1");

        handler.startElement("", "severity", "severity", attributes);
        handler.endElement("", "severity", "severity");

        handler.endElement("", "severities", "severities");

        handler.endElement("", "configuration", "configuration");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);
    }

    @Test
    public void testStatusElement() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        handler.startElement("", "configuration", "configuration", new AttributesImpl());

        handler.startElement("", "statuses", "statuses", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "value", "", "statusValue");
        attributes.addAttribute("", "", "order", "", "1");

        handler.startElement("", "status", "status", attributes);
        handler.endElement("", "status", "status");

        handler.endElement("", "statuses", "statuses");

        handler.endElement("", "configuration", "configuration");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);
    }

    @Test
    public void testNumberFormatExceptionThrowing() throws Exception {
        ImportHandler handler = new ImportHandler();
        assertNotNull("test not null importHandler", handler);

        handler.startElement("", "configuration", "configuration", new AttributesImpl());

        handler.startElement("", "statuses", "statuses", new AttributesImpl());

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "", "value", "", "statusValue");
        attributes.addAttribute("", "", "order", "", "test");

        try {
            handler.startElement("", "status", "status", attributes);
            fail("should throw SAXException");
            handler.endElement("", "status", "status");
        } catch (SAXException e) {
        } catch (Exception e) {
            fail("should throw SAXException not " + e);
        }

        handler.endElement("", "statuses", "statuses");

        handler.endElement("", "configuration", "configuration");

        AbstractEntity[] models = handler.getModels();

        assertNotNull(models);

    }
}
