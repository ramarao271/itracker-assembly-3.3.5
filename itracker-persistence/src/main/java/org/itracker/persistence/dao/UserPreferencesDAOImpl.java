package org.itracker.persistence.dao;

import org.itracker.model.User;
import org.itracker.model.UserPreferences;

/**
 *
 */
public class UserPreferencesDAOImpl extends BaseHibernateDAOImpl<UserPreferences>
        implements UserPreferencesDAO {

    private UserDAO userDAO;

    public UserPreferences findByUserId(Integer userId) {

        User user = userDAO.findByPrimaryKey(userId);

        return user.getPreferences();

    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

}
