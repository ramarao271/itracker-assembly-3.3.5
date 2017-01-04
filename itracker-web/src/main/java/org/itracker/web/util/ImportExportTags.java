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

import org.itracker.core.resources.ITrackerResources;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * This interface defines the tags used in the export XML.
 */
public interface ImportExportTags {
    SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    String EXPORT_LOCALE_STRING = ITrackerResources.BASE_LOCALE;
    Locale EXPORT_LOCALE = ITrackerResources.getLocale(EXPORT_LOCALE_STRING);

    String ATTR_CREATOR_ID = "creator-id";
    String ATTR_BIT = "bit";
    String ATTR_DATE = "date";
    String ATTR_ID = "id";
    String ATTR_NAME = "name";
    String ATTR_ORDER = "order";
    String ATTR_STATUS = "status";
    /**
     * TODO
     *
     * @deprecated must use speaking ids, systemid will be generated instead
     */
    String ATTR_SYSTEMID = "systemid";
    String ATTR_VALUE = "value";

    String TAG_COMPONENT = "component";
    String TAG_COMPONENTS = "components";
    String TAG_COMPONENT_ID = "component-id";
    String TAG_COMPONENT_DESCRIPTION = "component-description";
    String TAG_COMPONENT_NAME = "component-name";
    String TAG_CONFIGURATION = "configuration";
    String TAG_CONFIGURATION_VERSION = "configuration-version";
    String TAG_CUSTOM_FIELD = "custom-field";
    String TAG_CUSTOM_FIELDS = "custom-fields";
    String TAG_CUSTOM_FIELD_DATEFORMAT = "custom-field-dateformat";
    String TAG_CUSTOM_FIELD_LABEL = "custom-field-label";
    String TAG_CUSTOM_FIELD_OPTION = "custom-field-option";
    String TAG_CUSTOM_FIELD_REQUIRED = "custom-field-required";
    String TAG_CUSTOM_FIELD_SORTOPTIONS = "custom-field-sortoptions";
    String TAG_CUSTOM_FIELD_TYPE = "custom-field-type";
    String TAG_CREATE_DATE = "create-date";
    String TAG_CREATOR = "creator";
    String TAG_EMAIL = "email";
    String TAG_FIRST_NAME = "first-name";
    String TAG_HISTORY_ENTRY = "history-entry";
    String TAG_ISSUE = "issue";
    String TAG_ISSUES = "issues";
    String TAG_ISSUE_ATTACHMENT = "issue-attachment";
    String TAG_ISSUE_ATTACHMENTS = "issue-attachments";
    String TAG_ISSUE_ATTACHMENT_CREATOR = "issue-attachment-creator";
    String TAG_ISSUE_ATTACHMENT_DESCRIPTION = "issue-attachment-description";
    String TAG_ISSUE_ATTACHMENT_FILENAME = "issue-attachment-filename";
    String TAG_ISSUE_ATTACHMENT_ORIGFILE = "issue-attachment-origfile";
    String TAG_ISSUE_ATTACHMENT_SIZE = "issue-attachment-size";
    String TAG_ISSUE_ATTACHMENT_TYPE = "issue-attachment-type";
    String TAG_ISSUE_COMPONENTS = "issue-components";
    String TAG_ISSUE_DESCRIPTION = "issue-description";
    String TAG_ISSUE_FIELD = "issue-field";
    String TAG_ISSUE_FIELDS = "issue-fields";
    String TAG_ISSUE_HISTORY = "issue-history";
    String TAG_ISSUE_PROJECT = "issue-project";
    String TAG_ISSUE_RESOLUTION = "issue-resolution";
    String TAG_ISSUE_SEVERITY = "issue-severity";
    String TAG_ISSUE_STATUS = "issue-status";
    String TAG_ISSUE_VERSIONS = "issue-versions";
    String TAG_LAST_MODIFIED = "last-modified";
    String TAG_LAST_NAME = "last-name";
    String TAG_LOGIN = "login";
    String TAG_OWNER = "owner";
    String TAG_PROJECT = "project";
    String TAG_PROJECTS = "projects";
    String TAG_PROJECT_DESCRIPTION = "project-description";
    String TAG_PROJECT_FIELDS = "project-custom-fields";
    String TAG_PROJECT_FIELD_ID = "project-custom-field";
    String TAG_PROJECT_NAME = "project-name";
    String TAG_PROJECT_OPTIONS = "project-options";
    String TAG_PROJECT_OWNERS = "project-owners";
    String TAG_PROJECT_OWNER_ID = "project-owner";
    String TAG_PROJECT_STATUS = "project-status";
    String TAG_RESOLUTION = "resolution";
    String TAG_RESOLUTIONS = "resolutions";
    String TAG_ROOT = "itracker";
    String TAG_SEVERITIES = "severities";
    String TAG_SEVERITY = "severity";
    String TAG_STATUS = "status";
    String TAG_STATUSES = "statuses";
    String TAG_SUPER_USER = "super-user";
    String TAG_TARGET_VERSION_ID = "target-version-id";
    String TAG_USER = "user";
    String TAG_USERS = "users";
    String TAG_USER_STATUS = "user-status";
    String TAG_VERSION = "version";
    String TAG_VERSIONS = "versions";
    String TAG_VERSION_DESCRIPTION = "version-description";
    String TAG_VERSION_ID = "version-id";
    String TAG_VERSION_NUMBER = "version-number";
}
