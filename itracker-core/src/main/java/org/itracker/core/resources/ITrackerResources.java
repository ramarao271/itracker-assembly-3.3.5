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

package org.itracker.core.resources;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.itracker.ITrackerDirtyResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Please comment this class here. What is it for?
 *
 * @author ready
 */
public class ITrackerResources {

    private static final Logger logger = LoggerFactory
            .getLogger(ITrackerResources.class);

    public static final String RESOURCE_BUNDLE_NAME = "org.itracker.core.resources.ITracker";

    public static final String DEFAULT_LOCALE = "en_US";

    public static final String BASE_LOCALE = "BASE";

    public static final String KEY_BASE_CUSTOMFIELD_TYPE = "itracker.web.generic.";

    public static final String KEY_BASE_WORKFLOW_EVENT = "itracker.workflow.field.event.";

    public static final String KEY_BASE_PROJECT_STATUS = "itracker.project.status.";

    public static final String KEY_BASE_PERMISSION = "itracker.user.permission.";

    public static final String KEY_BASE_PRIORITY = "itracker.script.priority.";

    public static final String KEY_BASE_PRIORITY_LABEL = ".label";

    public static final String KEY_BASE_PRIORITY_SIZE = "size";

    public static final String KEY_BASE_RESOLUTION = "itracker.resolution.";

    public static final String KEY_BASE_ISSUE_RELATION = "itracker.issuerelation.";

    public static final String KEY_BASE_SEVERITY = "itracker.severity.";

    public static final String KEY_BASE_STATUS = "itracker.status.";

    public static final String KEY_BASE_USER_STATUS = "itracker.user.status.";

    public static final String KEY_BASE_CUSTOMFIELD = "itracker.customfield.";

    public static final String KEY_BASE_CUSTOMFIELD_OPTION = ".option.";

    public static final String KEY_BASE_CUSTOMFIELD_LABEL = ".label";

    public static final String KEY_BASE_LOCALE_NAME = "itracker.locale.name";

    private static String defaultLocale = DEFAULT_LOCALE;

    private static HashMap<String, Locale> locales = new HashMap<String, Locale>();

    private static HashMap<Locale, ResourceBundle> languages = new HashMap<Locale, ResourceBundle>();

    private static ITrackerResourcesProvider configurationService;
    private static Collection<String> availableLocales;

    private static boolean initialized = false;

    private static final Object bundleLock = new Object();


    public static Locale getLocale() {
        return getLocale(getDefaultLocale());
    }

