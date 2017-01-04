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

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;


/**
 * This class provides functionality needed to parse an XML document to be imported into
 * an ITracker instance, into a set of data models.
 */
public class ImportHandler extends DefaultHandler implements ImportExportTags {

    private final Logger logger;
    private List<AbstractEntity> items;
    private StringBuffer tagBuffer;
    private SAXException endException;

    private AbstractEntity parentModel;
    private AbstractEntity childModel;
    private List<Object> itemList;
    private String tempStorage;

    public ImportHandler() {
        this.logger = Logger.getLogger(getClass());
        this.items = new ArrayList<>();
        this.endException = null;
    }

    public AbstractEntity[] getModels() {
        return items.toArray(new AbstractEntity[items.size()]);
    }

    @Override
    public void startDocument() {
        logger.debug("Started import xml parsing.");
    }

    @Override
    public void endDocument() {
        logger.debug("Completed import xml parsing.");
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) throws SAXException {
        logger.debug("Parsing import tag " + qName);

        if (endException != null) {
            throw endException;
        }


        tempStorage = "";
        try {
            if (TAG_COMPONENT.equals(qName)) {
                String id = atts.getValue(ATTR_SYSTEMID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for component.");
                }

                // FIXME: pass component name instead of import/export ID. 
                childModel = new Component((Project) parentModel, atts.getValue(ATTR_ID));
                childModel.setId(new Integer(id));
            } else if (TAG_COMPONENTS.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_CONFIGURATION.equals(qName)) {
                parentModel = new SystemConfiguration();
            } else if (TAG_CUSTOM_FIELD.equals(qName)) {
                String id = atts.getValue(ATTR_SYSTEMID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for issue.");
                }

                childModel = new CustomField();
                childModel.setId(new Integer(id));
            } else if (TAG_CUSTOM_FIELD_OPTION.equals(qName)) {
                tempStorage = ITrackerResources.unescapeUnicodeString(atts.getValue(ATTR_VALUE));
            } else if (TAG_CUSTOM_FIELDS.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_HISTORY_ENTRY.equals(qName)) {
                String creatorId = atts.getValue(ATTR_CREATOR_ID);
                String date = atts.getValue(ATTR_DATE);
                String status = atts.getValue(ATTR_STATUS);
                if (creatorId == null) {
                    throw new SAXException("Attribute " + ATTR_CREATOR_ID + " was null for issue history.");
                } else if (date == null) {
                    throw new SAXException("Attribute date was null for issue history.");
                }

                childModel = new IssueHistory();
                ((IssueHistory) childModel).setUser((User) findModel(creatorId));
                ((IssueHistory) childModel).setStatus(status != null && !status.equals("") ? Integer.parseInt(status) : IssueUtilities.HISTORY_STATUS_AVAILABLE);
                childModel.setCreateDate(getDateValue(date, qName));
                tagBuffer = new StringBuffer();
            } else if (TAG_ISSUE.equals(qName)) {
                String id = atts.getValue(ATTR_SYSTEMID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for issue.");
                }

                parentModel = new Issue();
                parentModel.setId(new Integer(id));
            } else if (TAG_ISSUE_ATTACHMENT.equals(qName)) {
                childModel = new IssueAttachment();
            } else if (TAG_ISSUE_ATTACHMENTS.equals(qName)) {
                itemList = new ArrayList<Object>();
            } else if (TAG_ISSUE_COMPONENTS.equals(qName)) {
                itemList = new ArrayList<Object>();
            } else if (TAG_ISSUE_FIELD.equals(qName)) {
                String id = atts.getValue(ATTR_ID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for issue field.");
                }
                childModel = new IssueField((Issue) parentModel, (CustomField) findModel(id));
            } else if (TAG_ISSUE_FIELDS.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_ISSUE_HISTORY.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_ISSUE_VERSIONS.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_PROJECT.equals(qName)) {
                String id = atts.getValue(ATTR_SYSTEMID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for project.");
                }

                parentModel = new Project();
                parentModel.setId(new Integer(id));
            } else if (TAG_PROJECT_FIELDS.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_PROJECT_OWNERS.equals(qName)) {
                itemList = new ArrayList<>();
            } else if (TAG_RESOLUTION.equals(qName)) {
                String value = atts.getValue(ATTR_VALUE);
                String order = atts.getValue(ATTR_ORDER);

                childModel = new Configuration(Configuration.Type.resolution, value, Integer.parseInt(order));
                tagBuffer = new StringBuffer();
            } else if (TAG_SEVERITY.equals(qName)) {
                String value = atts.getValue(ATTR_VALUE);
                String order = atts.getValue(ATTR_ORDER);

                childModel = new Configuration(Configuration.Type.severity, value, Integer.parseInt(order));
                tagBuffer = new StringBuffer();
            } else if (TAG_STATUS.equals(qName)) {
                String value = atts.getValue(ATTR_VALUE);
                String order = atts.getValue(ATTR_ORDER);

                childModel = new Configuration(Configuration.Type.status, value, Integer.parseInt(order));
                tagBuffer = new StringBuffer();
            } else if (TAG_USER.equals(qName)) {
                String id = atts.getValue(ATTR_SYSTEMID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for user.");
                }

                parentModel = new User();
                parentModel.setId(new Integer(id));
            } else if (TAG_VERSION.equals(qName)) {
                String id = atts.getValue(ATTR_SYSTEMID);
                if (id == null) {
                    throw new SAXException("Attribute " + ATTR_SYSTEMID + " was null for version.");
                }

                // FIXME: pass version number instead of import/export ID. 
                childModel = new Version((Project) parentModel, atts.getValue(ATTR_ID));
                childModel.setId(new Integer(id));
            } else if (TAG_VERSIONS.equals(qName)) {
                itemList = new ArrayList<>();
            } else {
                tagBuffer = new StringBuffer();
            }
        } catch (NumberFormatException nfe) {
            throw new SAXException("Attribute in " + qName + " did not contain a numeric value.");
        }
    }

    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
        logger.debug("Completing import tag " + qName);

