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

import org.itracker.model.*;

import java.util.*;

public class Convert {
    /**
     * Converts an array of CustomFieldValueModels to NameValuePairs
     *
     * @param options the array of CustomFieldValueModels to convert
     * @return the new NameValuePair array
     */
    @Deprecated
    public static List<NameValuePair> customFieldOptionsToNameValuePairs(
            List<CustomFieldValue> options) {
        return customFieldOptionsToNameValuePairs(options, null);

    }

    /**
     * Converts an array of CustomFieldValueModels to NameValuePairs
     *
     * @param options the array of CustomFieldValueModels to convert
     * @return the new NameValuePair array
     */
    private static List<NameValuePair> customFieldOptionsToNameValuePairs(
            List<CustomFieldValue> options, Locale locale) {
        List<NameValuePair> returnValues = new ArrayList<NameValuePair>();
        String name;
        if (options != null) {
            returnValues = new ArrayList<NameValuePair>();
            for (int i = 0; i < options.size(); i++) {
                name = CustomFieldUtilities.getCustomFieldOptionName(options.get(i), locale);
                returnValues
                        .add(new NameValuePair(name, options.get(i).getValue()));
            }
        }

        return returnValues;
    }

    /**
     * Returns the options sorted-
     * @param customField LIST custom-field
     * @param locale a locale to translate values
     * @return
     */
    public static List<NameValuePair> customFieldOptionsToNameValuePairs(
            CustomField customField, Locale locale) {
        List<CustomFieldValue> kvs = customField.getOptions();
        if (!customField.isSortOptionsByName()) {
            Collections.sort(kvs, CustomFieldValue.SORT_ORDER_COMPARATOR);
        }
        List<NameValuePair> opts = customFieldOptionsToNameValuePairs(customField.getOptions(), locale);
        if (customField.isSortOptionsByName()) {
            Collections.sort(opts, NameValuePair.KEY_COMPARATOR);
        }
        return opts;
    }

    /**
     * Converts an array of UserModels to NameValuePairs
     *
     * @param users the array of UserModels to convert
     * @return the new NameValuePair array
     */
    public static List<NameValuePair> usersToNameValuePairs(List<User> users) {
        List<NameValuePair> returnValues = new ArrayList<NameValuePair>();

        if (users != null) {
            returnValues = new ArrayList<NameValuePair>();
            for (int i = 0; i < users.size(); i++) {
                returnValues.add(new NameValuePair(users.get(i).getFirstName()
                        + " " + users.get(i).getLastName(), users.get(i)
                        .getId().toString()));
            }
        }

        return returnValues;
    }

    /**
     * Converts an array of ComponentModels to NameValuePairs
     *
     * @param options the array of ComponentModels to convert
     * @return the new NameValuePair array
     */
    public static List<NameValuePair> componentsToNameValuePairs(
            List<Component> components) {
        NameValuePair[] returnValues = new NameValuePair[0];

        if (components != null) {
            returnValues = new NameValuePair[components.size()];
            for (int i = 0; i < components.size(); i++) {
                returnValues[i] = new NameValuePair(
                        components.get(i).getName(), components.get(i).getId()
                        .toString());
            }
        }

        return Arrays.asList(returnValues);
    }

    /**
     * Converts an array of VersionModels to NameValuePairs
     *
     * @param options the array of VersionModels to convert
     * @return the new NameValuePair array
     */
    public static List<NameValuePair> versionsToNameValuePairs(
            List<Version> versions) {
        NameValuePair[] returnValues = new NameValuePair[0];

        if (versions != null) {
            returnValues = new NameValuePair[versions.size()];
            for (int i = 0; i < versions.size(); i++) {
                returnValues[i] = new NameValuePair(
                        versions.get(i).getNumber(), versions.get(i).getId()
                        .toString());
            }
        }

        return Arrays.asList(returnValues);
    }

    public static String[] stringToArray(String input) {
        if (input == null || "".equals(input)) {
            return new String[0];
        }

        List<String> tokenList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(input, " ");
        while (tokenizer.hasMoreElements()) {
            boolean quotedToken = false;
            String tokenString = tokenizer.nextToken();
            if (tokenString.startsWith("\"")) {
                quotedToken = true;
                tokenString = tokenString.substring(1);
                if (tokenString.endsWith("\"") && !tokenString.endsWith("\\\"")) {
                    quotedToken = false;
                    tokenString = tokenString.substring(0, tokenString.length() - 1);
                }
            }

            if (quotedToken) {
                boolean getNext = true;

                StringBuffer token = new StringBuffer(tokenString);
                while (getNext) {
                    try {
                        token.append(tokenizer.nextToken("\""));
                        if (!token.toString().endsWith("\\\"")) {
                            getNext = false;
                        }
                    } catch (NoSuchElementException e) {
                        break;
                    }
                }
                tokenString = token.toString();
            }

            tokenList.add(tokenString);
        }

        String[] stringArray = (String[]) tokenList.toArray(new String[]{});

        return stringArray;
    }
}
