package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.itracker.ITrackerDirtyResourceException;
import org.junit.Test;

public class ItrackerDirtyResourceExceptionTest extends TestCase {

    @Test
    public void testConstructor() {

        ITrackerDirtyResourceException e =
                new ITrackerDirtyResourceException("my_message", "my_class", "my_key");

        assertTrue(e instanceof Exception);

        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.className", "my_class", e.getClassName());
        assertEquals("e.key", "my_key", e.getKey());

    }


}
