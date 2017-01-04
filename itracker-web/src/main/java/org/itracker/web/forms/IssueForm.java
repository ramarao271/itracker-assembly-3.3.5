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

import bsh.EvalError;
import bsh.Interpreter;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.itracker.IssueException;
import org.itracker.WorkflowException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.*;
import org.itracker.services.*;
import org.itracker.web.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * This form is by the struts actions to pass issue data.
 */
public class IssueForm extends ITrackerForm {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(IssueForm.class);

    private Integer id = null;
    private String caller = null;
    private Integer projectId = null;
    private Integer creatorId = null;
    private Integer ownerId = null;
    private String description = null;
    private Integer severity = null;
    private Integer status = null;
    private Integer prevStatus = null;
    private String resolution = null;
    private Integer targetVersion = null;
    private Integer[] components = new Integer[0];
    private Integer[] versions = new Integer[0];
    private String attachmentDescription = null;
    transient private FormFile attachment = null;
    private String history = null;
    // lets try to put Integer,String here:
    private HashMap<String, String> customFields = new HashMap<>();
    private IssueRelation.Type relationType = null;
    private Integer relatedIssueId = null;

    /**
     * The most general way to run scripts. All matching of event and fields
     * are embedded within. As a result, optionValues parameter will
     * contain updated values and form will contain new default values
     * if appropriate.
     *
     * @param projectScriptModels is a list of scripts.
     * @param event               is an event type.
     * @param currentValues       values mapped to field-ids
     * @param optionValues        is a map of current values to fields by field-Id.
     * @param currentErrors       is a container for errors.
     */
    public void processFieldScripts(List<ProjectScript> projectScriptModels, int event, Map<Integer, String> currentValues, Map<Integer, List<NameValuePair>> optionValues, ActionMessages currentErrors) throws WorkflowException {
        if ((!isWorkflowScriptsAllowed()) || projectScriptModels == null || projectScriptModels.size() == 0)
            return;
        log.debug("Processing " + projectScriptModels.size() + " field scripts for project " + projectScriptModels.get(0).getProject().getId());

        List<ProjectScript> scriptsToRun = new ArrayList<>(projectScriptModels.size());
        for (ProjectScript model : projectScriptModels) {
            if (model.getScript().getEvent() == event) {
                scriptsToRun.add(model);
            }
        }
        // order the scripts by priority
        Collections.sort(scriptsToRun, ProjectScript.PRIORITY_COMPARATOR);

        if (log.isDebugEnabled()) {
            log.debug(scriptsToRun.size() + " eligible scripts found for event " + event);
        }

        String currentValue;
        for (ProjectScript currentScript : scriptsToRun) {
            try {
                currentValue = currentValues.get((currentScript.getFieldType() == Configuration.Type.customfield?
                        currentScript.getFieldId():currentScript.getFieldType().getLegacyCode()));
                log.debug("Running script " + currentScript.getScript().getId()
                        + " with priority " + currentScript.getPriority());

                log.debug("Before script current value for field " + IssueUtilities.getFieldName(currentScript.getFieldId())
                        + " (" + currentScript.getFieldId() + ") is "
                        + currentValue + "'");

                List<NameValuePair> options;
                if (currentScript.getFieldType() == Configuration.Type.customfield) {
                    options = optionValues.get(currentScript.getFieldId());
                    if (null == options) {
                        options = Collections.emptyList();
                        optionValues.put(currentScript.getFieldId(), options);
                    }
                } else {
                    if (!optionValues.containsKey(currentScript.getFieldType().getLegacyCode())){
                        options = Collections.emptyList();
                        optionValues.put(currentScript.getFieldType().getLegacyCode(), options);
                    } else {
                        options = optionValues.get(currentScript.getFieldType().getLegacyCode());
                    }
                }

                currentValue = processFieldScript(currentScript, event,
                        currentValue,
                        options, currentErrors);
                currentValues.put( (currentScript.getFieldType() == Configuration.Type.customfield?
                        currentScript.getFieldId():currentScript.getFieldType().getLegacyCode()),
                        currentValue );


                log.debug("After script current value for field " + IssueUtilities.getFieldName(currentScript.getFieldId())
                        + " (" + currentScript.getFieldId() + ") is "
                        + currentValue + "'");

            } catch (WorkflowException we) {
                log.error("Error processing script ", we);
                currentErrors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system.message", we.getMessage(), "Workflow"));
            }
        }


        // apply new values
        for (ProjectScript script: projectScriptModels) {
            if (script.getScript().getEvent() == event) {
                final String val;
                switch (script.getFieldType()) {
                    case status:
                        val = currentValues.get(Configuration.Type.status.getLegacyCode());
                        try {
                            setStatus(Integer.valueOf(val));
                        } catch (RuntimeException re) {/* OK */}
                        break;
                    case severity:
                        val = currentValues.get(Configuration.Type.severity.getLegacyCode());
                        try {
                            setSeverity(Integer.valueOf(val));
                        } catch (RuntimeException re) {/* OK */}
                        break;
                    case resolution:
                        val = currentValues.get(Configuration.Type.resolution.getLegacyCode());
                        setResolution(val);
                        break;
                    case customfield:
                        getCustomFields().put(String.valueOf(script.getFieldId()), currentValues.get(script.getFieldId()));
                        break;
                    default:
                        log.warn("unsupported field type in script: " + script.getFieldType() + " in project " + script.getProject().getName());
                        break;
                }
            }
        }
    }


