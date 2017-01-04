package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LanguageTest {
    private Language lan;

    @Test
    public void testSetLocale() {
        try {
            lan.setLocale(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetResourceKey() {
        try {
            lan.setResourceKey(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testToString() {
        assertNotNull("toString", lan.toString());
    }


    @Before
    public void setUp() throws Exception {
        lan = new Language();
    }

    @After
    public void tearDown() throws Exception {
        lan = null;
    }

}
