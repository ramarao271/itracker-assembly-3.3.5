package org.itracker.model;

import java.io.Serializable;
import java.util.Date;

/**
 * All database entities must implement this interface, which represents
 * the minimum requirements to meet.
 * <p/>
 * <p>A database entity always has an <code>Integer</code> surrogate key
 * (system ID). It must also be Serializable and Cloneable. </p>
 * <p/>
 * <p>A database entity class must also fulfill the following requirements,
 * that cannot be expressed by a Java interface : </p>
 * <ul>
 * <li>be a Java bean ; </li>
 * <li>be non-final. </li>
 * </ul>
 *
 * @author johnny
 * @see AbstractEntity
 */
public interface Entity extends Serializable, Cloneable, Comparable<Entity> {

    /* PENDING : should createDate and lastModifiedDate properties 
* be part of this interface ?
* Should we add createdBy and lastModifiedBy ? */

    /**
     * Returns the system ID.
     *
     * @return ID &gt; 0 for persistent entities,
     *         <tt>null</tt> for transient ones
     */
    Integer getId();

    /**
     * Sets this entity's system ID.
     *
     * @param id ID &gt; 0 for persistent entities,
     *           <tt>null</tt> for transient ones
     */
    void setId(Integer id);

    void setLastModifiedDate(Date date);

    Date getLastModifiedDate();

    void setCreateDate(Date date);

    Date getCreateDate();

    boolean isNew();
}
