<%@ include file="/common/taglibs.jsp" %>

<%@ page import="org.itracker.model.PermissionType" %>
<%@ page import="org.itracker.model.Project" %>
<%@ page import="org.itracker.model.Report" %>
<%@ page import="org.itracker.model.util.ReportUtilities" %>
<%@ page import="org.itracker.model.util.UserUtilities" %>
<%@ page import="org.itracker.services.IssueService" %>
<%@ page import="org.itracker.services.ProjectService" %>
<%@ page import="org.itracker.services.ReportService" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>
<%@ page import="java.util.*" %>

<bean:define id="pageTitleKey" value="itracker.web.listreports.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="displayReportForm"/>

<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>

<html:form action="/displayreport">
   <html:hidden property="type" value="project"/>
   <div class="container-fluid maincontent">
      <div class="row">
         <div class="col-xs-12">
            <h4><it:message key="itracker.web.attr.projects"/>:</h4>
         </div>
      </div>
      <div class="row">

         <div class="col-xs-12">
            <div class="table-responsive">
               <table class="table table-striped">
                  <colgroup>
                     <col class="col-xs-2">
                     <col class="col-xs-2">
                     <col class="col-xs-4">
                     <col class="col-xs-2">
                     <col class="col-xs-2">
                  </colgroup>
                  <thead>
                  <tr>
                     <th><input type="checkbox" name="checkAll" value="false"
                                onchange="toggleChecked(this, 'projectIds')"
                                title="toggle select all"/></th>
                     <th><it:message key="itracker.web.attr.name"/></th>
                     <th><it:message key="itracker.web.attr.description"/></th>
                     <th class="text-right"><it:message key="itracker.web.attr.totalissues"/></th>
                     <th class="text-right text-nowrap"><it:message key="itracker.web.attr.lastissueupdate"/></th>
                  </tr>
                  </thead>
                  <tbody>
                  <%
                     final Map<Integer, Set<PermissionType>> permissions =
                             RequestHelper.getUserPermissions(session);

                     IssueService is = (IssueService) request.getAttribute("ih");
                     ProjectService ps = (ProjectService) request.getAttribute("ph");
                     ReportService rs = (ReportService) request.getAttribute("rh");

                     List<Project> projectsList = ps.getAllAvailableProjects();
                     Collections.sort(projectsList);
                     Iterator<Project> projectsIt = projectsList.iterator();
                     Project currentProject;
                     boolean hasProjects = false;
                     boolean shade = true;
                     while (projectsIt.hasNext()) {
                        currentProject = projectsIt.next();
                        if (!UserUtilities.hasPermission(permissions, currentProject.getId(), new PermissionType[]{PermissionType.ISSUE_VIEW_ALL, PermissionType.ISSUE_VIEW_USERS})) {
                           continue;
                        }
                        // update the alternating of row-background
                        shade = !shade;
                        hasProjects = true;

                        int totalIssueCount = 0;
                        Date newestIssueDate = null;

                        totalIssueCount = is.getTotalIssueCountByProjectId(currentProject.getId());
                        newestIssueDate = (totalIssueCount == 0 ? null : is.getLatestIssueDateByProjectId(currentProject.getId()));
                  %>
                  <tr >
                     <td><html:multibox property="projectIds" value="<%= currentProject.getId().toString() %>"/></td>
                     <td><%= currentProject.getName() %>
                     </td>
                     <td><%= currentProject.getDescription() %>
                     </td>
                     <td class="text-right"><%= totalIssueCount %>
                     </td>
                     <td class="text-right text-nowrap">
                        <it:formatDate date="<%= newestIssueDate %>"
                                       emptyKey="itracker.web.generic.notapplicable"/></td>
                  </tr>
                  <%
                     }
                  %>
                  </tbody>
               </table>
            </div>
         </div>
      </div>
      <%
         if (!hasProjects) {
      %>
      <div class="alert alert-danger">
         <it:message key="itracker.web.error.noprojects"/>
      </div>
      <% } else { %>

      <div class="row">
         <div class="col-xs-12">
            <div class="form-group form-inline">
               <html:select property="reportId" styleClass="form-control">
                  <%
                     List<Report> reports = new ArrayList<Report>();
                     try {
                        reports = rs.getAllReports();
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                     Iterator<Report> reportsIt = reports.iterator();
                     Report currentReport;
                     while (reportsIt.hasNext()) {
                        currentReport = reportsIt.next();
                        if (currentReport.getNameKey() != null) {
                  %>
                  <html:option value="<%= currentReport.getId().toString() %>"
                               key="<%= currentReport.getNameKey() %>"/>
                  <% } else { %>
                  <html:option value="<%= currentReport.getId().toString() %>"><%= currentReport.getName() %>
                  </html:option>
                  <%
                        }
                     }
                  %>
                  <html:option value="<%= Integer.toString(ReportUtilities.REPORT_EXPORT_XML) %>"
                               key="itracker.report.exportxml"/>
               </html:select>
               <html:select property="reportOutput" styleClass="form-control">
                  <html:option value="<%= ReportUtilities.REPORT_OUTPUT_PDF %>">PDF</html:option>
                  <%-- TODO HTMLreports will not show images, should be embedded in downloaded file or zipped
                  <html:option value="<%= ReportUtilities.REPORT_OUTPUT_HTML %>">HTML</html:option>--%>
                  <html:option value="<%= ReportUtilities.REPORT_OUTPUT_XLS %>">Excel</html:option>
                  <html:option value="<%= ReportUtilities.REPORT_OUTPUT_CSV %>">CSV</html:option>
               </html:select>
               <html:submit styleClass="btn btn-primary" altKey="itracker.web.button.run.alt"
                            titleKey="itracker.web.button.run.alt"><it:message
                       key="itracker.web.button.run"/></html:submit>
            </div>
         </div>

         <% } %>
      </div>
   </div>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
</html>
