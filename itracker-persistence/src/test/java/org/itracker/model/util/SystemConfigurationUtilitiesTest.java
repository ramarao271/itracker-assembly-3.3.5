/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.model.util;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.NameValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author seas
 */
public class SystemConfigurationUtilitiesTest {

    @Test
    public void testGetLanguageKey() {
        {
            final Configuration configuration =
                    new Configuration(Configuration.Type.status,
                            "value");
            final String languageKey =
                    SystemConfigurationUtilities.getLanguageKey(configuration);
            assertEquals(ITrackerResources.KEY_BASE_STATUS + "value",
                    languageKey);
        }
        {
            final Configuration configuration =
                    new Configuration(Configuration.Type.severity,
                            "value");
            final String languageKey =
                    SystemConfigurationUtilities.getLanguageKey(configuration);
            assertEquals(ITrackerResources.KEY_BASE_SEVERITY + "value",
                    languageKey);
        }
        {
            final Configuration configuration =
                    new Configuration(Configuration.Type.resolution,
                            "value");
            final String languageKey =
                    SystemConfigurationUtilities.getLanguageKey(configuration);
            assertEquals(ITrackerResources.KEY_BASE_RESOLUTION + "value",
                    languageKey);
        }
        {
            final String languageKey =
                    SystemConfigurationUtilities.getLanguageKey(null);
            assertEquals("",
                    languageKey);
        }
        {
            final Configuration configuration =
                    new Configuration(Configuration.Type.locale,
                            "value");
            final String languageKey =
                    SystemConfigurationUtilities.getLanguageKey(configuration);
            assertEquals("itracker.locale.name.value",
                    languageKey);
        }
    }

    @Test
    public void testGetVersionAsLong() {
        {
            final long versionNumber =
                    SystemConfigurationUtilities.getVersionAsLong("1.2.3");
            assertEquals(1002003, versionNumber);
        }
        {
            final long versionNumber =
                    SystemConfigurationUtilities.getVersionAsLong("1.2");
            assertEquals(1002000, versionNumber);
        }
        {
            final long versionNumber =
                    SystemConfigurationUtilities.getVersionAsLong("1");
            assertEquals(1000000, versionNumber);
        }
        {
            final long versionNumber =
                    SystemConfigurationUtilities.getVersionAsLong("3.2.1");
            assertEquals(3002001, versionNumber);
        }
        try {
            @SuppressWarnings("unused")
            final long versionNumber =
                    SystemConfigurationUtilities.getVersionAsLong("1.2.3.4");
            assertTrue(false);
        } catch (final IllegalArgumentException e) {
            assertTrue(true);
        }
        {
            final long versionNumber =
                    SystemConfigurationUtilities.getVersionAsLong("1.2:3");
            assertEquals(1000000, versionNumber);
        }
    }

    @Test
    public void testGetLocalType() {
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_INVALID,
                SystemConfigurationUtilities.getLocaleType(null));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_INVALID,
                SystemConfigurationUtilities.getLocaleType(""));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_BASE,
                SystemConfigurationUtilities.getLocaleType(
                        ITrackerResources.BASE_LOCALE));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_LOCALE,
                SystemConfigurationUtilities.getLocaleType(
                        "en_US"));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_LOCALE,
                SystemConfigurationUtilities.getLocaleType(
                        "ru_RU"));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE,
                SystemConfigurationUtilities.getLocaleType(
                        "en"));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE,
                SystemConfigurationUtilities.getLocaleType(
                        "ru"));
        assertEquals(SystemConfigurationUtilities.LOCALE_TYPE_INVALID,
                SystemConfigurationUtilities.getLocaleType(
                        "euro"));
    }

    @Test
    public void testGetLocalePart() {
        assertEquals("en_US",
                SystemConfigurationUtilities.getLocalePart(
                        "en_US", SystemConfigurationUtilities.LOCALE_TYPE_LOCALE));
        assertEquals(null,
                SystemConfigurationUtilities.getLocalePart(
                        "enUS", SystemConfigurationUtilities.LOCALE_TYPE_LOCALE));
        assertEquals("en",
                SystemConfigurationUtilities.getLocalePart(
                        "en_US", SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE));
    }

    @Test
    public void testNvpArrayToConfigurationArray() {
        final NameValuePair[] nvp = new NameValuePair[]{
                new NameValuePair("name1", "value1"),
                new NameValuePair("name2", "value2")
        };
        final Configuration[] configurations =
                SystemConfigurationUtilities.nvpArrayToConfigurationArray(
                        Configuration.Type.status, nvp);
        assertEquals(2, configurations.length);
        final Configuration configuration1 = configurations[0];
        assertEquals("name1", configuration1.getName());
        assertEquals("value1", configuration1.getValue());
        final Configuration configuration2 = configurations[1];
        assertEquals("name2", configuration2.getName());
        assertEquals("value2", configuration2.getValue());
    }

}
