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

package org.itracker.web.forms;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class RemoveRelationForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    java.lang.Integer relationId;
    java.lang.String caller;

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        relationId = null;
        caller = null;

    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        return errors;
    }

    public java.lang.String getCaller() {
        return caller;
    }

    public void setCaller(java.lang.String caller) {
        this.caller = caller;
    }

    public java.lang.Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(java.lang.Integer relationId) {
        this.relationId = relationId;
    }

}
