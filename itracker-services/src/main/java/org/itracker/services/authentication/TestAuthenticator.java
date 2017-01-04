package org.itracker.services.authentication;

import org.itracker.model.User;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;


/**
 * An authenticator that always returns the admin user.
 * Mainly for testing
 *
 * @author Ricardo Trindade (ricardo.trindade@emation.pt)
 */
public class TestAuthenticator extends DefaultAuthenticator {

    /**
     * @see org.itracker.ejb.authentication.AbstractPluggableAuthenticator#checkLogin(java.lang.String,
     *      java.lang.Object, int, int)
     */
    public User checkLogin(String login, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {

        UserService userService = getUserService();
        return (userService.getUserByLogin("admin"));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.itracker.ejb.authentication.AbstractPluggableAuthenticator#allowProfileUpdates(org.itracker.model.deprecatedmodels.User,
     *      java.lang.Object, int, int)
     */
    public boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.itracker.ejb.authentication.AbstractPluggableAuthenticator#allowPasswordUpdates(org.itracker.model.deprecatedmodels.User,
     *      java.lang.Object, int, int)
     */
    public boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        return false;
    }

}
