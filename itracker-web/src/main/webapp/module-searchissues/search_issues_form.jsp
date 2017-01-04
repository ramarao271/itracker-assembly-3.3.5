<%@ include file="/common/taglibs.jsp" %>

<%@ page import="org.itracker.core.resources.ITrackerResources" %>
<%@ page import="org.itracker.model.*" %>
<%@ page import="org.itracker.model.util.IssueUtilities" %>
<%@ page import="org.itracker.model.util.ReportUtilities" %>
<%@ page import="org.itracker.model.util.UserUtilities" %>
<%@ page import="org.itracker.services.ReportService" %>
<%@ page import="org.itracker.services.UserService" %>
<%@ page import="org.itracker.web.util.Constants" %>
<%@ page import="org.itracker.web.util.LoginUtilities" %>
<%@ page import="org.itracker.web.util.RequestHelper" %>
<%@ page import="java.util.*" %>

<% // TODO : move redirect logic to the Action class.
   final Map<Integer, Set<PermissionType>> permissions =
           RequestHelper.getUserPermissions(session);
   User um = RequestHelper.getCurrentUser(session);

   Locale locale = LoginUtilities.getCurrentLocale(request);
   ReportService rh = (ReportService) request.getAttribute("rh");
   UserService uh = (UserService) request.getAttribute("uh");

   Integer currUserId = um.getId();

   IssueSearchQuery query = (IssueSearchQuery) session.getAttribute(Constants.SEARCH_QUERY_KEY);
