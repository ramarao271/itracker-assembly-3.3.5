package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.itracker.model.Report;

import java.util.List;

/**
 *
 */
public class ReportDAOImpl extends BaseHibernateDAOImpl<Report>
        implements ReportDAO {

    public Report findByPrimaryKey(Integer id) {
        try {
            return (Report) getSession().get(Report.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Report> findAll() {
        try {
            return getSession().getNamedQuery("ReportsAllQuery").list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

}
