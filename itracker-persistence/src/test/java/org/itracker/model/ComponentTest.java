package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.*;

public class ComponentTest {
    private Component component;

    @Test
    public void testSetProject() {
        try {
            component.setProject(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetName() {
        try {
            component.setName(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetStatus() {
        try {
            component.setStatus(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testToString() {
        assertNotNull(component.toString());
    }

    @Test
    public void testNameComparator() throws Exception {
        Component componentA, componentB;

        componentA = new Component();
        componentB = new Component();

        componentA.setName("a");
        componentB.setName("b");

        assertEntityComparator("name comparator", Component.NAME_COMPARATOR,
                componentA, componentB);
        assertEntityComparator("name comparator", Component.NAME_COMPARATOR,
                componentA, null);

        componentA.setName(componentB.getName());
        assertEntityComparatorEquals("name comparator",
                Component.NAME_COMPARATOR, componentA, componentB);
        assertEntityComparatorEquals("name comparator",
                Component.NAME_COMPARATOR, componentA, componentA);

    }


    @Test
    public void testProjectNameComparator() throws Exception {
        Component componentA, componentB;

        componentA = new Component();
        componentB = new Component();

        componentA.setName("a");
        componentB.setName("b");

        componentA.setProject(new Project("a"));
        componentB.setProject(new Project("b"));

        assertEntityComparator("project name comparator", Component.PROJECTNAME_COMPARATOR,
                componentA, componentB);
        assertEntityComparator("project name comparator", Component.PROJECTNAME_COMPARATOR,
                componentA, null);

        componentA.setName(componentB.getName());

        assertEntityComparator("project name comparator", Component.PROJECTNAME_COMPARATOR,
                componentA, componentB);
        assertEntityComparator("project name comparator", Component.PROJECTNAME_COMPARATOR,
                componentA, null);

        componentA.getProject().setName(componentB.getProject().getName());
        assertEntityComparatorEquals("project name comparator",
                Component.PROJECTNAME_COMPARATOR, componentA, componentB);
        assertEntityComparatorEquals("project name comparator",
                Component.PROJECTNAME_COMPARATOR, componentA, componentA);

    }


    @Before
    public void setUp() throws Exception {
        component = new Component();
    }

    @After
    public void tearDown() throws Exception {
        component = null;
    }

}
