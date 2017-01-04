package org.itracker.persistence.dao;

import org.itracker.model.Issue;
import org.itracker.model.IssueSearchQuery;
import org.itracker.model.PermissionType;
import org.itracker.model.User;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Issue Data Access Object interface.
 */
public interface IssueDAO extends BaseDAO<Issue> {

    /**
     * Finds the issue with the given ID.
     * <p/>
     * <p>PENDING: should this method throw a NoSuchEntityException
     * instead of returning null if the issue doesn't exist ? </p>
     *
     * @param issueId ID of the issue to retrieve
     * @return issue with the given ID or <tt>null</tt> if none exits
     */
    Issue findByPrimaryKey(Integer issueId);

    /**
     * Finds all issues in all projects.
     * <p/>
     * <p>PENDING: do we really need to retrieve all issues at once ?
     * It can cause OutOfMemoryError depending on the DB size!
     * Consider scrolling through an issues result set in case we really do.
     * </p>
     *
     * @return list of exiting issues, in an unspecified order
     * @deprecated don't use due to expensive memory use.
     */
    List<Issue> findAll();

    /**
     * Finds all issues in the given status in all projects.
     *
     * @param status status of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatus(int status);

    /**
     * Finds all issues with a status less than the given one in all projects.
     *
     * @param maxExclusiveStatus all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatusLessThan(int maxExclusiveStatus);

    /**
     * Finds all issues with a status less than or equal to the given status
     * in all projects.
     *
     * @param maxStatus all issues less that or equal to this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatusLessThanEqualTo(int maxStatus);

    /**
     * Finds all issues with a status less than or equal to the given status
     * in active and viewable projects.
     *
     * @param maxStatus all issues less that or equal to this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByStatusLessThanEqualToInAvailableProjects(int maxStatus);

    /**
     * Finds all issues with the given severity in all projects.
     *
     * @param severity severity of the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findBySeverity(int severity);

    /**
     * Finds all issues of the given project.
     *
     * @param projectId ID of the project of which to retrieve all issues
     * @return list of issues in no particular order
     */
    List<Issue> findByProject(Integer projectId);

    /**
     * Counts the number of issues of the given project.
     *
     * @param projectId ID of the project of which to count issues
     * @return number of issues
     */
    Long countByProject(Integer projectId);

    /**
     * Finds all issues of the given project with a status lower than
     * the given one.
     *
     * @param projectId          ID of the project of which to retrieve the issues
     * @param maxExclusiveStatus all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByProjectAndLowerStatus(Integer projectId,
                                            int maxExclusiveStatus);

    /**
     * Counts the number of issues of the given project with a status
     * lower than the given one.
     *
     * @param projectId          ID of the project of which to count issues
     * @param maxExclusiveStatus all issues under this status will be counted
     * @return number of issues
     */
    Long countByProjectAndLowerStatus(Integer projectId,
                                      int maxExclusiveStatus);

    /**
     * Finds all issues of the given project with a status higher than
     * or equal to the given one.
     *
     * @param projectId ID of the project of which to retrieve the issues
     * @param minStatus all issues with this status or above will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByProjectAndHigherStatus(Integer projectId,
                                             int minStatus);

    /**
     * Counts the number of issues of the given project with a status
     * higher than or equal to the given one.
     *
     * @param projectId ID of the project of which to count issues
     * @param minStatus all issues with this status or above will be counted
     * @return number of issues
     */
    Long countByProjectAndHigherStatus(Integer projectId, int minStatus);

