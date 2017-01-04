package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.CustomField;
import org.junit.Test;

import java.util.List;
import static org.itracker.Assert.*;

public class CustomFieldDAOImplIT extends AbstractDependencyInjectionTest {

    private CustomFieldDAO customFieldDAO;

    @Test
    public void testFindByPrimaryKey() {
        CustomField customField = customFieldDAO.findByPrimaryKey(1);

        assertNotNull(customField);

        assertEquals(CustomField.Type.STRING, customField.getFieldType());
        assertEquals("dateonly", customField.getDateFormat());
        assertEquals(true, customField.isRequired());
        assertEquals(true, customField.isSortOptionsByName());
    }

    @Test
    public void testFindAll() {
        List<CustomField> customFields = customFieldDAO.findAll();

        assertEquals(4, customFields.size());

        CustomField customField = customFields.get(0);

        assertEquals(1, customField.getId().intValue());
        assertEquals(CustomField.Type.STRING, customField.getFieldType());
        assertEquals("dateonly", customField.getDateFormat());
        assertEquals(true, customField.isRequired());
        assertEquals(true, customField.isSortOptionsByName());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        customFieldDAO = (CustomFieldDAO) applicationContext.getBean("customFieldDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/customfieldbean_dataset.xml"
        };
    }


}
