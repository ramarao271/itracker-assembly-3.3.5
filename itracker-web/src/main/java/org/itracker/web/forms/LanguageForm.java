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
import org.apache.struts.validator.ValidatorForm;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class LanguageForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String action = null;
    private String parentLocale = null;
    private String key = null;
    private String locale = null;
    private String localeTitle = null;
    private Map<String, String> items = new HashMap<String, String>();
    private String localeBaseTitle;

    /*
      * public void reset(ActionMapping mapping, HttpServletRequest request) {
      * action = null; parentLocale = null; key= null; locale= null; HashMap<String,String>
      * items = new HashMap<String,String>(); }
      */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        return errors;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getParentLocale() {
        return parentLocale;
    }

    public void setParentLocale(String parentLocale) {
        this.parentLocale = parentLocale;
    }

    public String getLocaleTitle() {
        return localeTitle;
    }

    public void setLocaleTitle(String localeTitle) {
        this.localeTitle = localeTitle;
    }

    public void setLocaleBaseTitle(String resourceValue) {
        this.localeBaseTitle = resourceValue;

    }

    public String getLocaleBaseTitle() {
        return localeBaseTitle;
    }

}
