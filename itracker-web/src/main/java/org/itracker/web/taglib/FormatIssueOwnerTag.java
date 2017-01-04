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

import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.web.util.Constants;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Locale;


public class FormatIssueOwnerTag extends TagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String emptyKey = "itracker.web.generic.unassigned";
    private String format;
    private Issue issue;

    public String getFormat() {
        return format;
    }

    public void setFormat(String value) {
        format = value;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue value) {
        issue = value;
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
        String value = "";
        Locale locale = null;

        HttpSession session = pageContext.getSession();
        if (session != null) {
            locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
        }

        if (issue == null || issue.getOwner() == null) {
            value = ITrackerResources.getString(emptyKey, locale);
        } else {
            try {
                if ("short".equalsIgnoreCase(format)) {
                    value = (issue.getOwner().getFirstName().length() > 0 ? issue.getOwner().getFirstName().substring(0, 1).toUpperCase() + "." : "") +
                            " " + issue.getOwner().getLastName();
                } else {
                    value = issue.getOwner().getFirstName() + " " + issue.getOwner().getLastName();
                }
            } catch (Exception e) {
                value = ITrackerResources.getString(emptyKey, locale);
            }
        }

        // ResponseUtils.write(pageContext, value);
        TagUtils.getInstance().write(pageContext, value);
        clearState();
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        emptyKey = "itracker.web.generic.unassigned";
        format = null;
        issue = null;
    }
}
