package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SystemConfigurationTest {
    private SystemConfiguration conf;

    @Test
    public void testAddRESOLUTIONConfiguration() {
        assertEquals("Resolutions size 0", 0, conf.getResolutions().size());
        Configuration con = new Configuration();
        con.setType(Configuration.Type.resolution);
        conf.addConfiguration(con);
        assertEquals("Resolutions size 1", 1, conf.getResolutions().size());
        assertEquals("Resolutions type TYPE_RESOLUTION", Configuration.Type.resolution, conf.getResolutions().get(0).getType());
        assertEquals("Resolutions", con, conf.getResolutions().get(0));

        // fallback support
        con = new Configuration();
        con.setType(Configuration.Type.resolution);
        conf.addConfiguration(con);
        assertEquals("Resolutions size 2", 2, conf.getResolutions().size());
        assertEquals("Resolutions type TYPE_RESOLUTION", Configuration.Type.resolution, conf.getResolutions().get(1).getType());
        assertEquals("Resolutions", con, conf.getResolutions().get(1));

    }

    @Test
    public void testAddSEVERITYConfiguration() {
        assertEquals("SEVERITY size 0", 0, conf.getSeverities().size());
        Configuration con = new Configuration();
        con.setType(Configuration.Type.severity);
        conf.addConfiguration(con);
        assertEquals("SEVERITY size 1", 1, conf.getSeverities().size());
        assertEquals("SEVERITY type TYPE_SEVERITY", Configuration.Type.severity, conf.getSeverities().get(0).getType());
    }

    @Test
    public void testAddSTATUSConfiguration() {
        assertEquals("STATUS size 0", 0, conf.getStatuses().size());
        Configuration con = new Configuration();
        con.setType(Configuration.Type.status);
        conf.addConfiguration(con);
        assertEquals("STATUS size 1", 1, conf.getStatuses().size());
        assertEquals("STATUS type TYPE_STATUS", Configuration.Type.status, conf.getStatuses().get(0).getType());
    }

    @Test
    public void testAddNullConfiguration() {
        conf.addConfiguration(null);
        assertEquals("Resolutions size 0", 0, conf.getResolutions().size());
        assertEquals("SEVERITY size 0", 0, conf.getSeverities().size());
        assertEquals("STATUS size 0", 0, conf.getStatuses().size());
    }


    @Test
    public void testToString() {
        assertNotNull("toString", conf.toString());
    }


    @Before
    public void setUp() throws Exception {
        conf = new SystemConfiguration();
    }

    @After
    public void tearDown() throws Exception {
        conf = null;
    }

}
