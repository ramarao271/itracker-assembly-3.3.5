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

package org.itracker.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.itracker.IssueException;
import org.itracker.model.util.CustomFieldUtilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A custom field that can be added to an Issue.
 * <p/>
 * <p>
 * Allows the user to dynamically extend the set of attributes/properties of the
 * Issue class.
 * </p>
 * <p/>
 * <p>
 * A CustomField must be configured to be used in a Project in order to extend
 * the attributes/properties of all Issues created for that project. A
 * CustomField may be used in more than 1 project. (Project - CustomField is a
 * M-N relathionship).
 * </p>
 * <p/>
 * <p>
 * A CustomField has a type, which indicates the data type of its value. <br>
 * The special type <code>LIST</code>, allows to associate a list of string
 * options to a CustomField, which are the enumeration of possible values for
 * that field. <br>
 * Each option value is represented by a CustomFieldValue instance. There's a
 * 1-N relationship between CustomField - CustomFieldValue. A CustomFieldValue
 * can only belong to 1 CustomField (composition).
 * </p>
 * <p/>
 * <p>
 * A value of a CustomField for a given Issue is represented by an IssueField
 * instance. (CustomField - IssueField is a 1-N relationship).
 * </p>
 *
 * @author ready
 * @see CustomFieldValue
 * @see IssueField
 */
public class CustomField extends AbstractEntity implements Comparable<Entity> {

    private static final Logger logger = Logger.getLogger(CustomField.class);

    /**
     * Dateformat able to parse datepicker generated date string (dd/MM/yyyy)
     */
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Field value data type.
     */
    private Type type;

    /**
     * Display format to use if <code>fieldType</code> is a Date.
     * <p/>
     * TODO: use type-safe enum CustomField.DateFormat
     */
    private String dateFormat;

    /**
     * Whether this field is mandatory or optional. PENDING: this should be
     * specified when the field is used in a project!
     */
    private boolean required;

    /**
     * List of options for a field of type <code>LIST</code>.
     * <p/>
     * <p>
     * This is the enumeration of possible values for the field.
     * </p>
     * <p/>
     * Note: this field used to be named <code>values</code> is iTracker 2.
     * <p/>
     * <p>
     * PENDING: There's no way to use this as a list of proposed values,
     * allowing the user to enter a value that's not in this list.
     * </p>
     */
    private List<CustomFieldValue> options = new ArrayList<CustomFieldValue>();

    /**
     * Whether the options of a field of type List should be sorted by their
     * name rather than by {@link CustomFieldValue#getSortOrder() }.
     */
    private boolean sortOptionsByName;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public CustomField() {
    }


    public Type getFieldType() {
        return type;
    }

    public void setFieldType(Type type) {
        this.type = type;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<CustomFieldValue> getOptions() {
        return options;
    }

    public void setOptions(List<CustomFieldValue> options) {
        this.options = options;
    }

    /**
     * Adds a new option value/name to the custom field.
     * <p/>
     * <p>
     * New options are put at the end of the list even if they should be sorted.
     * <br>
     * This method is mainly used to build a new custom field so it can be saved
     * later.
     * </p>
     *
     * @param value the option value
     * @param label the label/name for the new option
     * @deprecated this can not be in the entity, replace by Utility or service.
     */
    public void addOption(String value, String label) {
        this.options.add(new CustomFieldValue(this, value));
    }


    public boolean isSortOptionsByName() {
        return sortOptionsByName;
    }

    public void setSortOptionsByName(boolean sortOptionsByName) {
        this.sortOptionsByName = sortOptionsByName;
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this)
                .append("id", getId())
                .append("type", getFieldType())
                .append("sortOptionsByName", isSortOptionsByName()).toString();
    }

    /**
     * Checks if the given value is assignable to this custom field.
     *
     * @param value custom field data
     * @throws org.itracker.IssueException if it isn't
     * @see IssueField#setValue(String, Locale, ResourceBundle)
     */
    public void checkAssignable(String value, Locale locale,
                                ResourceBundle bundle) throws IssueException {


        if (this.isRequired() && (value == null || value.trim().length() == 0)) {
            throw new IssueException("Value is required.", IssueException.TYPE_CF_REQ_FIELD);
        }

        switch (this.type) {

            case INTEGER:
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException nfe) {
                    throw new IssueException("Invalid integer.",
                            IssueException.TYPE_CF_PARSE_NUM);
                }
                break;

            case DATE:
                if (!CustomFieldUtilities.DATE_FORMAT_UNKNOWN.equals(this.dateFormat)) {
                    SimpleDateFormat format =
                            // DEFAULT_DATE_FORMAT;
                            new SimpleDateFormat(bundle
                                    .getString("itracker.dateformat." + this.dateFormat),
                                    locale);

                    try {
                        format.parse(value);
                    } catch (ParseException ex) {
                        throw new IssueException("Invalid date format.",
                                IssueException.TYPE_CF_PARSE_DATE);
                    }
                }
                break;

            case LIST:
                for (CustomFieldValue customFieldValue : getOptions()) {
                    if (customFieldValue.getValue().equalsIgnoreCase(value)) {
                        return;
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("checkAssignable: could not assign value to custom field values: " + value + ", " + getOptions());
                }
                throw new IssueException("Invalid value.", IssueException.TYPE_CF_INVALID_LIST_OPTION);
            default:
                // Value is OK
        }
    }

    /**
     * Enumeration of possible data types.
     */
    public static enum Type implements IntCodeEnum<Type> {

        STRING(1), INTEGER(2), DATE(3), LIST(4);

        private final Integer code;

        private Type(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public Type fromCode(Integer code) {
            return Type.valueOf(code);
        }

        public static Type valueOf(Integer code) {
            for (Type val: values()) {
                if (val.code.compareTo(code) == 0) {
                    return val;
                }
            }
            throw new IllegalArgumentException("Unknown code : " + code);
        }


    }

    /**
     * Date format for fields of type DATE.
     * <p/>
     * PENDING: consider replacing the DATE Type with these 3 new data types.
     */
    public static enum DateFormat {

        DATE_TIME("full"), DATE("dateonly"), TIME("timeonly");

        final String code;

        DateFormat(String code) {
            this.code = code;
        }

    }
}
