<%@ include file="/common/taglibs.jsp" %>

<c:set var="update" value="${workflowScriptForm.action == 'update'}"/>
<c:if test="${update}">
   <c:set var="pageTitleKey" scope="request">itracker.web.admin.editworkflowscript.title.update</c:set>
   <c:set var="pageTitleArg" scope="request" value="${workflowScriptForm.name}"/>
</c:if>
<c:if test="${not update}">
   <c:set var="pageTitleKey" scope="request">itracker.web.admin.editworkflowscript.title.create</c:set>
</c:if>

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
<html:form action="/editworkflowscript">
   <html:hidden property="action"/>
   <html:hidden property="id"/>


   <c:if test="${update}">
      <div class="row">
         <div class="col-sm-12">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.id"/>:</label>
               <p class="form-control-static">${workflowScriptForm.id}</p>
            </div>
         </div>
      </div>
   </c:if>
   <div class="row">

      <div class="col-sm-6 col-sm-push-6">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.created"/>:</label>
            <p class="form-control-static"><it:formatDate date="${workflowscript.createDate}"/></p>
         </div>
         <div class="form-group">
            <label><it:message key="itracker.web.attr.lastmodified"/>:</label>
            <p class="form-control-static"><it:formatDate date="${workflowscript.lastModifiedDate}"/></p>
         </div>
      </div>
      <div class="col-sm-6 col-sm-pull-6">
         <div class="form-group">
            <label><it:message key="itracker.web.attr.name"/>:</label>
            <html:text property="name" size="40" styleClass="form-control"/>
         </div>
         <div class="form-group">
            <label><it:message key="itracker.web.attr.event"/>:</label>
            <html:select property="event" styleClass="form-control">
               <html:optionsCollection property="eventOptions"/>
            </html:select>
         </div>
      </div>
   </div>
   <div class="row">
      <div class="col-xs-12">
         <div class="form-group">
            <div class="pull-right">
               <div class="radio-inline">
                  <label for="beanshell"><html:radio property="language" value="BeanShell" styleId="beanshell"
                                                     title="BeanShell"/> BeanShell</label>
               </div>
               <div class="radio-inline">
                  <label for="groovy"><html:radio property="language" value="Groovy" styleId="groovy" title="Groovy"/>
                     Groovy</label>
               </div>
            </div>
            <label><it:message key="itracker.web.attr.script"/>:
            </label>

            <html:textarea rows="20" property="script" styleClass="form-control pre"/>

         </div>
      </div>
   </div>

   <div class="row">
      <div class="col-xs-12">
         <c:choose>
            <c:when test="${update}">
               <html:submit styleClass="btn btn-primary btn-block"
                            altKey="itracker.web.button.update.alt"
                            titleKey="itracker.web.button.update.alt">
                  <it:message key="itracker.web.button.update"/>
               </html:submit>
            </c:when>
            <c:otherwise>
               <html:submit styleClass="btn btn-primary btn-block" altKey="itracker.web.button.create.alt"
                            titleKey="itracker.web.button.create.alt"><it:message
                       key="itracker.web.button.create"/></html:submit>
            </c:otherwise>
         </c:choose>
      </div>
   </div>
</html:form>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
