package org.itracker.core.resources.test;

import org.itracker.core.resources.ITrackerResourceBundle;
import org.itracker.core.resources.ITrackerResources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class ItrackerResourcesTest {
    private String defaultLocaleString_;
    private Locale defaultLocale_;
    private ResourceBundle defaultResourceBundle_;
    private String testLocaleString_ = "en_ID";
    private Locale testLocale_;
    private ITrackerResourceBundle testResourceBundle_;

    @Before
    public void onSetUp() throws Exception {

        defaultLocaleString_ = ITrackerResources.getDefaultLocale();
        assertNotNull(defaultLocaleString_);
        if (defaultLocaleString_.length() == 2) {
            defaultLocale_ = new Locale(defaultLocaleString_);
        } else if (defaultLocaleString_.length() == 5) {
            defaultLocale_ = new Locale(defaultLocaleString_.substring(0, 2), defaultLocaleString_.substring(3, 5));
        } else {
            fail();
        }
        assertNotNull(defaultLocale_);
        defaultResourceBundle_ = ITrackerResources.getBundle();

        Object[][] data = {{"itracker.web.attr.admin", "itracker.web.attr.administer", "itracker.web.attr.adminTask", "itracker.web.attr.longString", "itracker.web.attr.newLine"},
                {"Admin", "Administer", "The {0} {1} {2}", "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567", "abc\nde"}};

        testLocale_ = new Locale(testLocaleString_);
        testResourceBundle_ = new ITrackerResourceBundle(testLocale_, data);
        assertNotNull(testResourceBundle_);
        ITrackerResources.putBundle(testLocale_, testResourceBundle_);
    }

    @After
    public void onTearDown() throws Exception {
    }


    @Test
    public void testGetLocaleNull() {
        Locale locale = ITrackerResources.getLocale(null);
        assertNotNull(locale);
        assertEquals(ITrackerResources.getLocale(ITrackerResources.getDefaultLocale()), locale);
    }

    @Test
    public void testGetLocaleInvalidString() {
        Locale localeInvalid = ITrackerResources.getLocale("ABCDEFG");
        Locale localeDefault = ITrackerResources.getLocale(ITrackerResources.getDefaultLocale());
        assertEquals(localeInvalid, localeDefault);
        localeInvalid = ITrackerResources.getLocale("ABCDEFG");
        localeDefault = ITrackerResources.getLocale(ITrackerResources.DEFAULT_LOCALE);
        assertEquals(localeInvalid, localeDefault);
    }


    @Test
    public void testClearBundles() {
        Enumeration<String> keys = ITrackerResources.getBundle(ITrackerResources.BASE_LOCALE).getKeys();
        int nKeys = 0;
        while (keys.hasMoreElements()) {
            keys.nextElement();
            ++nKeys;
        }
        assertTrue(5 < nKeys);

        ITrackerResources.clearBundles();

        keys = ITrackerResources.getBundle(testLocale_).getKeys();
        nKeys = 0;
        while (keys.hasMoreElements()) {
            keys.nextElement();
            ++nKeys;
        }
        assertTrue(5 < nKeys);
    }

    @Test
    public void testClearKeyFromBundles() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle(testLocale_);
        String value = resourceBundle.getString("itracker.web.attr.admin");
        assertNotNull(value);
        assertEquals("Admin", value);

        ITrackerResources.clearKeyFromBundles("itracker.web.attr.admin", false);

        resourceBundle = ITrackerResources.getBundle(testLocale_);

        try {
            assertNotNull(resourceBundle.getString("itracker.web.attr.admin"));
            assertEquals("", ResourceBundle.getBundle(ITrackerResources.RESOURCE_BUNDLE_NAME, resourceBundle.getLocale()).getString("itracker.web.attr.admin"),
                    resourceBundle.getString("itracker.web.attr.admin"));
        } catch (RuntimeException exception) {
            fail("should fall back to properties resource, but throwed " + exception.getClass() + ", " + exception.getMessage());
        }
    }

    @Test
    public void testGetStringNullKey() {
        assertEquals("", ITrackerResources.getString(null, testLocaleString_));
    }

    @Test
    public void testGetStringDefaultLocaleString() {
        assertEquals("Admin", ITrackerResources.getString("itracker.web.attr.admin", (String) null));
    }

    @Test
    public void testGetStringWithLocaleString() {
        assertEquals("Admin", ITrackerResources.getString("itracker.web.attr.admin", testLocaleString_));
    }

    @Test
    public void testGetStringWithLocaleWithNullKey() {
        assertEquals("", ITrackerResources.getString(null, testLocale_));
    }


    @Test
    public void testGetStringWithLocaleWithRemovedKey() {
        testResourceBundle_.removeValue("itracker.web.attr.admin", false);
        String value = ITrackerResources.getString("itracker.web.attr.admin", testLocale_);
        // When a language items is removed, the default is loaded from properties.
        assertEquals("Admin", value);
    }

    @Test
    public void testGetStringWithWrongLocale() {
        Locale locale = new Locale("EEEEEEEEEEe");
        String value = ITrackerResources.getString("itracker.web.attr.admin", locale);


        // When a language items is removed, the default is loaded from properties.
        assertEquals("Admin", value);
    }

    @Test
    public void testGetStringWithMultipleOptions() {
        Object[] options = {"administrator", "administer", "the library"};
        String value = ITrackerResources.getString("itracker.web.attr.adminTask", testLocaleString_, options);
        assertEquals("The administrator administer the library", value);
    }

    @Test
    public void testGetStringWithSingleOption() {
        String value = ITrackerResources.getString("itracker.web.attr.adminTask", testLocaleString_, "administrator");
        assertEquals("The administrator {1} {2}", value);
    }

    @Test
    public void testGetStringWithLocaleWithSingleOption() {
        String value = ITrackerResources.getString("itracker.web.attr.adminTask", testLocale_, "administrator");
        assertEquals("The administrator {1} {2}", value);
    }

    @Test
    public void testGetCheckForKey() {
        String value = ITrackerResources.getCheckForKey("itracker.web.attr.admin");
        assertEquals("Admin", value);
    }

    @Test
    public void testGetCheckForKeyDirty() {
        testResourceBundle_.removeValue("itracker.web.attr.admin", true);
        String value = ITrackerResources.getCheckForKey("itracker.web.attr.admin");

        // When a language items is removed, the default is reloaded by the configuration service.

        assertEquals("Admin", value);
    }

    @Test
    public void testGetCheckForKeyWithWrongBundle() {
        try {
            ITrackerResources.getCheckForKey("itracker.web.attr.admin", new Locale("AAAAAA"));

        } catch (RuntimeException exception) {

            fail("throwed " + exception.getClass() + ": " + exception.getMessage());
        }
    }

    @Test
    public void testIsLongStringFalse() {
        assertFalse(ITrackerResources.isLongString("itracker.web.attr.admin"));
    }

    @Test
    public void testIsLongStringTrueLong() {
        assertTrue(ITrackerResources.isLongString("itracker.web.moveissue.instructions"));
    }

    @Test
    public void testIsLongStringTrueNewLine() {
        assertTrue(ITrackerResources.isLongString("itracker.email.forgotpass.body"));
    }

    @Test
    public void testInitialized() {
        assertFalse(ITrackerResources.isInitialized());
    }

    @Test
    public void testHex() {
        assertEquals('0', ITrackerResources.encodeHex(0));
        assertEquals(0, ITrackerResources.decodeHex('0'));
        assertEquals('1', ITrackerResources.encodeHex(1));
        assertEquals(1, ITrackerResources.decodeHex('1'));
        assertEquals('2', ITrackerResources.encodeHex(2));
        assertEquals(2, ITrackerResources.decodeHex('2'));
        assertEquals('3', ITrackerResources.encodeHex(3));
        assertEquals(3, ITrackerResources.decodeHex('3'));
        assertEquals('4', ITrackerResources.encodeHex(4));
        assertEquals(4, ITrackerResources.decodeHex('4'));
        assertEquals('5', ITrackerResources.encodeHex(5));
        assertEquals(5, ITrackerResources.decodeHex('5'));
        assertEquals('6', ITrackerResources.encodeHex(6));
        assertEquals(6, ITrackerResources.decodeHex('6'));
        assertEquals('7', ITrackerResources.encodeHex(7));
        assertEquals(7, ITrackerResources.decodeHex('7'));
        assertEquals('8', ITrackerResources.encodeHex(8));
        assertEquals(8, ITrackerResources.decodeHex('8'));
        assertEquals('9', ITrackerResources.encodeHex(9));
        assertEquals(9, ITrackerResources.decodeHex('9'));
        assertEquals('A', ITrackerResources.encodeHex(10));
        assertEquals(10, ITrackerResources.decodeHex('A'));
        assertEquals(10, ITrackerResources.decodeHex('a'));
        assertEquals('B', ITrackerResources.encodeHex(11));
        assertEquals(11, ITrackerResources.decodeHex('B'));
        assertEquals(11, ITrackerResources.decodeHex('b'));
        assertEquals('C', ITrackerResources.encodeHex(12));
        assertEquals(12, ITrackerResources.decodeHex('C'));
        assertEquals(12, ITrackerResources.decodeHex('c'));
        assertEquals('D', ITrackerResources.encodeHex(13));
        assertEquals(13, ITrackerResources.decodeHex('D'));
        assertEquals(13, ITrackerResources.decodeHex('d'));
        assertEquals('E', ITrackerResources.encodeHex(14));
        assertEquals(14, ITrackerResources.decodeHex('E'));
        assertEquals(14, ITrackerResources.decodeHex('e'));
        assertEquals('F', ITrackerResources.encodeHex(15));
        assertEquals(15, ITrackerResources.decodeHex('F'));
        assertEquals(15, ITrackerResources.decodeHex('f'));
    }

    @Test
    public void testUnicodeString() {
        String originalString = "itracker unit testing";
        String escapedString = ITrackerResources.escapeUnicodeString(originalString, true);
        assertEquals(originalString, ITrackerResources.unescapeUnicodeString(escapedString));
        escapedString = ITrackerResources.escapeUnicodeString(originalString, false);
        assertEquals(originalString, ITrackerResources.unescapeUnicodeString(escapedString));
    }

}

 	  	 
