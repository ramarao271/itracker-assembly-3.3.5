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

<div class="container-fluid maincontent">
   <html:form action="/editproject" acceptCharset="UTF-8" enctype="multipart/form-data">
      <html:hidden property="action"/>
      <html:hidden property="id"/>


      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.name"/>:</label>
               <html:text property="name" styleClass="form-control"/>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.status"/>:</label>
               <html:select property="status" styleClass="form-control">
                  <c:forEach var="status" items="${statuses}">
                     <html:option value="${status.value}">${status.name}
                     </html:option>
                  </c:forEach>
               </html:select>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.description"/>:</label>
               <html:text property="description" styleClass="form-control"/>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.created"/>:</label>
               <p class="form-control-static">
                  <it:formatDate date="${project.createDate}"/>
               </p>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-6 col-sm-push-6">
            <div class="form-group">
               <c:choose>
                  <c:when test="${isUpdate}">
                     <label><it:message key="itracker.web.attr.lastmodified"/>:</label>
                     <p class="form-control-static"><it:formatDate date="${project.lastModifiedDate}"/></p>
                  </c:when>
                  <c:otherwise>
                     <c:choose>
                        <c:when test="${allowPermissionUpdateOption}">
                           <label><it:message key="itracker.web.admin.editproject.addusers"/>:</label>
                           <div class="form-inline">
                              <html:select
                                      property="users" size="5" multiple="true"
                                      styleClass="form-control">
                                 <c:forEach var="user" items="${users}">
                                    <html:option value="${user.id}">${user.firstName}&nbsp;${user.lastName}
                                    </html:option>
                                 </c:forEach>
                              </html:select>
                              <html:select property="permissions" size="5" multiple="true" styleClass="form-control">
                                 <c:forEach var="permission" items="${permissions}">
                                    <html:option value="${permission.value}">${permission.name}
                                    </html:option>
                                 </c:forEach>
                              </html:select>
                           </div>
                        </c:when>
                     </c:choose>
                  </c:otherwise>
               </c:choose>
            </div>
         </div>
         <div class="col-sm-6 col-sm-pull-6">

            <div class="form-group">
               <label><it:message key="itracker.web.attr.owners"/>:</label>
               <html:select property="owners" size="5" multiple="true"
                            styleClass="form-control">
                  <c:forEach var="owner" items="${owners}">
                     <html:option value="${owner.id}">${owner.firstName}&nbsp;${owner.lastName}
                     </html:option>
                  </c:forEach>
               </html:select>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-12">
            <h4><it:message key="itracker.web.admin.editproject.options"/>:</h4>
         </div>
      </div>

      <div class="row">
         <div class="col-sm-6">
            <div class="checkbox-inline">
               <html:multibox property="options" value="${optionSupressHistoryHtml}"/>
               <label><it:message key="itracker.web.admin.editproject.options.html"/></label>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="checkbox-inline">
               <html:multibox property="options" value="${optionLiteralHistoryHtml}"/>
               <label><it:message key="itracker.web.admin.editproject.options.htmlliteral"/></label>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="checkbox-inline">
               <html:multibox property="options" value="${optionPredefinedResolutions}"/>
               <label><it:message key="itracker.web.admin.editproject.options.resolution"/></label>
            </div>
         </div>

         <div class="col-sm-6">
            <div class="checkbox-inline">
               <html:multibox property="options" value="${optionNoAttachments}"/>
               <label><it:message key="itracker.web.admin.editproject.options.noattach"/></label>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="checkbox-inline">
               <html:multibox property="options" value="${optionAllowAssignToClose}"/>
               <label><it:message key="itracker.web.admin.editproject.options.closed"/></label>
            </div>
         </div>
         <c:if test="${allowSelfRegister}">
            <div class="col-sm-6">
               <div class="checkbox-inline">
                  <html:multibox property="options" value="${optionAllowSelfRegisteredViewAll}"/>
                  <label><it:message key="itracker.web.admin.editproject.options.srview"/></label>
               </div>
            </div>
            <div class="col-sm-6">
               <div class="checkbox-inline">
                  <html:multibox property="options" value="${optionAllowSefRegisteredCreate}"/>
                  <label><it:message key="itracker.web.admin.editproject.options.srcreate"/></label>
               </div>
            </div>
         </c:if>
      </div>

      <c:if test="${ not empty customFields }">
         <div class="row">
            <div class="col-sm-12">
               <h4><it:message key="itracker.web.attr.customfields"/>:</h4>
            </div>
         </div>
         <div class="row">
            <c:forEach var="customField" items="${ customFields }" varStatus="i">
               <div class="col-sm-6">
                  <div class="checkbox-inline">
                     <html:multibox property="fields" value="${customField.id}"/>
                     <label>${customField.name} (${customField.type})</label>
                  </div>
               </div>

            </c:forEach>
         </div>
      </c:if>

      <div class="row">
         <div class="col-sm-12">
            <c:choose>
               <c:when test="${isUpdate}">
                  <html:submit styleClass="btn btn-primary btn-block"
                               altKey="itracker.web.button.update.alt"
                               titleKey="itracker.web.button.update.alt">
                     <it:message key="itracker.web.button.update"/>
                  </html:submit>
               </c:when>
               <c:otherwise>
                  <html:submit styleClass="btn btn-primary btn-block"
                               altKey="itracker.web.button.create.alt"
                               titleKey="itracker.web.button.create.alt">
                     <it:message key="itracker.web.button.create"/>
                  </html:submit>
               </c:otherwise>
            </c:choose>
         </div>
      </div>

      <div class="clearfix"></div>

      <c:if test="${isUpdate}">

         <div class="row">
            <div class="col-sm-12">
               <h4>
                  <div class="pull-right">
                     <c:if test="${currUser.superUser}">

                        <it:formatIconAction action="editprojectscriptform"
                                             paramName="projectId"
                                             paramValue="${ project.id }"
                                             targetAction="update"
                                             icon="plus" styleClass="fa-lg"
                                             info="itracker.web.image.create.projectscript.alt"
                                             arg0="${ project.name }"
                                             textActionKey="itracker.web.image.create.texttag"/>

                     </c:if>
                  </div>
                  <it:message key="itracker.web.attr.scripts"/>:
               </h4>
            </div>
         </div>
         <div class="row">
            <div class="col-xs-12">
               <div class="table-responsive">
                  <table class="table table-striped">
                     <colgroup>
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                     </colgroup>
                     <thead>
                        <%-- TODO: this should be tested more, or postponed for next release? --%>

                        <%-- REVIEW: Should it be possible to assign same script on different fields of the same project (HBM index)? --%>

                     <tr>
                        <th><it:message key="itracker.web.attr.field"/></th>
                        <th><it:message key="itracker.web.attr.script"/></th>
                        <th align="left"><it:message key="itracker.web.attr.priority"/></th>
                        <th><it:message key="itracker.web.attr.event"/></th>
                     </tr>
                     </thead>
                     <c:if test="${ projectScripts != null && not empty projectScripts }">
                        <c:forEach items="${ projectScripts }" var="script" varStatus="i">
                           <tr>
                              <td>${ script.fieldName }
                              </td>
                              <td>${ script.script.script.name }
                              </td>
                              <td class="priority-${script.script.priority}">${ priorityList[script.script.priority] }
                              </td>
                              <td>${ script.eventName }
                                 <div class="pull-right">
                                    <c:if test="${currUser.superUser}">
                                       <it:formatIconAction action="removeprojectscript"
                                                            paramName="delId"
                                                            paramValue="${ script.script.id }"
                                                            icon="remove" styleClass="deleteButton"
                                                            info="itracker.web.image.delete.projectscript.alt"
                                                            textActionKey="itracker.web.image.delete.texttag"/>
                                    </c:if>
                                 </div>
                              </td>
                           </tr>
                        </c:forEach>
                     </c:if>

                  </table>
               </div>
            </div>
         </div>

         <div class="row">
            <div class="col-sm-12">
               <h4>
                  <div class="pull-right">
                     <it:formatIconAction action="editversionform"
                                          icon="plus" styleClass="fa-lg"
                                          paramName="projectId"
                                          paramValue="${ project.id }"
                                          targetAction="create"
                                          info="itracker.web.image.create.version.alt"
                                          arg0="${ project.name }"
                                          textActionKey="itracker.web.image.create.texttag"/>
                  </div>
                  <it:message key="itracker.web.attr.versions"/>:
               </h4>
            </div>
         </div>

         <div class="row">
            <div class="col-xs-12">
               <div class="table-responsive">
                  <table class="table table-striped">
                     <colgroup>
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                     </colgroup>
                     <thead>

                     <tr>

                        <th><it:message key="itracker.web.attr.number"/></th>
                        <th><it:message key="itracker.web.attr.description"/></th>
                        <th><it:message key="itracker.web.attr.lastmodified"/></th>
                        <th><it:message key="itracker.web.attr.issues"/></th>
                     </tr>
                     </thead>

                     <c:forEach var="version" items="${versions}" varStatus="i">
                        <tr>
                           <td>${version.number}</td>
                           <td>${version.description}</td>
                           <td><it:formatDate date="${version.date}"/></td>
                           <td>
                              <div class="pull-right">
                                 <it:formatIconAction
                                         action="editversionform" paramName="id"
                                         paramValue="${version.id}" targetAction="update"
                                         icon="pencil"
                                         info="itracker.web.image.edit.version.alt"
                                         arg0="${version.number}"
                                         textActionKey="itracker.web.image.edit.texttag"/>
                              </div>
                                 ${version.count}
                           </td>

                        </tr>
                     </c:forEach>

                  </table>
               </div>
            </div>
         </div>


         <div class="row">
            <div class="col-sm-12">
               <h4>
                  <div class="pull-right">
                     <it:formatIconAction
                             action="editcomponentform" paramName="projectId"
                             paramValue="${project.id}" targetAction="create"
                             icon="plus" styleClass="fa-lg"
                             info="itracker.web.image.create.component.alt"
                             arg0="${project.name}"
                             textActionKey="itracker.web.image.create.texttag"/>
                  </div>
                  <it:message key="itracker.web.attr.components"/>:
               </h4>
            </div>
         </div>

         <div class="row">
            <div class="col-xs-12">
               <div class="table-responsive">
                  <table class="table table-striped">
                     <colgroup>
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                        <col class="col-xs-3">
                     </colgroup>
                     <thead>
                     <tr>

                        <th><it:message key="itracker.web.attr.name"/></th>
                        <th><it:message key="itracker.web.attr.description"/></th>
                        <th><it:message key="itracker.web.attr.lastmodified"/></th>
                        <th><it:message key="itracker.web.attr.issues"/></th>
                     </tr>
                     </thead>

                     <c:forEach var="component" items="${components}" varStatus="i">
                        <tr>
                           <td>${component.name}</td>
                           <td>${component.description}</td>
                           <td><it:formatDate date="${component.date}"/></td>
                           <td>
                              <div class="pull-right">
                                 <it:formatIconAction action="editcomponentform" paramName="id"
                                                      paramValue="${component.id}" targetAction="update"
                                                      icon="pencil"
                                                      info="itracker.web.image.edit.component.alt"
                                                      arg0="${component.name}"
                                                      textActionKey="itracker.web.image.edit.texttag"/>
                              </div>
                                 ${component.count}
                           </td>
                        </tr>

                     </c:forEach>
                  </table>

               </div>
            </div>
         </div>
      </c:if>

   </html:form>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>

 	  	 
