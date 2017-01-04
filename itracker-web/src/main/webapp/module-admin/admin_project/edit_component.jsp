<%@ include file="/common/taglibs.jsp" %>

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

<html:form action="/editcomponent" acceptCharset="UTF-8" enctype="multipart/form-data">
   <html:hidden property="action"/>
   <html:hidden property="projectId"/>
   <html:hidden property="id"/>

   <table border="0" cellspacing="0" cellspacing="1" width="100%">
      <tr>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.name"/>:</td>
         <td><html:text property="name" styleClass="editColumnText"/></td>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.created"/>:</td>
         <td class="editColumnText"><it:formatDate date="${component.createDate}"/></td>
      </tr>
      <tr>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.description"/>:</td>
         <td><html:text property="description" styleClass="editColumnText"/></td>
         <td class="editColumnTitle"><it:message key="itracker.web.attr.lastmodified"/>:</td>
         <td class="editColumnText"><it:formatDate date="${component.lastModifiedDate}"/></td>
      </tr>
      <tr>
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="10" width="1"/></td>
      </tr>
      <c:choose>
         <c:when test="${isNew}">
            <tr>
               <td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.create.alt"
                                                         titleKey="itracker.web.button.create.alt"><it:message
                       key="itracker.web.button.create"/></html:submit></td>
            </tr>
         </c:when>
         <c:otherwise>
            <tr>
               <td colspan="4" align="left"><html:submit styleClass="button" altKey="itracker.web.button.update.alt"
                                                         titleKey="itracker.web.button.update.alt"><it:message
                       key="itracker.web.button.update"/></html:submit></td>
            </tr>
         </c:otherwise>
      </c:choose>
   </table>
</html:form>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
