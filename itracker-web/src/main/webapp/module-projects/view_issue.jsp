<%@ include file="/common/taglibs.jsp" %>

<jsp:useBean id="project" scope="request" type="org.itracker.model.Project"/>
<jsp:useBean id="issue" scope="request" type="org.itracker.model.Issue"/>
<jsp:useBean id="previousIssue" scope="request" type="org.itracker.model.Issue" class="org.itracker.model.Issue"/>
<jsp:useBean id="nextIssue" scope="request" type="org.itracker.model.Issue" class="org.itracker.model.Issue"/>
<jsp:useBean id="canEditIssue" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="canCreateIssue" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="hasHardIssueNotification" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="hasIssueNotification" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="issueStatusName" scope="request" type="java.lang.String" />
<jsp:useBean id="issueSeverityName" scope="request" type="java.lang.String" />
<jsp:useBean id="issueOwnerName" scope="request" type="java.lang.String" />
<jsp:useBean id="projectFieldsMap" scope="request" type="java.util.Map" class="java.util.HashMap" />
<jsp:useBean id="hasAttachmentOption" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="attachments" scope="request" type="java.util.Collection" />
<jsp:useBean id="histories" scope="request" type="java.util.Collection" />
<jsp:useBean id="notifiedUsers" scope="request" type="java.util.Collection" />
<jsp:useBean id="notificationMap" scope="request" type="java.util.Map" />

