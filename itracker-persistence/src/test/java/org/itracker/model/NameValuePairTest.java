package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NameValuePairTest {
    private NameValuePair nvp;

    @Test
    public void testSetName() {
        nvp.setName("jerry");
        assertEquals("name#jerry", "jerry", nvp.getName());
        nvp.setName(null);
        assertEquals("name is empty", "", nvp.getName());
    }

    @Test
    public void testCompareKeyTo() {
        NameValuePair nvpCopy = new NameValuePair("name", "value");
        assertEquals(0, nvp.compareTo(nvpCopy));

        nvpCopy.setName("name1");
        assertEquals(-1, nvp.compareKeyTo(nvpCopy));

        nvpCopy.setName(null);
        assertEquals(4, nvp.compareKeyTo(nvpCopy));

        // test when NameValuePair is null
        nvpCopy = null;
        try {
            nvp.compareKeyTo(nvpCopy);
            fail("did not throw NullpointerException");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCompareValueTo() {
        NameValuePair nvpCopy = new NameValuePair("name", "value");
        assertEquals(0, nvp.compareValueTo(nvpCopy));

        nvpCopy.setValue("value1");
        assertEquals(-1, nvp.compareValueTo(nvpCopy));

        nvpCopy.setValue(null);
        try {
            assertEquals(0, nvp.compareValueTo(nvpCopy));
            fail("did not throw NullPointerException");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
        // test when NameValuePair is null
        nvpCopy = null;
        try {
            nvp.compareValueTo(nvpCopy);
            fail("did not throw NullPointerException");
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testEquals() {
        NameValuePair nvpCopy = nvp;
        assertTrue("nvp equals nvpCopy", nvp.equals(nvpCopy));

        nvpCopy = new NameValuePair("name", "value");
        nvpCopy.setId(nvp.getId());
        assertFalse("nvp not equals nvpCopy", nvp.equals(nvpCopy));

        nvpCopy = new NameValuePair("name", "value");
        assertFalse("nvp not equals nvpCopy", nvp.equals(nvpCopy));

        assertFalse("nvp not equals nvpCopy", nvp.equals(new User()));
        assertFalse("nvp not equals nvpCopy", nvp.equals(null));
    }


    @Test
    public void testToString() {
        assertNotNull("toString", nvp.toString());
    }

    @Before
    public void setUp() throws Exception {
        nvp = new NameValuePair("name", "value");
    }

    @After
    public void tearDown() throws Exception {
        nvp = null;
    }

}