    /**
     * Run provided BEANSHELL script against form instance, taking into account
     * incoming event type, field raised an event and current values.
     * As a result, a set of new current values is returned and if
     * appropriate, default values are changed in form.
     * TODO: should issue, project, user, services be available too?
     *
     * @param projectScript is a script to run.
     * @param event         is an event type.
     * @param currentValue  the current field value
     * @param optionValues  is a set of valid option-values.
     * @param currentErrors is a container for occured errors.
     * @return new changed currentValue.
     */
    public String processFieldScript(ProjectScript projectScript, int event, String currentValue, List<NameValuePair> optionValues, ActionMessages currentErrors) throws WorkflowException {
        if (projectScript == null) {
            throw new WorkflowException("ProjectScript was null.", WorkflowException.INVALID_ARGS);
        }
        if (currentErrors == null) {
            throw new WorkflowException("Errors was null.", WorkflowException.INVALID_ARGS);
        }

        if (!isWorkflowScriptsAllowed()) {
            return currentValue;
        }

        String result = currentValue;

        try {
            if (projectScript.getScript().getLanguage() != WorkflowScript.ScriptLanguage.Groovy) {
                result = processBeanShellScript(projectScript, currentValue, optionValues, currentErrors, event);
            } else {
                result = processGroovyScript(projectScript, currentValue, optionValues, currentErrors, event);
            }
            if (log.isDebugEnabled()) {
                log.debug("processFieldScript: Script returned current value of '" + optionValues + "' (" + (optionValues != null ? optionValues.getClass().getName() : "NULL") + ")");
            }
        } catch (EvalError evalError) {
            log.error("processFieldScript: eval failed: " + projectScript, evalError);
            currentErrors.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("itracker.web.error.invalidscriptdata", evalError.getMessage()));
        } catch (RuntimeException e) {
            log.warn("processFieldScript: Error processing field script: " + projectScript, e);
            currentErrors.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("itracker.web.error.system.message",
                            new Object[]{
                                    e.getMessage(),
                                    ITrackerResources.getString("itracker.web.attr.script") // Script
                            }));
        }
        if (log.isDebugEnabled()) {
            log.debug("processFieldScript: returning " + result + ", errors: " + currentErrors);
        }
        return result;
    }

    private String processGroovyScript(final ProjectScript projectScript,
                                       final String currentValue,
                                       final List<NameValuePair> optionValues,
                                       final ActionMessages currentErrors,
                                       final int event) {

        final Map<String,Object> ctx = new HashMap<>(8);
        ctx.put("currentValue", StringUtils.defaultString(currentValue));
        ctx.put("event", event);
        ctx.put("fieldId", (projectScript.getFieldType() == Configuration.Type.customfield ?
                projectScript.getFieldId() : projectScript.getFieldType().getLegacyCode()));

        ctx.put("optionValues", Collections.unmodifiableList(optionValues));
        ctx.put("currentErrors", currentErrors);
        ctx.put("currentForm", this);

        final Binding binding = new Binding(ctx);

        GroovyShell shell = new GroovyShell();
        Script script = shell.parse(projectScript.getScript().getScript(),
                        projectScript.getScript().getName());
        script.setBinding(binding);
        Object ret = script.run();
        if (!currentErrors.isEmpty()) {
            return currentValue;
        }
        return returnScriptResult(ret, ctx.get("currentValue"), currentValue);
    }

    private String processBeanShellScript(final ProjectScript projectScript,
                                          final String currentValue,
                                          final List<NameValuePair> optionValues,
                                          final ActionMessages currentErrors,
                                          final int event) throws EvalError {
        Interpreter bshInterpreter = new Interpreter();
        bshInterpreter.set("event", event);
        bshInterpreter.set("fieldId", (projectScript.getFieldType()== Configuration.Type.customfield?
            projectScript.getFieldId():projectScript.getFieldType().getLegacyCode()));
        bshInterpreter.set("currentValue", StringUtils.defaultString(currentValue));
        bshInterpreter.set("optionValues", optionValues);
        bshInterpreter.set("currentErrors", currentErrors);
        bshInterpreter.set("currentForm", this);

        Object obj = bshInterpreter.eval(projectScript.getScript().getScript());
        if (!currentErrors.isEmpty()) {
            return currentValue;
        }
        return returnScriptResult(obj, bshInterpreter.get("currentValue"), currentValue);
    }

    private static String returnScriptResult(Object returned, Object assigned, String currentValue) {
        if (! (returned instanceof CharSequence)) {
            log.debug("script did not return a value");
            returned = assigned;
        }
        if (returned instanceof CharSequence) {
            return String.valueOf(returned);
        }
        log.debug("failed to get value from script, returning previous value");
        return currentValue;
    }

    public final Issue processFullEdit(Issue issue, Project project, User user,
                                              Map<Integer, Set<PermissionType>> userPermissions, Locale locale,
                                              IssueService issueService, ActionMessages errors) throws Exception {
        int previousStatus = issue.getStatus();
        boolean needReloadIssue;
        ActionMessages msg = new ActionMessages();
        issue = addAttachment(issue, project, user, getITrackerServices(), msg);

        if (!msg.isEmpty()) {
            // Validation of attachment failed
            errors.add(msg);
            return issue;
        }

        needReloadIssue = issueService.setIssueVersions(issue.getId(),
                new HashSet<>(Arrays.asList(getVersions())),
                user.getId());

        needReloadIssue = needReloadIssue | issueService.setIssueComponents(issue.getId(),
                new HashSet<>(Arrays.asList(getComponents())),
                user.getId());

        // reload issue for further updates
        if (needReloadIssue) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: updating issue from session: " + issue);
            }
            issue = issueService.getIssue(issue.getId());
        }

        Integer targetVersion = getTargetVersion();
        if (targetVersion != null && targetVersion != -1) {
            ProjectService projectService = ServletContextUtils.getItrackerServices()
                    .getProjectService();
            Version version = projectService.getProjectVersion(targetVersion);
            if (version == null) {
                throw new RuntimeException("No version with Id "
                        + targetVersion);
            }
            issue.setTargetVersion(version);
        }

        issue.setResolution(getResolution());
        issue.setSeverity(getSeverity());

        applyLimitedFields(issue, project, user, userPermissions, locale, issueService);

        Integer formStatus = getStatus();
        issue.setStatus(formStatus);
        if (formStatus != null) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: processing status: " + formStatus);
            }
            if (previousStatus != -1) {
                // Reopened the issue. Reset the resolution field.
                if ((previousStatus >= IssueUtilities.STATUS_ASSIGNED && previousStatus < IssueUtilities.STATUS_RESOLVED)
                        && (previousStatus >= IssueUtilities.STATUS_RESOLVED && previousStatus < IssueUtilities.STATUS_END)) {
                    issue.setResolution("");
                }

                if (previousStatus >= IssueUtilities.STATUS_CLOSED
                        && !UserUtilities.hasPermission(userPermissions, project
                        .getId(), UserUtilities.PERMISSION_CLOSE)) {
                    if (previousStatus < IssueUtilities.STATUS_CLOSED) {
                        issue.setStatus(previousStatus);
                    } else {
                        issue.setStatus(IssueUtilities.STATUS_RESOLVED);
                    }
                }

                if (issue.getStatus() < IssueUtilities.STATUS_NEW
                        || issue.getStatus() >= IssueUtilities.STATUS_END) {
                    issue.setStatus(previousStatus);
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
                    && !UserUtilities.hasPermission(userPermissions, project
                    .getId(), PermissionType.ISSUE_CLOSE)) {
                issue.setStatus(IssueUtilities.STATUS_RESOLVED);
            }
        }

        if (issue.getStatus() < IssueUtilities.STATUS_NEW) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: status < STATUS_NEW: " + issue.getStatus());
            }
            issue.setStatus(IssueUtilities.STATUS_NEW);
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: updated to: " + issue.getStatus());
            }
        } else if (issue.getStatus() >= IssueUtilities.STATUS_END) {
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: status >= STATUS_END: " + issue.getStatus());
            }
            if (!UserUtilities.hasPermission(userPermissions, project.getId(),
                    PermissionType.ISSUE_CLOSE)) {
                issue.setStatus(IssueUtilities.STATUS_RESOLVED);
            } else {
                issue.setStatus(IssueUtilities.STATUS_CLOSED);
            }
            if (log.isDebugEnabled()) {
                log.debug("processFullEdit: status updated to: " + issue.getStatus());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("processFullEdit: updating issue " + issue);
        }
        return issueService.updateIssue(issue, user.getId());
    }

    public final void applyLimitedFields(Issue issue, Project project,
                                                User user, Map<Integer, Set<PermissionType>> userPermissionsMap,
                                                Locale locale,  IssueService issueService) throws Exception {

        issue.setDescription(getDescription());

        setIssueFields(issue, user, locale,  issueService);
        setOwner(issue, user, userPermissionsMap);
        addHistoryEntry(issue, user);
    }

    private void setIssueFields(Issue issue, User user, Locale locale,
                                       IssueService issueService) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("setIssueFields: called");
        }
        List<CustomField> projectCustomFields = issue.getProject()
                .getCustomFields();
        if (log.isDebugEnabled()) {
            log.debug("setIssueFields: got project custom fields: " + projectCustomFields);
        }

        if (projectCustomFields == null || projectCustomFields.size() == 0) {
            log.debug("setIssueFields: no custom fields, returning...");
            return;
        }


        // here you see some of the ugly side of Struts 1.3 - the forms... they
        // can only contain Strings and some simple objects types...
        HashMap<String, String> formCustomFields = getCustomFields();

        if (log.isDebugEnabled()) {
            log.debug("setIssueFields: got form custom fields: " + formCustomFields);
        }

        if (formCustomFields == null || formCustomFields.size() == 0) {
            log.debug("setIssueFields: no form custom fields, returning..");
            return;
        }

        ResourceBundle bundle = ITrackerResources.getBundle(locale);
        Iterator<CustomField> customFieldsIt = projectCustomFields.iterator();
        // declare iteration fields
        CustomField field;
        String fieldValue;
        IssueField issueField;
        try {
            if (log.isDebugEnabled()) {
                log.debug("setIssueFields: processing project fields");
            }
            // set values to issue-fields and add if needed
            while (customFieldsIt.hasNext()) {

                field = customFieldsIt.next();
                fieldValue = (String) formCustomFields.get(String.valueOf(field
                        .getId()));

                // remove the existing field for re-setting
                issueField = getIssueField(issue, field);


                if (fieldValue != null && fieldValue.trim().length() > 0) {
                    if (null == issueField) {
                        issueField = new IssueField(issue, field);
                        issue.getFields().add(issueField);
                    }

                    issueField.setValue(fieldValue, bundle);
                } else {
                    if (null != issueField) {
                        issue.getFields().remove(issueField);
                    }
                }
            }

            // set new issue fields for later saving
//			issue.setFields(issueFieldsList);

//			issueService.setIssueFields(issue.getId(), issueFieldsList);
        } catch (Exception e) {
            log.error("setIssueFields: failed to process custom fields", e);
            throw e;
        }
    }

    private static IssueField getIssueField(Issue issue, CustomField field) {
        Iterator<IssueField> it = issue.getFields().iterator();
        IssueField issueField;
        while (it.hasNext()) {
            issueField = it.next();
            if (issueField.getCustomField().equals(field)) {
                return issueField;
            }
        }
        return null;

    }

    private void setOwner(Issue issue, User user,
                                 Map<Integer, Set<PermissionType>> userPermissionsMap) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("setOwner: called to " + getOwnerId());
        }
        Integer currentOwner = (issue.getOwner() == null) ? null : issue
                .getOwner().getId();

        Integer ownerId = getOwnerId();

        if (ownerId == null || ownerId.equals(currentOwner)) {
            if (log.isDebugEnabled()) {
                log.debug("setOwner: returning, existing owner is the same: " + issue.getOwner());
            }
            return;
        }

        if (UserUtilities.hasPermission(userPermissionsMap,
                UserUtilities.PERMISSION_ASSIGN_OTHERS)
                || (UserUtilities.hasPermission(userPermissionsMap,
                UserUtilities.PERMISSION_ASSIGN_SELF) && user.getId()
                .equals(ownerId))
                || (UserUtilities.hasPermission(userPermissionsMap,
                UserUtilities.PERMISSION_UNASSIGN_SELF)
                && user.getId().equals(currentOwner) && ownerId == -1)) {
            User newOwner = ServletContextUtils.getItrackerServices().getUserService().getUser(ownerId);
            if (log.isDebugEnabled()) {
                log.debug("setOwner: setting new owner " + newOwner + " to " + issue);
            }
            issue.setOwner(newOwner);
//			issueService.assignIssue(issue.getId(), ownerId, user.getId());
        }

    }

    private void addHistoryEntry(Issue issue, User user) throws Exception {
        try {
            String history = getHistory();

            if (history == null || history.equals("")) {
                if (log.isDebugEnabled()) {
                    log.debug("addHistoryEntry: skip history to " + issue);
                }
                return;
            }


            if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, issue.getProject().getOptions())) {
                history = HTMLUtilities.removeMarkup(history);
            } else if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML, issue.getProject().getOptions())) {
                history = HTMLUtilities.escapeTags(history);
            } else {
                history = HTMLUtilities.newlinesToBreaks(history);
            }


            if (log.isDebugEnabled()) {
                log.debug("addHistoryEntry: adding history to " + issue);
            }
            IssueHistory issueHistory = new IssueHistory(issue, user, history,
                    IssueUtilities.HISTORY_STATUS_AVAILABLE);

            issueHistory.setDescription(getHistory());
            issueHistory.setCreateDate(new Date());

            issueHistory.setLastModifiedDate(new Date());
            issue.getHistory().add(issueHistory);


