package org.itracker.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.itracker.model.Permission;
import org.itracker.model.PermissionType;
import org.itracker.model.User;

import java.util.*;

/**
 *
 */
public class UserDAOImpl extends BaseHibernateDAOImpl<User> implements UserDAO {

    public User findByPrimaryKey(Integer userId) {
        User user;

        try {
            user = (User) getSession().get(User.class, userId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return user;
    }

    public User findByLogin(String login) {
        User user;

        try {
            Query query = getSession().getNamedQuery("UserByLoginQuery");
            query.setString("login", login);
            user = (User) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return user;
    }

    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        List<User> users;

        try {
            Query query = getSession().getNamedQuery("UsersAllQuery");
            users = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    public List<User> findActive() {
        List<User> users;

        try {
            Query query = getSession().getNamedQuery("UsersActiveQuery");
            users = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    public List<User> findByStatus(int status) {
        List<User> users;

        try {
            Query query = getSession().getNamedQuery("UsersByStatusQuery");
            query.setInteger("userStatus", status);
            users = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    public List<User> findSuperUsers() {
        List<User> users;

        try {
            Query query = getSession().getNamedQuery("UsersSuperQuery");
            users = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    public List<User> findByRegistrationType(int registrationType) {
        List<User> users;

        try {
            Query query = getSession().getNamedQuery("UsersByRegistrationTypeQuery");
            query.setInteger("registrationType", registrationType);
            users = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return users;
    }

    /**
     * Searches all permissions for the given user and sorts it by project. The
     * <code>HashMap</code> returned has the project ids as key (<code>Integer</code>)
     * and a <code>HashSet</code> as values. The <code>HashSet</code> holds a set of
     * string representation of the permission
     *
     * @param user          The user of interest
     * @return HashMap of permission keyed by project ids
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, Set<PermissionType>> getUsersMapOfProjectsAndPermissionTypes(User user) {

        // create hashMap to hold permission by project id as key
        final Map<Integer, Set<PermissionType>> permissionsByProjectId =
                new HashMap<Integer, Set<PermissionType>>();

        try {
            // load user bean
            User userBean = (User) getSession().load(User.class, user.getId());
            // create criteria
            Criteria criteria = getSession().createCriteria(Permission.class);
            criteria.add(Expression.eq("user", userBean));
            criteria.addOrder(Order.asc("project"));
            // perform search
            List<Permission> permissionsList = criteria.list();

            for (int i = 0; i < permissionsList.size(); i++) {
                Permission permission = permissionsList.get(i);

                // Super user has access to all projects, which is indicated by the "null" project. 
                final Integer projectId = (permission.getProject() == null)
                        ? null : permission.getProject().getId();

                Set<PermissionType> projectPermissions = permissionsByProjectId.get(projectId);

                if (projectPermissions == null) {
                    // First permission for the project. 
                    projectPermissions = new HashSet<PermissionType>();
                    permissionsByProjectId.put(projectId, projectPermissions);
                } //else { // Add the permission to the existing set of permissions for the project. }

                PermissionType permissionType = permission.getPermissionType();
                projectPermissions.add(permissionType);
            }
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return permissionsByProjectId;
    }


    @SuppressWarnings("unchecked")
    public List<User> findUsersForProjectByAllPermissionTypeList(Integer projectID, Integer[] permissionTypes) {
        PermissionType[] permissions = null;
        if (null != permissionTypes) {
            permissions = new PermissionType[permissionTypes.length];
            int c = 0;
            for (int p : permissionTypes) {
                permissions[c++] = PermissionType.valueOf(p);
            }
        }
        return findUsersForProjectByAllPermissionTypeList(projectID, permissions);
    }

    @Override
    public List<User> findUsersForProjectByAllPermissionTypeList(Integer projectID, PermissionType[] permissionTypes) {
        List<User> users = new ArrayList<User>();

        try {

            DetachedCriteria userCriteria = DetachedCriteria.forClass(User.class);
            userCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            DetachedCriteria permissionCriteria = userCriteria.createCriteria("permissions");

            permissionCriteria.add(Restrictions.in("permissionType", permissionTypes));
            permissionCriteria.add(Restrictions.eq("project.id", projectID));

            List<User> userList = userCriteria.getExecutableCriteria(getSession()).list();

            for (User user : userList) {
                if (hasAllPermissions(user.getPermissions(), permissionTypes)) {
                    users.add(user);
                }
            }

        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        return users;

    }

    private boolean hasAllPermissions(Collection<Permission> permissions, PermissionType[] required) {
        if (null == required || required.length == 0) {
            return true;
        }
        if (null == permissions || permissions.isEmpty()) {
            return false;
        }

        Collection<PermissionType> requiredPermissions = Arrays.asList(required);
        Collection<PermissionType> userPermissionTypes = new HashSet<>(permissions.size());

        Iterator<Permission> permsIt = permissions.iterator();
        while (permsIt.hasNext()) {
            userPermissionTypes.add(permsIt.next().getPermissionType());
        }

        return userPermissionTypes.containsAll(requiredPermissions);
    }

}