%>
<%
   if (query == null) {
%>
<logic:forward name="unauthorized"/>
<%
} else {
   List<Report> reports = new ArrayList<Report>();
   if (null != rh) {
      reports = rh.getAllReports();
   }


   Project project = null;
   List<User> possibleContributors = new ArrayList<User>();
   if (query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) {
      project = query.getProject();
      if (project == null) {
%>
<logic:forward name="unauthorized"/>
<%
      }

      possibleContributors = uh.getUsersWithAnyProjectPermission(query.getProjectId(),
              new int[]{UserUtilities.PERMISSION_CREATE, UserUtilities.PERMISSION_EDIT, UserUtilities.PERMISSION_EDIT_USERS});
      Collections.sort(possibleContributors, User.NAME_COMPARATOR);
   }
%>

<div class="container-fluid maincontent">


   <html:form action="/searchissues">
      <% if (query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
      <input type="hidden" name="type" value="<%= IssueSearchQuery.TYPE_PROJECT %>">
      <input type="hidden" name="projects" value="<%= query.getProjectId() %>">
      <% } else { %>
      <input type="hidden" name="type" value="<%= IssueSearchQuery.TYPE_FULL %>">
      <% } %>
      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <% if (query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
               <label><it:message key="itracker.web.attr.project"/>:</label>
               <p class="form-control-static"><%= query.getProjectName() %>
               </p>
               <% } else { %>
               <label><it:message key="itracker.web.attr.projects"/>:</label>
               <html:select property="projects" styleClass="form-control" size="5" multiple="true">
                  <%
                     for (int i = 0; i < query.getAvailableProjects().size(); i++) {
                  %>
                  <html:option value="<%= query.getAvailableProjects().get(i).getId().toString() %>"
                               styleClass="editColumnText"><%= query.getAvailableProjects().get(i).getName() %>
                  </html:option>
                  <% } %>
               </html:select>
               <% } %>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.sortorder"/>:</label>
               <html:select property="orderBy" styleClass="form-control">
                  <html:option value="id" key="itracker.web.attr.id"/>
                  <% if (!query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
                  <html:option value="proj" key="itracker.web.attr.project"/>
                  <% } %>
                  <html:option value="stat" key="itracker.web.attr.status"/>
                  <html:option value="sev" key="itracker.web.attr.severity"/>
                  <html:option value="owner" key="itracker.web.attr.owner"/>
                  <html:option value="lm" key="itracker.web.attr.lastmodified"/>
               </html:select>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.status"/>:</label>
               <% List<Configuration> statuses = IssueUtilities.getStatuses(); %>
               <html:select property="statuses" styleClass="form-control" size="5" multiple="true">
                  <% for (int i = 0; i < statuses.size(); i++) { %>
                  <html:option
                          value="<%= statuses.get(i).getValue() %>"><%= IssueUtilities.getStatusName(statuses.get(i).getValue(), locale) %>
                  </html:option>
                  <% } %>
               </html:select>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.severity"/>:</label>
               <% List<NameValuePair> severities = IssueUtilities.getSeverities(locale); %>
               <html:select property="severities" styleClass="form-control" size="5" multiple="true">
                  <%
                     for (int i = 0; i < severities.size(); i++) {
                  %>
                  <html:option value="<%= severities.get(i).getValue() %>"><%= severities.get(i).getName() %>
                  </html:option>
                  <% } %>
               </html:select>
            </div>
         </div>
      </div>


      <% if (query.getType().equals(IssueSearchQuery.TYPE_PROJECT)) { %>
      <% List<Component> components = project.getComponents(); %>
      <% List<Version> versions = project.getVersions(); %>
      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.creator"/>:</label>
               <html:select property="creator" styleClass="form-control">
                  <html:option value="-1" key="itracker.web.generic.any"/>
                  <% for (int j = 0; j < possibleContributors.size(); j++) { %>
                  <html:option
                          value="<%= possibleContributors.get(j).getId().toString() %>"><%= possibleContributors.get(j).getFirstName() + " " + possibleContributors.get(j).getLastName() %>
                  </html:option>
                  <% } %>
               </html:select>
            </div>
         </div>

         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.owner"/></label>
               <html:select property="owner" styleClass="form-control">
                  <html:option value="-1" key="itracker.web.generic.any"/>
                  <% for (int j = 0; j < possibleContributors.size(); j++) { %>
                  <html:option
                          value="<%= possibleContributors.get(j).getId().toString() %>"><%= possibleContributors.get(j).getFirstName() + " " + possibleContributors.get(j).getLastName() %>
                  </html:option>
                  <% } %>
               </html:select>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-6 col-sm-push-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.target"/></label>
               <% if (null != versions && versions.size() > 0) { %>
               <html:select property="targetVersion" styleClass="form-control">
                  <html:option value="-1" key="itracker.web.generic.any"/>
                  <% for (int i = 0; i < versions.size(); i++) { %>
                  <html:option value="<%= versions.get(i).getId().toString() %>"
                               styleClass="editColumnText"><%= versions.get(i).getNumber() %>
                  </html:option>
                  <% } %>
               </html:select>
               <% } else { %>
               <p class="form-control-static"><it:message key="itracker.web.generic.unavailable"/></p>
               <% } %>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.components"/>:</label>
               <% if (null != components && components.size() > 0) { %>
               <html:select property="components" styleClass="form-control" size="3" multiple="true">
                  <% for (int i = 0; i < components.size(); i++) { %>
                  <html:option value="<%= components.get(i).getId().toString() %>"
                               styleClass="editColumnText"><%= components.get(i).getName() %>
                  </html:option>
                  <% } %>
               </html:select>
               <% } else { %>
               <p class="form-control-static"><it:message key="itracker.web.generic.unavailable"/></p>
               <% } %>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.versions"/>:</label>
               <% if (null != versions && versions.size() > 0) { %>
               <html:select property="versions" styleClass="form-control" size="3" multiple="true">
                  <% for (int i = 0; i < versions.size(); i++) { %>
                  <html:option value="<%= versions.get(i).getId().toString() %>"
                               styleClass="editColumnText"><%= versions.get(i).getNumber() %>
                  </html:option>
                  <% } %>
               </html:select>
               <% } else { %>
               <p class="form-control-static"><it:message key="itracker.web.generic.unavailable"/></p>
               <% } %>
            </div>
         </div>

      </div>
      <% } %>

      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.resolution"/>:</label>
               <html:select property="resolution" styleClass="form-control">
                  <option value=""></option>
                  <%
                     List<NameValuePair> possResolutions = IssueUtilities.getResolutions(locale);
                     for (int i = 0; i < possResolutions.size(); i++) {
                  %>
                  <html:option
                          value="<%= possResolutions.get(i).getValue() %>"><%= possResolutions.get(i).getName() %>
                  </html:option>
                  <% } %>
               </html:select>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.phrase"/>:</label>
               <html:text property="textphrase" size="30" styleClass="form-control"/>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-xs-12">
            <html:submit styleClass="btn btn-block btn-primary" altKey="itracker.web.button.search.alt"
                         titleKey="itracker.web.button.search.alt"><it:message
                    key="itracker.web.button.search"/></html:submit>

         </div>
      </div>

   </html:form>


   <%
      List<Issue> issues = query.getResults();
      if (issues != null) {
   %>


   <div class="row">
      <div class="col-xs-12">


         <h4>
            <div class="pull-right label label-info text-right">
               <it:message key="itracker.web.generic.totalissues"
                           arg0="<%= Integer.toString(issues.size()) %>"/>
            </div>
            <it:message key="itracker.web.attr.issues"/></h4>
         <div class="table-responsive">
            <table class="table table-striped table-hover">
               <colgroup>
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-1">
                  <col class="col-xs-1">
                  <col class="col-xs-1">
                  <col class="col-xs-1">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
               </colgroup>
               <thead>
               <tr>
                  <th class="text-right"><it:message key="itracker.web.attr.id"/></th>
                  <th><it:message key="itracker.web.attr.project"/></th>
                  <th><it:message key="itracker.web.attr.status"/></th>
                  <th><it:message key="itracker.web.attr.severity"/></th>
                  <th><it:message key="itracker.web.attr.components"/></th>
                  <th><it:message key="itracker.web.attr.description"/></th>
                  <th><it:message key="itracker.web.attr.owner"/></th>
                  <th class="text-right text-nowrap">
                     <it:message key="itracker.web.attr.lastmodified"/></th>
               </tr>

               </thead>

               <tbody>

               <% for (int i = 0; i < issues.size(); i++) { %>
               <tr>

                  <td class="text-nowrap text-right">
                     <div class="pull-left">
                     <it:formatIconAction forward="viewissue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="<%= issues.get(i).getId() %>"
                                          icon="tasks" iconClass="fa-lg"
                                          info="itracker.web.image.view.issue.alt"
                                          arg0="<%= issues.get(i).getId() %>"
                                          caller="searchissue"
                                          textActionKey="itracker.web.image.view.texttag"/>

                     <% if (UserUtilities.hasPermission(permissions, issues.get(i).getProject().getId(), UserUtilities.PERMISSION_EDIT)) { %>
                     <it:formatIconAction forward="editissue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="<%= issues.get(i).getId() %>"
                                          caller="searchissue"
                                          icon="edit" iconClass="fa-lg"
                                          info="itracker.web.image.edit.issue.alt"
                                          arg0="<%= issues.get(i).getId() %>"
                                          textActionKey="itracker.web.image.edit.texttag"/>
                     <% } %>
                     <% if (!IssueUtilities.hasIssueNotification(issues.get(i), currUserId)) { %>
                    <span class="HTTP_POST">
                        <it:formatIconAction forward="watchissue" paramName="id"
                                             paramValue="<%= issues.get(i).getId() %>" caller="searchissue" icon="bell"
                                             iconClass="fa-lg" info="itracker.web.image.watch.issue.alt"
                                             arg0="<%= issues.get(i).getId() %>"
                                             textActionKey="itracker.web.image.watch.texttag"/>
                    </span>
                     <% } %>
                     </div>
                  <%= issues.get(i).getId() %>
                  </td>
                  <td class="text-nowrap"><%= issues.get(i).getProject().getName() %>
                  </td>
                  <td class="text-nowrap"><%= IssueUtilities.getStatusName(issues.get(i).getStatus(), locale) %>
                  </td>
                  <td class="text-nowrap"><%= IssueUtilities.getSeverityName(issues.get(i).getSeverity(), locale) %>
                  </td>
                  <td class="text-nowrap"><%= (issues.get(i).getComponents().size() == 0 ? ITrackerResources.getString("itracker.web.generic.unknown", locale) : issues.get(i).getComponents().get(0).getName() + (issues.get(i).getComponents().size() > 1 ? " (+)" : "")) %>
                  </td>
                  <td class="text-nowrap"><it:formatDescription><%= issues.get(i).getDescription() %>
                  </it:formatDescription></td>
                  <td class="text-nowrap"><it:formatIssueOwner issue="<%= issues.get(i) %>" format="short"/></td>
                  <td class="text-right text-nowrap"><it:formatDate date="<%= issues.get(i).getLastModifiedDate() %>"/></td>
               </tr>
               <%
                  }
                  if (issues.size() == 0) {
               %>
               <tr class="listRowUnshaded" align="left">
                  <td colspan="10" align="left"><it:message key="itracker.web.error.noissues"/>
                  </td>
               </tr>

               <% } else { %>
               </tbody>
               <tfoot>
               <tr>
                  <td colspan="8">
                     <html:form action="/displayreport" styleClass="form-inline">
                        <html:select property="reportId" styleClass="form-control">
                           <%
                              for (Report report : reports) {
                                 if (report.getNameKey() != null) {
                           %>
                           <html:option value="<%= report.getId().toString() %>" key="<%= report.getNameKey() %>"/>
                           <% } else { %>
                           <html:option value="<%= report.getId().toString() %>"><%= report.getName() %>
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
                                     titleKey="itracker.web.button.run.alt"><it:message key="itracker.web.button.run"/></html:submit>

                     </html:form>
                  </td>
               </tr>
               </tfoot>
               <% } %>
            </table>

         </div>
      </div>
   </div>
   <%
         }
      }
   %>

</div>