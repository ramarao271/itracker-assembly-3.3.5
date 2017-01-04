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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.web.util.LoginUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatDateTag extends TagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    final static Logger log = LoggerFactory.getLogger(FormatDateTag.class);
    private String emptyKey = "itracker.web.generic.unavailable";
    private String format;
    private Date date;

    public String getFormat() {
        return format;
    }

    public void setFormat(String value) {
        format = value;
    }

    public Date getDate() {
        if (null == date)
            return null;
        return new Date(date.getTime());
    }

    public void setDate(Date value) {
        if (null == value)
            this.date = null;
        else
            date = new Date(value.getTime());
    }

    public String getEmptyKey() {
        return emptyKey;
    }

    public void setEmptyKey(String value) {
        emptyKey = value;
    }

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        final String value;
        Locale locale = null;
        if (pageContext.getRequest() instanceof HttpServletRequest) {
            locale = LoginUtilities.getCurrentLocale((HttpServletRequest) pageContext.getRequest());
        }
        if (locale == null) {
            locale = ITrackerResources.getLocale();
        }

        if (null != getDate()) {
            value = renderFormattedDate(locale);
        } else {
            value = ITrackerResources.getString(getEmptyKey(), locale);
        }

        TagUtils.getInstance().write(pageContext, value);
        clearState();
        return EVAL_PAGE;
    }

    private String renderFormattedDate(Locale locale) {
        try {
            final String f = cleanupFormatPatternName(getFormat());
            final SimpleDateFormat sdf;
            final String k = "itracker.dateformat." + f;
            if (ITrackerResources.getBundle().containsKey(k)) {
                sdf = new SimpleDateFormat(ITrackerResources.getString(k, locale), locale);
            } else if (ITrackerResources.getBundle().containsKey(f)) {
                sdf = new SimpleDateFormat(ITrackerResources.getString(f, locale), locale);
            } else {
                sdf = new SimpleDateFormat(f);
            }
            return sdf.format(getDate());
        } catch (RuntimeException e) {
            log.warn("failed to format date '{}' with pattern '{}'", getDate(), getFormat());
        }

        return "";
    }

    private static String cleanupFormatPatternName(String f) {
        if (StringUtils.isEmpty(f)) {
            f = "full";
        } else if (StringUtils.equalsIgnoreCase("notime", f)) {
            f = "dateonly";
        } else if (StringUtils.equalsIgnoreCase(f, "short")
                || StringUtils.equalsIgnoreCase(f, "dateonly")
                || StringUtils.equalsIgnoreCase(f, "full")) {
            f = StringUtils.lowerCase(f);
        }
        return f;
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        emptyKey = "itracker.web.generic.unavailable";
        format = null;
        date = null;
    }

}
