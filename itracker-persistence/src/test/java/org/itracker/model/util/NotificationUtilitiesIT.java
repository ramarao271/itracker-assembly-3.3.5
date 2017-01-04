/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.itracker.model.util;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.User;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import static org.itracker.Assert.*;

/**
 * @author seas
 */
@Ignore
public class NotificationUtilitiesIT extends AbstractDependencyInjectionTest {

    @Test
    public void testGetRoleNameByLocale() {

        final Locale localeTest = new Locale("test");
        assertEquals("test-notification_role_ANY",
                NotificationUtilities.getRoleName(Role.ANY, localeTest));
        assertEquals("test-notification_role_CO",
                NotificationUtilities.getRoleName(Role.CO, localeTest));
        assertEquals("test-notification_role_CONTRIBUTER",
                NotificationUtilities.getRoleName(Role.CONTRIBUTER, localeTest));
        assertEquals("test-notification_role_CREATOR",
                NotificationUtilities.getRoleName(Role.CREATOR, localeTest));
        assertEquals("test-notification_role_IP",
                NotificationUtilities.getRoleName(Role.IP, localeTest));
        assertEquals("test-notification_role_OWNER",
                NotificationUtilities.getRoleName(Role.OWNER, localeTest));
        assertEquals("test-notification_role_PM",
                NotificationUtilities.getRoleName(Role.PM, localeTest));
        assertEquals("test-notification_role_PO",
                NotificationUtilities.getRoleName(Role.PO, localeTest));
        assertEquals("test-notification_role_QA",
                NotificationUtilities.getRoleName(Role.QA, localeTest));
        assertEquals("test-notification_role_VO",
                NotificationUtilities.getRoleName(Role.VO, localeTest));
    }

    private void doTestGetRoleNames(final Locale locale,
                                    final Map<Role, String> expected) {
        final Map<Role, String> actual =
                NotificationUtilities.getRoleNames(locale);
        for (final Map.Entry<Role, String> entry : expected.entrySet()) {
            final Role key = entry.getKey();
            if (actual.containsKey(key)) {
                final String valueActual = actual.get(key);
                assertEquals(
                        "NotificationUtilities.getRoleNames(" + locale + ")" +
                                ".get(" + key + ")",
                        entry.getValue(), valueActual);
            } else {
                assertEquals(
                        "NotificationUtilities.getRoleNames(" + locale + ")" +
                                ".get(" + key + ")." +
                                "contains(" + key + ")",
                        false);
            }

        }
        assertEquals("NotificationUtilities.getRoleNames(" + locale + ").size()",
                expected.size(), actual.size());
    }

    @Test
    public void testGetRoleNames() {
        final Map<Role, String> expected = new HashMap<Role, String>();
//        expected.put(Role.ANY, "test-notification_role_ANY");
        expected.put(Role.CO, "test-notification_role_CO");
        expected.put(Role.CONTRIBUTER, "test-notification_role_CONTRIBUTER");
        expected.put(Role.CREATOR, "test-notification_role_CREATOR");
        expected.put(Role.IP, "test-notification_role_IP");
        expected.put(Role.OWNER, "test-notification_role_OWNER");
        expected.put(Role.PM, "test-notification_role_PM");
        expected.put(Role.PO, "test-notification_role_PO");
        expected.put(Role.QA, "test-notification_role_QA");
        expected.put(Role.VO, "test-notification_role_VO");

        {
            final Map<Role, String> expectedTest = new HashMap<Role, String>();
            expectedTest.putAll(expected);
            expectedTest.put(Role.QA, "");
            doTestGetRoleNames(new Locale("test"),
                    expected);
        }
    }

    private void doTestMappedRoles(final List<Notification> notifications,
                                   final Map<User, Set<Notification.Role>> expected) {
        final Map<User, Set<Notification.Role>> actual =
                NotificationUtilities.mappedRoles(notifications);
        assertEquals(expected.size(), actual.size());
        for (final Map.Entry<User, Set<Notification.Role>> entryExpected :
                expected.entrySet()) {
            assertTrue(actual.containsKey(entryExpected.getKey()));
            final Set<Notification.Role> rolesActual =
                    actual.get(entryExpected.getKey());
            final Set<Notification.Role> rolesExpected =
                    entryExpected.getValue();
            assertEquals(rolesExpected, rolesActual);
        }
    }

    @Test
    public void testMappedRoles() {
        final List<Notification> notifications = new Vector<Notification>();
        final User user1 = new User("user-1");
        final User user2 = new User("user-2");
        final User user3 = new User("user-3");
        {
            final Notification notification = new Notification();
            notification.setUser(user1);
            notification.setRole(Role.CREATOR);
            notifications.add(notification);
        }
        {
            final Notification notification = new Notification();
            notification.setUser(user1);
            notification.setRole(Role.PM);
            notifications.add(notification);
        }
        {
            final Notification notification = new Notification();
            notification.setUser(user2);
            notification.setRole(Role.QA);
            notifications.add(notification);
        }
        {
            final Notification notification = new Notification();
            notification.setUser(user3);
            notification.setRole(Role.QA);
            notifications.add(notification);
        }
        final Map<User, Set<Notification.Role>> expected =
                new HashMap<User, Set<Notification.Role>>();
        {
            final Set<Notification.Role> roles = new HashSet<Notification.Role>();
            roles.add(Role.CREATOR);
            roles.add(Role.PM);
            expected.put(user1, roles);
        }
        {
            final Set<Notification.Role> roles = new HashSet<Notification.Role>();
            roles.add(Role.QA);
            expected.put(user2, roles);
        }
        {
            final Set<Notification.Role> roles = new HashSet<Notification.Role>();
            roles.add(Role.QA);
            expected.put(user3, roles);
        }
        doTestMappedRoles(notifications, expected);
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
