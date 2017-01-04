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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.itracker.model.util.UserUtilities;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * A user.
 *
 * @author ready
 */
public class User extends AbstractEntity implements Comparable<Entity> {

   /**
    *
    */
   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger(User.class);
   public static final Comparator<User> NAME_COMPARATOR = new NameComparator();
   public static final Comparator<User> LOGIN_COMPARATOR = new LoginComparator();

   private String login;

   private String password;

   private String firstName;

   private String lastName;

   private String email;

   private int status;

   private boolean superUser;

   private int registrationType;

   /**
    * The Permissions of this User on all Projects.
    */
   private Set<Permission> permissions = new TreeSet<Permission>(Permission.PERMISSION_PROPERTIES_COMPARATOR);

   /**
    * The Projects owned by this User.
    */
   private List<Project> projects = new ArrayList<Project>();
   private UserPreferences preferences;

   public UserPreferences getPreferences() {
      return preferences;
   }

   public void setPreferences(UserPreferences preferences) {
      this.preferences = preferences;
   }

   /**
    * Default constructor (required by Hibernate).
    * <p/>
    * <p>
    * PENDING: should be <code>private</code> so that it can only be used by
    * Hibernate, to ensure that the fields which form an instance's identity
    * are always initialized/never <tt>null</tt>.
    * </p>
    */
   public User() {
   }

   public User(String login) {
      setLogin(login);
   }

   public User(String login, String password, String firstName,
               String lastName, String email, boolean superUser) {
      this(login, password, firstName, lastName, email,
              UserUtilities.REGISTRATION_TYPE_ADMIN, superUser);
   }

   public User(String login, String password, String firstName,
               String lastName, String email, int registrationType,
               boolean superUser) {
      this(login);
      this.password = password;
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.registrationType = registrationType;
      setSuperUser(superUser);
   }

   public String getLogin() {
      return login;
   }

   public void setLogin(String login) {
      if (login == null) {
         throw new IllegalArgumentException("null login");
      }
      this.login = login;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String value) {
      this.password = value;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String value) {
      firstName = value;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(String value) {
      this.lastName = value;
   }

   public String getFullName() {
      return StringUtils.defaultString(
              StringUtils.trimToNull(
                      (StringUtils.isNotBlank(getFirstName()) ? getFirstName() : "")
                              + " " + (StringUtils.isNotBlank(getLastName()) ? getLastName() : "")
              ), getLogin());
   }

   public String getEmail() {
      return email;
   }

   public InternetAddress getEmailAddress() {

      if (null == getEmail() || getEmail().trim().length() == 0) {
         log.warn("getEmailAddress: failed to get eMail for user "
                 + getLogin() + " (" + getId() + ")");
         return null;
      }
      final InternetAddress adr;
      try {
         adr =  new InternetAddress(getEmail());
         try {
            adr.setPersonal(getFullName(), "utf-8");
         } catch (UnsupportedEncodingException e) {
            log.warn("getEmailAddress: unsupported encoding for setting personal: utf-8", e);
         }
         return adr;
      } catch (AddressException e1) {
         log.error("getEmailAddress: failed to parse email '"
                 + getEmail() + "' for user " + getLogin() + " ("
                 + getId() + "), returning null", e1);
         return null;
      }

   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Set<Permission> getPermissions() {
      return permissions;
   }

   public void setPermissions(Set<Permission> getPermissions) {
      this.permissions = getPermissions;
   }


   public int getRegistrationType() {
      return registrationType;
   }

   public void setRegistrationType(int registrationType) {
      this.registrationType = registrationType;
   }

   public int getStatus() {
      return status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public boolean isSuperUser() {
      return superUser;
   }

   public void setSuperUser(boolean superUser) {
      this.superUser = superUser;
   }

   public String getFirstInitial() {
      return (null != getFirstName() && getFirstName().length() > 0 ? getFirstName().substring(0, 1)
              .toUpperCase()
              + "." : "");
   }

   public boolean hasRequiredData() {
      return hasRequiredData(true);
   }

   public boolean hasRequiredData(boolean passwordRequired) {
      if (this.getLogin() == null || this.getLogin().equals("")
              || this.getFirstName() == null
              || this.getFirstName().equals("") || this.getLastName() == null
              || this.getLastName().equals("") || this.getEmail() == null
              || this.getEmail().equals("")) {
         return false;
      }
      if (passwordRequired
              && (this.getPassword() == null || this.getPassword().equals(""))) {
         return false;
      }
      return true;
   }

   public List<Project> getProjects() {
      return projects;
   }

   public void setProjects(List<Project> projects) {
      this.projects = projects;
   }

   @Override
   public String toString() {
      return new ToStringBuilder(this).append("id", getId())
              .append("login", getLogin()).toString();
   }

   /**
    * Compares 2 users by last and first name.
    */
   private static class NameComparator implements Comparator<User> {

      public int compare(User a, User b) {
         return new CompareToBuilder().append(a.getLastName(), b.getLastName())
                 .append(a.getFirstName(), b.getFirstName()).append(a, b, LOGIN_COMPARATOR).toComparison();
      }

   }

   public static final class LoginComparator implements Comparator<User> {
      public int compare(User o1, User o2) {
         return new CompareToBuilder().append(o1.getLogin(), o2.getLogin())
                 .toComparison();
      }
   }

}
