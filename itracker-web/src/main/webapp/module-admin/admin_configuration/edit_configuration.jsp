<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.editconfiguration.title.create"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
   <tiles:put name="errorHide" value="${ false }"/>
</tiles:insert>

<div class="container-fluid maincontent">
   <html:form action="/editconfiguration">
      <html:hidden property="action"/>
      <html:hidden property="id"/>


      <c:if test="${ configurationForm.action=='createstatus' || (configurationForm.action == 'update'
    && not empty configurationForm.value) }">
         <div class="row">
            <div class="col-xs-12">
               <div class="form-group">
                  <label><%-- TODO: Proper label localization --%>
                     <c:choose>
                        <c:when test="${ configurationForm.action=='createstatus' }"><it:message
                                key="itracker.web.attr.status"/>
                        </c:when>
                        <c:otherwise><it:message key="${configurationForm.typeKey}"/>
                        </c:otherwise>
                     </c:choose>
                     <it:message key="itracker.web.attr.value"/>:
                  </label>
                  <c:choose>
                     <c:when test="${ configurationForm.action=='createstatus' }">
                        <html:text property="value" styleClass="form-control"/>
                     </c:when>
                     <c:otherwise>
                        <p class="form-control-static">${configurationForm.value}</p>
                     </c:otherwise>
                  </c:choose>
               </div>
            </div>
         </div>
      </c:if>

      <div class="row">
         <div class="col-xs-12">
            <h4><it:message key="itracker.web.attr.translations"/>:</h4>
         </div>
      </div>
      <c:set var="placeholder" value=""/>

      <div class="row">
         <div class="col-xs-12">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.baselocale"/>
                  (<it:link action="editlanguageform" targetAction="update#${ configurationForm.key }"
                            paramName="locale" paramValue="BASE"
                            titleKey="itracker.web.admin.listlanguages.update.alt" arg0="BASE"
                            styleClass="pre">BASE</it:link>)
               </label>
               <c:if test="${configurationForm.action == 'update'}">
                  <c:set var="placeholder"><it:message key="${ configurationForm.key }" locale="BASE"/></c:set>
               </c:if>
               <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
               <html:text property="translations(BASE)" styleId="BASE" styleClass="form-control"/>
            </div>
         </div>
      </div>
      <c:forEach var="languageNameValue" items="${configurationForm.languages}" varStatus="itStatus">

         <div class="row">
            <div class="col-xs-11 col-xs-offset-1">
               <div class="form-group">
                  <label for="${languageNameValue.key.name}">
                        ${languageNameValue.key.value}
                     (<it:link action="editlanguageform" targetAction="update#${ configurationForm.key }"
                               paramName="locale" paramValue="${ languageNameValue.key.name }"
                               titleKey="itracker.web.admin.listlanguages.update.alt"
                               arg0="${ languageNameValue.key.name }"
                               styleClass="pre">${languageNameValue.key.name}</it:link>)
                  </label>

                  <c:if test="${configurationForm.action == 'update'}">
                     <c:set var="placeholder"><it:message key="${ configurationForm.key }"
                                                          locale="${languageNameValue.key.name}"/></c:set>
                  </c:if>
                  <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                  <html:text property="translations(${languageNameValue.key.name})"
                             styleId="${languageNameValue.key.name}" styleClass="form-control"/>
               </div>

            </div>
         </div>
         <c:forEach var="locale" items="${languageNameValue.value }" varStatus="itLStatus">

            <div class="row">
               <div class="col-xs-10 col-xs-offset-2">
                  <div class="form-group">
                     <label for="${locale.name}">
                           ${locale.value}
                        (<it:link action="editlanguageform" targetAction="update#${ configurationForm.key }"
                                  paramName="locale" paramValue="${ locale.name }"
                                  titleKey="itracker.web.admin.listlanguages.update.alt" arg0="${ locale.name }"
                                  styleClass="pre">${locale.name}</it:link>)
                     </label>

                     <c:if test="${configurationForm.action == 'update'}">
                        <c:set var="placeholder"><it:message key="${ configurationForm.key }"
                                                             locale="${locale.name}"/></c:set>
                     </c:if>
                     <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                     <html:text property="translations(${locale.name})" styleId="${locale.name}"
                                styleClass="form-control"/></div>
               </div>
            </div>
         </c:forEach>
      </c:forEach>

      <div class="row">
         <div class="col-xs-12">
            <c:choose>
               <c:when test="${configurationForm.action == 'update'}">
                  <html:submit styleClass="btn btn-block btn-primary"
                               altKey="itracker.web.button.update.alt"
                               titleKey="itracker.web.button.update.alt"><it:message
                          key="itracker.web.button.update"/></html:submit>
               </c:when>
               <c:otherwise>
                  <html:submit styleClass="button"
                               altKey="itracker.web.button.create.alt"
                               titleKey="itracker.web.button.create.alt"><it:message
                          key="itracker.web.button.create"/></html:submit>
               </c:otherwise>
            </c:choose>
         </div>
      </div>
   </html:form>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
