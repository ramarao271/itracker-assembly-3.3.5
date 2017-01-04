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

package org.itracker.model.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Contains utilities used when displaying and processing issues.
 */
public class IssueUtilities {

    private static final Logger log = Logger.getLogger(IssueUtilities.class);
    public static final int FIELD_TYPE_SINGLE = 1;
    public static final int FIELD_TYPE_INDEXED = 2;
    public static final int FIELD_TYPE_MAP = 3;

    public static final int FIELD_ID = -1;
    public static final int FIELD_DESCRIPTION = -2;
    public static final int FIELD_STATUS = -3;
    public static final int FIELD_RESOLUTION = -4;
    public static final int FIELD_SEVERITY = -5;
    public static final int FIELD_CREATOR = -6;
    public static final int FIELD_CREATEDATE = -7;
    public static final int FIELD_OWNER = -8;
    public static final int FIELD_LASTMODIFIED = -9;
    public static final int FIELD_PROJECT = -10;
    public static final int FIELD_TARGET_VERSION = -11;
    public static final int FIELD_COMPONENTS = -12;
    public static final int FIELD_VERSIONS = -13;
    public static final int FIELD_ATTACHMENTDESCRIPTION = -14;
    public static final int FIELD_ATTACHMENTFILENAME = -15;
    public static final int FIELD_HISTORY = -16;

    protected static final int[] STANDARD_FIELDS = {FIELD_ID,
            FIELD_DESCRIPTION, FIELD_STATUS, FIELD_RESOLUTION, FIELD_SEVERITY,
            FIELD_CREATOR, FIELD_CREATEDATE, FIELD_OWNER, FIELD_LASTMODIFIED,
            FIELD_PROJECT, FIELD_TARGET_VERSION, FIELD_COMPONENTS,
            FIELD_VERSIONS, FIELD_ATTACHMENTDESCRIPTION,
            FIELD_ATTACHMENTFILENAME, FIELD_HISTORY};

    public static final int STATUS_NEW = 100;
    public static final int STATUS_UNASSIGNED = 200;
    public static final int STATUS_ASSIGNED = 300;
    public static final int STATUS_RESOLVED = 400;
    public static final int STATUS_CLOSED = 500;

    // This marks the end of all status numbers. You can NOT add a status above
    // this number or
    // they will not be found.
    public static final int STATUS_END = 600;

    public static final int HISTORY_STATUS_REMOVED = -1;
    public static final int HISTORY_STATUS_AVAILABLE = 1;

    /**
     * Defines a related issue. Sample text: related to
     */
    public static final int RELATION_TYPE_RELATED_P = 1;
    /**
     * Defines a related issue. Sample text: related to
     */
    public static final int RELATION_TYPE_RELATED_C = 2;
    /**
     * Defines a duplicate issue. Sample text: duplicates
     */
    public static final int RELATION_TYPE_DUPLICATE_P = 3;
    /**
     * Defines a duplicate issue. Sample text: duplicate of
     */
    public static final int RELATION_TYPE_DUPLICATE_C = 4;
    /**
     * Defines a cloned issue. Sample text: cloned to
     */
    public static final int RELATION_TYPE_CLONED_P = 5;
    /**
     * Defines a cloned issue. Sample text: cloned from
     */
    public static final int RELATION_TYPE_CLONED_C = 6;
    /**
     * Defines a split issue. Sample text: split to
     */
    public static final int RELATION_TYPE_SPLIT_P = 7;
    /**
     * Defines a split issue. Sample text: split from
     */
    public static final int RELATION_TYPE_SPLIT_C = 8;
    /**
     * Defines a dependent issue. Sample text: dependents
     */
    public static final int RELATION_TYPE_DEPENDENT_P = 9;
    /**
     * Defines a dependent issue. Sample text: depends on
     */
    public static final int RELATION_TYPE_DEPENDENT_C = 10;

