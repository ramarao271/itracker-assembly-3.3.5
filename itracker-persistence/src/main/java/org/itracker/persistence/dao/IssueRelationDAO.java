package org.itracker.persistence.dao;

import org.itracker.model.IssueRelation;

import java.util.List;

/**
 *
 */
public interface IssueRelationDAO extends BaseDAO<IssueRelation> {

    IssueRelation findByPrimaryKey(Integer relationId);

    List<IssueRelation> findByIssue(Integer issueId);

}
