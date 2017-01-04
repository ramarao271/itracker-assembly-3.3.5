package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class UserPreferenceTest {
    private UserPreferences pre;


    @Test
    public void testToString() {
        assertNotNull("toString", pre.toString());
    }


    @Before
    public void setUp() throws Exception {
        pre = new UserPreferences();
    }

    @After
    public void tearDown() throws Exception {
        pre = null;
    }

}
