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

import bsh.Parser;
import groovy.lang.GroovyShell;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.WorkflowScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.itracker.model.util.WorkflowUtilities.*;
/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class WorkflowScriptForm extends ValidatorForm {
    /**
     *
     */
    private static final Logger log = LoggerFactory.getLogger(WorkflowScriptForm.class);
    private static final long serialVersionUID = 1L;
    String action;
    Integer id;
    String name;
    Integer event;
    String script;
    String language;

    private List<LabelValueBean> eventOptions;

    public void initEventOptions(Locale locale) {
        if (null != eventOptions) {
            return;
        }
        eventOptions = new ArrayList<>(5);
        String[] eventTypes = new String[]{String.valueOf(EVENT_FIELD_ONPOPULATE),
                String.valueOf(EVENT_FIELD_ONSETDEFAULT),
                String.valueOf(EVENT_FIELD_ONVALIDATE),
                String.valueOf(EVENT_FIELD_ONPRESUBMIT),
                String.valueOf(EVENT_FIELD_ONPOSTSUBMIT),
        };

        for (String eventType : eventTypes) {
            eventOptions.add(new LabelValueBean(
                    ITrackerResources.getString(ITrackerResources.KEY_BASE_WORKFLOW_EVENT + eventType, locale),
                    eventType));
        }

    }

    public List<LabelValueBean> getEventOptions() {
        return eventOptions;
    }


    public void reset(ActionMapping mapping, HttpServletRequest request) {
        action = null;
        id = null;
        name = null;
        event = null;
        script = null;
    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        if (WorkflowScript.ScriptLanguage.Groovy != WorkflowScript.ScriptLanguage.valueOf(getLanguage())) {
            validateBeanShellScript(errors);
        } else {
            validateGroovyScript(errors);
        }
        return errors;
    }

    private void validateBeanShellScript(ActionErrors errors) {
        try {
            Parser parser = new Parser(new StringReader(getScript()));
            boolean eof;
            while (!(eof = parser.Line())) {
            }
        } catch (Exception e) {
            addScriptError(errors, e);
        }
    }

    private void validateGroovyScript(ActionErrors errors) {
        try {
            new GroovyShell().parse(getScript());
        } catch (Exception e) {
            addScriptError(errors, e);
        }
    }

    private void addScriptError(ActionErrors errors, Exception e) {
        errors.add("script", new ActionMessage("itracker.web.error.invalidscriptdata", e.getMessage()));
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
