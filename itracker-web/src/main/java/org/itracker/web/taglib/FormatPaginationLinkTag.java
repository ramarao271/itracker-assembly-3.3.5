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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.net.MalformedURLException;

public final class FormatPaginationLinkTag extends BodyTagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String text = null;

    private String order = null;
    private String page = null;
    private Integer projectId = null;
    private int start = 0;
    private String styleClass = null;

    public String getOrder() {
        return order;
    }

    public void setOrder(String value) {
        order = value;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String value) {
        page = value;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer value) {
        projectId = value;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int value) {
        start = value;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String value) {
        styleClass = value;
    }

    public int doStartTag() throws JspException {
        StringBuffer buf = new StringBuffer("<a href=\"");
        try {
            // buf.append(RequestUtils.computeURL(pageContext, null, null, page, null, null, null, false));
            buf.append(TagUtils.getInstance().computeURL(pageContext, null, null, page, null, null, null, null, false));
        } catch (MalformedURLException murle) {
            buf.append(page);
        }
        buf.append("?start=" + start);
        if (projectId != null) {
            buf.append("&projectId=" + projectId);
        }
        if (order != null && order.trim().length() > 0) {
            buf.append("&order=" + order);
        }
        buf.append("\"");
        if (styleClass != null) {
            buf.append("class=\"" + styleClass + "\"");
        }
        buf.append(">");
        // ResponseUtils.write(pageContext, buf.toString());
        TagUtils.getInstance().write(pageContext, buf.toString());
        text = null;
        return (EVAL_BODY_BUFFERED);
    }

    public int doAfterBody() throws JspException {
        if (bodyContent != null) {
            String value = bodyContent.getString().trim();
            if (value.length() > 0) {
                text = value;
            }
        }
        return (SKIP_BODY);
    }

    public int doEndTag() throws JspException {
        StringBuffer results = new StringBuffer();
        if (text != null) {
            results.append(text);
        }
        results.append("</a>");
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
        order = null;
        page = null;
        projectId = null;
        start = 0;
        styleClass = null;
    }
}
