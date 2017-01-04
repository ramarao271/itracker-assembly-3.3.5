<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.moveissue.title"/>
<%--bean:define id="pageTitleArg" value="${issue.id}"/--%>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="moveIssueForm"/>
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
   <div class="panel panel-info">
      <div class="panel-body">
         <it:message key="itracker.web.moveissue.instructions"/>
      </div>
   </div>
   <div class="row">


      <html:form action="/moveissue" styleClass="" method="post"
                 onsubmit="return validateMoveIssueForm(this);">
         <html:hidden property="issueId"/>
         <html:hidden property="caller"/>

         <div class="col-sm-4">
            <div class="form-group ">
               <label><it:message key="itracker.web.attr.issue"/>:</label>
               <p class="form-control-static">
                  <strong>${issue.id}</strong> <em>${issue.description}</em></p>

            </div>
         </div>

         <div class="col-sm-4">
            <div class="form-group">
               <label><it:message key="itracker.web.generic.from"/>:</label>
               <p class="form-control-static">
                  <it:formatIconAction forward="listissues"
                                       module="/module-projects"
                                       paramName="projectId"
                                       paramValue="${issue.project.id}"
                                       caller="editissue"
                                       icon="tasks"
                                       styleClass="issuelist" iconClass="fa-lg"
                                       info="itracker.web.image.view.project.alt"
                                       textActionKey="itracker.web.image.issuelist.texttag"/>
                  <strong>${issue.project.name}</strong></p>
            </div>
         </div>

         <div class="col-sm-4">
            <div class="form-group">

               <label><it:message key="itracker.web.generic.to"/>:</label>
               <html:select property="projectId" styleClass="form-control">
                  <c:forEach items="${projects}" var="projects" step="1">
                     <html:option value="${projects.id}">${projects.name}</html:option>
                  </c:forEach>
               </html:select>
            </div>
         </div>
         <div class="col-xs-12">
            <html:submit styleClass="btn btn-primary btn-block" altKey="itracker.web.button.update.alt"
                         titleKey="itracker.web.button.update.alt"><it:message
                    key="itracker.web.button.update"/></html:submit>
         </div>
      </html:form>

   </div>
</div>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
       
        	