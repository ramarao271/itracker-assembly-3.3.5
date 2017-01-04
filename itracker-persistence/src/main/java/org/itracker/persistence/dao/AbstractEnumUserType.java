package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

import java.io.Serializable;
import java.util.Properties;

/**
 * Base class for custom Hibernate UserTypes to persist a Java 5 enum
 * constant.
 * <p/>
 * <p>This is a parameterized type, which requires the following parameter :
 * <code>enumClassName</code> = fully qualified name of the enum class
 * to persist. </p>
 * <p/>
 * <p>Example of inline mapping : </p>
 * <pre>
 *  <property name='suit'>
 *   <column name="suit"/>
 *   <type name="EnumUserType">
 *     <param name="enumClassName">com.company.project.Suit</param>
 *   </type>
 *  </property>
 * </pre>
 * <p/>
 * <p>Example of typedef : </p>
 * <pre>
 *  <typedef name="suit" class='EnumUserType'>
 *    <param name="enumClassName">com.company.project.Suit</param>
 *  </typedef>
 *
 *  <class ...>
 *   <column name="suit"/>
 *   <property name='suit' type='suit'/>
 *  </class>
 * </pre>
 *
 * @author johnny
 */
public abstract class AbstractEnumUserType
        implements EnhancedUserType, ParameterizedType {

    @SuppressWarnings("unchecked")
    protected Class<? extends Enum> enumClass;

    public AbstractEnumUserType() {
    }

    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            this.enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
        } catch (ClassNotFoundException ex) {
            throw new HibernateException("Enum class not found", ex);
        }
    }

    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum<?>) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return original;
    }

    public Class<?> returnedClass() {
        return this.enumClass;
    }

}
