package org.itracker.persistence.dao;

import org.itracker.model.Version;

import java.util.List;

/**
 *
 */
public interface VersionDAO extends BaseDAO<Version> {

    public Version findByPrimaryKey(Integer targetVersionId);

    public List<Version> findByProjectId(Integer projectId);

}
