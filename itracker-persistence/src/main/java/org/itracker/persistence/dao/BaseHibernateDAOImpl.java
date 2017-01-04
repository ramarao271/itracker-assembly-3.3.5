package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.itracker.model.Entity;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Date;

/**
 * Contains common behaviour to all hibernate factories
 *
 * @author rui silva
 */
public abstract class BaseHibernateDAOImpl<T extends Entity> extends HibernateDaoSupport
        implements BaseDAO<T> {

    /**
     *
     */
    public BaseHibernateDAOImpl() {
        super();
    }

    /**
     * insert a new entity.
     * create- and lastmodified-date is set with current time.
     *
     * @param entity - detached entity object
     */
    public void save(T entity) {
        if (null == entity) {
            throw new InvalidDataAccessResourceUsageException("entity must not be null");
        }
        try {
            Date createDate = new Date();
            entity.setCreateDate(createDate);
            entity.setLastModifiedDate(createDate);
            getSession().save(entity);
        } catch (HibernateException ex) {
            logger.debug("save: caught HibernateException, call to convertHibernateException", ex);
            throw convertHibernateAccessException(ex);
        }
    }

    /**
     * inserts a new detached entity or updates if it already exists.
     * create- and update-date are set automatically.
     *
     * @param entity - entity object to be inserted or updated
     */
    public void saveOrUpdate(T entity) {
        if (null == entity) {
            throw new InvalidDataAccessResourceUsageException("entity must not be null");
        }

        try {
            if (null == entity.getCreateDate()) {
                entity.setCreateDate(new Date());
                entity.setLastModifiedDate(entity.getCreateDate());
            } else {
                entity.setLastModifiedDate(new Date());
            }
            getSession().saveOrUpdate(entity);
        } catch (HibernateException ex) {
            logger.debug("saveOrUpdate: caught HibernateException, call to convertHibernateException", ex);
            throw convertHibernateAccessException(ex);
        }
    }

    public void delete(T entity) {
        if (null == entity) {
            throw new InvalidDataAccessResourceUsageException("entity must not be null");
        }
        try {
            getSession().delete(entity);
            getSession().flush();
        } catch (HibernateException ex) {
            logger.debug("delete: caught HibernateException, call to convertHibernateException", ex);
            throw convertHibernateAccessException(ex);
        }
    }

    public void detach(T entity) {
        if (null == entity) {
            throw new InvalidDataAccessResourceUsageException("entity must not be null");
        }
        try {
            getSession().evict(entity);
        } catch (HibernateException ex) {
            logger.debug("detach: caught HibernateException, call to convertHibernateException", ex);
            throw convertHibernateAccessException(ex);
        }
    }

    public void refresh(T entity) {
        if (null == entity) {
            throw new InvalidDataAccessResourceUsageException("entity must not be null");
        }
        try {
            getSession().refresh(entity);
        } catch (HibernateException ex) {
            logger.debug("refresh: caught HibernateException, call to convertHibernateException", ex);
            throw convertHibernateAccessException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public T merge(T entity) {
        if (null == entity) {
            throw new InvalidDataAccessResourceUsageException("entity must not be null");
        }
        try {
            return (T) getSession().merge(entity);
        } catch (HibernateException ex) {
            logger.debug("merge: caught HibernateException, call to convertHibernateException", ex);
            throw convertHibernateAccessException(ex);
        }
    }
}
