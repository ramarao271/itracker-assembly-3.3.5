<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.editcustomfield.title.create"/>
<bean:define id="pageTitleArg" value=""/>
<%--   redirect logic moved to Action --%>
<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
   <tiles:put name="errorHide" value="${ false }"/>
</tiles:insert>

<div class="container-fluid maincontent">
   <html:form action="/editcustomfield">
      <html:hidden property="action"/>
      <html:hidden property="id"/>


      <c:if test="${action == 'update'}">
         <div class="row">
            <div class="col-xs-12">
               <div class="form-group"><label><it:message key="itracker.web.attr.id"/></label>
                  <p class="form-control-static">${field.id}</p>
               </div>
            </div>
         </div>
      </c:if>
      <div class="row">
         <div class="col-sm-6 col-sm-push-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.created"/>:</label>
               <p class="form-control-static text-nowrap"><it:formatDate
                       date="${ field.createDate }"/></p>
            </div>

            <div class="form-group">
               <label><it:message key="itracker.web.attr.lastmodified"/>:</label>
               <p class="form-control-static text-nowrap"><it:formatDate
                       date="${field.lastModifiedDate}"/></p>
            </div>
         </div>
         <div class="col-sm-6 col-sm-pull-6">

            <div class="form-group">
               <label><it:message key="itracker.web.attr.fieldtype"/>:</label>
               <html:select property="fieldType" styleClass="form-control">
                  <html:option value="${fieldTypeString}" styleClass="editColumnText"><it:message
                          key="itracker.web.generic.string"/></html:option>
                  <html:option value="${fieldTypeInteger}" styleClass="editColumnText"><it:message
                          key="itracker.web.generic.integer"/></html:option>
                  <html:option value="${fieldTypeDate}" styleClass="editColumnText"><it:message
                          key="itracker.web.generic.date"/></html:option>
                  <html:option value="${fieldTypeList}" styleClass="editColumnText"><it:message
                          key="itracker.web.generic.list"/></html:option>
               </html:select>
            </div>
            <div class="form-group">
               <label><it:message key="itracker.web.attr.required"/>:</label>
               <div class="checkbox-inline">
                  <html:checkbox property="required" value="true" styleId="required"/>
                  <label for="required"><it:message key="itracker.web.generic.yes"/></label>
               </div>
            </div>
            <div class="form-group">
               <label><it:message key="itracker.web.attr.sortoptions"/>:</label>
               <div class="checkbox-inline">
                  <html:checkbox property="sortOptionsByName" value="true" styleId="sortOptionsByName"/>
                  <label for="sortOptionsByName"><it:message key="itracker.web.generic.yes"/></label></div>
            </div>
            <div class="form-group">
               <label><it:message key="itracker.web.attr.dateformat"/>:</label>

               <html:select property="dateFormat" styleClass="editColumnText">
                  <html:option value="${dateFormatDateOnly}" styleClass="editColumnText"><it:message
                          key="itracker.web.attr.date.dateonly"/> (<it:message
                          key="itracker.dateformat.dateonly"/>)</html:option>
                  <%--                            <html:option value="${dateFormatTimeonly}" styleClass="editColumnText"><it:message key="itracker.web.attr.date.timeonly"/> (<it:message key="itracker.dateformat.timeonly"/>)</html:option>--%>
                  <html:option value="${dateFormatFull}" styleClass="editColumnText"><it:message
                          key="itracker.web.attr.date.full"/> (<it:message
                          key="itracker.dateformat.full"/>)</html:option>
               </html:select>
            </div>
         </div>

         <div class="col-sm-6">
         </div>
      </div>


      <c:if test="${field.fieldType.code == CustomFieldType_List}">
         <div class="row">
            <div class="col-xs-12">
               <div class="pull-right">
                  <it:link action="editcustomfieldvalueform" targetAction="create" paramName="id"
                           paramValue="${field.id}" styleClass="btn btn-link"
                           titleKey="itracker.web.admin.editcustomfield.option.create.alt"><it:message
                          key="itracker.web.admin.editcustomfield.option.create"/></it:link>
               </div>
               <h4><it:message key="itracker.web.attr.fieldoptions"/>:</h4>
            </div>
         </div>

         <div class="row">
            <div class="col-xs-12">
               <div class="table-responsive">

                  <table class="table table-striped">

                     <c:forEach items="${options}" var="option" varStatus="i">

                        <tr>
                           <td class="text-nowrap">
                              <div class="pull-right btn-group btn-group-sm">
                                 <it:link action="editcustomfieldvalueform" targetAction="update" paramName="id"
                                          paramValue="${option.id}" styleClass="btn btn-sm btn-link"
                                          titleKey="itracker.web.admin.editcustomfield.option.edit.alt"><it:message
                                         key="itracker.web.admin.editcustomfield.option.edit"/></it:link>
                                 <it:link action="removecustomfieldvalue" targetAction="delete" paramName="id"
                                          paramValue="${option.id}" styleClass="btn btn-sm btn-link"
                                          titleKey="itracker.web.admin.editcustomfield.option.delete.alt"><it:message
                                         key="itracker.web.admin.editcustomfield.option.delete"/></it:link>
                                 <c:if test="${i.index != 0}">
                                    <it:link action="ordercustomfieldvalue" targetAction="up" paramName="id"
                                             paramValue="${option.id}" styleClass="btn btn-sm btn-link"
                                             titleKey="itracker.web.admin.editcustomfield.option.orderup.alt">
                                       <it:message key="itracker.web.admin.editcustomfield.option.orderup"/>
                                    </it:link>
                                 </c:if>
                                 <c:if test="${i.index lt fn:length(options)-1 }">
                                    <it:link action="ordercustomfieldvalue" targetAction="down" paramName="id"
                                             paramValue="${option.id}" styleClass="btn btn-sm btn-link"
                                             titleKey="itracker.web.admin.editcustomfield.option.orderdown.alt">
                                       <it:message key="itracker.web.admin.editcustomfield.option.orderdown"/>
                                    </it:link>
                                 </c:if>
                              </div>
                                 ${ optionsMap[option.id]  } (${  option.value })
                           </td>
                        </tr>

                     </c:forEach>
                  </table>

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
               <label for="BASE">
                  <it:message key="itracker.web.attr.baselocale"/>
                  (<it:link action="editlanguageform"
                            targetAction="update#itracker.customfield.${ field.id }.label"
                            paramName="locale" paramValue="BASE"
                            titleKey="itracker.web.admin.listlanguages.update.alt"
                            arg0="${ languageNameValue.key.name }"
                            styleClass="pre">BASE</it:link>)
               </label>
               <c:if test="${action == 'update'}">
                  <c:set var="placeholder"><it:message key="itracker.customfield.${ field.id }.label"
                                                       locale="BASE"/></c:set>
               </c:if>
               <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
               <html:text property="${baseLocaleKey}" styleId="BASE" styleClass="form-control"/>
            </div>
         </div>
      </div>


      <c:forEach var="languageNameValue" items="${languagesNameValuePair}" varStatus="itStatus">
         <div class="row">
            <div class="col-xs-11 col-xs-offset-1">
               <div class="form-group">
                  <label for="${languageNameValue.key.name}">
                        ${languageNameValue.key.value}
                     (<it:link action="editlanguageform"
                               targetAction="update#itracker.customfield.${ field.id }.label"
                               paramName="locale" paramValue="${ languageNameValue.key.name }"
                               titleKey="itracker.web.admin.listlanguages.update.alt"
                               arg0="${ languageNameValue.key.name }"
                               styleClass="pre">${languageNameValue.key.name}</it:link>)
                  </label>

                  <c:if test="${action == 'update'}">
                     <c:set var="placeholder"><it:message key="itracker.customfield.${ field.id }.label"
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
                        (<it:link action="editlanguageform"
                                  targetAction="update#itracker.customfield.${ field.id }.label"
                                  paramName="locale" paramValue="${ locale.name }"
                                  titleKey="itracker.web.admin.listlanguages.update.alt"
                                  arg0="${ locale.name }"
                                  styleClass="pre">${locale.name}</it:link>)
                     </label>

                     <c:if test="${action == 'update'}">
                        <c:set var="placeholder"><it:message key="itracker.customfield.${ field.id }.label"
                                                             locale="${locale.name}"/></c:set>
                     </c:if>
                     <input type="hidden" name="placeholder" value="${fn:escapeXml(placeholder)}"/>
                     <html:text property="translations(${locale.name})" styleId="${locale.name}"
                                styleClass="form-control"/>
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
 


