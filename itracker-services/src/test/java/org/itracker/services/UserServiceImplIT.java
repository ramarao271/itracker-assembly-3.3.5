package org.itracker.services;

import org.itracker.PasswordException;
import org.itracker.UserException;
import org.itracker.core.AuthenticationConstants;
import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.itracker.persistence.dao.PermissionDAO;
import org.itracker.persistence.dao.ProjectDAO;
import org.itracker.persistence.dao.UserDAO;
import org.itracker.persistence.dao.UserPreferencesDAO;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.itracker.Assert.*;
public class UserServiceImplIT extends AbstractServicesIntegrationTest {

    private UserService userService;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private PermissionDAO permissionDAO;
    private UserPreferencesDAO userPreferencesDAO;

    @Test
    public void getSuperUsers() {

        List<User> users = userService.getSuperUsers();

        assertNotNull(users);
        assertEquals(1, users.size());

    }


    @Test
    public void testGetPermissionsByUserId() {
        Integer userId = 3;
        Integer projectId = 2;
        List<Permission> currentPermissions;

        Project project = projectDAO.findByPrimaryKey(projectId);

        User user = userDAO.findByPrimaryKey(userId);

        List<Permission> assertedPermissions = new ArrayList<Permission>();
        assertedPermissions.add(new Permission(PermissionType.valueOf(1), user, project));
        assertedPermissions.add(new Permission(PermissionType.valueOf(2), user, project));
        assertedPermissions.add(new Permission(PermissionType.valueOf(3), user, project));

        currentPermissions = userService.getPermissionsByUserId(userId);
        assertEquals(assertedPermissions.get(0).getProject().getName(),
                currentPermissions.get(0).getProject().getName());

        assertEquals(assertedPermissions.get(1).getProject().getName(),
                currentPermissions.get(1).getProject().getName());

        assertEquals(assertedPermissions.get(2).getProject().getName(),
                currentPermissions.get(2).getProject().getName());

        assertEquals(assertedPermissions.get(0).getPermissionType(),
                currentPermissions.get(0).getPermissionType());

        assertEquals(assertedPermissions.get(1).getPermissionType(),
                currentPermissions.get(1).getPermissionType());

        assertEquals(assertedPermissions.get(2).getPermissionType(),
                currentPermissions.get(2).getPermissionType());

        assertEquals(assertedPermissions.get(0).getUser().getEmail(),
                currentPermissions.get(0).getUser().getEmail());

        assertEquals(assertedPermissions.get(1).getUser().getEmail(),
                currentPermissions.get(1).getUser().getEmail());

        assertEquals(assertedPermissions.get(2).getUser().getEmail(),
                currentPermissions.get(2).getUser().getEmail());


        currentPermissions = userService.getPermissionsByUserId(-1);
        assertNotNull(currentPermissions);
        assertTrue("empty permissions list", currentPermissions.isEmpty());
    }

    @Test
    public void testSetUserPermissions() {
        Integer userId = 3;
        Integer projectId = 2;

        List<Permission> newPermissions = new ArrayList<Permission>();

        User user = userDAO.findByPrimaryKey(userId);

        Project project = projectDAO.findByPrimaryKey(projectId);

        Permission permission = new Permission(PermissionType.ISSUE_CLOSE, user, project);
        permission.setCreateDate(new Date());
        permission.setLastModifiedDate(new Date());

        newPermissions.add(permission);

        userService.setUserPermissions(userId, newPermissions);

        assertEquals(newPermissions.get(0).getPermissionType(),
                userService.getPermissionsByUserId(userId).get(
                        0).getPermissionType());

    }

    @Test
    public void testSetAndUnsetUserPermissions() {
        Integer userId = 3;
        Integer projectId = 2;
        List<Permission> newPermissions = new ArrayList<Permission>();

        User user = userDAO.findByPrimaryKey(userId);

        Project project = projectDAO.findByPrimaryKey(projectId);

        Permission permission = new Permission(PermissionType.valueOf(4), user, project);
        permission.setCreateDate(new Date());
        permission.setLastModifiedDate(new Date());

        newPermissions.add(permission);
        userService.setUserPermissions(userId, newPermissions);

        assertEquals(newPermissions.get(0).getPermissionType(),
                userService.getPermissionsByUserId(userId).get(
                        0).getPermissionType());

        newPermissions.clear();
        userService.setUserPermissions(userId, newPermissions);

        permission = new Permission(PermissionType.valueOf(7), user, project);
//        permission.setCreateDate(new Date());
//        permission.setLastModifiedDate(new Date());

        newPermissions.add(permission);

        userService.setUserPermissions(userId, newPermissions);
        Boolean contains7 = false;
        Iterator<Permission> it = userService.getPermissionsByUserId(userId).iterator();
        while (it.hasNext()) {
            Permission permission2 = (Permission) it.next();
            assertNotNull(permission2);
            if (permission2.getPermissionType().getCode() == 7) {
                contains7 = true;
            }
        }
        assertTrue("userService.getPermissionsByUserId(userId).get(0),contains(7)", contains7);


    }

