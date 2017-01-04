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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @deprecated errors should be handled by Action classes, not JSPs!
 */
@Deprecated
public final class ClearErrorsTag extends TagSupport {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = Globals.ERROR_KEY;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public int doStartTag() throws JspException {
        return (SKIP_BODY);
    }

    public int doEndTag() throws JspException {
        HttpSession session = pageContext.getSession();

        if (session == null) {
            return EVAL_PAGE;
        }

        try {
            session.removeAttribute(getName());
        } catch (ClassCastException cce) {
        }
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
        name = Globals.ERROR_KEY;
    }
}
