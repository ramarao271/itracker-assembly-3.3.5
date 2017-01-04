package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Permission;
import org.itracker.model.Project;
import org.itracker.model.User;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import java.util.List;
import static org.itracker.Assert.*;

public class PermissionDAOImplIT extends AbstractDependencyInjectionTest {

    @SuppressWarnings("unused")
    private UserDAO userDAO;
    @SuppressWarnings("unused")
    private ProjectDAO projectDAO;
    private PermissionDAO permissionDAO;

    @Test
    public void testFindByUserId() {

        List<Permission> permissions = permissionDAO.findByUserId(2);

        assertNotNull("permissions", permissions);
        assertEquals(3, permissions.size());

    }

    // TODO: fix implementation or test
    @Test
    public void testFailedFindByUserId() {

        try {
            List<Permission> permissions = permissionDAO.findByUserId(-1);
            fail("Should have thrown a DataAccessException. Size of list:"
                    + permissions.size());
        } catch (DataAccessException e) {
            // Expected behavior
        }
    }

    @Test
    public void testCreatePermission() {

        Permission foundPermission = permissionDAO.findByUserId(2).get(1);
        User foundUser = foundPermission.getUser();
        Project foundProject = foundPermission.getProject();

        assertNotNull(foundPermission);
        assertNotNull(foundUser);
        assertNotNull(foundProject);
        assertEquals(Integer.valueOf(2), foundPermission.getPermissionType().getCode());
        assertEquals("admin_test1", foundUser.getLogin());
        assertEquals("test_name", foundProject.getName());

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();
        userDAO = (UserDAO) applicationContext.getBean("userDAO");
        projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
        permissionDAO = (PermissionDAO) applicationContext
                .getBean("permissionDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml"};
    }


}
