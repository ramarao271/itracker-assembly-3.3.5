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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.util.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class CustomFieldValueForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String action;

    private Integer id;

    private String value;
    private Integer sortOrder;
    private Map<String, String> translations = new HashMap<>();


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // let's try to put String,String here:
    public Map<String, String> getTranslations() {
        return translations;
    }

    // let's try to put String,String here:
    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    /**
     * get localization in base locale
     */
    private String getBaseTranslation() {
        return translations.get(ITrackerResources.BASE_LOCALE);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {

    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        // TODO: setup request env for validation output
        if (null == getBaseTranslation() || "".equals(getBaseTranslation().trim())) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.required",
                    ITrackerResources.getString("itracker.web.attr.baselocale", LoginUtilities.getCurrentLocale(request))));
        }
        return errors;
    }


}