    public static final int NUM_RELATION_TYPES = 10;

    private static List<Configuration> resolutions = new ArrayList<Configuration>();
    private static List<Configuration> severities = new ArrayList<Configuration>();
    private static List<Configuration> statuses = new ArrayList<Configuration>();
    private static List<CustomField> customFields = new ArrayList<CustomField>();
    private static final Logger logger = Logger.getLogger(IssueUtilities.class);

    public IssueUtilities() {
    }

    public static String getOwnerName(User owner, Locale locale) {
        return (null != owner? owner.getFullName():
                ITrackerResources.getString("itracker.web.generic.unassigned", locale));
    }

    public static int getFieldType(Integer fieldId) {
        if (fieldId != null) {
            if (fieldId > 0) {
                return FIELD_TYPE_MAP;
            }

        }

        return FIELD_TYPE_SINGLE;
    }

    public static String getFieldName(Integer fieldId) {
        if (fieldId == null) {
            return "";
        }

        if (fieldId > 0) {
            return "customFields";
        }

        switch (fieldId) {
            case FIELD_ID:
                return "id";
            case FIELD_DESCRIPTION:
                return "description";
            case FIELD_STATUS:
                return "status";
            case FIELD_RESOLUTION:
                return "resolution";
            case FIELD_SEVERITY:
                return "severity";
            case FIELD_CREATOR:
                return "creatorId";
            case FIELD_CREATEDATE:
                return "createdate";
            case FIELD_OWNER:
                return "ownerId";
            case FIELD_LASTMODIFIED:
                return "lastmodified";
            case FIELD_PROJECT:
                return "projectId";
            case FIELD_TARGET_VERSION:
                return "targetVersion";
            case FIELD_COMPONENTS:
                return "components";
            case FIELD_VERSIONS:
                return "versions";
            case FIELD_ATTACHMENTDESCRIPTION:
                return "attachmentDescription";
            case FIELD_ATTACHMENTFILENAME:
                return "attachment";
            case FIELD_HISTORY:
                return "history";
            default:
                return "";
        }
    }

    public static String getFieldName(Integer fieldId,
                                      List<CustomField> customFields, Locale locale) {

        if (fieldId < 0) {
            return ITrackerResources.getString(getStandardFieldKey(fieldId), locale);
        } else {
            return CustomFieldUtilities.getCustomFieldName(fieldId,
                    locale);
        }

    }

    public static String getStandardFieldKey(int fieldId) {
        switch (fieldId) {
            case FIELD_ID:
                return "itracker.web.attr.id";
            case FIELD_DESCRIPTION:
                return "itracker.web.attr.description";
            case FIELD_STATUS:
                return "itracker.web.attr.status";
            case FIELD_RESOLUTION:
                return "itracker.web.attr.resolution";
            case FIELD_SEVERITY:
                return "itracker.web.attr.severity";
            case FIELD_CREATOR:
                return "itracker.web.attr.creator";
            case FIELD_CREATEDATE:
                return "itracker.web.attr.createdate";
            case FIELD_OWNER:
                return "itracker.web.attr.owner";
            case FIELD_LASTMODIFIED:
                return "itracker.web.attr.lastmodified";
            case FIELD_PROJECT:
                return "itracker.web.attr.project";
            case FIELD_TARGET_VERSION:
                return "itracker.web.attr.target";
            case FIELD_COMPONENTS:
                return "itracker.web.attr.components";
            case FIELD_VERSIONS:
                return "itracker.web.attr.versions";
            case FIELD_ATTACHMENTDESCRIPTION:
                return "itracker.web.attr.attachmentdescription";
            case FIELD_ATTACHMENTFILENAME:
                return "itracker.web.attr.attachmentfilename";
            case FIELD_HISTORY:
                return "itracker.web.attr.detaileddescription";
            default:
                return "itracker.web.generic.unknown";
        }
    }

