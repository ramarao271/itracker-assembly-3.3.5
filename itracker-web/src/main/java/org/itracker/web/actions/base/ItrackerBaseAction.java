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
package org.itracker.web.actions.base;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.itracker.model.PermissionType;
import org.itracker.services.ITrackerServices;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
/**
 * The base itracker Struts-Action.
 *
 * @author Marky Goldstein
 */
public abstract class ItrackerBaseAction extends Action {

    private static final Logger log = Logger
            .getLogger(ItrackerBaseAction.class);

    public ItrackerBaseAction() {
        super();
    }


    /**
     * @deprecated moved to {@link org.itracker.web.util.RequestHelper}
     */
    protected Map<Integer, Set<PermissionType>> getUserPermissions(
            HttpSession session) {
        return RequestHelper.getUserPermissions(session);
    }

    /**
     * @deprecated moved to {@link org.itracker.web.util.LoginUtilities}
     */
    protected boolean hasPermission(PermissionType permissionNeeded,
                                    HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        return LoginUtilities.hasPermission(permissionNeeded, request, response);
    }

    @Deprecated
    protected boolean hasPermission(int permissionNeeded,
                                    HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        return LoginUtilities.hasPermission(permissionNeeded, request, response);
    }


    /**
     * @param request - request for base-url
     * @return normalized base-url for the request
     */
    public String getBaseURL(HttpServletRequest request) {

        String url = getITrackerServices().getConfigurationService().getSystemBaseURL();
        if (null == url) {
            url = new StringBuffer(request.getScheme()).append("://").append(
                    request.getServerName()).append(":").append(
                    request.getServerPort()).append(request.getContextPath())
                    .toString();

            log
                    .warn("getBaseURL: no base-url is configured, determin from request. ("
                            + url + ")");
        }
        try {
            return new URL(url).toExternalForm();
        } catch (MalformedURLException e) {
            log.warn("failed to get URL normalized, returning manual url: "
                    + url, e);
        }
        return url;
    }

    /**
     * @deprecated  moved to new static method in {@link ServletContextUtils}
     */
    protected ITrackerServices getITrackerServices() {
        ITrackerServices itrackerServices = ServletContextUtils
                .getItrackerServices();
        return itrackerServices;
    }


    @Override
    public Locale getLocale(HttpServletRequest request) {
        Locale locale = super.getLocale(request);
        if (null == locale) {
            locale = LoginUtilities.getCurrentLocale(request);
        }
        return locale;
    }

    /**
     * Log time passed since timestamp startTime was set. After logging, the passed startTime-Date is reset to
     * {@link System#currentTimeMillis()}. This helps on actions performance issues.
     */
    protected static void logTimeMillies(String message, Date startTime, Logger log,
                                         Level level) {
        if (log.isEnabledFor(level)) {
            long milliesStart = startTime.getTime();
            long milliesEnd = System.currentTimeMillis();
            if (null == log) {
                log = ItrackerBaseAction.log;
            }
            if (null == level) {
                level = Level.INFO;
            }

            log.log(level, new StringBuilder().append("logTimeMillies: ").append(
                    message).append(" took ").append(milliesEnd - milliesStart)
                    .append("ms.").toString());

            // reset the timestamp for next log
            startTime.setTime(System.currentTimeMillis());
        }
    }

}
