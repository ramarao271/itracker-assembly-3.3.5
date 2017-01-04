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

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.validator.ValidatorForm;
import org.itracker.model.*;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.ProjectUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ITrackerServices;
import org.itracker.services.ProjectService;
import org.itracker.services.UserService;
import org.itracker.web.ptos.ProjectScriptPTO;
import org.itracker.web.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * This is the LoginForm Struts Form. It is used by Login form.
 *
 * @author ready
 */
public class ProjectForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String TITLE_UPDATE = "itracker.web.admin.editproject.title.update";
    public static final String TITLE_CREATE = "itracker.web.admin.editproject.title.create";
    private String action;
    private Integer id;
    private String name;
    private Integer status;
    private String description;
    private Integer[] owners;
    private Integer[] users;
    private Integer[] permissions;
    private Integer[] options;
    private Integer[] fields;

    private static final Logger log = Logger.getLogger(ProjectForm.class);

    public ActionForward init(ActionMapping mapping, HttpServletRequest request) {
        ITrackerServices itrackerServices = ServletContextUtils.getItrackerServices();
        ProjectService projectService = itrackerServices.getProjectService();
        UserService userService = itrackerServices.getUserService();

        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute(Constants.USER_KEY);
        Boolean allowPermissionUpdate = userService.allowPermissionUpdates(user, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);


        final Map<Integer, Set<PermissionType>> permissions = RequestHelper
                .getUserPermissions(session);
        Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
        boolean isUpdate;

        if (project == null) {
            log.info("EditProjectAction: Forward: unauthorized");
            return mapping.findForward("unauthorized");
        } else {
            isUpdate = false;
            if (!project.isNew()) {
                isUpdate = true;
            }
        }
        request.setAttribute("isUpdate", isUpdate);

        setupTitle(request, projectService);


        List<NameValuePair> statuses = new ArrayList<NameValuePair>();
        statuses.add(new NameValuePair(ProjectUtilities.getStatusName(Status.ACTIVE, LoginUtilities.getCurrentLocale(request)), Integer.toString(Status.ACTIVE.getCode())));
        statuses.add(new NameValuePair(ProjectUtilities.getStatusName(Status.VIEWABLE, LoginUtilities.getCurrentLocale(request)), Integer.toString(Status.VIEWABLE.getCode())));
        statuses.add(new NameValuePair(ProjectUtilities.getStatusName(Status.LOCKED, LoginUtilities.getCurrentLocale(request)), Integer.toString(Status.LOCKED.getCode())));
        request.setAttribute("statuses", statuses);

        Set<User> owners = new TreeSet<User>(User.NAME_COMPARATOR);
        if (!project.isNew()) {
            owners.addAll(userService.getUsersWithProjectPermission(project.getId(), PermissionType.ISSUE_VIEW_ALL));
        } else {
            owners.addAll(userService.getSuperUsers());
        }
        owners.addAll(project.getOwners());
        request.setAttribute("owners", owners);

        boolean allowPermissionUpdateOption = allowPermissionUpdate == null ? false
                : allowPermissionUpdate && UserUtilities.hasPermission(permissions, new Integer(-1), PermissionType.USER_ADMIN);
        request.setAttribute("allowPermissionUpdateOption", allowPermissionUpdateOption);

        if (project.isNew()) {
            List<User> users = new ArrayList<User>();
            List<User> activeUsers = userService.getActiveUsers();
            Collections.sort(activeUsers, User.NAME_COMPARATOR);
            for (int i = 0; i < activeUsers.size(); i++) {
                if (owners.contains(activeUsers.get(i))) {
                    continue;
                }
                users.add(activeUsers.get(i));
            }
            request.setAttribute("users", users);
        }


        List<NameValuePair> permissionNames = UserUtilities.getPermissionNames(LoginUtilities.getCurrentLocale(request));
        request.setAttribute("permissions", permissionNames);

        request.setAttribute("optionSupressHistoryHtml", Integer.toString(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML));
        request.setAttribute("optionPredefinedResolutions", Integer.toString(ProjectUtilities.OPTION_PREDEFINED_RESOLUTIONS));
        request.setAttribute("optionAllowAssignToClose", Integer.toString(ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE));
        request.setAttribute("optionAllowSefRegisteredCreate", Integer.toString(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_CREATE));
        request.setAttribute("optionLiteralHistoryHtml", Integer.toString(ProjectUtilities.OPTION_LITERAL_HISTORY_HTML));
        request.setAttribute("optionNoAttachments", Integer.toString(ProjectUtilities.OPTION_NO_ATTACHMENTS));
        request.setAttribute("optionAllowSelfRegisteredViewAll", Integer.toString(ProjectUtilities.OPTION_ALLOW_SELF_REGISTERED_VIEW_ALL));

        List<CustomField> customFields = IssueUtilities.getCustomFields();


        List<EditProjectFormActionUtil.CustomFieldInfo> fieldInfos = new ArrayList<EditProjectFormActionUtil.CustomFieldInfo>(customFields.size());
        Iterator<CustomField> fieldsIt = customFields.iterator();
        CustomField ci;
        while (fieldsIt.hasNext()) {
            ci = fieldsIt.next();
            fieldInfos.add(new EditProjectFormActionUtil.CustomFieldInfo(ci.getId(),
                    CustomFieldUtilities.getCustomFieldName(ci.getId(), LoginUtilities.getCurrentLocale(request)),
                    CustomFieldUtilities.getTypeString(ci.getFieldType(), LoginUtilities.getCurrentLocale(request))));
        }

        request.setAttribute("customFields", fieldInfos);


        // setup project-scripts

        List<ProjectScript> scripts = project.getScripts();
        Collections.sort(scripts, ProjectScript.FIELD_PRIORITY_COMPARATOR);

        EditProjectFormActionUtil.setUpPrioritiesInEnv(request);

        Locale locale = LoginUtilities.getCurrentLocale(request);
        Iterator<ProjectScript> it = scripts.iterator();

        List<ProjectScriptPTO> scriptPTOs = new ArrayList<ProjectScriptPTO>(scripts.size());
        while (it.hasNext()) {
            ProjectScriptPTO projectScript = new ProjectScriptPTO(it.next(), locale);
            scriptPTOs.add(projectScript);
        }
        request.setAttribute("projectScripts", scriptPTOs);

        List<Version> versions = project.getVersions();
        Collections.sort(versions, new Version.VersionComparator());
        List<EditProjectFormActionUtil.VersionInfo> vis = new ArrayList<EditProjectFormActionUtil.VersionInfo>();

        for (Version v : versions)
            vis.add(new EditProjectFormActionUtil.VersionInfo(v.getId(), v.getNumber(), v.getDescription(), v.getLastModifiedDate(), projectService.countIssuesByVersion(v.getId())));
        request.setAttribute("versions", vis);

        List<Component> components = project.getComponents();
        Collections.sort(components);
        List<EditProjectFormActionUtil.ComponentInfo> cis = new ArrayList<EditProjectFormActionUtil.ComponentInfo>();

        for (Component c : components)
            cis.add(new EditProjectFormActionUtil.ComponentInfo(c.getId(), c.getName(), c.getDescription(), c.getLastModifiedDate(), projectService.countIssuesByComponent(c.getId())));
        request.setAttribute("components", cis);
        return null;
    }

    /**
     * Setup the title for the Project-Form Action
     *
     * @param request        -  the servlet request
     * @param projectService - project-service
     */
    public void setupTitle(HttpServletRequest request, ProjectService projectService) {
        String pageTitleKey;
        String pageTitleArg = "";

        if ("update".equals(getAction())) {
            pageTitleKey = TITLE_UPDATE;
                Project project = projectService.getProject(getId());
                if (null != project) {
                    pageTitleArg = project.getName();

            }
        } else {
            setAction("create");
            pageTitleKey = TITLE_CREATE;
        }
        request.setAttribute(Constants.PAGE_TITLE_KEY, pageTitleKey);
        request.setAttribute(Constants.PAGE_TITLE_ARG, pageTitleArg);
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        action = null;
        id = null;
        name = null;
        status = null;
        description = null;
        owners = null;
        users = null;
        permissions = null;
        options = null;
        fields = null;

    }

    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        if (log.isDebugEnabled()) {
            log.debug("ProjectForm validate called: mapping: " + mapping
                    + ", request: " + request + ", errors: " + errors);
        }
        if (ServletContextUtils.getItrackerServices().getProjectService()
                .isUniqueProjectName(getName(), getId())) {
        } else {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
                    "itracker.web.error.project.duplicate.name"));
        }

        init(mapping, request);
        return errors;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer[] getFields() {
        if (null == fields)
            return null;
        return fields.clone();
    }

    public void setFields(Integer[] fields) {
        if (null == fields)
            this.fields = null;
        else
            this.fields = fields.clone();
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

    public Integer[] getOptions() {
        if (null == options)
            return null;
        return options.clone();
    }

    public void setOptions(Integer[] options) {
        if (null == options)
            this.options = null;
        else
            this.options = options.clone();
    }

    public Integer[] getOwners() {
        if (null == owners)
            return null;
        return owners.clone();
    }

    public void setOwners(Integer[] owners) {
        if (null == owners)
            this.owners = null;
        else
            this.owners = owners.clone();
    }

    public Integer[] getPermissions() {
        if (null == permissions)
            return null;

        return permissions.clone();

    }

    public void setPermissions(Integer[] permissions) {
        if (null == permissions)
            this.permissions = null;
        else
            this.permissions = permissions.clone();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer[] getUsers() {
        if (null == users)
            return null;
        return users.clone();
    }

    public void setUsers(Integer[] users) {
        if (null == users)
            this.users = null;
        else
            this.users = users.clone();
    }

}
