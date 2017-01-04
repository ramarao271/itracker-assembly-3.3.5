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

package org.itracker.web.forms;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.NameValuePair;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * This is the Struts Form. It is used by action.
 *
 * @author ready
 */
@SuppressWarnings("serial")
public class CustomFieldForm extends ValidatorForm {
    String action = null;
    Integer id = null;
    Integer fieldType = null;
    String required = null;
    String dateFormat = null;
    String sortOptionsByName = null;
    String value = null;
    //	private String base_locale;
    Map<String, String> translations = new HashMap<String, String>();

    private static final Logger log = Logger.getLogger(CustomFieldForm.class);

    public final void setRequestEnv(HttpServletRequest request) {

        ConfigurationService configurationService = ServletContextUtils
                .getItrackerServices().getConfigurationService();
        Locale currentLocale = LoginUtilities.getCurrentLocale(request);
        CustomField customField = (CustomField) request.getSession().getAttribute(Constants.CUSTOMFIELD_KEY);

        Map<String, List<String>> languages_map = configurationService.getAvailableLanguages();
        String[] languagesArray = new String[languages_map.size()];
        int idx = 0;
       for (String language : languages_map.keySet()) {
          languagesArray[idx] = language;
          idx++;
       }

        String pageTitleKey = "itracker.web.admin.editcustomfield.title.create";
        String pageTitleArg = "";

        String action = getAction();

        if ("update".equals(action)) {
            pageTitleKey = "itracker.web.admin.editcustomfield.title.update";
            pageTitleArg = ITrackerResources.getString(CustomFieldUtilities.getCustomFieldLabelKey(customField.getId()));
        }
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        request.setAttribute("customFieldForm", this);
        request.setAttribute("languages", languagesArray);
        request.setAttribute("action", action);

        Map<String, List<String>> languages = configurationService.getAvailableLanguages();
        Map<NameValuePair, List<NameValuePair>> languagesNameValuePair = new TreeMap<>(NameValuePair.KEY_COMPARATOR);
        for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
            String language = entry.getKey();
            List<String> locales = entry.getValue();
            List<NameValuePair> localesNameValuePair = new ArrayList<NameValuePair>();
            for (String locale : locales) {
                NameValuePair localeNameValuePair = new NameValuePair(locale, ITrackerResources.getString("itracker.locale.name", locale));
                localesNameValuePair.add(localeNameValuePair);
            }
            NameValuePair languageNameValuePair = new NameValuePair(language, ITrackerResources.getString("itracker.locale.name", language));
            languagesNameValuePair.put(languageNameValuePair, localesNameValuePair);
        }
        HttpSession session = request.getSession();
        String baseLocaleKey = "translations(" + ITrackerResources.BASE_LOCALE + ")";

        List<CustomFieldValue> options = customField.getOptions();

        Collections.sort(options, CustomFieldValue.SORT_ORDER_COMPARATOR);
        if (log.isDebugEnabled()) {
            log.debug("setRequestEnv: sorted values by sort order comparator: " + options);
        }

        Map<Integer, String> optionsMap = new TreeMap<>();
        for (CustomFieldValue option : options) {
            String optionName = CustomFieldUtilities.getCustomFieldOptionName(customField.getId(), option.getId(), currentLocale);
            optionsMap.put(option.getId(), optionName);
        }

        String fieldTypeString = Integer.toString(CustomField.Type.STRING.getCode());
        String fieldTypeInteger = Integer.toString(CustomField.Type.INTEGER.getCode());
        String fieldTypeDate = Integer.toString(CustomField.Type.DATE.getCode());
        String fieldTypeList = Integer.toString(CustomField.Type.LIST.getCode());

        request.setAttribute("fieldTypeString", fieldTypeString);
        request.setAttribute("fieldTypeInteger", fieldTypeInteger);
        request.setAttribute("fieldTypeDate", fieldTypeDate);
        request.setAttribute("fieldTypeList", fieldTypeList);
        String dateFormatDateOnly = CustomFieldUtilities.DATE_FORMAT_DATEONLY;
        String dateFormatTimeOnly = CustomFieldUtilities.DATE_FORMAT_TIMEONLY;
        String dateFormatFull = CustomFieldUtilities.DATE_FORMAT_FULL;

        request.setAttribute("dateFormatDateOnly", dateFormatDateOnly);
        request.setAttribute("dateFormatTimeOnly", dateFormatTimeOnly);
        request.setAttribute("dateFormatFull", dateFormatFull);

        session.setAttribute("CustomFieldType_List", Integer.toString(CustomField.Type.LIST.getCode()));
        request.setAttribute("baseLocaleKey", baseLocaleKey);
        request.setAttribute("field", customField);
        request.setAttribute("languagesNameValuePair", languagesNameValuePair);
        request.setAttribute("options", options);
        request.setAttribute("optionsMap", optionsMap);

    }


    /*
      * public void reset(ActionMapping mapping, HttpServletRequest request) {
      * action = null; id = null; fieldType = null; required= null; dateFormat=
      * null; sortOptionsByName= null; value= null; translations = null;
      *  }
      */
    @Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        if (null == getBaseTranslation() || "".equals(getBaseTranslation().trim())) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.required",
                    ITrackerResources.getString("itracker.web.attr.baselocale", LoginUtilities.getCurrentLocale(request))));
        }

        setRequestEnv(request);
        return errors;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getSortOptionsByName() {
        return sortOptionsByName;
    }

    public void setSortOptionsByName(String sortOptionsByName) {
        this.sortOptionsByName = sortOptionsByName;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * get localization in base locale
     */
    private String getBaseTranslation() {
        return translations.get(ITrackerResources.BASE_LOCALE);
    }


}
