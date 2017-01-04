/**
 * Originally contributed by eMation (www.emation.pt)
 */
package org.itracker.services.authentication.adsson;

import org.apache.log4j.Logger;
import org.itracker.model.User;
import org.itracker.services.exceptions.AuthenticatorException;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * Extends the windows single sign on class, gets user information
 * from active directory
 *
 * @author ricardo
 */
public class WindowsSSONAuthenticatorADInfo extends WindowsSSONAuthenticator {
    private static final Logger logger = Logger.getLogger(WindowsSSONAuthenticatorADInfo.class);

    /**
     * @see com.emation.itracker.authentication.WindowsSSONAuthenticator#getExternalUserInfo(java.lang.String)
     */
    protected User getExternalUserInfo(String login) throws AuthenticatorException {
        try {
            // connect to active directory
            ADIntegration ad = new ADIntegration();
            ad.login();
            // get external user info
            User userModel = (User) ad.getUserInfo(login);
            return userModel;
        } catch (LoginException e) {
            logger.error("getExternalUserInfo: " + e.getMessage() + AuthenticatorException.SYSTEM_ERROR);
            throw new AuthenticatorException("Error accessing Active Directory: " + e.getMessage(), AuthenticatorException.SYSTEM_ERROR, e);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new AuthenticatorException(e.getMessage(), AuthenticatorException.SYSTEM_ERROR);
        }
    }

}
