package org.itracker.web.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessages;
import org.itracker.model.*;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Helper utils for admin project actions.
 */
public class AdminProjectUtilities {
    private static final Logger log = Logger.getLogger(AdminProjectUtilities.class);

    /**
     * When creating project, initial set of users with specific set of rights
     * can be defined.
     *
     * @param project        the project
     * @param userIds        the user IDs
     * @param permissions    the permissions
     * @param projectService the project service
     * @param userService    the user service
     */
    public static void handleInitialProjectMembers(Project project,
                                                   Set<Integer> userIds, Set<Integer> permissions,
                                                   ProjectService projectService, UserService userService) {
        List<Permission> userPermissionModels;
        if (userIds != null && permissions != null && userIds.size() > 0
                && permissions.size() > 0) {

            Set<User> users = new HashSet<>(userIds.size());
            for (Integer userId : userIds)
                users.add(userService.getUser(userId));

            // process member-users
            for (User user : users) {
                userPermissionModels = userService.getUserPermissionsLocal(user);

                for (Integer type : permissions)
                    userPermissionModels.add(new Permission(PermissionType.valueOf(type), user, project));

                // save the permissions
                userService.setUserPermissions(user.getId(), userPermissionModels);
                userService.updateAuthenticator(user.getId(), userPermissionModels);
            }
        }

    }


    /**
     * Setup permissions for updated project-owners.
     *
     * @param project     the project
     * @param userIds     the user IDs
     * @param userService the user service
     */
    public static void updateProjectOwners(Project project,
                                                 Set<Integer> userIds, ProjectService projectService,
                                                 UserService userService) {
        Set<Permission> userPermissionModels;

        if (log.isDebugEnabled()) {
            log.debug("updateProjectOwners: setting new owners: " + userIds);
        }

        // add all defined owners to project
        for (Integer userId : userIds) {
            User usermodel = userService.getUser(userId);
            boolean newPermissions = false;
            userPermissionModels = new HashSet<>(userService.getUserPermissionsLocal(usermodel));
            if (log.isDebugEnabled()) {
                log.debug("updateProjectOwners: setting owner " + usermodel + " to " + project);
            }
            for (Integer permission : UserUtilities.ALL_PERMISSIONS_SET) {
                if (userPermissionModels.add(new Permission(PermissionType.valueOf(permission),
                        usermodel, project))) {
                    newPermissions = true;
                }
            }
            if (newPermissions) {
                userService.addUserPermissions(usermodel.getId(),
                        new ArrayList<>(userPermissionModels));
                if (log.isDebugEnabled()) {
                    log.debug("updateProjectOwners: updated permissions for " + usermodel + " to " + userPermissionModels);
                }
            }
        }

        projectService.setProjectOwners(project, userIds);
    }

    public static void setFormProperties(Project project, ProjectService projectService,
                                               ActionForm form, ActionMessages errors)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        project.setDescription((String) PropertyUtils.getSimpleProperty(
                form, "description"));
        project.setName((String) PropertyUtils.getSimpleProperty(form,
                "name"));
        Integer projectStatus = (Integer) PropertyUtils.getSimpleProperty(
                form, "status");

        String projectName = (String) PropertyUtils.getSimpleProperty(form, "name");

        project.setName(projectName);


        if (errors.isEmpty()) {
            if (projectStatus != null) {
                project.setStatus(Status.valueOf(projectStatus));
            } else {
                project.setStatus(Status.ACTIVE);
            }

            Integer[] optionValues = (Integer[]) PropertyUtils
                    .getSimpleProperty(form, "options");
            int optionmask = 0;
            if (optionValues != null) {
                for (Integer optionValue : optionValues) {
                    optionmask += optionValue;
                }
            }
            project.setOptions(optionmask);

            Integer[] fieldsArray = (Integer[]) PropertyUtils.getSimpleProperty(form, "fields");
            Set<Integer> fields = null == fieldsArray ? new HashSet<Integer>(0) :
                    new HashSet<>(Arrays.asList(fieldsArray));

            projectService.setProjectFields(project, fields);

        }
    }
}
