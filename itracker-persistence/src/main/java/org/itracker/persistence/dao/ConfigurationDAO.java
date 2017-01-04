package org.itracker.persistence.dao;

import org.itracker.model.Configuration;

import java.util.List;

/**
 *
 */
public interface ConfigurationDAO extends BaseDAO<Configuration> {

    public Configuration findByPrimaryKey(Integer id);

    public List<Configuration> findByType(Configuration.Type type);

    /**
     *
     * @param type
     * @return
     * @deprecated use findByType(Configuration.Type type)
     */
    public List<Configuration> findByType(int type);

    @Deprecated
    public List<Configuration> findByTypeAndValue(int type, String value);

    /**
     *
     * @param type the type
     * @param value the value
     * @return identified configuration item
     */
    public Configuration findByTypeValueKey(Configuration.Type type, String value);
    /**
     *
     * @param type the type
     * @param value the value
     * @return list with the identified configuration item
     * @deprecated use findByTypeValueKey(int type, String value)
     */
    public List<Configuration> findByTypeAndValue(Configuration.Type type, String value);

}