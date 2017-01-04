/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.model.util;

import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Language;
import org.itracker.model.Status;
import org.itracker.persistence.dao.LanguageDAO;
import org.junit.Test;

import java.util.*;

import static org.itracker.Assert.*;
/**
 * @author seas
 */
public class ProjectUtilitiesIT extends AbstractDependencyInjectionTest {

    private static final Logger log = Logger.getLogger(ProjectUtilities.class);

    private void doTestGetStatusName(final Locale locale,
                                     final Status status, final String expected) {
        final String actual =
                ProjectUtilities.getStatusName(status, locale);
        assertEquals("ProjectUtilities.getStatusName(" + status + ", " +
                locale + ")", expected, actual);
    }

    /**
     * Verifies UserUtilities.getStatusName
     */
    @Test
    public void testGetStatusName() {

        // "Deleted"
        doTestGetStatusName(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE),
                Status.DELETED, "Deleted");
        doTestGetStatusName(null,
                Status.DELETED, "Deleted");
        doTestGetStatusName(new Locale("test"),
                Status.DELETED, "Deleted");

        // "Active"
        doTestGetStatusName(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE),
                Status.ACTIVE, "Active");
        doTestGetStatusName(null,
                Status.ACTIVE, "Active");
        doTestGetStatusName(new Locale("test"),
                Status.ACTIVE, "Active");

        // "Locked"
        doTestGetStatusName(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE),
                Status.LOCKED, "Locked");
        doTestGetStatusName(null,
                Status.LOCKED, "Locked");
        doTestGetStatusName(new Locale("test"),
                Status.LOCKED, "Locked");

        // "Locked"
        doTestGetStatusName(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE),
                Status.VIEWABLE, "Viewable Only");
        doTestGetStatusName(null,
                Status.VIEWABLE, "Viewable Only");
        doTestGetStatusName(new Locale("test"),
                Status.VIEWABLE, "Viewable Only");
    }

    private void doTestGetStatusNames(final Locale locale,
                                      final Map<Status, String> expected) {
        final Map<Status, String> actual = ProjectUtilities.getStatusNames(locale);
        for (final Map.Entry<Status, String> entry : expected.entrySet()) {
            final Status key = entry.getKey();
            if (actual.containsKey(key)) {
                final String valueActual = actual.get(key);
                assertEquals("ProjectUtilities.getStatusNames(" + locale + ")" +
                        ".get(" + key + ")",
                        entry.getValue(), valueActual);
            } else {
                assertEquals("ProjectUtilities.getStatusNames(" + locale + ")" +
                        ".get(" + key + ")." +
                        "contains(" + key + ")",
                        false);
            }

        }
        assertEquals("ProjectUtilities.getStatusNames(" + locale + ").size()",
                expected.size(), actual.size());
    }

    /**
     * Verifies UserUtilities#getStatusNames(Locale)
     */
    @Test
    public void testGetStatusNames() {
        final Map<Status, String> expected = new HashMap<Status, String>();
        expected.put(Status.DELETED, "Deleted");
        expected.put(Status.ACTIVE, "Active");

        expected.put(Status.LOCKED, "Locked");
//        junit.framework.ComparisonFailure: ProjectUtilities.getStatusNames().get(VIEWABLE) expected:<[Viewable Only]> but was:<[MISSING KEY: itracker.project.status.2]>
        expected.put(Status.VIEWABLE, "Viewable Only");

        doTestGetStatusNames(
                ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE),
                expected);
        doTestGetStatusNames(new Locale("test"),
                expected);
    }

    @Test
    public void testHasOption() {
        assertTrue(ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE));
        assertFalse(ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE));
        assertTrue(ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE | ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE));
        assertFalse(ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML,
                ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE | ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE));
    }

    private void doTestGetOptions(final int currentOptions,
                                  final Integer[] expected) {
        final Integer[] actual = ProjectUtilities.getOptions(currentOptions);
        final List<Integer> actualList = Arrays.asList(actual);
        assertEquals(expected.length, actual.length);
        for (final Integer er : expected) {
            assertTrue(actualList.contains(er));
        }
    }

    @Test
    public void testGetOptions() {
        doTestGetOptions(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                new Integer[]{ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE});
        doTestGetOptions(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE,
                new Integer[]{ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE});
        doTestGetOptions(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE | ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE,
                new Integer[]{ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                        ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE
                });
        doTestGetOptions(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE | ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE | ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL | ProjectUtilities.OPTION_LITERAL_HISTORY_HTML | ProjectUtilities.OPTION_NO_ATTACHMENTS | ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS | ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML,
                new Integer[]{ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                        ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE,
                        ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL,
                        ProjectUtilities.OPTION_LITERAL_HISTORY_HTML,
                        ProjectUtilities.OPTION_NO_ATTACHMENTS,
                        ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
                        ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML
                });

    }

    @Test
    public void testGetScriptPriorityLabelKey() {
        assertEquals(
                "Real Time",
                ProjectUtilities.getScriptPriorityLabelKey(1));
        assertEquals(
                "High",
                ProjectUtilities.getScriptPriorityLabelKey(2));
    }

    @Test
    public void getScriptPrioritySize() {
        assertEquals("6",
                ProjectUtilities.getScriptPrioritySize());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        try {
            // need to initialize translations from ITracker.properties explicitly
            LanguageDAO languageDAO = (LanguageDAO) applicationContext.getBean("languageDAO");

            Properties localeProperties = new PropertiesFileHandler(
                    "/org/itracker/core/resources/ITracker.properties").getProperties();
            for (Enumeration<?> propertiesEnumeration = localeProperties.propertyNames(); propertiesEnumeration.hasMoreElements(); ) {
                String key = (String) propertiesEnumeration.nextElement();
                String value = localeProperties.getProperty(key);
                languageDAO.saveOrUpdate(new Language(ITrackerResources.BASE_LOCALE, key, value));
            }

            ITrackerResources.clearBundles();
        } catch (final Exception e) {
            log.warn(e);
        }
    }


    /**
     * Defines a set of datafiles to be uploaded into database.
     *
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_dataset.xml"
        };
    }

}
