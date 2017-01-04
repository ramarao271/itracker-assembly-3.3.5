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

package org.itracker.web.util;

import java.io.Serializable;
import java.util.Date;

/**
 * What's this for? Please comment!
 *
 * @author ready
 */
public class SessionTracker implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //	private static transient final Logger logger = Logger
//			.getLogger(SessionTracker.class);
    private Date now;
    private String login;
    private String sessionId;

    public SessionTracker() {
        now = new Date();
    }

    public SessionTracker(String login, String sessionId) {
        this();
        this.login = login;
        this.sessionId = sessionId;
    }

    protected void finalize() throws Throwable {
//		if (logger.isDebugEnabled()) {
//			logger.debug("finalize: Invalidating SessionManager info for " + this.login);
//		}
        SessionManager.invalidateSession(this.login);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getNow() {
        if (null == now)
            return null;
        return new Date(now.getTime());
    }

    public void setNow(Date now) {
        if (null == now)
            this.now = null;
        else
            this.now = new Date(now.getTime());
    }
}
