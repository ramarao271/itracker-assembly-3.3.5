package org.itracker.persistence.dao;

import org.itracker.model.Component;

import java.util.List;

/**
 * Component Data Access Object interface.
 *
 * @author Johnny
 */
public interface ComponentDAO extends BaseDAO<Component> {

    /**
     * Finds the component with the given ID.
     *
     * @param componentId ID of the component to retrieve
     * @return component with the given ID or <tt>null</tt> if none exists
     */
    Component findById(Integer componentId);

    /**
     * Finds all components of a given project.
     *
     * @param projectId ID of the project of which to retrieve all components
     * @return list of components, in unspecified order
     */
    List<Component> findByProject(Integer projectId);

}
