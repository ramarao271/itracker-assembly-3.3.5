package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class WorkflowScriptTest {
    private WorkflowScript wst;

    @Test
    public void testToString() {
        assertNotNull("toString", wst.toString());
    }

    @Before
    public void setUp() throws Exception {
        wst = new WorkflowScript();
    }

    @After
    public void tearDown() throws Exception {
        wst = null;
    }

}
