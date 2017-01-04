/**
 *
 */
package org.itracker.model;

public enum IssueActivityType implements IntCodeEnum<IssueActivityType> {

    ISSUE_CREATED(1),

    STATUS_CHANGE(2),

    OWNER_CHANGE(3),

    SEVERITY_CHANGE(4),

    COMPONENTS_MODIFIED(5),

    VERSIONS_MODIFIED(6),

    REMOVE_HISTORY(7),

    ISSUE_MOVE(8),

    SYSTEM_UPDATE(9),

    TARGETVERSION_CHANGE(10),

    DESCRIPTION_CHANGE(11),

    RESOLUTION_CHANGE(12),

    RELATION_ADDED(13),

    RELATION_REMOVED(14),

    ATTACHMENT_ADDED(15);

    final Integer code;

    private IssueActivityType(Integer code) {
        this.code = code;
    }

    /**
     * @deprecated
     */
    public static final IssueActivityType forCode(Integer type) {
        return IssueActivityType.values()[0].fromCode(type);
    }
    public static final IssueActivityType valueOf(Integer type) {
        return IssueActivityType.values()[0].fromCode(type);
    }

    public IssueActivityType fromCode(Integer code) {
        for (IssueActivityType c : IssueActivityType.values()) {
            if (c.getCode()== code) {
                return c;
            }
        }
        return null;
    }

    public Integer getCode() {
        return this.code;
    }
}