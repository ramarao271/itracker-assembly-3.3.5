/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.model.util;

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.NameValuePair;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SystemConfigurationUtilities {

    private static final Logger log = Logger.getLogger(SystemConfigurationUtilities.class);


    @Deprecated
    public static final int TYPE_INITIALIZED = Configuration.Type.initialized.getCode();
    @Deprecated
    public static final int TYPE_LOCALE = Configuration.Type.locale.getCode();
    @Deprecated
    public static final int TYPE_STATUS = Configuration.Type.status.getCode();
    @Deprecated
    public static final int TYPE_SEVERITY = Configuration.Type.severity.getCode();
    @Deprecated
    public static final int TYPE_RESOLUTION = Configuration.Type.resolution.getCode();
    @Deprecated
    public static final int TYPE_CUSTOMFIELD = Configuration.Type.customfield.getCode();

    @Deprecated
    public static final int ACTION_CREATE = 1;
    @Deprecated
    public static final int ACTION_UPDATE = 2;
    @Deprecated
    public static final int ACTION_REMOVE = 3;

    public static final int LOCALE_TYPE_INVALID = -1;
    public static final int LOCALE_TYPE_BASE = 1;
    public static final int LOCALE_TYPE_LANGUAGE = 2;
    public static final int LOCALE_TYPE_LOCALE = 3;

    /**
     * Returns the key for a particular configuration item. This is made up of a
     * static part based on the type of configuration item, and the unique value
     * of the configuration item.
     *
     * @param configuration the Configuration to return the key for
     * @return the key for the item
     */
    public static String getLanguageKey(Configuration configuration) {
        if (configuration != null) {
            return configuration.getType().getLanguageKey(configuration);
        }
        return "";
    }

    @Deprecated
    public static String getTypeName(int type) {

        return Configuration.Type.valueOf(type).name();

    }


    public static String getTypeLanguageKey(Configuration configuration) {
        return configuration.getType().getTypeLanguageKey();
    }


    public static long getVersionAsLong(String version) {
        long versionNumber = 0;
        if (log.isDebugEnabled()) {
            log.debug("getVersionAsLong: transforming " + version);
        }
        if (version != null) {
            if ("0".equals(version)) {
                return 0;
            }
            // support -SNAPSHOT versions
            version = version.split("-")[0];
            StringTokenizer token = new StringTokenizer(version, ".");
            try {
                if (token.countTokens() > 0 && token.countTokens() <= 3) {
                    versionNumber += 1000000L * Integer.parseInt(token
                            .nextToken());
                    versionNumber += 1000L * Integer
                            .parseInt(token.nextToken());
                    versionNumber += Integer.parseInt(token.nextToken());
                } else {
                    throw new IllegalArgumentException("The version " + version + " is not parseable, excpected '(int)number[.(int)major[.(int)minor]]");
                }
            } catch (NumberFormatException nfe) {
                // OK
            } catch (NoSuchElementException nsee) {
                // OK
            }

        }

        if (log.isDebugEnabled()) {
            log.debug("getVersionAsLong: returning " + versionNumber);
        }
        return versionNumber;
    }

    public static int getLocaleType(String locale) {
        if (locale == null || locale.equals("")) {
            return LOCALE_TYPE_INVALID;
        }

        if (ITrackerResources.BASE_LOCALE.equalsIgnoreCase(locale)) {
            return LOCALE_TYPE_BASE;
        } else if (locale.length() == 5 && locale.indexOf('_') == 2) {
            return LOCALE_TYPE_LOCALE;
        } else if (locale.length() == 2) {
            return LOCALE_TYPE_LANGUAGE;
        } else {
            return LOCALE_TYPE_INVALID;
        }
    }

    public static String getLocalePart(String locale, int partType) {
        if (locale == null || partType == LOCALE_TYPE_INVALID) {
            return null;
        }

        if (partType == LOCALE_TYPE_LOCALE && locale.length() == 5
                && locale.indexOf('_') == 2) {
            return locale;
        } else if (partType == LOCALE_TYPE_LANGUAGE && locale.length() == 5
                && locale.indexOf('_') == 2) {
            return locale.substring(0, 2);
        } else if (partType == LOCALE_TYPE_LANGUAGE && locale.length() == 2) {
            return locale;
        } else if (partType == LOCALE_TYPE_BASE) {
            return ITrackerResources.BASE_LOCALE;
        }

        return null;
    }

    public static Configuration[] nvpArrayToConfigurationArray(Configuration.Type configType,
                                                               NameValuePair[] names) {
        if (names == null) {
            return new Configuration[0];
        }

        Configuration[] configModels = new Configuration[names.length];
        for (int i = 0; i < names.length; i++) {
            configModels[i] = new Configuration(configType, names[i]);
        }

        return configModels;
    }
}
