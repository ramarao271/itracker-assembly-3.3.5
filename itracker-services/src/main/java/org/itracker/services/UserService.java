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

package org.itracker.services;

import org.itracker.PasswordException;
import org.itracker.UserException;
import org.itracker.model.*;
import org.itracker.services.exceptions.AuthenticatorException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService extends UserDetailsService {

    User getUser(Integer userId);

    User getUserByLogin(String login);

    String getUserPasswordByLogin(String login);

    List<User> getAllUsers();

    int getNumberUsers();

    List<User> getActiveUsers();

    List<User> getSuperUsers();

    List<User> getPossibleOwners(Issue issue, Integer projectId, Integer userId);

    User createUser(User user) throws UserException;

    User updateUser(User user) throws UserException;

    String generateUserPassword(User user) throws PasswordException;

    UserPreferences updateUserPreferences(UserPreferences user) throws UserException;

    void clearOwnedProjects(User user);

    /**
     * Finds all users assigned to a project having all of the given permissionTypes.
     *
     * @param projectID the project ID
     * @param permissionTypes array of permission types
     * @return users having the required permissions
     */
    List<User> findUsersForProjectByPermissionTypeList(Integer projectID, PermissionType[] permissionTypes);

    /**
     * This method will call local EJBs to find users with a specific permission.
     * This method is used by the deafult authenticator to manage permissions locally.
     * If a pluggable authenticator is implemented that stores permissions in an
     * external system, calling this method may not have up to date information.
     *
     * @param projectId      id of the project on which the returned users have permissions
     * @param permissionType the type of permission to search for
     * @return an array of UserModels containing the users with the permission
     */

    List<User> getUsersWithPermissionLocal(Integer projectId, PermissionType permissionType);

    @Deprecated
    List<User> getUsersWithPermissionLocal(Integer projectId, int permissionType);

    /**
     * This method will call local EJBs to find all permissions for a user.
     * This method is used by the deafult authenticator to manage permissions locally.
     * If a pluggable authenticator is implemented that stores permissions in an
     * external system, calling this method may not have up to date information.
     *
     * @param user the user to find the permissions for
     * @return an array of PermissionModels containing the user's permissions
     */
    List<Permission> getUserPermissionsLocal(User user);

    /**
     * Adds an additional set of permissions to a user in the database.  This does not remove any existing
     * permissions.  Any duplication permissions will be ignored.  Before updating the permissions, this
     * method will call the pluggable authenticator to have the permission set augmented.  This augmented
     * set of permissions will then be used for the actual update.
     *
     * @param userId         the userId, not login, of the user to add the permissions to
     * @param newPermissions an array of PermissionModels that represent the new permissions to add to the user
     * @return true if the operation was successful
     */

    boolean updateAuthenticator(Integer userId, List<Permission> newPermissions);

    /**
     * Resets all of the permissions for a user in the database.  The new permissions for the user are contained in a
     * HashMap object.  The keys of this map MUST be in the format Perm<permission type>Prod<productId>.  For example to add the
     * VIEW_ALL permission to project 3, the key would be Perm7Prod3.  The value of the key would be a Permission
     * object.  Before updating the permissions, this method will call the pluggable authenticator to have the permission
     * set augmented.  This augmented set of permissions will then be used for the actual update.
     *
     * @param userId         the userId, not login, of the user to add the permissions to
     * @param newPermissions a HashMap containing keys and Permission values as described in the method description.
     * @return true if the operation was successful
     * @see org.itracker.model.util.UserUtilities
     */

    boolean addUserPermissions(Integer userId, List<Permission> newPermissions);

    /**
     * Resets all of the permissions for a user in the database.  The new permissions for the user are contained in a
     * HashMap object.  The keys of this map MUST be in the format Perm<permission type>Prod<productId>.  For example to add the
     * VIEW_ALL permission to project 3, the key would be Perm7Prod3.  The value of the key would be a Permission
     * object.  Before updating the permissions, this method will call the pluggable authenticator to have the permission
     * set augmented.  This augmented set of permissions will then be used for the actual update.
     *
     * @param userId         the userId, not login, of the user to add the permissions to
     * @param newPermissions a HashMap containing keys and Permission values as described in the method description.
     * @return true if the operation was successful
     * @see org.itracker.model.util.UserUtilities
     */

    boolean setUserPermissions(Integer userId, List<Permission> newPermissions);

    /**
     * Resets all of the permissions for a user in the database.  The new permissions for the user are contained in a
     * HashMap object.  The keys of this map MUST be in the format Perm<permission type>Prod<productId>.  For example to add the
     * VIEW_ALL permission to project 3, the key would be Perm7Prod3.  The value of the key would be a Permission
     * object.  Before updating the permissions, this method will call the pluggable authenticator to have the permission
     * set augmented.  This augmented set of permissions will then be used for the actual update.
     *
     * @param userId         the userId, not login, of the user to add the permissions to
     * @param newPermissions a HashMap containing keys and Permission values as described in the method description.
     * @return true if the operation was successful
     * @see org.itracker.model.util.UserUtilities
     */

    boolean removeUserPermissions(Integer userId, List<Permission> newPermissions);

    /**
     * Returns an array of Permission objects for the requested userId.
     *
     * @param userId the userId, not the login, to find the permissions of
     */

    List<Permission> getPermissionsByUserId(Integer userId);

    /**
     * Returns a HashMap of all permissions a user has.  The HashMap uses the projectId
     * as the key values, and then contains a HashSet as the value of the key containing
     * Integer objects of the permissions a user has.
     * <p/>
     * A special key of the Integer -1 may also be in the HashMap.  This key is used
     * to represent if the user is a super user, and in which case the user has all
     * permissions be default.
     * <p/>
     * The value of this key would be a Boolen object with the value true
     * if the user was a super user.
     * <p/>
     * This HashMap is usually not used directly, but in conjunction with the hasPermission
     * methods in UserUtilities to determine if a user has a particular permission.
     *
     * @param user     a User representing the user that the permissions should be obtained for
     * @param reqSource the source of the request
     * @return a Map of permission types by project ID
     * @see org.itracker.model.util.UserUtilities#hasPermission
     */
    Map<Integer, Set<PermissionType>> getUsersMapOfProjectIdsAndSetOfPermissionTypes(User user, int reqSource);

    /**
     * This method will return a list of users with a specific permission, either explicitly, or
     * by their role as a super user.  This list is obtained calling a method in the pluggable
     * authenticator, so the actual source of the data may not
     * be the local datastore.
     *
     * @param projectId  the project to find the permission for
     * @param permission the permission to check for
     * @return an array of Users that represent the users that have the permission
     */
    List<User> getUsersWithProjectPermission(Integer projectId, PermissionType permission);

    @Deprecated
    List<User> getUsersWithProjectPermission(Integer projectId, int permission);

    /**
     * This method will return a list of users with a specific permission, either explicitly, or
     * by their role as a super user.  This list is obtained calling a method in the pluggable
     * authenticator, so the actual source of the data may not
     * be the local datastore.
     *
     * @param projectId  the project to find the permission for
     * @param permission the permission to check for
     * @param activeOnly only include users who are currently active
     * @return an array of UserModels that represent the users that have the permission
     */
    @Deprecated
    List<User> getUsersWithProjectPermission(Integer projectId, int permission, boolean activeOnly);

    /**
     * This method will return a list of users with the supplied permission, either explicitly, or
     * by their role as a super user.  This list is obtained calling a method in the pluggable
     * authenticator, so the actual source of the data may not be the local datastore.
     *
     * @param projectId   the project to find the permission for
     * @param permissions the permissions that are checked against
     * @param requireAll  true if all the permissions in the array are required, false if only one is required
     * @param activeOnly  only include users who are currently active
     * @return an array of UserModels that represent the users that have the permission
     */
    List<User> getUsersWithProjectPermission(Integer projectId, PermissionType[] permissions, boolean requireAll, boolean activeOnly);

    @Deprecated
    List<User> getUsersWithProjectPermission(Integer projectId, int[] permissions, boolean requireAll, boolean activeOnly);

    /**
     * This method will return a list of users with any of the supplied permission, either explicitly, or
     * by their role as a super user.  This list is obtained calling a method in the pluggable
     * authenticator, so the actual source of the data may not
     * be the local datastore.
     *
     * @param projectId   the project to find the permission for
     * @param permissions the permissions that are checked against
     * @return an array of UserModels that represent the users that have the permission
     */
    List<User> getUsersWithAnyProjectPermission(Integer projectId, PermissionType[] permissions);

    List<User> getUsersWithProjectPermission(Integer projectId, PermissionType permissionType, boolean activeOnly);

    /**
     * This method will return a list of users with any of the supplied permission, either explicitly, or
     * by their role as a super user.  This list is obtained calling a method in the pluggable
     * authenticator, so the actual source of the data may not
     * be the local datastore.
     *
     * @param projectId   the project to find the permission for
     * @param permissionTypes the permissions that are checked against
     * @return an array of UserModels that represent the users that have the permission
     */
    @Deprecated
    List<User> getUsersWithAnyProjectPermission(Integer projectId, int[] permissionTypes);

    /**
     * This method will return a list of users with any of the supplied permission, either explicitly, or
     * by their role as a super user.  This list is obtained calling a method in the pluggable
     * authenticator, so the actual source of the data may not
     * be the local datastore.
     *
     * @param projectId   the project to find the permission for
     * @param permissions the permissions that are checked against
     * @param activeOnly  only include users who are currently active
     * @return an array of UserModels that represent the users that have the permission
     */
    List<User> getUsersWithAnyProjectPermission(Integer projectId, PermissionType[] permissions, boolean activeOnly);

    @Deprecated
    List<User> getUsersWithAnyProjectPermission(Integer projectId, int[] permissions, boolean activeOnly);

    /**
     * This method checks the login of a user, and returns the user if authentication was successful.
     *
     * @param login          the login the user/client provided
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return a User if the login is successful
     * @throws AuthenticatorException an exception if the login is unsuccessful, or an error occurs
     */
    User checkLogin(String login, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method checks to see if the given user is allowed to self register.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return true if the user is allowed to register
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowRegistration(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method checks to see if a new user profile can be created within ITracker
     *
     * @param user           a User object that contains the data for the new user.  If null,
     *                       then the request is being made for an unknown future user.  For example,
     *                       the system may request this with an null user if it needs to know if the system
     *                       should even present the option to create a new user
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return a boolean indicating whether new user profile can be created through ITracker
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowProfileCreation(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method checks to see if the given user's core user profile information
     * can be updated locally.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowProfileUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method checks to see if the given user's password can be updated locally.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowPasswordUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method checks to see if the given user's permission information
     * can be updated locally.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowPermissionUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;

    /**
     * This method checks to see if the given user's preference information
     * can be updated locally.
     *
     * @param user           a User object that contains the data the user submitted
     * @param authentication the user's authentication information, if known
     * @param authType       the type of authentication information being provided
     * @param reqSource      the source from which the request was made (eg web, api)
     * @return a boolean whether the user's core profile information can be updated
     * @throws AuthenticatorException an exception if an error occurs
     */
    boolean allowPreferenceUpdates(User user, Object authentication, int authType, int reqSource) throws AuthenticatorException;



}