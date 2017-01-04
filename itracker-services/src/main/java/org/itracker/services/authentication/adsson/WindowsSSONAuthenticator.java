/**
 * Originally contributed by eMation (www.emation.pt)
 */
package org.itracker.services.authentication.adsson;

import org.apache.log4j.Logger;
import org.itracker.model.User;
import org.itracker.UserException;
import org.itracker.model.UserPreferences;
import org.itracker.services.UserService;
import org.itracker.services.authentication.DefaultAuthenticator;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.model.util.UserUtilities;

import javax.servlet.http.HttpServletRequest;
import java.rmi.RemoteException;
import java.util.Date;


/**
 * Single Sign On class with Windows
 * <p/>
 * Gets an authentication from jcifs web filter, gets user information from
 * active directory, creates or updates the user with that information if needed
 *
 * @author Ricardo Trindade (ricardo.trindade@emation.pt)
 */
public abstract class WindowsSSONAuthenticator extends DefaultAuthenticator {

    private static final Logger logger = Logger.getLogger(WindowsSSONAuthenticator.class);

    private static String TEMPLATE_USER = "TemplateUser";

    /**
     * @see org.itracker.ejb.authentication.AbstractPluggableAuthenticator#checkLogin(java.lang.String,
     *      java.lang.Object, int, int)
     */
    public User checkLogin(String login, Object authentication, int authType, int reqSource)
            throws AuthenticatorException {
        User userModel;
        try {
            // this authenticator only handles authType=AUTH_TYPE_REQUEST
            // (HttpServletRequest)
            if (authType != AUTH_TYPE_REQUEST || !(authentication instanceof HttpServletRequest)) {
                logger.error("Only http request authentication supported by this single sign on class. Received "
                        + authType);
                throw new AuthenticatorException(
                        "Only http request authentication supported by this single sign on class",
                        AuthenticatorException.INVALID_AUTHENTICATION_TYPE);
            }
            UserService userService = getUserService();
            // validate we're really using jcifs, and we
            // have a valid authentication object
            // TODO: get user from jcifs
            String theLogin = ((HttpServletRequest) authentication).getRemoteUser();

            if (theLogin == null) {
                throw new AuthenticatorException("User obtained from jcifs is null. Check that jcifs is active",
                        AuthenticatorException.CUSTOM_ERROR);
            }

            // sometimes jcifs sends the username in the form of DOMAIN\USER
            if (theLogin.indexOf("\\") > 0) {
                theLogin = theLogin.substring(theLogin.indexOf("\\") + 1);
            }
            if (!theLogin.equals(login)) {
                // should an exception be thrown here?
                AuthenticatorException ex = new AuthenticatorException("User obtained from authenticator does not match, got " + theLogin + ", expected " + login + ".",
                        AuthenticatorException.CUSTOM_ERROR);
                logger.warn("checkLogin: checking login for " + login + " but got " + theLogin + " in authentication " + authentication, ex);
                throw ex;
            }

            userModel = updateOrCreateUser(theLogin, userService);
            return userModel;
        } catch (RemoteException ex) {
            logger.error("pt_PT", ex);
            throw new AuthenticatorException(ex.getMessage(), AuthenticatorException.SYSTEM_ERROR, ex);
        } catch (UserException ex) {
            logger.error("pt_PT", ex);
            throw new AuthenticatorException(ex.getMessage(), AuthenticatorException.SYSTEM_ERROR, ex);
        } catch (AuthenticatorException ex) {
            logger.error("pt_PT", ex);
            throw new AuthenticatorException(ex.getMessage(), AuthenticatorException.SYSTEM_ERROR, ex);
        }
    }

    /**
     * Checks if the user needs creating or updating, and if so, do it
     */
    private User updateOrCreateUser(String login, UserService userService) throws RemoteException, UserException,
            AuthenticatorException {
        User userModel;
        // check if the user already exists in ITracker
        // if he already exists, and needs updating, update him
        // if not, create him
        userModel = userService.getUserByLogin(login);
        if (null == userModel) {
            userModel = createUser(login, userService);
        } else {
            // user exists, update if needed
            // get user info from authentication source
            if (needsUpdate(userModel, getExternalUserInfo(login))) {
                // update user here...
                // userService.updateUser();
                // get updated version
                userModel = userService.getUserByLogin(login);
                userModel = updateUser(userModel, getExternalUserInfo(login));
                userService.updateUser(userModel);
            }
        }
        return userModel;
    }

    /**
     * Updates parts of profile that are obtained from external source
     */
    private User updateUser(User oldUserModel, User newUserModel) {
        oldUserModel.setEmail(newUserModel.getEmail());
        oldUserModel.setFirstName(newUserModel.getFirstName());
        oldUserModel.setLastName(newUserModel.getLastName());
        oldUserModel.setLastModifiedDate(new Date());
        oldUserModel.setSuperUser(newUserModel.isSuperUser());
        return (oldUserModel);
    }

    /**
     * Create a user in the ITracker database
     */
    private User createUser(String login, UserService userService) throws RemoteException, UserException,
            AuthenticatorException {

        // doesn't exist, create
        User userModel = getExternalUserInfo(login);
        userModel.setRegistrationType(UserUtilities.REGISTRATION_TYPE_ADMIN);
        userModel.setStatus(UserUtilities.STATUS_ACTIVE);
        userModel = userService.createUser(userModel);
        // if this user is a super user, there is no need to set default
        // permissions
        // if not, set default permissions
        if (!userModel.isSuperUser()) {
            setDefaultPermissions(userModel, userService);
        }

        return userModel;
    }

    /**
     * Set the default user permissions
     * <p/>
     * Default user permissions are the same as those of a user called
     * "TemplateUser"
     */
    private void setDefaultPermissions(User userModel, UserService userService) throws RemoteException,
            AuthenticatorException, UserException {

        User templateUser = userService.getUserByLogin(TEMPLATE_USER);
        if (templateUser == null) {
            String errorMessage = "TemplateUser not found. Create a user called template user, new permissions are copied from him to new users";
            logger.error(errorMessage);
            throw new AuthenticatorException(errorMessage, AuthenticatorException.CUSTOM_ERROR);
        }
        // set permissions
        userService.setUserPermissions(userModel.getId(), userService.getPermissionsByUserId(templateUser.getId()));
        // set preferences
        UserPreferences preferences = templateUser.getPreferences();
        preferences.setUser(userModel);
        userService.updateUserPreferences(preferences);
    }

    /**
     * Checks if a given internal user needs updating, by comparing him with the
     * external user data source
     *
     * @param localUser  The local User
     * @param remoteUser The remote User
     * @return true if the user needs updating, false otherwise
     */
    private boolean needsUpdate(User localUser, User remoteUser) {
        if (!(localUser.getEmail().equals(remoteUser.getEmail())))
            return true;
        if (!(localUser.getFirstName().equals(remoteUser.getFirstName())))
            return true;
        if (!(localUser.getLastName().equals(remoteUser.getLastName())))
            return true;
        if (localUser.isSuperUser() != remoteUser.isSuperUser())
            return true;
        return (false);
    }

    protected abstract User getExternalUserInfo(String login) throws AuthenticatorException;

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
