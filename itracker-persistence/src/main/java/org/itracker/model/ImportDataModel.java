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

public class ImportDataModel extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ImportDataModel.class);
    private AbstractEntity[] dataModels;
    private AbstractEntity[] existingModel;

    private boolean reuseConfig = true;
    private boolean reuseFields = true;
    private boolean reuseProjects = true;
    private boolean reuseUsers = true;
    private boolean createPasswords = true;

    private int[][] verifyStatistics = new int[7][2];

    public ImportDataModel() {
    }

    public AbstractEntity[] getData() {
        return (dataModels == null ? new AbstractEntity[0] : dataModels.clone());
    }

    public AbstractEntity[] getExistingModel() {

        return (existingModel == null ? new AbstractEntity[0] : existingModel.clone());

    }

    public AbstractEntity getExistingModel(int i) {
        return (existingModel != null && i < existingModel.length ? existingModel[i]
                : null);
    }

    public void setExistingModel(int i, AbstractEntity model) {
        if (existingModel != null && i < existingModel.length) {
            existingModel[i] = model;
        }
    }

    public void setData(AbstractEntity[] dataModels, AbstractEntity[] existingModel) {
        if (dataModels != null && existingModel != null
                && dataModels.length == existingModel.length) {
            this.dataModels = dataModels.clone();
            this.existingModel = existingModel.clone();
            this.verifyStatistics = new int[7][2];
        } else {
            throw new IllegalArgumentException("Data model must not be null and existing model must not be null nor empty.");
        }
    }

    public boolean getReuseConfig() {
        return reuseConfig;
    }

    public void setReuseConfig(Boolean value) {
        reuseConfig = (value != null ? value.booleanValue() : true);
    }

    public boolean getReuseFields() {
        return reuseFields;
    }

    public boolean getReuseProjects() {
        return reuseProjects;
    }

    public void setReuseProjects(Boolean value) {
        reuseProjects = (value != null ? value.booleanValue() : true);
    }

    public boolean getReuseUsers() {
        return reuseUsers;
    }


    public void setReuseUsers(Boolean value) {
        reuseUsers = (value != null ? value.booleanValue() : true);
    }

    public boolean getCreatePasswords() {
        return createPasswords;
    }

    public void setCreatePasswords(Boolean value) {
        createPasswords = (value != null ? value.booleanValue() : true);
    }

    public int[][] getImportStatistics() {
        return verifyStatistics;
    }

    public void addVerifyStatistic(int itemType, int category) {
        try {
            verifyStatistics[itemType][category]++;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public String statsToString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < verifyStatistics.length; i++) {
            buf.append(i + ":[" + verifyStatistics[i][0] + ", "
                    + verifyStatistics[i][1] + "] ");
        }
        return buf.toString();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append(
                "dataModels.length", getData().length).append("reuseUsers",
                getReuseUsers()).append("reuseProjects", getReuseProjects()).append(
                "reuseFields", getReuseFields()).append("reuseConfig", getReuseConfig())
                .append("createPasswords", getCreatePasswords()).toString();
    }
}