    public static NameValuePair[] getStandardFields(Locale locale) {
        NameValuePair[] fieldNames = new NameValuePair[STANDARD_FIELDS.length];
        for (int i = 0; i < STANDARD_FIELDS.length; i++) {
            fieldNames[i] = new NameValuePair(ITrackerResources.getString(
                    getStandardFieldKey(STANDARD_FIELDS[i]), locale), Integer
                    .toString(STANDARD_FIELDS[i]));
        }
        return fieldNames;
    }

    public static String getRelationName(IssueRelation.Type value) {
        return getRelationName(value, ITrackerResources.getLocale());
    }


    public static String getRelationName(IssueRelation.Type value, Locale locale) {
        return StringUtils.defaultIfBlank(ITrackerResources.getString(
                ITrackerResources.KEY_BASE_ISSUE_RELATION + value.getCode(), locale), value.name());
    }

    public static IssueRelation.Type getMatchingRelationType(IssueRelation.Type relationType) {
        switch (relationType) {
            case RELATED_P:
                return IssueRelation.Type.RELATED_C;
            case RELATED_C:
                return IssueRelation.Type.RELATED_P;
            case DUPLICATE_P:
                return IssueRelation.Type.DUPLICATE_C;
            case DUPLICATE_C:
                return IssueRelation.Type.DUPLICATE_P;
            case CLONED_P:
                return IssueRelation.Type.CLONED_C;
            case CLONED_C:
                return IssueRelation.Type.CLONED_P;
            case SPLIT_P:
                return IssueRelation.Type.SPLIT_C;
            case SPLIT_C:
                return IssueRelation.Type.SPLIT_P;
            case DEPENDENT_P:
                return IssueRelation.Type.DEPENDENT_C;
            case DEPENDENT_C:
            default:
                return IssueRelation.Type.DEPENDENT_P;
        }
    }

    public static String componentsToString(Issue issue) {
        StringBuilder value = new StringBuilder();
        if (issue != null && issue.getComponents().size() > 0) {
            for (int i = 0; i < issue.getComponents().size(); i++) {
                value.append(i != 0 ? ", " : "").append(issue.getComponents().get(i).getName());
            }
        }
        return value.toString();
    }

    public static String versionsToString(Issue issue) {
        StringBuilder value = new StringBuilder();
        if (issue != null && issue.getVersions().size() > 0) {
            for (int i = 0; i < issue.getVersions().size(); i++) {
                value.append(i != 0 ? ", " : "").append(issue.getVersions().get(i).getNumber());
            }
        }
        return value.toString();
    }

    public static String historyToString(Issue issue, SimpleDateFormat sdf) {
        StringBuilder value = new StringBuilder();
        if (issue != null && issue.getHistory().size() > 0 && sdf != null) {
            for (int i = 0; i < issue.getHistory().size(); i++) {
                value.append(i != 0 ? "," : "").append(issue.getHistory().get(i).getDescription()).append(",").append(issue.getHistory().get(i).getUser().getFirstName());
                value.append(" ").append(issue.getHistory().get(i).getUser().getLastName()).append(",").append(sdf.format(issue.getHistory().get(i)
                        .getLastModifiedDate()));
            }
        }
        return value.toString();
    }

    public static String getStatusName(Integer value) {
        return getStatusName(value, ITrackerResources.getLocale());
    }

    public static String getStatusName(Integer value, Locale locale) {
        return getStatusName(Integer.toString(value), locale);
    }

    public static String getStatusName(String value, Locale locale) {
        return StringUtils.defaultIfBlank(ITrackerResources.getString(ITrackerResources.KEY_BASE_STATUS
                + value, locale), value);
    }

    /**
     * getStatuses() needs to get implemented..
     */
    public static List<Configuration> getStatuses() {
        return statuses;
    }

