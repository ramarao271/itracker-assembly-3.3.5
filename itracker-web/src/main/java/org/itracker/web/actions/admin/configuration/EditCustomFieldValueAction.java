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
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldValueForm;
import org.itracker.web.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class EditCustomFieldValueAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditCustomFieldValueAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();


        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing configuration.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.findForward("listconfiguration");
        }
        resetToken(request);
        HttpSession session = request.getSession(true);
        CustomField customField = (CustomField) session.getAttribute(Constants.CUSTOMFIELD_KEY);
        if (customField == null) {
            return mapping.findForward("listconfiguration");
        }

        CustomFieldValueForm customFieldValueForm = (CustomFieldValueForm) form;

        try {

            String action = customFieldValueForm.getAction();

            if (action == null) {
                return mapping.findForward("listconfiguration");
            }

            ConfigurationService configurationService = getITrackerServices().getConfigurationService();
            CustomFieldValue customFieldValue;

            if ("create".equals(action)) {
                customFieldValue = new CustomFieldValue();
                customFieldValue.setCustomField(customField);
                customFieldValue.setValue(customFieldValueForm.getValue());
                customFieldValue.setSortOrder(customFieldValueForm.getSortOrder());
                customFieldValue = configurationService.createCustomFieldValue(customFieldValue);
            } else if ("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                customFieldValue = configurationService.getCustomFieldValue(id);
                customFieldValue.setValue(customFieldValueForm.getValue());
                customFieldValue.setSortOrder(customFieldValueForm.getSortOrder());
                customFieldValue = configurationService.updateCustomFieldValue(customFieldValue);
            } else {
                throw new SystemConfigurationException("Invalid action " + action + " while editing custom field value.");
            }

            if (customFieldValue == null) {
                throw new SystemConfigurationException("Unable to create new custom field value model.");
            }

            Map<String, String> translations = customFieldValueForm.getTranslations();
            String key = CustomFieldUtilities.getCustomFieldOptionLabelKey(customField.getId(), customFieldValue.getId());
            log.debug("Processing label translations for custom field value " + customFieldValue.getId() + " with key " + key);
            if (translations != null && key != null && !key.equals("")) {
               for (String locale : translations.keySet()) {
                  if (locale != null) {
                     String translation = translations.get(locale);
                     if (translation != null && !translation.equals("")) {
                        log.debug("Adding new translation for locale " + locale + " for " + String.valueOf(customFieldValue.getId()));
                        configurationService.updateLanguageItem(new Language(locale, key, translation));
                     }
                  }
               }
                String baseValue = translations.get(ITrackerResources.BASE_LOCALE);
                configurationService.updateLanguageItem(new Language(ITrackerResources.BASE_LOCALE, key, baseValue));
            }
            if (key != null)
                ITrackerResources.clearKeyFromBundles(key, true);
            // Now reset the cached versions in IssueUtilities
            configurationService.resetConfigurationCache(Configuration.Type.customfield);
            request.setAttribute("action", action);
            String pageTitleKey = "";
            String pageTitleArg = "";
            pageTitleKey = "itracker.web.admin.editcustomfieldvalue.title.create";
            if ("update".equals(action)) {
                pageTitleKey = "itracker.web.admin.editcustomfieldvalue.title.update";
            }

            request.setAttribute("languages", configurationService.getAvailableLanguages());
            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);

            saveToken(request);
            return new ActionForward(mapping.findForward("editcustomfield").getPath() + "?id=" + customField.getId() + "&action=update");
        } catch (SystemConfigurationException sce) {
            log.error("Exception processing form data: " + sce.getMessage(), sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(sce.getKey()));
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            request.setAttribute("customFieldValueForm", form);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }
}
  