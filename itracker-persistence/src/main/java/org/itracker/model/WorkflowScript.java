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

package org.itracker.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A BeanShell script to execute on a particular event.
 * <p> The script is interpreted on it's event fired<br />
 * in the environment there is available:</p>
 * <ul> <li>event (event type-id)
 * </li><li>fieldId (is a field id associated with event)
 * </li><li>currentValue (is a set of current values)
 * </li><li>currentErrors (is a container for occurred errors)
 * </li><li>currentForm (is a form instance, holding values)
 * </li> </ul>
 * <p>
 * currentValue will be applied to the currentForm, property field</p>
 * </p>
 * <p>
 * This allows to dynamically customize the system by executing custom actions
 * at given extension points where an event is generated.
 * </p>
 * <p>
 * A WorkflowScript needs to be configured to be executed for a particular field
 * of a Project. This configuration is represented as a ProjectScript. <br>
 * WorkflowScript - ProjectScript is a 1-N relationship.
 * </p>
 *
 * @author ready
 * @see ProjectScript
 */
public class WorkflowScript extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public enum ScriptLanguage {
        BeanShell,
        Groovy
    }
    private String name;

    private String script;

    private int event;

    // TODO: what's the expected type here?
    // private Collection projectFields;
    private int numUses;

    private ScriptLanguage language;
    /*
      * This class used to have a <code>projectFields</code> attribute, which
      * was a Collection<ProjectScript>. This has been removed because the
      * association WorkflowScript - ProjectScript doesn't need to be navigatable
      * in this direction.
      */

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
      * public Collection getProjectFields() { return projectFields; }
      *
      * public void setProjectFields(Collection projectFields) {
      * this.projectFields = projectFields; }
      */
    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }


    public ScriptLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ScriptLanguage language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", getName())
                .append("event", getEvent()).append("language", getLanguage()).append(
                        "script", getScript()).toString();
    }

}
