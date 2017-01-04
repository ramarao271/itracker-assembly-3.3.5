package org.itracker.persistence.dao;


import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Configuration;
import org.junit.Test;

import java.util.List;
import static org.itracker.Assert.*;

public class ConfigurationDAOImplIT extends AbstractDependencyInjectionTest {

    private ConfigurationDAO configurationDAO;

    @Test
    public void testFindByPrimaryKey() {
        Configuration configuration = configurationDAO.findByPrimaryKey(2000);

        assertNotNull(configuration);

        assertEquals("configuration.type.code", (Object)1, configuration.getType().getCode());
        assertEquals("configuration.type", Configuration.Type.locale, configuration.getType());
        assertEquals("Test Value", configuration.getValue());
        assertEquals("Version 1.0", configuration.getVersion());
        assertEquals(1, configuration.getOrder());
    }

    @Test
    public void testFindByType() {
        final List<Configuration> configurations = configurationDAO.findByType(Configuration.Type.locale);
        assertEquals("configurationDAO.findByType(Configuration.Type.status).size", 2, configurations.size());

        final Configuration configuration = configurations.get(0);
        assertEquals("configuration.id", 2000, configuration.getId().intValue());
        assertEquals("configuration.value", "Test Value", configuration.getValue());

        assertEquals("configuration.version", "Version 1.0", configuration.getVersion());
        assertEquals("configuration.order", 1, configuration.getOrder());

        // old code checks

        List<Configuration> configurations1 = configurationDAO.findByType(1);
        assertEquals("configurationDAO.findByType(1).size", 2, configurations1.size());
    }

    @Test
    public void testFindByTypeAndValue() {

        Configuration configuration = configurationDAO.findByTypeValueKey(Configuration.Type.locale, "Test Value");

        assertNotNull("configurationDAO.findByTypeValueKey(Configuration.Type.locale, 'Test Value')", configuration);


        // assertEquals(2000, configuration.getId().intValue());
        assertEquals("Test Value", configuration.getValue());
        assertEquals("Version 1.0", configuration.getVersion());
        assertEquals(1, configuration.getOrder());

        // old code checks
        List<Configuration> configurations = configurationDAO.findByTypeAndValue(1, "Test Value");
        assertEquals("configurationDAO.findByTypeAndValue(1, 'Test Value').size", 1, configurations.size());
        configurations = configurationDAO.findByTypeAndValue(Configuration.Type.locale, "Test Value");
        assertEquals("configurationDAO.findByTypeAndValue(Configuration.Type.locale, 'Test Value').size", 1, configurations.size());

        Configuration configuration1 = configurations.get(0);
        assertEquals(configuration1, configuration);
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        configurationDAO = (ConfigurationDAO) applicationContext.getBean("configurationDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/configurationbean_dataset.xml"
        };
    }


}
