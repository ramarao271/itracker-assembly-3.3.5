package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.CustomFieldValue;
import org.junit.Test;

import java.util.List;
import static org.itracker.Assert.*;

public class CustomFieldValueDAOImplIT extends AbstractDependencyInjectionTest {

    private CustomFieldValueDAO customFieldValueDAO;

    @Test
    public void testFindByPrimaryKey() {
        CustomFieldValue customFieldValue = customFieldValueDAO.findByPrimaryKey(1);
        assertNotNull(customFieldValue);
        assertNotNull("customFieldValue.id", customFieldValue.getId());
        assertEquals("customFieldValue.id", new Integer(1), customFieldValue.getId());
    }

    @Test
    public void testFindAll() {
        List<CustomFieldValue> customFieldValues = customFieldValueDAO.findAll();
        assertNotNull(customFieldValues);
        assertEquals("total custom field values", 3, customFieldValues.size());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        customFieldValueDAO = (CustomFieldValueDAO) applicationContext.getBean("customFieldValueDAO");
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/customfieldbean_dataset.xml",
                "dataset/customfieldvaluebean_dataset.xml",
        };
    }


}
