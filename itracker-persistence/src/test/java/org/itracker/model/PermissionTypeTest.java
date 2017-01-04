package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PermissionTypeTest {

    @Test
    public void testFromCode() {
        try {
            PermissionType.valueOf(0);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            PermissionType.valueOf(-2);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        try {
            PermissionType.valueOf(14);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertEquals("code 1", PermissionType.PRODUCT_ADMIN, PermissionType.valueOf(1));
        assertEquals("code 5", PermissionType.ISSUE_ASSIGN_SELF, PermissionType.valueOf(5));
        assertEquals("code -1", PermissionType.USER_ADMIN, PermissionType.valueOf(-1));
    }


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}
