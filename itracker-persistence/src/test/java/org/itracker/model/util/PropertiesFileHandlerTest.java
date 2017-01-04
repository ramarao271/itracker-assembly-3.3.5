package org.itracker.model.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class PropertiesFileHandlerTest {
    private PropertiesFileHandler propFileHandler;

    @Test
    public void testAddProperties() {
        //test add properties
        propFileHandler.addProperties("/configuration.properties");
        Properties prop = propFileHandler.getProperties();
        assertNotNull("property not null", prop);
        assertNotNull("project not null", prop.getProperty("project"));
        assertEquals("project value", "itracker", prop.getProperty("project"));
    }

    //test add null properties
    @Test
    public void testAddNullPoperties() {
        propFileHandler.addProperties(null);
        assertFalse("has hasProperties", propFileHandler.hasProperties());
    }

    //test add empty properties
    @Test
    public void testAddEmptyPoperties() {
        propFileHandler.addProperties("");
        assertFalse("has hasProperties", propFileHandler.hasProperties());
    }

    //test add none properties properties
    @Test
    public void testAddNonePoperties() {
        propFileHandler.addProperties("/aa.properties");
        assertFalse("has hasProperties", propFileHandler.hasProperties());
    }

    //test add not exist properties properties
    @Test
    public void testAddNotExistPoperties() {
        propFileHandler.addProperties("/src/main/resources/application-context.xml");
        assertFalse("has hasProperties", propFileHandler.hasProperties());
    }

    @Test
    public void testGetProperties() {
        assertNotNull("property not null", propFileHandler.getProperties());
    }

    @Test
    public void testGetProperty() {
        assertNull("project null", propFileHandler.getProperty("project"));
        propFileHandler.addProperties("/configuration.properties");
        assertNotNull("project not null", propFileHandler.getProperty("project"));
        assertEquals("project value", "itracker", propFileHandler.getProperty("project"));
    }

    @Test
    public void testHasProperty() {
        assertFalse("project property exist", propFileHandler.hasProperty("project"));
        propFileHandler.addProperties("/configuration.properties");
        assertTrue("project exist", propFileHandler.hasProperty("project"));
    }

    @Test
    public void testHasProperties() {
        assertFalse("properties exist", propFileHandler.hasProperties());
        propFileHandler.addProperties("/configuration.properties");
        assertTrue("properties exist", propFileHandler.hasProperties());
    }

    @Before
    public void setUp() throws Exception {
        propFileHandler = new PropertiesFileHandler();
    }

    @After
    public void tearDown() throws Exception {
        propFileHandler = null;
    }


}
