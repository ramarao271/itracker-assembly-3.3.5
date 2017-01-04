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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.*;
import org.itracker.model.util.WorkflowUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class EditIssueAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditIssueAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("execute: called");
        ActionMessages errors = new ActionMessages();
        Date logDate = new Date();
        Date startDate = new Date();
        logTimeMillies("execute: called", logDate, log, Level.DEBUG);
        if (!isTokenValid(request)) {
            log.debug("execute: Invalid request token while editing issue.");

            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            log.info("execute: return to edit-issue");
            saveToken(request);
            return mapping.getInputForward();
        }
        resetToken(request);


        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();


            logTimeMillies("execute: got issueService", logDate, log, Level.DEBUG);
            NotificationService notificationService = ServletContextUtils.getItrackerServices()
                    .getNotificationService();
            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);

            Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);
            IssueForm issueForm = (IssueForm) form;
            int previousStatus = -1;
            Issue issue;
            try {
                issue = issueService.getIssue(issueForm.getId());
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("could not load issue #" + issueForm.getId(), e);
                }
                issue = null;
            }

            if (issue == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidissue"));
                saveErrors(request, errors);
                log.info("execute: invalidissue " + issueForm.getId() + ", Forward: Error");
                return mapping.findForward("error");
            }

            logTimeMillies("execute: got issue", logDate, log, Level.DEBUG);

            Project project = issue.getProject();
            logTimeMillies("execute: got project", logDate, log, Level.DEBUG);
            if (project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidproject"));
                saveErrors(request, errors);
                log.info("execute: Forward: Error");
                return mapping.findForward("error");
            } else if (project.getStatus() != Status.ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.projectlocked"));
                saveErrors(request, errors);
                log.info("execute: Forward: Error");
                return mapping.findForward("error");
            } else if (!LoginUtilities.canEditIssue(issue)) {
                log.info("execute: Forward: unauthorized");
                return mapping.findForward("unauthorized");
            }

            logTimeMillies("execute: got scripts", logDate, log, Level.DEBUG);

            issueForm.invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, errors);

            logTimeMillies("execute: processed field scripts EVENT_FIELD_ONPRESUBMIT", logDate, log, Level.DEBUG);

            if (errors.isEmpty()) {
                previousStatus = issue.getStatus();
                try {
                    if (LoginUtilities.hasPermission(project,
                            PermissionType.ISSUE_EDIT_FULL)) {
                        if (log.isDebugEnabled()) {
                            log.debug("execute: process full, " + issue);
                        }
                        issue = issueForm.processFullEdit(issue, project, currUser, userPermissions,
                                getLocale(request), issueService, errors);
                        logTimeMillies("execute: processed fulledit", logDate, log, Level.DEBUG);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("execute: process limited, " + issue);
                        }
                        issue = issueForm.processLimitedEdit(issue, project, currUser, userPermissions,
                                getLocale(request), issueService, errors);
                        logTimeMillies("execute: processed limited edit", logDate, log, Level.DEBUG);
                    }
                } catch (Exception e) {
                    log.warn("execute: failed to update", e);
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.other"));
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
                }
            }

            if (errors.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("execute: sending notification for issue: " + issue
                            + " (HISTORIES: " + issueService.getIssueHistory(issue.getId()) + ")");
                }
                EditIssueActionUtil.sendNotification(issue.getId(), previousStatus,
                        getBaseURL(request), notificationService);
                logTimeMillies("execute: sent notification", logDate, log, Level.DEBUG);

                issueForm.invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, errors);

                logTimeMillies("execute: processed field scripts EVENT_FIELD_ONPOSTSUBMIT", logDate, log, Level.DEBUG);

                return EditIssueActionUtil.getReturnForward(issue, project, issueForm.getCaller(), mapping);
            }
        } catch (Exception e) {
            log.error("execute: Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        } finally {
            logTimeMillies("execute: processed", startDate, log, Level.DEBUG);
        }

        if (!errors.isEmpty()) {
            saveMessages(request, errors);
            saveErrors(request, errors);
            saveToken(request);

            return mapping.getInputForward();
        }

        log.info("execute: Forward: Error");
        return mapping.findForward("error");
    }

}
