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

public class SystemConfigurationException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 3619504720231179133L;
    private String key;

    public SystemConfigurationException() {
    }

    public SystemConfigurationException(String message) {
        super(message);
    }

    public SystemConfigurationException(String message, String key) {
        super(message);
        setKey(key);
    }

    public String getKey() {
        return (key == null ? "itracker.web.error.system" : key);
    }

    public void setKey(String value) {
        key = value;
    }
}

  