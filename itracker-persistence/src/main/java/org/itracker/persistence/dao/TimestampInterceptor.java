package org.itracker.persistence.dao;

import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.itracker.model.AbstractEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * A Hibernate Interceptor that automatically sets the <code>createDate</code>
 * and <code>lastModifiedDate</code> of any AbstractEntity instance that is
 * inserted or updated.
 *
 * @author johnny
 */
public class TimestampInterceptor extends EmptyInterceptor {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the AbstractEntity.createDate property
     */
    private static final String CREATE_DATE_PROPERTY = "createDate";

    /**
     * The name of the AbstractEntity.lastModifiedDate property
     */
    private static final String LAST_MODIFIED_DATE_PROPERTY = "lastModifiedDate";

    @SuppressWarnings("unused")
    private static final transient Logger logger = Logger.getLogger(TimestampInterceptor.class);

    /**
     *
     */
    public TimestampInterceptor() {
    }

    /**
     * Called before inserting an item in the datastore.
     * <p/>
     * <p>The interceptor may modify the state, which will be used for
     * the SQL INSERT and propagated to the persistent object. </p>
     * <p/>
     * <p>Automatically sets the createDate and lastModifiedDate properties. </p>
     *
     * @return true if the timestamp was set and thus the state was modified
     */
    public boolean onSave(Object entity, Serializable id,
                          Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof AbstractEntity) {
            final Date timestamp = new Date();

            // Number of properties to set. 
            int propertiesSet = 0;

            // Find createDate property to set. 
            for (int i = 0; i < propertyNames.length; i++) {

                if (CREATE_DATE_PROPERTY.equals(propertyNames[i])
                        || LAST_MODIFIED_DATE_PROPERTY.equals(propertyNames[i])) {
                    state[i] = timestamp;

//                    if (this.logger.isDebugEnabled()) {
//                        this.logger.debug("Setting " + propertyNames[i] 
//                                + " property of " + entity);
//                    }

                    if (++propertiesSet == 2) {
                        break;
                    }
                }
            }
            return (propertiesSet > 0);
        }
        return false;
    }

    /**
     * Called before updating the datastore.
     * <p/>
     * <p>Called when an object is detected to be dirty, during a flush.
     * The interceptor may modify the detected currentState, which will be
     * propagated to both the database and the persistent object. <br>
     * Note that not all flushes end in actual synchronization
     * with the database, in which case the new currentState will be
     * propagated to the object, but not necessarily (immediately)
     * to the database. It is strongly recommended that the interceptor
     * not modify the previousState. </p>
     * <p/>
     * <p>Automatically sets the lastModifiedDate property. </p>
     *
     * @return true if the timestamp was set and thus the currentState was modified
     */
    public boolean onFlushDirty(Object entity, Serializable id,
                                Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types) {

        if (entity instanceof AbstractEntity) {
            final Date timestamp = new Date();

            // Find lastModifiedDate property to set. 
            for (int i = 0; i < propertyNames.length; i++) {

                if (LAST_MODIFIED_DATE_PROPERTY.equals(propertyNames[i])) {
                    currentState[i] = timestamp;

//                    if (this.logger.isDebugEnabled()) {
//                        this.logger.debug("Setting " + propertyNames[i] 
//                                + " property of " + entity);
//                    }
                    return true;
                }
            }
        }
        return false;
    }

}
