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

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @deprecated errors should be handled by Action classes, not JSPs!
 */
@Deprecated
public final class AddErrorTag extends TagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = Globals.ERROR_KEY;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        key = value;
    }

    public int doStartTag() throws JspException {
        return (SKIP_BODY);
    }

    public int doEndTag() throws JspException {
        ActionErrors errors = null;
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        if (request == null || getKey() == null) {
            return EVAL_PAGE;
        }

        try {
            errors = (ActionErrors) request.getAttribute(getName());
        } catch (ClassCastException cce) {
        }

        if (errors == null) {
            errors = new ActionErrors();
        }

        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(getKey()));
        request.setAttribute(getName(), errors);
        clearState();
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        clearState();
    }

    private void clearState() {
        name = Globals.ERROR_KEY;
        key = null;
    }
}
