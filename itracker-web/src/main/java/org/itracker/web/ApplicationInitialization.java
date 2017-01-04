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

package org.itracker.web;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.User;
import org.itracker.UserException;
import org.itracker.persistence.dao.NoSuchEntityException;
import org.itracker.services.ConfigurationService;
import org.itracker.services.ReportService;
import org.itracker.services.UserService;
import org.itracker.PasswordException;
import org.itracker.model.util.UserUtilities;
import org.springframework.web.context.ServletConfigAware;

import javax.servlet.ServletConfig;
import java.util.StringTokenizer;


/**
 * This gets started when the Spring application-context starts up...
 *
 */

public class ApplicationInitialization implements ServletConfigAware {

    private final Logger logger;
    private UserService userService;
    private ConfigurationService configurationService;
    private ServletConfig servletConfig;

    public ApplicationInitialization(UserService userService, ConfigurationService configurationService, ReportService reportService) {
        this.userService = userService;
        this.configurationService = configurationService;
        this.logger = Logger.getLogger(getClass());
    }

    public void init() {
        try {
            ITrackerResources.setConfigurationService(configurationService);

            logger.info("Checking and initializing languages in the database.");

            configurationService.initializeAllLanguages(false);

            logger.info("Checking and initializing default system configuration in the database.");
            configurationService.initializeConfiguration();

            logger.info("Setting up cached configuration entries");
            configurationService.resetConfigurationCache();

            // check for and create admin user, if so configured
            createAdminUser(configurationService);

            setUpPdfFonts();
        } catch (PasswordException pe) {
            logger.info("Unable to create admin user.  Error: " + pe.getMessage());
        } catch (UserException ue) {
            logger.warn("Exception while creating admin user.", ue);
        }
    }

    private void setUpPdfFonts() {

        StringTokenizer st = new StringTokenizer(configurationService.getProperty("pdf.export.fonts", "arial"), ",");
        String font;
        while (st.hasMoreTokens()) {
            font = StringUtils.trim(st.nextToken());
            DefaultJasperReportsContext.getInstance().setProperty("net.sf.jasperreports.export.pdf.font." + font,
                    this.getClass().getResource("/fonts/" + font + ".ttf").toString());

        }
    }



    /**
     * Check if we should create the admin user, if so, do it.
     */
    private void createAdminUser(ConfigurationService configurationService) throws PasswordException, UserException {
        boolean createAdmin = configurationService.getBooleanProperty("create_super_user", false);
        if (createAdmin) {
            logger.info("Create default admin user option set to true.  Checking for existing admin user.");
            try {
                userService.getUserByLogin("admin");
            } catch (NoSuchEntityException e) {
                logger.debug("Attempting to create admin user.");
                User adminUser = new User("admin", UserUtilities.encryptPassword("admin"), "Super", "User",
                        "", true);
                userService.createUser(adminUser);
            }
        }
    }

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig =  servletConfig;
    }
}
