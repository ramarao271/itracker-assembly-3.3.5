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

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileHandler {
    private Properties props;
    private final Logger logger;

    public PropertiesFileHandler() {
        this.logger = Logger.getLogger(getClass());
        props = new Properties();
    }

    public PropertiesFileHandler(String resource) {
        this();
        addProperties(resource);
    }

    public void addProperties(String resource) {
        if (resource == null || resource.equals("") || !resource.endsWith(".properties")) {
            if (logger.isInfoEnabled()) {
                logger.info("addProperties: skip " + resource);
            }
            return;
        }

        try {
            InputStream is = getClass().getResourceAsStream(resource);
            if (is != null) {
                props.load(is);
            } else {
                logger.debug("No properties resource, " + resource + " was found.");
            }
        } catch (IOException ioe) {
            logger.warn("Could not load properties resource: " + resource, ioe);
        }
    }

    public Properties getProperties() {
        return (Properties) props.clone();
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public boolean hasProperty(String name) {
        return props.containsKey(name);
    }

    public boolean hasProperties() {
        return (props.size() > 0 ? true : false);
    }
}