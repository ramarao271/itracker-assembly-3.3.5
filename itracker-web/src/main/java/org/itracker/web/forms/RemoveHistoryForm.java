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

package org.itracker.web.forms;

/**
 * please comment.
 */
public class RemoveHistoryForm extends ITrackerForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    java.lang.Integer historyId;
    java.lang.String caller;

    public java.lang.String getCaller() {
        return caller;
    }

    public void setCaller(java.lang.String caller) {
        this.caller = caller;
    }

    public java.lang.Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(java.lang.Integer historyId) {
        this.historyId = historyId;
    }

}
