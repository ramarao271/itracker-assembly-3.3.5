package org.itracker.web.util;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.Notification.Type;
import org.itracker.model.util.*;
import org.itracker.services.NotificationService;
import org.itracker.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public class EditIssueActionUtil {
    private static final Logger log = Logger.getLogger(EditIssueActionUtil.class);

    public static void sendNotification(Integer issueId, int previousStatus,
                                              String baseURL, NotificationService notificationService) {
        Type notificationType = Type.UPDATED;

        Issue issue = ServletContextUtils.getItrackerServices().getIssueService().getIssue(issueId);

        if ((previousStatus < IssueUtilities.STATUS_CLOSED)
                && issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
            notificationType = Type.CLOSED;
        }

        if (log.isDebugEnabled()) {
            log.debug("notificationService: before send");
        }
        notificationService.sendNotification(issue, notificationType, baseURL);

        if (log.isDebugEnabled()) {
            log.debug("notificationService: after send");
        }
    }

    public static ActionForward getReturnForward(Issue issue, Project project,
                                                       String caller, ActionMapping mapping) throws Exception {
        if ("index".equals(caller)) {
            log.info("EditIssueAction: Forward: index");
            return mapping.findForward("index");
        } else if ("viewissue".equals(caller) && issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
            log.info("EditIssueAction: Forward: viewissue");
            return new ActionForward(mapping.findForward("viewissue").getPath()
                    + "?id=" + issue.getId());
        } else {
            log.info("EditIssueAction: Forward: listissues");
            return new ActionForward(mapping.findForward("listissues")
                    .getPath()
                    + "?projectId=" + project.getId());
        }
    }


    public static Map<Integer, List<NameValuePair>> getListOptions(
            HttpServletRequest request, Issue issue,
            List<NameValuePair> ownersList,
            Map<Integer, Set<PermissionType>> userPermissions, Project project,
            User currUser) {
        Map<Integer, List<NameValuePair>> listOptions = new HashMap<Integer, List<NameValuePair>>();

        Locale locale = (Locale) request.getSession().getAttribute(
                Constants.LOCALE_KEY);

        if (ownersList != null && ownersList.size() > 0) {
            listOptions.put(IssueUtilities.FIELD_OWNER, ownersList);
        }

        boolean hasFullEdit = UserUtilities.hasPermission(userPermissions,
                project.getId(), UserUtilities.PERMISSION_EDIT_FULL);

        List<NameValuePair> allStatuses = IssueUtilities.getStatuses(locale);
        List<NameValuePair> statusList = new ArrayList<NameValuePair>();

        if (!hasFullEdit) {

            if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                    && UserUtilities.hasPermission(userPermissions, project
                    .getId(), UserUtilities.PERMISSION_CLOSE)) {
                for (NameValuePair allStatuse : allStatuses) {
                    int statusNumber = Integer.parseInt(allStatuse
                            .getValue());
                    if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED
                            && statusNumber >= IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuse);
                    } else if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                            && statusNumber >= IssueUtilities.STATUS_RESOLVED) {
                        statusList.add(allStatuse);
                    }
                }
            }

        } else {

            if (currUser.isSuperUser()) {
                for (NameValuePair allStatuse : allStatuses) {
                    statusList.add(allStatuse);
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_ASSIGNED
                    && issue.getStatus() < IssueUtilities.STATUS_RESOLVED) {
                for (NameValuePair allStatuse : allStatuses) {
                    int statusNumber = Integer.parseInt(allStatuse
                            .getValue());
                    if (statusNumber >= IssueUtilities.STATUS_ASSIGNED
                            && statusNumber < IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuse);
                    } else if (statusNumber >= IssueUtilities.STATUS_CLOSED
                            && ProjectUtilities
                            .hasOption(
                                    ProjectUtilities.OPTION_ALLOW_ASSIGN_TO_CLOSE,
                                    project.getOptions())
                            && UserUtilities.hasPermission(userPermissions,
                            project.getId(),
                            UserUtilities.PERMISSION_CLOSE)) {
                        statusList.add(allStatuse);
                    }
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_RESOLVED
                    && issue.getStatus() < IssueUtilities.STATUS_CLOSED) {
                for (NameValuePair allStatuse : allStatuses) {
                    int statusNumber = Integer.parseInt(allStatuse
                            .getValue());
                    if (statusNumber >= IssueUtilities.STATUS_ASSIGNED
                            && statusNumber < IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuse);
                    } else if (statusNumber >= IssueUtilities.STATUS_CLOSED
                            && UserUtilities.hasPermission(userPermissions,
                            project.getId(),
                            UserUtilities.PERMISSION_CLOSE)) {
                        statusList.add(allStatuse);
                    }
                }
            } else if (issue.getStatus() >= IssueUtilities.STATUS_CLOSED) {
                for (NameValuePair allStatuse : allStatuses) {
                    int statusNumber = Integer.parseInt(allStatuse
                            .getValue());
                    if ((statusNumber >= IssueUtilities.STATUS_ASSIGNED && statusNumber < IssueUtilities.STATUS_RESOLVED)
                            || statusNumber >= IssueUtilities.STATUS_CLOSED) {
                        statusList.add(allStatuse);
                    }
                }
            }

        }
        // sort by status code so it will be ascending output.
        Collections.sort(statusList, NameValuePair.VALUE_COMPARATOR);
        listOptions.put(IssueUtilities.FIELD_STATUS, statusList);

        List<NameValuePair> severities = IssueUtilities.getSeverities(locale);
        // sort by severity code so it will be ascending output.
        Collections.sort(severities, NameValuePair.VALUE_COMPARATOR);
        listOptions.put(IssueUtilities.FIELD_SEVERITY, severities);

        List<NameValuePair> resolutions = IssueUtilities.getResolutions(locale);
        listOptions.put(IssueUtilities.FIELD_RESOLUTION, resolutions);

        List<Component> components = project.getComponents();
        Collections.sort(components, Component.NAME_COMPARATOR);
        listOptions.put(IssueUtilities.FIELD_COMPONENTS, Convert
                .componentsToNameValuePairs(components));

        List<Version> versions = project.getVersions();
        listOptions.put(IssueUtilities.FIELD_VERSIONS, Convert
                .versionsToNameValuePairs(versions));
        listOptions.put(IssueUtilities.FIELD_TARGET_VERSION, Convert
                .versionsToNameValuePairs(versions));

        List<CustomField> projectFields = project.getCustomFields();
        for (CustomField projectField : projectFields) {
            if (projectField.getFieldType() == CustomField.Type.LIST) {
                listOptions.put(projectField.getId(), Convert
                        .customFieldOptionsToNameValuePairs(projectField, locale));
            }
        }

        return listOptions;
    }

    private static List<NameValuePair> fieldOptions(CustomField field) {
        return Convert.customFieldOptionsToNameValuePairs(field.getOptions());
    }

    public static Map<Integer, List<NameValuePair>> mappedFieldOptions(List<CustomField> fields ) {
        Map<Integer, List<NameValuePair>> options = new HashMap<Integer, List<NameValuePair>>(fields.size());
        for (CustomField field: fields) {

            List<NameValuePair> listOptions = fieldOptions(field);
            if (field.isSortOptionsByName()) {
                Collections.sort(listOptions, NameValuePair.KEY_COMPARATOR);
            }

            options.put(field.getId(), listOptions);
        }



        return options;
    }


    /**
     * This method will obtain and build a list of possible owners for the
     * webpages to display and the operator to choose from.
     */
    public static List<NameValuePair> getAssignableIssueOwnersList(Issue issue,
                                                                   Project project, User currUser, Locale locale,
                                                                   UserService userService,
                                                                   Map<Integer, Set<PermissionType>> userPermissions) {

        List<NameValuePair> ownersList = new ArrayList<NameValuePair>();

        if (UserUtilities.hasPermission(userPermissions, project.getId(),
                UserUtilities.PERMISSION_ASSIGN_OTHERS)) {
            if (issue.getOwner() == null) {
                ownersList.add(new NameValuePair(ITrackerResources.getString(
                        "itracker.web.generic.unassigned", locale), "-1"));
            } else {
                ownersList.add(new NameValuePair(ITrackerResources.getString(
                        "itracker.web.generic.unassign", locale), "-1"));
            }
            List<User> possibleOwners = userService.getPossibleOwners(issue,
                    project.getId(), currUser.getId());
            Collections.sort(possibleOwners, User.NAME_COMPARATOR);
            List<NameValuePair> ownerNames = Convert
                    .usersToNameValuePairs(possibleOwners);
            for (NameValuePair ownerName : ownerNames) {
                ownersList.add(ownerName);
            }
        } else if (UserUtilities.hasPermission(userPermissions,
                project.getId(), UserUtilities.PERMISSION_ASSIGN_SELF)) {
            if (issue.getOwner() != null) {
                if (IssueUtilities.canUnassignIssue(issue, currUser.getId(),
                        userPermissions)) {
                    ownersList.add(new NameValuePair(ITrackerResources
                            .getString("itracker.web.generic.unassign",
                                    locale), "-1"));
                }
                if (!issue.getOwner().getId().equals(currUser.getId())) {
                    ownersList.add(new NameValuePair(issue.getOwner()
                            .getFirstName()
                            + " " + issue.getOwner().getLastName(), issue
                            .getOwner().getId().toString()));
                    ownersList.add(new NameValuePair(currUser.getFirstName()
                            + " " + currUser.getLastName(), currUser.getId()
                            .toString()));
                } else {
                    ownersList.add(new NameValuePair(currUser.getFirstName()
                            + " " + currUser.getLastName(), currUser.getId()
                            .toString()));
                }
            }
        } else if (issue.getOwner() != null
                && IssueUtilities.canUnassignIssue(issue, currUser.getId(),
                userPermissions)) {
            ownersList.add(new NameValuePair(ITrackerResources.getString(
                    "itracker.web.generic.unassign", locale), "-1"));
            ownersList.add(new NameValuePair(issue.getOwner().getFirstName()
                    + " " + issue.getOwner().getLastName(), issue.getOwner()
                    .getId().toString()));
        }

        return ownersList;
    }

    @SuppressWarnings("unchecked")
    public static void setupCreateIssue(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Project project = (Project) session.getAttribute(Constants.PROJECT_KEY);
        Map<Integer, List<NameValuePair>> listOptions = (Map<Integer, List<NameValuePair>>) session
                .getAttribute(Constants.LIST_OPTIONS_KEY);
        List<NameValuePair> possibleOwners = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_OWNER);
        List<NameValuePair> severities = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_SEVERITY);
        List<NameValuePair> possibleCreators = WorkflowUtilities
                .getListOptions(listOptions, IssueUtilities.FIELD_CREATOR);
        List<NameValuePair> components = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_COMPONENTS);
        List<NameValuePair> versions = WorkflowUtilities.getListOptions(
                listOptions, IssueUtilities.FIELD_VERSIONS);
        List<CustomField> projectFields = project.getCustomFields();
        if (projectFields != null && projectFields.size() > 0) {
            Collections.sort(projectFields, CustomField.ID_COMPARATOR);
        }
        String wrap = "soft";
        if (ProjectUtilities.hasOption(ProjectUtilities.OPTION_SURPRESS_HISTORY_HTML, project.getOptions())) {
            wrap = "hard";
        }
        /*
           * Get the status name for the current locale and set in request
           */
        String pageTitleKey = "itracker.web.createissue.title";
        String pageTitleArg = project.getName();
        req.setAttribute("pageTitleKey", pageTitleKey);
        req.setAttribute("pageTitleArg", pageTitleArg);

        req.setAttribute("statusName", IssueUtilities.getStatusName(
                IssueUtilities.STATUS_NEW,
                LoginUtilities.getCurrentLocale(req)));
        req.setAttribute("hasAttachmentOption", !ProjectUtilities.hasOption(
                ProjectUtilities.OPTION_NO_ATTACHMENTS, project.getOptions()));
        req.setAttribute("possibleOwners", possibleOwners);
        req.setAttribute("severities", severities);
        req.setAttribute("possibleCreators", possibleCreators);
        req.setAttribute("components", components);
        req.setAttribute("versions", versions);
        req.setAttribute("projectFields", projectFields);
        req.setAttribute("listOptions", listOptions);
        req.setAttribute("wrap", wrap);
    }
}
