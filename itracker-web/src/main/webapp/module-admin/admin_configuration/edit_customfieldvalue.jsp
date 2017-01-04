<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE HTML >
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
   <tiles:put name="errorHide" value="${ false }"/>
</tiles:insert>

<div class="container-fluid maincontent">
   <html:form action="/editcustomfieldvalue">
      <html:hidden property="action"/>
      <html:hidden property="id"/>

      <div class="row">

         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.value"/>:</label>
               <html:text property="value" styleClass="form-control"/>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.sortorder"/>:</label>
               <html:text property="sortOrder" styleClass="form-control"/>
            </div>
         </div>
      </div>

      <div class="row">
         <div class="col-xs-12">
            <h4><it:message key="itracker.web.attr.translations"/>:</h4>
         </div>
      </div>

      <c:set var="placeholder" value=""/>
      <div class="row">
         <div class="col-xs-12">
            <div class="form-group">
               <label for="BASE"><it:message key="itracker.web.attr.baselocale"/>

                  (<it:link action="editlanguageform" targetAction="update#${ messageKey }"
                            paramName="locale" paramValue="BASE"
                            titleKey="itracker.web.admin.listlanguages.update.alt" arg0="BASE"
                            styleClass="pre">BASE</it:link>)</label>
               <c:if test="${ not empty messageKey }">
                  <c:set var="placeholder"><it:message key="${ messageKey }" locale="BASE"/></c:set>
               </c:if>
               <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
               <html:text property="translations(BASE)" styleClass="form-control" styleId="BASE"/>
            </div>
         </div>
      </div>

      <c:forEach var="languageNameValue" items="${languagesNameValuePair}">
         <div class="row">
            <div class="col-xs-11 col-xs-offset-1">
               <div class="form-group">
                  <label for="${ languageNameValue.key.name }">
                        ${languageNameValue.key.value}
                     (<it:link action="editlanguageform" targetAction="update#${ messageKey }"
                               paramName="locale" paramValue="${ languageNameValue.key.name }"
                               titleKey="itracker.web.admin.listlanguages.update.alt"
                               arg0="${ languageNameValue.key.name }"
                               styleClass="pre">${ languageNameValue.key.name }</it:link>)
                  </label>
                  <c:if test="${ not empty messageKey }">
                     <c:set var="placeholder"><it:message key="${ messageKey }"
                                                          locale="${languageNameValue.key.name}"/></c:set>
                  </c:if>
                  <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                  <html:text property="translations(${languageNameValue.key.name })"
                             styleClass="form-control" styleId="${ languageNameValue.key.name }"/>
               </div>
            </div>
         </div>

         <c:forEach var="locale" items="${languageNameValue.value}">
            <div class="row">
               <div class="col-xs-10 col-xs-offset-2">
                  <div class="form-group">
                     <label for="${ locale.name }">
                           ${locale.value }
                        (<it:link action="editlanguageform" targetAction="update#${ messageKey }"
                                  paramName="locale" paramValue="${ locale.name }"
                                  titleKey="itracker.web.admin.listlanguages.update.alt"
                                  arg0="${ locale.name }"
                                  styleClass="pre">${ locale.name }</it:link>)
                     </label>
                     <c:if test="${ not empty messageKey }">
                        <c:set var="placeholder"><it:message key="${ messageKey }"
                                                             locale="${locale.name}"/></c:set>
                     </c:if>
                     <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                     <html:text property="translations(${locale.name})" styleClass="form-control"
                                styleId="${ locale.name }"/>
                  </div>
               </div>
            </div>
         </c:forEach>
      </c:forEach>


      <div class="row">
         <div class="col-xs-12">
            <c:choose>
               <c:when test="${action == 'update'}">
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
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>