    public static List<NameValuePair> getStatuses(Locale locale) {
        List<NameValuePair> statusStrings = new ArrayList<>(statuses.size());
        for (Configuration status : statuses) {
            statusStrings.add(new NameValuePair(getStatusName(status.getValue(), locale), status.getValue()));
        }
        return statusStrings;
    }

    public static void setStatuses(List<Configuration> value) {
        statuses = (value == null ? new ArrayList<Configuration>() : value);
    }

    public static int getNumberStatuses() {
        return statuses.size();
    }

    public static String getSeverityName(Integer value) {
        return StringUtils.defaultIfBlank(getSeverityName(value, ITrackerResources.getLocale()), String.valueOf(value));
    }

    public static String getSeverityName(Integer value, Locale locale) {
        return StringUtils.defaultIfBlank(getSeverityName(Integer.toString(value), locale), String.valueOf(value));
    }

    public static String getSeverityName(String value, Locale locale) {
        return StringUtils.defaultIfBlank(ITrackerResources.getString(ITrackerResources.KEY_BASE_SEVERITY
                + value, locale), String.valueOf(value));
    }

    /**
     * Returns the list of the defined issue severities in the system. The array
     * returned is a cached list set from the setSeverities method. The actual
     * values are stored in the database and and can be obtained from the
     * ConfigurationService bean.
     *
     * @param locale the locale to return the severities as
     */
    public static List<NameValuePair> getSeverities(Locale locale) {
        List<NameValuePair> severityStrings = new ArrayList<>();

        for (Configuration severity : severities) {
            NameValuePair nvp = new NameValuePair(getSeverityName(severity.getValue(), locale),
                    severity.getValue());
            severityStrings.add(nvp);
        }
        return severityStrings;
    }

    public static void setSeverities(List<Configuration> value) {
        severities = (value == null ? new ArrayList<Configuration>() : value);
    }

    public static int getNumberSeverities() {
        return severities.size();
    }

    /**
     * Compares the severity of two issues. The int returned will be negative if
     * the the severity of issue A is less than the severity of issue B,
     * positive if issue A is a higher severity than issue B, or 0 if the two
     * issues have the same severity.
     *
     * @param issueA IssueModel A
     * @param issueB IssueModel B
     */
    public static int compareSeverity(Issue issueA, Issue issueB) {
        if (issueA == null && issueB == null) {
            return 0;
        } else if (issueA == null) {
            return -1;
        } else if (issueB == null) {
            return 1;
        } else {
            int issueAIndex = Integer.MAX_VALUE;
            int issueBIndex = Integer.MAX_VALUE;
            for (int i = 0; i < severities.size(); i++) {
                if (severities.get(i) != null) {
                    if (severities.get(i).getValue().equalsIgnoreCase(
                            Integer.toString(issueA.getSeverity()))) {
                        issueAIndex = i;
                    }
                    if (severities.get(i).getValue().equalsIgnoreCase(
                            Integer.toString(issueB.getSeverity()))) {
                        issueBIndex = i;
                    }
                }
            }
            if (issueAIndex > issueBIndex) {
                return -1;
            } else if (issueAIndex < issueBIndex) {
                return 1;
            }
        }

        return 0;
    }

    public static String getResolutionName(int value) {
        return getResolutionName(value, ITrackerResources.getLocale());
    }

    public static String getResolutionName(int value, Locale locale) {
        return getResolutionName(Integer.toString(value), locale);
    }

    public static String getResolutionName(String value, Locale locale) {
        return ITrackerResources.getString(
                ITrackerResources.KEY_BASE_RESOLUTION + value, locale);
    }

    public static String checkResolutionName(String value, Locale locale)
            throws MissingResourceException {
        return ITrackerResources.getCheckForKey(
                ITrackerResources.KEY_BASE_RESOLUTION + value, locale);
    }

