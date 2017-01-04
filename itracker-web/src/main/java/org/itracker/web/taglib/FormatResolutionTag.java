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
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.web.util.Constants;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.Locale;
import java.util.MissingResourceException;


/**
 * Formats a resolution for a display.  If the project uses fixed resolutions,
 * it prints the appropriate string for the current locale.  Currently the tag
 * only supports non-editable resolution fields.
 */
public class FormatResolutionTag extends BodyTagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String DISPLAY_TYPE_EDIT = "edit";
    public static final String DISPLAY_TYPE_VIEW = "view";

    private String text = null;
    private String displayType;
    private int projectOptions;

    public int getProjectOptions() {
        return projectOptions;
    }

    public void setProjectOptions(int value) {
        projectOptions = value;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String value) {
        displayType = value;
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

    public int doEndTag() throws JspException {
        StringBuffer results = new StringBuffer();
        if (text != null && !text.trim().equals("")) {
            Locale locale = null;

            HttpSession session = pageContext.getSession();
            if (session != null) {
                locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            }

            try {

                projectOptions = ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS;
            } catch (NumberFormatException nfe) {
            }

            if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, projectOptions)) {
                try {
                    text = IssueUtilities.checkResolutionName(text, locale);
                } catch (MissingResourceException mre) {
                    // Key didn't exist so just stick the key in as a real number.
                }
            }
            results.append(text);
        }
        // ResponseUtils.write(pageContext, results.toString());
        TagUtils.getInstance().write(pageContext, results.toString());
        clearState();
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        text = null;
        displayType = DISPLAY_TYPE_VIEW;
        projectOptions = 0;
    }
}
