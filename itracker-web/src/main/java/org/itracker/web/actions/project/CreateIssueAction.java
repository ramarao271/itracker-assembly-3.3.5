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

package org.itracker.web.actions.project;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.ProjectException;
import org.itracker.WorkflowException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.Notification.Type;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.model.util.WorkflowUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * This action handles the actual creation of a new issue.
 * <p/>
 * FIXME: There are validation issues, the createissue-form becomes invalid, missing inputs, missing options.
 *
 * @author ranks
 */

//  TODO: Action Cleanup

public class CreateIssueAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(CreateIssueAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!isTokenValid(request)) {
            log.info("execute: Invalid request token while creating issue.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            log.info("execute: return to edit-issue");
            saveToken(request);


            EditIssueActionUtil.setupCreateIssue(request);
            return mapping.findForward("createissue");

        }
        resetToken(request);

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            NotificationService notificationService = ServletContextUtils.getItrackerServices()
                    .getNotificationService();
            ProjectService projectService = ServletContextUtils.getItrackerServices()
                    .getProjectService();

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissionsMap = RequestHelper.getUserPermissions(session);
            Locale locale = getLocale(request);
            Integer currUserId = currUser.getId();

            IssueForm issueForm = (IssueForm) form;

            Integer creator = currUserId;

            Project project = null;
            Integer projectId = issueForm.getProjectId();

            if (projectId == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidproject"));
            } else {
                project = projectService.getProject(projectId);
            }

            if (errors.isEmpty() && project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidproject"));
            } else if (errors.isEmpty() && project.getStatus() != Status.ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.projectlocked"));
            } else if (!UserUtilities.hasPermission(userPermissionsMap,
                    projectId, PermissionType.ISSUE_CREATE)) {
                return mapping.findForward("unauthorized");
            } else {
                issueForm.invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, errors);

                Issue issue = new Issue();
                issue.setDescription(issueForm.getDescription());
                issue.setSeverity(issueForm.getSeverity());
                issue.setStatus(IssueUtilities.STATUS_NEW);
                issue.setResolution("");

                IssueHistory issueHistory = new IssueHistory(issue, currUser,
                        issueForm.getHistory(),
                        IssueUtilities.HISTORY_STATUS_AVAILABLE);
                issueHistory.setCreateDate(new Date());
                issue.getHistory().add(issueHistory);

                // creating issues as another user

                if (UserUtilities.hasPermission(userPermissionsMap,
                        projectId, PermissionType.ISSUE_CREATE_OTHERS)) {
                    creator = null != issueForm.getCreatorId() ? issueForm.getCreatorId() : currUserId;
                    if (log.isDebugEnabled()) {
                        log.debug("New issue creator set to " + creator
                                + ". Issue created by " + currUserId);
                    }
                }
                // create the issue in the database
                ActionMessages msg = AttachmentUtilities.validate(issueForm.getAttachment(), ServletContextUtils.getItrackerServices());

                if (!msg.isEmpty()) {
                    log.info("execute: tried to create issue with invalid attachemnt: " + msg);
                    errors.add(msg);
                    saveErrors(request, errors);
                    EditIssueActionUtil.setupCreateIssue(request);
                    return mapping.findForward("createissue");
                }

                if (log.isDebugEnabled()) {
                    log.debug("execute: creating new issue..");
                }

                issue = issueService.createIssue(issue, projectId,
                        creator, currUserId);

                if (log.isDebugEnabled()) {
                    log.debug("execute: issue created: " + issue);
                }