    /**
     * Returns the list of predefined resolutions in the system. The array
     * returned is a cached list set from the setResolutions method. The actual
     * values are stored in the database and and can be obtained from the
     * ConfigurationService bean.
     *
     * @param locale the locale to return the resolutions as
     */
    public static List<NameValuePair> getResolutions(Locale locale) {
        final List<NameValuePair> resolutionStrings = new ArrayList<>(resolutions.size());
        for (Configuration resolution : resolutions) {
            resolutionStrings.add(new NameValuePair(
                    getResolutionName(resolution.getValue(), locale),
                    resolution.getValue()));
        }
        return resolutionStrings;
    }

    /**
     * Sets the cached list of predefined resolutions.
     */
    public static void setResolutions(List<Configuration> value) {
        resolutions = (value == null ? new ArrayList<Configuration>() : value);
    }

    public static String getActivityName(IssueActivityType type) {
        return getActivityName(type, ITrackerResources.getLocale());
    }

    public static String getActivityName(IssueActivityType type, Locale locale) {
        return StringUtils.defaultIfBlank(ITrackerResources.getString("itracker.activity."
                + String.valueOf(type.name()), locale), type.name());
    }

    /**
     * Returns the cached array of CustomFieldModels.
     *
     * @return an array of CustomFieldModels
     */
    public static List<CustomField> getCustomFields() {
        return (customFields == null ? new ArrayList<CustomField>()
                : customFields);
    }

    /**
     * Sets the cached array of CustomFieldModels.
     *
     */
    public static void setCustomFields(List<CustomField> value) {
        customFields = (value == null ? new ArrayList<CustomField>() : value);
    }

    /**
     * Returns the custom field with the supplied id. Any labels will be
     * localized to the system default locale.
     *
     * @param id the id of the field to return
     * @return the requested CustomField object, or a new field if not found
     */
    public static CustomField getCustomField(Integer id) {
        return getCustomField(id, ITrackerResources.getLocale());
    }

    /**
     * Returns the custom field with the supplied id value. Any labels will be
     * translated to the given locale.
     *
     * @param id     the id of the field to return
     * @param locale the locale to initialize any labels with
     * @return the requested CustomField object, or a new field if not found
     */
    public static CustomField getCustomField(Integer id, Locale locale) {
        CustomField retField = null;

        try {
            for (CustomField customField : customFields) {
                if (customField != null
                        && customField.getId() != null
                        && customField.getId().equals(id)) {
                    retField = (CustomField) customField.clone();
                    break;
                }
            }
        } catch (CloneNotSupportedException cnse) {
            logger.error("Error cloning CustomField: " + cnse.getMessage());
        }
        if (retField == null) {
            retField = new CustomField();
        }

        return retField;
    }

    /**
     * Returns the total number of defined custom fields
     */
    public static int getNumberCustomFields() {
        return customFields.size();
    }

    /**
     * Returns true if the user has permission to view the requested issue.
     *
     * @param issue       an IssueModel of the issue to check view permission for
     * @param user        a User for the user to check permission for
     * @param permissions a HashMap of the users permissions
     */
    public static boolean canViewIssue(Issue issue, User user,
                                       Map<Integer, Set<PermissionType>> permissions) {
        if (user == null) {
            if (log.isInfoEnabled()) {
                log
                        .info("canViewIssue: missing argument. user is null returning false");
            }
            return false;
        }
        return canViewIssue(issue, user.getId(), permissions);
    }


