package org.itracker.model;

/**
 * Enumeration for permission types.
 *
 * @author johnny
 */
public enum PermissionType implements IntCodeEnum<PermissionType> {

    /**
     * User Admin Permission.
     * Currently this is equivalent to super user, since the permission can't be granted,
     * and is only available to an admin.
     */
    USER_ADMIN(-1),

    /**
     * Product Admin Permission
     */
    PRODUCT_ADMIN(1),

    /**
     * Issue Create Permission
     */
    ISSUE_CREATE(2),

    /**
     * Issue Edit Permission.
     * Users with this permission can edit any issue in the project.
     */
    ISSUE_EDIT_ALL(3),

    /**
     * Issue Close Permission.
     * Users with this permission can close issues in the project.
     */
    ISSUE_CLOSE(4),

    /**
     * Issue Assign to Self Permission.
     * Users with this permission can assign issues to themselves.
     */
    ISSUE_ASSIGN_SELF(5),

    /**
     * Issue Assign to Others Permissions.
     * Users with this permission can assign issues to anyone,
     * given than those users have the ability to recieve the assignment.
     */
    ISSUE_ASSIGN_OTHERS(6),

    /**
     * View All Issues Permission.  Users can view all issues in the project.
     */
    ISSUE_VIEW_ALL(7),

    /**
     * View Users Issues Permission.  Users can view thier own issues.
     * This includes ones they are the creator or owner of.
     */
    ISSUE_VIEW_USERS(8),

    /**
     * Edit Users Issues Permission.
     * Users with this permission can edit any issue they created or own.
     * They are limited to editing the description, adding history entries,
     * and adding attachments.
     */
    ISSUE_EDIT_USERS(9),

    /**
     * Issue Unassign Self Permission.
     * Users with this permission can unassign issues they own.
     */
    ISSUE_UNASSIGN_SELF(10),

    /**
     * Issue Assignable.
     * Users with this permission can be assigned any issue in the system.
     * To determine if a user can be assigned an issue,
     * it will be a combination of users with EDIT_ALL,
     * users with EDIT_USERS if they are the creator,
     * and users with this permission and EDIT_USERS.
     */
    ISSUE_ASSIGNABLE(11),

    /**
     * Create for Others.
     * Users with this permission are allowed to create issues on behalf of other users.
     * The system will treat the issue as if the other user had created it.
     * The actual creator will be logged in the audit log.
     */
    ISSUE_CREATE_OTHERS(12),

    /**
     * Full edit permission.
     * This defines what levelof editing a user has for an issue.
     * Without this permission, users will
     * be limited to editing only the description, attachments, custom fields,
     * and history of an issue.
     */
    ISSUE_EDIT_FULL(13);

    /* The project value matches the enum order (except for USER_ADMIN which is at position 0) */
    private static final PermissionType[] PERMISSION_TYPES = values();

    /**
     * The integer value of this enum member.
     */
    private final int code;

    /**
     * Creates a new instance of this enum.
     *
     * @param code unique value representing this instance
     */
    PermissionType(Integer code) {
        this.code = code;
    }

    /**
     * Returns the integer value representing this enum member.
     *
     * @return unique value representing this instance
     */
    public Integer getCode() {
        return code;
    }

    public static PermissionType valueOf(Integer type) {
        return values()[0].fromCode(type);
    }

    @Deprecated
    public static PermissionType[] valueOf(int[] type) {
        if (null == type) {
            return null;
        }
        final PermissionType[] result = new PermissionType[type.length];
        int c = 0;
        for (Integer i: type) {
            result[c++] = valueOf(i);
        }
        return result;
    }


    /**
     * Returns the enum instance matching the integer value.
     *
     * @param code unique value of the enum instance to return
     * @return enum instance matching the int value
     */
    public PermissionType fromCode(Integer code) {
        if (code == 0 || code < -1 || code > 13) {
            throw new IllegalArgumentException("Unknown PermissionType code " + code);
        }
        if (code == -1) {
            return PERMISSION_TYPES[0];
        }
        return PERMISSION_TYPES[code];
    }

    public String name(Project project) {
        if (null != project) {
            if (project.isNew()) {
                throw new IllegalStateException("New project can't be granted.");
            }
            return name() + "#" + project.getId();
        }
        return name();
    }

}
