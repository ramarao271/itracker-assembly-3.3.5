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

package org.itracker.services.exceptions;

/**
 * This class encapsulates the errors that may occur during a login
 * or other types of actions typically performed by a pluggable
 * authentication module.<br><br>
 * A pluggable authentication module should set the type of error generated
 * using the setType method, or the appropriate constructor.  If the type
 * of error does not match one of the existing types and the error should
 * be returned to the user, the module should use the CUSTOM_ERROR type,
 * and then also populate the the messageKey attribute with a key that would
 * be suitable for display to the user.<br><br>
 * This class can also be used to send the user to a custom error page in the
 * event of a failure.  If this is required, the page type should be set using
 * the setErrorPageType method, and the appropriate value for the type is set
 * using setErrorPageValue.  The currently supported types are either a URL
 * or a Struts forward action mapping..
 */
public class AuthenticatorException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -7799413588815903874L;
    public static final int INVALID_DATA = -1;
    public static final int UNKNOWN_USER = -2;
    public static final int INVALID_PASSWORD = -3;
    public static final int INACTIVE_ACCOUNT = -4;
    public static final int SYSTEM_ERROR = -5;
    public static final int INVALID_AUTHENTICATION_TYPE = -6;
    public static final int CUSTOM_ERROR = -7;

    public static final int ERRORPAGE_TYPE_UNDEFINED = -1;
    public static final int ERRORPAGE_TYPE_FORWARD = 1;
    public static final int ERRORPAGE_TYPE_URL = 2;

    private int type = 0;
    private String messageKey = "itracker.web.error.login.system";
    private int errorPageType = ERRORPAGE_TYPE_UNDEFINED;
    private String errorPageValue = null;

    public AuthenticatorException() {
    }

    public AuthenticatorException(int type) {
        this.type = type;
    }

    public AuthenticatorException(int type, String messageKey) {
        this(type);
        this.messageKey = messageKey;
    }

    public AuthenticatorException(String message, int type) {
        super(message);
        this.type = type;
    }

    public AuthenticatorException(String message, int type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public AuthenticatorException(String message, int type, String messageKey) {
        this(message, type);
        this.messageKey = messageKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        String message = super.getMessage();
        if (message == null || message.equals("")) {
            message = "Empty message, type: " + getTypeString();
        }

        return message;
    }

    /**
     * Returns a key that contains a custom error message to display to the user.
     *
     * @return a resource key that can be used to look up the custom error
     *         message for this exception.
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Sets a key that contains a custom error message to display to the user.
     *
     * @param messageKey a resource key that can be used to look up the custom error
     *                   message for this exception.
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Returns the type of error page that is has been set.
     * Supported values are urls and Struts forward action mappings.
     *
     * @see AuthenticatorException#ERRORPAGE_TYPE_FORWARD
     * @see AuthenticatorException#ERRORPAGE_TYPE_URL
     */
    public int getErrorPageType() {
        return errorPageType;
    }

    /**
     * Sets the type of error page that should be used to display this exception.
     * Supported values are urls and Struts forward action mappings.
     *
     * @param value the type of error page that has been set
     * @see AuthenticatorException#ERRORPAGE_TYPE_FORWARD
     * @see AuthenticatorException#ERRORPAGE_TYPE_URL
     */
    public void setErrorPageType(int value) {
        errorPageType = value;
    }

    /**
     * Returns the error page that should be used to display this exception
     * Supported values are urls and Struts forward action mappings.  The type that
     * has been set must be identified using the setErrorPageType method.
     *
     * @see AuthenticatorException#setErrorPageType
     */
    public String getErrorPageValue() {
        return errorPageValue;
    }

    /**
     * Returns the error page that should be used to display this exception
     * Supported values are urls and Struts forward action mappings.  The type that
     * has been set must be identified using the setErrorPageType method.
     *
     * @param value the error page that should be used to display this message
     * @see AuthenticatorException#setErrorPageType
     */
    public void setErrorPageValue(String value) {
        errorPageValue = value;
    }

    private String getTypeString() {
        if (type == INVALID_DATA) {
            return "Invalid Data";
        } else if (type == UNKNOWN_USER) {
            return "Unknown User";
        } else if (type == INVALID_PASSWORD) {
            return "Invalid Password";
        } else if (type == INACTIVE_ACCOUNT) {
            return "Inactive Account";
        } else if (type == SYSTEM_ERROR) {
            return "System Error";
        } else if (type == INVALID_AUTHENTICATION_TYPE) {
            return "Invalid Authentication Type";
        } else if (type == CUSTOM_ERROR) {
            return "Custom Error.  Check message key.";
        }

        return "Unknown Type";
    }
}

  