package org.itracker.persistence.dao;

import org.itracker.model.ProjectScript;

import java.util.List;

/**
 * Interface to define basic operations to deal with the
 * <code>ProjectScript</code> entity
 */
public interface ProjectScriptDAO extends BaseDAO<ProjectScript> {

    /**
     * Find a <code>ProjectScript</code> by its primary key
     *
     * @param scriptId system ID
     * @return project script or <tt>null</tt> if none exists with the given id
     */
    ProjectScript findByPrimaryKey(Integer scriptId);

    /**
     * Finds all <code>ProjectScript</code>s
     *
     * @return all <code>ProjectScript</code>s
     */
    List<ProjectScript> findAll();

    /**
     * Finds all scripts applied to fields on a particular project.
     *
     * @return list of project scripts
     */
    List<ProjectScript> findByProject(Integer projectId);

    /**
     * Finds all scripts applied to fields on a particular project.
     *
     * @return list of project scripts
     */
    List<ProjectScript> findByProjectField(Integer projectId, Integer fieldId);

}
