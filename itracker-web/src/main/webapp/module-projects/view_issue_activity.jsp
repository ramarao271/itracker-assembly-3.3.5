<%@ include file="/common/taglibs.jsp" %>

<bean:define toScope="request" id="pageTitleKey" value="itracker.web.issueactivity.title"/>
<bean:define toScope="request" id="pageTitleArg" value="${issueId}"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="container-fluid maincontent">
   <c:choose>
      <c:when test="${not empty activities}">
         <div class="row">

            <div class="col-xs-12 table-responsive">

               <table class="table table-striped">
                 
                  <colgroup>
                     <col class="col-sm-2" />
                     <col class="col-sm-4" />
                     <col class="col-sm-4" />
                     <col class="col-sm-2" />
                  </colgroup>
                  <thead>
                  <tr>
                     <th><it:message key="itracker.web.attr.type"/></th>
                     <th><it:message key="itracker.web.attr.description"/></th>
                     <th><it:message key="itracker.web.attr.user"/></th>
                     <th class="text-right"><it:message key="itracker.web.attr.date"/></th>
                  </tr>
                  </thead>
                  <tbody>
                  <c:forEach var="activity" items="${activities}" varStatus="i">
                     <tr>
                        <td>${activity.value}</td>
                        <td>${activity.key.description}</td>
                        <td>${activity.key.user.fullName}</td>
                        <td class="text-right text-nowrap"><it:formatDate date="${activity.key.createDate}"/></td>
                     </tr>
                  </c:forEach>   
                  </tbody>
               </table>
            </div>
         </div>
      </c:when>
   </c:choose>
   <c:if test='${empty activities}'>
      <div class="alert alert-danger">
         <it:message key="itracker.web.error.noactivity"/>
      </div>
   </c:if>

</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

