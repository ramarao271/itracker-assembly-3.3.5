package org.itracker.web.util;

import org.itracker.model.NameValuePair;
import org.itracker.model.PermissionType;
import org.itracker.model.User;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class with utility methods to help with request handling in Action
 * or JSP pages.
 *
 * @author johnny
 */
public final class RequestHelper {

    /**
     * Creates a new instance of RequestUtils
     */
    private RequestHelper() {
    }

    /**
     * Returns the map of a permissions by project id.
     */
    @SuppressWarnings("unchecked")
    public static Map<Integer, Set<PermissionType>>
    getUserPermissions(HttpSession session) {

        return (Map<Integer, Set<PermissionType>>)
                session.getAttribute(Constants.PERMISSIONS_KEY);
    }

    public static User getCurrentUser(HttpSession session) {

        return (User) session.getAttribute(Constants.USER_KEY);
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, List<NameValuePair>>
    getListOptions(HttpSession session) {
        return (Map<Integer, List<NameValuePair>>)
                session.getAttribute(Constants.LIST_OPTIONS_KEY);
    }

}