//  TODO why do we need to updateIssue here, and can not later?
//			issueService.updateIssue(issue, user.getId());
        } catch (Exception e) {
            log.error("addHistoryEntry: failed to add", e);
            throw e;
        }
//		issueService.addIssueHistory(issueHistory);
        if (log.isDebugEnabled()) {
            log.debug("addHistoryEntry: added history for issue " + issue);
        }
    }

    public final Issue processLimitedEdit(Issue issue, Project project,
                                                 User user, Map<Integer, Set<PermissionType>> userPermissionsMap,
                                                 Locale locale, IssueService issueService, ActionMessages messages)
            throws Exception {
        ActionMessages msg = new ActionMessages();
        issue = addAttachment(issue, project, user, ServletContextUtils.getItrackerServices(), msg);

        if (!msg.isEmpty()) {
            messages.add(msg);
            return issue;
        }

        Integer formStatus = getStatus();

        if (formStatus != null) {

            if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                    && formStatus >= IssueUtilities.STATUS_CLOSED
                    && UserUtilities.hasPermission(userPermissionsMap,
                    UserUtilities.PERMISSION_CLOSE)) {

                issue.setStatus(formStatus);
            }

        }

        applyLimitedFields(issue, project, user, userPermissionsMap, locale, issueService);
        return issueService.updateIssue(issue, user.getId());

    }

    /**
     * method needed to prepare request for edit_issue.jsp
     */
    public static void setupJspEnv(ActionMapping mapping,
                                         IssueForm issueForm, HttpServletRequest request, Issue issue,
                                         IssueService issueService, UserService userService,
                                         Map<Integer, Set<PermissionType>> userPermissions,
                                         Map<Integer, List<NameValuePair>> listOptions, ActionMessages errors)
            throws ServletException, IOException, WorkflowException {

        if (log.isDebugEnabled()) {
            log.debug("setupJspEnv: for issue " + issue);
        }

        NotificationService notificationService = ServletContextUtils
                .getItrackerServices().getNotificationService();
        String pageTitleKey = "itracker.web.editissue.title";
        String pageTitleArg = request.getParameter("id");
        Locale locale = LoginUtilities.getCurrentLocale(request);
        User um = LoginUtilities.getCurrentUser(request);
        List<NameValuePair> statuses = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_STATUS);
        String statusName = IssueUtilities.getStatusName(issue.getStatus(), locale);
        boolean hasFullEdit = UserUtilities.hasPermission(userPermissions,
                issue.getProject().getId(), UserUtilities.PERMISSION_EDIT_FULL);
        List<NameValuePair> resolutions = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_RESOLUTION);
        String severityName = IssueUtilities.getSeverityName(issue
                .getSeverity(), locale);
        List<NameValuePair> components = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_COMPONENTS);
        List<NameValuePair> versions = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_VERSIONS);
        List<NameValuePair> targetVersion = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_TARGET_VERSION);
        List<Component> issueComponents = issue.getComponents();
        Collections.sort(issueComponents);
        List<Version> issueVersions = issue.getVersions();
        Collections.sort(issueVersions, new Version.VersionComparator());
        /* Get project fields and put name and value in map */
        setupProjectFieldsMapJspEnv(issue.getProject().getCustomFields(), issue.getFields(), request);

        setupRelationsRequestEnv(issue.getRelations(), request);


        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);
        request.getSession().setAttribute(Constants.LIST_OPTIONS_KEY,
                listOptions);
        request.setAttribute("targetVersions", targetVersion);
        request.setAttribute("components", components);
        request.setAttribute("versions", versions);
        request.setAttribute("hasIssueNotification", notificationService
                .hasIssueNotification(issue, um.getId()));
        request.setAttribute("hasHardIssueNotification", IssueUtilities.hasHardNotification(issue, issue.getProject(), um.getId()));
        request.setAttribute("hasEditIssuePermission", UserUtilities
                .hasPermission(userPermissions, issue.getProject().getId(),
                        UserUtilities.PERMISSION_EDIT));
        request.setAttribute("canCreateIssue",
                issue.getProject().getStatus() == Status.ACTIVE
                        && UserUtilities.hasPermission(userPermissions, issue
                        .getProject().getId(),
                        UserUtilities.PERMISSION_CREATE));
        request.setAttribute("issueComponents", issueComponents);
        request.setAttribute("issueVersions",
                issueVersions == null ? new ArrayList<Version>()
                        : issueVersions);
        request.setAttribute("statuses", statuses);
        request.setAttribute("statusName", statusName);
        request.setAttribute("hasFullEdit", hasFullEdit);
        request.setAttribute("resolutions", resolutions);
        request.setAttribute("severityName", severityName);
        request.setAttribute("hasPredefinedResolutionsOption", ProjectUtilities
                .hasOption(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS,
                        issue.getProject().getOptions()));
        request.setAttribute("issueOwnerName",
                (issue.getOwner() == null ? ITrackerResources.getString(
                        "itracker.web.generic.unassigned", locale)
                        : issue.getOwner().getFirstName() + " "
                        + issue.getOwner().getLastName()));
        request.setAttribute("isStatusResolved",
                issue.getStatus() >= IssueUtilities.STATUS_RESOLVED);


        request.setAttribute("fieldSeverity", WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_SEVERITY));
        request.setAttribute("possibleOwners", WorkflowUtilities
                .getListOptions(listOptions, IssueUtilities.FIELD_OWNER));

        request.setAttribute("hasNoViewAttachmentOption", ProjectUtilities
                .hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS, issue
                        .getProject().getOptions()));

        if (log.isDebugEnabled()) {
            log.debug("setupJspEnv: options " + issue.getProject().getOptions() + ", hasNoViewAttachmentOption: " + request.getAttribute("hasNoViewAttachmentOption"));
        }

        setupNotificationsInRequest(request, issue, notificationService);

        // setup issue to request, as it's needed by the jsp.
        request.setAttribute(Constants.ISSUE_KEY, issue);
        request.setAttribute("issueForm", issueForm);
        request.setAttribute(Constants.PROJECT_KEY, issue.getProject());
        List<IssueHistory> issueHistory = issueService.getIssueHistory(issue
                .getId());
        Collections.sort(issueHistory, IssueHistory.CREATE_DATE_COMPARATOR);
        request.setAttribute("issueHistory", issueHistory);


    }

    /**
     * Get project fields and put name and value in map
     * TODO: simplify this code, it's not readable, unsave yet.
     */
    public static final void setupProjectFieldsMapJspEnv(List<CustomField> projectFields, Collection<IssueField> issueFields, HttpServletRequest request) {
        Map<CustomField, String> projectFieldsMap = new HashMap<CustomField, String>();

        if (projectFields != null && projectFields.size() > 0) {
            Collections.sort(projectFields, CustomField.ID_COMPARATOR);

            HashMap<String, String> fieldValues = new HashMap<String, String>();
            Iterator<IssueField> issueFieldsIt = issueFields.iterator();
            while (issueFieldsIt.hasNext()) {
                IssueField issueField = issueFieldsIt.next();

                if (issueField.getCustomField() != null
                        && issueField.getCustomField().getId() > 0) {
                    if (issueField.getCustomField().getFieldType() == CustomField.Type.DATE) {
                        Locale locale = LoginUtilities.getCurrentLocale(request);
                        String value = issueField.getValue(locale);
                        fieldValues.put(issueField.getCustomField().getId()
                                .toString(), value);
                    } else {
                        fieldValues.put(issueField.getCustomField().getId()
                                .toString(), issueField
                                .getStringValue());
                    }
                }
            }
            Iterator<CustomField> fieldsIt = projectFields.iterator();
            CustomField field;
            while (fieldsIt.hasNext()) {

                field = fieldsIt.next();

                String fieldValue = fieldValues.get(String.valueOf(field
                        .getId()));
                if (null == fieldValue) {
                    fieldValue = "";
                };
                projectFieldsMap.put(field, fieldValue);

            }

            request.setAttribute("projectFieldsMap", projectFieldsMap);
        }
    }

    protected static void setupRelationsRequestEnv(List<IssueRelation> relations, HttpServletRequest request) {
        Collections.sort(relations, IssueRelation.LAST_MODIFIED_DATE_COMPARATOR);
        request.setAttribute("issueRelations", relations);

    }

    public static void setupNotificationsInRequest(
            HttpServletRequest request, Issue issue,
            NotificationService notificationService) {
        List<Notification> notifications = notificationService
                .getIssueNotifications(issue);

        Collections.sort(notifications, Notification.TYPE_COMPARATOR);

        request.setAttribute("notifications", notifications);
        Map<User, Set<Notification.Role>> notificationMap = NotificationUtilities
                .mappedRoles(notifications);
        request.setAttribute("notificationMap", notificationMap);
        request.setAttribute("notifiedUsers", notificationMap.keySet());
    }

    /**
     * Adds an attachment to issue.
     *
     * @return updated issue
     */
    public Issue addAttachment(Issue issue, Project project, User user,
                                       ITrackerServices services, ActionMessages messages) {


        FormFile file = getAttachment();

        if (file == null || file.getFileName().trim().length() < 1) {
            log.info("addAttachment: skipping file " + file);
            return issue;
        }

        if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_NO_ATTACHMENTS,
                project.getOptions())) {
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.validate.attachment.disabled", project.getName()));
            return issue;
        }

        String origFileName = file.getFileName();
        String contentType = file.getContentType();
        int fileSize = file.getFileSize();

        String attachmentDescription = getAttachmentDescription();

        if (null == contentType || 0 >= contentType.length()) {
            log.info("addAttachment: got no mime-type, using default plain-text");
            contentType = "text/plain";
        }

        if (log.isDebugEnabled()) {
            log.debug("addAttachment: adding file, name: " + origFileName
                    + " of type " + file.getContentType() + ", description: "
                    + getAttachmentDescription() + ". filesize: " + fileSize);
        }
        ActionMessages validation = AttachmentUtilities.validate(file, services);
        if (validation.isEmpty()) {

//		if (AttachmentUtilities.checkFile(file, getITrackerServices())) {
            int lastSlash = Math.max(origFileName.lastIndexOf('/'),
                    origFileName.lastIndexOf('\\'));
            if (lastSlash > -1) {
                origFileName = origFileName.substring(lastSlash + 1);
            }

            IssueAttachment attachmentModel = new IssueAttachment(issue,
                    origFileName, contentType, attachmentDescription, fileSize,
                    user);

            attachmentModel.setIssue(issue);
//			issue.getAttachments().add(attachmentModel);
            byte[] fileData;
            try {
                fileData = file.getFileData();
            } catch (IOException e) {
                log.error("addAttachment: failed to get file data", e);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
                return issue;
            }
            if (services.getIssueService()
                    .addIssueAttachment(attachmentModel, fileData)) {
                return services.getIssueService().getIssue(issue.getId());
            }


        } else {
            if (log.isDebugEnabled()) {
                log.debug("addAttachment: failed to validate: " + origFileName + ", " + validation);
            }
            messages.add(validation);
        }
        return issue;
    }

    public final void setupIssueForm(Issue issue,
                                            final Map<Integer, List<NameValuePair>> listOptions,
                                            HttpServletRequest request, ActionMessages errors)
            throws WorkflowException {
        HttpSession session = request.getSession(true);

        IssueService issueService = ServletContextUtils.getItrackerServices()
                .getIssueService();
        Locale locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
        setId(issue.getId());
        setProjectId(issue.getProject().getId());
        setPrevStatus(issue.getStatus());
        setCaller(request.getParameter("caller"));

        setDescription(HTMLUtilities.handleQuotes(issue
                .getDescription()));
        setStatus(issue.getStatus());

        if (!ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS, issue
                .getProject().getOptions())) {
            // TODO What happens here, validation?
            try {
                issue.setResolution(IssueUtilities.checkResolutionName(issue
                        .getResolution(), locale));
            } catch (MissingResourceException | NumberFormatException mre) {
                log.error(mre.getMessage());
            }
        }

        setResolution(HTMLUtilities.handleQuotes(issue
                .getResolution()));
        setSeverity(issue.getSeverity());

        setTargetVersion(issue.getTargetVersion() == null ? -1
                : issue.getTargetVersion().getId());

        setOwnerId((issue.getOwner() == null ? -1 : issue.getOwner()
                .getId()));

        List<IssueField> fields = issue.getFields();
        HashMap<String, String> customFields = new HashMap<String, String>();
        for (IssueField field : fields) {
            customFields.put(field.getCustomField().getId().toString(),
                    field.getValue(locale));
        }

        setCustomFields(customFields);

        HashSet<Integer> selectedComponents = issueService
                .getIssueComponentIds(issue.getId());
        if (selectedComponents != null) {
            Integer[] componentIds;
            ArrayList<Integer> components = new ArrayList<>(
                    selectedComponents);
            componentIds = components.toArray(new Integer[components.size()]);
            setComponents(componentIds);
        }

        HashSet<Integer> selectedVersions = issueService
                .getIssueVersionIds(issue.getId());
        if (selectedVersions != null) {
            Integer[] versionIds;
            ArrayList<Integer> versions = new ArrayList<>(
                    selectedVersions);
            versionIds = versions.toArray(new Integer[versions.size()]);
            setVersions(versionIds);
        }

        invokeProjectScripts(issue.getProject(), WorkflowUtilities.EVENT_FIELD_ONPOPULATE, listOptions, errors);

    }

    public void invokeProjectScripts(Project project, int event, final Map<Integer, List<NameValuePair>> options, ActionMessages errors)
            throws WorkflowException {
        final Map<Integer, String> values = new HashMap<>(options.size());
        for (CustomField field: project.getCustomFields()) {
            values.put(field.getId()
                    , getCustomFields().get(String.valueOf(field.getId())));
        }
        values.put(Configuration.Type.status.getLegacyCode(),
                String.valueOf(getStatus()));
        values.put(Configuration.Type.severity.getLegacyCode(),
                String.valueOf(getSeverity()));
        values.put(Configuration.Type.resolution.getLegacyCode(),
                getResolution());

        processFieldScripts(project.getScripts(),
                event, values, options, errors);

    }

    public  Map<Integer, List<NameValuePair>> invokeProjectScripts(Project project, int event, ActionMessages errors)
            throws WorkflowException {

        final Map<Integer, List<NameValuePair>> options = EditIssueActionUtil.mappedFieldOptions(project.getCustomFields()) ;
        invokeProjectScripts(project, event, options, errors);
        return options;
    }

    public FormFile getAttachment() {
        return attachment;
    }

    public void setAttachment(FormFile attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentDescription() {
        return attachmentDescription;
    }

    public void setAttachmentDescription(String attachmentDescription) {
        this.attachmentDescription = attachmentDescription;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public Integer[] getComponents() {
        if (null == components)
            return null;
        return components.clone();
    }

    public void setComponents(Integer[] components) {
        if (null == components)
            this.components = null;
        else
            this.components = components.clone();
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    // let's try to put Integer,String here:
    public HashMap<String, String> getCustomFields() {
        return customFields;
    }

    // let's try to put Integer,String here:
    public void setCustomFields(HashMap<String, String> customFields) {
        this.customFields = customFields;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getPrevStatus() {
        return prevStatus;
    }

    public void setPrevStatus(Integer prevStatus) {
        this.prevStatus = prevStatus;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getRelatedIssueId() {
        return relatedIssueId;
    }

    public void setRelatedIssueId(Integer relatedIssueId) {
        this.relatedIssueId = relatedIssueId;
    }

    public IssueRelation.Type getRelationType() {
        return relationType;
    }

    public void setRelationType(IssueRelation.Type relationType) {
        this.relationType = relationType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(Integer targetVersion) {
        this.targetVersion = targetVersion;
    }

    public Integer[] getVersions() {
        if (null == versions)
            return null;
        return versions.clone();
    }

    public void setVersions(Integer[] versions) {
        if (null == versions)
            this.versions = null;
        else
            this.versions = versions.clone();
    }

    /**
     * This methods adds in validation for custom fields. It makes sure the
     * datatype matches and also that all required fields have been populated.
     *
     * @param mapping the ActionMapping object
     * @param request the current HttpServletRequest object
     * @return an ActionErrors object containing any validation errors
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("validate called: mapping: " + mapping + ", request: "
                    + request);
        }
        ActionErrors errors = super.validate(mapping, request);

        if (log.isDebugEnabled()) {
            log.debug("validate called: mapping: " + mapping + ", request: "
                    + request + ", errors: " + errors);
        }

        try {
            if (null != getId()) {
                Issue issue;
                try {
                    issue = getITrackerServices().getIssueService().getIssue(
                            getId());
                } catch (Exception e) {
                    return errors;
                }

                Locale locale = (Locale) request.getSession().getAttribute(
                        Constants.LOCALE_KEY);
                User currUser = (User) request.getSession().getAttribute(
                        Constants.USER_KEY);
                List<NameValuePair> ownersList = EditIssueActionUtil
                        .getAssignableIssueOwnersList(issue,
                                issue.getProject(), currUser, locale,
                                getITrackerServices().getUserService(),
                                RequestHelper.getUserPermissions(request
                                        .getSession()));

                setupJspEnv(mapping, this, request, issue,
                        getITrackerServices().getIssueService(),
                        getITrackerServices().getUserService(), RequestHelper
                        .getUserPermissions(request.getSession()),
                        EditIssueActionUtil.getListOptions(request, issue,
                                ownersList, RequestHelper
                                .getUserPermissions(request
                                        .getSession()), issue
                                .getProject(), currUser), errors);

                if (errors.isEmpty() && issue.getProject() == null) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate: issue project is null: " + issue);
                    }
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(
                                    "itracker.web.error.invalidproject"));
                } else if (errors.isEmpty()
                        && issue.getProject().getStatus() != Status.ACTIVE) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate: issue project is not active: " + issue);
                    }
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(
                                    "itracker.web.error.projectlocked"));
                } else if (errors.isEmpty() && !"editIssueForm".equals(mapping.getName())) {
                    if (log.isDebugEnabled()) {
                        log.debug("validate: validation had errors for " + issue + ": " + errors);
                    }

                    if (UserUtilities.hasPermission(RequestHelper.getUserPermissions(request.getSession()),
                            issue.getProject().getId(),
                            UserUtilities.PERMISSION_EDIT_FULL)) {
                        validateProjectFields(issue.getProject(), request, errors);
                    }


                    validateProjectScripts(issue.getProject(), errors);
                    validateAttachment(this.getAttachment(), getITrackerServices(), errors);
                }
            } else {
                EditIssueActionUtil.setupCreateIssue(request);
                HttpSession session = request.getSession();
                Project project = (Project) session
                        .getAttribute(Constants.PROJECT_KEY);
                if (log.isDebugEnabled()) {
                    log.debug("validate: validating create new issue for project: " + page);
                }
                validateProjectFields(project, request, errors);
                validateProjectScripts(project, errors);
                validateAttachment(this.getAttachment(), getITrackerServices(), errors);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("validate: unexpected exception", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.system"));
        }
        if (log.isDebugEnabled()) {
            log.debug("validate: returning errors: " + errors);
        }
        return errors;
    }

    private static void validateAttachment(FormFile attachment, ITrackerServices services, ActionMessages errors) {
        if (null != attachment) {
            ActionMessages msg = AttachmentUtilities.validate(attachment, services);
            if (!msg.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("validateAttachment: failed to validate, " + msg);
                }
                errors.add(msg);
            }
        }
    }

    private static void validateProjectFields(Project project,
                                              HttpServletRequest request, ActionErrors errors) {

        List<CustomField> projectFields = project.getCustomFields();
        if (null != projectFields && projectFields.size() > 0) {

            Locale locale = LoginUtilities.getCurrentLocale(request);

            ResourceBundle bundle = ITrackerResources.getBundle(locale);
            for (CustomField customField : projectFields) {
                String fieldValue = request.getParameter("customFields("
                        + customField.getId() + ")");
                if (fieldValue != null && !fieldValue.equals("")) {

                    // Don't create an IssueField only so that we can call
                    // setValue to validate the value!
                    try {
                        customField.checkAssignable(fieldValue, locale, bundle);
                    } catch (IssueException ie) {
                        String label = CustomFieldUtilities.getCustomFieldName(
                                customField.getId(), locale);
                        errors.add(ActionMessages.GLOBAL_MESSAGE,
                                new ActionMessage(ie.getType(), label));
                    }
                } else if (customField.isRequired()) {
                    String label = CustomFieldUtilities.getCustomFieldName(
                            customField.getId(), locale);
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                            new ActionMessage(IssueException.TYPE_CF_REQ_FIELD,
                                    label));
                }
            }
        }
    }

    private void validateProjectScripts(Project project, ActionErrors errors)
            throws WorkflowException {

        invokeProjectScripts(project, WorkflowUtilities.EVENT_FIELD_ONVALIDATE, errors);

    }

    public static boolean isWorkflowScriptsAllowed() {
        Boolean val = ServletContextUtils.getItrackerServices().getConfigurationService().getBooleanProperty("allow_workflowscripts", true);
        if (log.isDebugEnabled()) {
            log.debug("isWorkflowScriptsAllowed: {}allowed by configuration 'allow_workflowscripts'", !val?"NOT ":"");
        }
        return val;
    }

}
