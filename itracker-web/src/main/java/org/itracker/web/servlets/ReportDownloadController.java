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

package org.itracker.web.servlets;

import org.apache.log4j.Logger;
import org.itracker.model.Report;
import org.itracker.services.ReportService;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ReportDownloadController extends GenericController {

    private static final Logger logger = Logger.getLogger(ReportDownloadController.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ReportDownloadController() {
    }

    public void init(ServletConfig config) {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();

            Integer reportId;
            Report report = null;

            try {
                reportId = new Integer((request.getParameter("id") == null ? "-1" : (request.getParameter("id"))));
                report = reportService.getReportDAO().findByPrimaryKey(reportId);
            } catch (NumberFormatException nfe) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid reportId " + request.getParameter("id") + " specified.");
                }
            }

            if (report == null) {
                forward("/error.do", request, response);
                return;
            }

            response.setHeader("Content-Disposition", "attachment; filename=report" + report.getId() + "\"");
            ServletOutputStream out = response.getOutputStream();
            out.write(report.getFileData());
            out.close();
        } catch (IOException ioe) {
            logger.info("Unable to display report.", ioe);
        }
    }
}   