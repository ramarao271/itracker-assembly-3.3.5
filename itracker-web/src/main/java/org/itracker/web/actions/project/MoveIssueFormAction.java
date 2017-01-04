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
import org.itracker.model.Project;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.MoveIssueForm;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class MoveIssueFormAction extends ItrackerBaseAction {

    private static final Logger log = Logger.getLogger(MoveIssueFormAction.class);

    private static final String UNAUTHORIZED_PAGE = "unauthorized";
    private static final String PAGE_TITLE_KEY = "itracker.web.moveissue.title";


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();
        request.setAttribute("pageTitleKey", PAGE_TITLE_KEY);
        request.setAttribute("pageTitleArg", "itracker.web.generic.unknown");

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            ProjectService projectService = ServletContextUtils.getItrackerServices()
                    .getProjectService();

            Integer issueId = Integer
                    .valueOf((request.getParameter("id") == null ? "-1"
                            : (request.getParameter("id"))));
            Issue issue = issueService.getIssue(issueId);
            if (issue == null) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.invalidissue"));
            } else {
                request.setAttribute("pageTitleArg", issue.getId());

                if (errors.isEmpty()) {
                    if (!isPermissionGranted(request, issue)) {
                        return mapping.findForward(UNAUTHORIZED_PAGE);
                    }

                    List<Project> projects = projectService.getAllAvailableProjects();
                    if (projects.size() == 0) {
                        return mapping.findForward(UNAUTHORIZED_PAGE);
                    }

                    List<Project> availableProjects = getAvailableProjects(request,
                            projects, issue);
                    if (availableProjects.size() == 0) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage("itracker.web.error.noprojects"));
                    }

                    if (errors.isEmpty()) {
                        setupMoveIssueForm(request, form, issue, availableProjects);
                        return mapping.getInputForward();
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error("Exception while creating move issue form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

    /**
     * Sets request attributes and fills MoveIssueForm.
     *
     * @param request           HttpServletRequest.
     * @param form              ActionForm.
     * @param issue             issue.
     * @param availableProjects list of available projects.
     */
    private void setupMoveIssueForm(HttpServletRequest request, ActionForm form, Issue issue, List<Project> availableProjects) {
        MoveIssueForm moveIssueForm = (MoveIssueForm) form;
        if (moveIssueForm == null) {
            moveIssueForm = new MoveIssueForm();
        }
        moveIssueForm.setIssueId(issue.getId());
        moveIssueForm.setCaller(request.getParameter("caller"));

        request.setAttribute("moveIssueForm", moveIssueForm);
        request.setAttribute("projects", availableProjects);
        request.setAttribute("issue", issue);
        saveToken(request);
        log.info("No errors while moving issue. Forwarding to move issue form.");
    }

    /**
     * Returns list of available projects.
     *
     * @param request  HttpServletRequest.
     * @param projects list of all projects.
     * @param issue    operated issue.
     * @return list of available projects.
     */
    private List<Project> getAvailableProjects(HttpServletRequest request, List<Project> projects,
                                               Issue issue) {
        Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(request.getSession());
        List<Project> availableProjects = new ArrayList<Project>();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getId() != null
                    && !projects.get(i).equals(issue.getProject())) {
                if (UserUtilities.hasPermission(userPermissions,
                        projects.get(i).getId(), new PermissionType[]{
                        PermissionType.ISSUE_EDIT_ALL,
                        PermissionType.ISSUE_CREATE})) {
                    availableProjects.add(projects.get(i));
                }
            }
        }
        Collections.sort(availableProjects, new Project.ProjectComparator());
        return availableProjects;
    }

    /**
     * Checks permissions.
     *
     * @param request HttpServletRequest.
     * @param issue   issue.
     * @return true if permission is granted.
     */
    private boolean isPermissionGranted(HttpServletRequest request, Issue issue) {
        Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(request.getSession());

        if (!UserUtilities.hasPermission(userPermissions, issue.getProject().getId(), UserUtilities.PERMISSION_EDIT)) {
            log.debug("Unauthorized user requested access to move issue for issue "
                    + issue.getId());
            return false;
        }
        return true;
    }
}
