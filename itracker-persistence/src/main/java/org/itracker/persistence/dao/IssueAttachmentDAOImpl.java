package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.IssueAttachment;

import java.util.List;

/**
 * Persistence Hibernate POJO
 *
 * @author mbae, ready
 */
public class IssueAttachmentDAOImpl extends BaseHibernateDAOImpl<IssueAttachment>
        implements IssueAttachmentDAO {

    public IssueAttachment findByPrimaryKey(Integer attachmentId) {
        try {
            return (IssueAttachment) getSession().get(IssueAttachment.class,
                    attachmentId);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
    }

    public IssueAttachment findByFileName(String fileName) {
        IssueAttachment attachment;

        try {
            Query query = getSession().getNamedQuery(
                    "AttachmentByFileNameQuery");
            query.setString("fileName", fileName);
            attachment = (IssueAttachment) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return attachment;
    }

    @SuppressWarnings("unchecked")
    public List<IssueAttachment> findAll() {
        List<IssueAttachment> attachments;

        try {
            Query query = getSession().getNamedQuery(
                    "AttachmentsAllQuery");
            attachments = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return attachments;
    }

    @SuppressWarnings("unchecked")
    public List<IssueAttachment> findByIssue(Integer issueId) {
        List<IssueAttachment> attachments;

        try {
            Query query = getSession().getNamedQuery(
                    "AttachmentsByIssueQuery");
            query.setInteger("issueId", issueId);
            attachments = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return attachments;
    }

    public Long countAll() {
        Long count;
        try {
            Query query = getSession().getNamedQuery(
                    "AttachmentsCountAllQuery");
            count = (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return count;
    }

    public Long totalAttachmentsSize() {
        Long count;
        try {
            Query query = getSession().getNamedQuery(
                    "TotalAttachmentsSizeQuery");
            count = (Long) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        if (count == null) {
            count = 0L;
        }

        return count;
    }
}
