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
public class LoginForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String login = null;
    private String password = null;
    private boolean saveLogin;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public boolean isSaveLogin() {
        return saveLogin;
    }

    public void setSaveLogin(boolean saveLogin) {
        this.saveLogin = saveLogin;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        login = null;
        password = null;

    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        Boolean skipLogin = (Boolean) request.getSession().getAttribute(
                "loginForwarded");
        if (null == skipLogin) {
            skipLogin = false;
        }
        skipLogin |= (null == getLogin() && null == getPassword());

        ActionErrors errors;
        /*
           * SKIP credentials validation when forwarded to login.
           */
        if (skipLogin == null || !skipLogin.booleanValue()) {
            // log.debug("execute: forwarded, skip login.");
            errors = super.validate(mapping, request);
        } else {
            request.getSession().removeAttribute("loginForwarded");
            return new ActionErrors();
        }
        return errors;
    }

}
