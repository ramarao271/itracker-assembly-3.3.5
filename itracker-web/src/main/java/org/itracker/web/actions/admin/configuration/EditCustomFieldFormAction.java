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
import org.itracker.model.CustomField;
import org.itracker.model.Language;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//  TODO: Action Cleanup

public class EditCustomFieldFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditCustomFieldFormAction.class);

    /* (non-Javadoc)
      * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request, response)) {
            return mapping.findForward("unauthorized");
        }

        try {

            HttpSession session = request.getSession(true);

            CustomFieldForm customFieldForm = (CustomFieldForm) form;
            if (customFieldForm == null)
                customFieldForm = new CustomFieldForm();

            String action = (String) PropertyUtils.getSimpleProperty(customFieldForm, "action");
            CustomField customField = null;
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();
            if ("create".equals(action)) {
                customField = new CustomField();
            } else if ("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(customFieldForm, "id");
                customField = configurationService.getCustomField(id);
                if (customField == null)
                    throw new SystemConfigurationException("Invalid custom field id " + id);

                customFieldForm.setId(id);
                customFieldForm.setFieldType(customField.getFieldType().getCode());
                customFieldForm.setRequired(Boolean.toString(customField.isRequired()));
                customFieldForm.setDateFormat(customField.getDateFormat());
                customFieldForm.setSortOptionsByName(Boolean.toString(customField.isSortOptionsByName()));

                Map<String, String> translations = new TreeMap<>();
                List<Language> languageItems = configurationService.getLanguageItemsByKey(CustomFieldUtilities.getCustomFieldLabelKey(customField.getId()));
               for (Language languageItem : languageItems) {
                  translations.put(languageItem.getLocale(), languageItem.getResourceValue());
               }
                customFieldForm.setTranslations(translations);
            }

            /*
                * Check whether customField is null or not, if null redirect the
                * page to error, otherwise put required objects in request
                * object to render output
                */
            if (customField == null) {
                log.error("EditCustomFieldFormAction#execute: customField was null!");
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidproject"));
                return mapping.findForward("error");
            }
            session.setAttribute(Constants.CUSTOMFIELD_KEY, customField);
            customFieldForm.setRequestEnv(request);
            saveToken(request);

        } catch (SystemConfigurationException sce) {
            log.error("execute: Exception system configuration exception caught.", sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidcustomfield"));
        } catch (Exception e) {
            log.error("execute: Exception while creating edit custom field form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("error");
        }

        return mapping.getInputForward();
    }


}