    public static Locale getLocale(String localeString) {

        if (localeString == null || localeString.trim().equals("")) {
            return getLocale(getDefaultLocale());
        }

        Locale locale = locales.get(localeString);
        if (locale == null
                && !StringUtils.isEmpty(localeString)) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating new locale for '" + localeString
                            + "'");
                }
                if (localeString.length() == 5) {
                    locale = new Locale(localeString.substring(0, 2),
                            localeString.substring(3));
                } else if (localeString.length() == 2) {
                    locale = new Locale(localeString, "");
                } else if (localeString.equals(BASE_LOCALE)) {
                    locale = new Locale("", "");
                } else {

                    logger
                            .error("Invalid locale '"
                                    + localeString
                                    + "' specified.  It must be either LN or LN_CN.");
                    throw new Exception("Invalid locale string");
                }
            } catch (Exception ex) {
                if (!localeString.equals(getDefaultLocale())) {
                    logger.error("Failed creating new locale for '"
                            + localeString
                            + "' attempting for default locale '"
                            + getDefaultLocale() + "'", ex);
                    return getLocale(getDefaultLocale());
                } else {
                    logger.error("Failed creating new default locale for '"
                            + getDefaultLocale()
                            + "' attempting for DEFAULT_LOCALE '"
                            + DEFAULT_LOCALE + "'", ex);
                    return getLocale(DEFAULT_LOCALE);
                }
            }
            locales.put(localeString, locale);
        }
        return locale;
    }

    public static String getDefaultLocale() {
        return (defaultLocale == null ? DEFAULT_LOCALE : defaultLocale);
    }

    private static void setDefaultLocale(String value) {
        defaultLocale = value;
    }

    public static String getLocaleDN(String locale, Locale displayLocale) {
        String name;
        if (null == displayLocale) {
            displayLocale = getLocale();
        }
        try {
            name = getBundle(displayLocale).getString(
                    KEY_BASE_LOCALE_NAME + "." + locale);
        } catch (RuntimeException e) {
            name = getLocaleNativeName(getLocale(locale));
        }

        return name;
    }

    public static String getLocaleDN(Locale locale, Locale displayLocale) {

        if (null == displayLocale) {
            return getLocaleNativeName(displayLocale);
        }
        return getLocaleDN(locale.toString(), displayLocale);

    }

    public static String getLocaleFullDN(Locale locale, Locale displayLocale) {

        if (null == locale) {
            locale = new Locale("");
        }
        String fullName = StringUtils.trimToNull(getLocaleNativeName(locale));
        if (null == displayLocale || locale.getLanguage().equals(displayLocale.getLanguage())) {
            return fullName;
        }
        if (StringUtils.equals(fullName, String.valueOf(locale))) {
            fullName = getLocaleDN(locale, displayLocale);
            return fullName;
        } else {
            String localizedName = StringUtils.trimToNull(getLocaleDN(locale, displayLocale));
            if (null != fullName && !StringUtils.equals(fullName, localizedName)) {
                return fullName.trim() + " (" + localizedName.trim() + ")";
            } else if (null != localizedName) {
                return localizedName.trim();
            } else if (null != fullName) {
                return fullName.trim();
            }
        }


        return locale.getDisplayName()
                + (!locale.equals(displayLocale) ? " (" + locale.getDisplayLanguage(locale) + ")" : "");

    }

    public static String getLocaleNativeName(Locale locale) {
        try {
            return getString(KEY_BASE_LOCALE_NAME, locale);
        } catch (MissingResourceException e) {
            return locale.getDisplayName(locale);
        }
    }

    public static Map<String, String> getLocaleNamesMap(Locale locale, Set<String> languageCodes, Map<String, List<String>> languagesMap) {
        Map<String, String> ret = new LinkedHashMap<String, String>();
        for (String languageCode : languageCodes) {
            List<String> languagelist = languagesMap.get(languageCode);

            String name = getLocaleFullDN(ITrackerResources.getLocale(languageCode), locale);

            ret.put(languageCode, name);
            for (String languageitem : languagelist) {
                name = getLocaleFullDN(ITrackerResources.getLocale(languageitem), locale);
                ret.put(languageitem, name);
            }

        }
        if (ret.size() == 0) {
            ret.put(getDefaultLocale(), getLocaleNativeName(getLocale(getDefaultLocale())));
        }
        return ret;

    }

    public static ResourceBundle getBundle() {
        return getBundle(getDefaultLocale());
    }

    public static ResourceBundle getBundle(String locale) {
        if (locale == null || locale.equals("")) {
            locale = getDefaultLocale();
        }

        return getBundle(getLocale(locale));
    }

    public static ResourceBundle getBundle(Locale locale) {
        if (locale == null) {
            locale = getLocale();
        }

        ResourceBundle bundle = languages.get(locale);
        if (bundle == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("getBundle: Loading new resource bundle for locale " + locale
                        + " from the database.");
            }
            if (!isInitialized()) {
                return ITrackerResourceBundle.loadBundle(locale);
            } else {

                Properties languageItems = configurationService
                        .getLanguageProperties(locale);

                logger.info("lazy loading locale bundle resources: {}", locale);

                bundle = ITrackerResourceBundle.loadBundle(locale, languageItems);
                logger.info("getBundle: got loaded for locale {} with {} items from the database.", locale, null == languageItems ? "no" : CollectionUtils.size(languageItems));
                logger.debug("getBundle: got loaded for locale {} with items {} from the database.", locale, languageItems);

                putBundle(locale, bundle);
            }

        }

        return bundle;
    }

    public static ResourceBundle getEditBundle(Locale locale) {
        if (locale == null) {
            locale = getLocale(getDefaultLocale());
        }
        ResourceBundle bundle;
        logger.debug("Loading new resource bundle for locale " + locale
                + " from the database.");
        Properties languageItems = configurationService.getLanguageProperties(
                locale);
        bundle = ITrackerResourceBundle.loadBundle(locale, languageItems);
        putBundle(locale, bundle);
        return bundle;
    }

    public static void putBundle(Locale locale, ResourceBundle bundle) {
        if (locale != null && bundle != null) {
            synchronized (bundleLock) {
                languages.put(locale, bundle);
                String localeString = locale.toString();
                if (localeString.length() == 5) {
                    localeString = localeString.substring(0, 2) + "_"
                            + localeString.substring(3).toUpperCase();
                }
                locales.put(localeString, locale);
            }
        }
    }


    /**
     * Clears a single cached resource bundle. The next time the bundle is
     * accessed, it will be reloaded and placed into the cache.
     */
    public static void clearBundle(Locale locale) {
        if (locale != null) {
            synchronized (bundleLock) {
                languages.remove(locale);
            }
        }
    }

    /**
     * Clears all cached resource bundles. The next time a bundle is accessed,
     * it will be reloaded and placed into the cache.
     */
    public static void clearBundles() {
        synchronized (bundleLock) {
            languages.clear();
        }
    }

    /**
     * Clears a single key from all cached resource bundles. The key is then
     * marked that it is dirty and should be reloaded on hte next access.
     */
    public static void clearKeyFromBundles(String key, boolean markDirty) {
        if (key != null) {
            synchronized (bundleLock) {
                for (ResourceBundle resourceBundle : languages.values()) {
                    ((ITrackerResourceBundle) resourceBundle).removeValue(key,
                            markDirty);
                }
            }
        }
    }

    public static String getString(String key) {
        return getString(key, getLocale(defaultLocale));
    }

    public static String getString(String key, String locale) {
        if (key == null) {
            return "";
        }

        if (locale == null || locale.equals("")) {
            locale = getDefaultLocale();
        }

        return getString(key, getLocale(locale));
    }

    private static String handleMissingResourceException(final MissingResourceException ex, final String key, final Locale locale) {

        logger.warn(
                "no value while retrieving translation key '{}' for locale {}", key, locale);
        Locale l = locale;
        if (null == l) {
            l = getLocale(getDefaultLocale());
        }
        if (StringUtils.isNotEmpty(l.getCountry())) {
            l = new Locale(l.getLanguage());
        } else if (StringUtils.isNotEmpty(l.getLanguage())) {
            l = new Locale("");
        }
        if (l != locale) {
            logger.debug("resolving {} from parent bundle ()", key, l);
            return getString(key, l);
        }
        throw ex;

    }
    private static String handleDirtyResourceException(final ITrackerDirtyResourceException e, final String key, final Locale locale) {

        logger.debug(
                "handleDirtyResourceException: key '{}' for locale {}", new Object[]{key, locale, e});
        ITrackerResourceBundle bundle = (ITrackerResourceBundle)getBundle(locale);
        try {
            final String languageItem = configurationService
                    .getLanguageEntry(key, locale);
            bundle.updateValue(key, languageItem);
            return languageItem;
        } catch (MissingResourceException e2) {
            bundle.removeValue(key, false);
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e3) {
                return handleMissingResourceException(e2, key, locale);
            }
        }
    }

    public static String getString(final String key, final Locale locale) {
        if (key == null) {
            return "";
        }

        String val;
        try {
            final ResourceBundle bundle = getBundle(locale);
            try {
                val = bundle.getString(key);
                return val;

            } catch (ITrackerDirtyResourceException e) {
                val = handleDirtyResourceException(e, key, locale);
            } catch (MissingResourceException e) {
                val = handleMissingResourceException(e, key, locale);
            }
            return val;
        } catch (NullPointerException ex) {
            logger.error(
                    "Unable to get any resources.  The requested locale was "
                            + locale, ex);
            return "MISSING BUNDLE: " + locale;

        } catch (MissingResourceException ex) {
            logger.warn(
                    "MissingResourceException caught while retrieving translation key '{}' for locale {}", key, locale);
            logger.debug(
                    "MissingResourceException was", ex);
            return "MISSING KEY: " + key;
        } catch (RuntimeException ex) {
            logger.info("getString: not found " + key + " locale: " + locale,
                    ex);
            try {
                return getEditBundle(locale).getString(key);
            } catch (Exception ex2) {
                logger.warn(
                        "Exception caught while retrieving translation key '{}' for locale {}: {}", new Object[]{key, locale, ex2.getMessage()});
                logger.debug("Exception was", ex2);
                return "MISSING KEY: " + key;
            }
        }
    }

    public static String getString(String key, Object[] options) {
        return getString(key, getLocale(getDefaultLocale()), options);
    }

    public static String getString(String key, String locale, Object[] options) {
        return getString(key, getLocale(locale), options);
    }

    public static String getString(String key, Locale locale, Object[] options) {
        String message = getString(key, locale);
        return MessageFormat.format(message, options, locale);
    }

    public static String getString(String key, String locale, String option) {
        String message = getString(key, locale);
        return MessageFormat.format(message, new Object[]{option},
                getLocale(locale));
    }

    public static String getString(String key, Locale locale, String option) {
        String message = getString(key, locale);
        return MessageFormat.format(message, new Object[]{option}, locale);
    }

    public static String getCheckForKey(String key)
            throws MissingResourceException {
        return getCheckForKey(key, getLocale());
    }

    public static String getCheckForKey(String key, Locale locale)
            throws MissingResourceException {
        try {
            return getBundle(locale).getString(key);
        } catch (ITrackerDirtyResourceException idre) {
            return getString(key, locale);
        } catch (NullPointerException ex) {
            logger.debug("Unable to get ResourceBundle for locale " + locale,
                    ex);
            throw new MissingResourceException("MISSING LOCALE: " + locale,
                    "ITrackerResources", key);
        }
    }

    public static boolean isLongString(String key) {
        String value = getString(key);
        return value.length() > 80 || value.indexOf('\n') > 0;
    }

    public static String escapeUnicodeString(String str, boolean escapeAll) {
        if (str == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (!escapeAll && ((ch >= 0x0020) && (ch <= 0x007e))) {
                sb.append(ch);
            } else {
                sb.append('\\').append('u');
                sb.append(encodeHex((ch >> 12) & 0xF));
                sb.append(encodeHex((ch >> 8) & 0xF));
                sb.append(encodeHex((ch >> 4) & 0xF));
                sb.append(encodeHex(ch & 0xF));
            }
        }
        return sb.toString();
    }

    public static String unescapeUnicodeString(String str) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); ) {
            char ch = str.charAt(i++);
            if (ch == '\\') {
                if (str.charAt(i++) == 'u') {
                    int value = 0;
                    for (int j = 0; j < 4; j++) {
                        value = (value << 4) + decodeHex(str.charAt(i++));
                    }
                    sb.append((char) value);
                } else {
                    sb.append("\\").append(str.charAt(i));
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static final String HEXCHARS = "0123456789ABCDEF";

    public static char encodeHex(int value) {
        return HEXCHARS.charAt(value & 0xf);
    }

    public static int decodeHex(char ch) {
        int value = -1;

        if (ch >= '0' && ch <= '9') {
            value = ch - '0';
        } else if (ch >= 'a' && ch <= 'f') {
            value = ch - 'a' + 10;
        } else if (ch >= 'A' && ch <= 'F') {
            value = ch - 'A' + 10;
        }

        return value;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    private static void setInitialized(boolean initialized) {
        ITrackerResources.initialized = initialized;
    }

    public static void setConfigurationService(ITrackerResourcesProvider service) {
        if (isInitialized()) {
            throw new IllegalStateException("Service is already set up.");
        }
        configurationService = service;
        String[] availableLocales = StringUtils.split(configurationService.getProperty("available_locales", getDefaultLocale()), ',');
        ArrayList<String> locales = new ArrayList<>(availableLocales.length);
        for (String l: availableLocales) {
            locales.add(StringUtils.trim(l));
        }
        ITrackerResources.availableLocales = locales;
        ITrackerResources.setDefaultLocale(configurationService.getProperty("default_locale", ITrackerResources.DEFAULT_LOCALE));

        logger.info("Set system default locale to '" + ITrackerResources.getDefaultLocale() + "'");
        setInitialized(true);

    }

}
