package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.UserPreferences;
import org.junit.Test;

import static org.itracker.Assert.*;
public class UserPreferencesDAOImplIT extends AbstractDependencyInjectionTest {

    private UserPreferencesDAO userPreferencesDAO;

    @Test
    public void testFindByUserID() {
        UserPreferences userPreferences = userPreferencesDAO.findByUserId(2);

        assertNotNull(userPreferences);

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        userPreferencesDAO = (UserPreferencesDAO) applicationContext.getBean("userPreferencesDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml"
        };
    }


}
