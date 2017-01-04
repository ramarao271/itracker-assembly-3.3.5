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
import org.itracker.model.*;
import org.itracker.model.util.IssueUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.IssueForm;
import org.itracker.web.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * This class populates an IssueForm object for display by the edit issue page.
 */
public class EditIssueFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditIssueFormAction.class);

    /* (non-Javadoc)
      * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("execute: called with mapping: " + mapping + ", form: "
                    + form + ", request: " + request + ", response: "
                    + response);
        }
        ActionMessages errors = new ActionMessages();

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();
            Integer issueId = new Integer(
                    (request.getParameter("id") == null ? "-1" : (request
                            .getParameter("id"))));

            Issue issue;
            try {
                issue = issueService.getIssue(issueId);
            } catch (Exception ex) {
                issue = null;
            }
            if (issue == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidissue"));
                saveErrors(request, errors);
                return mapping.findForward("error");
            }
            Project project = issueService.getIssueProject(issueId);

            if (project == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidproject"));
            } else if (project.getStatus() != Status.ACTIVE) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.projectlocked"));
            } else {
                HttpSession session = request.getSession(true);
                User currUser = (User) session.getAttribute(Constants.USER_KEY);
                Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);

                Locale locale = getLocale(request);

                List<NameValuePair> ownersList = EditIssueActionUtil
                        .getAssignableIssueOwnersList(issue, project, currUser,
                                locale, userService, userPermissions);

                if (!IssueUtilities.canEditIssue(issue, currUser.getId(),
                        userPermissions)) {
                    log
                            .debug("Unauthorized user requested access to edit issue for project "
                                    + project.getId());
                    return mapping.findForward("unauthorized");
                }

                IssueForm issueForm = (IssueForm) form;
                if (issueForm == null) {
                    issueForm = new IssueForm();
                }
                Map<Integer, List<NameValuePair>> listOptions = EditIssueActionUtil.getListOptions(
                        request, issue, ownersList, userPermissions, issue
                        .getProject(), currUser);

                issueForm.setupIssueForm(issue, listOptions, request, errors);
                IssueNavigationUtil.setupNextPreviousIssueInRequest(request, issue, issueService);

                IssueForm.setupJspEnv(mapping, issueForm, request,
                        issue, issueService, userService, userPermissions,
                        listOptions, errors);

                log.debug("Forwarding to edit issue form for issue "
                        + issue.getId());

                // TODO: Sort attachments
                // Collections.sort(attachments,
                // IssueAttachment.CREATE_DATE_COMPARATOR);

                saveToken(request);
                if (issue == null) {
                    return mapping.findForward("error");
                }
                if (errors.isEmpty()) {
                    log.info("EditIssueFormAction: Forward: InputForward");
                    saveErrors(request, errors);
                    return mapping.getInputForward();
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating edit issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        log.info("EditIssueFormAction: Forward: Error");
        return mapping.findForward("error");
    }

}
