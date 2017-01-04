package org.itracker.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Configuration;

import java.util.List;

/**
 *
 */
public class ConfigurationDAOImpl extends BaseHibernateDAOImpl<Configuration>
        implements ConfigurationDAO {

    public Configuration findByPrimaryKey(Integer configId) {
        try {
            return (Configuration) getSession().get(Configuration.class, configId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public List<Configuration> findByType(int type) {
        return _findByTypeAndValue(Configuration.Type.valueOf(type), null);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public List<Configuration> findByTypeAndValue(int type, String value) {
        return findByTypeAndValue(Configuration.Type.valueOf(type), value);
    }


    public Configuration findByTypeValueKey(Configuration.Type type, String value) {
        List<Configuration> res = _findByTypeAndValue(type, value);
        if (res.isEmpty()) {
            return null;
        }
        return res.get(0);
    }

    public List<Configuration> findByType(Configuration.Type type) {
        return _findByTypeAndValue(type, null);
    }

    public List<Configuration> findByTypeAndValue(Configuration.Type type, final String value) {
        String _val = value;
        if (null == _val) {
            _val = "null";
        }
        return _findByTypeAndValue(type, _val);
    }
    private List<Configuration> _findByTypeAndValue(Configuration.Type type, String value) {
        Criteria criteria = getSession().createCriteria(Configuration.class);
        criteria.add(Expression.eq("type", type));
        if (null != value) {
            criteria.add(Expression.eq("value", value));
            criteria.setMaxResults(1);
        }

        try {
            return criteria.list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }


}
