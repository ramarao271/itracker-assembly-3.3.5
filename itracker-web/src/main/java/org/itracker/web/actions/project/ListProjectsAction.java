package org.itracker.web.actions.project;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.ptos.ProjectPTO;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class ListProjectsAction extends ItrackerBaseAction {
	private static final Logger log = Logger
			.getLogger(ListProjectsAction.class);

	/**
	 * returns listing of ALL projects with given permissions. Also locked and
	 * view-only projects are selected.
	 * 
	 * @param projectService
	 * @param permissionFlags
	 * @param permissions
	 * @return
	 */
	protected static List<ProjectPTO> getAllPTOs(ProjectService projectService,
			PermissionType[] permissionFlags,
			final Map<Integer, Set<PermissionType>> permissions) {
		List<Project> projects = projectService.getAllProjects();

		ArrayList<Project> projects_tmp = new ArrayList<Project>(projects);
		Iterator<Project> projectIt = projects.iterator();
		while (projectIt.hasNext()) {
			Project project = projectIt.next();
			if (!UserUtilities.hasPermission(permissions, project.getId(),
					permissionFlags)) {
				projects_tmp.remove(project);
			}
		}

		projects = projects_tmp;
		Collections.sort(projects, new Project.ProjectComparator());

		ArrayList<ProjectPTO> ptos = new ArrayList<>(projects_tmp
				.size());

		projectIt = projects.iterator();

		while (projectIt.hasNext()) {
			Project project = projectIt.next();
			ptos.add(createProjectPTO(project, projectService, permissions));
		}

		return ptos;
	}

	protected static List<ProjectPTO> getPTOs(ProjectService projectService,
			PermissionType[] permissionFlags,
			final Map<Integer, Set<PermissionType>> permissions) {
		List<Project> projects = projectService.getAllAvailableProjects();

		ArrayList<Project> projects_tmp = new ArrayList<Project>(projects);
		Iterator<Project> projectIt = projects.iterator();
		while (projectIt.hasNext()) {
			Project project = (Project) projectIt.next();
			if (!UserUtilities.hasPermission(permissions, project.getId(),
					permissionFlags)) {
				projects_tmp.remove(project);
			}
		}

		projects = projects_tmp;
		Collections.sort(projects, new Project.ProjectComparator());

		ArrayList<ProjectPTO> ptos = new ArrayList<ProjectPTO>(projects_tmp
				.size());

		projectIt = projects.iterator();

		while (projectIt.hasNext()) {
			Project project = projectIt.next();
			ptos.add(createProjectPTO(project, projectService, permissions));
		}

		return ptos;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		final Map<Integer, Set<PermissionType>> permissions = RequestHelper
				.getUserPermissions(request.getSession());

		ProjectService projectService = ServletContextUtils.getItrackerServices()
				.getProjectService();

		request.setAttribute("projects", getPTOs(projectService, new PermissionType[] {
				PermissionType.ISSUE_VIEW_ALL,
				PermissionType.ISSUE_VIEW_USERS }, permissions));

		// TODO add a secure token to feed-URL
        request.setAttribute("rssFeed", "/servlets/issues/");

		String pageTitleKey = "itracker.web.listprojects.title";
		String pageTitleArg = "";

		request.setAttribute("pageTitleKey", pageTitleKey);
		request.setAttribute("pageTitleArg", pageTitleArg);

		log.info("ListProjectsAction: Forward: listprojects");
		return mapping.findForward("list_projects");
	}

	private static void setupNumberOfIssues(ProjectPTO pto,
			ProjectService service) {
		pto.setTotalNumberIssues(service.getTotalNumberIssuesByProject(pto
				.getId()));
	}

	private static void setupNumberOfOpenIssues(ProjectPTO pto,
			ProjectService service) {
		pto.setTotalOpenIssues(service.getTotalNumberOpenIssuesByProject(pto
				.getId()));
	}

	private static void setupNumberOfResolvedIssues(ProjectPTO pto,
			ProjectService service) {
		pto.setTotalResolvedIssues(service
				.getTotalNumberResolvedIssuesByProject(pto.getId()));
	}

	private static void setupCanCreate(ProjectPTO pto,
			final Map<Integer, Set<PermissionType>> permissions) {
		pto.setCanCreate(UserUtilities.hasPermission(permissions, pto.getId(),
				PermissionType.ISSUE_CREATE));
	}

	private static void setupLastIssueUpdateDate(ProjectPTO pto,
			ProjectService service) {
		pto.setLastUpdatedIssueDate(service
				.getLatestIssueUpdatedDateByProjectId(pto.getId()));
	}

	/**
	 * 
	 * Makes a page transfer object for the project in first argument.
	 * 
	 * @param project
	 *            - wrapped project for the pto
	 * @param projectService
	 *            - project-service
	 * @param permissions
	 *            - users permissions

	 * @return
	 */
	private static ProjectPTO createProjectPTO(Project project,
			ProjectService projectService,
			final Map<Integer, Set<PermissionType>> permissions) {
		ProjectPTO pto = new ProjectPTO();
		pto.setCreateDate(project.getCreateDate());
		pto.setModifiedDate(project.getLastModifiedDate());
		pto.setId(project.getId());
		pto.setDescription(project.getDescription());
		pto.setName(project.getName());
		pto.setStatus(project.getStatus());
		setupCanCreate(pto, permissions);
		setupLastIssueUpdateDate(pto, projectService);
		setupNumberOfIssues(pto, projectService);
		setupNumberOfOpenIssues(pto, projectService);
		setupNumberOfResolvedIssues(pto, projectService);
		return pto;
	}
}
