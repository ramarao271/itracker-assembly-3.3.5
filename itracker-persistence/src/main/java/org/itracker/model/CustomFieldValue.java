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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Comparator;

/**
 * An option for the value of a CustomField of type <code>LIST</code>.
 *
 * @author ready
 * @author johnny
 */
public class CustomFieldValue extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final Comparator<CustomFieldValue> SORT_ORDER_COMPARATOR = new SortOrderComparator();

    /**
     * The custom field to which this option belongs.
     */
    private CustomField customField;


    /**
     * This option's value.
     */
    private String value;

    /**
     * This option's order among all available options for the
     * <code>customField</code>.
     */
    private int sortOrder;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public CustomFieldValue() {
    }

    public CustomFieldValue(CustomField customField, String value) {
        setCustomField(customField);
        setValue(value);
    }

    public CustomField getCustomField() {
        return (customField);
    }

    public void setCustomField(CustomField customField) {
        if (customField == null) {
            throw new IllegalArgumentException("null customField");
        }
        this.customField = customField;
    }


    public String getValue() {
        return value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        this.value = value;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Returns a string with this instance's id and natural key.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append(
                "customField", getCustomField()).append("value", getValue())
                .toString();
    }

    /**
     * Compares 2 CustomFieldValues by custom field and sort order.
     * <p/>
     * <p>
     * Note that it doesn't match the class' natural ordering because it doesn't
     * take into account the custom field. <br>
     * It should therefore only be used to compare options that belong to a
     * single custom field.
     * </p>
     */
    private static class SortOrderComparator implements
            Comparator<CustomFieldValue>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(CustomFieldValue a, CustomFieldValue b) {
            return new CompareToBuilder().append(a.getSortOrder(),
                    b.getSortOrder())
                    .toComparison();
        }

    }


}
