<%@ include file="/common/taglibs.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML>
<bean:define toScope="request" id="pageTitleKey" value="itracker.web.listissues.title"/>
<bean:define toScope="request" id="pageTitleArg" value="${project.name}"/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="container-fluid maincontent">
   <div class="row">

      <div class="col-xs-12">

         <div class="pull-right">
            <c:if test="${canCreateIssue}">
               <it:formatIconAction forward="createissue"
                                    paramName="projectId"
                                    paramValue="${project.id}"
                                    icon="plus" iconClass="fa-2x"
                                    info="itracker.web.image.create.issue.alt"
                                    arg0="${project.name}"
                                    textActionKey="itracker.web.image.create.texttag"/>
            </c:if>
            <it:formatIconAction forward="searchissues"
                                 paramName="projectId"
                                 paramValue="${project.id}"
                                 icon="search" iconClass="fa-2x"
                                 info="itracker.web.image.search.issue.alt"
                                 arg0="${project.name}"
                                 textActionKey="itracker.web.image.search.texttag"/>
         </div>
         <h4><it:message key="itracker.web.attr.issues"/>:</h4>
      </div>
   </div>
   <div class="row">
      <div class="col-xs-12 table-responsive" id="issues">
         <table class=" table table-striped table-hover">
            <colgroup>
               <col class="col-xs-2">
               <col class="col-xs-2">
               <col class="col-xs-1">
               <col class="col-xs-1">
               <col class="col-xs-2">
               <col class="col-xs-2">
               <col class="col-xs-2">
            </colgroup>
            <thead>
            <tr>
               <th class="text-right"><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}"
                                                               styleClass=""
                                                               order="id"><it:message
                       key="itracker.web.attr.id"/></it:formatPaginationLink></th>
               <th><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass=""
                                            order="stat"><it:message
                       key="itracker.web.attr.status"/></it:formatPaginationLink></th>
               <th><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass=""
                                            order="sev"><it:message
                       key="itracker.web.attr.severity"/></it:formatPaginationLink></th>
               <th><it:message key="itracker.web.attr.components"/></th>
               <th><it:message key="itracker.web.attr.description"/></th>
               <th><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass=""
                                            order="own"><it:message
                       key="itracker.web.attr.owner"/></it:formatPaginationLink></th>
               <th class="text-right"><it:formatPaginationLink page="/list_issues.do" projectId="${project.id}"
                                                               styleClass=""
                                                               order="lm"><it:message
                       key="itracker.web.attr.lastmodified"/></it:formatPaginationLink></th>
            </tr>

            </thead>

            <tbody>
            <c:forEach items="${issuePTOs}" var="issuePTO"
                       varStatus="i">

               <tr id="issue.${i.count}">

                  <td class="text-right">
                     <div class="pull-left">
                        <it:formatIconAction forward="viewissue" module="/module-projects" paramName="id"
                                             paramValue="${issuePTO.issue.id}"
                                             icon="tasks" iconClass="fa-lg"
                                             info="itracker.web.image.view.issue.alt" arg0="${issuePTO.issue.id}"
                                             textActionKey="itracker.web.image.view.texttag"/>
                        <c:if test="${issuePTO.userCanEdit}">
                           <it:formatIconAction action="editissueform" module="/module-projects" paramName="id"
                                                paramValue="${issuePTO.issue.id}" icon="edit" iconClass="fa-lg"
                                                info="itracker.web.image.edit.issue.alt" arg0="${issuePTO.issue.id}"
                                                textActionKey="itracker.web.image.edit.texttag"/>
                        </c:if>

                        <c:if test="${issuePTO.userHasIssueNotification}">
                    <span class="HTTP_POST">
            	    <it:formatIconAction forward="watchissue" paramName="id" paramValue="${issuePTO.issue.id}"
                                        icon="bell" iconClass="fa-lg"
                                        info="itracker.web.image.watch.issue.alt" arg0="${issuePTO.issue.id}"
                                        textActionKey="itracker.web.image.watch.texttag"/>
                    </span>
                        </c:if>
                     </div>
                        ${issuePTO.issue.id}</td>
                  <td class="status"> ${issuePTO.statusLocalizedString}</td>
                  <td class="severity">${issuePTO.severityLocalizedString}</td>
                  <td>${issuePTO.componentsSize}</td>
                  <td><it:formatDescription>${issuePTO.issue.description}</it:formatDescription></td>
                  <td>
                     <c:choose>
                        <c:when test="${issuePTO.unassigned}">
                           <it:message key="itracker.web.generic.unassigned"/>
                        </c:when>
                        <c:otherwise>
                           <it:formatIssueOwner issue="${issuePTO.issue}" format="short"/>
                        </c:otherwise>
                     </c:choose>
                  </td>
                  <td class="text-right text-nowrap"><it:formatDate date="${issuePTO.issue.lastModifiedDate}"/></td>
               </tr>


            </c:forEach>

            </tbody>
            <tfoot>
            <c:choose>

               <c:when test="${hasIssues}">
                  <tr class="listRowUnshaded" align="left">
                     <td colspan="7" align="left">
                        <it:message key="itracker.web.generic.totalissues" arg0="${pagination.total}"/>
                     </td>
                  </tr>
               </c:when>
               <c:otherwise>
                  <tr>
                     <td colspan="7" align="left"><it:message key="itracker.web.error.noissues"/></td>
                  </tr>
               </c:otherwise>
            </c:choose>
            </tfoot>

         </table>
      </div>
   </div>
   <c:if test="${pagination.perPage > 0 && pagination.pageCount > 0}">
      <div class="row">
         <div class="col-xs-10 col-xs-offset-1">

            <c:if test="${pagination.currentPage > 1}">
               <it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="pull-left"
                                        start="${pagination.start - pagination.perPage}"
                                        order="${orderParam}">
                  <%--<it:message key="itracker.web.generic.prevpage"/>--%>
                  <i class="fa fa-2x fa-chevron-left"></i>

               </it:formatPaginationLink>
            </c:if>

            <c:if test="${pagination.currentPage < pagination.pageCount}">
               <it:formatPaginationLink page="/list_issues.do" projectId="${project.id}" styleClass="pull-right"
                                        start="${pagination.start + pagination.perPage}"
                                        order="${orderParam}">
                  <i class="fa fa-2x fa-chevron-right"></i>
                  <%--<it:message key="itracker.web.generic.nextpage"/>--%>
               </it:formatPaginationLink>
            </c:if>

            <div class="paging">
               (<fmt:formatNumber
                    value="${pagination.currentPage}" maxFractionDigits="0"/>/<fmt:formatNumber
                    value="${pagination.pageCount}" maxFractionDigits="0"/>)
            </div>

         </div>
      </div>
   </c:if>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
