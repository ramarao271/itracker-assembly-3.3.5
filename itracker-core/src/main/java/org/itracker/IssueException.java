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


/**
 * This class is used to represent a variety of exceptions that can occur
 * while processing issues.
 */
public class IssueException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 3433495017849044287L;
    public static final String TYPE_UNKNOWN = "itracker.web.error.system";
    public static final String TYPE_CF_INVALID_LIST_OPTION = "itracker.web.error.validate.invalid";
    public static final String TYPE_CF_PARSE_NUM = "itracker.web.error.validate.number";
    public static final String TYPE_CF_PARSE_DATE = "itracker.web.error.validate.date";
    public static final String TYPE_CF_REQ_FIELD = "itracker.web.error.validate.required";

    private String type;

    /**
     * Creates a new IssueException of unknown type.
     */
    public IssueException() {
        type = TYPE_UNKNOWN;
    }

    /**
     * Creates a new IssueException of unknown type with a default message.
     *
     * @param message the exception error message
     */
    public IssueException(String message) {
        super(message);
        type = TYPE_UNKNOWN;
    }

    /**
     * Creates a new IssueException of unknown type with a default message.
     *
     * @param message the exception error message
     */
    public IssueException(String message, Throwable cause) {
        super(message, cause);
        type = TYPE_UNKNOWN;
    }

    /**
     * Creates a new IssueException of specified type with a default message.
     *
     * @param message the exception error message
     * @param type    the exception type represented by a resource bundle key
     */
    public IssueException(String message, String type) {
        super(message);
        this.type = type;
    }

    /**
     * Creates a new IssueException of specified type with a default message.
     *
     * @param message the exception error message
     * @param type    the exception type represented by a resource bundle key
     */
    public IssueException(String message, String type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    /**
     * Returns the exception type which can be used to format a localized
     * error message.
     *
     * @return String resource bundle key representing the exception type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the issue exception type.
     *
     * @param value a String code representing the type of issue exception
     *              that occured.
     */
    public void setType(String value) {
        type = value;
    }
}

  