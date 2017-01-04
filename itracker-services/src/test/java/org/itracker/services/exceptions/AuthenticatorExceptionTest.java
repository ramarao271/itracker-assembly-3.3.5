package org.itracker.services.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class AuthenticatorExceptionTest extends TestCase {

    @Test
    public void testConstructor() {
        AuthenticatorException e = new AuthenticatorException();
        assertTrue(e instanceof Exception);

        e = new AuthenticatorException(1);
        assertEquals("e.type", 1, e.getType());

        e = new AuthenticatorException(1, "my_key");
        assertEquals("e.type", 1, e.getType());
        assertEquals("e.messageKey", "my_key", e.getMessageKey());

        e = new AuthenticatorException("my_message", 1);
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.type", 1, e.getType());

        e = new AuthenticatorException("my_message", 1, "my_key");
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.messageKey", "my_key", e.getMessageKey());
        assertEquals("e.type", 1, e.getType());

        Throwable cause = new Throwable();
        e = new AuthenticatorException("my_message", 1, cause);
        assertEquals("e.message", "my_message", e.getMessage());
        assertEquals("e.type", 1, e.getType());
        assertSame("e.cause", cause, e.getCause());


    }


    @Test
    public void testSetErrorPageType() {
        AuthenticatorException e = new AuthenticatorException();
        e.setErrorPageType(10);
        assertEquals("e.errorPageType", 10, e.getErrorPageType());
    }

    @Test
    public void testSetErrorPageValue() {
        AuthenticatorException e = new AuthenticatorException();
        e.setErrorPageValue("my_value");
        assertEquals("e.errorPageValue", "my_value", e.getErrorPageValue());
    }

    @Test
    public void testSetMessageKey() {
        AuthenticatorException e = new AuthenticatorException();
        e.setMessageKey("my_key");
        assertEquals("e.messageKey", "my_key", e.getMessageKey());
    }

    @Test
    public void testSetType() {
        AuthenticatorException e = new AuthenticatorException();
        e.setType(1);
        assertEquals("e.type", 1, e.getType());
    }

    @Test
    public void testGetMessage() {
        AuthenticatorException e = new AuthenticatorException();
        for (int j = -7; j <= 2; j++) {
            e.setType(j);
            assertNotNull("e.message", e.getMessage());
        }
    }
}

