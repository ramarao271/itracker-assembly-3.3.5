package org.itracker.core.resources.test;

import org.itracker.core.resources.MessageFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MessageFormatTest {
    @Test
    public void testFormat() {
        Object[] options = {"administrator", "administer", "the network"};
        String message = MessageFormat.format("The {0} {1} {2}", options);
        assertEquals("The administrator administer the network", message);
    }

    @Test
    public void testWrongOptions() {
        Object[] options = {};
        String message = MessageFormat.format("The {0} {1} {2}", options);
        assertEquals("The {0} {1} {2}", message);
    }

    @Test
    public void testNumberFormat() {
        Integer a = new Integer(1);
        Integer b = new Integer(2);
        Integer c = a + b;
        Object[] options = {a, b, c};
        String message = MessageFormat.format("{0,number} + {1,number} equals {2,number}", options);
        assertEquals(a + " + " + b + " equals " + c, message);
    }

    @Test
    public void testDecimalFormat() {
        Double a = new Double(1);
        Double b = new Double(2);
        Double c = a - b;
        Object[] options = {a, b, c};
        String message = MessageFormat.format("{0,number,#.##} - {1,number,#.##} equals {2,number,#.##;(#.##)}", options);
        assertEquals("1 - 2 equals (1)", message);
    }

    @Test
    public void testWrongDecimalFormat() {
        Double a = new Double(((double) 1) / 3);
        Object[] options = {a};
        try {
            MessageFormat.format("{0,number,\\.\\.\\.}", options);

            fail("no exception");
        } catch (IllegalArgumentException iae) {
            // fine
        }
    }

}

 	  	 
