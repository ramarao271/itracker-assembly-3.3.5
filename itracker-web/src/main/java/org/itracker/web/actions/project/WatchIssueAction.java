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
import org.itracker.model.Notification.Role;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.IssueService;
import org.itracker.services.NotificationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class WatchIssueAction extends ItrackerBaseAction {


    private static final Logger log = Logger.getLogger(WatchIssueAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ActionMessages errors = new ActionMessages();

        try {
            IssueService issueService = ServletContextUtils.getItrackerServices().getIssueService();
            NotificationService notificationService = ServletContextUtils.getItrackerServices().getNotificationService();
            Integer issueId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
            Issue issue = issueService.getIssue(issueId);
            Project project = issueService.getIssueProject(issueId);

            if (project == null) {
                return mapping.findForward("unauthorized");
            }

            HttpSession session = request.getSession(true);
            User currUser = (User) session.getAttribute(Constants.USER_KEY);
            Map<Integer, Set<PermissionType>> userPermissions = RequestHelper.getUserPermissions(session);

            if (!UserUtilities.hasPermission(userPermissions, project.getId(), UserUtilities.PERMISSION_VIEW_ALL)) {
                return mapping.findForward("unauthorized");
            }

            Notification notification = new Notification();


            boolean userHasIssueNotification = false;
            List<Notification> notifications = issue.getNotifications();

            for (Iterator<Notification> nIterator = notifications.iterator(); nIterator.hasNext(); ) {
                Notification issue_notification = nIterator.next();
                if (issue_notification.getUser().getId().equals(currUser.getId())) {
                    userHasIssueNotification = true;
                    if (issue_notification.getRole() == Role.IP) {
                        notification = issue_notification;
                        nIterator.remove();
                        break;
                    }
                }
            }
            if (userHasIssueNotification) {
                if (null!=notification.getId()) {
                    issue.setNotifications(notifications);
                    notificationService.removeIssueNotification(notification.getId());
                }
            } else {
                notification.setUser(currUser);
                notification.setIssue(issue);
                notification.setRole(Role.IP);
                notificationService.addIssueNotification(notification);
            }
            String caller = request.getParameter("caller");
            if ("editissue".equals(caller)) {
                return new ActionForward(mapping.findForward("editissue").getPath() + "?id=" + issueId);
            } else if ("viewissue".equals(caller)) {
                return new ActionForward(mapping.findForward("viewissue").getPath() + "?id=" + issueId);
                //index was the old name for portalhome, we have to clean the naming in this area... 
            } else if ("index".equals(caller)) {
                return mapping.findForward("index");
            } else {
                return new ActionForward(mapping.findForward("listissues").getPath() + "?projectId=" + project.getId());
            }
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.login.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
  