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
import org.itracker.model.NameValuePair;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class ConfigurationForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String action;
    Integer id;
    String value;
    Integer order;
    String key;
    String typeKey;

    // let's try to put String,String here:
    Map<String, String> translations = new TreeMap<>();
    private Map<NameValuePair, List<NameValuePair>> languages;

    /*
      * public void reset(ActionMapping mapping, HttpServletRequest request) {
      * action = null; id = null; value = null; order = null; key = null;
      * translations = null; }
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    // let's try to put String,String here:
    public Map<String, String> getTranslations() {
        return translations;
    }

    // let's try to put String,String here:
    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLanguages(Map<NameValuePair, List<NameValuePair>> languages) {
        this.languages = languages;
    }

    public Map<NameValuePair, List<NameValuePair>> getLanguages() {
        return languages;
    }
}
