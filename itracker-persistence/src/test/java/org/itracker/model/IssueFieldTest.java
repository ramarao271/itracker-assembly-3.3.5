package org.itracker.model;

import org.itracker.IssueException;
import org.itracker.core.resources.ITrackerResources;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.itracker.Assert.*;

public class IssueFieldTest {
    private IssueField iss;

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
    public void testSetCustomField() {
        try {
            iss.setCustomField(null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetDateValue() {
        Date date = new Date(100000000);
        iss.setDateValue(date);
        assertEquals("date value", date, iss.getDateValue());

        date = null;
        iss.setDateValue(date);
        assertNull("date value is null", iss.getDateValue());
    }

    @Test
    public void testSetDateValue() {
        Date date = new Date(100000000);
        iss.setDateValue(date);
        assertEquals("date value", date, iss.getDateValue());

        date = null;
        iss.setDateValue(date);
        assertNull("date value is null", iss.getDateValue());
    }

    @Test
    public void testGetValue() {
        Locale en = new Locale("en");
        CustomField cust = new CustomField();
        cust.setFieldType(CustomField.Type.INTEGER);
        iss.setCustomField(cust);
        iss.setIntValue(23);
        assertEquals("int value 23", "23", iss.getValue(en));

        cust.setFieldType(CustomField.Type.DATE);
        cust.setDateFormat(CustomField.DateFormat.DATE.code);
        SimpleDateFormat sdf =
                new SimpleDateFormat(ITrackerResources.getBundle(en)
                        .getString("itracker.dateformat."
                                + cust.getDateFormat()), en);

        final Date date = new Date(10000);
        final String dateString = sdf.format(date);

        iss.setDateValue(date);
        assertEquals("date value", dateString, iss.getValue(en));

        cust.setRequired(false);
        iss.setDateValue(null);
        assertNull("date value is null", iss.getValue(en));

        cust.setRequired(true);
        iss.setDateValue(null);
        assertNotNull("date value is not null", iss.getValue(en));

    }

    @Test
    public void testGetStringValue() {
        CustomField cust = new CustomField();
        iss.setCustomField(cust);
        iss.setStringValue(null);
        cust.setFieldType(CustomField.Type.STRING);
        assertNull("iss.stringValue", iss.getStringValue());

        cust.setFieldType(CustomField.Type.STRING);
        iss.setCustomField(cust);
        iss.setStringValue("value");
        assertEquals("iss.stringValue", "value", iss.getStringValue());
    }

    @Test
    public void testSetValue() {
        //test type is integer
        Locale en = new Locale("en");
        CustomField cust = new CustomField();
        cust.setFieldType(CustomField.Type.INTEGER);
        iss.setCustomField(cust);
        try {
            iss.setValue("23", ITrackerResources.getBundle(en));
        } catch (IssueException e) {
            fail("throw IssueException" + e);
        }
        assertTrue("23".equals(iss.getValue(en)));

        //test wrong number
        try {
            iss.setValue("ww", ITrackerResources.getBundle(en));
        } catch (IssueException e) {
            assertTrue(true);
        }

        //test type is date
        cust.setFieldType(CustomField.Type.DATE);
        cust.setDateFormat(CustomField.DateFormat.DATE.code);
        SimpleDateFormat sdf =
                new SimpleDateFormat(ITrackerResources.getBundle(en)
                        .getString("itracker.dateformat."
                                + cust.getDateFormat()), en);

        final String dateString = sdf.format(new Date(0));

        try {
            iss.setValue(dateString, ITrackerResources.getBundle(en));
        } catch (IssueException e) {
            fail("throw IssueException" + e);
        }
        try {
            assertEquals("date value", sdf.parseObject(dateString), iss.getDateValue());
        } catch (ParseException e) {
            fail("throw ParseException" + e);
        }
        //test wrong date
        try {
            iss.setValue("xxxx" + dateString, ITrackerResources.getBundle(en));
        } catch (Exception e) {
            assertTrue(true);
        }


        //test value is empty
        try {
            iss.setValue(null, ITrackerResources.getBundle(en));
            assertEquals("", iss.getStringValue());
            assertNull(iss.getDateValue());
            assertEquals(Integer.valueOf(0), iss.getIntValue());
        } catch (IssueException e) {
            fail("throw IssueException" + e);
        }
    }

    @Test
    //test for method setValue(String value, ResourceBundle bundle)
    public void testSetValueByResourceBundle() throws IssueException {
        Locale en = new Locale("en");
        CustomField cust = new CustomField();
        cust.setFieldType(CustomField.Type.INTEGER);
        iss.setCustomField(cust);
        iss.setValue("23", ITrackerResources.getBundle(en));
        assertTrue("23".equals(iss.getValue(en)));
    }


    @Test
    public void testToString() {
        assertNotNull("toString", iss.toString());
    }

    @Before
    public void setup() throws Exception {
        iss = new IssueField();
    }


}
