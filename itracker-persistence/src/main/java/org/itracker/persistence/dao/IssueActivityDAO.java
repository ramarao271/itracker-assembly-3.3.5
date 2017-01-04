package org.itracker.persistence.dao;

import org.itracker.model.IssueActivity;

import java.util.List;

public interface IssueActivityDAO extends BaseDAO<IssueActivity> {

    /**
     * Finds the activity with the given ID.
     *
     * @param activityId system ID of the activity to return
     * @return activity instance of <tt>null</tt> if none exists
     */
    IssueActivity findById(Integer activityId);

    /**
     * Finds all activities of a given Issue.
     * <p/>
     * TODO : rename to findByIssue()
     *
     * @param issueId system ID of the issue of which to retrieve activities
     * @return list of activities of the issue, in unspecified order
     */
    List<IssueActivity> findByIssueId(Integer issueId);

    /**
     * Finds all activities for an Issue with the given notification status.
     *
     * @param issueId          system ID of the issue of which to retrieve activities
     * @param notificationSent whether to return activities for which
     *                         a notification has been sent or not
     */
    List<IssueActivity> findByIssueIdAndNotification(Integer issueId,
                                                     boolean notificationSent);


}
