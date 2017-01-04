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

package org.itracker.core.resources;

import java.util.Locale;

/**
 * This class provides support for message replacement when there is a need for more than
 * 10 arguements.  Currently the only additional formatting it accepts are number patterns.
 */
public class MessageFormat {

    public static String format(String message, Object[] options) {
        return format(message, options, ITrackerResources.getLocale());
    }

    public static String format(String message, Object[] options, Locale locale) {
        java.text.MessageFormat f = new java.text.MessageFormat(message, locale);
        return f.format(options).toString();

    }

}
