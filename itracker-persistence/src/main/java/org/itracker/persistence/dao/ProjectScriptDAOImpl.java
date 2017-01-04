package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.ProjectScript;

import java.util.List;

/**
 * ProjectScript DAO implementation.
 *
 * @author johnny
 */
public class ProjectScriptDAOImpl extends BaseHibernateDAOImpl<ProjectScript>
        implements ProjectScriptDAO {

    /**
     *
     */
    public ProjectScriptDAOImpl() {
    }

    public ProjectScript findByPrimaryKey(Integer scriptId) {
        ProjectScript script;

        try {
            script = (ProjectScript) getSession().get(ProjectScript.class, scriptId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return script;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectScript> findAll() {
        List<ProjectScript> scripts;

        try {
            Query query = getSession().getNamedQuery("ProjectScriptsAllQuery");
            scripts = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return scripts;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectScript> findByProject(Integer projectId) {
        List<ProjectScript> scripts;

        try {
            Query query = getSession().getNamedQuery(
                    "ProjectScriptsByProjectQuery");
            query.setInteger("projectId", projectId);
            scripts = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return scripts;
    }

    @SuppressWarnings("unchecked")
    public List<ProjectScript> findByProjectField(Integer projectId,
                                                  Integer fieldId) {
        List<ProjectScript> scripts;

        try {
            Query query = getSession().getNamedQuery(
                    "ProjectScriptsByProjectAndFieldQuery");
            query.setInteger("projectId", projectId);
            query.setInteger("fieldId", fieldId);
            scripts = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return scripts;
    }

}
