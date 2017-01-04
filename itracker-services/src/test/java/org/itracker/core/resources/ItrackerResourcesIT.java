package org.itracker.core.resources;

import org.itracker.model.Configuration;
import org.itracker.services.AbstractServicesIntegrationTest;
import org.itracker.services.ConfigurationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static junit.framework.Assert.fail;
import static org.itracker.Assert.assertTrue;


public class ItrackerResourcesIT extends AbstractServicesIntegrationTest {

    private ConfigurationService configurationService;

    @Test
    public void testInitialized() {
        assertTrue(ITrackerResources.isInitialized());
    }

    @Test
    public void testGetBundleEmptyString() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle("");
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetBundleStringParameter() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle(ITrackerResources.getLocale());
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetBundleNullLocale() {
        ResourceBundle resourceBundle = ITrackerResources.getBundle((Locale) null);
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getBundle(), resourceBundle);
    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey() {
        ResourceBundle b = ITrackerResources.getBundle(Locale.ENGLISH);
        final String key = "itracker.web.attr.admin";
        ITrackerResources.clearKeyFromBundles(key, true);

        final String value_1 = ITrackerResources.getString(key, ITrackerResources.getLocale());
        Assert.assertEquals("Admin", value_1);
        ((ITrackerResourceBundle) b).removeValue(key, true);
        String value_2 = ITrackerResources.getString(key, ITrackerResources.getLocale());


        // When a language items is removed, the default is loaded from properties.
        Assert.assertEquals("Admin", value_2);
    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey_GB() {
        final String key = "en_key";

        final ResourceBundle b = ITrackerResources.getBundle("en_GB");
        final String value_1 = ITrackerResources.getString(key, b.getLocale());
        Assert.assertEquals("en_value-en_GB", value_1);
        ((ITrackerResourceBundle) b).removeValue(key, true);
        String value_2 = ITrackerResources.getString(key, b.getLocale());

        // When a language items is removed, the default is loaded from database.
        Assert.assertEquals("en_value-en_GB", value_2);
    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey_removed_en() {
        final String key = "itracker.resolution.1";
        ITrackerResources.clearKeyFromBundles(key, true);

        final Locale en_l = new Locale("en");
        final ResourceBundle en = ITrackerResources.getBundle(en_l);
        final String en_1 = ITrackerResources.getString(key, en_l);
        Assert.assertEquals("en_1", "Fixed_en", en_1);
        ((ITrackerResourceBundle) en).removeValue(key, true);
        final String en_2 = ITrackerResources.getString(key, en_l);
        // When a language items is removed, the default is loaded from database.
        Assert.assertEquals("en_2", "Fixed_en", en_2);

        // When a language items is deleted, the default is loaded from properties.
        configurationService.removeLanguageKey(key);

        try {
            configurationService.getLanguageEntry(key, en_l);
            fail("should throw MissingResourceException");
        } catch (MissingResourceException e) {
            // fine
        }
        ITrackerResources.clearKeyFromBundles(key, true);        final String en_3 = ITrackerResources.getString(key, en_l);
        Assert.assertEquals("en_3", "Fixed", en_3);

    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey_removed_GB() {
        final String key = "itracker.resolution.1";

        ITrackerResources.clearKeyFromBundles(key, true);

        final Locale GB_l = new Locale("en", "GB");
        final ResourceBundle GB = ITrackerResources.getBundle(GB_l);
        final String GB_1 = ITrackerResources.getString(key, GB_l);
        ((ITrackerResourceBundle) GB).removeValue(key, true);
        Assert.assertEquals("GB_1", "Fixed_GB", GB_1);
        final String GB_2 = ITrackerResources.getString(key, GB_l);
        // When a language items is removed, the default is loaded from database.
        Assert.assertEquals("GB_2", "Fixed_GB", GB_2);

        // When a language items is deleted, the default is loaded from properties.
        configurationService.removeLanguageKey(key);

        try {
            configurationService.getLanguageEntry(key, GB_l);
            fail("should throw MissingResourceException");
        } catch (MissingResourceException e) {
            // fine
        }
        ITrackerResources.clearKeyFromBundles(key, true);
        final String GB_3 = ITrackerResources.getString(key, GB_l);
        Assert.assertEquals("GB_3", "Fixed", GB_3);
    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey_removed_es() throws Exception {
        final String key = "itracker.resolution.1";

        ITrackerResources.clearKeyFromBundles(key, true);

        final Locale es_l = new Locale("es");
        final ResourceBundle es = ITrackerResources.getBundle(es_l);
        final String es_1 = ITrackerResources.getString(key, es_l);
        Assert.assertEquals("es_1", "Fixed_es", es_1);
        ((ITrackerResourceBundle) es).removeValue(key, true);
        final String es_2 = ITrackerResources.getString(key, es_l);
        // When a language items is removed, the default is loaded from database.
        Assert.assertEquals("es_2", "Fixed_es", es_2);

        // When a language items is deleted, the default is loaded from properties.
        configurationService.removeLanguageKey(key);

        try {
            configurationService.getLanguageEntry(key, es_l);
            fail("should throw MissingResourceException");
        } catch (MissingResourceException e) {
            // fine
        }
        ITrackerResources.clearKeyFromBundles(key, true);
        //((ITrackerResourceBundle) es).removeValue(key, true);
        final String es_3 = ITrackerResources.getString(key, es_l);
        Assert.assertEquals("es_3", "Resulta", es_3);

    }

    @Test
    public void testGetStringWithLocaleWithDirtyKey_removed_MX() {
        final String key = "itracker.resolution.1";

        ITrackerResources.clearKeyFromBundles(key, true);

        final Locale MX_l = new Locale("es", "MX");
        final ResourceBundle MX = ITrackerResources.getBundle(MX_l);
        final String MX_1 = ITrackerResources.getString(key, MX_l);
        Assert.assertEquals("MX_1", "Fixed_MX", MX_1);
        ((ITrackerResourceBundle) MX).removeValue(key, true);
        final String MX_2 = ITrackerResources.getString(key, MX_l);
        // When a language items is removed, the default is loaded from database.
        Assert.assertEquals("MX_2", "Fixed_MX", MX_2);

        // When a language items is deleted, the default is loaded from properties.
        configurationService.removeLanguageKey(key);

        try {
            configurationService.getLanguageEntry(key, MX_l);
            fail("should throw MissingResourceException");
        } catch (MissingResourceException e) {
            // fine
        }
        ITrackerResources.clearKeyFromBundles(key, true);
        final String MX_3 = ITrackerResources.getString(key, MX_l);
        // When a language items is deleted, the default is loaded from properties.
        Assert.assertEquals("MX_3", "Reparado", MX_3);

    }

    @Test
    public void testGetEditBundleNullLocale() {
        //TODO: set languageDAO of ConfigurationService
        ResourceBundle resourceBundle = ITrackerResources.getEditBundle(null);
        Assert.assertNotNull(resourceBundle);
        Assert.assertEquals(ITrackerResources.getLocale(), resourceBundle.getLocale());
        Enumeration<String> keys = resourceBundle.getKeys(); // keys of copy bundle
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Assert.assertEquals(ITrackerResources.getString(key), resourceBundle.getString(key));
        }
        keys = ITrackerResources.getBundle().getKeys(); // keys of original bundle
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Assert.assertEquals(ITrackerResources.getString(key), resourceBundle.getString(key));
        }
    }

    @Before
    public void setup() {
        this.configurationService =
                (ConfigurationService) applicationContext.getBean("configurationService");
        configurationService.resetConfigurationCache(Configuration.Type.locale);

    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_dataset.xml"
        };
    }

}
