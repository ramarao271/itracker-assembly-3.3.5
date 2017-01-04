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

import org.apache.log4j.Logger;


public class HTMLUtilities extends org.itracker.util.HTMLUtilities {

    private static final Logger logger = Logger.getLogger(HTMLUtilities.class);

    static {


    }

    public static String removeQuotes(String input) {
        StringBuffer sb = new StringBuffer(input.length());
        int len = input.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = input.charAt(i);
            if (c == '\'') {
                sb.append("''");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String handleQuotes(String input) {
        if (input == null || "".equals(input) || input.indexOf('"') == -1) {
            return input;
        }

        StringBuffer buf = new StringBuffer();

        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') {
                buf.append("&quot;");
            } else {
                buf.append(chars[i]);
            }
        }

        return buf.toString();
    }

    public static String escapeNewlines(String input) {
        if (input == null || "".equals(input) || input.indexOf('\n') == -1) {
            return input;
        }

        StringBuffer buf = new StringBuffer();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\r') {
                continue;
            } else if (chars[i] == '\n') {
                buf.append("\\n");
            } else {
                buf.append(chars[i]);
            }
        }
        return buf.toString();
    }

    public static String newlinesToBreaks(String input) {
        if (input == null || "".equals(input) || input.indexOf('\n') == -1) {
            return input;
        }

        StringBuffer buf = new StringBuffer();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\r') {
                continue;
            } else if (chars[i] == '\n') {
                buf.append("<br>");
            } else {
                buf.append(chars[i]);
            }
        }
        return buf.toString();
    }



    public static String escapeTags(String input) {
        if (null == input) {
            return "";
        }
        StringBuffer sb = new StringBuffer(input.length());
        int len = input.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = input.charAt(i);
            if (c == '"') {
                sb.append("&quot;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else {
                int ci = 0xffff & c;
                if (ci < 160) {
                    // nothing special only 7 Bit
                    sb.append(c);
                } else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(new Integer(ci).toString());
                    sb.append(';');
                }
            }
        }

        return sb.toString();
    }

    /**
     * format a itracker date format for jacascript datepicker
     *
     */
    public static final String getJSDateFormat(String format) {

        String f = format.replace('m', 'n');
        f = f.toLowerCase();
        return f;


    }

}

 