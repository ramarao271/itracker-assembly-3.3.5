package org.itracker.persistence.dao;

import org.itracker.model.Report;

import java.util.List;

/**
 *
 */
public interface ReportDAO extends BaseDAO<Report> {

    /**
     * Finds a report by id.
     *
     * @param reportId system ID
     * @return report instance or <tt>null</tt> if none exists with the given id
     */
    Report findByPrimaryKey(Integer reportId);

    /**
     * Finds all existing reports.
     *
     * @return list of reports in unspecified order
     */
    List<Report> findAll();

}
