package org.itracker.persistence.dao;

import org.itracker.model.IssueHistory;

import java.util.List;

/**
 *
 *
 */
public interface IssueHistoryDAO extends BaseDAO<IssueHistory> {

    /**
     * Returns the issue history entry with the given primary key.
     *
     * @param entryId system ID
     * @return issue history entry or <tt>null</tt>
     */
    IssueHistory findByPrimaryKey(Integer entryId);

    /**
     * Finds all history entries for an Issue.
     *
     * @param issueId system ID
     * @return list of history entries in unspecified order
     */
    List<IssueHistory> findByIssueId(Integer issueId);

}
