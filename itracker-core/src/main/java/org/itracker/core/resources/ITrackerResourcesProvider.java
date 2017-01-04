package org.itracker.core.resources;

import java.util.Locale;
import java.util.Properties;

/**
 * To provide Locale values from application properties configuration.
 * @author ranks
 */
public interface ITrackerResourcesProvider {
    /**
     * Load all translation keys for a locale.
     *
     * @param locale the locale
     * @return loaded properties
     */
    Properties getLanguageProperties(Locale locale);

    /**
     * Load the exact translation by key for a locale.
     *
     * @param key the key
     * @param locale the locale
     * @return the language value
     */
    String getLanguageEntry(String key, Locale locale);

    String getProperty(String name, String defaultValue);
}
