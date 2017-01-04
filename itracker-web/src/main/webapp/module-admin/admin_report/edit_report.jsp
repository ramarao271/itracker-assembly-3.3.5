<%@ include file="/common/taglibs.jsp" %>

<%@ page import="org.itracker.model.Report" %>
<%@ page import="org.itracker.web.util.Constants" %>

<% // TODO : move redirect logic to the Action class.
   Report report = (Report) session.getAttribute(Constants.REPORT_KEY);

   boolean isUpdate = false;
   if (report == null) {
%>
<logic:forward name="unauthorized"/>
<%
} else {
   if (report.getId().intValue() > 0) {
      isUpdate = true;
   }
%>

<!DOCTYPE HTML>
<c:choose>
   <c:when test="${ reportForm.action == 'update' }">
      <c:set var="pageTitleKey" scope="request">itracker.web.admin.editreport.title.update</c:set>
   </c:when>
   <c:otherwise>
      <c:set var="pageTitleKey" scope="request">itracker.web.admin.editreport.title.create</c:set>
   </c:otherwise>
</c:choose>
<c:set var="pageTitleArg" value="" scope="request"/>

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

<div class="container-fluid maincontent"
<html:form action="/editreport" enctype="multipart/form-data" acceptCharset="utf-8">
   <html:hidden property="action"/>
   <html:hidden property="id"/>

   <div class="row">
      <div class="col-sm-6 col-sm-push-6">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.created"/>:</label>
            <p class="form-control-static text-nowrap"><it:formatDate
                    date="<%= report.getCreateDate() %>"/></p>
         </div>

         <div class="form-group">
            <label><it:message key="itracker.web.attr.lastmodified"/>:</label>
            <p class="form-control-static text-nowrap"><it:formatDate
                    date="<%= report.getLastModifiedDate() %>"/></p>
         </div>
      </div>
      <div class="col-sm-6 col-sm-pull-6">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.name"/><sup>*</sup>:</label>
            <html:text property="name" styleClass="form-control"/>
         </div>
         <div class="form-group">
            <label><it:message key="itracker.web.attr.namekey"/><sup>*</sup>:</label>
            <html:text property="nameKey" size="30" styleClass="form-control"/>
         </div>
      </div>
   </div>
   <div class="row">
      <div class="col-sm-12">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.description"/>:</label>
            <html:text property="description" styleClass="form-control"/>
         </div>
      </div>
   </div>
   <div class="row">
      <div class="col-sm-12">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.reportdefinition"/>:</label>
            <html:file property="fileDataFile" styleClass="editColumnText"/>
         </div>
      </div>
   </div>
   <c:set var="fileData" value="${reportForm.fileData}"/>
   <logic:notEmpty name="fileData">
      <div class="form-group">
         <label><it:message key="itracker.web.attr.reportcontent"/>:</label>
         <pre class="form-control-static pre-scrollable"><code><c:out value="${fileData}"
                     escapeXml="true"/></code></pre>
      </div>
   </logic:notEmpty>
   <div class="row">
      <div class="col-xs-12">
         <% if (isUpdate) { %>
         <html:submit styleClass="btn btn-primary btn-block" altKey="itracker.web.button.update.alt"
                      titleKey="itracker.web.button.update.alt"><it:message
                 key="itracker.web.button.update"/></html:submit>
         <% } else { %>
         <html:submit styleClass="btn btn-primary btn-block" altKey="itracker.web.button.create.alt"
                      titleKey="itracker.web.button.create.alt"><it:message
                 key="itracker.web.button.create"/></html:submit>
         <% } %>
      </div>
   </div>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<% } %>
