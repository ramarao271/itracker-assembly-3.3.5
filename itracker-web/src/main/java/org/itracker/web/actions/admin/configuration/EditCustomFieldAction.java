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
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EditCustomFieldAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditCustomFieldAction.class);


    /* (non-Javadoc)
      * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    @SuppressWarnings("unchecked")
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing configuration.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }
        resetToken(request);
        try {
            ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();
            String action = (String) PropertyUtils.getSimpleProperty(form, "action");
            if (action == null) {
                return mapping.findForward("listconfiguration");
            }
            CustomFieldForm customFieldForm = (CustomFieldForm) form;

            CustomField customField;
            if ("create".equals(action)) {
                customField = new CustomField();
                customField.setFieldType(CustomField.Type.valueOf(customFieldForm.getFieldType()));
                customField.setRequired(("true".equals(PropertyUtils.getSimpleProperty(form, "required")) ? true : false));
                customField.setSortOptionsByName(("true".equals((String) PropertyUtils.getSimpleProperty(form, "sortOptionsByName")) ? true : false));
                customField.setDateFormat((String) PropertyUtils.getSimpleProperty(form, "dateFormat"));
                customField = configurationService.createCustomField(customField);
            } else if ("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                customField = configurationService.getCustomField(id);
                if (customField == null) {
                    throw new SystemConfigurationException("Invalid custom field id " + id);
                }
                List<CustomFieldValue> customFieldValues = customField.getOptions();
                customField.setFieldType(CustomField.Type.valueOf(customFieldForm.getFieldType()));
                customField.setRequired(("true".equals((String) PropertyUtils.getSimpleProperty(form, "required")) ? true : false));
                customField.setSortOptionsByName(("true".equals((String) PropertyUtils.getSimpleProperty(form, "sortOptionsByName")) ? true : false));
                customField.setDateFormat((String) PropertyUtils.getSimpleProperty(form, "dateFormat"));
                customField.setOptions(customFieldValues);
                customField = configurationService.updateCustomField(customField);
            } else {
                throw new SystemConfigurationException("Invalid action " + action + " while editing custom field.");
            }

            if (customField == null) {
                throw new SystemConfigurationException(
                        "Unable to create new custom field model.");
            }

            HashMap<String, String> translations = (HashMap<String, String>) PropertyUtils
                    .getSimpleProperty(form, "translations");
            String key = CustomFieldUtilities
                    .getCustomFieldLabelKey(customField.getId());
            log.debug("Processing label translations for custom field "
                    + customField.getId() + " with key " + key);
            if (translations != null && key != null && !key.equals("")) {
                configurationService.removeLanguageKey(key);
                Iterator<String> iter = translations.keySet().iterator();
                while (iter.hasNext()) {
                    String locale = iter.next();
                    if (locale != null) {
                        String translation = translations.get(locale);
                        if (translation != null && !translation.trim().equals("")) {
                            log.debug("Adding new translation for locale "
                                    + locale + " for "
                                    + customField.getId());
                            configurationService
                                    .updateLanguageItem(new Language(locale,
                                            key, translation));
                        }
                    }
                }
            }
            if (key != null) {
                ITrackerResources.clearKeyFromBundles(key, true);
            }
            try {
                // Now reset the cached versions in IssueUtilities
                configurationService.resetConfigurationCache(Configuration.Type.customfield);
            } catch (Exception e) {
                log.info("execute: resetConfigurationCache trowed exception, caught", e);
            }

            HttpSession session = request.getSession();
            if (customField.getFieldType() == CustomField.Type.LIST && "create".equals(action)) {
                session.setAttribute(Constants.CUSTOMFIELD_KEY, customField);
                customFieldForm.setRequestEnv(request);
                saveToken(request);
                return new ActionForward(mapping.findForward("editcustomfield").getPath() + "?id=" + customField.getId() + "&action=update");
            }

            session.removeAttribute(Constants.CUSTOMFIELD_KEY);
        } catch (SystemConfigurationException sce) {
            log.error("Exception processing form data: " + sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(sce.getKey()));
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("error");
        }

        return mapping.findForward("listconfiguration");
    }
}

