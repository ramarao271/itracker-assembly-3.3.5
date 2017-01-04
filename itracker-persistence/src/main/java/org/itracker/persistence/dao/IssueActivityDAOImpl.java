package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.IssueActivity;

import java.util.List;

/**
 *
 */
public class IssueActivityDAOImpl extends BaseHibernateDAOImpl<IssueActivity>
        implements IssueActivityDAO {

    public IssueActivityDAOImpl() {
    }

    public IssueActivity findById(Integer activityId) {
        IssueActivity activity;
        try {
            activity = (IssueActivity) getSession().get(IssueActivity.class,
                    activityId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return activity;
    }

    @SuppressWarnings("unchecked")
    public List<IssueActivity> findByIssueId(Integer issueId) {
        List<IssueActivity> activities;

        try {
            Query query = getSession().getNamedQuery(
                    "IssueActivitiesByIssueQuery");
            query.setInteger("issueId", issueId);
            activities = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return activities;
    }

    @SuppressWarnings("unchecked")
    public List<IssueActivity> findByIssueIdAndNotification(Integer issueId,
                                                            boolean notificationSent) {
        List<IssueActivity> activities;

        try {
            Query query = getSession().getNamedQuery(
                    "IssueActivitiesByIssueAndNotificationQuery");
            query.setInteger("issueId", issueId);
            query.setBoolean("notificationSent", notificationSent);
            activities = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return activities;
    }


}
