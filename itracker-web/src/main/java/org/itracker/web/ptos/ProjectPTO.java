package org.itracker.web.ptos;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.itracker.model.PermissionType;
import org.itracker.model.Status;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ProjectService;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ProjectPTO {

    private Long totalOpenIssues = null;
    private Long totalResolvedIssues = null;
    private Date lastUpdatedIssueDate = null;
    private Boolean canCreate = null;
    private Date createDate;
    private Boolean viewable;
    private String description;
    private Integer id;
    private Date modifiedDate;
    private Status status;
    private String name;


    public ProjectPTO() {
    }

    public Long getTotalNumberIssues() {
        return getTotalOpenIssues() + getTotalResolvedIssues();
    }

    public void setTotalNumberIssues(Long totalNumberIssues) {
        setTotalOpenIssues(totalNumberIssues);
        setTotalResolvedIssues(0l);
    }

    public void setTotalOpenIssues(Long totalOpenIssues) {
        this.totalOpenIssues = totalOpenIssues;
    }

    public Long getTotalOpenIssues() {
        return totalOpenIssues;
    }

    public void setTotalResolvedIssues(Long totalResolvedIssues) {
        this.totalResolvedIssues = totalResolvedIssues;
    }

    public Long getTotalResolvedIssues() {
        return totalResolvedIssues;
    }

    /**
     * @see org.itracker.model.AbstractEntity#getCreateDate()
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @see org.itracker.model.Project#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see org.itracker.model.AbstractEntity#getId()
     */
    public Integer getId() {
        return id;
    }

    /**
     * @see org.itracker.model.AbstractEntity#getLastModifiedDate()
     */
    public Date getLastModifiedDate() {
        return modifiedDate;
    }

    /**
     * @see org.itracker.model.Project#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.itracker.model.Project#getStatus()
     */
    public Status getStatus() {
        return this.status;
    }

    public Boolean getActive() {
        return getStatus() == Status.ACTIVE;
    }

    public Boolean isActive() {
        return getActive();
    }

    public Boolean getViewable() {
        return viewable;
    }

    public Boolean isViewable() {
        return getViewable();
    }

    public Boolean getCanCreate() {
        return this.canCreate;
    }

    public Boolean isCanCreate() {
        return getCanCreate();
    }

    public void setCanCreate(Boolean canCreate) {
        this.canCreate = canCreate;
    }


    public void setLastUpdatedIssueDate(Date lastUpdatedIssueDate) {
        this.lastUpdatedIssueDate = lastUpdatedIssueDate;
    }

    public Date getLastUpdatedIssueDate() {
        return lastUpdatedIssueDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(getName()).toString();
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setViewable(Boolean viewable) {
        this.viewable = viewable;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }
}
