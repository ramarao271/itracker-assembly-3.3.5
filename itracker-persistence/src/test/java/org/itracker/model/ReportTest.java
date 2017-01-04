package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.itracker.Assert.assertEntityComparator;
import static org.itracker.Assert.assertEntityComparatorEquals;
import static org.junit.Assert.assertNotNull;

public class ReportTest {
    private Report rpt;

    @Test
    public void testToString() {
        assertNotNull("toString", rpt.toString());
    }

    @Test
    public void testNameComparator() {
        Report entityA = new Report();
        Report entityB = new Report();
        entityA.setName("a");
        entityB.setName("b");

        assertEntityComparator("name comparator", Report.NAME_COMPARATOR, entityA, entityB);
        assertEntityComparator("name comparator", Report.NAME_COMPARATOR, entityA, null);
        assertEntityComparatorEquals("name comparator", Report.NAME_COMPARATOR, entityA, entityA);

        entityA.setName(entityB.getName());
        entityA.setNameKey("a");
        entityB.setNameKey("b");

        assertEntityComparator("name comparator", Report.NAME_COMPARATOR, entityA, entityB);
        assertEntityComparator("name comparator", Report.NAME_COMPARATOR, entityA, null);
        assertEntityComparatorEquals("name comparator", Report.NAME_COMPARATOR, entityA, entityA);

        entityA.setNameKey(entityB.getNameKey());
        entityA.setId(1);
        entityB.setId(2);

        assertEntityComparator("name comparator", Report.NAME_COMPARATOR, entityA, entityB);
        assertEntityComparator("name comparator", Report.NAME_COMPARATOR, entityA, null);

        entityA.setId(entityB.getId());

        assertEntityComparatorEquals("name comparator", Report.NAME_COMPARATOR, entityA, entityB);
        assertEntityComparatorEquals("name comparator", Report.NAME_COMPARATOR, entityA, entityA);

    }

    @Before
    public void setUp() throws Exception {
        rpt = new Report();
    }

    @After
    public void tearDown() throws Exception {
        rpt = null;
    }

}
