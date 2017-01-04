/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 */

package org.itracker.services.authentication;

import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.User;
import org.itracker.services.exceptions.AuthenticatorException;

import java.util.List;
import java.util.Map;

/**
 * This interface should be implemented to provide a new authentication module for
 * ITracker.  It provides service to check if a user can be authenticated
 * during a login, and also whether a user self registration is allowed.  A new
 * instance of this object is created for each check.
 *
 * @see org.itracker.core.AuthenticationConstants
 */
public interface PluggableAuthenticator {

    /**
     * This method should be implemented to determine if a user login is successful.  The method
     * should return a valid User object.
     *
     * @param login          the login the user/client provided
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a User if the login is successful
     * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
     */
    User checkLogin(String login, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should return all the permissions a user has in the authentication system.  This
     * list may then be augmented based on other attributes of the user, or project level options.
     *
     * @param user      a User object that contains the user to retrieve permissions for
     * @param reqSource the source of the request (eg web, api)
     * @return an array of PermissionModels
     * @throws AuthenticatorException an error occurs
     */
    List<Permission> getUserPermissions(User user, int reqSource) throws AuthenticatorException;

    /**
     * This method should return an array of users that have certain permissions in the
     * authentication system.  This list must always include all super users, even if they
     * do not explicitly have the required permission.
     *
     * @param projectId       id of the project on which the users return have permissions
     * @param permissionTypes types of permissions required
     * @param requireAll      true is the user must possess any of the permissions, false if only one is required
     * @param activeOnly      true if only users listed as active should be returned
     * @param reqSource       the source of the request (eg web, api)
     * @return an array of UserModels
     * @throws AuthenticatorException an error occurs
     */
    List<User> getUsersWithProjectPermission(Integer projectId,
                                             PermissionType[] permissionTypes, boolean requireAll,
                                             boolean activeOnly, int reqSource)
            throws AuthenticatorException;

    /**
     * This method should return an array of users that have certain permissions in the
     * authentication system.  This list must always include all super users, even if they
     * do not explicitly have the required permission.
     *
     * @param projectId       id of the project on which the users return have permissions
     * @param permissionTypes types of permissions required
     * @param requireAll      true is the user must possess any of the permissions, false if only one is required
     * @param activeOnly      true if only users listed as active should be returned
     * @param reqSource       the source of the request (eg web, api)
     * @return an array of UserModels
     * @throws AuthenticatorException an error occurs
     */
    @Deprecated
    List<User> getUsersWithProjectPermission(Integer projectId,
                                             int[] permissionTypes, boolean requireAll,
                                             boolean activeOnly, int reqSource)
            throws AuthenticatorException;

    /**
     * This method should be implemented to determine if a user is authorized to self register.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a boolean whether the user should be allowed to register
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowRegistration(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to determine if a new user profile should be allowed
     * to be created.  This applies to both self registration and also new users created by
     * a super user on the system.  If this method would always return false, then some other
     * mechanism must be provided for new users to be created in the system.
     *
     * @param user           a User object that contains the data for the new user.  If null,
     *                       then the request is being made for an unknown future user.  For example,
     *                       the system may request this with an null user if it needs to know if the system
     *                       should even present the option to create a new user
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a boolean whether new profile creation is allowed
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowProfileCreation(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to determine if the particular user is
     * allowed to perform profile updates on the system.  This method is used in
     * conjunction with allowPasswordUpdates, allowPreferenceUpdates, and
     * allowPermissionUpdates to determine what parts of the user's information
     * is allowed to be updated through ITracker.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     * @see PluggableAuthenticator#allowPasswordUpdates
     * @see PluggableAuthenticator#allowPermissionUpdates
     * @see PluggableAuthenticator#allowPreferenceUpdates
     */
    boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to determine if the particular user is allowed to perform
     * password updates on the system.  This method is used in conjunction with allowProfileUpdates,
     * allowPermissionUpdates, and allowPreferenceUpdates to determine what parts of the user's
     * information is allowed to be updated through ITracker.
     *
     * @param user           a User object that contains the current user data
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     * @see PluggableAuthenticator#allowProfileUpdates
     * @see PluggableAuthenticator#allowPermissionUpdates
     * @see PluggableAuthenticator#allowPreferenceUpdates
     */
    boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to determine if the particular user is allowed to perform
     * permissions updates on the system.  This method is used in conjunction with allowProfileUpdates,
     * allowPasswordUpdates, and allowPreferenceUpdates to determine what parts of the user's
     * information is allowed to be updated through ITracker.  If the user model is null, then the
     * request is being made for multiple users, for example on the edit project page, and is being applied
     * on a generic basis, that is are permission updates allowed at all on the system.
     *
     * @param user           a User object that contains the current user data, or null if multiple users
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     * @see PluggableAuthenticator#allowProfileUpdates
     * @see PluggableAuthenticator#allowPasswordUpdates
     * @see PluggableAuthenticator#allowPreferenceUpdates
     */
    boolean allowPermissionUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to determine if the particular user is allowed to perform
     * preferences updates on the system.  This method is used in conjunction with allowProfileUpdates,
     * allowPasswordUpdates, and allowPermissionUpdate to determine what parts of the user's
     * information is allowed to be updated through ITracker.
     *
     * @param user           a User object that contains the current user data
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     * @see PluggableAuthenticator#allowProfileUpdates
     * @see PluggableAuthenticator#allowPasswordUpdates
     * @see PluggableAuthenticator#allowPermissionUpdates
     */
    boolean allowPreferenceUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to perform any updates that are necessary in the authentication
     * system to support a new user.  Any updates needed to the data supplied should be made in the supplied
     * User.  The system will then update the information in the ITracker datastore.  Only changes to the
     * core profile information and password are made here.  Any permission information for the new user
     * would be done through an updateProfile call.
     *
     * @param user           a User object that contains the newly created profile
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true if changes were made
     * @throws AuthenticatorException an error occurs
     * @see PluggableAuthenticator#updateProfile
     */
    boolean createProfile(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to perform any updates that are necessary in the authentication
     * system to support the updated user information.  This action will be called any time there are any
     * updates to a user including core profile information, password information, permission information
     * or preference changes. Any changes should be made directly to user model supplied to the method.
     *
     * @param user           a User object that contains the updated profile
     * @param updateType     the type of information that is being updated
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source of the request (eg web, api)
     * @return true if changes were made
     * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
     */
    boolean updateProfile(User user, int updateType, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method should be implemented to setup any needed components.  It is called
     * Every time a new check is performed but could be used to store static information
     * that is not changed.
     *
     * @param value A HashMap that contains some default information.  The current calls
     *              pass a UserService bean as userService, and an ConfigurationService
     *              bean as configurationService
     */
    void initialize(Map<?, ?> value);

}
