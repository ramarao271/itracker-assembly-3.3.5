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

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.Issue;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.IssueService;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.MoveIssueForm;
import org.itracker.web.util.LoginUtilities;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class MoveIssueAction extends ItrackerBaseAction {

    private static final Logger log = Logger.getLogger(MoveIssueAction.class);

    private static final String UNAUTHORIZED_PAGE = "unauthorized";
    private static final String VIEW_ISSUE_PAGE = "viewissue";
    private static final String EDIT_ISSUE_PAGE = "editissue";
    private static final String DEFAULT_PAGE = "index";
    private static final String PAGE_TITLE_KEY = "itracker.web.moveissue.title";

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        request.setAttribute("pageTitleKey", PAGE_TITLE_KEY);
        request.setAttribute("pageTitleArg", "itracker.web.generic.unknown");

        if (!isValidToken(mapping, request, errors)) {
            return mapping.findForward(DEFAULT_PAGE);
        }

        try {
            IssueService issueService = getITrackerServices().getIssueService();
            Integer issueId = ((MoveIssueForm) form).getIssueId();
            Integer projectId = ((MoveIssueForm) form).getProjectId();
            String caller = ((MoveIssueForm) form).getCaller() != null ? ((MoveIssueForm) form)
                    .getCaller()
                    : DEFAULT_PAGE;

            Issue issue = issueService.getIssue(issueId);
            if (issue == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidissue"));
            }

            request.setAttribute("pageTitleArg", issue.getId());

            // is already on this issue
            if (issue.getProject() != null && issue.getProject().getId().equals(projectId)) {
                log.error("execute: attempted to move issue to its containing project");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
            }

            if (errors.isEmpty()) {
                User user = LoginUtilities.getCurrentUser(request);
                if (!isPermissionGranted(request, issue))
                    return mapping.findForward(UNAUTHORIZED_PAGE);

                issueService.moveIssue(issue, projectId, user.getId());
                if (caller.equals(EDIT_ISSUE_PAGE)) {
                    log.info("execute: go to forward editissue");
                    return new ActionForward(mapping.findForward(EDIT_ISSUE_PAGE).getPath() + "?id=" + issue.getId());
                } else if (caller.equals(VIEW_ISSUE_PAGE)) {
                    log.info("execute: go to forward viewissue");
                    return new ActionForward(mapping.findForward(VIEW_ISSUE_PAGE).getPath() + "?id=" + issue.getId());
                } else {
                    return mapping.findForward(caller);
                }
            }
        } catch (Exception e) {
            log.error("execute: Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

    /**
     * Validates token.
     *
     * @param mapping ActionMapping.
     * @param request HttpServletRequest.
     * @param errors  ActionMessages.
     * @return true if token is valid.
     */
    private boolean isValidToken(ActionMapping mapping,
                                 HttpServletRequest request, ActionMessages errors) {
        if (!isTokenValid(request)) {
            log.debug("Invalid request token while creating issue.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return false;
        }
        resetToken(request);
        return true;
    }


    /**
     * Checks permissions.
     *
     * @param request HttpServletRequest.
     * @param issue   issue.
     * @return true if permission is granted.
     */
    private boolean isPermissionGranted(HttpServletRequest request, Issue issue) {
        Map<Integer, Set<PermissionType>> userPermissions = getUserPermissions(request.getSession());
        // TODO is seems first condition is not necessary
        // TODO: return detailed messages on the missing authorization
        if (!UserUtilities.hasPermission(userPermissions, issue.getProject().getId(), UserUtilities.PERMISSION_EDIT_FULL)) {
            log.debug("User not authorized to move issue " + issue.getProject().getId());
            return false;
        }
        if (!UserUtilities.hasPermission(userPermissions, issue.getProject().getId(), new PermissionType[]{PermissionType.ISSUE_EDIT_ALL, PermissionType.ISSUE_CREATE})) {
            log.debug("User attempted to move issue " + issue.getId() + " to unauthorized project.");
            return false;
        }
        return true;
    }
}
