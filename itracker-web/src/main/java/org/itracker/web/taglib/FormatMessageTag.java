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
import org.itracker.web.util.Constants;
import org.itracker.web.util.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Arrays;
import java.util.Locale;


public class FormatMessageTag extends TagSupport {

    private static final long serialVersionUID = 1L;
    private String key = null;
    private String arg0 = null;
    private String arg1 = null;
    private String arg2 = null;
    private String locale = null;
    private String localeKey = Constants.LOCALE_KEY;

    private static final Logger log = Logger.getLogger(FormatMessageTag.class);

    protected static final Locale defaultLocale = ITrackerResources.getLocale();

    public String getArg0() {
        return arg0;
    }

    public void setArg0(String value) {
        arg0 = value;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String value) {
        arg1 = value;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String value) {
        arg2 = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        key = value;
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public void setLocaleKey(String value) {
        localeKey = value;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String value) {
        locale = value;
    }

    public int doStartTag() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        String message = null;
        Locale messageLocale = defaultLocale;
        messageLocale = (Locale) LoginUtilities.getCurrentLocale((HttpServletRequest) pageContext.getRequest());
//        messageLocale = (Locale) (pageContext.getSession().getAttribute(getLocaleKey()));

        if (locale != null) {
            messageLocale = ITrackerResources.getLocale(locale);
            if (log.isDebugEnabled()) {
                log.debug("doEndTag: locale resolved to " + messageLocale);
            }
        }
        Object[] args = null;
        if (getArg0() == null) {
            message = ITrackerResources.getString(key, messageLocale);
        } else if (getArg2() != null) {
            args = new Object[]{getArg0(), getArg1(), getArg2()};
            message = ITrackerResources.getString(key, messageLocale, args);
        } else if (getArg1() != null) {
            args = new Object[]{getArg0(), getArg1()};
            message = ITrackerResources.getString(key, messageLocale, args);
        } else {
            args = new Object[]{getArg0()};
            message = ITrackerResources.getString(key, messageLocale, args);
        }

        if (log.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer("doEndTag: resolved ").append(key).append(" for locale: ").append(" to '").append(message).append("', args_: ").append((null == args ? null : Arrays.asList(args)));
            log.debug(sb.toString());
        }

        //ResponseUtils.write(pageContext, message);
        TagUtils.getInstance().write(pageContext, message);

        clearState();
        return EVAL_PAGE;
    }


    public void release() {
        super.release();
        localeKey = Constants.LOCALE_KEY;
    }

    private void clearState() {
        arg0 = null;
        arg1 = null;
        arg2 = null;
        key = null;
        locale = null;
    }
}
