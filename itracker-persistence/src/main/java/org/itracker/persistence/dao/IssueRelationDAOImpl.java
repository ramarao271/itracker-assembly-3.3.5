package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.IssueRelation;

import java.util.List;

/**
 *
 */
public class IssueRelationDAOImpl extends BaseHibernateDAOImpl<IssueRelation>
        implements IssueRelationDAO {

    public IssueRelation findByPrimaryKey(Integer relationId) {
        try {
            return (IssueRelation) getSession().get(IssueRelation.class, relationId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<IssueRelation> findByIssue(Integer issueId) {
        List<IssueRelation> relations;

        try {
            Query query = getSession().getNamedQuery(
                    "IssueRelationsByIssueQuery");
            query.setInteger("issueId", issueId);
            relations = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return relations;
    }

}
