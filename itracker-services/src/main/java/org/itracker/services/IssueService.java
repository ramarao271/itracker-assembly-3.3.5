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

package org.itracker.services;

import org.itracker.*;
import org.itracker.model.*;

import java.util.*;

public interface IssueService {

    Issue getIssue(Integer issueId);

    /**
     * @deprecated Don't use due to EXPENSIVE memory use.
     */
    List<Issue> getAllIssues();


    Long getNumberIssues();

    /**
     * Returns an array of issues that are currently at the given status.
     *
     * @param status the status to search for
     * @return an array of issues with the given status
     */
    List<Issue> getIssuesWithStatus(int status);

    /**
     * Returns an array of issues that are currently at the given status or a status
     * less than the given status.
     *
     * @param status the status to search for
     * @return an array of issues with the given status or lower
     */
    List<Issue> getIssuesWithStatusLessThan(int status);

    /**
     * Returns an array of issues that are currently at the given severity.
     *
     * @param severity the severity to search for
     * @return an array of issues with the given severity
     */
    List<Issue> getIssuesWithSeverity(int severity);

    List<Issue> getIssuesByProjectId(Integer projectId);

    List<Issue> getIssuesByProjectId(Integer projectId, int status);

    List<Issue> getIssuesCreatedByUser(Integer userId);

    List<Issue> getIssuesCreatedByUser(Integer userId, boolean availableProjectsOnly);

    List<Issue> getIssuesOwnedByUser(Integer userId);

    List<Issue> getIssuesOwnedByUser(Integer userId, boolean availableProjectsOnly);

    List<Issue> getIssuesWatchedByUser(Integer userId);

    List<Issue> getIssuesWatchedByUser(Integer userId, boolean availableProjectsOnly);

    List<Issue> getUnassignedIssues();

    List<Issue> getUnassignedIssues(boolean availableProjectsOnly);

    public List<Issue> getNextIssues(Integer issueId);
    public List<Issue> getPreviousIssues(Integer issueId);
    /**
     * Creates a new issue in a project.
     *
     * @param issue       an Issue representing the new issue information
     * @param projectId   the projectId the issue belongs to
     * @param userId      the id of registered creator of the new issue
     * @param createdById the id of the actual creator of the issue.  This would normally be the same as the userId.
     * @return an Issue containing the newly created issue, or null if the create failed
     */
    Issue createIssue(Issue issue, Integer projectId, Integer userId, Integer createdById)
            throws ProjectException;

    /**
     * Save a modified issue to the persistence layer
     *
     * @param issue      the changed, unsaved issue to update on persistency layer
     * @param userId     the user-id of the changer
     */
    Issue updateIssue(Issue issue, Integer userId) throws ProjectException;

    Issue systemUpdateIssue(Issue issue, Integer userId) throws ProjectException;

    /**
     * Moves an issues from its current project to a new project.
     *
     * @param issue     an Issue of the issue to move
     * @param projectId the id of the target project
     * @param userId    the id of the user that is moving the issue
     * @return an Issue of the issue after it has been moved
     */
    Issue moveIssue(Issue issue, Integer projectId, Integer userId);

    /**
     * @param issueId
     * @param userId
     * @return
     */
    boolean assignIssue(Integer issueId, Integer userId);

    /**
     * @param issueId
     * @param userId
     * @param assignedByUserId
     * @return
     */
    boolean assignIssue(Integer issueId, Integer userId, Integer assignedByUserId);

    /**
     * TODO: Add Javadocs here: document this whole class.describe what is the use for this method.
     */
    boolean setIssueFields(Integer issueId, List<IssueField> fields);

    boolean setIssueComponents(Integer issueId, HashSet<Integer> componentIds, Integer userId);

    boolean setIssueVersions(Integer issueId, HashSet<Integer> versionIds, Integer userId);

    boolean addIssueHistory(IssueHistory history);

    boolean addIssueRelation(Integer issueId, Integer relatedIssueId, IssueRelation.Type relationType, Integer userId);

    boolean addIssueAttachment(IssueAttachment attachment, byte[] data);

    /**
     * Updates the binary data of the attachment stored in the database.
     *
     * @param attachmentId the id of the attachment to update
     * @param data         a byte arrray of the binary data for the attachment
     * @return true if the update was successful
     */
    boolean setIssueAttachmentData(Integer attachmentId, byte[] data);

    /**
     * Updates the binary data of the attachment stored in the database.  Used mainly
     * to take an existing attachment stored on the filesystem and move it into
     * the database.
     *
     * @param fileName the filename listed in the database for the localtion of the attachment.
     *                 This is the name that was previously used to store the data on the
     *                 filesystem, not the original filename of the attachment.
     * @param data     a byte arrray of the binary data for the attachment
     * @return true if the update was successful
     */
    boolean setIssueAttachmentData(String fileName, byte[] data);

//    boolean addIssueNotification(Notification notification);

    boolean removeIssueAttachment(Integer attachmentId);

    //TODO: shall we deprecate this one? why do we need to give it a userId?
    Integer removeIssueHistoryEntry(Integer entryId, Integer userId);

    void removeIssueRelation(Integer relationId, Integer userId);

    Project getIssueProject(Integer issueId);

    List<Component> getIssueComponents(Integer issueId);

    HashSet<Integer> getIssueComponentIds(Integer issueId);

    List<Version> getIssueVersions(Integer issueId);

    HashSet<Integer> getIssueVersionIds(Integer issueId);

    User getIssueCreator(Integer issueId);

    User getIssueOwner(Integer issueId);

    IssueRelation getIssueRelation(Integer relationId);

    List<IssueActivity> getIssueActivity(Integer issueId);

    List<IssueActivity> getIssueActivity(Integer issueId, boolean notificationSent);

    List<IssueAttachment> getAllIssueAttachments();

    Long getAllIssueAttachmentSize();

    Long getAllIssueAttachmentCount();


    IssueAttachment getIssueAttachment(Integer attachmentId);

    /**
     * Returns the binary data for an attachment.
     *
     * @param attachmentId the id of the attachment to obtain the data for
     * @return a byte array containing the attachment data
     */
    byte[] getIssueAttachmentData(Integer attachmentId);

    List<IssueAttachment> getIssueAttachments(Integer issueId);

    int getIssueAttachmentCount(Integer issueId);

    /**
     * Returns the latest issue history entry for a particular issue.
     *
     * @param issueId the id of the issue to return the history entry for.
     * @return the latest IssueHistory, or null if no entries could be found
     */
    IssueHistory getLastIssueHistory(Integer issueId);

    List<IssueHistory> getIssueHistory(Integer issueId);

    int getOpenIssueCountByProjectId(Integer projectId);

    int getResolvedIssueCountByProjectId(Integer projectId);

    int getTotalIssueCountByProjectId(Integer projectId);

    Date getLatestIssueDateByProjectId(Integer projectId);

    boolean canViewIssue(Integer issueId, User user);

    boolean canViewIssue(Issue issue, User user);

    public List<Issue> searchIssues(IssueSearchQuery queryModel, User user, Map<Integer, Set<PermissionType>> userPermissions) throws IssueSearchException;


}
