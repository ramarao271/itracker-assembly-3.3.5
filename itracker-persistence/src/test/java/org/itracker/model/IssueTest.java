package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.assertNotNull;

public class IssueTest {
    private Issue iss;

    @Test
    public void testToString() {
        assertNotNull("toString", iss.toString());
    }

    @Test
    public void testOwnerAndStatusComparator() {
        Issue issueA, issueB;

        issueA = new Issue();
        issueA.setOwner(new User("aaa", "", "a", "a", "", false));
        issueA.setStatus(100);
        issueB = new Issue();
        issueB.setOwner(new User("bbb", "", "b", "b", "", false));
        issueB.setStatus(200);

        assertEntityComparator("owner status comparator",
                Issue.OWNER_AND_STATUS_COMPARATOR, issueA, issueB);
        assertEntityComparator("owner status comparator",
                Issue.OWNER_AND_STATUS_COMPARATOR, issueA, null);

        issueA.setOwner(issueB.getOwner());

        assertEntityComparator("owner status comparator",
                Issue.OWNER_AND_STATUS_COMPARATOR, issueA, issueB);
        assertEntityComparator("owner status comparator",
                Issue.OWNER_AND_STATUS_COMPARATOR, issueA, null);

        issueA.setStatus(issueB.getStatus());

        assertEntityComparatorEquals("owner status comparator",
                Issue.OWNER_AND_STATUS_COMPARATOR, issueA, issueB);
        assertEntityComparatorEquals("owner status comparator",
                Issue.OWNER_AND_STATUS_COMPARATOR, issueA, issueA);

    }

    @Test
    public void testProjectAndStatusComparator() {
        Issue issueA, issueB;

        issueA = new Issue();
        issueA.setProject(new Project("aaa"));
        issueA.setStatus(100);
        issueB = new Issue();
        issueB.setProject(new Project("bbb"));
        issueB.setStatus(200);


        assertEntityComparator("project status comparator",
                Issue.PROJECT_AND_STATUS_COMPARATOR, issueA, issueB);
        assertEntityComparator("project status comparator",
                Issue.PROJECT_AND_STATUS_COMPARATOR, issueA, null);

        issueA.setProject(issueB.getProject());

        assertEntityComparator("project status comparator",
                Issue.PROJECT_AND_STATUS_COMPARATOR, issueA, issueB);
        assertEntityComparator("project status comparator",
                Issue.PROJECT_AND_STATUS_COMPARATOR, issueA, null);


        issueA.setStatus(issueB.getStatus());

        assertEntityComparatorEquals("project status comparator",
                Issue.PROJECT_AND_STATUS_COMPARATOR, issueA, issueB);
        assertEntityComparatorEquals("project status comparator",
                Issue.PROJECT_AND_STATUS_COMPARATOR, issueA, issueA);
    }

    @Test
    public void testSeverityComparator() {
        Issue issueA, issueB;

        issueA = new Issue();
        issueA.setSeverity(800);
        issueA.setStatus(100);
        issueB = new Issue();
        issueB.setSeverity(900);
        issueB.setStatus(200);


        assertEntityComparator("severity comparator",
                Issue.SEVERITY_COMPARATOR, issueA, issueB);
        assertEntityComparator("severity comparator",
                Issue.SEVERITY_COMPARATOR, issueA, null);

        issueA.setSeverity(issueB.getSeverity());

        assertEntityComparator("severity comparator",
                Issue.SEVERITY_COMPARATOR, issueA, issueB);
        assertEntityComparator("severity comparator",
                Issue.SEVERITY_COMPARATOR, issueA, null);


        issueA.setStatus(issueB.getStatus());

        assertEntityComparatorEquals("severity comparator",
                Issue.SEVERITY_COMPARATOR, issueA, issueB);
        assertEntityComparatorEquals("severity comparator",
                Issue.SEVERITY_COMPARATOR, issueA, issueA);
    }


    @Before
    public void setUp() throws Exception {
        iss = new Issue();
    }

    @After
    public void tearDown() throws Exception {
        iss = null;
    }

}
