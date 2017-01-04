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

import org.apache.oro.text.regex.*;
import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.web.util.Constants;
import org.itracker.web.util.HTMLUtilities;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.net.MalformedURLException;
import java.util.Locale;


/**
 * Formats an ITracker Issue History entry.  Will rewrite the entry to include
 * links to other issues automatically based on the strings provided in the
 * resource bundles under the itracker.web.issuenames key.  This key can contain
 * a pipe seperated list of names to look for and then matches in the pattern
 * ([names])(\d+).
 */
public class FormatHistoryEntryTag extends BodyTagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Perl5Matcher matcher = new Perl5Matcher();
    private static PatternCompiler compiler = new Perl5Compiler();

    private String text = null;
    private String issueNamesKey = "itracker.web.issuenames";
    private String forward = "viewissue";
    private String paramName = "id";
    private String paramValue = "$2";
    private String textPattern = "$1 $2";
    private String styleClass = "history";
    private int projectOptions = 0;

    public String getForward() {
        return forward;
    }

    public void setForward(String value) {
        forward = value;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String value) {
        paramName = value;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object value) {
        paramValue = (value != null ? value.toString() : null);
    }

    public String getIssueNamesKey() {
        return issueNamesKey;
    }

    public void setIssueNamesKey(String value) {
        issueNamesKey = value;
    }

    public String getTextPattern() {
        return textPattern;
    }

    public void setTextPattern(String value) {
        textPattern = value;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String value) {
        styleClass = value;
    }

    public int getProjectOptions() {
        return projectOptions;
    }

    public void setProjectOptions(int value) {
        projectOptions = value;
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
        if (text != null) {
            Locale locale = null;

            HttpSession session = pageContext.getSession();
            if (session != null) {
                locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
            }

            if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, projectOptions)) {
                text = HTMLUtilities.removeMarkup(text);
            } else if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, projectOptions)) {
                text = HTMLUtilities.escapeTags(text);
            } else {
                text = HTMLUtilities.newlinesToBreaks(text);
            }

            try {
                Pattern pattern = compiler.compile("(" + ITrackerResources.getString(issueNamesKey, locale) + ")\\s(\\d+)", Perl5Compiler.CASE_INSENSITIVE_MASK);

                StringBuffer buf = new StringBuffer("<a href=\"");
                try {
                    //buf.append(RequestUtils.computeURL(pageContext, forward, null, null, null, null, null, false));
                    buf.append(TagUtils.getInstance().computeURL(pageContext, forward, null, null, null, null, null, null, false));
                } catch (MalformedURLException murle) {
                    buf.append(forward);
                }
                buf.append("?" + paramName + "=" + paramValue + "\" ");
                buf.append("class=\"" + styleClass + "\">");
                buf.append(textPattern);
                buf.append("</a>");

                text = Util.substitute(matcher, pattern, new Perl5Substitution(buf.toString()), text, Util.SUBSTITUTE_ALL);
            } catch (MalformedPatternException mpe) {
            }

            if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, projectOptions) ||
                    ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, projectOptions)) {
                text = "<pre>" + text + "</pre>";
            }

            // ResponseUtils.write(pageContext, text);
            TagUtils.getInstance().write(pageContext, text);
        }

        clearState();
        return (EVAL_PAGE);
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        text = null;
        issueNamesKey = "itracker.web.issuenames";
        forward = "viewissue";
        paramName = "id";
        paramValue = "$2";
        textPattern = "$1 $2";
        styleClass = "history";
    }
}
