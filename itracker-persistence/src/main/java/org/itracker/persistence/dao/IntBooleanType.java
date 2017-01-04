package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * A Hibernate <code>UserType</code> to map a Java boolean or java.lang.Boolean
 * property to an SQL column of type INT with the values 0 (= false) or 1 (= true).
 * <p/>
 * <p>This is necessary because of the legacy ITracker 2.x database schema
 * which uses INT instead of BOOLEAN or BIT, which aren't supported by all
 * databases (e.g. DB2). </p>
 *
 * @author johnny
 */
public final class IntBooleanType implements UserType {

    private static final int[] SQL_TYPES = {Types.INTEGER};

    /**
     * Default constructor, required by Hibernate.
     */
    public IntBooleanType() {
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            final int intValue = ((Boolean) value).booleanValue() ? 1 : 0;
            statement.setInt(index, intValue);
        }
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
            throws HibernateException, SQLException {
        final int value = resultSet.getInt(names[0]);
        return resultSet.wasNull() ? null : (value == 1);
    }

    public int hashCode(Object x) throws HibernateException {
        /* Apparently, there's no need to check for nulls here. */
        assert (x != null);

        return x.hashCode();
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class<Boolean> returnedClass() {
        return Boolean.class;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public boolean isMutable() {
        return false;
    }

    /**
     * Implements persistence equality.
     */
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }

        if (x == null || y == null) {
            return false; // test (x == y) has been done before and was false. 
        }
        return x.equals(y);
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

}
