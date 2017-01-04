package org.itracker.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.itracker.model.CustomField;

import java.util.List;

/**
 *
 */
public class CustomFieldDAOImpl extends BaseHibernateDAOImpl<CustomField>
        implements CustomFieldDAO {

    public CustomField findByPrimaryKey(Integer customFieldId) {
        try {
            return (CustomField) getSession().get(CustomField.class, customFieldId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CustomField> findAll() {
        Criteria criteria = getSession().createCriteria(CustomField.class);

        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

}
