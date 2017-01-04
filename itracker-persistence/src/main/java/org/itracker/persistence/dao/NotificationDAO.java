package org.itracker.persistence.dao;

import org.itracker.model.Notification;

import java.util.List;

/**
 *
 */
public interface NotificationDAO extends BaseDAO<Notification> {

    Notification findById(Integer id);

    /**
     * Finds all Notifications for an Issue.
     *
     * @return list of notification for the given issue, in unspecified order
     */
    List<Notification> findByIssueId(Integer issueId);

}
