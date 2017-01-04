/**
 *   All entity classes are POJO beans. 
 *
 * Entity classes are never declared <code>final</code> so that 
 * they can be persisted with Hibernate. 
 *
 * All entity class inherit AbstractBean, but use implicit polymorphism 
 * in the Hibernate mapping files : this means that all concrete subclasses 
 * must declare persistent fields inherited from AbstractBean. 
 * See Hibernate reference chapter 9 Inheritence Mapping and particularly 
 * 9.1.6. Table per concrete class, using implicit polymorphism
 *
 */
package org.itracker.model;
