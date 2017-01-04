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

package org.itracker.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.itracker.core.resources.ITrackerResources;

/**
 * The system configuration of a User.
 * <p/>
 * <p>
 * User - UserPreferences is a 1-1 relationship.
 * </p>
 *
 * @author ready
 */
public class UserPreferences extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The User to whom these preferences belong.
     */
    private User user;


    private String userLocale = ITrackerResources.getDefaultLocale();

    private int numItemsOnIndex = 0; // all

    private int numItemsOnIssueList = 0; // all

    private boolean showClosedOnIssueList = false;

    private String sortColumnOnIssueList = "id";

    private int hiddenIndexSections = 0;

    private boolean rememberLastSearch = false;

    private boolean useTextActions = false;
    private boolean saveLogin;

    @Deprecated
    public boolean getSaveLogin() {
        return saveLogin;
    }

    @Deprecated
    public void setSaveLogin(boolean saveLogin) {
        this.saveLogin = saveLogin;
    }

    public int getHiddenIndexSections() {
        return hiddenIndexSections;
    }

    public void setHiddenIndexSections(int hiddenIndexSections) {
        this.hiddenIndexSections = hiddenIndexSections;
    }

    public int getNumItemsOnIndex() {
        return numItemsOnIndex;
    }

    public void setNumItemsOnIndex(int numItemsOnIndex) {
        this.numItemsOnIndex = numItemsOnIndex;
    }

    public int getNumItemsOnIssueList() {
        return numItemsOnIssueList;
    }

    public void setNumItemsOnIssueList(int numItemsOnIssueList) {
        this.numItemsOnIssueList = numItemsOnIssueList;
    }

    public boolean getRememberLastSearch() {
        return rememberLastSearch;
    }

    public void setRememberLastSearch(boolean rememberLastSearch) {
        this.rememberLastSearch = rememberLastSearch;
    }

    public boolean getShowClosedOnIssueList() {
        return showClosedOnIssueList;
    }

    public void setShowClosedOnIssueList(boolean showClosedOnIssueList) {
        this.showClosedOnIssueList = showClosedOnIssueList;
    }

    public String getSortColumnOnIssueList() {
        return sortColumnOnIssueList;
    }

    public void setSortColumnOnIssueList(String sortColumnOnIssueList) {
        this.sortColumnOnIssueList = sortColumnOnIssueList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserLocale() {
        return userLocale;
    }

    public void setUserLocale(String userLocale) {
        this.userLocale = userLocale;
    }

    public boolean getUseTextActions() {
        return useTextActions;
    }

    public void setUseTextActions(boolean useTextActions) {
        this.useTextActions = useTextActions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("user", getUser())
                .append("userLocale", getUserLocale()).append("useTextActions",
                        getUseTextActions()).append(
                        "rememberLastSearch", getRememberLastSearch()).append(
                        "hiddenIndexSections", getHiddenIndexSections()).append(
                        "numItemsOnIndex", getNumItemsOnIndex()).append(
                        "numItemsOnIssueList", getNumItemsOnIssueList()).append(
                        "showClosedOnIssueList", getShowClosedOnIssueList())
                .toString();
    }

}
