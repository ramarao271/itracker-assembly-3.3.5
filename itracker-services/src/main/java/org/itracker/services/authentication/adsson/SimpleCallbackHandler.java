/**
 * Originally contributed by eMation (www.emation.pt)
 */
package org.itracker.services.authentication.adsson;

import javax.security.auth.callback.*;

/**
 * Callback class for Active Directory authentication Gets username and password
 * from class constructor
 *
 * @author ricardo
 */
public class SimpleCallbackHandler implements CallbackHandler {

    private String username;

    private String password;

    public SimpleCallbackHandler(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws java.io.IOException, UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback cb = (NameCallback) callbacks[i];
                cb.setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback cb = (PasswordCallback) callbacks[i];
                cb.setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callbacks[i]);
            }
        }

    }
}
