<%@ include file="/common/taglibs.jsp" %>

<jsp:useBean id="projects" scope="request" type="java.util.Collection"/>
<bean:define id="pageTitleKey" value="itracker.web.listprojects.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
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

<div class="container-fluid maincontent">
   <div class="row">
      <div class="col-xs-12">
         <h4><it:message key="itracker.web.attr.projects"/>:</h4>
      </div>
   </div>

   <c:set var="hasProjects" value="${ not empty( projects) }"/>
   <c:choose>
      <c:when test="${ hasProjects }">
         <div class="row">
            <div class="col-xs-12 table-responsive" id="projects">
               <table class=" table table-striped table-hover">
                  <colgroup>
                     <col class="col-xs-1">
                     <col class="col-xs-2">
                     <col class="col-xs-3">
                     <col class="col-xs-2">
                     <col class="col-xs-2">
                     <col class="col-xs-2">
                  </colgroup>

                  <thead>
                  <tr>
                     <th></th>
                     <th><it:message key="itracker.web.attr.name"/></th>
                     <th class="text-right"><it:message key="itracker.web.attr.openissues"/></th>
                     <th class="text-right"><it:message key="itracker.web.attr.resolvedissues"/></th>
                     <th class="text-right"><it:message key="itracker.web.attr.totalissues"/></th>
                     <th class="text-right"><it:message key="itracker.web.attr.lastissueupdate"/></th>
                  </tr>
                  </thead>
                  <tbody>
                  <c:set var="totalOpenIssues" value="0"/>
                  <c:set var="totalResolvedIssues" value="0"/>
                  <c:set var="totalNumberProjects" value="0"/>
                  <c:forEach var="project" varStatus="i" items="${ projects }" step="1">

                     <c:set var="totalOpenIssues" value="${ project.totalOpenIssues + totalOpenIssues }"/>
                     <c:set var="totalResolvedIssues" value="${ project.totalResolvedIssues + totalResolvedIssues}"/>
                     <c:set var="totalNumberProjects" value="${ totalNumberProjects + 1 }"/>

                     <tr id="project.${project.id}">
                        <td class="text-nowrap">
                           <it:formatIconAction forward="listissues" paramName="projectId"
                                                paramValue="${ project.id }"
                                                icon="tasks" iconClass="fa-lg"
                                                info="itracker.web.image.view.project.alt" arg0="${ project.name }"
                                                textActionKey="itracker.web.image.view.texttag"/>
                           <c:if test="${ project.active && project.canCreate }">
                              <it:formatIconAction forward="createissue" paramName="projectId"
                                                   paramValue="${ project.id }"
                                                   icon="plus" iconClass="fa-lg"
                                                   info="itracker.web.image.create.issue.alt"
                                                   arg0="${ project.name }"
                                                   textActionKey="itracker.web.image.create.texttag"/>
                           </c:if>
                           <it:formatIconAction forward="searchissues" paramName="projectId"
                                                paramValue="${ project.id }"
                                                icon="search" iconClass="fa-lg"
                                                info="itracker.web.image.search.issue.alt" arg0="${ project.name }"
                                                textActionKey="itracker.web.image.search.texttag"/>
                        </td>
                        <td> ${ project.name }</td>
                        <td class="text-right totalOpenIssues">${ project.totalOpenIssues }</td>
                        <td class="text-right totalResolvedIssues">${ project.totalResolvedIssues }</td>
                        <td class="text-right totalNumberIssues">${ project.totalNumberIssues }</td>
                        <td class="text-right text-nowrap lastUpdatedIssueDate">
                           <it:formatDate date="${ project.lastUpdatedIssueDate }"
                                          emptyKey="itracker.web.generic.notapplicable"/></td>
                     </tr>
                  </c:forEach>
                  </tbody>
                  <tfoot>
                  <tr class="listProjectsTotals">
                     <td class="text-right" colspan="2"><strong><it:message
                             key="itracker.web.attr.total"/>:&nbsp;<%-- ${ totalNumberProjects }--%></strong></td>
                     <td class="text-right totalOpenIssues"><strong>${ totalOpenIssues }</strong></td>
                     <td class="text-right totalResolvedIssues"><strong>${ totalResolvedIssues }</strong></td>
                     <td class="text-right totalNumberIssues">
                        <strong>${ totalOpenIssues + totalResolvedIssues }</strong></td>
                     <td></td>
                  </tr>
                  </tfoot>
               </table>
            </div>
         </div>
      </c:when>
      <c:otherwise>
         <div class="row">
            <div class="col-xs-12"><strong><it:message key="itracker.web.error.noprojects"/></strong>
            </div>
         </div>
      </c:otherwise>
   </c:choose>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body></html>
