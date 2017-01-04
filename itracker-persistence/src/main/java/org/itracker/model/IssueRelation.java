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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.itracker.core.resources.ITrackerResources;

/**
 * A relation between issues.
 *
 * @author ready
 */
public class IssueRelation extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Issue issue;

    private Issue relatedIssue;

    /**
     * Indicates the kind of relation that exists between the 2 issues.
     */
    private Type type;

    private Integer matchingRelationId;

    /**
     * Default constructor (required by Hibernate).
     * <p/>
     * <p>
     * PENDING: should be <code>private</code> so that it can only be used by
     * Hibernate, to ensure that the fields which form an instance's identity
     * are always initialized/never <tt>null</tt>.
     * </p>
     */
    public IssueRelation() {
    }

    public IssueRelation(Issue issue, Issue relatedIssue, Type relationType) {
        setIssue(issue);
        setRelatedIssue(relatedIssue);
        setRelationType(relationType);
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("null issue");
        }
        this.issue = issue;
    }

    public Issue getRelatedIssue() {
        return relatedIssue;
    }

    public void setRelatedIssue(Issue relatedIssue) {
        if (relatedIssue == null) {
            throw new IllegalArgumentException("null relatedIssue");
        }
        this.relatedIssue = relatedIssue;
    }

    public Type getRelationType() {
        return type;
    }

    public void setRelationType(Type type) {
        this.type = type;
    }

    public Integer getMatchingRelationId() {
        return matchingRelationId;
    }

    public void setMatchingRelationId(Integer matchingRelationId) {
        this.matchingRelationId = matchingRelationId;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId())
                .append("issue", getIssue()).append("relatedIssue", getRelatedIssue())
                .append("type", getRelationType()).toString();
    }

    public static enum Type implements IntCodeEnum<Type> {

        /**
         * Defines a related issue. Sample text: related to
         */
        RELATED_P(1),

        /**
         * Defines a related issue. Sample text: related to
         */
        RELATED_C(2),

        /**
         * Defines a duplicate issue. Sample text: duplicates
         */
        DUPLICATE_P(3),

        /**
         * Defines a duplicate issue. Sample text: duplicate of
         */
        DUPLICATE_C(4),

        /**
         * Defines a cloned issue. Sample text: cloned to
         */
        CLONED_P(5),

        /**
         * Defines a cloned issue. Sample text: cloned from
         */
        CLONED_C(6),

        /**
         * Defines a split issue. Sample text: split to
         */
        SPLIT_P(7),

        /**
         * Defines a split issue. Sample text: split from
         */
        SPLIT_C(8),

        /**
         * Defines a dependent issue. Sample text: dependents
         */
        DEPENDENT_P(9),

        /**
         * Defines a dependent issue. Sample text: depends on
         */
        DEPENDENT_C(10);



        private final Integer code;

        private Type(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public Type fromCode(Integer code) {
            return Type.valueOf(code);
        }

        public static Type valueOf(Integer code) {
            for (Type val: values()) {
                if (val.code.compareTo(code) == 0) {
                    return val;
                }
            }
            throw new IllegalArgumentException("Unknown code : " + code);
        }
        /**
         * Returns the key for a particular issue relation type.
         *
         * @return the key for the item
         */
        public String getLanguageKeyCode() {
            String key = ITrackerResources.KEY_BASE_ISSUE_RELATION + getCode();
            return key;
        }


    }

}
