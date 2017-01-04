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

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.SystemConfigurationException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.Language;
import org.itracker.model.NameValuePair;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.CustomFieldValueForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EditCustomFieldValueFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger
            .getLogger(EditCustomFieldValueFormAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        //  TODO: Action Cleanup

        if (!LoginUtilities.hasPermission(UserUtilities.PERMISSION_USER_ADMIN, request,
                response)) {
            return mapping.findForward("unauthorized");
        }
        ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
                .getConfigurationService();

        try {

            HttpSession session = request.getSession(true);
            Map<String, List<String>> languages = configurationService
                    .getAvailableLanguages();

            CustomFieldValueForm customFieldValueForm = (CustomFieldValueForm) form;

            if (customFieldValueForm == null) {
                customFieldValueForm = new CustomFieldValueForm();
            }

            CustomFieldValue customFieldValue = new CustomFieldValue();

            String action = customFieldValueForm.getAction();

            String messageKey;
            if ("update".equals(action)) {
                Integer id = customFieldValueForm.getId();
                customFieldValue = configurationService.getCustomFieldValue(id);
                if (customFieldValue == null) {
                    throw new SystemConfigurationException(
                            "Invalid custom field value id " + id);
                }

                customFieldValueForm.setId(id);
                customFieldValueForm.setValue(customFieldValue.getValue());

                customFieldValueForm.setSortOrder(customFieldValue.getSortOrder());

                Map<String, String> translations = new TreeMap<>();
                messageKey = CustomFieldUtilities
                        .getCustomFieldOptionLabelKey(customFieldValue
                                        .getCustomField().getId(),
                                customFieldValue.getId());
                List<Language> languageItems = configurationService
                        .getLanguageItemsByKey(messageKey);

                for (Language languageItem : languageItems) {
                    translations.put(languageItem.getLocale(),
                            languageItem.getResourceValue());
                }
                customFieldValueForm.setTranslations(translations);
            } else {
                customFieldValue.setCustomField((CustomField) session.getAttribute(Constants.CUSTOMFIELD_KEY));
                messageKey = "";
            }

            CustomField field = customFieldValue.getCustomField();

            String pageTitleKey = "";
            String pageTitleArg = "";
            pageTitleKey = "itracker.web.admin.editcustomfieldvalue.title.create";
            if ("update".equals(action)) {
                pageTitleKey = "itracker.web.admin.editcustomfieldvalue.title.update";
            }

            request.setAttribute("languages", configurationService.getAvailableLanguages());
            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);

            request.setAttribute("messageKey", messageKey);
            request.setAttribute("languages", languages);
            request.setAttribute("customFieldValueForm", customFieldValueForm);
            request.setAttribute("action", action);
            session.setAttribute(Constants.CUSTOMFIELDVALUE_KEY,
                    customFieldValue);
            session.setAttribute("field", field);
            saveToken(request);
            setRequestEnvironment(request, configurationService);
            return mapping.getInputForward();



        } catch (SystemConfigurationException sce) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.invalidcustomfieldvalue"));
        } catch (Exception e) {
            log.error("Exception while creating edit custom field value form.",
                    e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            setRequestEnvironment(request, configurationService);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }

    public static void setRequestEnvironment(HttpServletRequest request, ConfigurationService configurationService) {
        Map<String, List<String>> languages = configurationService.getAvailableLanguages();
        Map<NameValuePair, List<NameValuePair>> languagesNameValuePair = new TreeMap<>(NameValuePair.KEY_COMPARATOR);
        for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
            String language = entry.getKey();
            List<String> locales = entry.getValue();

            List<NameValuePair> localesNameValuePair = new ArrayList<>();
            for (String locale : locales) {
                NameValuePair localeNameValuePair = new NameValuePair(locale, ITrackerResources.getString("itracker.locale.name", locale));
                localesNameValuePair.add(localeNameValuePair);
            }
            NameValuePair languageNameValuePair = new NameValuePair(language, ITrackerResources.getString("itracker.locale.name", language));
            languagesNameValuePair.put(languageNameValuePair, localesNameValuePair);
        }
        request.setAttribute("languagesNameValuePair", languagesNameValuePair);
        request.setAttribute("baseLocale", ITrackerResources.BASE_LOCALE);
    }

}
