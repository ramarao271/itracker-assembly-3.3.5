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

package org.itracker.services.authentication;

import org.itracker.services.ConfigurationService;
import org.itracker.services.UserService;
import org.itracker.services.exceptions.AuthenticatorException;
import org.itracker.core.AuthenticationConstants;

import java.util.Map;

// TODO: Rewrite Javadocs here: we don't have session beans or EJBs anymore

/**
 * This class provides a skeleton implementation of the PluggableAuthenticator interface.
 * It can be extended to provide a new authentication module for ITracker reducing the amount
 * of effort to implement the PluggableAuthenticator interface.
 */
public abstract class AbstractPluggableAuthenticator
        implements PluggableAuthenticator, AuthenticationConstants {

    //    private final Logger logger;
    private UserService userService = null;
    private ConfigurationService configurationService = null;


    /**
     * This method is called after creating a new instance of the Authenticator.  It supplies
     * some default EJB objects that the authenticator can use.
     */
    public void initialize(Map<?, ?> values) {
        if (values != null) {
            Object userService = values.get("userService");
            Object configurationService = values.get("configurationService");

            if (userService instanceof UserService) {
                this.userService = (UserService) userService;
            }
            if (configurationService instanceof ConfigurationService) {
                this.configurationService = (ConfigurationService) configurationService;
            }
        }
    }

    /**
     * Returns a UserService session bean that can be used to call needed methods such
     * as retrieving a user.
     *
     * @return userService
     * @throws AuthenticatorException an exception if an error occur
     */
    public UserService getUserService() throws AuthenticatorException {
        if (userService == null || !(userService instanceof UserService)) {
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }

        return userService;
    }

    /**
     * Returns an ConfigurationService session bean that can be used to retreive properties
     * that have been set in the system.  These properties can be used to provide any
     * needed configuration for the authenticator.
     *
     * @return configurationService
     * @throws AuthenticatorException an exception if an error occur
     */
    public ConfigurationService getConfigurationService() throws AuthenticatorException {
        if (configurationService == null || !(configurationService instanceof ConfigurationService)) {
            throw new AuthenticatorException(AuthenticatorException.SYSTEM_ERROR);
        }

        return configurationService;
    }

}