    @Test
    public void testGetUsersWithProjectPermission() {
        Integer projectId = 2;
        Integer permissionId = 1;
        List<User> users = userService.getUsersWithProjectPermission(projectId,
                permissionId);
        assertNotNull(users);
    }

    @Test
    public void testUpdateUser() throws UserException {
        User user = userService.getUser(2);

        user.setEmail("updated_email@test.com");

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("updated_email@test.com", updatedUser.getEmail());
    }

    @Test
    public void testUpdateUserPw() throws UserException {
        User user = userService.getUser(2);

        user.setPassword("new password");

        User updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("new password", updatedUser.getPassword());
    }

    @Test
    public void testGetPossibleOwners() {
        Issue issue = new Issue();
        Integer projectId = 2;
        Integer userId = 2;
        List<User> users =
                userService.getPossibleOwners(issue, projectId, userId);
        assertNotNull(users);
    }

    @Test
    public void testUpdateUserPreferences() throws UserException {

        UserPreferences userPreferences = userPreferencesDAO.findByUserId(2);
        assertNotNull("userPreferences#2", userPreferences);

        assertTrue("userPreferences#2.rememberLastSearch", userPreferences.getRememberLastSearch());

        userPreferences.setRememberLastSearch(false);

        UserPreferences updatedUserPreferences = userService.updateUserPreferences(userPreferences);

        assertNotNull(updatedUserPreferences);
        assertFalse(updatedUserPreferences.getRememberLastSearch());


        User user = userService.getUser(2);
        UserPreferences userPrefs = user.getPreferences();

//        test the last-modified date for plausability (automatically set by DAO)
        Long date = System.currentTimeMillis();

        user.setPreferences(userPrefs);
        updatedUserPreferences = userService.updateUserPreferences(userPrefs);
        assertNotNull(updatedUserPreferences);
        assertTrue("lastModifiedDate >= now", updatedUserPreferences.getLastModifiedDate().getTime() <= System.currentTimeMillis());
        assertTrue("lastModifiedDate <= date", updatedUserPreferences.getLastModifiedDate().getTime() >= date);

    }


