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
import org.itracker.ITrackerDirtyResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class ITrackerResourceBundle extends ResourceBundle {

    private static final Logger log = LoggerFactory
            .getLogger(ITrackerResourceBundle.class);
    private final Properties data = new Properties();

    private ResourceBundle propertiesBundle;


    static ResourceBundle loadBundle() {
        return new ITrackerResourceBundle();
    }

    static ResourceBundle loadBundle(Locale locale) {
        return new ITrackerResourceBundle(locale);
    }


    static ResourceBundle loadBundle(Locale locale, Properties items) {
        return new ITrackerResourceBundle(locale, items);
    }

    private ITrackerResourceBundle() {
        super.setParent(ResourceBundle.getBundle(
                ITrackerResources.RESOURCE_BUNDLE_NAME, new Locale(
                ITrackerResources.getDefaultLocale())));
    }

    /**
     * @param locale
     */
    private ITrackerResourceBundle(Locale locale) {
        if (null == locale) {
            locale = ITrackerResources.getLocale(ITrackerResources
                    .getDefaultLocale());
        }
        this.propertiesBundle = ResourceBundle.getBundle(
                ITrackerResources.RESOURCE_BUNDLE_NAME, locale,
                Control.getNoFallbackControl(Control.FORMAT_DEFAULT));

        if (!locale.equals(ITrackerResources
                .getLocale(ITrackerResources.BASE_LOCALE))) {
            if (locale.getCountry().length() > 0) {
                setParent(ITrackerResources.getBundle(new Locale(locale
                        .getLanguage())));
            } else if (locale.getLanguage().length() > 0) {
                setParent(ITrackerResources.getBundle(ITrackerResources
                        .getLocale(ITrackerResources.BASE_LOCALE)));
            }
        }

    }

    public ITrackerResourceBundle(Locale locale, Properties items) {
        this(locale);
        if (null != items) {
            this.data.putAll(items);
        }
    }

    public static ResourceBundle getBundle() {
        return ITrackerResources.getBundle();
    }

    public static ResourceBundle getBundle(Locale locale) {
        return ITrackerResources.getBundle(locale);
    }

    /**
     * @deprecated used still for testing
     */
    public ITrackerResourceBundle(Locale locale, Object[][] data) {
        this(locale);
        setContents(data);
    }

    /**
     * @param content
     */
    @Deprecated
    private void setContents(Object[][] content) {
        if (content != null && content.length == 2
                && content[0].length == content[1].length) {
            synchronized (data) {
                data.clear();
                for (int i = 0; i < content[0].length; i++) {
                    data.put(content[0][i], content[1][i]);
                }
            }
        }
    }

    @Override
    public Locale getLocale() {
        Locale l = super.getLocale();
        if (null == l && null != propertiesBundle) {
            l = propertiesBundle.getLocale();
        }
        return l;
    }

    public boolean isDirty(String key) {
        try {
            handleGetObject(key);
        } catch (ITrackerDirtyResourceException exception) {
            return true;
        }
        return false;
    }


    public void updateValue(String key, String value) {
        if (null == key) {
            throw new IllegalArgumentException("key must not be null");
        }
        if (null == value) {
            throw new IllegalArgumentException("value must not be null");
        }
        synchronized (data) {
            data.put(key, value);
        }
    }


    public void removeValue(String key, boolean markDirty) {
        if (key != null) {
            synchronized (data) {
                if (markDirty) {
                    data.put(key, new DirtyKey() {
                    });
                } else {
                    data.remove(key);
                }
            }
        }
    }

    /**
     * Implementation of ResourceBundle.handleGetObject. Returns the request key
     * from the internal data map.
     */
    public final Object handleGetObject(String key) {
        Object value = data.get(key);
        if (value instanceof DirtyKey) {
            throw new ITrackerDirtyResourceException(
                    "The requested key has been marked dirty.",
                    "ITrackerResourceBundle_" + getLocale(), key);
        }
        if (null == value) {
            try {
                value = propertiesBundle.getObject(key);
            } catch (MissingResourceException e) {
                if (log.isDebugEnabled()) {
                    log.debug("handleGetObject: " + key, e);
                }
            }
        }
        return value;
    }

    /**
     * Implementation of ResourceBundle.getKeys. Since it returns an
     * enumeration, It creates a new Set, and returns that collections
     * enumerator.
     */
    public Enumeration<String> getKeys() {
                Set set = new TreeSet(data.keySet());
        if (null != parent) {
            Enumeration<String> keys = parent.getKeys();
            String key;
            while (keys.hasMoreElements()) {
                key = keys.nextElement();
                set.add(key);
            }
        }
        if (null != propertiesBundle) {
            Enumeration<String> keys = propertiesBundle.getKeys();
            String key;
            while (keys.hasMoreElements()) {
                key = keys.nextElement();
                set.add(key);
            }
        }
        return Collections.enumeration(set);
    }

    public interface DirtyKey {
    }
}
