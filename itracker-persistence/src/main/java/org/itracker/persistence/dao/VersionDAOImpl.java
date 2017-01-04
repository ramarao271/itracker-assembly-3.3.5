package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.itracker.model.Project;
import org.itracker.model.Version;

import java.util.List;

/**
 * Hibernate implementation of <code>VersionDAO</code> interface
 */
public class VersionDAOImpl extends BaseHibernateDAOImpl<Version>
        implements VersionDAO {

    /**
     * find <code>Version</code> by id
     *
     * @param versionId id of the <code>Version</code> to find
     * @return <code>Version</code> found
     */
    public Version findByPrimaryKey(Integer versionId) {
        try {
            return (Version) getSession().get(Version.class, versionId);
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }

    /**
     * Finds <code>Version</code>s by a project id and returns them as
     * a <code>Collection</code>.
     *
     * @param projectId id of the parent <code>Project</code>
     * @return a <code>Collection</code> containing the <code>Version</code>s found
     */
    @SuppressWarnings("unchecked")
    public List<Version> findByProjectId(Integer projectId) {
        try {
            Project project = (Project) getSession().load(Project.class, projectId);
            return getSession().createCriteria(Version.class)
                    .add(Expression.eq("project", project))
                    .list();
        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        }
    }
}
