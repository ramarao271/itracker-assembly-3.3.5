package org.itracker.web.filters;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.PermissionType;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ITrackerServices;
import org.itracker.web.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author ranks
 */
public class ExecuteAlwaysFilter implements Filter {

    /**
     * Logger for ExecuteAlwaysFilter
     */
    private static final Logger log = LoggerFactory
            .getLogger(ExecuteAlwaysFilter.class);

    private ITrackerServices iTrackerServices;


    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (!(servletRequest instanceof HttpServletRequest)) {
            RuntimeException re = new IllegalArgumentException(
                    "Unsupported servlet-request of type: "
                            + servletRequest.getClass().getName());
            log.error("doFilter: failed, invalid request type", re);
            throw re;
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String path = request.getRequestURI().substring(
                request.getContextPath().length());
        if (log.isDebugEnabled()) {
            log.debug("doFilter: called with path " + path);
        }

        // From IrackerBaseAction.executeAlways
        if (log.isDebugEnabled()) {
            log.debug("doFilter: setting the common request attributes, (coming from the former header.jsp)");
        }

        setupCommonReqAttributes(request, ServletContextUtils.getItrackerServices().getConfigurationService());
        if (SessionManager.getSessionNeedsReset(request.getRemoteUser())) {
            // logout and go to login
            request.getSession().invalidate();
            ((HttpServletResponse) response).sendRedirect(request.getContextPath());
        }

        setupCommonReqAttributesEx(request);

        try {
            log.debug("doFilter: executing chain..");

            chain.doFilter(request, response);

            log.debug("doFilter: completed chain execution.");

        } catch (RuntimeException e) {
            log.error(
                    "doFilter: failed to execute chain with runtime exception: {}",
                            e.getMessage(), e);
            handleError(e, request, response);

        } catch (IOException ioe) {
            log.error("doFilter: failed to execute chain with i/o exception: {}",
                    ioe.getMessage(), ioe);
            handleError(ioe, request, response);

        } catch (ServletException se) {
            log.error(
                    "doFilter: failed to execute chain with servlet exception: "
                            + se.getMessage(), se);
            handleError(se, request, response);

        } catch (Error err) {
            log.error("doFilter: caught fatal error executing filter chain",
                    err);
            throw err;
        }
    }

    private static void handleError(Throwable error, ServletRequest request, ServletResponse response) throws ServletException {

        if (null == error) {
            log.debug("handleError: called with null throwable");
            throw new IllegalArgumentException("null error");
        }

        log.debug("handleError: called with " + error.getClass().getSimpleName(), error);

        if (!(response instanceof HttpServletResponse) || !(request instanceof HttpServletRequest)) {
            log.error("handleError: unknown request/response: " + request + ", " + response, error);
            throw new ServletException(error.getMessage(), error);
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message",
                new Object[]{error.getLocalizedMessage() == null ? error.getMessage() : error.getLocalizedMessage(),
                        error.getClass().getCanonicalName()}));

        saveErrors((HttpServletRequest) request, errors);
        try {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/error.do");
        } catch (IOException e) {
            log.error("handleError: failed to redirect to error-page", e);
        }
    }

    /**
     * <p>Save the specified error messages keys into the appropriate request
     * attribute for use by the &lt;html:errors&gt; tag, if any messages
     * are required. Otherwise, ensure that the request attribute is not
     * created.</p>
     *
     * @param request The servlet request we are processing
     * @param errors  Error messages object
     * @since Struts 1.2
     */
    protected static void saveErrors(HttpServletRequest request, ActionMessages errors) {

        // Remove any error messages attribute if none are required
        if ((errors == null) || errors.isEmpty()) {
            request.removeAttribute(Globals.ERROR_KEY);
            request.getSession().removeAttribute(Globals.ERROR_KEY);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("saveErrors: saved errors: {}", errors);
        }
        // Save the error messages we need
        request.setAttribute(Globals.ERROR_KEY, errors);

        request.getSession().setAttribute(Globals.ERROR_KEY, errors);
    }


    private static void setupCommonReqAttributes(
            HttpServletRequest request,
            ConfigurationService configurationService) {
        boolean allowForgotPassword;
        boolean allowSelfRegister;
        boolean allowSaveLogin;
        String siteTitle;
        String siteLogo;

        allowForgotPassword = configurationService.getBooleanProperty(
                "allow_forgot_password", true);
        allowSelfRegister = configurationService.getBooleanProperty(
                "allow_self_register", false);
        allowSaveLogin = configurationService.getBooleanProperty(
                "allow_save_login", true);
        siteTitle = configurationService
                        .getProperty("site_title", "itracker.org");
        siteLogo = configurationService
                .getProperty("site_logo", null);

        Locale locale = LoginUtilities.getCurrentLocale(request);

        // TODO: this should be configured per-instance. Request server-name
        // should only be used for exception and logged (configuration not
        // found!)

        String baseURL = configurationService.getSystemBaseURL();
        if (null == baseURL) {
            baseURL = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + request.getContextPath();
            log.warn("setupCommonReqAttributes: not found system_base_url configuration, setting from request: " + baseURL);
        }
        request.setAttribute("allowForgotPassword", allowForgotPassword);
        request.setAttribute("allowSelfRegister", allowSelfRegister);
        request.setAttribute("allowSaveLogin", allowSaveLogin);
        request.setAttribute("siteLogo", siteLogo);
        request.setAttribute("siteTitle", siteTitle);
        request.setAttribute("baseURL", baseURL);
        // TODO: remove deprecated currLocale attribute
        request.setAttribute("currLocale", locale);


        request.setAttribute("locales", configurationService.getAvailableLanguages());
        request.setAttribute(Constants.LOCALE_KEY, locale);

        request.setAttribute("contextPath", request.getContextPath());

        request.setAttribute("currentDate", new java.util.Date());
        request.setAttribute("currentVersion", configurationService.getProperty("version", "Unknown"));


    }

   /**
    * @deprecated this should not be necessary anymore (default.header.jsp).
    * @param request
    */
   @Deprecated
    private static void setupCommonReqAttributesEx(HttpServletRequest request) {
        final Map<Integer, Set<PermissionType>> permissions = RequestHelper
                .getUserPermissions(request.getSession());
        request.setAttribute("hasPermissionUserAdmin", UserUtilities.hasPermission(permissions,
                PermissionType.USER_ADMIN));
        request.setAttribute("hasPermissionProductAdmin", UserUtilities.hasPermission(permissions,
                PermissionType.PRODUCT_ADMIN));
        request.setAttribute("hasPermissionViewAll",
                UserUtilities.hasPermission(permissions,
                        PermissionType.ISSUE_VIEW_ALL));
    }

    /**
     *
     */
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public ITrackerServices getITrackerServices() {
        if (null == this.iTrackerServices) {
            this.iTrackerServices = ServletContextUtils.getItrackerServices();
        }
        return iTrackerServices;
    }

}
