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
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class RemoveCustomFieldAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(RemoveCustomFieldAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

            Integer valueId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
            if (valueId == null || valueId.intValue() <= 0) {
                throw new SystemConfigurationException("Invalid custom field id.");
            }

            CustomField customField = configurationService.getCustomField(valueId);
            if (customField == null) {
                throw new SystemConfigurationException("Invalid custom field id.");
            }
            String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
            boolean status = configurationService.removeCustomField(customField.getId());
            if (status) {
                if (key != null)
                    ITrackerResources.clearKeyFromBundles(key, false);
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            }

            configurationService.resetConfigurationCache(Configuration.Type.customfield);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.getInputForward();
            }
            return mapping.findForward("listconfiguration");
        } catch (SystemConfigurationException sce) {
            log.debug(sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfield"));
        } catch (NumberFormatException nfe) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidcustomfield"));
            log.debug("Invalid custom field  id " + request.getParameter("id") + " specified.");
        } catch (Exception e) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
            log.error("System Error.", e);
        }
        if (!errors.isEmpty()) {
            saveMessages(request, errors);
        }
        return mapping.findForward("error");
    }

}
