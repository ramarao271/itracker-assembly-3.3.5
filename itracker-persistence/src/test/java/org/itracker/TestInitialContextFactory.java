package org.itracker;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;


public final class TestInitialContextFactory implements
        InitialContextFactory {

    public static SimpleNamingContextBuilder namingContext;
    static {
        try {
            namingContext =
                    SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        } catch (NamingException e) {

        }
    }


    public TestInitialContextFactory() {

    }

    public Context getInitialContext(Hashtable<?, ?> environment)
            throws NamingException {
        if (null == SimpleNamingContextBuilder.getCurrentContextBuilder()) {
            SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        }
        SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.getCurrentContextBuilder();
//            builder.bind("java:comp", new InitialContext());
        return builder.createInitialContextFactory(environment)
                .getInitialContext(environment);
    }

    public static SimpleNamingContextBuilder getNamingContext() {
        return namingContext;
    }
}
