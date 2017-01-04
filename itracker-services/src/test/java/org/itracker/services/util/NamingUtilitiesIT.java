/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.services.util;

import org.itracker.services.AbstractServicesIntegrationTest;
import org.itracker.util.NamingUtilites;
import org.junit.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import static org.itracker.Assert.*;

// TODO: Add Javadocs here: what is the purpose of this class?

/**
 * @author seas
 */
public class NamingUtilitiesIT extends AbstractServicesIntegrationTest {

    @Test
    public void testGetStringValue() {
        final Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("key", "value");
        try {
            final InitialContext context = new InitialContext(hashtable);
            final String value = NamingUtilites.getStringValue(context, "key", "value");
            assertEquals("value", value);
        } catch (final NamingException e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /**
     * Defines a set of datafiles to be uploaded into database.
     *
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_dataset.xml"
        };
    }

}
