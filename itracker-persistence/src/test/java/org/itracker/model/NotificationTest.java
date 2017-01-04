package org.itracker.model;

import org.itracker.model.Notification.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.*;

public class NotificationTest {
    private Notification not;

    @Test
    public void testSetIssue() {
        try {
            not.setIssue(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetUser() {
        try {
            not.setUser(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    /**
     * TODO remove method from Notification
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSetNotificationRole() {
        not.setNotificationRole(1);
        assertEquals(1, not.getNotificationRole());
        not.setNotificationRole(10000);
        assertEquals(-1, not.getNotificationRole());
    }

    @Test
    public void testToString() {
        assertNotNull("toString", not.toString());
    }

    @Test
    public void testUserComparator() {
        Notification entityA = new Notification(new User("aaa", "", "a", "a", "a@a.com", false), new Issue(), Role.ANY);
        Notification entityB = new Notification(new User("bbb", "", "b", "b", "b@b.com", false), new Issue(), Role.ANY);

        assertEntityComparator("user comparator", Notification.USER_COMPARATOR, entityA, entityB);
        assertEntityComparator("user comparator", Notification.USER_COMPARATOR, entityA, null);

        entityA.setUser(entityB.getUser());
        assertEntityComparatorEquals("user comparator", Notification.USER_COMPARATOR, entityA, entityB);
        assertEntityComparatorEquals("user comparator", Notification.USER_COMPARATOR, entityA, entityA);

    }

    @Test
    public void testIssueUserRoleComparator() {
        Issue issueA = new Issue();
        issueA.setId(1);
        Issue issueB = new Issue();
        issueB.setId(2);
        User userA = new User("aaa", "", "a", "a", "a@a.com", false);
        User userB = new User("bbb", "", "b", "b", "b@b.com", false);
        Notification entityA = new Notification(userA, issueA, Role.ANY);
        Notification entityB = new Notification(userA, issueB, Role.ANY);

        assertEntityComparator("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, entityB);
        assertEntityComparator("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, null);

        entityA.setIssue(entityB.getIssue());
        entityB.setUser(userB);

        assertEntityComparator("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, entityB);
        assertEntityComparator("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, null);

        entityB.setUser(userA);
        entityB.setRole(Role.CREATOR);

        assertEntityComparator("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, entityB);
        assertEntityComparator("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, null);

        entityA.setRole(entityB.getRole());
        assertEntityComparatorEquals("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, entityB);
        assertEntityComparatorEquals("issue user role comparator", Notification.ISSUE_USER_ROLE_COMPARATOR, entityA, entityA);

    }

    @Test
    public void testRoleComparator() {
        Issue issueA = new Issue();
        issueA.setId(1);
        Issue issueB = new Issue();
        issueB.setId(2);

        User userA = new User("aaa", "", "a", "a", "a@a.com", false);
        User userB = new User("bbb", "", "b", "b", "b@b.com", false);
        Notification entityA = new Notification(userA, issueA, null);
        Notification entityB = new Notification(userB, issueA, Role.CREATOR);

        assertEntityComparator("role comparator", Notification.TYPE_COMPARATOR, entityA, entityB);
        assertEntityComparator("role comparator", Notification.TYPE_COMPARATOR, entityA, null);

        entityA.setRole(Role.ANY);

        assertEntityComparator("role comparator", Notification.TYPE_COMPARATOR, entityA, entityB);
        assertEntityComparator("role comparator", Notification.TYPE_COMPARATOR, entityA, null);

        entityB.setRole(entityA.getRole());

        assertEntityComparatorEquals("role comparator", Notification.TYPE_COMPARATOR, entityA, entityB);
        assertEntityComparatorEquals("role comparator", Notification.TYPE_COMPARATOR, entityA, entityA);

    }

    @Before
    public void setUp() throws Exception {
        not = new Notification();
    }

    @After
    public void tearDown() throws Exception {
        not = null;
    }

}
