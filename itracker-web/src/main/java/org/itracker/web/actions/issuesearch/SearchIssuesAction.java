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
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.IssueSearchException;
import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchIssuesAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(SearchIssuesAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        String pageTitleKey = "itracker.web.search.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute(Constants.USER_KEY);
        Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);

        try {

            ReportService reportService = ServletContextUtils.getItrackerServices()
                    .getReportService();
            UserService userService = ServletContextUtils.getItrackerServices().getUserService();
            request.setAttribute("rh", reportService);
            request.setAttribute("uh", userService);

            IssueSearchQuery isqm = (IssueSearchQuery) session
                    .getAttribute(Constants.SEARCH_QUERY_KEY);
            if (isqm == null) {
                return mapping.findForward("searchissues");
            }
            processQueryParameters(isqm, (ValidatorForm) form, errors);

            if (errors.isEmpty()) {
                List<Issue> results = ServletContextUtils.getItrackerServices().getIssueService()
                        .searchIssues(isqm, user, userPermissions);
                if (log.isDebugEnabled()) {
                    log.debug("SearchIssuesAction received " + results.size()
                            + " results to query.");
                }

                isqm.setResults(results);
                log.debug("Setting search results with "
                        + isqm.getResults().size() + " results");
                session.setAttribute(Constants.SEARCH_QUERY_KEY, isqm);
            }
        } catch (IssueSearchException ise) {
            if (ise.getType() == IssueSearchException.ERROR_NULL_QUERY) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.nullsearch"));
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.system"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.getInputForward();
    }

    private IssueSearchQuery processQueryParameters(IssueSearchQuery isqm,
                                                    ValidatorForm form, ActionMessages errors) {
        if (isqm == null) {
            isqm = new IssueSearchQuery();
        }

        try {
            Integer creatorValue = (Integer) PropertyUtils.getSimpleProperty(
                    form, "creator");
            if (creatorValue != null && creatorValue.intValue() != -1) {
                isqm.setCreator(ServletContextUtils.getItrackerServices().getUserService().getUser(
                        creatorValue));
            } else {
                isqm.setCreator(null);
            }

            Integer ownerValue = (Integer) PropertyUtils.getSimpleProperty(
                    form, "owner");
            if (ownerValue != null && ownerValue.intValue() != -1) {
                isqm.setOwner(ServletContextUtils.getItrackerServices().getUserService().getUser(
                        ownerValue));
            } else {
                isqm.setOwner(null);
            }

            String textValue = (String) PropertyUtils.getSimpleProperty(form,
                    "textphrase");
            if (textValue != null && textValue.trim().length() > 0) {
                isqm.setText(textValue.trim());
            } else {
                isqm.setText(null);
            }

            String resolutionValue = (String) PropertyUtils.getSimpleProperty(
                    form, "resolution");
            if (resolutionValue != null && !resolutionValue.equals("")) {
                isqm.setResolution(resolutionValue);
            } else {
                isqm.setResolution(null);
            }

            Integer[] projectsArray = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "projects");
            List<Integer> projects = Arrays.asList(projectsArray);
            if (projects == null || projects.size() == 0) {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                        "itracker.web.error.projectrequired"));
            } else {
                isqm.setProjects(projects);
            }

            Integer[] severitiesArray = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "severities");
            if (severitiesArray != null && severitiesArray.length > 0) {
                List<Integer> severities = Arrays.asList(severitiesArray);
                isqm.setSeverities(severities);
            } else {
                isqm.setSeverities(null);
            }

            Integer[] statusesArray = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "statuses");
            if (statusesArray != null && statusesArray.length > 0) {
                List<Integer> statuses = Arrays.asList(statusesArray);
                isqm.setStatuses(statuses);
            } else {
                isqm.setStatuses(null);
            }

            Integer[] componentsArray = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "components");
            if (componentsArray != null && componentsArray.length > 0) {
                List<Integer> components = Arrays.asList(componentsArray);
                isqm.setComponents(components);
            } else {
                isqm.setComponents(null);
            }

            Integer[] versionsArray = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "versions");
            if (versionsArray != null && versionsArray.length > 0) {
                List<Integer> versions = Arrays.asList(versionsArray);
                isqm.setVersions(versions);
            } else {
                isqm.setVersions(null);
            }

            Integer targetVersion = (Integer) PropertyUtils.getSimpleProperty(
                    form, "targetVersion");
            if (targetVersion != null && targetVersion > 0) {
                isqm.setTargetVersion(targetVersion);
            } else {
                isqm.setTargetVersion(null);
            }

            String orderBy = (String) PropertyUtils.getSimpleProperty(form,
                    "orderBy");
            if (orderBy != null && !orderBy.equals("")) {
                if (log.isDebugEnabled()) {
                    log
                            .debug("processQueryParameters: set orderBy: "
                                    + orderBy);
                }
                isqm.setOrderBy(orderBy);
            }

            Integer type = (Integer) PropertyUtils.getSimpleProperty(form,
                    "type");
            if (type != null) {
                if (log.isDebugEnabled()) {
                    log.debug("processQueryParameters: set type: " + type);
                }
                isqm.setType(type);
            }
        } catch (RuntimeException e) {
            log.error(
                    "processQueryParameters: Unable to parse search query parameters: "
                            + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidsearchquery"));
        } catch (IllegalAccessException e) {
            log.error(
                    "processQueryParameters: Unable to parse search query parameters: "
                            + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidsearchquery"));
        } catch (InvocationTargetException e) {
            log.error(
                    "processQueryParameters: Unable to parse search query parameters: "
                            + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidsearchquery"));
        } catch (NoSuchMethodException e) {
            log.error(
                    "processQueryParameters: Unable to parse search query parameters: "
                            + e.getMessage(), e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidsearchquery"));
        }

        return isqm;
    }
}
