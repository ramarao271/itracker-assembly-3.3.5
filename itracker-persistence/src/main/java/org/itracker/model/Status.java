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

/**
 * Enumeration of Project, Component or Version statuses.
 *
 * @author johnny
 */
public enum Status implements IntCodeEnum<Status> {


    DELETED(-1),

    ACTIVE(1),

    VIEWABLE(2),

    LOCKED(3);


    private final Integer code;

    private Status(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public Status fromCode(Integer code) {
        return Status.valueOf(code);
    }

    public static Status valueOf(Integer code) {
        switch (code) {
            case -1:
                return DELETED;
            case 1:
                return ACTIVE;
            case 2:
                return VIEWABLE;
            case 3:
                return LOCKED;
            default:
                throw new IllegalArgumentException(
                        "Unknown enum code : " + code);
        }
    }

}
