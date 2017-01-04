package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.User;

import java.util.List;

public class PermissionDAOImpl extends BaseHibernateDAOImpl<Permission>
        implements PermissionDAO {

    @SuppressWarnings("unchecked")
    public List<Permission> findByUserId(Integer userId) {
        List<Permission> permissions;

        if (getSession().get(User.class, userId) == null) {
            throw new NoSuchEntityException("User " + userId + " not found.");
        }

        try {
            Query query = getSession().getNamedQuery(
                    "PermissionsByUserQuery");
            query.setInteger("userId", userId);
            permissions = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return permissions;
    }


    @Deprecated
    @SuppressWarnings("unchecked")
    public List<Permission> findByProjectIdAndPermission(Integer projectId,
                                                         int permissionType) {
        return findByProjectIdAndPermission(projectId, PermissionType.valueOf(permissionType));
    }

    @Override
    public List<Permission> findByProjectIdAndPermission(Integer projectId, PermissionType permissionType) {

        List<Permission> permissions;

        if (getSession().get(Project.class, projectId) == null) {
            throw new NoSuchEntityException("Project " + projectId + " not found.");
        }

        try {
            Query query = getSession().getNamedQuery(
                    "PermissionsByProjectAndTypeQuery");
            query.setInteger("projectId", projectId);
            query.setParameter("permissionType", permissionType);
            permissions = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return permissions;
    }

}
