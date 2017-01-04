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

package org.itracker.web.actions.admin.configuration;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.SystemConfigurationException;
import org.itracker.model.Configuration;
import org.itracker.model.Configuration.Type;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OrderConfigurationItemAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(OrderConfigurationItemAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

            Integer configId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            if (configId == null || configId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }

            Configuration configItem = configurationService.getConfigurationItem(configId);
            if (configItem == null) {
                throw new SystemConfigurationException("Invalid configuration id.");
            }

            Type configType = configItem.getType();
            List<Configuration> configItems = configurationService.getConfigurationItemsByType(configType);
            List<Configuration> newConfigItems = new ArrayList<Configuration>();

            for (int i = 0; i < configItems.size(); i++) {
                newConfigItems.add(configItems.get(i));
            }
            for (int i = 0; i < configItems.size(); i++) {
                if (configItems.get(i) != null) {
                    Configuration firstConfiguration = new Configuration();
                    Configuration secondConfiguration = new Configuration();
                    Configuration curConfiguration = configItems.get(i);
                    int todo_i = -1;
                    if (curConfiguration.getId().equals(configId)) {
                        if ("up".equals(action)) {
                            todo_i = i - 1;
                        } else {
                            todo_i = i + 1;
                        }
                        Configuration todoConfiguration = configItems.get(todo_i);

                        firstConfiguration.setId(todoConfiguration.getId());
                        firstConfiguration.setCreateDate(todoConfiguration.getCreateDate());
                        firstConfiguration.setLastModifiedDate(todoConfiguration.getLastModifiedDate());
                        firstConfiguration.setName(todoConfiguration.getName());
                        firstConfiguration.setOrder(curConfiguration.getOrder());
                        firstConfiguration.setType(todoConfiguration.getType());
                        firstConfiguration.setValue(todoConfiguration.getValue());
                        firstConfiguration.setVersion(todoConfiguration.getVersion());


                        secondConfiguration.setId(curConfiguration.getId());
                        secondConfiguration.setCreateDate(curConfiguration.getCreateDate());
                        secondConfiguration.setLastModifiedDate(curConfiguration.getLastModifiedDate());
                        secondConfiguration.setName(curConfiguration.getName());
                        secondConfiguration.setOrder(todoConfiguration.getOrder());
                        secondConfiguration.setType(curConfiguration.getType());
                        secondConfiguration.setValue(curConfiguration.getValue());
                        secondConfiguration.setVersion(curConfiguration.getVersion());

                        newConfigItems.set(todo_i, firstConfiguration);
                        newConfigItems.set(i, secondConfiguration);
                    }
                }
            }

            newConfigItems = configurationService.updateConfigurationItems(newConfigItems, configType);

            // Only resolutions and severities can be reordered at this point.  Statuses
            // and some basic workflow depend on the actual value of the status, so
            // the order must equal the value of the status for it to work correctly.
            switch (configType) {
                case resolution:
                case severity:
                    configurationService.resetConfigurationCache(configType);
            }


            return mapping.findForward("listconfiguration");
        } catch (SystemConfigurationException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
            log.debug("Invalid configuration item id " + request.getParameter("id") + " specified.");
        } catch (NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidconfiguration"));
            log.debug("Invalid configuration item id " + request.getParameter("id") + " specified.");
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return mapping.findForward("error");
    }

}
