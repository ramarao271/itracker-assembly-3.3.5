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

public class ReportException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -5788612890885772801L;
    private String errorKey = null;

    public ReportException() {
    }

    public ReportException(String message) {
        super(message);
    }

    public ReportException(Throwable e) {
        super(e);
    }

    public ReportException(String message, String errorKey) {
        this(message);
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }
}

  