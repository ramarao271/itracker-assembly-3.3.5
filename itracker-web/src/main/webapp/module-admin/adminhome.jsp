<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE HTML>
<bean:define id="pageTitleKey" value="itracker.web.admin.index.title"/>
<bean:define id="pageTitleArg" value=""/>

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

<div class="container-fluid">

   <div class="row">
      <div class="col-sm-6 col-lg-4">
         <%--
            PROJECTS
          --%>
         <section id="projects" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link action="editprojectform" targetAction="create" styleClass="btn btn-link">
                     [<it:message
                          key="itracker.web.attr.create"/>]
                  </it:link>
                  <it:link forward="listprojectsadmin" styleClass="btn btn-link">
                     [<it:message
                          key="itracker.web.attr.administer"/>]
                  </it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.projectadmin"/></h3>
            </div>
            <div class="panel-body">
               <div class="row">
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalprojects"/>:
                        <fmt:formatNumber value="${sizeps}"/>
                     </h5 class="text-nowrap">
                  </div>
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalissues"/>:
                        <fmt:formatNumber value="${numberIssues}"/>
                     </h5 class="text-nowrap">
                  </div>

               </div>
            </div>
         </section>
      </div>
      <div class="col-sm-6 col-lg-4">
         <%--
            USERS
          --%>
         <section id="users" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link forward="listusers" styleClass="btn btn-link">
                     [<it:message key="itracker.web.attr.administer"/>]</it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.useradmin"/></h3>
            </div>
            <div class="panel-body">
               <div class="row">
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalactive"/>:
                        <fmt:formatNumber value="${numberofActiveSesssions}"/>
                     </h5 class="text-nowrap">
                  </div>
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalusers"/>:
                        <fmt:formatNumber value="${numberUsers}"/>
                     </h5 class="text-nowrap">
                  </div>

               </div>
            </div>
         </section>
      </div>
      <div class="clearfix hidden-md hidden-lg"></div>
      <div class="col-sm-6 col-lg-4">
         <%--
            REPORTS
          --%>
         <section id="reports" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link forward="listreportsadmin" styleClass="btn btn-link">
                     [<it:message key="itracker.web.attr.administer"/>]</it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.reportadmin"/></h3>
            </div>
            <div class="panel-body">
               <div class="row">
                  <div class="col-xs-12">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalnumber"/>:
                        <fmt:formatNumber value="${numberReports}"/>
                     </h5 class="text-nowrap">
                  </div>
               </div>
            </div>
         </section>
      </div>
      <div class="clearfix hidden-sm hidden-md"></div>
      <div class="col-sm-6 col-lg-4">
         <%--
            CONFIG
          --%>
         <section id="config" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link forward="listconfiguration" styleClass="btn btn-link">[<it:message
                          key="itracker.web.attr.administer"/>]</it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.configadmin"/></h3>
            </div>
            <div class="panel-body">
               <div class="row">
                  <div class="col-xs-6">
                     <h5 class="text-nowrap"><it:message key="itracker.web.attr.statuses"/>:
                        <fmt:formatNumber value="${numberOfStatuses}"/>
                     </h5 class="text-nowrap">
                  </div>
                  <div class="col-xs-6">
                     <h5 class="text-nowrap"><it:message key="itracker.web.attr.severities"/>:
                        <fmt:formatNumber value="${numberOfSeverities}"/>
                     </h5 class="text-nowrap">
                  </div>
               </div>
               <div class="row">
                  <div class="col-xs-6">
                     <h5 class="text-nowrap"><it:message key="itracker.web.attr.resolutions"/>:
                        <fmt:formatNumber value="${numberOfResolutions}"/>
                     </h5 class="text-nowrap">
                  </div>
                  <div class="col-xs-6">
                     <h5 class="text-nowrap"><it:message key="itracker.web.attr.customfields"/>:
                        <fmt:formatNumber value="${numberOfCustomProjectFields}"/>
                     </h5 class="text-nowrap">
                  </div>
               </div>
            </div>
         </section>
      </div>
      <div class="col-sm-6 col-lg-4">
         <%--
              WORKFLOW
         --%>
         <section id="workflow" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link forward="listworkflow" styleClass="btn btn-link">
                     [<it:message key="itracker.web.attr.administer"/>]</it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.workflowadmin"/></h3>
            </div>
            <div class="panel-body">
               <div class="row">
                  <div class="col-xs-12">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.workflowscripts"/>:
                        <fmt:formatNumber value="${numberOfWorkflowScripts}"/>
                     </h5 class="text-nowrap">
                  </div>

               </div>
            </div>
         </section>
      </div>
      <div class="col-sm-6 col-lg-4">

         <%--
            LANGUAGE
          --%>

         <section id="languages" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <!--
            <it:link action="initializelanguages"
            titleKey="itracker.web.admin.index.language.reinitialize.alt"
            styleClass="btn btn-link">[<it:message key="itracker.web.attr.reinitialize"/>]
            </it:link>-->
                  <it:link forward="listlanguages" styleClass="btn btn-link">[<it:message
                          key="itracker.web.attr.administer"/>]</it:link>

               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.languageadmin"/></h3>
            </div>
            <div class="panel-body">
               <div class="row">
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totallanguages"/>:
                        <fmt:formatNumber value="${numberAvailableLanguages}"/>
                     </h5 class="text-nowrap">
                  </div>
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalkeys"/>:
                        <fmt:formatNumber value="${numberDefinedKeys}"/>
                     </h5 class="text-nowrap">
                  </div>

               </div>
            </div>
         </section>
      </div>
      <div class="clearfix hidden-md"></div>
      <div class="col-sm-6 col-lg-4 col-lg-offset-4 ">
         <%--
            ATTACHMENTS
          --%>
         <section id="attachments" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link forward="listattachments" styleClass="btn btn-link">[<it:message
                          key="itracker.web.attr.administer"/>]</it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.admin.index.attachmentadmin"/></h3>
            </div>
            <div class="panel-body">


               <div class="row">
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalnumber"/>:
                        <fmt:formatNumber value="${allIssueAttachmentsTotalNumber}"/>
                     </h5 class="text-nowrap">
                  </div>
                  <div class="col-xs-6">
                     <h5 class="text-nowrap">
                        <it:message key="itracker.web.attr.totalsize"/>:
                        <fmt:formatNumber value="${allIssueAttachmentsTotalSize}" pattern="0.##"/><it:message
                             key="itracker.web.generic.kilobyte"/>
                     </h5 class="text-nowrap">
                  </div>

               </div>
            </div>
         </section>
      </div>

   </div>
</div>
<table border="0" cellspacing="0" cellspacing="1" width="800px">


   <tr>
      <td><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="15" width="1"/></td>
   </tr>

   <%--
      SCHEDULER
    --%>
   <%--  <tr>
      <td colspan="3" class="editColumnTitle"><it:message key="itracker.web.admin.index.scheduleradmin"/></td>
      <td colspan="2" align="right"><it:link forward="listtasks" styleClass="btn btn-link">[<it:message key="itracker.web.attr.administer"/>]</it:link></td>
    </tr>
    <tr class="listHeading"><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td></tr>
    <tr><td colspan="5"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td></tr>
    <tr class="listRowUnshaded">
      <td class="editColumnTitle"><it:message key="itracker.web.attr.totaltasks"/>: </td>
      <td class="btn btn-link"><%= -1/*Scheduler.getNumTasks()*/ %></td>
      <td></td>
      <td class="editColumnTitle"><it:message key="itracker.web.attr.lastrun"/>: </td>
      <td class="btn btn-link"><c:out value="${lastRun}"/></td>
    </tr>--%>
</table>
<br>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body>
</html>

