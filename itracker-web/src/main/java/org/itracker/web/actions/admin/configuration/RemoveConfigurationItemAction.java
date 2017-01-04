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

package org.itracker.web.actions.admin.configuration;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.Issue;
import org.itracker.SystemConfigurationException;
import org.itracker.model.User;
import org.itracker.model.util.IssueUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.model.util.UserUtilities;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

//TODO: Action Cleanup
public class RemoveConfigurationItemAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(RemoveConfigurationItemAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = getITrackerServices().getConfigurationService();

            Integer configId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            if (configId == null || configId <= 0) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }

            Configuration configItem = configurationService.getConfigurationItem(configId);
            if (configItem == null) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }

            String key;
            if (configItem.getType() == Configuration.Type.severity) {
                key = ITrackerResources.KEY_BASE_SEVERITY + configItem.getValue();

                // Need to promote all issues with the deleted severity.  The safest thing to do is
                // promote them to the next higher severity.
                try {
                    String currConfigValue = configItem.getValue();
                    String newConfigValue = null;

                    List<Configuration> configItems = configurationService.getConfigurationItemsByType(Configuration.Type.severity);
                    for (int i = 0; i < configItems.size(); i++) {
                        if (configItems.get(i) != null && configId.equals(configItems.get(i).getId())) {
                            if (i == 0 && (i + 1) < configItems.size()) {
                                newConfigValue = configItems.get(i + 1).getValue();
                                break;
                            } else if (i > 0) {
                                newConfigValue = configItems.get(i - 1).getValue();
                                break;
                            }
                        }
                    }

                    int currSeverity = Integer.parseInt(currConfigValue);
                    int newSeverity = Integer.parseInt(newConfigValue);
                    log.debug("Promoting issues in severity " + IssueUtilities.getSeverityName(currSeverity) + " to " + IssueUtilities.getSeverityName(newSeverity));

                    HttpSession session = request.getSession(true);
                    User currUser = (User) session.getAttribute(Constants.USER_KEY);
                    Integer currUserId = (currUser == null ? -1 : currUser.getId());

                    IssueService issueService = getITrackerServices().getIssueService();
                    List<Issue> issues = issueService.getIssuesWithSeverity(currSeverity);
                    for (int i = 0; i < issues.size(); i++) {
                        if (issues.get(i) != null) {
                            issues.get(i).setSeverity(newSeverity);

                            issues.set(i, issueService.systemUpdateIssue(issues.get(i), currUserId));
                        }
                    }
                } catch (Exception e) {
                    log.debug("Exception while promoting issues with severity " + configItem.getValue(), e);
                }
            } else if (configItem.getType() == Configuration.Type.status) {
                key = ITrackerResources.KEY_BASE_STATUS + configItem.getValue();

                // Need to demote all issues with the deleted severity.  The safest thing to do is
                // move them down one status to make sure they don't skip something important in any
                // workflow.

                try {
                    String currConfigValue = configItem.getValue();
                    String newConfigValue = null;

                    List<Configuration> configItems = configurationService.getConfigurationItemsByType(Configuration.Type.status);
                    for (int i = 0; i < configItems.size(); i++) {
                        if (configItems.get(i) != null && configId.equals(configItems.get(i).getId())) {
                            if (i == 0 && (i + 1) < configItems.size()) {
                                newConfigValue = configItems.get(i + 1).getValue();
                                break;
                            } else if (i > 0) {
                                newConfigValue = configItems.get(i - 1).getValue();
                                break;
                            }
                        }
                    }

                    int currStatus = Integer.parseInt(currConfigValue);
                    int newStatus = Integer.parseInt(newConfigValue);
                    log.debug("Promoting issues in status " + IssueUtilities.getStatusName(currStatus) + " to " + IssueUtilities.getStatusName(newStatus));

                    HttpSession session = request.getSession(true);
                    User currUser = (User) session.getAttribute(Constants.USER_KEY);
                    Integer currUserId = (currUser == null ? -1 : currUser.getId());

                    IssueService issueService = getITrackerServices().getIssueService();
                    List<Issue> issues = issueService.getIssuesWithStatus(currStatus);
                    for (int i = 0; i < issues.size(); i++) {
                        if (issues.get(i) != null) {
                            issues.get(i).setStatus(newStatus);

                            issues.set(i, issueService.systemUpdateIssue(issues.get(i), currUserId));
                        }
                    }
                } catch (Exception e) {
                    log.debug("Exception while promoting issues with status " + configItem.getValue(), e);
                }
            } else if (configItem.getType() == Configuration.Type.resolution) {
                key = ITrackerResources.KEY_BASE_RESOLUTION + configItem.getValue();

                // No need to edit any issues since the resolutions are stored as text in the issue
            } else {
                throw new SystemConfigurationException("Unsupported configuration item type " + configItem.getType().name() + " found.");
            }

            configurationService.removeConfigurationItem(configItem.getId());
            // Now reset the cached items of removed item's type
            configurationService.resetConfigurationCache(configItem.getType());
            configurationService.removeLanguageKey(key);
            ITrackerResources.clearKeyFromBundles(key, false);


            return mapping.findForward("listconfiguration");
        } catch (SystemConfigurationException sce) {
            log.debug(sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
        } catch (NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
            log.debug("Invalid configuration item id " + request.getParameter("id") + " specified.");
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
  