        try {
            if (TAG_ISSUE.equals(qName) || TAG_PROJECT.equals(qName) || TAG_USER.equals(qName) || TAG_CONFIGURATION.equals(qName)) {
                items.add((AbstractEntity) parentModel.clone());
                parentModel = null;
                childModel = null;
                itemList = null;
            } else if (TAG_RESOLUTION.equals(qName) || TAG_SEVERITY.equals(qName) || TAG_STATUS.equals(qName)) {
                ((Configuration) childModel).setName(getBuffer());
                items.add((AbstractEntity) childModel.clone());
                ((SystemConfiguration) parentModel).addConfiguration((Configuration) childModel);
                childModel = null;
            } else if (TAG_COMPONENT.equals(qName) || TAG_VERSION.equals(qName) || TAG_CUSTOM_FIELD.equals(qName)) {
                // Add to both so we can search the the models for components, customfields, and versions later
                // Make sure they are double added when processed
                items.add((AbstractEntity) childModel.clone());
                itemList.add(childModel.clone());
                childModel = null;
            } else if (TAG_HISTORY_ENTRY.equals(qName)) {
                ((IssueHistory) childModel).setDescription(getBuffer());
                itemList.add(childModel.clone());
                childModel = null;
            } else if (TAG_ISSUE_ATTACHMENT.equals(qName)) {
                itemList.add(childModel.clone());
                childModel = null;
            } else if (TAG_ISSUE_FIELD.equals(qName)) {
                itemList.add(childModel.clone());
                childModel = null;
            } else if (TAG_COMPONENTS.equals(qName)) {
                List<Component> itemListArray = new ArrayList<>();
                for (Object anItemList : itemList) {
                    itemListArray.add((Component) anItemList);
                }
                ((Project) parentModel).setComponents(itemListArray);
            } else if (TAG_COMPONENT_DESCRIPTION.equals(qName)) {
                ((Component) childModel).setDescription(getBuffer());
            } else if (TAG_COMPONENT_ID.equals(qName)) {
                if (itemList == null) {
                    itemList = new ArrayList<>();
                }
                itemList.add(findModel(getBuffer()));
            } else if (TAG_COMPONENT_NAME.equals(qName)) {
                ((Component) childModel).setName(getBuffer());
            } else if (TAG_CONFIGURATION_VERSION.equals(qName)) {
                ((SystemConfiguration) parentModel).setVersion(getBuffer());
            } else if (TAG_CREATE_DATE.equals(qName)) {
                parentModel.setCreateDate(getDateValue(getBuffer(), qName));
            } else if (TAG_CREATOR.equals(qName)) {
                ((Issue) parentModel).setCreator((User) findModel(getBuffer()));
            } else if (TAG_CUSTOM_FIELDS.equals(qName)) {
                List<CustomField> itemListArray = new ArrayList<>();
                for (Object anItemList : itemList) {
                    itemListArray.add((CustomField) anItemList);
                }
                ((SystemConfiguration) parentModel).setCustomFields(itemListArray);
            } else if (TAG_CUSTOM_FIELD_DATEFORMAT.equals(qName)) {
                ((CustomField) childModel).setDateFormat(getBuffer());
            } else if (TAG_CUSTOM_FIELD_LABEL.equals(qName)) {
//                ((CustomField) childModel).setName(getBuffer());
//            	TODO handle configuration for setting the BASE label
            } else if (TAG_CUSTOM_FIELD_OPTION.equals(qName)) {
                ((CustomField) childModel).addOption(tempStorage, getBuffer());
            } else if (TAG_CUSTOM_FIELD_REQUIRED.equals(qName)) {
                ((CustomField) childModel).setRequired(("true".equalsIgnoreCase(getBuffer())));
            } else if (TAG_CUSTOM_FIELD_SORTOPTIONS.equals(qName)) {
                ((CustomField) childModel).setSortOptionsByName(("true".equalsIgnoreCase(getBuffer())));
            } else if (TAG_CUSTOM_FIELD_TYPE.equals(qName)) {
                final String s = getBuffer();
                try {
                    // fallback for old export type as int-code
                    int i = Integer.valueOf(s);
                    ((CustomField) childModel).setFieldType(CustomField.Type.valueOf(i));
                } catch (RuntimeException e) {
                    try {
                        ((CustomField) childModel).setFieldType(CustomField.Type.valueOf(s));
                    } catch (RuntimeException re) {
                        throw new SAXException("Could not convert string buffer to type value.");
                    }
                }
            } else if (TAG_EMAIL.equals(qName)) {
                ((User) parentModel).setEmail(getBuffer());
            } else if (TAG_FIRST_NAME.equals(qName)) {
                ((User) parentModel).setFirstName(getBuffer());
            } else if (TAG_ISSUE_ATTACHMENTS.equals(qName)) {
                List<IssueAttachment> itemListArray = new ArrayList<>();
                for (Object anItemList : itemList) {
                    itemListArray.add((IssueAttachment) anItemList);
                }
                ((Issue) parentModel).setAttachments(itemListArray);
            } else if (TAG_ISSUE_ATTACHMENT_CREATOR.equals(qName)) {
                ((IssueAttachment) childModel).setUser((User) findModel(getBuffer()));
            } else if (TAG_ISSUE_ATTACHMENT_DESCRIPTION.equals(qName)) {
                ((IssueAttachment) childModel).setDescription(getBuffer());
            } else if (TAG_ISSUE_ATTACHMENT_FILENAME.equals(qName)) {
                ((IssueAttachment) childModel).setFileName(getBuffer());
            } else if (TAG_ISSUE_ATTACHMENT_ORIGFILE.equals(qName)) {
                ((IssueAttachment) childModel).setOriginalFileName(getBuffer());
            } else if (TAG_ISSUE_ATTACHMENT_SIZE.equals(qName)) {
                ((IssueAttachment) childModel).setSize(getBufferAsLong());
            } else if (TAG_ISSUE_ATTACHMENT_TYPE.equals(qName)) {
                ((IssueAttachment) childModel).setType(getBuffer());
            } else if (TAG_ISSUE_COMPONENTS.equals(qName)) {
                List<Component> itemListArray = new ArrayList<>();
                for (Object anItemList : itemList) {
                    itemListArray.add((Component) anItemList);
                }
                ((Issue) parentModel).setComponents(itemListArray);
            } else if (TAG_ISSUE_DESCRIPTION.equals(qName)) {
                ((Issue) parentModel).setDescription(getBuffer());
            } else if (TAG_ISSUE_FIELDS.equals(qName)) {
                List<IssueField> itemListArray = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    itemListArray.add(i, (IssueField) itemList.get(i));
                }
                ((Issue) parentModel).setFields(itemListArray);
            } else if (TAG_ISSUE_HISTORY.equals(qName)) {
                List<IssueHistory> itemListArray = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    itemListArray.add(i, (IssueHistory) itemList.get(i));
                }
                ((Issue) parentModel).setHistory(itemListArray);
            } else if (TAG_ISSUE_PROJECT.equals(qName)) {
                ((Issue) parentModel).setProject((Project) findModel(getBuffer()));
            } else if (TAG_ISSUE_RESOLUTION.equals(qName)) {
                ((Issue) parentModel).setResolution(getBuffer());
            } else if (TAG_ISSUE_SEVERITY.equals(qName)) {
                ((Issue) parentModel).setSeverity(getBufferAsInt());
            } else if (TAG_ISSUE_STATUS.equals(qName)) {
                ((Issue) parentModel).setStatus(getBufferAsInt());
            } else if (TAG_ISSUE_VERSIONS.equals(qName)) {
                List<Version> itemListArray = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    itemListArray.add(i, (Version) itemList.get(i));
                }
                ((Issue) parentModel).setVersions(itemListArray);
            } else if (TAG_LAST_MODIFIED.equals(qName)) {
                parentModel.setLastModifiedDate(getDateValue(getBuffer(), qName));
            } else if (TAG_LAST_NAME.equals(qName)) {
                ((User) parentModel).setLastName(getBuffer());
            } else if (TAG_LOGIN.equals(qName)) {
                ((User) parentModel).setLogin(getBuffer());
            } else if (TAG_OWNER.equals(qName)) {
                ((Issue) parentModel).setOwner((User) findModel(getBuffer()));
            } else if (TAG_PROJECT_NAME.equals(qName)) {
                ((Project) parentModel).setName(getBuffer());
            } else if (TAG_PROJECT_DESCRIPTION.equals(qName)) {
                ((Project) parentModel).setDescription(getBuffer());
            } else if (TAG_PROJECT_FIELDS.equals(qName)) {
                List<CustomField> itemListArray = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    itemListArray.add(i, (CustomField) itemList.get(i));
                }
                ((Project) parentModel).setCustomFields(itemListArray);
            } else if (TAG_PROJECT_FIELD_ID.equals(qName)) {
                itemList.add(findModel(getBuffer()));
            } else if (TAG_PROJECT_OPTIONS.equals(qName)) {
                ((Project) parentModel).setOptions(getBufferAsInt());
            } else if (TAG_PROJECT_OWNERS.equals(qName)) {
                List<User> itemListArray = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    itemListArray.add(i, (User) itemList.get(i));
                }
                ((Project) parentModel).setOwners(itemListArray);
            } else if (TAG_PROJECT_OWNER_ID.equals(qName)) {
                itemList.add(findModel(getBuffer()));
            } else if (TAG_PROJECT_STATUS.equals(qName)) {
                // By default activate the project
                ((Project) parentModel).setStatus(Status.ACTIVE);
                try {
                    ((Project) parentModel).setStatus(Status.valueOf(getBuffer().toUpperCase()));
                } catch (RuntimeException re) {
                    // ok, it is active.
                }
            } else if (TAG_SUPER_USER.equals(qName)) {
                ((User) parentModel).setSuperUser(("true".equalsIgnoreCase(getBuffer())));
            } else if (TAG_TARGET_VERSION_ID.equals(qName)) {
                ((Issue) parentModel).setTargetVersion((Version) findModel(getBuffer()));
            } else if (TAG_USER_STATUS.equals(qName)) {
                // By default lock the user
                ((User) parentModel).setStatus(UserUtilities.STATUS_LOCKED);
                // TODO: Make status Enum
                String currBuffer = getBuffer();
                try {
                    ((User)parentModel).setStatus(Integer.parseInt(currBuffer));
                } catch (RuntimeException re) {
                    HashMap<String, String> userStatuses = UserUtilities.getStatusNames(EXPORT_LOCALE);
                    for (String key : userStatuses.keySet()) {
                        String keyValue = userStatuses.get(key);
                        if (keyValue != null && keyValue.equalsIgnoreCase(currBuffer)) {
                            ((User) parentModel).setStatus(Integer.parseInt(key));
                            break;
                        }
                    }
                }
            } else if (TAG_VERSIONS.equals(qName)) {
                List<Version> itemListArray = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    itemListArray.add(i, (Version) itemList.get(i));
                }
                ((Project) parentModel).setVersions(itemListArray);
            } else if (TAG_VERSION_DESCRIPTION.equals(qName)) {
                ((Version) childModel).setDescription(getBuffer());
            } else if (TAG_VERSION_ID.equals(qName)) {
                if (itemList == null) {
                    itemList = new ArrayList<>();
                }
                itemList.add(findModel(getBuffer()));
            } else if (TAG_VERSION_NUMBER.equals(qName)) {
                ((Version) childModel).setVersionInfo(getBuffer());
            }
        } catch (RuntimeException e) {
            logger.debug("endElement: RuntimeException importing data.", e);
            endException = new SAXException("Error processing tag " + qName + ": " + e.getMessage());
            throw endException;
        } catch (SAXException e) {
            logger.debug("endElement: SAXException importing data.", e);
            throw e;
        } catch (CloneNotSupportedException e) {
            logger.debug("endElement: CloneNotSupportedException importing data.", e);
            endException = new SAXException("Error processing tag " + qName + ": " + e.getMessage());
            throw endException;
        }
        tagBuffer = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        logger.debug("Read " + ch.length + " Start: " + start + " Length: " + length);
        logger.debug("String: " + new String(ch, start, length));
        if (tagBuffer != null) {
            tagBuffer.append(ITrackerResources.unescapeUnicodeString(new String(ch, start, length)));
        }
    }

    private String getBuffer() {
        if (tagBuffer == null) {
            return "";
        } else {
            return tagBuffer.toString();
        }
    }

    private int getBufferAsInt() throws SAXException {
        if (tagBuffer == null) {
            return -1;
        } else {
            try {
                return Integer.parseInt(tagBuffer.toString());
            } catch (NumberFormatException nfe) {
                throw new SAXException("Could not convert string buffer to int value.");
            }
        }
    }

    private long getBufferAsLong() throws SAXException {
        if (tagBuffer == null) {
            return -1;
        } else {
            try {
                return Long.parseLong(tagBuffer.toString());
            } catch (NumberFormatException nfe) {
                throw new SAXException("Could not convert string buffer to long value.");
            }
        }
    }

    private AbstractEntity findModel(String itemTypeId) {
        if (itemTypeId != null && !itemTypeId.equals("")) {
            for (AbstractEntity model : items) {
                if (getModelTypeIdString(model).equalsIgnoreCase(itemTypeId)) {
                    return model;
                }
            }
        }
        logger.debug("Unable to find model id " + itemTypeId + " during import.");
        return null;
    }

    private String getModelTypeIdString(AbstractEntity model) {
        String idString = "UNKNOWN";

        if (model != null && model.getId() != null) {
            String type = "";
            if (model instanceof Component) {
                type = TAG_COMPONENT;
            } else if (model instanceof CustomField) {
                type = TAG_CUSTOM_FIELD;
            } else if (model instanceof Issue) {
                type = TAG_ISSUE;
            } else if (model instanceof Project) {
                type = TAG_PROJECT;
            } else if (model instanceof User) {
                type = TAG_USER;
            } else if (model instanceof Version) {
                type = TAG_VERSION;
            }

            idString = type + model.getId();
        }

        return idString;
    }

    private Date getDateValue(String dateString, String qName) throws SAXException {
        if (dateString == null || "".equals(dateString)) {
            return new Date();
        }

        try {
            return DATE_FORMATTER.parse(dateString);
        } catch (Exception e) {
            throw new SAXException("Value in " + qName + " did not contain a valid date value.");
        }
    }
}
