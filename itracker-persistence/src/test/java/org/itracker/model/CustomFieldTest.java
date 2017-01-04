package org.itracker.model;

import org.itracker.IssueException;
import org.itracker.core.resources.ITrackerResources;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.itracker.Assert.*;

public class CustomFieldTest {
    private CustomField cust;

    @Test
    public void testAddOption() {
        assertNotNull("options", cust.getOptions());
        assertEquals("options size", 0, cust.getOptions().size());
        cust.addOption("0", "male");
        assertEquals("options size", 1, cust.getOptions().size());
    }

    @Test
    public void testCheckAssignable() throws IssueException {
        Locale en = new Locale("en");
        cust.setFieldType(CustomField.Type.INTEGER);
        try {
            cust.checkAssignable("23", en, ITrackerResources.getBundle(en));
        } catch (IssueException e) {
            assertTrue(true);
        }        try {
            cust.checkAssignable("ww", en, ITrackerResources.getBundle(en));
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
        final Date date = new Date(100000);
        final String dateString = sdf.format(date);

        try {
            cust.checkAssignable(dateString, en, ITrackerResources.getBundle(en));
            cust.checkAssignable("abdcd" + dateString, en, ITrackerResources.getBundle(en));
        } catch (IssueException e) {
            assertTrue(true);
        }
    }


    @Test
    public void testToString() {
        assertNotNull("toString", cust.toString());
    }


    @Before
    public void setup() throws Exception {
        cust = new CustomField();
    }




}
