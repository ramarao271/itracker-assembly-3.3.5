/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.services.authentication;

import org.apache.log4j.Logger;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.UserException;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.PasswordException;
import org.itracker.model.util.UserUtilities;

import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class provides a default authentication scheme for ITracker.  It uses passwords
 * in the user table provided by ITracker to authenticate users.  This authenticator
 * allows any user to self register if self registration is available in the system.
 */
public class DefaultAuthenticator extends AbstractPluggableAuthenticator {

    private static final Logger logger = Logger.getLogger(DefaultAuthenticator.class);

    /**
     * Checks the login of a user against the user profile provided in ITracker.  This is
     * the default authentication scheme provided by ITracker.
     *
     * @param login          the login the user/client provided
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a User if the login is successful
     * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
     */
    public User checkLogin(final String login, final Object authentication, final int authType, final int reqSource) throws AuthenticatorException {
        if (logger.isDebugEnabled()) {
            logger.debug("Checking login for " + login + " using DefaultAuthenticator");
        }

        if (login != null && authentication != null && !login.equals("")) {
            User user;
            try {
                user = getUserService().getUserByLogin(login);
            } catch (DataAccessException e) {
                logger.error("checkLogin: failed to get user by login: " + login, e);
                throw new AuthenticatorException(AuthenticatorException.UNKNOWN_USER, e.getMessage());
            }

            if (user.getStatus() != UserUtilities.STATUS_ACTIVE) {
                AuthenticatorException e = new AuthenticatorException(AuthenticatorException.INACTIVE_ACCOUNT);
                logger.info("checkLogin: user is inactive, user: " + user, e);
                throw e;
            }

            String userPassword;
            try {
                userPassword = getUserService().getUserPasswordByLogin(login);
            } catch (DataAccessException e) {
                AuthenticatorException ex = new AuthenticatorException(e.getMessage(), authType);
                logger.info("checkLogin: user is inactive, user: " + user, ex);
                throw e;
            }
            if (userPassword == null || userPassword.equals("")) {
                AuthenticatorException e = new AuthenticatorException(AuthenticatorException.INVALID_PASSWORD);
                logger.info("checkLogin: user has no password, user: " + user, e);
                throw e;
            }

            try {
                if (!userPassword.endsWith("=")) {
                    logger.info("checkLogin: User " + login + " has old style password.  Converting to SHA1 hash.");
                    try {
                        user.setPassword(UserUtilities.encryptPassword(userPassword));
                        getUserService().updateUser(user);
                    } catch (UserException ue) {
                        logger.error("checkLogin: User password conversion failed for user " + user, ue);
                        throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
                    }
                }

                if (authType == AUTH_TYPE_PASSWORD_PLAIN) {
                    if (!userPassword.equals(UserUtilities.encryptPassword((String) authentication))) {
                        throw new AuthenticatorException(AuthenticatorException.INVALID_PASSWORD);
                    }
                } else if (authType == AUTH_TYPE_PASSWORD_ENC) {
                    if (!userPassword.equals(authentication)) {
                        throw new AuthenticatorException(AuthenticatorException.INVALID_PASSWORD);
                    }
                } else {
                    logger.info("checkLogin: invalid authenticator type: " + authType);
                    throw new AuthenticatorException(AuthenticatorException.INVALID_AUTHENTICATION_TYPE);
                }
            } catch (ClassCastException cce) {
                logger.error("checkLogin: Authenticator was of wrong type.", cce);
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch (PasswordException pe) {
                throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
            } catch (AuthenticatorException ae) {
                if (logger.isDebugEnabled()) {
                    logger.debug("checkLogin: failed to authenticate " + login, ae);
                }
                throw ae;
            }

            return user;
        }

        logger.info("checkLogin: no login was supplied: " + login + ", type: " + authType + ", source: " + reqSource);
        throw new AuthenticatorException(AuthenticatorException.INVALID_DATA);
    }

    /**
     * The DefaultAuthenticator returns a list of user permissions from the database.
     *
     * @param user      a User object that contains the user to retrieve permissions for
     * @param reqSource the source of the request (eg web, api)
     * @return an array of PermissionModels
     * @throws AuthenticatorException an error occurs
     */
    public List<Permission> getUserPermissions(User user, int reqSource) throws AuthenticatorException {
        if (user == null || user.getId() == null) {
            throw new AuthenticatorException(AuthenticatorException.INVALID_DATA);
        }

        List<Permission> permissionList;
        try {
            permissionList = getUserService().getUserPermissionsLocal(user);
        } catch (DataAccessException e) {
            throw new AuthenticatorException(e.getMessage(), reqSource);
        }

        if (user.isSuperUser()) {
            List<Permission> augmentedPermissions = new ArrayList<Permission>();

            // Super user has access to all projects (represented by the "null" project). 
            Permission permission = new Permission(PermissionType.USER_ADMIN, user, null);
            augmentedPermissions.add(permission);
            augmentedPermissions.addAll(permissionList);
            return augmentedPermissions;

        } else {
            return permissionList;
        }

    }



    /**
     * Returns the list of users for a given project. User permissions can be specified.
     *
     * @param projectId       - The Project to search for users
     * @param permissionTypes - User rights to filter
     * @param requireAll      - Require all permissions
     * @param activeOnly      - Filter users who are active (Possible user status: DELETED, ACTIVE, LOCKED)
     * @param reqSource       - not used. TODO: Tagged for removal
     * @return List of users for the project with filters applied.
     */
    @Override
    public List<User> getUsersWithProjectPermission(Integer projectId, PermissionType[] permissionTypes, boolean requireAll, boolean activeOnly, int reqSource) throws AuthenticatorException {

        List<User> users;

        try {
            Map<Integer, User> userMap = new HashMap<Integer, User>();

            if (requireAll) {

                List<User> explicitUsers = getUserService().findUsersForProjectByPermissionTypeList(projectId, permissionTypes);

                for (User user : explicitUsers) {
                    userMap.put(user.getId(), user);
                }
            } else {

                for (int i = 0; i < permissionTypes.length; i++) {
                    List<User> explicitUsers = getUserService().getUsersWithPermissionLocal(projectId, permissionTypes[i]);

                    for (User user : explicitUsers) {
                        userMap.put(user.getId(), user);
                    }

                }

            }

            List<User> superUsers = getUserService().getSuperUsers();
            for (User superUser : superUsers) {
                userMap.put(superUser.getId(), superUser);
            }

            users = new ArrayList<User>();
            for (User user : userMap.values()) {
                if (activeOnly) {
                    if (user.getStatus() == UserUtilities.STATUS_ACTIVE) {
                        users.add(user);
                    }
                } else {
                    users.add(user);
                }
            }

        } catch (Exception e) {
            logger.error("Error retreiving users with permissions.", e);
            throw new AuthenticatorException();
        }

        return users;
    }
    @Override
    @Deprecated
    public List<User> getUsersWithProjectPermission(Integer projectId,
                                                    int[] permissionTypes,
                                                    boolean requireAll,
                                                    boolean activeOnly,
                                                    int reqSource)
            throws AuthenticatorException {

        return getUsersWithProjectPermission(projectId,
                PermissionType.valueOf(permissionTypes),
                requireAll,
                activeOnly,
                reqSource);

    }

    /**
     * The DefaultAuthenticator always allows self registered users.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true
     */
    public boolean allowRegistration(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }


    /**
     * The DefaultAuthenticator always allows new user profiles.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @return true
     * @throws AuthenticatorException an exception if an error occurs
     */
    public boolean allowProfileCreation(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
     * The DefaultAuthenticator always allows profile updates.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true
     * @throws AuthenticatorException an exception if an error occurs
     */
    public boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
     * The DefaultAuthenticator always allows password updates.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true
     * @throws AuthenticatorException an exception if an error occurs
     */
    public boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
     * The DefaultAuthenticator always allows permission updates.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true
     * @throws AuthenticatorException an exception if an error occurs
     */
    public boolean allowPermissionUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
     * The DefaultAuthenticator always allows preferences updates.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true
     * @throws AuthenticatorException an exception if an error occurs
     */
    public boolean allowPreferenceUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return true;
    }

    /**
     * The DefaultAuthenticator does not make any changes to a newly created profile.
     *
     * @param user           a User object that contains the newly created profile
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return boolean indicating whther changes to the user were made
     * @throws AuthenticatorException an error occurs
     */
    public boolean createProfile(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return false;
    }

    /**
     * The DefaultAuthenticator does not make any changes to an updated profile.
     *
     * @param user           a User object that contains the updated profile
     * @param updateType     the type of information that is being updated
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return boolean indicating whther changes to the user were made
     * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
     */
    public boolean updateProfile(User user, int updateType, Object authentication, int authType, int reqSource) throws AuthenticatorException {
        return false;
    }

}