                if (issue != null) {
                    if (!ProjectUtilities.hasOption(
                            ProjectUtilities.OPTION_NO_ATTACHMENTS, project
                            .getOptions())) {
                        msg = new ActionMessages();
                        issue = issueForm.addAttachment(issue, project, currUser, ServletContextUtils.getItrackerServices(), msg);

                        if (!msg.isEmpty()) {
                            errors.add(msg);
                        }

                    }


                    Integer newOwner = issueForm.getOwnerId();

                    if (newOwner != null && newOwner.intValue() >= 0) {
                        if (UserUtilities.hasPermission(userPermissionsMap,
                                PermissionType.ISSUE_ASSIGN_OTHERS)
                                || (UserUtilities.hasPermission(
                                userPermissionsMap,
                                PermissionType.ISSUE_ASSIGN_SELF) && currUserId
                                .equals(newOwner))) {
                            issueService.assignIssue(issue.getId(), newOwner,
                                    currUserId);
                        }
                    }

                    // TODO this is absolutely complex, unreadable code. why do it, what does it do, can we keep it simple?
                    // it seems to set issueCustomField (issueFields), you might be able to refactor this into its own method (hiding in a method ;) )
                    List<IssueField> issueFields = new ArrayList<IssueField>();
                    Map<String, String> customFields = issueForm.getCustomFields();

                    if (customFields != null && customFields.size() > 0) {
                        List<IssueField> issueFieldsVector = new ArrayList<IssueField>();
                        ResourceBundle bundle = ITrackerResources
                                .getBundle(locale);

                        for (Iterator<String> iter = customFields.keySet().iterator(); iter
                                .hasNext(); ) {
                            try {
                                Integer fieldId = Integer.valueOf(iter.next());
                                CustomField field = IssueUtilities
                                        .getCustomField(fieldId);
                                String fieldValue = (String) PropertyUtils
                                        .getMappedProperty(form,
                                                "customFields(" + fieldId + ")");

                                if (fieldValue != null
                                        && fieldValue.trim().length() != 0) {
                                    IssueField issueField = new IssueField(
                                            issue, field);
                                    issueField.setValue(fieldValue, bundle);
                                    issueFieldsVector.add(issueField);
                                }
                            } catch (Exception e) {
                                log.error("execute: failed to assign issue", e);
                            }
                        }
                        issueFields = issueFieldsVector;
                    }
                    issueService.setIssueFields(issue.getId(), issueFields);

                    HashSet<Integer> components = new HashSet<>();
                    Integer[] componentIds = issueForm.getComponents();


                    if (componentIds != null) {
                        Collections.addAll(components, componentIds);
                        issueService.setIssueComponents(issue.getId(),
                                components, creator);
                    }
                    HashSet<Integer> versions = new HashSet<>();
                    Integer[] versionIds = issueForm.getVersions();

                    if (versionIds != null) {
                        Collections.addAll(versions, versionIds);
                        issueService.setIssueVersions(issue.getId(), versions,
                                creator);
                    }

                    try {
                        Integer relatedIssueId = issueForm.getRelatedIssueId();
                        IssueRelation.Type relationType = issueForm.getRelationType();

                        if (relatedIssueId != null
                                && relatedIssueId > 0
                                && relationType != null
                                && relationType.getCode() > 0) {
                            Issue relatedIssue = issueService
                                    .getIssue(relatedIssueId);

                            if (relatedIssue == null) {
                                log.debug("Unknown relation issue, relation not created.");
                            } else if (relatedIssue.getProject() == null
                                    || !IssueUtilities.canEditIssue(
                                    relatedIssue, currUserId,
                                    userPermissionsMap)) {
                                log.info("User not authorized to add issue relation from issue "
                                                + issue.getId()
                                                + " to issue "
                                                + relatedIssueId);
                            } else if (IssueUtilities.hasIssueRelation(issue,
                                    relatedIssueId)) {
                                log.debug("Issue " + issue.getId()
                                        + " is already related to issue "
                                        + relatedIssueId
                                        + ", relation ot created.");
                            } else {
                                if (!issueService.addIssueRelation(issue
                                        .getId(), relatedIssueId, relationType,
                                        currUser.getId())) {
                                    log.info("Error adding issue relation from issue "
                                                    + issue.getId()
                                                    + " to issue "
                                                    + relatedIssueId);
                                }
                            }
                        }
                    } catch (RuntimeException e) {
                        log
                                .warn(
                                        "execute: Exception adding new issue relation.",
                                        e);
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("itracker.web.error.system"));
                        saveErrors(request, errors);
                        EditIssueActionUtil.setupCreateIssue(request);
                        return mapping.findForward("createissue");
                    }

                    notificationService.sendNotification(issue, Type.CREATED,
                            getBaseURL(request));
                } else {

                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage("itracker.web.error.system"));
                    saveErrors(request, errors);
                    return mapping.findForward("createissue");
                }
                session.removeAttribute(Constants.PROJECT_KEY);

                issueForm.invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, errors);

                if (errors.isEmpty()) {
                    return getReturnForward(issue, project, issueForm, mapping);
                }
                saveErrors(request, errors);
                EditIssueActionUtil.setupCreateIssue(request);
                return mapping.findForward("createissue");

            }
        } catch (RuntimeException e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } catch (WorkflowException e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } catch (ProjectException e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("createissue");
    }

    private ActionForward getReturnForward(Issue issue, Project project,
                                           IssueForm issueForm, ActionMapping mapping) {
        log.info("getReturnForward: listissues");

        return new ActionForward(mapping.findForward("listissues")
                .getPath()
                + "?projectId=" + project.getId());
    }

}