    /**
     * Finds all issues owned by the given user in all projects
     * and with a status lower than the given one.
     *
     * @param ownerId            ID of the user who owns the issues to return
     * @param maxExclusiveStatus status under which to return issues
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByOwner(Integer ownerId, int maxExclusiveStatus);

    /**
     * Finds all issues owned by the given user in all active and viewable
     * projects and with a status less than the given one.
     *
     * @param ownerId            ID of the user who owns the issues to return
     * @param maxExclusiveStatus status under which to return issues
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByOwnerInAvailableProjects(Integer ownerId,
                                               int maxExclusiveStatus);

    /**
     * Finds all issues without owner with a status less than
     * or equal to the given one in all projects.
     *
     * @param maxStatus maximum status allowed for the issues to return
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findUnassignedIssues(int maxStatus);

    /**
     * Finds all issues created by the given user in all projects
     * and with a status less than the given one.
     *
     * @param userId             ID of the user who created the issues to return
     * @param maxExclusiveStatus all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByCreator(Integer creatorId, int maxExclusiveStatus);

    /**
     * Finds all issues created by the given user in all active and viewable
     * projects and with a status less than the given one.
     *
     * @param userId             ID of the user who created the issues to return
     * @param maxExclusiveStatus all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByCreatorInAvailableProjects(Integer creatorId,
                                                 int maxExclusiveStatus);

    /**
     * Finds all issues with notifications for the given user in all projects
     * and with a status less than the given one.
     * <p/>
     * <p>Only 1 instance of every issue is returned, even if multiple
     * notifications exist for an issue. </p>
     *
     * @param userId             ID of the user with notifications for the issues to return
     * @param maxExclusiveStatus all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByNotification(Integer userId, int maxExclusiveStatus);

    /**
     * Finds all issues with notifications for the given user in active
     * and viewable projects.
     * <p/>
     * <p>Only 1 instance of every issue is returned, even if multiple
     * notifications exist for an issue. </p>
     *
     * @param userId             ID of the user with notifications for the issues to return
     * @param maxExclusiveStatus all issues under this status will be returned
     * @return list of issues matching the above filter, in an unspecified order
     */
    List<Issue> findByNotificationInAvailableProjects(Integer userId,
                                                      int maxExclusiveStatus);

    /**
     * Finds all issues of the component with the given ID.
     *
     * @param componentId ID of the component of which to retrieve all issues
     * @return list of issues in no particular order
     */
    List<Issue> findByComponent(Integer componentId);

    /**
     * Get the next issues in the project based on ID.
     * @param issueId the issue ID
     * @return List of next issues in the project.
     */
    List<Issue> findNextIssues(Integer issueId);

    /**
     * Get the next issues in the project based on ID.
     * @param issueId the issue ID
     * @return List of next issues in the project.
     */
    List<Issue> findPreviousIssues(Integer issueId);

    /**
     * Count all Issues in database
     */
    Long countAllIssues();

    /**
     * Counts the number of issues attached to a component.
     *
     * @param componentId ID of the component
     * @return number of issues
     */
    Long countByComponent(Integer componentId);

    /**
     * Finds all issues of the version with the given ID.
     *
     * @param versionId ID of the version of which to retrieve all issues
     * @return list of issues in no particular order
     */
    List<Issue> findByVersion(Integer versionId);

    /**
     * Counts the number of issues attached to a version.
     *
     * @param versionId ID of the version
     * @return number of issues
     */
    Long countByVersion(Integer versionId);

    /**
     * Returns the modification date of the latest modified issue
     * in the project with the given id.
     *
     * @param projectId ID of the project of which to retrieve the issues
     * @return date of the most recent issue modification for the project.
     *         <tt>null</tt> if no issue exists in the project
     */
    Date latestModificationDate(Integer projectId);

    /**
     * Query the list of issues that satisfies the search criteria
     * specified in <code>queryModel</code>.
     *
     * @param queryModel      The search criteria.
     * @param user            The currently logged-in user.
     * @param userPermissions Permissions currently inforced. TODO: We could look this up instead of passing this as parameter.
     */
    List<Issue> query(IssueSearchQuery queryModel, User user, Map<Integer, Set<PermissionType>> userPermissions);

    /**
     * Delete all issues targeted for the specified version.
     *
     * @param versionId the version ID.
     */
    List<Issue> findByTargetVersion(Integer versionId);


}
