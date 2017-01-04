package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {
    private Version ver;

    @Test
    public void testSetProject() {
        try {
            ver.setProject(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetStatus() {
        try {
            ver.setStatus(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetVersionInfo() {
        ver.setVersionInfo("1.2");
        assertNotNull("version number is not null", ver.getNumber());
        assertEquals("version number 1.2", "1.2", ver.getNumber());
        assertEquals("major 1", 1, ver.getMajor());
        assertEquals("minor 2", 2, ver.getMinor());
    }

    @Test
    public void testSetNullVersionInfo() {
        //set version info null
        try {
            ver.setVersionInfo(null);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertNull("version number is  null", ver.getNumber());
        assertEquals("major 0", 0, ver.getMajor());
        assertEquals("minor 0", 0, ver.getMinor());
    }

    @Test
    public void testSetEmptyVersionInfo() {
        //set version info empty
        ver.setVersionInfo("");
        assertNotNull("version number is not null", ver.getNumber());
        assertEquals("version number is empty", "", ver.getNumber());
        assertEquals("major 0", 0, ver.getMajor());
        assertEquals("minor 0", 0, ver.getMinor());
    }

    @Test
    public void testSetWrongVersionInfo() {
        //set version info wrong format
        ver.setVersionInfo("V1.2");
        assertNotNull("version number is not null", ver.getNumber());
        assertEquals("version number is V1.2", "V1.2", ver.getNumber());
        assertEquals("major 0", 0, ver.getMajor());
        assertEquals("minor 2", 2, ver.getMinor());

        ver.setVersionInfo("V12");
        assertNotNull("version number is not null", ver.getNumber());
        assertEquals("version number is V12", "V12", ver.getNumber());
        assertEquals("major 0", 0, ver.getMajor());
        assertEquals("minor 0", 0, ver.getMinor());
    }

    @Test
    public void testToString() {
        assertNotNull("toString", ver.toString());
    }


    @Before
    public void setUp() throws Exception {
        ver = new Version();
    }

    @After
    public void tearDown() throws Exception {
        ver = null;
    }

}