    /**
     * Returns true if the user has permission to view the requested issue.
     *
     * @param issue       an IssueModel of the issue to check view permission for
     * @param userId      the userId of the user to check permission for
     * @param permissions a HashMap of the users permissions
     */
    public static boolean canViewIssue(Issue issue, Integer userId,
                                       Map<Integer, Set<PermissionType>> permissions) {

        if (issue == null || userId == null || permissions == null) {
            if (log.isInfoEnabled()) {
                log.info("canViewIssue: missing argument. issue: " + issue
                        + ", userid: " + userId + ", permissions: "
                        + permissions);
            }
            return false;
        }

        if (UserUtilities.hasPermission(permissions,
                issue.getProject().getId(), PermissionType.ISSUE_VIEW_ALL)) {
            if (log.isDebugEnabled()) {
                log.debug("canViewIssue: issue: " + issue + ", user: " + userId
                        + ", permission: " + PermissionType.ISSUE_VIEW_ALL);
            }
            return true;
        }

        boolean isUsersIssue = false;
        // I think owner & creator should always be able to view the issue
        // otherwise it makes no sense of creating the issue itself.
        // So put these checks before checking permissions for the whole project.
        if (issue.getCreator().getId().equals(userId)) {
            if (log.isDebugEnabled()) {
                log.debug("canViewIssue: issue: " + issue + ", user: " + userId
                        + ", permission: is creator");
            }
            isUsersIssue = true;
        }

        if (issue.getOwner() != null) {
            if (issue.getOwner().getId().equals(userId)) {

                if (log.isDebugEnabled()) {
                    log.debug("canViewIssue: issue: " + issue + ", user: "
                            + userId + ", permission: is owner");
                }
                isUsersIssue = true;
            }
        }

        // TODO should be checking for
        // UserUtilities.hasPermission(permissions, issue.getProject()
        //             .getId(), PermissionType.ISSUE_VIEW_USERS)
        if (isUsersIssue) {
            if (log.isDebugEnabled()) {
                log.debug("canViewIssue: issue: " + issue + ", user: "
                        + userId + ", permission: isUsersIssue");
            }
            return true;
        }


        if (log.isDebugEnabled()) {
            log.debug("canViewIssue: issue: " + issue + ", user: " + userId
                    + ", permission: none matched");
        }
        return false;
    }

