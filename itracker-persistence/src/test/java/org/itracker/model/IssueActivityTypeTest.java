package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IssueActivityTypeTest {

    @Test
    public void testForCode() {
        // TODO: Remove call to depreciated code
        assertEquals(IssueActivityType.ISSUE_CREATED, IssueActivityType.forCode(1));
        assertEquals(IssueActivityType.STATUS_CHANGE, IssueActivityType.forCode(2));
        assertEquals(IssueActivityType.OWNER_CHANGE, IssueActivityType.forCode(3));
        assertEquals(IssueActivityType.SEVERITY_CHANGE, IssueActivityType.forCode(4));
        assertEquals(IssueActivityType.COMPONENTS_MODIFIED, IssueActivityType.forCode(5));
        assertEquals(IssueActivityType.VERSIONS_MODIFIED, IssueActivityType.forCode(6));
        assertEquals(IssueActivityType.REMOVE_HISTORY, IssueActivityType.forCode(7));
        assertEquals(IssueActivityType.ISSUE_MOVE, IssueActivityType.forCode(8));
        assertEquals(IssueActivityType.SYSTEM_UPDATE, IssueActivityType.forCode(9));
        assertEquals(IssueActivityType.TARGETVERSION_CHANGE, IssueActivityType.forCode(10));
        assertEquals(IssueActivityType.DESCRIPTION_CHANGE, IssueActivityType.forCode(11));
        assertEquals(IssueActivityType.RESOLUTION_CHANGE, IssueActivityType.forCode(12));
        assertEquals(IssueActivityType.RELATION_ADDED, IssueActivityType.forCode(13));
        assertEquals(IssueActivityType.RELATION_REMOVED, IssueActivityType.forCode(14));
        assertEquals(IssueActivityType.ATTACHMENT_ADDED, IssueActivityType.forCode(15));
        assertEquals(null, IssueActivityType.forCode(16));
    }

    @Test
    public void testFromCode() {
        assertEquals(IssueActivityType.ISSUE_CREATED, IssueActivityType.ISSUE_CREATED.fromCode(1));
        assertEquals(IssueActivityType.STATUS_CHANGE, IssueActivityType.ISSUE_CREATED.fromCode(2));
        assertEquals(IssueActivityType.OWNER_CHANGE, IssueActivityType.ISSUE_CREATED.fromCode(3));
        assertEquals(IssueActivityType.SEVERITY_CHANGE, IssueActivityType.ISSUE_CREATED.fromCode(4));
        assertEquals(IssueActivityType.COMPONENTS_MODIFIED, IssueActivityType.ISSUE_CREATED.fromCode(5));
        assertEquals(IssueActivityType.VERSIONS_MODIFIED, IssueActivityType.ISSUE_CREATED.fromCode(6));
        assertEquals(IssueActivityType.REMOVE_HISTORY, IssueActivityType.ISSUE_CREATED.fromCode(7));
        assertEquals(IssueActivityType.ISSUE_MOVE, IssueActivityType.ISSUE_CREATED.fromCode(8));
        assertEquals(IssueActivityType.SYSTEM_UPDATE, IssueActivityType.ISSUE_CREATED.fromCode(9));
        assertEquals(IssueActivityType.TARGETVERSION_CHANGE, IssueActivityType.ISSUE_CREATED.fromCode(10));
        assertEquals(IssueActivityType.DESCRIPTION_CHANGE, IssueActivityType.ISSUE_CREATED.fromCode(11));
        assertEquals(IssueActivityType.RESOLUTION_CHANGE, IssueActivityType.ISSUE_CREATED.fromCode(12));
        assertEquals(IssueActivityType.RELATION_ADDED, IssueActivityType.ISSUE_CREATED.fromCode(13));
        assertEquals(IssueActivityType.RELATION_REMOVED, IssueActivityType.ISSUE_CREATED.fromCode(14));
        assertEquals(IssueActivityType.ATTACHMENT_ADDED, IssueActivityType.ISSUE_CREATED.fromCode(15));
        assertEquals(null, IssueActivityType.ISSUE_CREATED.fromCode(16));
    }


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

}
