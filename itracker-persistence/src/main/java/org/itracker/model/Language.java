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

import java.util.Comparator;

/**
 * Models a language entry.
 *
 * @author ready
 */
public class Language extends AbstractEntity {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String locale;

    private String resourceKey;

    private String resourceValue;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public Language() {
    }

    public Language(String locale, String key) {
        setLocale(locale);
        setResourceKey(key);
    }

    /**
     * Convenience constructor to set the value too.
     */
    public Language(String locale, String key, String value) {
        this(locale, key);
        setResourceValue(value);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        if (locale == null) {
            throw new IllegalArgumentException("null locale");
        }
        this.locale = locale;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        if (resourceKey == null) {
            throw new IllegalArgumentException("null resourceKey");
        }
        this.resourceKey = resourceKey;
    }

    public String getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(String resourceValue) {
        this.resourceValue = resourceValue;
    }

    // @Override
    // public boolean equals(Object obj) {
    // if (this == obj) {
    // return true;
    // }
    //
    // if (obj instanceof Language) {
    // final Language other = (Language)obj;
    //
    // return this.resourceKey.equals(other.resourceKey)
    // && this.locale.equals(other.locale);
    // }
    // return false;
    // }
    //
    // @Override
    // public int hashCode() {
    // return this.resourceKey.hashCode() + this.locale.hashCode();
    // }
    //
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("resourceKey",
                getResourceKey()).append("locale", getLocale()).append("value", getResourceValue()).toString();
    }

    public static final Comparator<Language> KEY_COMPARATOR = new LanguageKeyComparator();

    private static class LanguageKeyComparator implements Comparator<Language> {

        public int compare(Language o1, Language o2) {
            return new CompareToBuilder().append(o1.getResourceKey(), o2.getResourceKey()).append(o1.getLocale(), o2.getLocale()).append(o1.getId(), o2.getId()).toComparison();
        }

    }

    public static final Comparator<Language> VALUE_COMPARATOR = new LanguageValueComparator();

    private static class LanguageValueComparator implements Comparator<Language> {

        public int compare(Language o1, Language o2) {
            return new CompareToBuilder()
                    .append(o1.getResourceValue(), o2.getResourceValue())
                    .append(o1.getResourceKey(), o2.getResourceKey())
                    .append(o1.getId(), o2.getId()).toComparison();
        }

    }

}