    /**
     * Returns true if the user has permission to edit the requested issue.
     *
     * @param issue       an IssueModel of the issue to check edit permission for
     * @param userId      the userId of the user to check permission for
     * @param permissions a HashMap of the users permissions
     */
    @Deprecated
    public static boolean canEditIssue(Issue issue, Integer userId,
                                       Map<Integer, Set<PermissionType>> permissions) {
        if (issue == null || userId == null || permissions == null) {
            if (log.isInfoEnabled()) {
                log.info("canEditIssue: missing argument. issue: " + issue
                        + ", userid: " + userId + ", permissions: "
                        + permissions);
            }
            return false;
        }


        if (UserUtilities.hasPermission(permissions,
                issue.getProject().getId(), PermissionType.ISSUE_EDIT_ALL)) {

            if (log.isDebugEnabled()) {
                log.debug("canEditIssue: user " + userId
                        + " has permission to edit issue " + issue.getId()
                        + ":" + PermissionType.ISSUE_EDIT_ALL);
            }
            return true;
        }
        if (!UserUtilities.hasPermission(permissions, issue.getProject()
                .getId(), PermissionType.ISSUE_EDIT_USERS)) {
            if (log.isDebugEnabled()) {
                log.debug("canEditIssue: user " + userId
                        + " has not permission  to edit issue " + issue.getId()
                        + ":" + PermissionType.ISSUE_EDIT_USERS);
            }
            return false;
        }

        if (issue.getCreator().getId().equals(userId)) {
            if (log.isDebugEnabled()) {
                log.debug("canEditIssue: user " + userId
                        + " is creator of issue " + issue.getId() + ":");
            }
            return true;
        }
        if (issue.getOwner() != null) {
            if (issue.getOwner().getId().equals(userId)) {
                if (log.isDebugEnabled()) {
                    log.debug("canEditIssue: user " + userId
                            + " is owner of issue " + issue.getId() + ":");
                }
                return true;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("canEditIssue: user " + userId
                    + " could not match permission, denied");
        }
        return false;
    }

    /**
     * Returns true if the user can be assigned to this issue.
     *
     * @param issue       an IssueModel of the issue to check assign permission for
     * @param userId      the userId of the user to check permission for
     * @param permissions a HashMap of the users permissions
     */
    @Deprecated
    public static boolean canBeAssignedIssue(Issue issue, Integer userId,
                                             Map<Integer, Set<PermissionType>> permissions) {
        if (issue == null || userId == null || permissions == null) {
            return false;
        }

        if (UserUtilities.hasPermission(permissions,
                issue.getProject().getId(), PermissionType.ISSUE_EDIT_ALL)) {
            return true;
        }
        if (UserUtilities.hasPermission(permissions,
                issue.getProject().getId(), PermissionType.ISSUE_EDIT_USERS)) {
            if (issue.getCreator().getId().equals(userId)) {
                return true;
            } else if (UserUtilities.hasPermission(permissions, issue
                    .getProject().getId(), PermissionType.ISSUE_ASSIGNABLE)) {
                return true;
            } else if (issue.getOwner().getId() != null
                    && issue.getOwner().getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the user can unassign themselves from the issue.
     *
     * @param issue       an IssueModel of the issue to check assign permission for
     * @param userId      the userId of the user to check permission for
     * @param permissions a HashMap of the users permissions
     */
    public static boolean canUnassignIssue(Issue issue, Integer userId,
                                           Map<Integer, Set<PermissionType>> permissions) {
        if (issue == null || userId == null || permissions == null) {
            return false;
        }

        if (UserUtilities.hasPermission(permissions,
                issue.getProject().getId(), PermissionType.ISSUE_ASSIGN_OTHERS)) {
            return true;
        }
        return issue.getOwner() != null
                && userId.equals(issue.getOwner().getId())
                && UserUtilities.hasPermission(permissions, issue.getProject()
                .getId(), PermissionType.ISSUE_UNASSIGN_SELF);

    }

    public static boolean hasIssueRelation(Issue issue, Integer relatedIssueId) {
        if (issue != null) {
            List<IssueRelation> relations = issue.getRelations();
            for (IssueRelation relation : relations) {
                if (relation.getRelatedIssue().getId().equals(
                        relatedIssueId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasIssueNotification(Issue issue, Integer userId) {
        return hasIssueNotification(issue, issue.getProject(), userId);
    }

    public static boolean hasHardNotification(Issue issue, Project project, Integer userId) {
        if (issue == null || userId == null) {
            return false;
        }


        if ((issue.getOwner() != null && issue.getOwner().getId().equals(userId))
                || issue.getCreator().getId().equals(userId)) {
            return true;
        }

        if (project != null && project.getOwners() != null) {
            for (User user : project.getOwners()) {
                if (user.getId().equals(userId)) {
                    return true;
                }
            }
        }
        Collection<Notification> notifications = issue.getNotifications();
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (notification.getUser().getId().equals(userId) && notification.getRole() != Notification.Role.IP) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Evaluate if a certain user is notified on issue change.
     * <p/>
     * FIXME: Does not work for admin of unassigned-issue-projects owner, see portalhome.do
     */
    public static boolean hasIssueNotification(Issue issue, Project project,
                                               Integer userId) {
        if (issue == null || userId == null) {
            return false;
        }

        if (hasHardNotification(issue, project, userId)) {
            return true;
        }

        Collection<Notification> notifications = issue.getNotifications();
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (notification.getUser().getId().equals(userId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static URL getIssueURL(Issue issue, String baseURL) throws MalformedURLException {
        return getIssueURL(issue, new URL(baseURL + (StringUtils.endsWith(baseURL, "/") ? "" : "/")
        ));
    }

    public static URL getIssueURL(Issue issue, URL base) {
        try {
            if (null != base && null != issue)
                return new URL(base, "module-projects/view_issue.do?id=" + issue.getId());
        } catch (MalformedURLException e) {
            log.error("could not create URL", e);
        }
        return null;
    }
}


