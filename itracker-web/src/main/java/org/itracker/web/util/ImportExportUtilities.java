/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.web.util;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.itracker.ImportExportException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * FIXME: This is not XML, this is string concatenating/parsing. Use proper SAX Handler or remove this unsave code. see java.xml.parsers for more information.
 * <p/>
 * This class provides functionality needed to import and export issues and their associated
 * data as XML.  This xml provides all the data necessary to import the issues into another
 * instance of ITracker or some other issue tracking tool.
 */
public class ImportExportUtilities implements ImportExportTags {

    private static final Logger logger = LoggerFactory.getLogger(ImportExportUtilities.class);
    public static final int IMPORT_STAT_NEW = 0;
    public static final int IMPORT_STAT_REUSED = 1;

    public static final int IMPORT_STAT_USERS = 0;
    public static final int IMPORT_STAT_PROJECTS = 1;
    public static final int IMPORT_STAT_ISSUES = 2;
    public static final int IMPORT_STAT_STATUSES = 3;
    public static final int IMPORT_STAT_SEVERITIES = 4;
    public static final int IMPORT_STAT_RESOLUTIONS = 5;
    public static final int IMPORT_STAT_FIELDS = 6;

    public ImportExportUtilities() {
    }


    private static DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }
    /**
     * Takes an XML file matching the ITracker import/export DTD and returns an array
     * of AbstractBean objects.  The array will contain all of the projects, components
     * versions, users, custom fields, and issues contained in the XML.
     *
     * @param xmlReader an xml reader to import
     * @throws ImportExportException thrown if the xml can not be parsed into the appropriate objects
     */
    public static AbstractEntity[] importIssues(Reader xmlReader) throws ImportExportException {
        AbstractEntity[] abstractBeans;

        try {
            logger.debug("Starting XML data import.");

            XMLReader reader = XMLReaderFactory.createXMLReader();
            ImportHandler handler = new ImportHandler();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(new InputSource(xmlReader));
            abstractBeans = handler.getModels();

            logger.debug("Imported a total of " + abstractBeans.length + " beans.");
        } catch (Exception e) {
            logger.error("Exception.", e);
            throw new ImportExportException(e.getMessage());
        }

        return abstractBeans;
    }


    /**
     * export the issues to an XML and write it to the response.
     * @param issues
     * @param config
     * @param request
     * @param response
     * @return  if <code>true</code> the export was sucessful.
     * @throws ServletException
     * @throws IOException
     */
    public static boolean exportIssues(List<Issue> issues, SystemConfiguration config, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        response.setContentType("text/xml; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"issue_export.xml\"");

        XMLWriter writer = new XMLWriter(response.getOutputStream(), OutputFormat.createCompactFormat());

        try {
            // TODO instead to have a string returned, it should directly serialize the
            // export to the response-writer.
            ImportExportUtilities.exportIssues(writer, issues, config);


        } catch (ImportExportException iee) {
            logger.error("Error exporting issue data. Message: " + iee.getMessage(), iee);
            return false;
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }

        return true;
    }

    public static AbstractEntity importXml(InputSource is) throws Exception {
        // TODO unmarshal from is
        JAXBContext jc = JAXBContext.newInstance("org.itracker");
        Unmarshaller u = jc.createUnmarshaller();
        AbstractEntity o = (AbstractEntity) u.unmarshal(is);
        return o;
    }

    public static void export(AbstractEntity o, OutputStream os) throws Exception {
        // TODO marshal to System.out
        JAXBContext jc = JAXBContext.newInstance("org.itracker");
        Marshaller m = jc.createMarshaller();
        m.marshal(o, System.out);
    }

    public static void exportIssues(XMLWriter writer, List<Issue> issues, SystemConfiguration config) throws ImportExportException {
        Element elRoot = getDocumentFactory().createElement(TAG_ROOT);
        try {
            writer.startDocument();
            writer.writeOpen(elRoot);

            exportConfigModels(writer, config);
            exportIssuesModels(writer, issues);

            writer.writeClose(elRoot);
            writer.endDocument();
        } catch (SAXException e) {
            throw new ImportExportException(e.getMessage(), ImportExportException.TYPE_UNKNOWN);
        } catch (IOException e) {
            throw new ImportExportException("Problem writing export stream. "
                    + e.getMessage(), ImportExportException.TYPE_UNKNOWN);
        }
    }

    public static void exportConfigModels(XMLWriter writer, SystemConfiguration config) throws IOException {

        if (config != null) {

            Element elConfigs = getDocumentFactory().createElement(TAG_CONFIGURATION);
            writer.writeOpen(elConfigs);
            getConfigurationXML(writer, config);
            writer.writeClose(elConfigs);
        }
    }
    public static void exportIssuesModels(XMLWriter writer, List<Issue> issues) throws IOException {
        HashMap<String, Project> projects = new HashMap<>();
        HashMap<String, User> users = new HashMap<>();

        if (issues == null || issues.size() == 0) {
            throw new IllegalArgumentException("The issue list was null or zero length.");
        }

        // initialize dataset
        for (Issue issue : issues) {
            if (!projects.containsKey(issue.getProject().getId().toString())) {
                if (logger.isDebugEnabled())
                logger.debug("Adding new project " + issue.getProject().getId() + " to export.");
                projects.put(issue.getProject().getId().toString(), issue.getProject());
            }

            if (issue.getCreator() != null && !users.containsKey(issue.getCreator().getId().toString())) {
                if (logger.isDebugEnabled())
                logger.debug("Adding new user " + issue.getCreator().getId() + " to export.");
                users.put(issue.getCreator().getId().toString(), issue.getCreator());
            }
            if (issue.getOwner() != null && !users.containsKey(issue.getOwner().getId().toString())) {
                if (logger.isDebugEnabled())
                logger.debug("Adding new user " + issue.getOwner().getId() + " to export.");
                users.put(issue.getOwner().getId().toString(), issue.getOwner());
            }

            List<IssueHistory> history = issue.getHistory();
            for (IssueHistory aHistory : history) {
                if (aHistory != null && aHistory.getUser() != null && !users.containsKey(aHistory.getUser().getId().toString())) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding new user " + aHistory.getUser().getId() + " to export.");
                    users.put(aHistory.getUser().getId().toString(), aHistory.getUser());
                }
            }

            List<IssueAttachment> attachments = issue.getAttachments();
            for (IssueAttachment attachment : attachments) {
                if (attachment != null && attachment.getUser() != null && !users.containsKey(attachment.getUser().getId().toString())) {
                    if (logger.isDebugEnabled())
                        logger.debug("Adding new user " + attachment.getUser().getId() + " to export.");
                    users.put(attachment.getUser().getId().toString(), attachment.getUser());
                }
            }
        }


        for (String s : projects.keySet()) {
            Project project = projects.get(s);
            for (User o:project.getOwners()) {
                users.put(o.getId().toString(), o);
            }
        }

        Element elUsers = getDocumentFactory().createElement(TAG_USERS);
        writer.writeOpen(elUsers);
        for (User u: users.values()) {
            exportModel(writer, u);
        }
        writer.writeClose(elUsers);

        Element elProjects = getDocumentFactory().createElement(TAG_PROJECTS);
        writer.writeOpen(elProjects);
        for (Project s : projects.values()) {
            exportModel(writer, s);
        }
        writer.writeClose(elProjects);

        Element elIssues = getDocumentFactory().createElement(TAG_ISSUES);
        writer.writeOpen(elIssues);
        for (Issue issue: issues) {
            exportModel(writer, issue);
        }
        writer.writeClose(elIssues);

    }
    /**
     * Takes an array of IssueModels and exports them as XML suitable for import into another
     * instance of ITracker, or another issue tracking tool.
     *
     * @param issues an array of Issue objects to export
     * @throws ImportExportException thrown if the array of issues can not be exported
     */
    public static String exportIssues(List<Issue> issues, SystemConfiguration config) throws ImportExportException {
        StringBuffer buf = new StringBuffer();
        HashMap<String, Project> projects = new HashMap<String, Project>();
        HashMap<String, User> users = new HashMap<String, User>();

        if (issues == null || issues.size() == 0) {
            throw new ImportExportException("The issue list was null or zero length.");
        }
        buf.append("<" + TAG_ISSUES + ">\n");
        for (int i = 0; i < issues.size(); i++) {
            if (!projects.containsKey(issues.get(i).getProject().getId().toString())) {
                logger.debug("Adding new project " + issues.get(i).getProject().getId() + " to export.");
                projects.put(issues.get(i).getProject().getId().toString(), issues.get(i).getProject());
            }

            if (issues.get(i).getCreator() != null && !users.containsKey(issues.get(i).getCreator().getId().toString())) {
                logger.debug("Adding new user " + issues.get(i).getCreator().getId() + " to export.");
                users.put(issues.get(i).getCreator().getId().toString(), issues.get(i).getCreator());
            }
            if (issues.get(i).getOwner() != null && !users.containsKey(issues.get(i).getOwner().getId().toString())) {
                logger.debug("Adding new user " + issues.get(i).getOwner().getId() + " to export.");
                users.put(issues.get(i).getOwner().getId().toString(), issues.get(i).getOwner());
            }

            List<IssueHistory> history = issues.get(i).getHistory();
            for (int j = 0; j < history.size(); j++) {
                if (history.get(j) != null && history.get(j).getUser() != null && !users.containsKey(history.get(j).getUser().getId().toString())) {
                    logger.debug("Adding new user " + history.get(j).getUser().getId() + " to export.");
                    users.put(history.get(j).getUser().getId().toString(), history.get(j).getUser());
                }
            }

            List<IssueAttachment> attachments = issues.get(i).getAttachments();
            for (int j = 0; j < attachments.size(); j++) {
                if (attachments.get(j) != null && attachments.get(j).getUser() != null && !users.containsKey(attachments.get(j).getUser().getId().toString())) {
                    logger.debug("Adding new user " + attachments.get(j).getUser().getId() + " to export.");
                    users.put(attachments.get(j).getUser().getId().toString(), attachments.get(j).getUser());
                }
            }

            buf.append(exportModel((AbstractEntity) issues.get(i)));
        }
        buf.append("</" + TAG_ISSUES + ">\n");
        buf.append("</" + TAG_ROOT + ">\n");


        buf.insert(0, "</" + TAG_PROJECTS + ">\n");
        for (Iterator<String> iter = projects.keySet().iterator(); iter.hasNext(); ) {
            Project project = (Project) projects.get((String) iter.next());
            for (int i = 0; i < project.getOwners().size(); i++) {
                users.put(project.getOwners().get(i).getId().toString(), project.getOwners().get(i));
            }
            buf.insert(0, exportModel((AbstractEntity) project));
        }
        buf.insert(0, "<" + TAG_PROJECTS + ">\n");

        buf.insert(0, "</" + TAG_USERS + ">\n");
        for (Iterator<String> iter = users.keySet().iterator(); iter.hasNext(); ) {
            buf.insert(0, exportModel((AbstractEntity) users.get((String) iter.next())));
        }
        buf.insert(0, "<" + TAG_USERS + ">\n");

        if (config != null) {
            buf.insert(0, "</" + TAG_CONFIGURATION + ">\n");
            buf.insert(0, getConfigurationXML(config));
            buf.insert(0, "<" + TAG_CONFIGURATION + ">\n");
        }

        buf.insert(0, "<" + TAG_ROOT + ">\n");

        return buf.toString();
    }


    /**
     * Returns the appropriate XML block for a given model.
     *
     * @param abstractBean a model that extends AbstractEntity
     * @throws ImportExportException thrown if the given model can not be exported
     */
    public static String exportModel(AbstractEntity abstractBean) throws ImportExportException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLWriter writer = new XMLWriter(os);
            exportModel(writer, abstractBean);
            writer.close();
            return (os.toString("utf-8"));
        } catch (Exception e) {
            logger.error("could not create xml string", e);
            throw new ImportExportException(e.getMessage(), ImportExportException.TYPE_UNKNOWN);
        }

    }
    public static void exportModel(XMLWriter writer, AbstractEntity abstractBean) throws IOException {
        if (abstractBean == null) {
            throw new IllegalArgumentException("The bean to export was null.");
        } else if (abstractBean instanceof Issue) {
            getIssueXML(writer, (Issue) abstractBean);
        } else if (abstractBean instanceof Project) {
            getProjectXML(writer, (Project) abstractBean);
        } else if (abstractBean instanceof User) {
            getUserXML(writer, (User) abstractBean);
        } else {
            throw new IllegalArgumentException("This bean type can not be exported.");
        }
    }

    /**
     * Write the properties to simple XML tags
     * @param writer
     * @param tags
     * @throws IOException
     */
    private static void addPropertyTags(XMLWriter writer, Properties tags) throws IOException {
        DocumentFactory factory = getDocumentFactory();
        for (String tag: tags.stringPropertyNames()) {
            Element el = factory.createElement(tag);
            el.setText(tags.getProperty(tag));
            writer.write(el);
        }
    }

    /**
     * Write the properties to simple CDATA tags
     * @param writer
     * @param tags
     * @throws IOException
     */
    private static void addCdataPropertyTags(XMLWriter writer, Properties tags) throws IOException {
        DocumentFactory factory = getDocumentFactory();
        for (String tag: tags.stringPropertyNames()) {
            Element el = factory.createElement(tag);
            el.add(factory.createCDATA(tags.getProperty(tag)));
            writer.write(el);
        }
    }

    private static void addIdCollection(XMLWriter writer, List<? extends AbstractEntity> entities,
                                        String elName, String itName, String idPrefix) throws IOException {
        if (entities.size() > 0) {
            Element elTmp;
            final DocumentFactory factory = getDocumentFactory();
            final Element el = factory.createElement(elName);
            writer.writeOpen(el);
            for (AbstractEntity c: entities) {
                elTmp = factory.createElement(itName);
                elTmp.setText(idPrefix + c.getId());
                writer.write(elTmp);
            }
            writer.writeClose(el);
        }
    }
    private static void addIssueFields(XMLWriter writer, List<IssueField> entities) throws IOException {
        if (entities.size() > 0) {
            Element elTmp;
            final DocumentFactory factory = getDocumentFactory();
            final Element el = factory.createElement(TAG_ISSUE_FIELDS);
            writer.writeOpen(el);
            for (IssueField c: entities) {
                elTmp = factory.createElement(TAG_ISSUE_FIELD);
                elTmp.addAttribute(ATTR_ID, TAG_CUSTOM_FIELD + c.getId());
                elTmp.add(factory.createCDATA(c.getValue(EXPORT_LOCALE)));
                writer.write(elTmp);
            }
            writer.writeClose(el);
        }
    }

    private static void addIssueAttachments(XMLWriter writer, List<IssueAttachment> entities) throws IOException {
        if (entities.size() > 0) {
            Element elTmp;
            final DocumentFactory factory = getDocumentFactory();
            final Element el = factory.createElement(TAG_ISSUE_ATTACHMENTS);
            writer.writeOpen(el);
            for (IssueAttachment c: entities) {
                elTmp = factory.createElement(TAG_ISSUE_ATTACHMENT);
                writer.writeOpen(elTmp);
                Properties pTmp = new Properties();
                pTmp.setProperty(TAG_ISSUE_ATTACHMENT_DESCRIPTION, ITrackerResources.escapeUnicodeString(c.getDescription(), false));
                pTmp.setProperty(TAG_ISSUE_ATTACHMENT_FILENAME, ITrackerResources.escapeUnicodeString(c.getFileName(), false));
                pTmp.setProperty(TAG_ISSUE_ATTACHMENT_ORIGFILE, ITrackerResources.escapeUnicodeString(c.getOriginalFileName(), false));
                addCdataPropertyTags(writer, pTmp);
                pTmp.clear();

                pTmp.setProperty(TAG_ISSUE_ATTACHMENT_SIZE, String.valueOf(c.getSize()));
                pTmp.setProperty(TAG_ISSUE_ATTACHMENT_TYPE, StringUtils.defaultString(c.getType(), "application/octet-stream"));
                pTmp.setProperty(TAG_ISSUE_ATTACHMENT_CREATOR, TAG_USER + c.getUser().getId());
                addPropertyTags(writer, pTmp);
                writer.writeClose(elTmp);
            }
            writer.writeClose(el);
        }
    }

    private static void addIssueHistory(XMLWriter writer, List<IssueHistory> entities) throws IOException {
        if (entities.size() > 0) {
            Element elTmp;
            final DocumentFactory factory = getDocumentFactory();
            final Element el = factory.createElement(TAG_ISSUE_HISTORY);
            writer.writeOpen(el);
            for (IssueHistory c: entities) {
                elTmp = factory.createElement(TAG_HISTORY_ENTRY);
                elTmp.addAttribute(ATTR_CREATOR_ID, TAG_USER + c.getUser().getId());
                elTmp.addAttribute(ATTR_DATE, DATE_FORMATTER.format(c.getCreateDate()));
                elTmp.addAttribute(ATTR_STATUS, String.valueOf(c.getStatus()));

                writer.writeOpen(elTmp);
                writer.write(factory.createCDATA(ITrackerResources.escapeUnicodeString(c.getDescription(), false)));
                writer.writeClose(elTmp);
            }
            writer.writeClose(el);
        }
    }

    public static void getIssueXML(XMLWriter writer, Issue issue) throws IOException {
        Element elIssue = getDocumentFactory().createElement(TAG_ISSUE);
        elIssue.addAttribute(ATTR_ID, TAG_ISSUE+issue.getId());
        elIssue.addAttribute(ATTR_SYSTEMID, String.valueOf(issue.getId()));

        writer.writeOpen(elIssue);

        final Properties tags = new Properties();
        final Properties ctags = new Properties();

        tags.setProperty(TAG_ISSUE_PROJECT, TAG_PROJECT + issue.getProject().getId());
        ctags.setProperty(TAG_ISSUE_DESCRIPTION, ITrackerResources.escapeUnicodeString(issue.getDescription(), false));
        tags.setProperty(TAG_ISSUE_SEVERITY, String.valueOf(issue.getSeverity()));
        tags.setProperty(TAG_ISSUE_STATUS, String.valueOf(issue.getStatus()));
        tags.setProperty(TAG_ISSUE_RESOLUTION, StringUtils.defaultString(issue.getResolution()));
        if (issue.getTargetVersion() != null) {
            tags.setProperty(TAG_TARGET_VERSION_ID, TAG_VERSION + issue.getTargetVersion().getId());
        }
        tags.setProperty(TAG_CREATE_DATE, DATE_FORMATTER.format(issue.getCreateDate()));
        tags.setProperty(TAG_LAST_MODIFIED, DATE_FORMATTER.format(issue.getLastModifiedDate()));
        tags.setProperty(TAG_CREATOR, TAG_USER + issue.getCreator().getId());
        if (issue.getOwner() != null) {
            tags.setProperty(TAG_OWNER, TAG_USER + issue.getOwner().getId());
        }

        addPropertyTags(writer, tags);
        addCdataPropertyTags(writer, ctags);

        addIdCollection(writer, issue.getComponents(), TAG_ISSUE_COMPONENTS, TAG_COMPONENT_ID, TAG_COMPONENT);
        addIdCollection(writer, issue.getVersions(), TAG_ISSUE_VERSIONS, TAG_VERSION_ID, TAG_VERSION);

        addIssueFields(writer, issue.getFields());
        addIssueAttachments(writer, issue.getAttachments());
        addIssueHistory(writer, issue.getHistory());

        writer.writeClose(elIssue);
    }
    /**
     * Generates an XML block that encapsulates an issue for import or export.  This
     * function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param issue an Issue to generate the XML for
     * @return a String containing the XML for the issue
     */
    public static String getIssueXML(Issue issue) {
        if (issue == null) {
            return "";
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLWriter writer = new XMLWriter(os);
            getIssueXML(writer, issue);
            writer.close();
            return (os.toString("utf-8"));
        } catch (Exception e) {
            logger.error("could not create xml string", e);
            return "";
        }
    }


    private static void addProjectComponents(XMLWriter writer, List<Component> entities) throws IOException {
        if (entities.size() > 0) {
            Element elTmp;
            final DocumentFactory factory = getDocumentFactory();
            final Element el = factory.createElement(TAG_COMPONENTS);
            writer.writeOpen(el);
            for (Component c : entities) {
                elTmp = factory.createElement(TAG_COMPONENT);

                elTmp.addAttribute(ATTR_ID, TAG_CUSTOM_FIELD + c.getId());
                elTmp.addAttribute(ATTR_SYSTEMID, String.valueOf(c.getId()));

                writer.writeOpen(elTmp);
                Properties tags = new Properties();
                tags.setProperty(TAG_COMPONENT_NAME, ITrackerResources.escapeUnicodeString(c.getName(), false));
                tags.setProperty(TAG_COMPONENT_DESCRIPTION, ITrackerResources.escapeUnicodeString(c.getDescription(), false));
                addCdataPropertyTags(writer, tags);
                writer.writeClose(elTmp);
            }
            writer.writeClose(el);
        }
    }

    private static void addProjectVersions(XMLWriter writer, List<Version> entities) throws IOException {
        if (entities.size() > 0) {
            Element elTmp;
            final DocumentFactory factory = getDocumentFactory();
            final Element el = factory.createElement(TAG_VERSIONS);
            writer.writeOpen(el);
            for (Version c : entities) {
                elTmp = factory.createElement(TAG_VERSION);

                elTmp.addAttribute(ATTR_ID, TAG_VERSION + c.getId());
                elTmp.addAttribute(ATTR_SYSTEMID, String.valueOf(c.getId()));

                writer.writeOpen(elTmp);
                Properties tags = new Properties();
                tags.setProperty(TAG_VERSION_NUMBER, ITrackerResources.escapeUnicodeString(c.getNumber(), false));
                tags.setProperty(TAG_VERSION_DESCRIPTION, ITrackerResources.escapeUnicodeString(c.getDescription(), false));
                addCdataPropertyTags(writer, tags);
                writer.writeClose(elTmp);
            }
            writer.writeClose(el);
        }
    }
    private static void getProjectXML(XMLWriter writer, Project project) throws IOException {
        Element elProject = getDocumentFactory().createElement(TAG_PROJECT);
        elProject.addAttribute(ATTR_ID, TAG_PROJECT + project.getId());
        elProject.addAttribute(ATTR_SYSTEMID, String.valueOf(project.getId()));

        writer.writeOpen(elProject);
        Properties tags = new Properties();
        tags.setProperty(TAG_PROJECT_NAME, ITrackerResources.escapeUnicodeString(project.getName(), false));
        tags.setProperty(TAG_PROJECT_DESCRIPTION, ITrackerResources.escapeUnicodeString(project.getDescription(), false));
        addCdataPropertyTags(writer, tags);
        tags.clear();
        tags.setProperty(TAG_PROJECT_STATUS, ProjectUtilities.getStatusName(project.getStatus(), EXPORT_LOCALE));
        tags.setProperty(TAG_PROJECT_OPTIONS, String.valueOf(project.getOptions()));
        addPropertyTags(writer, tags);

        addIdCollection(writer, project.getCustomFields(), TAG_PROJECT_FIELDS, TAG_PROJECT_FIELD_ID, TAG_CUSTOM_FIELD);
        addIdCollection(writer, project.getOwners(), TAG_PROJECT_OWNERS, TAG_PROJECT_OWNER_ID, TAG_USER);

        addProjectComponents(writer, project.getComponents());
        addProjectVersions(writer, project.getVersions());

        writer.writeClose(elProject);

    }

    /**
     * Generates an XML block that encapsulates a project for import or export.  This
     * function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param project a Project to generate the XML for
     * @return a String containing the XML for the project
     */
    public static String getProjectXML(Project project) {
        if (project == null) {
            return "";
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLWriter writer = new XMLWriter(os);
            getProjectXML(writer, project);
            writer.close();
            return (os.toString("utf-8"));
        } catch (Exception e) {
            logger.error("could not create xml string", e);
            return "";
        }
    }

    public static void getUserXML(XMLWriter writer, User user) throws IOException {
        Element elUser = getDocumentFactory().createElement(TAG_USER);
        elUser.addAttribute(ATTR_ID, TAG_USER + user.getId());
        elUser.addAttribute(ATTR_SYSTEMID, String.valueOf(user.getId()));

        writer.writeOpen(elUser);
        Properties tags = new Properties();
        tags.setProperty(TAG_LOGIN, ITrackerResources.escapeUnicodeString(user.getLogin(), false));
        tags.setProperty(TAG_FIRST_NAME, ITrackerResources.escapeUnicodeString(user.getFirstName(), false));
        tags.setProperty(TAG_LAST_NAME, ITrackerResources.escapeUnicodeString(user.getLastName(), false));
        tags.setProperty(TAG_EMAIL, ITrackerResources.escapeUnicodeString(user.getEmail(), false));
        addCdataPropertyTags(writer, tags);
        tags.clear();

        tags.setProperty(TAG_USER_STATUS, String.valueOf(user.getStatus()));
        tags.setProperty(TAG_SUPER_USER, String.valueOf(user.isSuperUser()));
        addPropertyTags(writer, tags);

        writer.writeClose(elUser);
    }
    /**
     * Generates an XML block that encapsulates a user for import or export.  This
     * function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param user a User to generate the XML for
     * @return a String containing the XML for the user
     */
    public static String getUserXML(User user) {
        if (user == null) {
            return "";
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLWriter writer = new XMLWriter(os);
            getUserXML(writer, user);
            writer.close();
            return (os.toString("utf-8"));
        } catch (Exception e) {
            logger.error("could not create xml string", e);
            return "";
        }

    }


    public static void getConfigurationXML(XMLWriter writer, SystemConfiguration config) throws IOException {
        Properties tags = new Properties();
        tags.setProperty(TAG_CONFIGURATION_VERSION, StringUtils.defaultString(config.getVersion(), "null"));
        addCdataPropertyTags(writer, tags);

        getCustomFieldsXML(writer, config);

        getConfigurationXML(writer, config.getResolutions(), TAG_RESOLUTIONS, TAG_RESOLUTION);
        getConfigurationXML(writer, config.getSeverities(), TAG_SEVERITIES, TAG_SEVERITY);
        getConfigurationXML(writer, config.getStatuses(), TAG_STATUSES, TAG_STATUS);

    }

    private static void getConfigurationXML(XMLWriter writer, List<Configuration> cs, String elName, String itName) throws IOException {
        Element el = getDocumentFactory().createElement(elName);
        writer.writeOpen(el);
        for (Configuration c: cs) {
            Element elIt = getDocumentFactory().createElement(itName);
            elIt.addAttribute(ATTR_VALUE, c.getValue());
            elIt.addAttribute(ATTR_ORDER, String.valueOf(c.getOrder()));
            writer.writeOpen(elIt);
            writer.write(getDocumentFactory().createCDATA(ITrackerResources.escapeUnicodeString(c.getName(), false)));
            writer.writeClose(elIt);
        }
        writer.writeClose(el);
    }

    private static void getCustomFieldsXML(XMLWriter writer, SystemConfiguration config) throws IOException {
        Properties tags = new Properties();
        Element elCustomField = getDocumentFactory().createElement(TAG_CUSTOM_FIELDS);
        Element elTmp;
        writer.writeOpen(elCustomField);
        for (CustomField c: config.getCustomFields()) {
            tags.clear();
            tags.setProperty(TAG_CUSTOM_FIELD_LABEL, ITrackerResources.escapeUnicodeString(CustomFieldUtilities.getCustomFieldName(c.getId()), false));
            tags.setProperty(TAG_CUSTOM_FIELD_TYPE,  String.valueOf(c.getFieldType().name()));
            tags.setProperty(TAG_CUSTOM_FIELD_REQUIRED, String.valueOf(c.isRequired()));
            tags.setProperty(TAG_CUSTOM_FIELD_DATEFORMAT, ITrackerResources.escapeUnicodeString(c.getDateFormat(), false));
            tags.setProperty(TAG_CUSTOM_FIELD_SORTOPTIONS, String.valueOf(c.isSortOptionsByName()));
            tags.setProperty(TAG_CUSTOM_FIELD_LABEL, ITrackerResources.escapeUnicodeString(CustomFieldUtilities.getCustomFieldName(c.getId()), false));
            elTmp = getDocumentFactory().createElement(TAG_CUSTOM_FIELD);

            elTmp.addAttribute(ATTR_ID, TAG_CUSTOM_FIELD + c.getId());
            elTmp.addAttribute(ATTR_SYSTEMID, String.valueOf(c.getId()));
            writer.writeOpen(elTmp);
            addCdataPropertyTags(writer, tags);
            if (c.getFieldType() == CustomField.Type.LIST) {
                Element elOption;
                for (CustomFieldValue o: c.getOptions()) {
                    elOption = getDocumentFactory().createElement(TAG_CUSTOM_FIELD_OPTION);
                    elOption.addAttribute(ATTR_VALUE, ITrackerResources.escapeUnicodeString(o.getValue(), false));
                    elOption.add(getDocumentFactory().createCDATA(ITrackerResources.escapeUnicodeString(CustomFieldUtilities.getCustomFieldOptionName(o, null), false)));
                    writer.write(elOption);
                }
            }
            writer.writeClose(elTmp);
        }


        writer.writeClose(elCustomField);
    }

    /**
     * Generates an XML block that encapsulates the system configuration for import or export.
     * This function will not generate the XML for other models needed for a complete import
     * or export.
     *
     * @param config a SystemConfiguration to generate the XML for
     * @return a String containing the XML for the configuration
     */
    public static String getConfigurationXML(SystemConfiguration config) {
        if (config == null) {
            return "";
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            XMLWriter writer = new XMLWriter(os);
            getConfigurationXML(writer, config);
            writer.close();
            return (os.toString("utf-8"));
        } catch (Exception e) {
            logger.error("could not create xml string", e);
            return "";
        }
    }
}
