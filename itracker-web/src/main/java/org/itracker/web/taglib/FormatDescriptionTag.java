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
import org.itracker.web.util.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;


/**
 * Truncate a string if it's longer than truncateLength
 *
 * @author ranks@rosa.com
 */
public class FormatDescriptionTag extends BodyTagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String text = null;

    private String truncateKey = "itracker.web.generic.truncated";
    private String truncateText = null;
    private int truncateLength = 40;

    public String getTruncateKey() {
        return truncateKey;
    }

    public void setTruncateKey(String value) {
        truncateKey = value;
    }

    public int getTruncateLength() {
        return truncateLength;
    }

    public void setTruncateLength(int value) {
        truncateLength = value;
    }

    public int doStartTag() throws JspException {
        text = null;
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() > 0) {
                text = value;
            }
        }
        return SKIP_BODY;
    }

    public String getTruncateText() {
        if (null == truncateText) {
            if (pageContext.getRequest() instanceof HttpServletRequest) {
                truncateText = ITrackerResources.getString(truncateKey, LoginUtilities.getCurrentLocale((HttpServletRequest) this.pageContext.getRequest()));
            } else {
                truncateText = ITrackerResources.getString(truncateKey);
            }
            if (null != truncateText) {
                truncateText = truncateText.trim();
            } else {
                truncateText = "";
            }
        }
        return truncateText;
    }

    public int doEndTag() throws JspException {
        StringBuffer results = new StringBuffer();
        if (text != null && text.trim().length() > truncateLength - getTruncateText().length()) {
            results.append(text.trim().substring(0, truncateLength - getTruncateText().length()).trim());
            results.append(getTruncateText());
        } else if (null == text) {
            results.append("");
        } else {
            results.append(text.trim());
        }
        TagUtils.getInstance().write(pageContext, results.toString());
        clearState();
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        truncateKey = "itracker.web.generic.truncated";
        truncateLength = 40;
        text = null;
        truncateText = null;
    }
}
