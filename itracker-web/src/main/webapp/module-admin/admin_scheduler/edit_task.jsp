<%@ include file="/common/taglibs.jsp"%>

<% // TODO : move redirect logic to the Action class.
  //  ScheduledTask task = (ScheduledTask) session.getAttribute(Constants.TASK_KEY);
   // if(task == null) {
%>
      <logic:forward name="unauthorized"/>
<%
  //  } else {
      boolean isUpdate = false;
   //   if(task.getId().intValue() > 0) {
   //       isUpdate = true;
  //    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>
      <html:form action="/edittask">
        <html:hidden property="action"/>
        <html:hidden property="id"/>

        <table border="0" cellspacing="0"  cellspacing="1"  width="100%">
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.predefinedtask"/>:</td>
            <td>
              <html:select property="className" styleClass="editColumnText">
                <option value=""></option>
                <%
    //               java.util.HashMap<String,String> classes = SchedulerUtilities.getDefinedTasks();
       //            for(java.util.Iterator<String> iter = classes.keySet().iterator(); iter.hasNext(); ) {
        //              String className = iter.next();
      //                String classNameKey = classes.get(className);
                %>
                      <html:option value="<%-- = className %>" styleClass="editColumnText"><%--<it:message key="<%= classNameKey %>--%>"/>--%></html:option>
                <% } %>
              </html:select>
            </td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
            <td class="editColumnText"><%--<it:formatDate date="<%= task.getCreateDate() %>"/>--%></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.class"/>:</td>
            <td><html:text property="classNameText" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
            <td class="editColumnText"><%--<it:formatDate date="<%= task.getLastModifiedDate() %>"/>--%></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.arguments"/>:</td>
            <td colspan="3"><html:text property="args" styleClass="editColumnText"/></td>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td></tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.months"/>:</td>
            <td><html:text property="months" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.daysofmonth"/>:</td>
            <td><html:text property="daysOfMonth" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.hours"/>:</td>
            <td><html:text property="hours" styleClass="editColumnText"/></td>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.minutes"/>:</td>
            <td><html:text property="minutes" styleClass="editColumnText"/></td>
          </tr>
          <tr>
            <td class="editColumnTitle"><it:message key="itracker.web.attr.weekdays"/>:</td>
            <td><html:text property="weekdays" styleClass="editColumnText"/></td>
          </tr>
          <tr><td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td></tr>
          <% if(isUpdate) { %>
            <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt" titleKey="itracker.web.button.update.alt"><it:message key="itracker.web.button.update"/></html:submit></td></tr>
          <% } else { %>
            <tr><td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt" titleKey="itracker.web.button.create.alt"><it:message key="itracker.web.button.create"/></html:submit></td></tr>
          <%-- } --%>
        </table>
      </html:form>
      <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%-- // } // 
--%>
