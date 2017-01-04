/**
 * This package contains all Data Access Object (XxxDAO) interfaces 
 * along with their Hibernate implementations (XxxDAOImpl). 
 *
 * <p>DAO interfaces are implementation independent&nbsp;:  
 * all DAO methods throw <code>org.springframework.dao.DataAccessException</code> 
 * instead of implementation-specific exceptions like 
 * <code>java.sql.SQLException</code> or 
 * <code>org.hibernate.HibernateException</code>. 
 * <br>
 * Since this is a general convention, DAO methods don't need to specify it  
 * in their <code>throws</code> clause. </p>
 *
 * <p>DAO implementations that don't use Spring's <code>HibernateTemplate</code>
 * and callbacks must explicitly catch <code>HibernateException</code>s 
 * and convert them to <code>DataAccessException</code>s by wrapping 
 * all Hibernate code that can throw such exceptions like this: 
 * <pre>
 *      try {
 *          getSession()...
 *      } catch (HibernateException ex) {
 *          throw convertHibernateAccessException(ex);
 *      }
 * </pre>
 * </p>
 *
 * <p>Session management (opening and closing the Hibernate session) is handled 
 * automatically by the <code>HibernateTransactionManager</code> 
 * or an AOP interceptor, if a DAO method is invoked outside the scope 
 * of a transaction. </p>
 *
 * @since 3.0
 */
package org.itracker.persistence.dao;
