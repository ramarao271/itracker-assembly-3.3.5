package org.itracker.model;

import org.junit.Before;
import org.junit.Test;


import static org.itracker.Assert.*;

public class CustomFieldValueTest {
    private CustomFieldValue cust;

    @Test
    public void testSetCustomField() {
        try {
            cust.setCustomField(null);
            fail("did not throw IllegalArgumentException ");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetValue() {
        try {
            cust.setValue(null);
            fail("did not throw IllegalArgumentException ");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSortOrderComparator() throws Exception {
        CustomFieldValue valueA, valueB;

        CustomField fieldA = new CustomField();
        CustomField fieldB = new CustomField();

        fieldA.setId(0);
        fieldB.setId(1);

        valueA = new CustomFieldValue(fieldA, "1");
        valueA.setSortOrder(1);
        valueB = new CustomFieldValue(fieldB, "2");
        valueB.setSortOrder(2);
        valueA.setId(1);
        valueB.setId(2);


        assertEntityComparator("name comparator", CustomFieldValue.SORT_ORDER_COMPARATOR,
                valueA, valueB);
        assertEntityComparator("name comparator", CustomFieldValue.SORT_ORDER_COMPARATOR,
                valueA, null);

        valueA.setSortOrder(valueB.getSortOrder());
        assertEntityComparatorEquals("name comparator",
                CustomFieldValue.SORT_ORDER_COMPARATOR, valueA, valueB);
        assertEntityComparatorEquals("name comparator",
                CustomFieldValue.SORT_ORDER_COMPARATOR, valueA, valueB);

    }

    @Test
    public void testToString() {
        assertNotNull(cust.toString());
    }


    @Before
    public void onSetUp() throws Exception {
        cust = new CustomFieldValue();
    }


}
