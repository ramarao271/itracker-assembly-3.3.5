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

package org.itracker.model.util;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.User;

import java.util.*;

public class NotificationUtilities {

    /**
     * @deprecated, use enumeration Notification.Role instead
     */
    private static HashMap<Locale, HashMap<Role, String>> roleNames = new HashMap<Locale, HashMap<Role, String>>();

    public NotificationUtilities() {
        super();
    }

    /**
     * @deprecated
     */
    public static String getRoleName(int value) {
        return getRoleName(value, ITrackerResources.getLocale());
    }

    /**
     * @deprecated
     */
    public static String getRoleName(int value, Locale locale) {
        return ITrackerResources.getString("itracker.notification.role."
                + value, locale);
    }

    public static String getRoleName(Role role) {
        return getRoleName(role, ITrackerResources.getLocale());
    }

    public static String getRoleName(Role role, Locale locale) {
        String s;
        if (null != (s = ITrackerResources.getString(
                "itracker.notification.role." + role, locale))) {
            return s;
        }

        return ITrackerResources.getString("itracker.notification.role."
                + role.getCode(), locale);
    }


    public static HashMap<Role, String> getRoleNames(Locale locale) {
        HashMap<Role, String> roles = roleNames.get(locale);
        if (roles == null) {
            roles = new HashMap<Role, String>();
            roles.put(Notification.Role.CREATOR,
                    getRoleName(Notification.Role.CREATOR, locale));
            roles.put(Notification.Role.OWNER, getRoleName(Role.OWNER, locale));
            roles.put(Notification.Role.CONTRIBUTER, getRoleName(
                    Notification.Role.CONTRIBUTER, locale));
            roles.put(Notification.Role.QA, getRoleName(Role.QA, locale));
            roles.put(Notification.Role.PM, getRoleName(Role.PM, locale));
            roles.put(Notification.Role.PO, getRoleName(Role.PO, locale));
            roles.put(Notification.Role.CO, getRoleName(Role.CO, locale));
            roles.put(Notification.Role.VO, getRoleName(Role.VO, locale));
            roles.put(Notification.Role.IP, getRoleName(Role.IP, locale));
        }
        roleNames.put(locale, roles);
        return roles;
    }


    public static final Map<User, Set<Notification.Role>> mappedRoles(List<Notification> notifications) {

        Map<User, Set<Role>> mapping = new Hashtable<User, Set<Role>>();
        Iterator<Notification> notificationIt = notifications.iterator();
        while (notificationIt.hasNext()) {
            Notification notification = (Notification) notificationIt.next();
            Set<Role> roles;
            if (mapping.keySet().contains(notification.getUser())) {
                roles = mapping.get(notification.getUser());
                roles.add(notification.getRole());
            } else {
                roles = new TreeSet<Role>(new Comparator<Role>() {
                    public int compare(Role o1, Role o2) {
                        return new CompareToBuilder().append(o1.getCode(), o2.getCode()).toComparison();
                    }
                });
                roles.add(notification.getRole());
                mapping.put(notification.getUser(), roles);
            }
        }


        return mapping;
    }

}
