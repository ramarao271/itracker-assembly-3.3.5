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

import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.NameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Contains utilities used when displaying and processing workflow and field events
 */
public class WorkflowUtilities {

    /**
     * Fires for each field when building the form.  Mainly used to build dynamic list options.
     */
    public static final int EVENT_FIELD_ONPOPULATE = 1;
    /**
     * NOT CURRENTLY IMPLEMENTED.  Use the onPopulate event instead. In the future, this event may be implemented to allow for list sorting after list value population.
     */
    public static final int EVENT_FIELD_ONSORT = 2;
    /**
     * Fires to set the current value of a form field.  This will overwrite any data in the form field pulled from the database.
     */
    public static final int EVENT_FIELD_ONSETDEFAULT = 3;
    /**
     * Fires on validation of the form field.
     */
    public static final int EVENT_FIELD_ONVALIDATE = 4;
    /**
     * Fires after validation, but before the data is committed to the database.
     */
    public static final int EVENT_FIELD_ONPRESUBMIT = 5;
    /**
     * Fires after all data is submitted to the db for all fields. Performed right before the response is sent.
     */
    public static final int EVENT_FIELD_ONPOSTSUBMIT = 6;

    private static final Logger logger = Logger.getLogger(WorkflowUtilities.class);

    public WorkflowUtilities() {
    }

    /**
     * Returns a title of workflow event, according to selected locale.
     *
     * @param value  is an identifier of incoming event.
     * @param locale is a selected locale.
     * @return a name of event or something like "MISSING KEY: <resourceBundleKey>".
     */
    public static String getEventName(int value, Locale locale) {
        final String eventName = getEventName(Integer.toString(value), locale);
        return eventName;
    }

    public static String getEventName(String value, Locale locale) {
        return ITrackerResources.getString(ITrackerResources.KEY_BASE_WORKFLOW_EVENT + value, locale);
    }

    /**
     * Returns an array of pairs (eventName, eventId), where eventName
     * is an event title, according to selected locale.
     *
     * @param locale is a selected locale.
     * @return an array of pairs (eventName, eventId), which is never null.
     */
    public static NameValuePair[] getEvents(Locale locale) {
        NameValuePair[] eventNames = new NameValuePair[5];
        eventNames[0] = new NameValuePair(getEventName(EVENT_FIELD_ONPOPULATE, locale), Integer.toString(EVENT_FIELD_ONPOPULATE));
//        eventNames[] = new NameValuePair(getEventName(EVENT_FIELD_ONSORT, locale), Integer.toString(EVENT_FIELD_ONSORT));
        eventNames[1] = new NameValuePair(getEventName(EVENT_FIELD_ONSETDEFAULT, locale), Integer.toString(EVENT_FIELD_ONSETDEFAULT));
        eventNames[2] = new NameValuePair(getEventName(EVENT_FIELD_ONVALIDATE, locale), Integer.toString(EVENT_FIELD_ONVALIDATE));
        eventNames[3] = new NameValuePair(getEventName(EVENT_FIELD_ONPRESUBMIT, locale), Integer.toString(EVENT_FIELD_ONPRESUBMIT));
        eventNames[4] = new NameValuePair(getEventName(EVENT_FIELD_ONPOSTSUBMIT, locale), Integer.toString(EVENT_FIELD_ONPOSTSUBMIT));
        return eventNames;
    }

    /**
     * Select a list of NameValuePair objects from provided map object according
     * to fieldId selector. Typesafe version of #getListOptions(Map, Integer)
     *
     * @param listOptions is a map, with stored NameValuePair objects lists
     *                    associated with specific integer id.
     * @param fieldId     is a selector from map.
     * @return a list of objects, which may be empty, but never null.
     */
    public static List<NameValuePair> getListOptions(Map<Integer, List<NameValuePair>> listOptions, int fieldId) {
        return getListOptions(listOptions, Integer.valueOf(fieldId));
    }

    /**
     * Select a list of NameValuePair objects from provided map object according
     * to fieldId selector.
     *
     * @param listOptions is a map, with stored NameValuePair objects lists
     *                    associated with specific integer id.
     * @param fieldId     is a selector from map.
     * @return a list of objects, which may be empty, but never null.
     */
    @SuppressWarnings("unchecked")
    public static List<NameValuePair> getListOptions(Map listOptions, Integer fieldId) {
        List<NameValuePair> options = new ArrayList<NameValuePair>();

        if (listOptions != null && listOptions.size() != 0 && fieldId != null) {
            Object mapOptions = listOptions.get(fieldId);
            if (mapOptions != null) {
                options = (List<NameValuePair>) mapOptions;
            }
        }

        return options;
    }

}
