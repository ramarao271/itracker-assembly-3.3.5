package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.IssueHistory;

import java.util.List;

public class IssueHistoryDAOImpl extends BaseHibernateDAOImpl<IssueHistory>
        implements IssueHistoryDAO {

    public IssueHistoryDAOImpl() {
    }

    public IssueHistory findByPrimaryKey(Integer entryId) {
        try {
            return (IssueHistory) getSession().get(IssueHistory.class, entryId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<IssueHistory> findByIssueId(Integer issueId) {
        List<IssueHistory> history;

        try {
            Query query = getSession().getNamedQuery(
                    "IssueHistoryByIssueQuery");
            query.setInteger("issueId", issueId);
            history = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return history;
    }

}
