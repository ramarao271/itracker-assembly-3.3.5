package org.itracker.persistence.dao;

import org.itracker.model.UserPreferences;

/**
 *
 */
public interface UserPreferencesDAO extends BaseDAO<UserPreferences> {

    public UserPreferences findByUserId(Integer userId);

}
