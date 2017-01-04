<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listreports.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>

<c:set var="pageTitleKey" scope="request">itracker.web.admin.listreports.title</c:set>
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

<div class="container-fluid maincontent">

   <div class="row">
      <div class="col-xs-12">
         <div class="pull-right">
            <it:formatIconAction action="editreportform" targetAction="create"
                                 icon="plus" iconClass="fa-2x"
                                 info="itracker.web.image.create.report.alt"
                                 textActionKey="itracker.web.image.create.texttag"/>
         </div>
         <h4><it:message key="itracker.web.attr.reports"/>:</h4>
      </div>
   </div>
   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">
            <table class="table table-striped">
               <colgroup>
                  <col class="col-xs-2">
                  <col class="col-xs-3">
                  <col class="col-xs-4">
                  <col class="col-xs-3">
               </colgroup>
               <thead>

               <tr>
                  <th></th>
                  <th><it:message key="itracker.web.attr.report"/></th>
                  <th><it:message key="itracker.web.attr.description"/></th>
                  <th class="text-right text-nowrap"><it:message key="itracker.web.attr.lastmodified"/></th>
               </tr>
               </thead>

               <c:forEach items="${reports}" var="report" varStatus="i">
                  <tr>

                     <td>
                        <it:formatIconAction action="editreportform" paramName="id" paramValue="${report.id}"
                                             targetAction="update"
                                             icon="edit" info="itracker.web.image.edit.report.alt" iconClass="fa-lg"
                                             arg0="${report.name}" textActionKey="itracker.web.image.edit.texttag"/>
                        <it:formatIconAction action="downloadreport" paramName="id" paramValue="${report.id}"
                                             icon="download" info="itracker.web.image.download.report.alt"
                                             iconClass="fa-lg"
                                             arg0="${report.name}"
                                             textActionKey="itracker.web.image.download.texttag"/>
                        <it:formatIconAction action="removereport" paramName="id" paramValue="${report.id}"
                                             icon="remove" iconClass="fa-lg" styleClass="deleteButton"
                                             info="itracker.web.image.delete.report.alt" arg0="${report.name}"
                                             textActionKey="itracker.web.image.delete.texttag"/>
                     </td>
                     <td>${report.name}</td>
                     <td><it:formatDescription truncateLength="60">${report.description}</it:formatDescription></td>
                     <td class="text-right text-nowrap"><it:formatDate date="${report.lastModifiedDate}"/></td>

                  </tr>

               </c:forEach>

            </table>
         </div>
      </div>
   </div>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
