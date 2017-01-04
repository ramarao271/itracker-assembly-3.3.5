package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.*;

public class ProjectTest {
    private Project pro;

    @Test
    public void testSetName() {
        try {
            pro.setName(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetStatus() {
        try {
            pro.setStatus(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testToString() {
        assertNotNull("toString", pro.toString());
    }

    @Test
    public void testProjectComparator() {
        Project entityA = new Project("a");
        Project entityB = new Project("b");

        assertEntityComparator("project comparator", Project.PROJECT_COMPARATOR, entityA, entityB);
        assertEntityComparator("project comparator", Project.PROJECT_COMPARATOR, entityA, null);
        assertEntityComparatorEquals("project comparator", Project.PROJECT_COMPARATOR, entityA, entityA);

        entityA.setName(entityB.getName());
        entityA.setId(1);
        entityB.setId(2);

        assertEntityComparator("project comparator", Project.PROJECT_COMPARATOR, entityA, entityB);
        assertEntityComparator("project comparator", Project.PROJECT_COMPARATOR, entityA, null);

        entityB.setId(entityA.getId());
        assertEntityComparatorEquals("project comparator", Project.PROJECT_COMPARATOR, entityA, entityB);
        assertEntityComparatorEquals("project comparator", Project.PROJECT_COMPARATOR, entityA, entityA);
    }

    @Before
    public void setUp() throws Exception {
        pro = new Project();
    }

    @After
    public void tearDown() throws Exception {
        pro = null;
    }

}
