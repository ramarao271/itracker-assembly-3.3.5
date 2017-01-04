package org.itracker.util;

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * Utilities class for naming
 *
 * @author ranks@rosa.com
 */
public class NamingUtilites {

    private static final Logger log = Logger.getLogger(NamingUtilites.class
            .getName());


    /**
     * Read a String value of any type of object
     *
     * @param ctx          -
     *                     Context for lookup
     * @param lookupName   -
     *                     lookup name
     * @param defaultValue -
     *                     default value
     */
    public static final String getStringValue(Context ctx, String lookupName,
                                              String defaultValue) {
        String value = null;
        Object val;
        if (log.isDebugEnabled()) {
            log.debug("getStringValue: look up " + lookupName + " in context "
                    + ctx + ", default: " + defaultValue);

        }


        if (null == ctx) {
            log.debug("getStringValue: creating new InitialContext");
            try {
                ctx = new InitialContext();
                if (log.isDebugEnabled()) {
                    log.debug("created new InitialContext: " + ctx);
                }
            } catch (NamingException e) {
                log.warn("getStringValue: failed to create InitialContext", e);
                if (log.isDebugEnabled())
                    log.debug("getStringValue: context was null, exception from new initial context caught", e);
            }
        }


        if (null != ctx) {


            val = lookup(ctx, lookupName);
            if (val instanceof String) {
                value = (String) val;
            } else {
                value = (null == val) ? null : String.valueOf(val);
            }

        }
        value = (null == value) ? defaultValue : value;
        if (log.isDebugEnabled()) {
            log.debug("getStringValue: returning '" + value + "' for " + lookupName);

        }

        return value;
    }

    /**
     * savely get object from naming context
     *
     * @return Object - value
     * @throws IllegalArgumentException -
     *                                  if any argument is null, or the lookup name was empty
     */
    public static Object lookup(Context ctx, String lookupName) {
        if (null == ctx) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (null == lookupName || lookupName.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "lookup name must not be empty, got: "
                            + ((null == lookupName) ? "<null>" : "'"
                            + lookupName + "'"));
        }
        try {
            return ctx.lookup(lookupName);
        } catch (NamingException e) {
            if (e instanceof NameNotFoundException) {
                if (log.isDebugEnabled()) {
                    log.debug("lookup: failed to lookup " + lookupName
                            + ": name not found");
                }
            } else {
                log.warn("lookup: failed to lookup " + lookupName
                        + " in context " + ctx, e);
            }
            return null;
        }
    }

    /**
     * get a default initial context
     *
     * @return initial context.
     * @throws IllegalStateException - if initial context was null
     */
    public static final Context getDefaultInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return new InitialContext(environment);
    }

    /**
     * get a default initial context
     *
     * @return initial context.
     * @throws IllegalStateException - if initial context was null
     */
    public static final Context getDefaultInitialContext() throws NamingException {
        return new InitialContext();
    }
}
