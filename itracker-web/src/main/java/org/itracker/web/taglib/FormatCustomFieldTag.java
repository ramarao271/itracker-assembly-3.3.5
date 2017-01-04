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

package org.itracker.web.taglib;

import org.apache.log4j.Logger;
import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.CustomField;
import org.itracker.model.CustomFieldValue;
import org.itracker.model.NameValuePair;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.web.util.HTMLUtilities;
import org.itracker.web.util.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FormatCustomFieldTag extends TagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String DISPLAY_TYPE_EDIT = "edit";
    public static final String DISPLAY_TYPE_VIEW = "view";

    private static final Logger logger = Logger
            .getLogger(FormatCustomFieldTag.class);

    private CustomField field;
    private String currentValue;
    private String displayType;
    private String formName;
    private Map<Integer, List<NameValuePair>> listOptions;

    public CustomField getField() {
        return field;
    }

    public void setField(CustomField value) {
        field = value;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String value) {
        currentValue = value;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String value) {
        displayType = value;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String value) {
        formName = value;
    }

    public Map<Integer, List<NameValuePair>> getListOptions() {
        return (listOptions == null ? new HashMap<Integer, List<NameValuePair>>()
                : listOptions);
    }

    public void setListOptions(Map<Integer, List<NameValuePair>> value) {
        listOptions = value;
    }

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        Locale locale = null;

        if (field != null) {
            locale = LoginUtilities
                    .getCurrentLocale((HttpServletRequest) pageContext
                            .getRequest());

            StringBuilder buf = new StringBuilder();
            buf
            .append("<div class=\"form-group\"><label>")
            .append(CustomFieldUtilities.getCustomFieldName(field.getId(), locale))
            .append(":")
            .append("</label>\n");

            if (DISPLAY_TYPE_VIEW.equalsIgnoreCase(displayType)) {
                buf.append("<p class=\"form-control-static\">\n");
                if (currentValue != null) {
                    if (field.getFieldType() == CustomField.Type.LIST) {
                        buf.append(CustomFieldUtilities
                                .getCustomFieldOptionName(getField(),
                                        currentValue, locale));
                    } else {
                        buf.append(currentValue);
                    }
                }
                buf.append("</p>");

            } else {
                Object requestValue = TagUtils.getInstance().lookup(
                        pageContext,
                        org.apache.struts.taglib.html.Constants.BEAN_KEY,
                        "customFields(" + field.getId() + ")", null);
                if (currentValue == null && requestValue != null) {
                    currentValue = requestValue.toString();
                }


                if (field.getFieldType() == CustomField.Type.LIST) {
                    final List<CustomFieldValue> options = field.getOptions();

                    buf.append("<select name=\"customFields(").append(
                            field.getId()).append(
                            ")\" class=\"form-control\">\n");
                    for (CustomFieldValue option : options) {
                        buf.append("<option value=\"").append(
                                HTMLUtilities.escapeTags(option
                                        .getValue())).append("\"");
                        if (currentValue != null
                                && currentValue.equals(option
                                .getValue())) {
                            buf.append(" selected=\"selected\"");
                        }
                        buf.append(" class=\"editColumnText\">");
                        buf.append(CustomFieldUtilities
                                .getCustomFieldOptionName(option,
                                        locale));
                        buf.append("</option>\n");
                    }
                    buf.append("</select>\n");


                } else if (field.getFieldType() == CustomField.Type.DATE) {
                    String df = ITrackerResources.getString(
                            "itracker.dateformat.dateonly", locale);
                    if (field.getDateFormat().equals("full")) {
                        df = ITrackerResources.getString(
                                "itracker.dateformat.full", locale);
                    }

                    String fieldName = "customFields(" + field.getId() + ")";
                    buf.append("<div class=\"input-group date\" data-format=\"")
                            .append(HTMLUtilities.getJSDateFormat(df))
                            .append("\">");
                    buf.append("<input  type=\"text\" name=\"")
                            .append(fieldName).append("\" id=\"")
                            .append(fieldName).append("\"");
                    buf.append((currentValue != null
                            && !currentValue.equals("") ? " value=\""
                            + currentValue + "\"" : ""));
                    buf.append(" class=\"form-control\" />")
                    .append("<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-th\"></i></span>")
                    .append("</div>");

                } else  if (null != getListOptions() && null != getListOptions().get(field.getId())
                        && !getListOptions().get(field.getId()).isEmpty()) {
                    List<NameValuePair> options = getListOptions().get(field.getId());
                    buf.append("<select name=\"customFields(").append(
                               field.getId()).append(
                               ")\" class=\"form-control\">\n");
                    for (NameValuePair option : options) {
                        buf.append("<option value=\"").append(
                                HTMLUtilities.escapeTags(option
                                        .getValue())).append("\"");
                        if (currentValue != null
                                && currentValue.equals(option
                                .getValue())) {
                            buf.append(" selected=\"selected\"");
                        }
                        buf.append(option.getName());
                        buf.append("</option>\n");
                    }
                       buf.append("</select>\n");
                } else {
                    buf.append("<input type=\"text\" name=\"customFields(")
                            .append(field.getId()).append(")\"");
                    buf.append((currentValue != null
                            && !currentValue.equals("") ? " value=\""
                            + currentValue + "\"" : ""));
                    buf.append(" class=\"form-control\">");
                }
            }
            buf.append("</div>");

            TagUtils.getInstance().write(pageContext, buf.toString());
        }

        clearState();
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        field = null;
        currentValue = null;
        displayType = null;
        listOptions = null;
        formName = null;
    }


}
