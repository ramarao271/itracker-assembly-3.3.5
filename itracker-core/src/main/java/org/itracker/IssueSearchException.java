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

package org.itracker;

public class IssueSearchException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 4184441026918406008L;
    public static final int ERROR_UNKNOWN_TYPE = 0;
    public static final int ERROR_SQL_EXCEPTION = 1;
    public static final int ERROR_EJB_EXCEPTION = 2;
    public static final int ERROR_NULL_QUERY = 3;

    private int type;

    public IssueSearchException() {
    }

    public IssueSearchException(String message) {
        super(message);
    }

    public IssueSearchException(String message, int type) {
        super(message);
        setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int value) {
        type = value;
    }
}

  