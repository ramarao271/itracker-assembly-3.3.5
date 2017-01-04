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

package org.itracker.core;


/**
 * This interface defines some constants used by the pluggable authentication system.
 */
public interface AuthenticationConstants {
    /**
     * The authentication information is of an unknown type, or not provided.  The authenticator
     * in this case will be the request object if available.
     */
    public static final int AUTH_TYPE_UNKNOWN = -1;
    /**
     * The authentication information is a String object containing the plaintext password.
     */
    public static final int AUTH_TYPE_PASSWORD_PLAIN = 1;
    /**
     * The authentication information is a String object containing the SHA1 hash of the
     * plaintext password.
     */
    public static final int AUTH_TYPE_PASSWORD_ENC = 2;
    /**
     * The authentication information is an String object containing shared secret of
     * some type, or a unique key.
     */
    public static final int AUTH_TYPE_SHARED_SECRET = 3;
    /**
     * The authentication information is a Certificate object containing the certificate
     * presented by the user.
     */
    public static final int AUTH_TYPE_CERTIFICATE = 4;
    /**
     * The authentication information is a HttpServletRequest object containing the required
     * authentication information in request or session attributes/parameters.
     */
    public static final int AUTH_TYPE_REQUEST = 5;

    /**
     * The type of update being performed only includes core profile information, and possibly the password
     */
    public static final int UPDATE_TYPE_CORE = 1;
    /**
     * The type of update being performed only includes permission information.  All permissions are being updated.
     */
    public static final int UPDATE_TYPE_PERMISSION_SET = 2;
    /**
     * The type of update being performed only includes permission information.  Only additional permissions are being added.
     */
    public static final int UPDATE_TYPE_PERMISSION_ADD = 3;
    /**
     * The type of update being performed only includes user preferences
     */
    public static final int UPDATE_TYPE_PREFERENCE = 4;


    /**
     * The authentication request is being made from an unknown location
     */
    public static final int REQ_SOURCE_UNKNOWN = -1;
    /**
     * The authentication request is being made from the supplied web application
     */
    public static final int REQ_SOURCE_WEB = 1;
    /**
     * The authentication request is being made from an API call
     */
    public static final int REQ_SOURCE_API = 2;
}
