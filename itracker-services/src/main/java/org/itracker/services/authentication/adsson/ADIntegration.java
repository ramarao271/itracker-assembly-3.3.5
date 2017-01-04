/**
 * Originally contributed by eMation (www.emation.pt)
 */
package org.itracker.services.authentication.adsson;

import org.apache.log4j.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Properties;

/**
 * Performs a kerberos authenticated search in AD
 *
 * @author ricardo
 */
public class ADIntegration {

    private static final String AD_AUTH_PROPERTIES_FILE = "adauth.properties";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String BASE_BRANCH = "basebranch";
    private static final String PROVIDER_URL = "url";

    private final Logger logger;
    private LoginContext lc = null;
    private Properties adAuth;

    public ADIntegration() throws IOException {
        this.logger = Logger.getLogger(getClass());
        adAuth = new Properties();
        InputStream is = getClass().getResourceAsStream("/" + AD_AUTH_PROPERTIES_FILE);
        if (is == null) {
            String message = "Can't find " + AD_AUTH_PROPERTIES_FILE + " to get A.D. auth properties. This file should be in the root of your classpath or EAR file";
            logger.error(message);
            throw new IOException(message);
        }
        adAuth.load(is);
    }

    public void login() throws LoginException {
        try {
            // 1. Log in (to Kerberos)
            // The login context should be configured in login-config.xml
            lc = new LoginContext("Helpdesk", new SimpleCallbackHandler(getUsername(), getPassword()));
            // Attempt authentication
            // You might want to do this in a "for" loop to give
            // user more than one chance to enter correct username/password
            lc.login();
        } catch (IOException e) {
            throw new LoginException(e.getMessage());
        }
    }

    public Object getUserInfo(String login) throws AccessControlException {
        // 2. Perform JNDI work as logged in subject
        Object userInfo = Subject.doAs(lc.getSubject(), new GetUserModelFromADPrivilegedAction(login, getBaseBranch(), getProviderUrl()));

        if (userInfo == null) {
            logger.error("Can't get info on " + login + " from A.D.");
            throw new AccessControlException("Can't get info on " + login + " from A.D.");
        }

        return (userInfo);
    }

    /**
     * @return
     */
    private String getProviderUrl() {
        return (adAuth.getProperty(PROVIDER_URL));
    }

    /**
     * @return
     */
    private String getPassword() throws IOException {
        return (adAuth.getProperty(PASSWORD));
    }

    /**
     * @return
     */
    private String getUsername() throws IOException {
        return (adAuth.getProperty(USERNAME));
    }

    /**
     * @return
     */
    private String getBaseBranch() {
        return (adAuth.getProperty(BASE_BRANCH));
    }
}
