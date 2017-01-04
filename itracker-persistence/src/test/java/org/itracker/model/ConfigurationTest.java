package org.itracker.model;

import org.itracker.model.util.IssueUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.itracker.model.util.IssueUtilities.FIELD_STATUS;
import static org.junit.Assert.*;

public class ConfigurationTest {
    private Configuration conf;


    @Test
    public void testType() {
        Configuration.Type type = Configuration.Type.status;
        assertEquals("status.name", "status", type.name());
        assertEquals("status.code", (Integer)2, type.getCode());
        assertEquals("status.legacyCode", (Integer)FIELD_STATUS, type.getLegacyCode());
        assertEquals("Configuration.Type.valueOf(2)",
                type, Configuration.Type.valueOf(type.getCode()));
        assertEquals("Configuration.Type.valueOf('status')", type, Configuration.Type.valueOf("status"));

    }


    @Test
    public void testSetValue() {
        try {
            conf.setValue(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testSetVersion() {
        try {
            conf.setVersion(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testToString() {
        assertNotNull(conf.toString());
    }

    @Test
    public void SortOrderComparator() throws Exception {
        Configuration configurationB = new Configuration();
        configurationB.setOrder(conf.getOrder() + 1);

        assertEntityComparator("sort order",
                Configuration.CONFIGURATION_ORDER_COMPARATOR, conf,
                configurationB);
        assertEntityComparator("sort order",
                Configuration.CONFIGURATION_ORDER_COMPARATOR, conf, null);

        configurationB.setOrder(conf.getOrder());
        assertEntityComparatorEquals("sort order",
                Configuration.CONFIGURATION_ORDER_COMPARATOR, conf,
                configurationB);
    }

    @Before
    public void setUp() throws Exception {
        conf = new Configuration();
    }

    @After
    public void tearDown() throws Exception {
        conf = null;
    }

}
