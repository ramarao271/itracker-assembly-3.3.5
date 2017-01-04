/**
 * Originally contributed by eMation (www.emation.pt)
 */
package org.itracker.services.authentication.adsson;

import org.apache.log4j.Logger;
import org.itracker.model.User;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.*;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;

//TODO: Add Javadocs here

/**
 * @author ricardo
 */
public class GetUserModelFromADPrivilegedAction implements PrivilegedAction<Object> {

    private static String ITRACKER_SUPER_USERS_GROUP = "ITracker Super Users";

    private final Logger logger;
    private String login;
    private String providerUrl;
    private String baseBranch;

    public GetUserModelFromADPrivilegedAction(String login, String baseBranch, String providerUrl) {
        this.logger = Logger.getLogger(getClass());
        this.login = login;
        this.providerUrl = providerUrl;
        this.baseBranch = baseBranch;
    }

    public Object run() {
        try {
            return getUserInfo(login);
        } catch (NamingException e) {
            logger.error(e.getMessage());
            return (null);
        }
    }

    private User getUserInfo(String login) throws NamingException {
        // Set up environment for creating initial context
        Hashtable<String, String> env = new Hashtable<String, String>(11);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        // Must use fully qualified hostname
        env.put(Context.PROVIDER_URL, providerUrl);
        // Request the use of the "GSSAPI" SASL mechanism
        // Authenticate by using already established Kerberos credentials
        env.put(Context.SECURITY_AUTHENTICATION, "GSSAPI");

        /* Create initial context */
        DirContext ctx = new InitialDirContext(env);
        // do something useful with ctx
        SearchControls sc = new SearchControls();
        sc.setCountLimit(1);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(&(objectclass=user)(sAMAccountName=" + login + "))";
        NamingEnumeration<?> answer = ctx.search(baseBranch, filter, sc);

        if (!answer.hasMoreElements()) {
            logger.error("A.D. had no info on " + login);
            return (null);
        }

        SearchResult sr;
        try {
            sr = (SearchResult) answer.next();
            logger.info("A.D. had info on " + login);
        } catch (PartialResultException e) {
            logger.error("A.D. had no info on " + login);
            return (null);
        }

        Attributes attributes = sr.getAttributes();
        String mail = "";
        String firstName = "";
        String lastName = "";

        // check that properties are present
        // active directory sometimes doesn't have "mail"
        if ((attributes.get("givenName") == null) || (attributes.get("sn") == null)) {
            logger.error("AD didn't return proper attributes. Check that it has at least [mail, givenName , sn]");
            return (null);
        }

        if (attributes.get("mail") != null) {
            mail = (String) attributes.get("Mail").get();
        }
        if (attributes.get("givenName") != null)
            firstName = (String) attributes.get("givenName").get();
        if (attributes.get("sn") != null) {
            lastName = (String) attributes.get("sn").get();
        }
        logger.info("Got at least givenName and sn from A.D. for user " + login);

        // create user 
        User user = new User();

        user.setEmail(mail);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setLogin(login);
        user.setPassword("notused=");

        // if user belongs to "ITracker Super Users" group
        // make him a super user
        user.setSuperUser(false);

        logger.info("About to check if user " + login + " is a super user");
        logger.debug("User attributes for user " + login + " " + attributes);
        if (attributes.get("memberOf") != null) {
            for (Enumeration<?> groups = attributes.get("memberOf").getAll(); groups.hasMoreElements(); ) {
                String group = (String) groups.nextElement();
                logger.info(login + " belongs to NT Group " + group);
                if (group.indexOf(ITRACKER_SUPER_USERS_GROUP) > 0) {
                    user.setSuperUser(true);
                    logger.info("User " + user.getLogin() + " was made an administrator ");
                }
            }
        } else {
            logger.info("User attributes didn't contain memberOf...Looks like the A.D. user you specified in the adauth.properties properties file doesn't have enough permissions to check group membership for other users. Give that user enough privileges to read the memberOf attribute from A.D.");
        }

        if (user.isSuperUser()) {
            logger.info(login + " is a super user");
        } else {
            logger.info(login + " is not a super user");
        }

        ctx.close();

        return user;
    }
}