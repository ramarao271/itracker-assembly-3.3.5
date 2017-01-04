package org.itracker.persistence.dao;

import org.itracker.model.IssueAttachment;

import java.util.List;

/**
 *
 */
public interface IssueAttachmentDAO extends BaseDAO<IssueAttachment> {

    /**
     * @param attachmentId system ID
     * @return attachment instance or <tt>null</tt> if none exists
     */
    IssueAttachment findByPrimaryKey(Integer attachmentId);

    /**
     * Finds an attachment by the file name on the filesystem.
     *
     * @return attachment instance or <tt>null</tt> if none exists
     */
    IssueAttachment findByFileName(String fileName);

    /**
     * Finds all Issue attachments.
     *
     * @return list of all issue attachments, in unspecified order
     */
    List<IssueAttachment> findAll();

    /**
     * Counts all Issue attachments.
     *
     * @return count of all issue attachments in system
     */

    Long countAll();

    /**
     * Calculates the total Size of attachments in the system.
     *
     * @return total size of issue attachments
     */
    Long totalAttachmentsSize();

    /**
     * Finds all attachments for an Issue.
     *
     * @param issueId system ID
     * @return list of all issue attachments, in unspecified order
     */
    List<IssueAttachment> findByIssue(Integer issueId);

}
