package org.itracker.persistence.dao;

import org.itracker.model.CustomField;

import java.util.List;


public interface CustomFieldDAO extends BaseDAO<CustomField> {

    public CustomField findByPrimaryKey(Integer customFieldId);

    public List<CustomField> findAll();

}
