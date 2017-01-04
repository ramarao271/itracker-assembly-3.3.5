package org.itracker.persistence.dao;

import org.itracker.model.WorkflowScript;

import java.util.List;

/**
 * Interface to define basic operations to deal with the
 * <code>WorkflowScript</code> entity
 */
public interface WorkflowScriptDAO extends BaseDAO<WorkflowScript> {

    /**
     * Find a <code>WorkflowScript</code> by its primary key
     *
     * @param id primary key of the <code>WorkflowScript</code>
     * @return The <code>WorkflowScript</code> found
     */
    public WorkflowScript findByPrimaryKey(Integer id);

    /**
     * Finds all <code>WorkflowScript</code>s
     *
     * @return a <code>Collection</code> with all <code>WorkflowScript</code>s
     */
    public List<WorkflowScript> findAll();

}
