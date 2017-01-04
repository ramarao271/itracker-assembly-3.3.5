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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class provides basic storage for name values pairs. The name is usually a key
 * of some type, like a status number, and the value is a localized name for
 * that key.
 */
public class NameValuePair extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name = "";

    private String value = "";

    private static final class NameComparator implements Comparator<NameValuePair>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(NameValuePair o1, NameValuePair o2) {

            return o1.name.compareTo(o2.name);
        }

        ;
    }

    private static final class ValueComparator implements Comparator<NameValuePair>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(NameValuePair o1, NameValuePair o2) {
            return o1.value.compareTo(o2.value);
        }

        ;
    }

    public static final Comparator<NameValuePair> KEY_COMPARATOR = new NameComparator();
    public static final Comparator<NameValuePair> VALUE_COMPARATOR = new ValueComparator();

    public NameValuePair(String name, String value) {
        setName(name);
        setValue(value);
    }

    /**
     * Returns the name of the name/value pair.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the name/value pair.
     */
    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    /**
     * Returns the value of the name/value pair.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the name/value pair.
     */
    public void setValue(String value) {
        this.value = value;
    }

    public int compareKeyTo(NameValuePair other) {
        return KEY_COMPARATOR.compare(this, other);
    }

    public int compareValueTo(NameValuePair other) {
        return VALUE_COMPARATOR.compare(this, other);
    }


//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//
//		if (obj instanceof NameValuePair) {
//			final NameValuePair other = (NameValuePair) obj;
//			return new EqualsBuilder().append(name, other.name).append(value,
//					other.value).isEquals();
//
//		}
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder().append(this.name).append(this.value)
//				.toHashCode();
//	}

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("value",
                value).toString();
    }

}
