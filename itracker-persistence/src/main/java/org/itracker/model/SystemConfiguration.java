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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class SystemConfiguration extends AbstractEntity {


    public static final Comparator<SystemConfiguration> VERSION_COMPARATOR = new SystemConfigurationComparator();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String version;

    private List<CustomField> customFields = new ArrayList<CustomField>();

    private List<Configuration> resolutions = new ArrayList<Configuration>();
    private List<Configuration> severities = new ArrayList<Configuration>();
    private List<Configuration> statuses = new ArrayList<Configuration>();

    public SystemConfiguration() {
    }

    public String getVersion() {
        return (version == null ? "" : version);
    }

    public void setVersion(String value) {
        version = value;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomField> value) {
        if (value != null) {
            customFields = value;
        }
    }

    public List<Configuration> getResolutions() {
        return (resolutions == null ? new ArrayList<Configuration>()
                : resolutions);
    }

    public void setResolutions(List<Configuration> value) {
        if (value != null) {
            resolutions = value;
        }
    }

    public List<Configuration> getSeverities() {
        return (severities == null ? new ArrayList<Configuration>()
                : severities);
    }

    public void setSeverities(List<Configuration> value) {
        if (value != null) {
            severities = value;
        }
    }

    public List<Configuration> getStatuses() {
        return (statuses == null ? new ArrayList<Configuration>() : statuses);
    }

    public void setStatuses(List<Configuration> value) {
        if (value != null) {
            statuses = value;
        }
    }

    public void addConfiguration(Configuration configuration) {
        if (configuration != null) {
            switch (configuration.getType()) {
                case resolution:
                    resolutions.add(configuration);
                    break;
                case severity:
                    severities.add(configuration);
                    break;
                case status:
                    statuses.add(configuration);
                    break;
                default:
                    return;
            }
        }
    }

    public String toString() {

        return new ToStringBuilder(this).append("id", getId()).append("version", getVersion()).toString();

    }

    private static final class SystemConfigurationComparator implements
            Comparator<SystemConfiguration> {
        public int compare(SystemConfiguration o1, SystemConfiguration o2) {
            return new CompareToBuilder().append(o1.getVersion(),
                    o2.getVersion()).append(o1.getId(), o2.getId())
                    .toComparison();
        }
    }
}
