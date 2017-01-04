<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey"
             value="itracker.web.admin.listattachments.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="container-fluid maincontent">

   <div class="row">
      <div class="col-xs-12">
         <div class="pull-right">
            <it:formatIconAction
                    action="exportattachments" icon="download" iconClass="fa-2x"
                    info="itracker.web.image.export.attachments.alt"
                    textActionKey="itracker.web.image.export.texttag"/>
         </div>
         <h4><it:message key="itracker.web.attr.attachments"/>:</h4>
      </div>
   </div>
   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">
            <table class="table table-striped table-condensed">
               <colgroup>
                  <col class="col-xs-1">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-1">
                  <col class="col-xs-2">
               </colgroup>
               <thead>
               <tr>
                  <th></th>
                  <th><it:message key="itracker.web.attr.issue"/></th>
                  <th><it:message key="itracker.web.attr.name"/></th>
                  <th><it:message key="itracker.web.attr.description"/></th>
                  <th class="text-right text-nowrap"><it:message key="itracker.web.attr.filesize"/></th>
                  <th class="text-right text-nowrap"><it:message key="itracker.web.attr.lastupdated"/></th>
               </tr>
               </thead>
               <tbody>
               <c:forEach items="${pto.attachments}" var="attachment" varStatus="i">
                  <tr>
                     <td>
                        <it:formatIconAction action="downloadAttachment" module="/module-projects"
                                             paramName="id" paramValue="${attachment.id}"
                                             icon="download" styleClass="download" iconClass="fa-lg"
                                             info="itracker.web.image.download.attachment.alt"
                                             textActionKey="itracker.web.image.download.texttag"/>
                        <it:formatIconAction action="removeattachment"
                                             paramName="id" paramValue="${attachment.id}"
                                             icon="remove" styleClass="deleteButton" iconClass="fa-lg"
                                             info="itracker.web.image.delete.attachment.alt"
                                             textActionKey="itracker.web.image.delete.texttag"/>
                     </td>
                     <td><c:out value="${attachment.issue.id}"/></td>
                     <td><c:out value="${attachment.originalFileName}"/></td>
                     <td><c:out value="${attachment.description}"/></td>
                     <td class="text-right text-nowrap">
                        <fmt:formatNumber value="${attachment.size / 1024}" pattern="0.##"/> <it:message
                             key="itracker.web.generic.kilobyte"/>
                     </td>
                     <td class="text-right text-nowrap"><it:formatDate
                             date="${attachment.lastModifiedDate}"/></td>
                  </tr>
               </c:forEach>

               <c:if test="${!pto.hasAttachments}">
                  <tr>
                     <td colspan="8" class="listRowText" align="left"><it:message
                             key="itracker.web.error.noattachments"/></td>
                  </tr>
               </c:if>
               </tbody>
               <tfoot>
               <tr>
                  <td colspan="3"></td>
                  <td class="text-right text-nowrap"><strong><it:message key="itracker.web.attr.total"/>:</strong></td>
                  <td class="text-right text-nowrap"><strong><fmt:formatNumber
                          value="${pto.sizeOfAllAttachments / 1024} " pattern="0.##"/> <it:message
                          key="itracker.web.generic.kilobyte"/></strong></td>
                  <td></td>
               </tr>
               </tfoot>
            </table>
         </div>
      </div>
   </div>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