    @Test
    public void testGetUserByLogin() {

        try {
            User user = userService.getUserByLogin("admin_test1");

            assertNotNull("admin_test1", user);

        } catch (Exception e) {
            logger.error("testGetUserByLogin: failed to lookup user", e);
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetUserPasswordByLogin() throws PasswordException {
        String password = userService.getUserPasswordByLogin("admin_test1");
        assertNotNull("password", password);
        assertEquals(UserUtilities.encryptPassword("admin_test1"), password);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        for (User user : users) {
            assertNotNull(user);
        }
        assertEquals("total 5 users", 5, users.size());
    }

    @Test
    public void testGetNumberUsers() {
        assertEquals("total 5 users", 5, userService.getNumberUsers());
    }

    @Test
    public void testGetActiveUsers() {
        List<User> users = userService.getActiveUsers();
        assertNotNull(users);
        assertEquals("total 4 active users", 4, users.size());
        for (User user : users) {
            assertEquals("user is active", UserUtilities.STATUS_ACTIVE, user.getStatus());
        }
    }

    @Test
    public void testGenerateUserPassword() {
        User user = new User();
        String password;
        try {
            password = userService.generateUserPassword(user);
            assertNotNull(password);
            assertNotNull(user.getPassword());
        } catch (PasswordException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testClearOwnedProjects() {
        User user = new User("user", "password", "User", "User", "user@user.com", false);
//    	user.setPassword("password");
        List<Project> projects = new ArrayList<Project>(1);
        projects.add(new Project("project"));
        user.setProjects(projects);
        userService.clearOwnedProjects(user);
        assertNotNull(user.getProjects());
        assertTrue("empty projects list", user.getProjects().isEmpty());

    }

    @Test
    public void testUpdateAuthenticator() {
        try {
            User user = userService.getUser(2);
            ArrayList<Permission> permissions = new ArrayList<Permission>(user.getPermissions().size());
            permissions.addAll(user.getPermissions());
            boolean updated = userService.updateAuthenticator(2, permissions);
            assertTrue(updated);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddUserPermissions() {

        assertEquals(4, userService.getPermissionsByUserId(2).size());
        boolean added = userService.addUserPermissions(2, null);
        assertFalse(added);

        assertEquals(4, userService.getPermissionsByUserId(2).size());
        added = userService.addUserPermissions(2, new ArrayList<Permission>());
        assertEquals(4, userService.getPermissionsByUserId(2).size());
        assertFalse(added);
        added = userService.setUserPermissions(2, new ArrayList<Permission>());
        assertTrue(added);
        assertEquals(1, userService.getPermissionsByUserId(2).size());


        List<Permission> permissions = new ArrayList<Permission>();
        Permission p1 = new Permission();
        Permission p2 = new Permission();
        permissions.add(p1);
        permissions.add(p2);

        User user = userService.getUser(2);
        p1.setUser(user);
        p2.setUser(user);

        Project proj1 = projectDAO.findAll().get(0);

        p1.setProject(proj1);
        p2.setProject(proj1);

        p1.setPermissionType(PermissionType.valueOf(1));
        p2.setPermissionType(PermissionType.valueOf(2));
        assertEquals(1, userService.getPermissionsByUserId(2).size());

        added = userService.addUserPermissions(2, permissions);
        assertTrue(added);

        assertEquals(3, userService.getPermissionsByUserId(2).size());


    }

    @Test
    public void testRemoveUserPermissions() {

        boolean removed = userService.removeUserPermissions(3, null);
        assertFalse(removed);

        removed = userService.removeUserPermissions(3, new ArrayList<Permission>());
        assertFalse(removed);


        List<Permission> permissions = userService.getPermissionsByUserId(3);
        assertNotNull(permissions);
        assertTrue("permission size for user#3", permissions.size() > 0);

        removed = userService.removeUserPermissions(3, permissions);
        assertTrue(removed);

        permissions = userService.getPermissionsByUserId(3);
        assertNotNull(permissions);
        assertEquals("permission size for user#3", 0, permissions.size());

    }

    @Test
    public void testGetUsersWithAnyProjectPermission() {

        List<User> users = userService.getUsersWithAnyProjectPermission(2,
                PermissionType.valueOf(new int[]{1}));
        assertNotNull(users);
        assertEquals("user 2,3", 2, users.size());
        assertTrue("user 2,3", users.get(0).getId() == 2 || users.get(0).getId() == 3);
        assertTrue("user 2,3", users.get(1).getId() == 2 || users.get(1).getId() == 3);

    }

    @Test
    public void testAllowRegistration() {

        try {
            userService.allowRegistration(
                    new User(),
                    "password",
                    AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                    AuthenticationConstants.REQ_SOURCE_WEB);

        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    public void testAllowProfileCreation() {

        try {
            userService.allowProfileCreation(
                    new User(),
                    "password",
                    AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                    AuthenticationConstants.REQ_SOURCE_WEB);

        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    public void testAllowPasswordUpdates() {

        try {
            userService.allowPasswordUpdates(
                    new User(),
                    "password",
                    AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                    AuthenticationConstants.REQ_SOURCE_WEB);

        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    public void testAllowPermissionUpdates() {

        try {
            userService.allowPermissionUpdates(
                    new User(),
                    "password",
                    AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                    AuthenticationConstants.REQ_SOURCE_WEB);

        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    public void testCheckLogin() {

        try {
            userService.checkLogin(
                    "admin_test1",
                    "admin_test1",
                    AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                    AuthenticationConstants.REQ_SOURCE_API);

        } catch (Exception e) {
            fail(e.getMessage());
        }


    }

    @Test
    public void testAllowPreferenceUpdates() {

        try {
            userService.allowPreferenceUpdates(
                    new User(),
                    "password",
                    AuthenticationConstants.AUTH_TYPE_PASSWORD_PLAIN,
                    AuthenticationConstants.REQ_SOURCE_WEB);

        } catch (Exception e) {
            fail(e.getMessage());
        }


    }


    @Test
    public void testAllowProfileUpdates() {

        User user = userService.getUser(2);

        boolean allowProfileUpdates = userService.allowProfileUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);

        assertTrue(allowProfileUpdates);

    }


    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();

        userService = (UserService) applicationContext.getBean("userService");
        userPreferencesDAO = (UserPreferencesDAO) applicationContext.getBean("userPreferencesDAO");
        projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
        userDAO = (UserDAO) applicationContext.getBean("userDAO");
        permissionDAO = (PermissionDAO) applicationContext.getBean("permissionDAO");

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
