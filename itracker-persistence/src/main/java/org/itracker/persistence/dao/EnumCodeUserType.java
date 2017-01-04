package org.itracker.persistence.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.itracker.model.IntCodeEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Custom Hibernate UserType to persist a Java 5 enum constant
 * that implement {@link org.itracker.model.IntCodeEnum } as an INTEGER
 * using its integer code.
 * <p/>
 * <p>This should be preferred to using the enum.ordinal() or enum.name(). </p>
 *
 * @author johnny
 */
public class EnumCodeUserType extends AbstractEnumUserType {

    private static final int[] SQL_TYPES = {Types.SMALLINT};
    private static final Logger log = Logger.getLogger(EnumCodeUserType.class);
    /**
     * Enum members, in the order they where declared.
     */
    @SuppressWarnings("unchecked")
    private IntCodeEnum[] enumValues;

    /**
     * Default constructor, required by Hibernate.
     */
    public EnumCodeUserType() {
    }

    public void setParameterValues(Properties parameters) {
        super.setParameterValues(parameters);
        this.enumValues = (IntCodeEnum[]) this.enumClass.getEnumConstants();
    }

    public Object nullSafeGet(ResultSet rs, String[] names,
                              Object owner) throws HibernateException, SQLException {
        final int code = rs.getInt(names[0]);

        /* It is safe to assume there's always at least 1 enum constant
        * because the compiler ensures that enums are never empty.
        *
        * Note : fromCode cannot be static in IntEnumCode !
        */
        try {
            return rs.wasNull() ? null : this.enumValues[0].fromCode(code);
        } catch (Exception e) {
            log.info("nullSafeGet: failed to get default code enum, trying DEFAULT-code " + IntCodeEnum.DEFAULT_CODE, e);
            return this.enumValues[0].fromCode(IntCodeEnum.DEFAULT_CODE);
        }
    }

    public void nullSafeSet(PreparedStatement stmt, Object value,
                            int index) throws HibernateException, SQLException {
        if (value == null) {
            stmt.setNull(index, Types.INTEGER);
        } else {
            stmt.setInt(index, ((IntCodeEnum<?>) value).getCode());
        }
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public String objectToSQLString(Object value) {
        return Integer.toString(((IntCodeEnum<?>) value).getCode());
    }

    public String toXMLString(Object value) {
        return objectToSQLString(value);
    }

    public Object fromXMLString(String xmlValue) {
        final int code;

        try {
            code = Integer.parseInt(xmlValue);
        } catch (NumberFormatException ex) {
            throw new HibernateException(ex);
        }

        /* It is safe to assume there's always at least 1 enum constant
        * because the compiler ensures that enums are never empty.
        *
        * Note : fromCode cannot be static in IntEnumCode !
        */
        return this.enumValues[0].fromCode(code);
    }

}
