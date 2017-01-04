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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.SystemConfigurationException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditConfigurationAction extends ItrackerBaseAction {

    private static final Logger log = Logger
            .getLogger(EditConfigurationAction.class);

    @SuppressWarnings("unchecked")
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();
        // TODO: Action Cleanup

        User currUser = LoginUtilities.getCurrentUser(request);
        if (!isTokenValid(request)) {
            log.debug("Invalid request token while editing configuration.");
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.transaction"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        resetToken(request);

        try {
            final ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
                    .getConfigurationService();

            final String action = (String) PropertyUtils.getSimpleProperty(form,
                    "action");
            String formValue = (String) PropertyUtils.getSimpleProperty(form,
                    "value");

            String initialLanguageKey = null;
            Map<String, String> translations = (Map<String, String>) PropertyUtils
                    .getSimpleProperty(form, "translations");

            if (action == null) {
                return mapping.findForward("listconfiguration");
            }

            Configuration configItem = null;
            if ("createresolution".equals(action)) {
                int value = -1;
                int order = 0;

                try {
                    List<Configuration> resolutions = configurationService
                            .getConfigurationItemsByType(Configuration.Type.resolution);
                    if (resolutions.size() < 1) {
                        // fix for no existing resolution
                        value = Math.max(value, 0);
                    }
                    for (Configuration resolution : resolutions) {
                        value = Math.max(value, Integer.parseInt(resolution.getValue()));
                        order = resolution.getOrder();
                    }
                    if (value > -1) {
                        String version = configurationService
                                .getProperty("version");
                        configItem = new Configuration(
                                Configuration.Type.resolution,
                                Integer.toString(++value), version, ++order);
                    }
                } catch (NumberFormatException nfe) {
                    log.debug("Found invalid value or order for a resolution.",
                            nfe);
                    throw new SystemConfigurationException(
                            "Found invalid value or order for a resolution.");
                }
            } else if ("createseverity".equals(action)) {
                int value = -1;
                int order = 0;

                try {
                    List<Configuration> severities = configurationService
                            .getConfigurationItemsByType(Configuration.Type.severity);
                    if (severities.size() < 1) {
                        // fix for no existing severity
                        value = Math.max(value, 0);
                    }
                    for (Configuration severity : severities) {
                        value = Math.max(value, Integer.parseInt(severity.getValue()));
                        order = severity.getOrder();
                    }
                    if (value > -1) {
                        String version = configurationService
                                .getProperty("version");
                        configItem = new Configuration(
                                Configuration.Type.severity,
                                Integer.toString(++value), version, ++order);
                    }
                } catch (NumberFormatException nfe) {
                    log.debug("Found invalid value or order for a severity.",
                            nfe);
                    throw new SystemConfigurationException(
                            "Found invalid value or order for a severity.");
                }
            } else if ("createstatus".equals(action)) {
                try {
                    if (null == formValue) {

                        throw new SystemConfigurationException(
                                "Supplied status value is null.",
                                "itracker.web.error.validate.required");
                    }
                    int value = Integer.parseInt(formValue);
                    List<Configuration> statuses = configurationService
                            .getConfigurationItemsByType(Configuration.Type.status);
                    for (Configuration status : statuses) {
                        if (value == Integer.parseInt(status
                                .getValue())) {
                            throw new SystemConfigurationException(
                                    "Supplied status value already equals existing status.",
                                    "itracker.web.error.existingstatus");
                        }
                    }

                    String version = configurationService
                            .getProperty("version");
                    configItem = new Configuration(
                            Configuration.Type.status,
                            formValue, version, value);
                } catch (NumberFormatException nfe) {
                    throw new SystemConfigurationException("Invalid value "
                            + formValue + " for status.",
                            "itracker.web.error.invalidstatus");
                }
            } else if ("update".equals(action)) {
                Integer id = (Integer) PropertyUtils.getSimpleProperty(form,
                        "id");
                configItem = configurationService.getConfigurationItem(id);

                if (configItem == null) {
                    throw new SystemConfigurationException(
                            "Invalid configuration item id " + id);
                }
                formValue = configItem.getValue();

                initialLanguageKey = SystemConfigurationUtilities
                        .getLanguageKey(configItem);

                if (configItem.getType() == Configuration.Type.status
                        && formValue != null && !formValue.equals("")) {
                    if (!configItem.getValue().equalsIgnoreCase(formValue)) {
                        try {
                            int currStatus = Integer.parseInt(configItem
                                    .getValue());
                            int newStatus = Integer.parseInt(formValue);

                            List<Configuration> statuses = configurationService
                                    .getConfigurationItemsByType(Configuration.Type.status);
                            for (Configuration statuse : statuses) {
                                if (newStatus == Integer.parseInt(statuse.getValue())) {
                                    throw new SystemConfigurationException(
                                            "Supplied status value already equals existing status.",
                                            "itracker.web.error.existingstatus");
                                }
                            }
                            // set new value
                            configItem.setValue(formValue.trim());

                            log.debug("Changing issue status values from "
                                    + configItem.getValue() + " to "
                                    + formValue);

                            IssueService issueService = ServletContextUtils.getItrackerServices()
                                    .getIssueService();

                            List<Issue> issues = issueService
                                    .getIssuesWithStatus(currStatus);
                            Issue issue;
                            for (Issue i : issues) {
                                if (i != null) {
                                    i.setStatus(newStatus);
                                    IssueActivity activity = new
                                            IssueActivity();
                                    activity.setActivityType(IssueActivityType.SYSTEM_UPDATE);
                                    activity.setDescription(ITrackerResources.getString("itracker.activity.system.status"));
                                    i.getActivities().add(activity);
                                    activity.setIssue(i);
                                    issue = issueService.systemUpdateIssue(i, currUser.getId());
                                    issues.add(issue);
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            throw new SystemConfigurationException(
                                    "Invalid value " + formValue
                                            + " for updated status.",
                                    "itracker.web.error.invalidstatus");
                        }
                    }
                }
            } else {
                throw new SystemConfigurationException("Invalid action "
                        + action + " while editing configuration item.");
            }

            if (configItem == null) {
                throw new SystemConfigurationException(
                        "Unable to create new configuration item model.");
            }
            if ("update".equals(action)) {
                configItem = configurationService
                        .updateConfigurationItem(configItem);
            } else {
                configItem = configurationService
                        .createConfigurationItem(configItem);
            }

            if (configItem == null) {
                throw new SystemConfigurationException(
                        "Unable to create new configuration item.");
            }

            String key = SystemConfigurationUtilities
                    .getLanguageKey(configItem);
            log.debug("Processing translations for configuration item "
                    + configItem.getId() + " with key " + key);
            if (translations != null && StringUtils.isNotBlank(key)) {
                String locale, translation;
                Iterator<String> iter = translations.keySet().iterator();
                configurationService.removeLanguageKey(key);
                while (iter.hasNext()) {
                    locale = iter.next();
                    if (locale != null) {
                        translation = translations.get(locale);
                        if (StringUtils.isNotBlank(translation)) {
                            log.debug("Adding new translation for locale "
                                    + locale + " for " + configItem);
                            configurationService
                                    .updateLanguageItem(new Language(locale,
                                            key, translation));
                        }
                    }
                }
                String baseValue = translations
                        .get(ITrackerResources.BASE_LOCALE);
                if (StringUtils.isNotBlank(baseValue)) {
                    configurationService.updateLanguageItem(new Language(
                            ITrackerResources.BASE_LOCALE, key, baseValue));
                }
                // remove old languageItems if resource key has changed
                if (initialLanguageKey != null
                        && !initialLanguageKey.equals(key)) {
                    configurationService.removeLanguageKey(initialLanguageKey);
                }
                ITrackerResources.clearKeyFromBundles(key, true);
                ITrackerResources.clearKeyFromBundles(initialLanguageKey, true);
            }

            // Now reset the cached versions in IssueUtilities
            configurationService.resetConfigurationCache(configItem.getType());

            PropertyUtils.setSimpleProperty(form, "value", formValue);

            String pageTitleKey = "";
            String pageTitleArg = "";

            if ("update".equals(action)) {
                pageTitleKey = "itracker.web.admin.editconfiguration.title.update";
            } else {
                Locale locale = getLocale(request);
                pageTitleKey = "itracker.web.admin.editconfiguration.title.create";
                if ("createseverity".equals(BeanUtils.getSimpleProperty(form, "action"))) {
                    pageTitleArg = ITrackerResources.getString(
                            "itracker.web.attr.severity", locale);
                } else if ("createstatus"
                        .equals(BeanUtils.getSimpleProperty(form, "action"))) {
                    pageTitleArg = ITrackerResources.getString(
                            "itracker.web.attr.status", locale);
                } else if ("createresolution".equals(BeanUtils.getSimpleProperty(form, "action"))) {
                    pageTitleArg = ITrackerResources.getString(
                            "itracker.web.attr.resolution", locale);
                } else {
                    return mapping.findForward("unauthorized");
                }
            }
            request.setAttribute("pageTitleKey", pageTitleKey);
            request.setAttribute("pageTitleArg", pageTitleArg);
            return mapping.findForward("listconfiguration");
        } catch (SystemConfigurationException sce) {
            log.error("Exception processing form data: " + sce.getMessage(),
                    sce);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(sce
                    .getKey()));
        } catch (Exception e) {
            log.error("Exception processing form data", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            return mapping.getInputForward();
        }

        return mapping.findForward("error");
    }
}
