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

import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Status;

import java.util.*;


public class ProjectUtilities {
    // Options use bitmasks and are stored as a single integer in the db
    public static final int OPTION_SURPRESS_HISTORY_HTML = 1;
    public static final int OPTION_ALLOW_ASSIGN_TO_CLOSE = 2;
    public static final int OPTION_PREDEFINED_RESOLUTIONS = 4;
    public static final int OPTION_ALLOW_SELF_REGISTERED_CREATE = 8;
    public static final int OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL = 16;
    public static final int OPTION_NO_ATTACHMENTS = 32;
    public static final int OPTION_LITERAL_HISTORY_HTML = 64;

    /**
     * Cache of status names by Locale, loaded lazily on first request
     * from itracker.properties.
     * <p/>
     * The Map implementation is synchronized because it can be accessed
     * by multiple threads that will alter it in case of cache miss.
     */
    private static final Map<Locale, Map<Status, String>> statusNames =
            new Hashtable<Locale, Map<Status, String>>();

    /**
     * Contains only static methods and isn't intended to be instantiated.
     */
    private ProjectUtilities() {
    }

    /**
     * Returns the localized name of the given status for the application
     * default locale.
     *
     * @param status enum constant of which we want the localized name
     * @return name in the current locale
     */
    public static String getStatusName(Status status) {
        return getStatusName(status, ITrackerResources.getLocale());
    }

    /**
     * Returns the localized name of the given status for the given locale.
     *
     * @param status enum constant of which we want the localized name
     * @param locale desired locale
     * @return name in the given locale or "MISSING RESOURCE " + resource key
     *         if no resource could be found
     */
    public static String getStatusName(Status status, Locale locale) {
        return ITrackerResources.getString(
                ITrackerResources.KEY_BASE_PROJECT_STATUS + status.getCode(),
                locale);
    }

    /**
     * @return unmodifiable map of status names for the application default Locale
     */
    @Deprecated
    public static Map<Status, String> getStatusNames() {
        return getStatusNames(ITrackerResources.getLocale());
    }

    /**
     * This method loads the status names in the cache if they're not
     * found in it.
     * <p/>
     * <p>The returned map is cached for future requests. </p>
     *
     * @return unmodifiable map of status names for the requested Locale
     */
    public static Map<Status, String> getStatusNames(Locale locale) {
        Map<Status, String> statuses = statusNames.get(locale);


        if (statuses == null) {
            // No labels found for the requested Locale => load in cache.
            statuses = new EnumMap<Status, String>(Status.class);

            for (Status status : Status.values()) {
                statuses.put(status, getStatusName(status, locale));


            }
            statusNames.put(locale, Collections.unmodifiableMap(statuses));

        }
        return Collections.unmodifiableMap(statusNames.get(locale));

    }

    public static boolean hasOption(int option, int currentOptions) {
        return ((option & currentOptions) == option);
    }

    public static Integer[] getOptions(int currentOptions) {
        List<Integer> options = new ArrayList<Integer>();
        if (hasOption(OPTION_SURPRESS_HISTORY_HTML, currentOptions)) {
            options.add(OPTION_SURPRESS_HISTORY_HTML);
        }
        if (hasOption(OPTION_ALLOW_ASSIGN_TO_CLOSE, currentOptions)) {
            options.add(OPTION_ALLOW_ASSIGN_TO_CLOSE);
        }
        if (hasOption(OPTION_PREDEFINED_RESOLUTIONS, currentOptions)) {
            options.add(OPTION_PREDEFINED_RESOLUTIONS);
        }
        if (hasOption(OPTION_ALLOW_SELF_REGISTERED_CREATE, currentOptions)) {
            options.add(OPTION_ALLOW_SELF_REGISTERED_CREATE);
        }
        if (hasOption(OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL, currentOptions)) {
            options.add(OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL);
        }
        if (hasOption(OPTION_NO_ATTACHMENTS, currentOptions)) {
            options.add(OPTION_NO_ATTACHMENTS);
        }
        if (hasOption(OPTION_LITERAL_HISTORY_HTML, currentOptions)) {
            options.add(OPTION_LITERAL_HISTORY_HTML);
        }
        Integer[] optionsArray = new Integer[options.size()];
        options.toArray(optionsArray);
        return optionsArray;
    }

    public static String getScriptPriorityLabelKey(Integer fieldId) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_PRIORITY + fieldId + ITrackerResources.KEY_BASE_PRIORITY_LABEL);
    }

    public static String getScriptPrioritySize() {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_PRIORITY + ITrackerResources.KEY_BASE_PRIORITY_SIZE);
    }
}
