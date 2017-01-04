package org.itracker.persistence.dao;

import org.hibernate.HibernateException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Custom Hibernate UserType to persist a Java 5 enum constant as a VARCHAR
 * using its name.
 * <p/>
 * <p>Based on the original class by Gavin King
 * (http://www.hibernate.org/272.html). </p>
 *
 * @author johnny
 */
public class EnumNameUserType extends AbstractEnumUserType {

    private static final int[] SQL_TYPES = {Types.VARCHAR};

    /**
     * Default constructor, required by Hibernate.
     */
    public EnumNameUserType() {
    }

    @SuppressWarnings("unchecked")
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws HibernateException, SQLException {
        final String name = rs.getString(names[0]);

        return rs.wasNull() ? null : Enum.valueOf(this.enumClass, name);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, ((Enum<?>) value).name());
        }
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public String objectToSQLString(Object value) {
        return '\'' + ((Enum<?>) value).name() + '\'';
    }

    public String toXMLString(Object value) {
        return ((Enum<?>) value).name();
    }

    @SuppressWarnings("unchecked")
    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(this.enumClass, xmlValue);
    }

}
