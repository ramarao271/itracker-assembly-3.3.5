package org.itracker.persistence.dao;

import org.hibernate.Session;
import org.itracker.model.Entity;

/**
 *
 */
public interface BaseDAO<T extends Entity> {
    /**
     * Insert a new entity.
     * create- and lastmodified-date is set with current time.
     *
     * @param entity - detached entity object
     * @see Session#save(Object)
     */
    public void save(T entity);

    /**
     * Inserts a new detached entity or updates if it already exists.
     * create- and update-date are set automatically.
     *
     * @param entity - entity object to be inserted or updated
     * @see Session#saveOrUpdate(Object)
     */
    public void saveOrUpdate(T entity);

    /**
     * Deletes entity from persistence store.
     *
     * @see Session#delete(Object)
     */
    public void delete(T entity);

    /**
     * Remove this instance from the session cache.
     *
     * @see Session#evict(Object)
     */
    public void detach(T entity);

    /**
     * Reloads an entity from persistance.
     *
     * @see Session#refresh(Object)
     */
    public void refresh(T entity);

    /**
     * Copy the state of the given object onto the persistent object with the same
     * identifier. If there is no persistent instance currently associated with
     * the session, it will be loaded.
     *
     * @see Session#merge(Object)
     */
    public T merge(T entity);
}
