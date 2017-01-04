package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IssueRelationTest {
    private IssueRelation iss;

    @Test
    public void testSetIssue() {
        try {
            iss.setIssue(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetRelatedIssue() {
        try {
            iss.setRelatedIssue(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testToString() {
        assertNotNull("toString", iss.toString());
    }


    @Before
    public void setUp() throws Exception {
        iss = new IssueRelation();
    }

    @After
    public void tearDown() throws Exception {
        iss = null;
    }

}
