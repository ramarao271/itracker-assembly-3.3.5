package org.itracker.web.ptos;

import org.itracker.model.Issue;
import org.itracker.model.User;

import java.util.List;


public class IssuePTO {

    private Issue issue;
    private boolean userCanEdit;
    private boolean userCanViewIssue;
    private boolean userHasIssueNotification;
    private boolean userHasPermission_PERMISSION_ASSIGN_SELF;
    private boolean userHasPermission_PERMISSION_ASSIGN_OTHERS;
    private boolean unassigned;
    private String severityLocalizedString;
    private String statusLocalizedString;
    private String componentsSize;
    private List<User> possibleOwners;

    public IssuePTO(Issue issue) {
        this.issue = issue;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getSeverityLocalizedString() {
        return severityLocalizedString;
    }

    public void setSeverityLocalizedString(String severityLocalizedString) {
        this.severityLocalizedString = severityLocalizedString;
    }

    public String getStatusLocalizedString() {
        return statusLocalizedString;
    }

    public void setStatusLocalizedString(String statusLocalizedString) {
        this.statusLocalizedString = statusLocalizedString;
    }

    public boolean isUserCanEdit() {
        return userCanEdit;
    }

    public void setUserCanEdit(boolean userCanEdit) {
        this.userCanEdit = userCanEdit;
    }

    public boolean isUserCanViewIssue() {
        return userCanViewIssue;
    }

    public void setUserCanViewIssue(boolean userCanViewIssue) {
        this.userCanViewIssue = userCanViewIssue;
    }

    public boolean isUserHasIssueNotification() {
        return userHasIssueNotification;
    }

    public void setUserHasIssueNotification(boolean userHasIssueNotification) {
        this.userHasIssueNotification = userHasIssueNotification;
    }

    public String getComponentsSize() {
        return componentsSize;
    }

    public void setComponentsSize(String componentsSize) {
        this.componentsSize = componentsSize;
    }

    public boolean isUnassigned() {
        return unassigned;
    }

    public void setUnassigned(boolean unassigned) {
        this.unassigned = unassigned;
    }

    public boolean getUserHasPermission_PERMISSION_ASSIGN_SELF() {
        return userHasPermission_PERMISSION_ASSIGN_SELF;
    }

    public void setUserHasPermission_PERMISSION_ASSIGN_SELF(boolean userHasPermission) {
        this.userHasPermission_PERMISSION_ASSIGN_SELF = userHasPermission;
    }

    public boolean getUserHasPermission_PERMISSION_ASSIGN_OTHERS() {
        return userHasPermission_PERMISSION_ASSIGN_OTHERS;
    }

    public void setUserHasPermission_PERMISSION_ASSIGN_OTHERS(
            boolean userHasPermission_PERMISSION_ASSIGN_OTHERS) {
        this.userHasPermission_PERMISSION_ASSIGN_OTHERS = userHasPermission_PERMISSION_ASSIGN_OTHERS;
    }

    public void setPossibleOwners(List<User> users) {
        this.possibleOwners = users;
    }

    public List<User> getPossibleOwners() {
        return possibleOwners;
    }

}
