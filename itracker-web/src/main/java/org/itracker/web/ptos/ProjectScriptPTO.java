package org.itracker.web.ptos;

import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Configuration;
import org.itracker.model.ProjectScript;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.WorkflowUtilities;

import java.util.Locale;

public class ProjectScriptPTO {

    private final ProjectScript script;
    private final Locale locale;

    public ProjectScriptPTO(ProjectScript script, Locale locale) {
        this.script = script;
        this.locale = locale;
    }

    public String getFieldName() {
        if (script.getFieldType() == Configuration.Type.customfield)
            return CustomFieldUtilities.getCustomFieldName(this.script.getFieldId(), locale);
        else {
            return ITrackerResources.getString("itracker.web.attr." + script.getFieldType().name());
        }
    }
    public Boolean isBuiltinField() {
        return script.getFieldType() == Configuration.Type.customfield;
    }

    public String getEventName() {
        return WorkflowUtilities.getEventName(script.getScript().getEvent(), locale);
    }

    @Deprecated
    public ProjectScript getVO() {
        return this.script;
    }

    public ProjectScript getScript() {
        return this.script;
    }

}