<bean:define id="pageTitleKey" value="itracker.web.viewissue.title"/>
<bean:define id="pageTitleArg" value="${issue.id}"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="container-fluid maincontent">


   <div class="row">
      <div class="col-xs-4 col-xs-push-8">
         <div class="form-group"><label class="sr-only"><it:message key="itracker.web.attr.actions"/>:</label>
            <div id="actions" class="actions form-control-static text-right">
               <div class="issue-nav pull-right">
                  <c:if test="${previousIssue.id > 0}">
                     <it:formatIconAction action="view_issue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${previousIssue.id}"
                                          caller="viewissue" styleClass="previous"
                                          icon="chevron-circle-left" iconClass="fa-lg"
                                          info="itracker.web.image.previous.issue.alt"
                                          arg0="${previousIssue.id}"
                                          textActionKey="itracker.web.image.previous.texttag"/>
                  </c:if>
                  <c:choose>
                     <c:when test="${nextIssue.id > 0}">
                        <it:formatIconAction action="view_issue"
                                             module="/module-projects"
                                             paramName="id"
                                             paramValue="${nextIssue.id}"
                                             caller="viewissue" styleClass="next"
                                             icon="chevron-circle-right" iconClass="fa-lg"
                                             info="itracker.web.image.next.issue.alt"
                                             arg0="${nextIssue.id}"
                                             textActionKey="itracker.web.image.next.texttag"/>
                     </c:when>
                  </c:choose>
               </div>

               <c:if test="${!hasHardIssueNotification}">
                  <c:if test="${hasIssueNotification}">
                      <span class="HTTP_POST">
                          <it:formatIconAction forward="watchissue"
                                               module="/module-projects"
                                               paramName="id"
                                               paramValue="${issue.id}"
                                               caller="viewissue"
                                               icon="bell-slash"
                                               styleClass="watch" iconClass="fa-lg"
                                               info="itracker.web.image.watch.issue.alt"
                                               arg0="${issue.id}"
                                               textActionKey="itracker.web.image.watch.texttag"/>
                      </span>
                  </c:if>
                  <c:if test="${!hasIssueNotification}">
                      <span class="HTTP_POST">
                          <it:formatIconAction forward="watchissue"
                                               module="/module-projects"
                                               paramName="id"
                                               paramValue="${issue.id}"
                                               caller="viewissue"
                                               icon="bell"
                                               styleClass="watch" iconClass="fa-lg"
                                               info="itracker.web.image.watch.issue.alt"
                                               arg0="${issue.id}"
                                               textActionKey="itracker.web.image.watch.texttag"/>
                      </span>
                  </c:if>
               </c:if>
               <c:if test="${hasHardIssueNotification}">
                  <c:set var="watched"><it:message key="itracker.web.attr.notifications"/></c:set>
                  <i class="fa fa-bell-o fa-lg" title="${watched}"></i>
               </c:if>
               <c:if test="${canEditIssue}">

                  <it:formatIconAction action="editissueform"
                                       module="/module-projects"
                                       paramName="id"
                                       paramValue="${issue.id}"
                                       caller="viewissue"
                                       icon="edit"
                                       styleClass="edit" iconClass="fa-lg"
                                       info="itracker.web.image.edit.issue.alt"
                                       arg0="${issue.id}"
                                       textActionKey="itracker.web.image.edit.texttag"/>

                  <it:formatIconAction action="moveissueform"
                                       module="/module-projects"
                                       paramName="id"
                                       paramValue="${issue.id}"
                                       caller="viewissue"
                                       icon="share-square-o"
                                       styleClass="moveIssue" iconClass="fa-lg"
                                       info="itracker.web.image.move.issue.alt"
                                       arg0="${issue.id}"
                                       textActionKey="itracker.web.image.move.texttag"/>

                  <%-- TODO reinstate this when relate issues works correctly
                  <it:formatImageAction forward="relateissue" paramName="id" paramValue="${issue.id}" caller="viewissue" src="/images/link.gif" altKey="itracker.web.image.link.issue.alt" textActionKey="itracker.web.image.link.texttag"/>
                  --%>
               </c:if>
               <c:if test="${canCreateIssue}">
                  <it:formatIconAction forward="createissue"
                                       module="/module-projects"
                                       paramName="projectId"
                                       paramValue="${project.id}"
                                       icon="plus-square-o"
                                       styleClass="create" iconClass="fa-lg"
                                       info="itracker.web.image.create.issue.alt"
                                       arg0="${project.name}"
                                       textActionKey="itracker.web.image.create.texttag"/>
               </c:if>


            </div>
         </div>
      </div>
   </div>
   <div class="row">
      <div class="col-sm-6">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.description"/>:</label>
            <p class="form-control-static" id="description">${issue.description}
            </p>
         </div>
            <div class="form-group">
               <label><it:message key="itracker.web.attr.project"/>:</label>
               <p class="form-control-static">
                  <it:formatIconAction forward="listissues"
                                       module="/module-projects"
                                       paramName="projectId"
                                       paramValue="${project.id}"
                                       caller="viewissue"
                                       icon="tasks"
                                       styleClass="issuelist" iconClass="fa-lg"
                                       info="itracker.web.image.issuelist.issue.alt"
                                       textActionKey="itracker.web.image.issuelist.texttag"/>
                  ${issue.project.name}
               </p>
      </div>
   </div>
      <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.creator"/>:</label>
               <p class="form-control-static" id="creator"><it:formatDate date="${issue.createDate}"/>
                  (${issue.creator.firstName}&nbsp;${issue.creator.lastName})</p>
            </div>
            <div class="form-group">
               <label><it:message key="itracker.web.attr.lastmodified"/>:</label>
               <p class="form-control-static" id="lastmodified"><it:formatDate date="${issue.lastModifiedDate}"/></p>
            </div>
         </div>
      </div>


   <div class="row">
      <div class="col-sm-6">
         <div class="form-group"><label><it:message key="itracker.web.attr.status"/>:</label>
            <p class="form-control-static" id="status">${issueStatusName}</p>
         </div>
      </div>
      <div class="col-sm-6">
         <div class="form-group"><label><it:message key="itracker.web.attr.resolution"/>:</label>
            <p class="form-control-static" id="resolution"><it:formatResolution
                    projectOptions="${project.options}">${issue.resolution}</it:formatResolution></p>
         </div>
      </div>
   </div>

   <div class="row">
      <div class="col-sm-6">
         <div class="form-group"><label><it:message key="itracker.web.attr.severity"/>:</label>
            <p class="form-control-static" id="severity">${issueSeverityName}</p></div>
      </div>
      <div class="col-sm-6">
         <div class="form-group"><label><it:message key="itracker.web.attr.owner"/>:</label>
            <p class="form-control-static" id="owner">${issueOwnerName}</p></div>
      </div>
   </div>
   <div class="row">
      <div class="col-sm-6">

      </div>
      <div class="col-sm-6">
         <c:if test="${not empty project.versions}">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.target"/>:</label>
               <p class="form-control-static"
                  id="target">${issue.targetVersion == null ? '' : issue.targetVersion.number}</p>
            </div>
         </c:if>
      </div>
   </div>


   <div class="row">
      <div class="col-sm-6">

         <c:choose>
            <c:when test="${not empty project.components}">
               <div class="form-group" id="components">
                  <label><it:message key="itracker.web.attr.components"/>:</label>
                  <c:forEach var="component" items="${issue.components}">
                     <p class="form-control-static">
                           ${component.name}
                     </p>
                  </c:forEach>
               </div>
            </c:when>
         </c:choose>
      </div>
      <div class="col-sm-6">

         <c:choose>
            <c:when test="${not empty project.versions}">
               <div class="form-group" id="versions">
                  <label><it:message key="itracker.web.attr.versions"/>:</label>
                  <c:forEach var="version" items="${issue.versions}">
                     <p class="form-control-static">
                           ${version.number}
                     </p>
                  </c:forEach>
               </div>
            </c:when>
         </c:choose>
      </div>
   </div>

   <c:if test="${ not empty projectFieldsMap }">

      <div class="row">
         <div class="col-sm-6">
            <h5><it:message
                    key="itracker.web.attr.customfields"/></h5>
         </div>
      </div>
      <div class="row" id="customfields">
         <c:forEach var="projectField" varStatus="i" items="${ projectFieldsMap }">
            <div class="col-md-6" id="customfield-${projectField.key}">
               <it:formatCustomField field="${projectField.key}" currentValue="${projectField.value}"
                                     displayType="view"/>
            </div>
         </c:forEach>

      </div>
   </c:if>


   <c:if test="${hasAttachmentOption && not empty attachments}">
      <div class="row">
         <div class="col-sm-6">
            <h5><it:message key="itracker.web.attr.attachments"/></h5>
         </div>
      </div>
      <c:choose>
         <c:when test="${not empty issue.attachments}">
            <div class="row" id="attachments">
               <c:forEach items="${issue.attachments}" var="attachment" varStatus="status">
                  <div class="col-sm-6 ">
                     <it:formatIconAction action="downloadAttachment" module="/module-projects"
                                          paramName="id" paramValue="${attachment.id}"
                                          icon="download" styleClass="download" iconClass="fa-lg"
                                          info="itracker.web.image.download.attachment.alt"
                                          textActionKey="itracker.web.image.download.texttag"/>

                     <it:link action="downloadAttachment"
                              paramName="id"
                              paramValue="${attachment.id}">${attachment.originalFileName}
                        (${attachment.type}, <fmt:formatNumber pattern="0.##" value="${attachment.size / 1024}"
                                                               type="number"/> <it:message
                                key="itracker.web.generic.kilobyte"/>)
                     </it:link>
                     <em><it:formatDate date="${attachment.lastModifiedDate}"/>
                        (${attachment.user.firstName}&nbsp;${attachment.user.lastName})</em>

                     <div class="well well-sm">
                        <it:formatDescription>${attachment.description}</it:formatDescription></div>
                  </div>
               </c:forEach>
            </div>
         </c:when>
      </c:choose>
   </c:if>

   <div class="row">
      <div class="col-sm-12">
         <h5>

            <div class="pull-right text-right">
               <div class="issue-nav pull-right">
                  <c:if test="${previousIssue.id > 0}">
                     <it:formatIconAction action="view_issue"
                                          module="/module-projects"
                                          paramName="id"
                                          paramValue="${previousIssue.id}"
                                          caller="viewissue" styleClass="previous"
                                          icon="chevron-circle-left" iconClass="fa-lg"
                                          info="itracker.web.image.previous.issue.alt"
                                          arg0="${previousIssue.id}"
                                          textActionKey="itracker.web.image.previous.texttag"/>
                  </c:if>
                  <c:choose>
                     <c:when test="${nextIssue.id > 0}">
                        <it:formatIconAction action="view_issue"
                                             module="/module-projects"
                                             paramName="id"
                                             paramValue="${nextIssue.id}"
                                             caller="viewissue" styleClass="next"
                                             icon="chevron-circle-right" iconClass="fa-lg"
                                             info="itracker.web.image.next.issue.alt"
                                             arg0="${nextIssue.id}"
                                             textActionKey="itracker.web.image.next.texttag"/>
                     </c:when>
                  </c:choose>
               </div>
               <it:formatIconAction forward="view_issue_activity.do"
                                    paramName="id"
                                    paramValue="${issue.id}"
                                    styleClass="history"
                                    icon="history" iconClass="fa-lg"
                                    info="itracker.web.image.view.activity.alt"
                                    textActionKey="itracker.web.image.view.texttag"/>
            </div>
            <it:message key="itracker.web.attr.history"/>
         </h5>
      </div>
   </div>

   <div class="row">
      <div class="col-sm-12">
         <div class="table-responsive">
            <table class="table">
               <colgroup>
                  <col class="col-xs-1">
                  <col class="col-xs-7">
                  <col class="col-xs-4">
               </colgroup>
               <thead>
               <tr>
                  <th></th>
                  <th><it:message key="itracker.web.attr.updator"/></th>
                  <th class="text-right"><it:message key="itracker.web.attr.updated"/></th>
               </tr>
               </thead>
               <tbody>
               <c:forEach items="${histories}" var="historyEntry" varStatus="status">

                  <tr>
                     <td align="right" valign="bottom">
                           ${status.count})
                     </td>
                     <td class="historyName">
                           ${historyEntry.user.firstName}&nbsp;${historyEntry.user.lastName}
                        (<a href="mailto:${historyEntry.user.email}"
                            class="mailto">${historyEntry.user.email}</a>)
                     </td>
                     <td class="historyDate text-nowrap text-right">
                        <it:formatDate date="${historyEntry.createDate}"/>
                     </td>
                  </tr>
                  <tr>
                     <td></td>
                     <td colspan="2">
                        <it:formatHistoryEntry
                                projectOptions="${project.options}">${historyEntry.description}</it:formatHistoryEntry>

                     </td>
                  </tr>

               </c:forEach>

               </tbody>
            </table>
         </div>
      </div>
   </div>

   <div class="row">
      <div class="col-sm-12">
         <h5><it:message key="itracker.web.attr.notifications"/></h5>
      </div>
   </div>
   <div class="row">
      <div class="col-sm-12">

         <div class="table-responsive">
            <table class="table table-striped">
               <colgroup>
                  <col class="col-xs-4">
                  <col class="col-xs-4">
                  <col class="col-xs-4">
               </colgroup>
               <thead>
               <tr>
                  <th><it:message key="itracker.web.attr.name"/></th>
                  <th><it:message key="itracker.web.attr.email"/></th>
                  <th><it:message key="itracker.web.attr.role"/></th>
               </tr>
               </thead>
               <tbody>
               <c:forEach items="${notifiedUsers}" var="user" varStatus="status">
                  <tr>
                     <td>${user.firstName}&nbsp;${user.lastName}</td>
                     <td>
                        <a href="mailto:${user.email}"
                           class="mailto">${user.email}</a>
                     </td>
                     <td>
                        <ul class="list-inline">
                           <c:forEach items="${notificationMap[user]}" var="role">
                              <li><it:message key="itracker.notification.role.${role.code}"/></li>
                           </c:forEach>
                        </ul>
                     </td>
                  </tr>
               </c:forEach>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
             

