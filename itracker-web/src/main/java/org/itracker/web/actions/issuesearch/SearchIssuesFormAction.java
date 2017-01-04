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

package org.itracker.web.actions.issuesearch;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.UserPreferences;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.SearchForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class SearchIssuesFormAction extends ItrackerBaseAction {

    private static final Logger log = Logger.getLogger(SearchIssuesFormAction.class);

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        HttpSession session = request.getSession();

        try {
            ProjectService projectService = ServletContextUtils.getItrackerServices().getProjectService();

            ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();
            request.setAttribute("rh", reportService);
            request.setAttribute("uh", userService);

            String projectId = request.getParameter("projectId");

            UserPreferences userPrefs = (UserPreferences) session.getAttribute(Constants.PREFERENCES_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);

            String action = (String) PropertyUtils.getSimpleProperty(form, "action");

            SearchForm searchForm = (SearchForm) form;
            if (searchForm == null) {
                searchForm = new SearchForm();
            }

            boolean newQuery = false;
            IssueSearchQuery query = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);

            log.debug("projectid = " + projectId);
            log.debug("query type = " + (query == null ? "NULL" : query.getType().toString()));
            log.debug("query projectid = " + (query == null ? "NULL" : query.getProjectId().toString()));

            if (query == null || query.getType() == null || "reset".equalsIgnoreCase(action) || (userPrefs != null && !userPrefs.getRememberLastSearch())) {
                log.debug("New search query.  No existing query, reset forced, or saved querys not allowed.");
                query = new IssueSearchQuery();
                query.setType(IssueSearchQuery.TYPE_FULL);
                newQuery = true;
            } else if (query.getType().intValue() == IssueSearchQuery.TYPE_FULL.intValue() && projectId != null) {
                log.debug("New search query.  Previous query FULL, new query PROJECT.");
                query = new IssueSearchQuery();
                query.setType(IssueSearchQuery.TYPE_PROJECT);
                newQuery = true;
            } else if (query.getType().intValue() == IssueSearchQuery.TYPE_PROJECT.intValue()) {
                if (projectId == null || projectId.equals("")) {
                    log.debug("New search query.  Previous query PROJECT, new query FULL.");
                    query = new IssueSearchQuery();
                    query.setType(IssueSearchQuery.TYPE_FULL);
                    newQuery = true;
                } else if (!projectId.equals(query.getProjectId().toString())) {
                    log.debug("New search query.  Requested project (" + projectId + ") different from previous query (" + query.getProjectId().toString() + ")");
                    query = new IssueSearchQuery();
                    query.setType(IssueSearchQuery.TYPE_PROJECT);
                    newQuery = true;
                }
            }

            query.setAvailableProjects(null);

            List<Project> projects = projectService.getAllAvailableProjects();

            List<Project> availableProjectsList = new ArrayList<Project>();
            List<Integer> selectedProjectsList = new ArrayList<Integer>();

            for (Project project : projects) {
                if (!UserUtilities.hasPermission(userPermissions, project.getId(), PermissionType.ISSUE_VIEW_ALL) &&
                        !UserUtilities.hasPermission(userPermissions, project.getId(), PermissionType.ISSUE_VIEW_USERS)) {
                    continue;
                }

                log.debug("Adding project " + project.getId() + " to list of available projects.");
                availableProjectsList.add(project);

                if (projectId != null && StringUtils.equals(String.valueOf(project.getId()), projectId)) {
                    query.setType(IssueSearchQuery.TYPE_PROJECT);
                    query.setProject(project);
                    String pageTitleKey = "itracker.web.search.project.title";
                    String pageTitleArg = project.getName();
                    request.setAttribute("pageTitleKey", pageTitleKey);
                    request.setAttribute("pageTitleArg", pageTitleArg);
                    break;
                } else {
                    if (query.getProjects().contains(project.getId())) {
                        selectedProjectsList.add(project.getId());
                    }

                }
            }

            if (!availableProjectsList.isEmpty()) {
                log.debug("Issue Search has " + availableProjectsList.size() + " available projects.");

                Collections.sort(availableProjectsList, new Project.ProjectComparator());
                query.setAvailableProjects(availableProjectsList);
                if (query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) {
                    searchForm.setProject(query.getProjectId());
                }

                if (newQuery) {
                    log.debug("New search query.  Clearing results and setting defaults.");
                    query.setResults(null);
                    List<Integer> selectedStatusesIntegerList = new ArrayList<Integer>();
                    for (int i = 0; i < IssueUtilities.getStatuses().size(); i++) {
                        try {
                            int statusNumber = Integer.parseInt(IssueUtilities.getStatuses().get(i).getValue());
                            if (statusNumber < IssueUtilities.STATUS_CLOSED) {
                                selectedStatusesIntegerList.add(statusNumber);
                            }
                        } catch (Exception e) {
                            log.debug("Invalid status entry: " + IssueUtilities.getStatuses().get(i));
                        }
                    }

                    Integer[] statusesArray = new Integer[selectedStatusesIntegerList.size()];
                    selectedStatusesIntegerList.toArray(statusesArray);
                    searchForm.setStatuses(statusesArray);

                    List<Integer> selectedSeverities = new ArrayList<Integer>();
                    for (int i = 1; i <= IssueUtilities.getNumberSeverities(); i++) {
                        selectedSeverities.add(i);
                    }

                    Integer[] severitiesArray = new Integer[selectedSeverities.size()];
                    selectedSeverities.toArray(severitiesArray);
                    searchForm.setSeverities(severitiesArray);
                } else {
                    List<Integer> selectedProjects;
                    selectedProjects = selectedProjectsList;
                    query.setProjects(selectedProjects);

                    searchForm.setComponents(null);
                    if (null != query.getComponents() && query.getComponents().size() > 0) {
                        Integer[] componentsArray = new Integer[query.getComponents().size()];
                        query.getComponents().toArray(componentsArray);
                        searchForm.setComponents(componentsArray);
                    }

                    searchForm.setCreator(null);
                    if (null != query.getCreator()) {
                        searchForm.setCreator(query.getCreator().getId());
                    }
                    searchForm.setOwner(null);
                    if (null != query.getOwner()) {
                        searchForm.setOwner(query.getOwner().getId());
                    }
                    searchForm.setOrderBy(query.getOrderBy());
                    searchForm.setProject(query.getProjectId());

                    searchForm.setProjects(null);
                    if (null != query.getProjects() && query.getProjects().size() > 0) {
                        Integer[] projectsArray = new Integer[query.getProjects().size()];
                        query.getProjects().toArray(projectsArray);
                        searchForm.setProjects(projectsArray);
                    }

                    searchForm.setResolution(query.getResolution());

                    searchForm.setSeverities(null);
                    if (null != query.getSeverities() && query.getSeverities().size() > 0) {
                        Integer[] severitiesArray = new Integer[query.getSeverities().size()];
                        query.getSeverities().toArray(severitiesArray);
                        searchForm.setSeverities(severitiesArray);
                    }

                    searchForm.setStatuses(null);
                    if (null != query.getStatuses() && query.getStatuses().size() > 0) {
                        Integer[] statusesArray = new Integer[query.getStatuses().size()];
                        query.getStatuses().toArray(statusesArray);
                        searchForm.setStatuses(statusesArray);
                    }

                    searchForm.setTargetVersion(query.getTargetVersion());
                    searchForm.setTextphrase(query.getText());

                    searchForm.setVersions(null);
                    if (query.getVersions() != null && query.getVersions().size() > 0) {
                        Integer[] versionsArray = new Integer[query.getVersions().size()];
                        query.getVersions().toArray(versionsArray);
                        searchForm.setVersions(versionsArray);
                    }

                }

                request.setAttribute("searchForm", searchForm);

                session.setAttribute(Constants.SEARCH_QUERY_KEY, query);

            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.noprojects"));
            }

            if (errors.isEmpty()) {
                return mapping.getInputForward();
            }
        } catch (Exception e) {
            log.error("Exception while creating search issues form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }

}
