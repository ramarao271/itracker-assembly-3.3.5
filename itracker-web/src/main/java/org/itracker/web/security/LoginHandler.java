package org.itracker.web.security;

import org.apache.commons.lang.StringUtils;
import org.itracker.core.AuthenticationConstants;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.model.UserPreferences;
import org.itracker.services.UserService;
import org.itracker.web.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class LoginHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String redirectUrl = "/";
    private boolean isAutologinSuccessHandler = false;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        UserService userService = ServletContextUtils.getItrackerServices().getUserService();

        log.debug("Creating new session");

        HttpSession session = request.getSession(true);

        if (log.isDebugEnabled()) {
            log.debug("Setting session timeout to "
                    + LoginUtilities.getConfiguredSessionTimeout() + " minutes");
        }
        session.setMaxInactiveInterval(LoginUtilities.getConfiguredSessionTimeout() * 60);

        if (log.isDebugEnabled()) {
            log.debug("Setting session tracker");
        }
        session.setAttribute(Constants.SESSION_TRACKER_KEY, new SessionTracker(
                request.getRemoteUser(), session.getId()));

        log.debug("Setting user information");

        final User user = userService.getUserByLogin(authentication.getName());

        session.setAttribute(Constants.USER_KEY, user);
        session.setAttribute("userDN", getDisplayName(user));

        log.debug("Setting preferences for user {}", user.getLogin());

        UserPreferences userPrefs = user.getPreferences();

        session.setAttribute(Constants.PREFERENCES_KEY, userPrefs);

        if (log.isDebugEnabled()) {
            log.debug("Setting user " + user + " locale to " + ITrackerResources
                    .getLocale(userPrefs.getUserLocale()));
        }
        session.setAttribute(Constants.LOCALE_KEY, ITrackerResources
                .getLocale(userPrefs.getUserLocale()));

        if (log.isDebugEnabled()) {
            log.debug("Setting autologin cookie for user " + user.getLogin());
        }

        log.debug("Setting permissions for user {}", user.getLogin());
        Map<Integer, Set<PermissionType>> usersMapOfProjectIdsAndSetOfPermissionTypes = userService
                .getUsersMapOfProjectIdsAndSetOfPermissionTypes(user,
                        AuthenticationConstants.REQ_SOURCE_WEB);
        session.setAttribute(Constants.PERMISSIONS_KEY,
                usersMapOfProjectIdsAndSetOfPermissionTypes);

        // Reset some session forms
        session.setAttribute(Constants.SEARCH_QUERY_KEY, null);

        SessionManager.clearSessionNeedsReset(user.getLogin());
        log.debug("User session data updated.");

        SessionManager.createSession(user.getLogin());

        redirectToOnLoginSuccess(request, response, getRedirectStrategy());

    }

    private Object getDisplayName(User user) {
        StringBuilder sb =
                new StringBuilder(StringUtils.defaultString(user.getFirstName()));
        if (sb.length() > 0)
            sb.append(' ');
        sb.append(StringUtils.defaultString(user.getLastName()));
        if (sb.length() == 1) {
            return user.getLogin();
        }
        return sb.toString();
    }

    public void redirectToOnLoginSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                RedirectStrategy redirectStrategy) throws IOException {

        final String path = getRedirectUrl(request);

        redirectStrategy.sendRedirect(request, response, path);
    }
    protected SavedRequest getRequest(HttpServletRequest currentRequest) {
        HttpSession session = currentRequest.getSession(false);

        if (session != null) {
            return (DefaultSavedRequest) session.getAttribute(SAVED_REQUEST);
        }

        return null;
    }
    protected String getRedirectUrl(HttpServletRequest request) {

        if (isAutologinSuccessHandler()) {

            return StringUtils.defaultString(request.getServletPath());
        }

        SavedRequest savedRequest = getRequest(request);
        if (getRequest(request) != null) {
            return savedRequest.getRedirectUrl();
        }


        /* return a sane default in case data isn't there */
        return getRedirectUrl();
    }
    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public boolean isAutologinSuccessHandler() {
        return isAutologinSuccessHandler;
    }

    public void setIsAutologinSuccessHandler(boolean isAutologinSuccessHandler) {
        this.isAutologinSuccessHandler = isAutologinSuccessHandler;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
