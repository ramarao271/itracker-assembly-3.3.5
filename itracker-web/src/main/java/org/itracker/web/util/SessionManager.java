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

import java.util.Date;
import java.util.HashMap;

/**
 * Seems to manage the Web Sessions...
 *
 * @author ready
 */
public class SessionManager {
    private static HashMap<String, Date> activeSessionsStarted = new HashMap<String, Date>();
    private static HashMap<String, Date> activeSessionsLastAccess = new HashMap<String, Date>();
    private static HashMap<String, Integer> activeSessionsReset = new HashMap<String, Integer>();
    private static HashMap<String, String> renamedLogins = new HashMap<String, String>();

    public SessionManager() {
    }

    public static int getNumActiveSessions() {
        return activeSessionsStarted.size();
    }

    public static Date getSessionStart(String login) {
        return activeSessionsStarted.get(login);
    }

    public static Date getSessionLastAccess(String login) {
        return  activeSessionsLastAccess.get(login);
    }

    public static boolean getSessionNeedsReset(String login) {
        return activeSessionsReset.get(login) != null ? true : false;
    }

    public static void createSession(String login) {
        Date now = new Date();
        activeSessionsStarted.put(login, now);
        activeSessionsLastAccess.put(login, now);
    }

    public static void invalidateSession(String login) {
        activeSessionsLastAccess.remove(login);
        activeSessionsStarted.remove(login);
        activeSessionsReset.remove(login);
    }

    public static void setSessionNeedsReset(String login) {
        activeSessionsReset.put(login, 1);
    }

    public static void addRenamedLogin(String prevLogin, String newLogin) {
        renamedLogins.put(prevLogin, newLogin);
    }

    public static void removeRenamedLogin(String prevLogin) {
        renamedLogins.remove(prevLogin);
    }

    public static String checkRenamedLogin(String prevLogin) {
        if (renamedLogins.containsKey(prevLogin)) {
            return renamedLogins.get(prevLogin);
        }

        return null;
    }

    public static void clearSessionNeedsReset(String login) {
        activeSessionsReset.remove(login);
    }
}