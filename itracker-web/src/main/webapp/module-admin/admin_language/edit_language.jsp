<%@ include file="/common/taglibs.jsp" %>


<c:set var="isUpdate" value="${ languageForm.action == 'update' }"/>
<c:set var="localeType" value="${editlangtype}"/>

<bean:parameter id="action" name="action"/>
<bean:parameter id="locale" name="locale"/>
<bean:parameter id="parentLocale" name="parentLocale" value="BASE"/>

<c:choose>
   <c:when test="${ isUpdate }">
      <c:set var="pageTitleKey" scope="request">itracker.web.admin.editlanguage.title.update</c:set>
   </c:when>
   <c:otherwise>
      <c:set var="pageTitleKey" scope="request">itracker.web.admin.editlanguage.title.create</c:set>
   </c:otherwise>
</c:choose>
<c:set var="pageTitleArg" value="${ languageForm.localeTitle }" scope="request"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
   <tiles:put name="errorHide" value="${ false }"/>
</tiles:insert>

<div class="container-fluid maincontent">
   <html:form method="post" action="/editlanguage">
      <html:hidden property="action"/>
      <html:hidden property="parentLocale"/>

      <c:if test="${ isUpdate }">
         <html:hidden property="locale"/>
      </c:if>
      <div class="row">
         <div class="col-sm-12">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.localeiso"/>:</label>
               <html:text readonly="${ readOnly }" property="locale"
                          styleClass="form-control" maxlength="${ maxLength }" size="${ maxSize }"/>
            </div>
         </div>
      </div>

      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.language"/>:</label>
               <html:text property="localeTitle" styleClass="form-control"
                          maxlength="20" size="20"/>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.baselocale"/>:</label>
               <html:text property="localeBaseTitle" styleClass="form-control"
                          maxlength="20" size="20"/>
            </div>
         </div>
      </div>


      <div class="row">
         <div class="col-xs-12">
            <div class="table-responsive">

               <table class="table table-striped table-hover table-condensed table-language">

                  <c:set var="maxLength" value="2"/>
                  <c:set var="maxSize" value="2"/>
                  <c:set var="locMsg" value="itracker.web.attr.language"/>
                  <c:set var="afterTd" value="<td> </td>"/>
                  <c:set var="beforeTd" value=""/>

                  <c:set var="locHead"><it:message key="itracker.web.attr.baselocale"/></c:set>
                  <c:set var="readOnly" value="false"/>
                  <c:if test="${ isUpdate }">
                     <c:set var="readOnly" value="true"/>
                  </c:if>

                  <c:if test="${ localeType == 1 }">
                     <colgroup>
                        <col class="col-xs-6">
                        <col class="col-xs-6">
                     </colgroup>
                  </c:if>
                  <c:if test="${ localeType == 2 }">
                     <colgroup>
                        <col class="col-xs-4">
                        <col class="col-xs-4">
                        <col class="col-xs-4">
                     </colgroup>
                     <%--<c:set var="beforeTd" value="<td>${locHead}</td>"/>--%>
                  </c:if>
                     <%-- if localeType == SystemConfigurationUtilities.LOCALE_TYPE_LOCALE --%>
                  <c:if test="${ localeType == 3}">
                     <colgroup>
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                     </colgroup>

                     <c:set var="afterTd" value=""/>

                     <c:set var="locHead2"><it:message key="${locMsg}"/> ( <c:out
                             value="${ languageForm.parentLocale }"/> )</c:set>
                     <c:set var="beforeTd" value="<td>${locHead}</td><td>${locHead2}</td>"/>
                     <c:set var="locMsg" value="itracker.web.attr.locale"/>
                     <c:set var="maxLength" value="5"/>
                     <c:set var="maxSize" value="5"/>
                  </c:if>

                  <thead>
                  <tr>
                     <th><it:message key="itracker.web.attr.key"/></th>
                        ${ beforeTd }
                     <th><it:message key="${ locMsg }"/></th>
                        ${ afterTd }
                  </tr>
                  </thead>

                  <logic:iterate id="itemlangs" name="languageForm" property="items">
                     <bean:define name="itemlangs" property="key" id="key" type="java.lang.String"/>
                     <bean:define name="itemlangs" property="value" id="value" type="java.lang.String"/>

                     <c:set var="propertyKey" value="items(${ key })"/>
                     <c:set var="isLongString" value="${ it:ITrackerResources_IsLongString(key) }"/>

                     <c:if test="${ (key != 'itracker.locales') &&  (key != 'itracker.locale.name') }">
                        <tr>
                           <td><label for="${ key }"><code class="pre">${ key }</code></label></td>
                              <%-- localeType != SystemConfigurationUtilities.LOCALE_TYPE_BASE --%>
                           <c:if test="${ localeType > 1 }">
                              <td>
                                 <c:set var="pl" value="BASE"/>
                                 <c:choose>
                                    <c:when test="${ isLongString }">
                                       <pre class="pre localization pre-scrollable"><it:message key="${ key }" locale="BASE"/></pre>
                                    </c:when>
                                    <c:otherwise>
                                       <code class="pre localization"><it:message key="${ key }" locale="BASE"/></code>
                                    </c:otherwise>
                                 </c:choose>
                              </td>
                           </c:if>
                              <%-- localeType != SystemConfigurationUtilities.LOCALE_TYPE_LANGUAGE --%>
                           <c:if test="${ localeType > 2 }">
                              <td>
                                 <c:set var="pl" value="${languageForm.parentLocale}"/>
                                 <c:choose>
                                    <c:when test="${ isLongString }">
                                       <pre class="pre localization localizationSub pre-scrollable"><it:message key="${ key }"
                                               locale="${languageForm.parentLocale}"/></pre>
                                    </c:when>
                                    <c:otherwise>
                                       <code class="pre localization localizationSub"><it:message key="${ key }"
                                               locale="${languageForm.parentLocale}"/></code>
                                    </c:otherwise>
                                 </c:choose>
                              </td>
                           </c:if>
                           <c:set var="lc" value="${isUpdate?languageForm.locale:pl}"/>

                           <c:set var="loadedString"><it:message key="${key}" locale="${lc}"/></c:set>

                           <td>
                              <!-- ${loadedString} -->
                              <input type="hidden" name="placeholder" value="${fn:escapeXml(loadedString)}"/>
                              <c:choose>
                                 <c:when test="${ isLongString }">
                                    <html:textarea indexed="false" name="languageForm" rows="6"
                                                   property="${ propertyKey }"
                                                   styleClass="form-control pre-scrollable" styleId="${ key }"/>
                                 </c:when>
                                 <c:otherwise>
                                    <html:text indexed="false" name="languageForm" property="${ propertyKey }"
                                               styleClass="form-control"
                                               styleId="${ key }"/>
                                 </c:otherwise>
                              </c:choose>
                           </td>
                              ${ afterTd }
                        </tr>

                     </c:if>
                  </logic:iterate>
               </table>
            </div>
         </div>
      </div>

      <div class="row">
         <div class="col-xs-12">
            <c:choose>
               <c:when test="${ isUpdate }">
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

