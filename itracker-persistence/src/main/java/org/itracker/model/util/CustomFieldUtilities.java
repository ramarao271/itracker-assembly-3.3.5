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
import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class CustomFieldUtilities {

    public static final String DATE_FORMAT_UNKNOWN = "UNKNOWN";
    public static final String DATE_FORMAT_FULL = "full";
    public static final String DATE_FORMAT_DATEONLY = "dateonly";
    public static final String DATE_FORMAT_TIMEONLY = "timeonly";
    private static final Logger logger = Logger.getLogger(CustomFieldUtilities.class);


    /**
     * Returns the string representation of a field type
     *
     * @param code   type code to translate
     * @param locale the locale to translate the type into
     * @return a string representation of the field type translated to the default locale
     */
    public static String getTypeString(int code, Locale locale) {
        return getTypeString(CustomField.Type.valueOf(code), locale);
    }

    /**
     * Returns the string representation of a field type.
     *
     * @param type   the type to translate
     * @param locale the locale to translate the type into
     * @return a string representation of the field type translated to the default locale
     */
    public static String getTypeString(CustomField.Type type, Locale locale) {
        if (type == CustomField.Type.STRING) {
            return ITrackerResources.getString(ITrackerResources.KEY_BASE_CUSTOMFIELD_TYPE + "string", locale);
        } else if (type == CustomField.Type.INTEGER) {
            return ITrackerResources.getString(ITrackerResources.KEY_BASE_CUSTOMFIELD_TYPE + "integer", locale);
        } else if (type == CustomField.Type.DATE) {
            return ITrackerResources.getString(ITrackerResources.KEY_BASE_CUSTOMFIELD_TYPE + "date", locale);
        } else if (type == CustomField.Type.LIST) {
            return ITrackerResources.getString(ITrackerResources.KEY_BASE_CUSTOMFIELD_TYPE + "list", locale);
        }

        return ITrackerResources.getString(ITrackerResources.KEY_BASE_CUSTOMFIELD_TYPE + "unknown", locale);
    }

    /**
     * Returns the label key for a particular custom field.  This is made up of
     * a static part and the unique value of the custom field.
     *
     * @param fieldId the CustomField id to return the label key for
     * @return the label key for the field
     */
    public static String getCustomFieldLabelKey(Integer fieldId) {
        return ITrackerResources.KEY_BASE_CUSTOMFIELD + fieldId + ITrackerResources.KEY_BASE_CUSTOMFIELD_LABEL;
    }

    /**
     * Returns the label key for a particular custom field option.  This is made up of
     * a static part and the unique value of the custom field option.
     *
     * @param fieldId  the CustomField id to return the label key for
     * @param optionId the CustomField option's id to return the label key for
     * @return the label key for the field option
     */
    public static String getCustomFieldOptionLabelKey(Integer fieldId, Integer optionId) {
        return ITrackerResources.KEY_BASE_CUSTOMFIELD + fieldId + ITrackerResources.KEY_BASE_CUSTOMFIELD_OPTION + optionId + ITrackerResources.KEY_BASE_CUSTOMFIELD_LABEL;
    }

    /**
     * Returns the label for a custom field in the default locale.
     *
     * @param fieldId the id of the field to return the label for
     * @return the label for the field translated to the default locale
     */
    public static String getCustomFieldName(Integer fieldId) {
        return getCustomFieldName(fieldId, ITrackerResources.getLocale());
    }

    /**
     * Returns the label for a custom field in the specified locale.
     *
     * @param fieldId the id of the field to return the label for
     * @param locale  the locale to return the label for
     * @return the label for the field translated to the specified locale
     */
    public static String getCustomFieldName(Integer fieldId, Locale locale) {
        return ITrackerResources.getString(CustomFieldUtilities.getCustomFieldLabelKey(fieldId), locale);
    }

    /**
     * Returns the label for a custom field option in the specified locale.
     *
     * @param fieldId  the id of the field to return the label for
     * @param optionId the id of the field option to return the label for
     * @param locale   the locale to return the label for
     * @return the label for the field option translated to the default locale
     */
    public static String getCustomFieldOptionName(Integer fieldId, Integer optionId, Locale locale) {
        if (fieldId != null && optionId != null) {
            return ITrackerResources.getString(CustomFieldUtilities.getCustomFieldOptionLabelKey(fieldId, optionId), locale);
        }
        return "";
    }

    public static String getCustomFieldOptionName(CustomFieldValue option, Locale locale) {
        if (null == option) {
            return null;
        }
        return getCustomFieldOptionName(option.getCustomField().getId(), option.getId(), locale);
    }

    public static CustomFieldValue getCustomFieldOptionByValue(List<CustomFieldValue> fields, String value) {

        if (null != fields && !fields.isEmpty()) {
            for (CustomFieldValue fieldValue : fields) {
                if (fieldValue.getValue().equalsIgnoreCase(value)) {
                    return fieldValue;
                }
            }
            return fields.get(0);
        }
        return null;
    }


    public static String getCustomFieldOptionName(CustomField field,
                                                        String value, Locale locale) {
        if (null == field) {
            return null;
        }

        if (field.getFieldType() != CustomField.Type.LIST) {
            return value;
        }
        try {
            return CustomFieldUtilities.getCustomFieldOptionName(field.getId(),
                    CustomFieldUtilities.getCustomFieldOptionByValue(
                            field.getOptions(),
                            value).getId(),
                    locale);
        } catch (Exception e) {
            logger.warn("doEndTag: failed to get custom field option name for value " + value + ", " + field.getOptions());
        }
        return value;
    }

    public static final class CustomFieldByNameComparator implements Comparator<CustomField>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final Locale locale;

        public CustomFieldByNameComparator(Locale locale) {
            this.locale = locale;
        }

        public int compare(CustomField o1, CustomField o2) {
            return new CompareToBuilder().append(
                    getCustomFieldName(o1.getId(), locale),
                    getCustomFieldName(o2.getId(), locale))
                    .append(o1.getId(), o2.getId())
                    .toComparison();
        }
    }

    /**
     * Compares 2 CustomFieldValues by name.
     * <p/>
     * <p>
     * If 2 instances have the same name, they are ordered by sortOrder.
     * </p>
     * <p/>
     * <p>
     * It doesn't take into account the custom field. <br>
     * It should therefore only be used to compare options that belong to a
     * single custom field.
     * </p>
     */
    public static final class CustomFieldValueByNameComparator implements
            Comparator<CustomFieldValue>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private final Locale locale;

        public CustomFieldValueByNameComparator(Locale locale) {
            this.locale = locale;
        }

        public int compare(CustomFieldValue a, CustomFieldValue b) {
            return new CompareToBuilder()
                    .append(
                            CustomFieldUtilities.getCustomFieldOptionName(a, this.locale),
                            CustomFieldUtilities.getCustomFieldOptionName(b, this.locale))
                    .append(a.getSortOrder(), b.getSortOrder())
                    .append(a.getId(), b.getId()).toComparison();
        }

    }
}
