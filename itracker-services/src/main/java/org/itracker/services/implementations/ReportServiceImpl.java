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

package org.itracker.services.implementations;

import org.itracker.model.Report;
import org.itracker.persistence.dao.ReportDAO;
import org.itracker.services.ReportService;

import java.util.List;

public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;

    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    // kill this
    public List<Report> getAllReports() {
        return reportDAO.findAll();
    }

    // kill this
    public int getNumberReports() {
        return reportDAO.findAll().size();
    }

    // kill this
    public Report createReport(Report report) {
//		report.setCreateDate(new Date());
//		report.setLastModifiedDate(report.getCreateDate());
        reportDAO.save(report);
        return report;
    }



    public ReportDAO getReportDAO() {
        return reportDAO;
    }


}
