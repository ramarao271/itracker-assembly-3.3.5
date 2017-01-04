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

public class PasswordException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 7738934888428402714L;
    public static final int INVALID_DATA = -1;
    public static final int UNKNOWN_USER = -2;
    public static final int INVALID_NAME = -3;
    public static final int INVALID_EMAIL = -4;
    public static final int INACTIVE_ACCOUNT = -5;
    public static final int SYSTEM_ERROR = -6;
    public static final int FEATURE_DISABLED = -7;

    private int type = 0;

    public PasswordException() {
    }

    public PasswordException(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}

  