package org.itracker.persistence.dao;

import org.itracker.model.CustomFieldValue;

import java.util.List;

/**
 *
 */
public interface CustomFieldValueDAO extends BaseDAO<CustomFieldValue> {

    public CustomFieldValue findByPrimaryKey(Integer customFieldId);

    public List<CustomFieldValue> findAll();

}
