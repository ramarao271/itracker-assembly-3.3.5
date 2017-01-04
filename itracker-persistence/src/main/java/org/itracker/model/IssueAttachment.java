/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.model;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A file attachment to an Issue.
 * <p/>
 * <p>
 * An IssueAttachment can only belong to 1 Issue (composition).
 * </p>
 *
 * @author ready
 */
public class IssueAttachment extends AbstractEntity implements
        Comparable<Entity> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Compares 2 attachments by file size.
     */
    public static final Comparator<IssueAttachment> SIZE_COMPARATOR = new SizeComparator();
    /**
     * Compares 2 attachments by original filename.
     */
    public static final Comparator<IssueAttachment> ORIGIINAL_FILENAME_COMPARATOR = new OriginalFilenameComparator();

    /**
     * The issue to which the file is attached.
     */
    private Issue issue;

    /**
     * The file name used to upload the attachment.
     */
    private String originalFileName;

    /**
     * Globally unique file name constructed from the concatenation of the issue
     * id and original file name.
     * <p/>
     * PENDING: remove this computed field.
     */
    private String fileName;

    /**
     * MIME type.
     */
    private String type;

    /**
     * Byte size.
     */
    private long size;

    /**
     * Attachment description or comment.
     */
    private String description;

    /**
     * PENDING: this should probably not be saved in the DB nor be loaded in
     * memory for good resource management.
     */
    private byte[] fileData;

    /**
     * The User who created this attachment.
     */
    private User user;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public IssueAttachment() {
    }

    public IssueAttachment(Issue issue, String originalFileName) {
        setIssue(issue);
        setOriginalFileName(originalFileName);
    }

    /**
     * Convenience constructor.
     */
    public IssueAttachment(Issue issue, String origFileName, String type,
                           String description, long size) {
        this(issue, origFileName);
        this.setType(type);
        this.setDescription(description);
        this.setSize(size);
    }

    /**
     * Convenience constructor.
     */
    public IssueAttachment(Issue issue, String origFileName, String type,
                           String description, long size, User user) {
        this(issue, origFileName, type, description, size);
        this.setUser(user);
    }

    public Issue getIssue() {
        return (issue);
    }

    public void setIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("null issue");
        }
        this.issue = issue;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }
        this.originalFileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String mimeType) {
        if (mimeType == null) {
            throw new IllegalArgumentException("null mimeType");
        }
        this.type = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String value) {
        this.fileName = value;
    }

    public String getFileExtension() {
        final int lastIndex = this.getOriginalFileName().lastIndexOf('.');

        if (lastIndex > 0) {
            return this.getOriginalFileName().substring(lastIndex);
        }
        return "";
    }

    public byte[] getFileData() {
        if (null == fileData)
            return null;
        return fileData.clone();
    }

    public void setFileData(byte[] value) {
        if (null == value)
            throw new IllegalArgumentException("value must not be null");
        fileData = value.clone();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("issue",
                getIssue()).append("originalfileName", getOriginalFileName())
                .toString();
    }

    /**
     * Compares 2 attachments by file size.
     */
    public static class SizeComparator implements Comparator<IssueAttachment>,
            Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(IssueAttachment a, IssueAttachment b) {
            return new CompareToBuilder()
                    .append(a.getSize(), b.getSize())
                    .append(a.getOriginalFileName(), b.getOriginalFileName())
                    .append(a.getCreateDate(), b.getCreateDate())
                    .toComparison();
        }

    }

    /**
     * Compares 2 attachments by original filename
     *
     * @author ranks
     */
    public static final class OriginalFilenameComparator implements
            Comparator<IssueAttachment>, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int compare(IssueAttachment o1, IssueAttachment o2) {
            return new CompareToBuilder()
                    .append(o1.getOriginalFileName(), o1.getOriginalFileName())
                    .append(o1.getCreateDate(), o2.getCreateDate())
                    .toComparison();

        }

    }

}
