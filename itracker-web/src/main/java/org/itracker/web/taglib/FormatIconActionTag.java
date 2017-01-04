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

package org.itracker.web.taglib;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts.taglib.TagUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.UserPreferences;
import org.itracker.web.util.Constants;
import org.itracker.web.util.HTMLUtilities;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.net.MalformedURLException;
import java.util.Locale;

public final class FormatIconActionTag extends BodyTagSupport {
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   private String action = null;
   private String forward = null;
   private String module = null;
   private String paramName = null;
   private String paramValue = null;
   private String icon = null;
   private String styleClass = null;
   private String iconClass = null;
   private String styleId = null;
   private String arg0 = null;
   private String textActionKey = null;
   private String info = null;
   private String caller = null;
   private String targetAction = null;
   private String target = null;

   public String getAction() {
      return action;
   }

   public void setAction(String value) {
      action = value;
   }

   public String getForward() {
      return forward;
   }

   public void setForward(String value) {
      forward = value;
   }

   public String getModule() {
      return module;
   }

   public void setModule(String module) {
      this.module = module;
   }

   public String getParamName() {
      return paramName;
   }

   public void setParamName(String value) {
      paramName = value;
   }

   public Object getParamValue() {
      return paramValue;
   }

   public void setParamValue(Object value) {
      paramValue = (value != null ? value.toString() : null);
   }

   public String getIcon() {
      return icon;
   }

   public void setIcon(String icon) {
      this.icon = icon;
   }

   public String getStyleClass() {
      return styleClass;
   }

   public void setStyleClass(String styleClass) {
      this.styleClass = styleClass;
   }

   public String getIconClass() {
      return iconClass;
   }

   public void setIconClass(String iconClass) {
      this.iconClass = iconClass;
   }

   public String getStyleId() {
      return styleId;
   }

   public void setStyleId(String styleId) {
      this.styleId = styleId;
   }

   public Object getArg0() {
      return arg0;
   }

   public void setArg0(Object value) {
      arg0 = (value != null ? value.toString() : null);
   }

   public String getTextActionKey() {
      return textActionKey;
   }

   public void setTextActionKey(String value) {
      textActionKey = value;
   }

   public String getInfo() {
      return info;
   }

   public void setInfo(String info) {
      this.info = info;
   }

   public String getCaller() {
      return caller;
   }

   public void setCaller(String value) {
      caller = value;
   }

   public String getTargetAction() {
      return targetAction;
   }

   public void setTargetAction(String value) {
      targetAction = value;
   }

   public String getTarget() {
      return target;
   }

   public void setTarget(String value) {
      target = value;
   }

   public int doStartTag() throws JspException {
      boolean useTextActions = false;

      HttpSession session = pageContext.getSession();
      if (session != null) {
         UserPreferences currUserPrefs = (UserPreferences) session
                 .getAttribute(Constants.PREFERENCES_KEY);
         useTextActions = (currUserPrefs != null
                 && currUserPrefs.getUseTextActions());
      }

      if (!useTextActions) {
         textActionKey = null;
      }
      if (Strings.isBlank(textActionKey))
         return EVAL_BODY_BUFFERED;
      return SKIP_BODY;
   }

   public int doEndTag() throws JspException {
      Locale locale = null;
      HttpSession session = pageContext.getSession();
      if (session != null) {
         locale = (Locale) session.getAttribute(Constants.LOCALE_KEY);
      }
      styleClass = StringUtils.defaultIfEmpty(styleClass, "");

      StringBuilder buf = new StringBuilder();
      appendStartTag(locale, buf);
      appendBody(locale, buf);
      appendEndTag(buf);
      return EVAL_PAGE;
   }

   private void appendBody(Locale locale, StringBuilder buf) {
      boolean hasBody = bodyContent != null && !StringUtils.isBlank(bodyContent.getString());
      if (hasBody) {
         icon = null;
         buf.append(bodyContent.getString());
      } else {
         if (!StringUtils.isBlank(textActionKey)) {
            buf.append(HTMLUtilities.escapeTags(ITrackerResources.getString(textActionKey, locale,
                    (arg0 == null ? "" : arg0))));
         } else if (!StringUtils.isBlank(icon)) {
            appendIcon(locale, buf);
         } else {
            buf.append("???");
         }
      }
   }

   private void appendEndTag(StringBuilder buf) throws JspException {
      buf.append("</a>");
      TagUtils.getInstance().write(pageContext, buf.toString());
      clearState();
   }

   private void appendIcon(Locale locale, StringBuilder buf) {

      buf.append("<i class=\"fa fa-").append(icon).append(null != iconClass ? " " + iconClass : "")
              .append("\" aria-hidden=\"true\"")
              .append(" ></i>");

      buf.append("<span class=\"sr-only\">")
              .append(HTMLUtilities.escapeTags(ITrackerResources
                      .getString(textActionKey, locale,
                              (arg0 == null ? "" : arg0))))
              .append("</span>");
   }

   private void appendStartTag(Locale locale, StringBuilder buf) {
      buf.append("<a href=\"");
      appendUrl(buf, pageContext, forward, action, module, paramName, paramValue, caller, targetAction);
      buf.append("\"");
      if (!StringUtils.isBlank(target)) {
         buf.append(" target=\"" + target + "\"");
      }
      if (!StringUtils.isBlank(styleId)) {
         buf.append(" id=\"" + styleId + "\"");
      }

      buf.append(" title=\"").append(
              HTMLUtilities.escapeTags(
                      ITrackerResources.getString(info, locale, StringUtils.trimToEmpty(arg0))
              ))
              .append("\"");

      styleClass += " action";
      if (!Strings.isBlank(icon) && Strings.isBlank(textActionKey)) {
         styleClass += " icon " + icon;
      }
      buf.append(!StringUtils.isBlank(StringUtils.trimToNull(styleClass)) ? " class=\"" + styleClass + "\"" : "")
              .append(">");
   }

   private static void appendUrl(StringBuilder buf, PageContext pageContext, String forward, String action, String module, String paramName, String paramValue, String caller, String targetAction) {
      boolean hasParams = false;
      try {
         buf.append(TagUtils.getInstance().computeURL(pageContext, forward,
                 null, null, action, module, null, null, false));
      } catch (MalformedURLException x) {
         buf.append(HTMLUtilities.escapeTags(forward));
      }
      if (paramName != null && paramValue != null) {
         buf.append("?" + paramName + "=" + paramValue);
         hasParams = true;
      }
      if (caller != null) {
         buf.append(hasParams ? "&amp;" : "?")
                 .append("caller=")
                 .append(HTMLUtilities.escapeTags(caller));
         hasParams = true;
      }
      if (targetAction != null) {
         buf.append(hasParams ? "&amp;" : "?")
                 .append("action=")
                 .append(HTMLUtilities.escapeTags(targetAction));
      }
   }


   public void release() {
      super.release();
      clearState();
   }

   private void clearState() {
      action = null;
      forward = null;
      paramName = null;
      paramValue = null;
      icon = null;
      styleClass = null;
      iconClass = null;
      styleId = null;
      arg0 = null;
      textActionKey = null;
      caller = null;
      target = null;
      targetAction = null;
      module = null;
   }
}
