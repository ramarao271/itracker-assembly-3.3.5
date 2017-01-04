<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listworkflow.title"/>
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
         <div class="pull-right">
            <it:formatIconAction action="editworkflowscriptform" targetAction="create"
                                 icon="plus" iconClass="fa-2x"
                                 info="itracker.web.image.create.workflowscript.alt"
                                 textActionKey="itracker.web.image.create.texttag"/>
         </div>
         <h4><it:message key="itracker.web.attr.workflowscripts"/>:</h4>
      </div>
   </div>

   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">
            <table class="table table-striped">
               <colgroup>
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
               </colgroup>
               <thead>
               <tr>
                  <th></th>
                  <th><it:message key="itracker.web.attr.name"/></th>
                  <th><it:message key="itracker.web.attr.event"/></th>
                  <th class="text-right text-nowrap"><it:message key="itracker.web.attr.lastmodified"/></th>
               </tr>
               </thead>
               <tbody>
               <c:forEach items="${sc.workflowScripts}" var="aScript" varStatus="i">
                  <tr>
                     <td>
                        <it:formatIconAction action="editworkflowscriptform" paramName="id"
                                             paramValue="${aScript.id}" targetAction="update" arg0="${aScript.name}"
                                             icon="edit" iconClass="fa-lg"
                                             info="itracker.web.image.edit.workflowscript.alt"
                                             textActionKey="itracker.web.image.edit.texttag"/>
                        <it:formatIconAction action="removeworkflowscript" paramName="id"
                                             paramValue="${aScript.id}" arg0="${aScript.name}"
                                             icon="remove" iconClass="fa-lg" styleClass="deleteButton"
                                             info="itracker.web.image.delete.workflowscript.alt"
                                             textActionKey="itracker.web.image.delete.texttag"/>
                     </td>
                     <td>${aScript.name}</td>
                     <td><it:message key="itracker.workflow.field.event.${aScript.event}"/></td>
                     <td class="text-right text-nowrap"><it:formatDate date="${aScript.lastModifiedDate}"/></td>
                  </tr>
               </c:forEach>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
