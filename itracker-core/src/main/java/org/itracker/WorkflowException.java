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

public class WorkflowException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -3826882855960029370L;

    public static final int INVALID_ARGS = -1;

    private int type = 0;

    public WorkflowException() {
    }

    public WorkflowException(String message) {
        super(message);
    }

    public WorkflowException(int type) {
        setType(type);
    }

    public WorkflowException(String message, int type) {
        super(message);
        setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}

  