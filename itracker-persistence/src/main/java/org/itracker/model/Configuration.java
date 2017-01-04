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
import org.itracker.model.util.IssueUtilities;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A configuration item.
 *
 * @author ready
 */
public class Configuration extends AbstractEntity implements Comparable<Entity> {

    public static enum Type implements IntCodeEnum<Type> {
        initialized(-1, 0),
        locale(1, 0),
        status(2, IssueUtilities.FIELD_STATUS),
        severity(3, IssueUtilities.FIELD_SEVERITY),
        resolution(4, IssueUtilities.FIELD_RESOLUTION),
        customfield(5, 0);


        private final Integer code;
        private final Integer legacyCode;

        private Type(Integer code, Integer legacyCode) {
            this.code = code;
            this.legacyCode = legacyCode;
        }

        public Integer getCode() {
            return code;
        }

        public Type fromCode(Integer code) {
            return Type.valueOf(code);
        }

        public Integer getLegacyCode() {
            return legacyCode;
        }

        public static Type valueOf(Integer code) {
            for (Type val: values()) {
                if (val.code.compareTo(code) == 0) {
                    return val;
                }
            }
            throw new IllegalArgumentException("Unknown code : " + code);
        }
        /**
         * Returns the key for a particular configuration item. This is made up of a
         * static part based on the type of configuration item, and the unique value
         * of the configuration item.
         *
         * @param configuration the Configuration to return the key for
         * @return the key for the item
         */
        public String getLanguageKey(final Configuration configuration) {
            if (configuration != null) {
                final Type type = configuration.getType();
                String key = "itracker." + type.name() + ".";

                if (type == Type.locale) {
                    key += "name.";
                }

                key += configuration.getValue();

                if (type == Type.customfield) {
                    key += ".label";
                }
                return key;
            }
            return "";
        }
        public String getTypeLanguageKey() {
            final String base = "itracker.web.attr.";

            return base + name();
        }



    }

    public static final ConfigurationOrderComparator CONFIGURATION_ORDER_COMPARATOR = new ConfigurationOrderComparator();
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * PENDING: this field doesn't exist in the database!?
     * <p/>
     * <p>
     * TODO : every configuration item should have a name, similar to a Java
     * property in a properties file. A description would be nice to have too.
     * name + version should be the natural key. (note: we shouldn't allow 2
     * configuration items with the same name and version, but with different
     * types).
     * </p>
     * <p/>
     * <p>
     * But since <code>name</code> is nullable, only the type and value can be
     * used as natural key at the moment. This should be a temporary situation,
     * because the value is allowed to change.
     * </p>
     */
    private String name;

    /**
     * ITracker version in which this configuration item was added.
     */
    private String version;

    /**
     * The real type of the value stored as a string.
     */
    private Type type;

    /**
     * The configuration value as a string.
     */
    private String value;

    /**
     * Display order.
     * <p/>
     * <p>
     * Several instances may have the same display order.
     * </p>
     */
    private int order;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public Configuration() {
    }

    public Configuration(Type type, String value) {
        setType(type);
        setValue(value);
    }

    public Configuration(Type type, NameValuePair pair) {
        this(type, pair.getValue());
        setName(pair.getName());
    }

    public Configuration(Type type, String value, String version) {
        this(type, value);
        setVersion(version);
    }

    public Configuration(Type type, String value, int order) {
        this(type, value);
        setOrder(order);
    }

    public Configuration(Type type, String value, String version, int order) {
        this(type, value, version);
        setOrder(order);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version == null) {
            throw new IllegalArgumentException("null version");
        }
        this.version = version;
    }

    /**
     * String composed of system ID and natural key (name and version).
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("type", getType())
                .append("name", getName()).append("version", getVersion()).append(
                        "value", getValue()).toString();

    }

    public static final class ConfigurationOrderComparator implements
            Comparator<Configuration>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(Configuration o1, Configuration o2) {
            return new CompareToBuilder().append(o1.getOrder(), o2.getOrder()).append(o1.getValue(), o2.getValue())
                    .toComparison();
        }


    }

}
