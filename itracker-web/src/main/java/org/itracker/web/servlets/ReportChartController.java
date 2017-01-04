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

import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


public class ReportChartController extends GenericController {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ReportChartController() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        Map<?, ?> imagesMap = (Map<?, ?>) session.getAttribute(Constants.REPORT_IMAGEMAP_KEY);

        if (imagesMap != null) {
            String imageName = request.getParameter("image");
            if (imageName != null) {
                byte[] imageData = (byte[]) imagesMap.get(imageName);

                response.setContentLength(imageData.length);
                ServletOutputStream ouputStream = response.getOutputStream();
                ouputStream.write(imageData, 0, imageData.length);
                ouputStream.flush();
                ouputStream.close();
            }
        }
    }
}
