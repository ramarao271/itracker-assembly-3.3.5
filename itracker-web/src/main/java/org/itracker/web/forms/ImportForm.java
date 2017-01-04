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
public class ImportForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private transient org.apache.struts.upload.FormFile importFile;
    private Boolean optionreuseusers;
    private Boolean optionreuseprojects;
    private Boolean optionreuseconfig;
    private Boolean optionreusefields;
    private Boolean optioncreatepasswords;

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        importFile = null;
        optionreuseusers = null;
        optionreuseprojects = null;
        optionreuseconfig = null;
        optionreusefields = null;
        optioncreatepasswords = null;

    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        return errors;
    }

    public org.apache.struts.upload.FormFile getImportFile() {
        return importFile;
    }

    public void setImportFile(org.apache.struts.upload.FormFile importFile) {
        this.importFile = importFile;
    }

    public Boolean getOptioncreatepasswords() {
        return optioncreatepasswords;
    }

    public void setOptioncreatepasswords(Boolean optioncreatepasswords) {
        this.optioncreatepasswords = optioncreatepasswords;
    }

    public Boolean getOptionreuseconfig() {
        return optionreuseconfig;
    }

    public void setOptionreuseconfig(Boolean optionreuseconfig) {
        this.optionreuseconfig = optionreuseconfig;
    }

    public Boolean getOptionreusefields() {
        return optionreusefields;
    }

    public void setOptionreusefields(Boolean optionreusefields) {
        this.optionreusefields = optionreusefields;
    }

    public Boolean getOptionreuseprojects() {
        return optionreuseprojects;
    }

    public void setOptionreuseprojects(Boolean optionreuseprojects) {
        this.optionreuseprojects = optionreuseprojects;
    }

    public Boolean getOptionreuseusers() {
        return optionreuseusers;
    }

    public void setOptionreuseusers(Boolean optionreuseusers) {
        this.optionreuseusers = optionreuseusers;
    }

}
