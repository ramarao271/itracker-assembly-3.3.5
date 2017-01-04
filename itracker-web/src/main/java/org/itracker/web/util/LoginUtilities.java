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

package org.itracker.web.util;

import org.itracker.core.AuthenticationConstants;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.UserService;
import org.itracker.services.authentication.ITrackerUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

public class LoginUtilities {

    private static final Logger logger = LoggerFactory.getLogger(LoginUtilities.class);
    private static final int DEFAULT_SESSION_TIMEOUT = 30;

    public static boolean checkAutoLogin(HttpServletRequest request,
                                         boolean allowSaveLogin) {
        boolean foundLogin = false;

        if (request != null) {
            int authType = getRequestAuthType(request);

            // Check for auto login in request
            if (authType == AuthenticationConstants.AUTH_TYPE_REQUEST) {
                String redirectURL = request.getRequestURI().substring(
                        request.getContextPath().length())
                        + (request.getQueryString() != null ? "?"
                        + request.getQueryString() : "");
                request.setAttribute(Constants.AUTH_TYPE_KEY,
                        AuthenticationConstants.AUTH_TYPE_REQUEST);
                request.setAttribute(Constants.AUTH_REDIRECT_KEY,
                        redirectURL);
                request.setAttribute("processLogin", "true");
                foundLogin = true;

            }

            // Add in check for client certs

            // Check for auto login with cookies, this will only happen if users
            // are allowed to save
            // their logins to cookies
            if (allowSaveLogin && !foundLogin) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (Constants.COOKIE_NAME.equals(cookie.getName())) {
                            int seperator = cookie.getValue().indexOf('~');
                            final String login;
                            if (seperator > 0) {
                                login = cookie.getValue()
                                        .substring(0,
                                                seperator);
                                if (logger.isDebugEnabled()) {
                                    logger
                                            .debug("Attempting autologin for user "
                                                    + login
                                                    + ".");
                                }

                                String redirectURL = request.getRequestURI()
                                        .substring(
                                                request.getContextPath()
                                                        .length())
                                        + (request.getQueryString() != null ? "?"
                                        + request.getQueryString()
                                        : "");
                                request.setAttribute(Constants.AUTH_LOGIN_KEY,
                                        cookie.getValue().substring(0,
                                                seperator));
                                request.setAttribute(Constants.AUTH_TYPE_KEY,
                                        AuthenticationConstants.AUTH_TYPE_PASSWORD_ENC);

                                request.setAttribute(Constants.AUTH_VALUE_KEY,
                                        cookie.getValue().substring(
                                                seperator + 1));
                                request.setAttribute(
                                        Constants.AUTH_REDIRECT_KEY,
                                        redirectURL);
                                request.setAttribute("processLogin", "true");
                                foundLogin = true;
                            }
                        }
                    }
                }
            }

        }

        return foundLogin;
    }

    public static int getRequestAuthType(HttpServletRequest request) {
        int authType = AuthenticationConstants.AUTH_TYPE_UNKNOWN;

        try {
            if (request.getAttribute(Constants.AUTH_TYPE_KEY) != null) {
                authType = (Integer) request
                        .getAttribute(Constants.AUTH_TYPE_KEY);
            }
            if (request.getParameter(Constants.AUTH_TYPE_KEY) != null) {
                authType = Integer.valueOf(request
                        .getParameter(Constants.AUTH_TYPE_KEY));
            }
        } catch (Exception e) {
            logger
                    .debug("Error retrieving auth type while checking auto login.  "
                            + e.getMessage());
        }

        return authType;
    }

    /**
     * Get a locale from request
     * <p/>
     * <p>
     * TODO the order of retrieving locale from request should be:
     * <ol>
     * <li>request-attribute Constants.LOCALE_KEY</li>
     * <li>request-param 'loc'</li>
     * <li>session attribute <code>Constants.LOCALE_KEY</code></li>
     * <li>cookie 'loc'</li>
     * <li>request.getLocale()/request.getLocales()</li>
     * <li>ITrackerResources.DEFAULT_LOCALE</li>
     * </ol>
     * </p>
     */
    @SuppressWarnings("unchecked")
    public static Locale getCurrentLocale(HttpServletRequest request) {
        Locale requestLocale = null;
        HttpSession session = request.getSession(true);
        try {

            requestLocale = (Locale) request.getAttribute(Constants.LOCALE_KEY);

            if (logger.isDebugEnabled()) {
                logger.debug("getCurrentLocale: request-attribute was {}",
                        requestLocale);
            }

            if (null == requestLocale) {
                // get locale from request param
                String loc = request
                        .getParameter("loc");
                if (null != loc && loc.trim().length() > 1) {
                    requestLocale = ITrackerResources.getLocale(loc);
                }

                logger.debug("getCurrentLocale: request-parameter was {}",
                        loc);

            }

            if (null == requestLocale) {
                // get it from the session
                requestLocale = (Locale) session
                        .getAttribute(Constants.LOCALE_KEY);
//				if (logger.isDebugEnabled()) {
//					logger.debug("getCurrentLocale: session-attribute was "
//							+ requestLocale);
//				}
            }

            if (null == requestLocale) {
                ResourceBundle bundle = ITrackerResources.getBundle(request
                        .getLocale());
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("getCurrentLocale: trying request header locale "
                                    + request.getLocale());
                }
                if (bundle.getLocale().getLanguage().equals(
                        request.getLocale().getLanguage())) {
                    requestLocale = request.getLocale();
                    if (logger.isDebugEnabled()) {
                        logger.debug("getCurrentLocale: request-locale was "
                                + requestLocale);
                    }
                }
            }

            // is there no way to detect supported locales of current
            // installation?

            if (null == requestLocale) {
                Enumeration<Locale> locales = (Enumeration<Locale>) request.getLocales();
                ResourceBundle bundle;
                Locale locale;
                while (locales.hasMoreElements()) {
                    locale = locales.nextElement();
                    bundle = ITrackerResources.getBundle(locale);

                    logger.debug("getCurrentLocale: request-locales processing {}, bundle: {}",
                            locale, bundle);

                    if (bundle.getLocale().getLanguage().equals(
                            locale.getLanguage())) {
                        requestLocale = locale;

                        logger.debug("getCurrentLocale: request-locales locale was {}",
                                requestLocale);

                    }
                }
            }

        } finally {
            if (null == requestLocale) {
                // fall back to default locale
                requestLocale = ITrackerResources.getLocale();

                logger.debug("getCurrentLocale: fallback default locale was {}",
                        requestLocale);

            }
            session.setAttribute(Constants.LOCALE_KEY, requestLocale);
            request.setAttribute(Constants.LOCALE_KEY, requestLocale);
            request.setAttribute("currLocale", requestLocale);

            logger.debug("getCurrentLocale: request and session was setup with {}",
                    requestLocale);

        }

        return requestLocale;
    }

    /**
     * get current user from request-attribute currUser, if not set from request-session
     *
     * @return current user or null if unauthenticated
     * @throws NullPointerException if the request was null
     */
    @Deprecated
    public static User getCurrentUser(HttpServletRequest request) {

        final String remoteUser = request.getRemoteUser();
        if (null == remoteUser) {
            return null;
        }
        User currUser = (User) request.getAttribute("currUser");
        if (null != currUser && currUser.getLogin().equals(remoteUser)) {
            if (logger.isDebugEnabled()) {
                logger.debug("found user in request: " + remoteUser);
            }
        }
        if (null == currUser) {
            currUser = (User) request.getSession().getAttribute("currUser");
            if (null != currUser && currUser.getLogin().equals(remoteUser)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("found user in session: " + remoteUser);
                }
            }
        }
        if (null == currUser) {
            currUser = ServletContextUtils.getItrackerServices().getUserService().getUserByLogin(remoteUser);
            if (null != currUser && currUser.getLogin().equals(remoteUser)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("found user by login: " + remoteUser);
                }
            }
        }

        return currUser;
    }

    /**
     * Utility for accessing the current logged in user's principal
     * @return current user principal
     */
    public static ITrackerUserDetails getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof ITrackerUserDetails) {
            return (ITrackerUserDetails)principal;
        }

        return null;
    }

    public static Boolean allowSaveLogin(HttpServletRequest request) {
        return (boolean) request.getAttribute("allowSaveLogin");
    }

    public static User setupSession(String login, HttpServletRequest request,
                                    HttpServletResponse response) {
        if (null == login) {
            logger.warn("setupSession: null login", (logger.isDebugEnabled() ? new RuntimeException() : null));
            throw new IllegalArgumentException("null login");
        }
        UserService userService = ServletContextUtils.getItrackerServices().getUserService();
        User user = userService.getUserByLogin(login);
        if (user != null) {
            String encPassword = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (Constants.COOKIE_NAME.equals(cookie.getName())) {
                        int seperator = cookie.getValue().indexOf('~');
                        if (seperator > 0) {
                            encPassword = cookie.getValue().substring(
                                    seperator + 1);
                        }
                    }
                }
            }

            return setupSession(user, encPassword, request, response);
        }
        return null;
    }

    public static User setupSession(User user, String encPassword,
                                    HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            logger.warn("setupSession: null user", (logger.isDebugEnabled() ? new RuntimeException() : null));
            throw new IllegalArgumentException("null user");
        }

        UserService userService = ServletContextUtils.getItrackerServices().getUserService();

        if (logger.isDebugEnabled()) {
            logger.debug("Creating new session");
        }
        HttpSession session = request.getSession(true);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting session timeout to "
                    + getConfiguredSessionTimeout() + " minutes");
        }
        session.setMaxInactiveInterval(getConfiguredSessionTimeout() * 60);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting session tracker");
        }
        session.setAttribute(Constants.SESSION_TRACKER_KEY, new SessionTracker(
                user.getLogin(), session.getId()));

        if (logger.isDebugEnabled()) {
            logger.debug("Setting user information");
        }
        session.setAttribute(Constants.USER_KEY, user);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting preferences for user " + user.getLogin());
        }
        UserPreferences userPrefs = user.getPreferences();
        // TODO : this is a hack, remove when possible
        if (userPrefs == null) {
            logger.warn("setupSession: got user with no preferences!: " + user + " (prefs: " + user.getPreferences() + ")");
            userPrefs = new UserPreferences();
        }
        session.setAttribute(Constants.PREFERENCES_KEY, userPrefs);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting user " + user + " locale to " + ITrackerResources
                    .getLocale(userPrefs.getUserLocale()));
        }
        session.setAttribute(Constants.LOCALE_KEY, ITrackerResources
                .getLocale(userPrefs.getUserLocale()));

        // TODO: cookie could be removed
        Cookie cookie = new Cookie(Constants.COOKIE_NAME, "");
        cookie.setPath(request.getContextPath());

        cookie.setValue("");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        if (logger.isDebugEnabled()) {
            logger.debug("Setting permissions for user " + user.getLogin());
        }
        Map<Integer, Set<PermissionType>> usersMapOfProjectIdsAndSetOfPermissionTypes = userService
                .getUsersMapOfProjectIdsAndSetOfPermissionTypes(user,
                        AuthenticationConstants.REQ_SOURCE_WEB);
        session.setAttribute(Constants.PERMISSIONS_KEY,
                usersMapOfProjectIdsAndSetOfPermissionTypes);

        // Reset some session forms
        session.setAttribute(Constants.SEARCH_QUERY_KEY, null);

        SessionManager.clearSessionNeedsReset(user.getLogin());
        if (logger.isDebugEnabled()) {
            logger.debug("User session data updated.");
        }
        return user;
    }

    public static int getConfiguredSessionTimeout() {
        return (ServletContextUtils.getItrackerServices().getConfigurationService()
                .getIntegerProperty("web_session_timeout", DEFAULT_SESSION_TIMEOUT));
    }

    @Deprecated
    public static boolean hasPermission(int permissionNeeded,
                                        HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return hasPermission(PermissionType.valueOf(permissionNeeded), request, response);
    }
    public static boolean hasPermission(PermissionType permissionNeeded,
                                              HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        return hasPermission(new PermissionType[]{permissionNeeded}, request, response);

    }


    @Deprecated
    public static boolean hasPermission(PermissionType[] permissionsNeeded,
                                        HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            UserDetails user = LoginUtilities.getPrincipal();

            HttpSession session = request.getSession(false);
            Map<Integer, Set<PermissionType>> permissions = (session == null) ? null
                    : RequestHelper.getUserPermissions(session);
            return UserUtilities.hasPermission(permissions, permissionsNeeded);
        } catch (RuntimeException re) {
            logger.debug("hasPermission: failed to check permission", re);
            return false;
        }
    }

    /**
     * Returns true if the user has any of required permissions for the project.
     */
    public static boolean hasAnyPermission(Project project, PermissionType[] permissionsNeeded) {


        if (null == permissionsNeeded || permissionsNeeded.length == 0) {
            permissionsNeeded = PermissionType.values();
        }
        for (PermissionType permissionType: permissionsNeeded) {
            if (hasPermission(project, permissionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the user has all of required permissions for the project.
     */
    public static boolean hasPermission(Project project, PermissionType[] permissionsNeeded) {

        for (PermissionType permissionType: permissionsNeeded) {
            if (!hasPermission(project, permissionType)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns true if the user has all the required permissions.
     *
     * @param permissionNeeded the permission to check for
     */
    public static boolean hasPermission(PermissionType permissionNeeded) {
        return hasPermission(null, permissionNeeded);
    }
    /**
     * Returns true if the user has the required permission for the given project.
     *
     * @param project project to which permission is checked for
     * @param permissionNeeded the permission to check for
     */
    public static boolean hasPermission(Project project, PermissionType permissionNeeded) {
        UserDetails user = getPrincipal();
        if (null == user) {
            return false;
        }
        if (permissionNeeded != PermissionType.USER_ADMIN
                && hasPermission(PermissionType.USER_ADMIN)) {
            return true;
        } else if (null != project && permissionNeeded != PermissionType.PRODUCT_ADMIN
            && hasPermission(project, PermissionType.PRODUCT_ADMIN)) {
            return true;
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String permissionName = permissionNeeded.name(project);
        for (GrantedAuthority authority: authorities) {
            if (authority.getAuthority().equals(permissionName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns true if the user has permission to view the requested issue.
     *
     * @param issue       an IssueModel of the issue to check view permission for
     */
    public static boolean canViewIssue (Issue issue) {
        return  canViewIssue(issue, getPrincipal());
    }

    /**
     * Returns true if the user has permission to view the requested issue.
     *
     * @param issue       an IssueModel of the issue to check view permission for
     * @param user        the user principal of the user to check permission for
     */
    public static boolean canViewIssue (Issue issue, UserDetails user) {
        if (hasAnyPermission(issue.getProject(), new PermissionType[] {
                  PermissionType.PRODUCT_ADMIN, PermissionType.ISSUE_VIEW_ALL})) {
            return true;
        }

        // TODO option on project that all users can view own issues
        boolean canViewUsers = true;// hasPermission(issue.getProject(), PermissionType.ISSUE_VIEW_USERS);

        // I think owner & creator should always be able to view the issue
        // otherwise it makes no sense of creating the issue itself.
        // So put these checks before checking permissions for the whole project.
        if (canViewUsers && issue.getCreator().getLogin().equals(user.getUsername())) {
            if (logger.isInfoEnabled()) {
                logger.info("canViewIssue: issue: " + issue + ", user: " + user.getUsername()
                        + ", permission: is creator");
            }
            return true;
        }

        if (canViewUsers && issue.getOwner() != null) {
            if (issue.getOwner().getLogin().equals(user.getUsername())) {

                if (logger.isInfoEnabled()) {
                    logger.info("canViewIssue: issue: " + issue + ", user: "
                            + user.getUsername() + ", permission: is owner");
                }
                return true;
            }
        }
        return false;
    }

    public static boolean canEditIssue(Issue issue) {
        return canEditIssue(issue, getPrincipal());
    }

    public static boolean canEditIssue(Issue issue, UserDetails user) {
           if (issue == null ) {
               return false;
           }

            if (hasAnyPermission(issue.getProject(), new PermissionType[] {
                      PermissionType.ISSUE_EDIT_ALL})) {
                return true;
            }

           if (!hasPermission(issue.getProject(), PermissionType.ISSUE_EDIT_USERS)) {
               if (logger.isDebugEnabled()) {
                   logger.debug("canEditIssue: user " + user.getUsername()
                           + " has not permission  to edit issue " + issue.getId()
                           + ":" + PermissionType.ISSUE_EDIT_USERS);
               }
               return false;
           }

           if (issue.getCreator().getLogin().equals(user.getUsername())) {
               if (logger.isDebugEnabled()) {
                   logger.debug("canEditIssue: user " + user.getUsername()
                           + " is creator of issue " + issue.getId());
               }
               return true;
           }
           if (issue.getOwner() != null) {
               if (issue.getOwner().getLogin().equals(user.getUsername())) {
                   if (logger.isDebugEnabled()) {
                       logger.debug("canEditIssue: user " + user.getUsername()
                               + " is owner of issue " + issue.getId());
                   }
                   return true;
               }
           }

           if (logger.isDebugEnabled()) {
               logger.debug("canEditIssue: user " + user.getUsername()
                       + " could not match permission, denied");
           }
           return false;
       }
}
