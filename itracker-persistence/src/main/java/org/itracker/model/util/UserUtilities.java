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

package org.itracker.model.util;

import org.itracker.PasswordException;
import org.itracker.core.AuthenticationConstants;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.util.Base64Coder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class UserUtilities implements AuthenticationConstants {
    protected static final char[] alphabet = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static final int STATUS_DELETED = -1;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_LOCKED = 2;

    // TODO: Could use an enumeration

    /**
     * User Admin Permission.  Currently this is equivalent to super user, since the permission can't be granted, and is only available to an admin.
     */
    public static final int PERMISSION_USER_ADMIN = PermissionType.USER_ADMIN.getCode();
    /**
     * Product Admin Permission
     */
    public static final int PERMISSION_PRODUCT_ADMIN = PermissionType.PRODUCT_ADMIN.getCode();
    /**
     * Issue Create Permission
     */
    public static final int PERMISSION_CREATE = PermissionType.ISSUE_CREATE.getCode();
    /**
     * Issue Edit Permission.  Users with this permission can edit any issue in the project.
     */
    public static final int PERMISSION_EDIT = PermissionType.ISSUE_EDIT_ALL.getCode();
    /**
     * Issue Close Permission.  Users with this permission can close issues in the project.
     */
    public static final int PERMISSION_CLOSE = PermissionType.ISSUE_CLOSE.getCode();
    /**
     * Issue Assign to Self Permission.  Users with this permission can assign issues to themselves.
     */
    public static final int PERMISSION_ASSIGN_SELF = PermissionType.ISSUE_ASSIGN_SELF.getCode();
    /**
     * Issue Assign to Others Permissions.  Users with this permission can assign issues to anyone, given than those users have the ability to recieve the assignment.
     */
    public static final int PERMISSION_ASSIGN_OTHERS = PermissionType.ISSUE_ASSIGN_OTHERS.getCode();
    /**
     * View All Issues Permission.  Users can view all issues in the project.
     */
    public static final int PERMISSION_VIEW_ALL = PermissionType.ISSUE_VIEW_ALL.getCode();
    /**
     * View Users Issues Permission.  Users can view thier own issues.  This includes ones they are the creator or owner of.
     */
    public static final int PERMISSION_VIEW_USERS = PermissionType.ISSUE_VIEW_USERS.getCode();
    /**
     * Edit Users Issues Permission.  Users with this permission can edit any issue they created or own.
     * They are limited to editing the description, adding history entries, and adding attachments.
     */
    public static final int PERMISSION_EDIT_USERS = PermissionType.ISSUE_EDIT_USERS.getCode();
    /**
     * Issue Unassign Self Permission.  Users with this permission can unassign issues they own.
     */
    public static final int PERMISSION_UNASSIGN_SELF = PermissionType.ISSUE_UNASSIGN_SELF.getCode();
    /**
     * Issue Assignable.  Users with this permission can be assigned any issue in the system.  To determine if a user can
     * be assigned an issue, it will be a combination of users with EDIT_ALL, users with EDIT_USERS if they are the creator,
     * and users with this permission and EDIT_USERS.
     */
    public static final int PERMISSION_ASSIGNABLE = PermissionType.ISSUE_ASSIGNABLE.getCode();
    /**
     * Create for Others.  Users with this permission are allowed to create issues on behalf of other users.  The system
     * will treat the issue as if the other user had created it.  The actual creator will be logged in the audit log.
     */
    public static final int PERMISSION_CREATE_OTHERS = PermissionType.ISSUE_CREATE_OTHERS.getCode();
    /**
     * Full edit permission.  This defines what levelof editing a user has for an issue.  Without this permission, users will
     * be limited to editing only the description, attachments, custom fields, and history of an issue.
     */
    public static final int PERMISSION_EDIT_FULL = PermissionType.ISSUE_EDIT_FULL.getCode();


    private static final Integer[] ALL_PERMISSIONS = new Integer[]{
            PERMISSION_PRODUCT_ADMIN,
            PERMISSION_CREATE,
            PERMISSION_EDIT,
            PERMISSION_CLOSE,
            PERMISSION_ASSIGN_SELF,
            PERMISSION_ASSIGN_OTHERS,
            PERMISSION_VIEW_ALL,
            PERMISSION_VIEW_USERS,
            PERMISSION_EDIT_USERS,
            PERMISSION_UNASSIGN_SELF,
            PERMISSION_ASSIGNABLE,
            PERMISSION_CREATE_OTHERS,
            PERMISSION_EDIT_FULL,

    };
    public static final Set<Integer> ALL_PERMISSIONS_SET = Collections.unmodifiableSet(getAllPermissionsSet());

    private static final Set<Integer> getAllPermissionsSet() {
        return new HashSet<Integer>(Arrays.asList(ALL_PERMISSIONS));
    }

    public static final int REGISTRATION_TYPE_ADMIN = 1;
    public static final int REGISTRATION_TYPE_SELF = 2;
    public static final int REGISTRATION_TYPE_IMPORT = 3;

    public static final int PREF_HIDE_ASSIGNED = 1;
    public static final int PREF_HIDE_UNASSIGNED = 2;
    public static final int PREF_HIDE_CREATED = 4;
    public static final int PREF_HIDE_WATCHED = 8;

    public UserUtilities() {
    }

    public static String getStatusName(int value) {
        return getStatusName(value, ITrackerResources.getLocale());
    }

    public static String getStatusName(int value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_USER_STATUS + value, locale);
    }

    public static HashMap<String, String> getStatusNames() {
        return getStatusNames(ITrackerResources.getLocale());
    }

    public static HashMap<String, String> getStatusNames(Locale locale) {
        HashMap<String, String> statuses = new HashMap<String, String>();
        statuses.put(Integer.toString(STATUS_DELETED), getStatusName(STATUS_DELETED, locale));
        statuses.put(Integer.toString(STATUS_ACTIVE), getStatusName(STATUS_ACTIVE, locale));
        statuses.put(Integer.toString(STATUS_LOCKED), getStatusName(STATUS_LOCKED, locale));
        return statuses;
    }

    public static String getPermissionName(int value) {
        return getPermissionName(value, ITrackerResources.getLocale());
    }

    public static String getPermissionName(int value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_PERMISSION + value, locale);
    }

    public static List<NameValuePair> getPermissionNames() {
        return getPermissionNames(ITrackerResources.getLocale());
    }

    public static List<NameValuePair> getPermissionNames(Locale locale) {
        List<NameValuePair> permissions = new ArrayList<>();
        permissions.add(0, new NameValuePair(getPermissionName(PERMISSION_CREATE, locale), Integer.toString(PERMISSION_CREATE)));
        permissions.add(1, new NameValuePair(getPermissionName(PERMISSION_CREATE_OTHERS, locale), Integer.toString(PERMISSION_CREATE_OTHERS)));
        permissions.add(2, new NameValuePair(getPermissionName(PERMISSION_EDIT, locale), Integer.toString(PERMISSION_EDIT)));
        permissions.add(3, new NameValuePair(getPermissionName(PERMISSION_EDIT_USERS, locale), Integer.toString(PERMISSION_EDIT_USERS)));
        permissions.add(4, new NameValuePair(getPermissionName(PERMISSION_EDIT_FULL, locale), Integer.toString(PERMISSION_EDIT_FULL)));
        permissions.add(5, new NameValuePair(getPermissionName(PERMISSION_CLOSE, locale), Integer.toString(PERMISSION_CLOSE)));
        permissions.add(6, new NameValuePair(getPermissionName(PERMISSION_ASSIGNABLE, locale), Integer.toString(PERMISSION_ASSIGNABLE)));
        permissions.add(7, new NameValuePair(getPermissionName(PERMISSION_ASSIGN_SELF, locale), Integer.toString(PERMISSION_ASSIGN_SELF)));
        permissions.add(8, new NameValuePair(getPermissionName(PERMISSION_UNASSIGN_SELF, locale), Integer.toString(PERMISSION_UNASSIGN_SELF)));
        permissions.add(9, new NameValuePair(getPermissionName(PERMISSION_ASSIGN_OTHERS, locale), Integer.toString(PERMISSION_ASSIGN_OTHERS)));
        permissions.add(10, new NameValuePair(getPermissionName(PERMISSION_VIEW_ALL, locale), Integer.toString(PERMISSION_VIEW_ALL)));
        permissions.add(11, new NameValuePair(getPermissionName(PERMISSION_VIEW_USERS, locale), Integer.toString(PERMISSION_VIEW_USERS)));
        permissions.add(12, new NameValuePair(getPermissionName(PERMISSION_PRODUCT_ADMIN, locale), Integer.toString(PERMISSION_PRODUCT_ADMIN)));
        return permissions;
    }
    public static List<NameValuePair> getPermissionTypeNames(Locale locale) {
        List<NameValuePair> permissions = new ArrayList<>();
        permissions.add(0, new NameValuePair(getPermissionName(PERMISSION_CREATE, locale), PermissionType.valueOf(PERMISSION_CREATE).name()));
        permissions.add(1, new NameValuePair(getPermissionName(PERMISSION_CREATE_OTHERS, locale), PermissionType.valueOf(PERMISSION_CREATE_OTHERS).name()));
        permissions.add(2, new NameValuePair(getPermissionName(PERMISSION_EDIT, locale), PermissionType.valueOf(PERMISSION_EDIT).name()));
        permissions.add(3, new NameValuePair(getPermissionName(PERMISSION_EDIT_USERS, locale), PermissionType.valueOf(PERMISSION_EDIT_USERS).name()));
        permissions.add(4, new NameValuePair(getPermissionName(PERMISSION_EDIT_FULL, locale), PermissionType.valueOf(PERMISSION_EDIT_FULL).name()));
        permissions.add(5, new NameValuePair(getPermissionName(PERMISSION_CLOSE, locale), PermissionType.valueOf(PERMISSION_CLOSE).name()));
        permissions.add(6, new NameValuePair(getPermissionName(PERMISSION_ASSIGNABLE, locale), PermissionType.valueOf(PERMISSION_ASSIGNABLE).name()));
        permissions.add(7, new NameValuePair(getPermissionName(PERMISSION_ASSIGN_SELF, locale), PermissionType.valueOf(PERMISSION_ASSIGN_SELF).name()));
        permissions.add(8, new NameValuePair(getPermissionName(PERMISSION_UNASSIGN_SELF, locale), PermissionType.valueOf(PERMISSION_UNASSIGN_SELF).name()));
        permissions.add(9, new NameValuePair(getPermissionName(PERMISSION_ASSIGN_OTHERS, locale), PermissionType.valueOf(PERMISSION_ASSIGN_OTHERS).name()));
        permissions.add(10, new NameValuePair(getPermissionName(PERMISSION_VIEW_ALL, locale), PermissionType.valueOf(PERMISSION_VIEW_ALL).name()));
        permissions.add(11, new NameValuePair(getPermissionName(PERMISSION_VIEW_USERS, locale), PermissionType.valueOf(PERMISSION_VIEW_USERS).name()));
        permissions.add(12, new NameValuePair(getPermissionName(PERMISSION_PRODUCT_ADMIN, locale), PermissionType.valueOf(PERMISSION_PRODUCT_ADMIN).name()));
        return permissions;
    }

    /**
     * Genrates a new random password.  The password that is returned is in plain text.
     *
     * @return a new randon plaintext password
     */
    public static String generatePassword() throws PasswordException {
        StringBuffer buf = new StringBuffer();
        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            buf.append((rand.nextInt(2) == 0 ? Character.toUpperCase(alphabet[rand.nextInt(34)]) : alphabet[rand.nextInt(34)]));
        }
        return buf.toString();
    }

    /**
     * Returns an encrypted (digest) password from a plain text password.
     *
     * @param password the plain text password to encrypt.
     * @return the encrypted password
     */
    public static String encryptPassword(String password) throws PasswordException {
        String hash = null;
        if (password != null && !password.equals("")) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(password.getBytes("UTF-8"));
                byte raw[] = md.digest();
                // TODO: must we really use this BASE64Encoder()? it seems to be not support by jrockit rt.jar
//                hash = Base64.encodeBytes(raw);
                hash = String.valueOf(Base64Coder.encode(raw));
//                hash = (new BASE64Encoder()).encode(raw);
            } catch (NoSuchAlgorithmException nsae) {
                throw new PasswordException(PasswordException.SYSTEM_ERROR);
            } catch (UnsupportedEncodingException uee) {
                throw new PasswordException(PasswordException.SYSTEM_ERROR);
            }
        }
        return hash;
    }


    /**
     * Checks to see if the user is a super user.
     *
     * @param permissionsMap map of user permissions by project Id
     * @return true is the user is a super user
     */
    @Deprecated
    public static boolean isSuperUser(Map<Integer, Set<PermissionType>> permissionsMap) {
        if (permissionsMap == null) {
            return false;
        }

        // Super user has access to all projects, which is indicated by null. 
        final Set<PermissionType> permissionTypes = permissionsMap.get(null);

        return (permissionTypes != null) && permissionTypes.contains(PermissionType.USER_ADMIN);
    }

    @Deprecated
    public static boolean hasPermission(Map<Integer, Set<PermissionType>> permissionsMap, int permissionNeeded) {
        return hasPermission(permissionsMap, PermissionType.valueOf(permissionNeeded));
    }

    /**
     * Returns true if the user has the required permission in any project.
     *
     * @param permissionsMap   a Map of the user's permissions by project ID
     * @param permissionNeeded the permission to check for
     */
    @Deprecated
    public static boolean hasPermission(Map<Integer, Set<PermissionType>> permissionsMap, PermissionType permissionNeeded) {
        if (permissionsMap == null) {
            return false;
        }

        if (isSuperUser(permissionsMap)) {
            return true;
        }

        // Set of project Ids for which the user has permissions. 
        Set<Integer> keySet = permissionsMap.keySet();

        for (Iterator<Integer> iterator = keySet.iterator(); iterator.hasNext(); ) {
            Integer projectId = iterator.next();
            if (hasPermission(permissionsMap, projectId, permissionNeeded)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the user has any of required permissions in any project.
     *
     * @param permissionsMap    a HashMap of the user's permissions
     * @param permissionsNeeded a list of permissions that can fulfill the permission check
     */
    @Deprecated
    public static boolean hasPermission(Map<Integer, Set<PermissionType>> permissionsMap, PermissionType[] permissionsNeeded) {
        if (permissionsMap == null) {
            return false;
        }

        if (isSuperUser(permissionsMap)) {
            return true;
        }

        Set<Integer> keySet = permissionsMap.keySet();

        for (Iterator<Integer> iterator = keySet.iterator(); iterator.hasNext(); ) {
            Integer projectId = iterator.next();

            if (hasPermission(permissionsMap, projectId, permissionsNeeded)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static boolean hasPermission(Map<Integer, Set<PermissionType>> permissionsMap, Integer projectId, int permissionNeeded) {
        return hasPermission(permissionsMap, projectId, PermissionType.valueOf(permissionNeeded));
    }
    /**
     * Returns true if the user has the required permission for the given project.
     *
     * @param permissionsMap   a HashMap of the user's permissions
     * @param projectId        the project that the permission is required for
     * @param permissionNeeded the permission to check for
     */
    public static boolean hasPermission(Map<Integer, Set<PermissionType>> permissionsMap, Integer projectId, PermissionType permissionNeeded) {
        if (permissionsMap == null) {
            return false;
        }

        if (isSuperUser(permissionsMap)) {
            return true;
        }

        final Set<PermissionType> permissionTypes = permissionsMap.get(projectId);

        if ((permissionTypes != null) && permissionTypes.contains(permissionNeeded)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the user has any of required permissions for the given project.
     *
     * @param permissionsMap    a HashMap of the user's permissions
     * @param projectId         the project that the permission is required for
     * @param permissionsNeeded a list of permissions that can fulfill the permission check
     */
    public static boolean hasPermission(Map<Integer, Set<PermissionType>> permissionsMap, Integer projectId, PermissionType[] permissionsNeeded) {
        if (permissionsMap == null) {
            return false;
        }

        if (isSuperUser(permissionsMap)) {
            return true;
        }

        final Set<PermissionType> permissionTypes = permissionsMap.get(projectId);

        if (permissionTypes != null) {
            for (int i = 0; i < permissionsNeeded.length; i++) {
                if (permissionTypes.contains(permissionsNeeded[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getInitial(String name) {
        return (name != null && name.length() > 0 ? name.substring(0, 1).toUpperCase() + "." : "");
    }

    public static Permission[] createPermissionArray(User user, Project project, int[] permissions) {
        Permission[] permissionsArray = new Permission[0];

        List<Permission> permissionsList = new ArrayList<Permission>();

        if (user.isSuperUser()) {
            permissionsList.add(new Permission(PermissionType.valueOf(-1), user, (Project) null));
        }

        for (int i = 0; i < permissions.length; i++) {
            permissionsList.add(new Permission(PermissionType.valueOf(permissions[i]), user, project));
        }
        permissionsArray = new Permission[permissionsList.size()];
        permissionsArray = permissionsList.toArray(new Permission[]{});

        return permissionsArray;
    }

    /**
     * Maps sets of permission types by project ID.
     */
    public static Map<Integer, Set<PermissionType>> mapPermissionTypesByProjectId(
            List<Permission> permissionsList) {

        final Map<Integer, Set<PermissionType>> permissionsByProjectId =
                new HashMap<Integer, Set<PermissionType>>();

        for (int i = 0; i < permissionsList.size(); i++) {
            Permission permission = permissionsList.get(i);

            // Super user has access to all projects, which is indicated by the "null" project. 
            final Integer projectId = (permission.getProject() == null)
                    ? null : permission.getProject().getId();

            Set<PermissionType> projectPermissions = permissionsByProjectId.get(projectId);

            if (projectPermissions == null) {
                // First permission for the project. 
                projectPermissions = new HashSet<PermissionType>();
                permissionsByProjectId.put(projectId, projectPermissions);
            } //else { // Add the permission to the existing set of permissions for the project. }

            PermissionType permissionType = permission.getPermissionType();
            projectPermissions.add(permissionType);
        }
        return permissionsByProjectId;
    }

    /**
     * Returns whether the user is currently hiding a particular section on the myItracker page.
     *
     * @param section  the section to check if it is hidden
     * @param sections an integer of all sections the user is hiding
     * @return true if the current section is hidden
     */
    public static boolean hideIndexSection(int section, int sections) {
        return ((section & sections) == section);
    }

    public static Integer[] getHiddenIndexSections(int sections) {
        List<Integer> sectionsList = new ArrayList<Integer>();
        if (hideIndexSection(PREF_HIDE_ASSIGNED, sections)) {
            sectionsList.add(Integer.valueOf(PREF_HIDE_ASSIGNED));
        }
        if (hideIndexSection(PREF_HIDE_UNASSIGNED, sections)) {
            sectionsList.add(Integer.valueOf(PREF_HIDE_UNASSIGNED));
        }
        if (hideIndexSection(PREF_HIDE_CREATED, sections)) {
            sectionsList.add(Integer.valueOf(PREF_HIDE_CREATED));
        }
        if (hideIndexSection(PREF_HIDE_WATCHED, sections)) {
            sectionsList.add(Integer.valueOf(PREF_HIDE_WATCHED));
        }
        Integer[] sectionsArray = new Integer[sectionsList.size()];
        sectionsList.toArray(sectionsArray);

        return sectionsArray;
    }

}
