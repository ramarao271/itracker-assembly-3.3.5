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

   <div class="row">
      <div class="col-sm-6">
         <section id="statuses" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link action="editconfigurationform" targetAction="createstatus"
                           styleClass="btn btn-link"
                           titleKey="itracker.web.admin.listconfiguration.status.create.alt"><it:message
                          key="itracker.web.admin.listconfiguration.status.create"/></it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.attr.statuses"/>
               </h3>
            </div>
            <div class="panel-body table-responsive">
               <table class="table table-striped table-condensed">
                  <c:forEach items="${ statuses }" var="status" varStatus="i">
                     <tr>
                        <td class="text-nowrap">
                           <div class="pull-right btn-group btn-group-sm">
                              <it:link action="editconfigurationform" targetAction="update" paramName="id"
                                       paramValue="${ status.id }" styleClass="btn btn-sm btn-link "
                                       titleKey="itracker.web.admin.listconfiguration.status.update.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.status.update"/></it:link>
                              <it:link action="removeconfiguration" targetAction="delete" paramName="id"
                                       paramValue="${ status.id }" styleClass="btn btn-sm btn-link deleteButton"
                                       titleKey="itracker.web.admin.listconfiguration.status.delete.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.status.delete"/></it:link>
                           </div>
                              ${ it:getStatusName(status.value, pageLocale) }
                           (${ status.value })
                        </td>
                     </tr>
                  </c:forEach>
               </table>
            </div>
         </section>
      </div>

      <div class="col-sm-6">
         <section id="severities" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link action="editconfigurationform" targetAction="createseverity" styleClass="btn btn-link"
                           titleKey="itracker.web.admin.listconfiguration.severity.create.alt"><it:message
                          key="itracker.web.admin.listconfiguration.severity.create"/></it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.attr.severities"/>
               </h3>
            </div>
            <div class="panel-body table-responsive">
               <table class="table table-striped table-condensed">

                  <c:forEach items="${ severities }" var="severity" varStatus="i" step="1">
                     <tr>
                        <td class="text-nowrap">
                           <div class="pull-right btn-group btn-group-sm">
                              <c:if test="${ i.index != 0 }">
                                 <it:link action="orderconfiguration" targetAction="up" paramName="id"
                                          paramValue="${ severity.id }" styleClass="btn btn-sm btn-link"
                                          titleKey="itracker.web.admin.listconfiguration.severity.orderup.alt"><it:message
                                         key="itracker.web.admin.listconfiguration.severity.orderup"/></it:link>
                              </c:if>
                              <c:if test="${ i.index != ((fn:length(severities)) - 1) }">
                                 <it:link action="orderconfiguration" targetAction="down" paramName="id"
                                          paramValue="${ severity.id }" styleClass="btn btn-sm btn-link"
                                          titleKey="itracker.web.admin.listconfiguration.severity.orderdown.alt"><it:message
                                         key="itracker.web.admin.listconfiguration.severity.orderdown"/></it:link>
                              </c:if>

                              <it:link action="editconfigurationform" targetAction="update" paramName="id"
                                       paramValue="${ severity.id }" styleClass="btn btn-sm btn-link"
                                       titleKey="itracker.web.admin.listconfiguration.severity.update.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.severity.update"/></it:link>
                              <it:link action="removeconfiguration" targetAction="delete" paramName="id"
                                       paramValue="${ severity.id }" styleClass="btn btn-sm btn-link deleteButton"
                                       titleKey="itracker.web.admin.listconfiguration.severity.delete.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.severity.delete"/></it:link>
                           </div>
                              ${ it:getSeverityName(severity.value, pageLocale) }
                           (${ severity.value })
                        </td>
                     </tr>
                  </c:forEach>
               </table>
            </div>
         </section>
      </div>
   </div>
   <div class="row">
      <div class="col-sm-6">

         <section id="resolutions" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link action="editconfigurationform" targetAction="createresolution" styleClass="btn btn-link"
                           titleKey="itracker.web.admin.listconfiguration.resolution.create.alt"><it:message
                          key="itracker.web.admin.listconfiguration.resolution.create"/></it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.attr.resolutions"/>
               </h3>
            </div>
            <div class="panel-body table-responsive">
               <table class="table table-striped table-condensed">
                  <c:forEach items="${ resolutions }" var="resolution" varStatus="i" step="1">
                     <tr>
                        <td class="text-nowrap">
                           <div class="pull-right btn-group btn-group-sm">
                              <c:if test="${ i.index != 0 }">
                                 <it:link action="orderconfiguration" targetAction="up" paramName="id"
                                          paramValue="${ resolution.id }" styleClass="btn btn-sm btn-link"
                                          titleKey="itracker.web.admin.listconfiguration.resolution.orderup.alt"><it:message
                                         key="itracker.web.admin.listconfiguration.resolution.orderup"/></it:link>
                              </c:if>
                              <c:if test="${ i.index != ((fn:length(resolutions)) - 1) }">
                                 <it:link action="orderconfiguration" targetAction="down" paramName="id"
                                          paramValue="${ resolution.id }" styleClass="btn btn-sm btn-link"
                                          titleKey="itracker.web.admin.listconfiguration.resolution.orderdown.alt"><it:message
                                         key="itracker.web.admin.listconfiguration.resolution.orderdown"/></it:link>
                              </c:if>

                              <it:link action="editconfigurationform" targetAction="update" paramName="id"
                                       paramValue="${ resolution.id }" styleClass="btn btn-sm btn-link"
                                       titleKey="itracker.web.admin.listconfiguration.resolution.update.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.resolution.update"/></it:link>
                              <it:link action="removeconfiguration" targetAction="delete" paramName="id"
                                       paramValue="${ resolution.id }" styleClass="btn btn-sm btn-link deleteButton"
                                       titleKey="itracker.web.admin.listconfiguration.resolution.delete.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.resolution.delete"/></it:link>
                           </div>
                              ${ it:getResolutionName(resolution.value, pageLocale) }
                           (${ resolution.value })
                        </td>
                     </tr>
                  </c:forEach>
               </table>
            </div>
         </section>
      </div>

      <div class="col-sm-6">
         <section id="customfields" class="panel panel-default">
            <div class="panel-heading">
               <div class="btn-group pull-right">
                  <it:link action="editcustomfieldform"
                           targetAction="create" styleClass="btn btn-link"
                           titleKey="itracker.web.admin.listconfiguration.customfield.create.alt"><it:message
                          key="itracker.web.admin.listconfiguration.customfield.create"/></it:link>
               </div>
               <h3 class="panel-title">
                  <it:message key="itracker.web.attr.customfields"/>
               </h3>
            </div>
            <div class="panel-body table-responsive">
               <table class="table table-striped table-condensed">
                  <c:forEach items="${ customfields }" var="customField" varStatus="i">
                     <tr>
                        <td class="text-nowrap">
                           <div class="pull-right btn-group btn-group-sm">
                              <it:link action="editcustomfieldform" targetAction="update" paramName="id"
                                       paramValue="${ customField.id }" styleClass="btn btn-sm btn-link"
                                       titleKey="itracker.web.admin.listconfiguration.customfield.update.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.customfield.update"/></it:link>
                              <it:link action="removecustomfield" targetAction="delete" paramName="id"
                                       paramValue="${ customField.id }" styleClass="btn btn-sm btn-link deleteButton"
                                       titleKey="itracker.web.admin.listconfiguration.customfield.delete.alt"><it:message
                                      key="itracker.web.admin.listconfiguration.customfield.delete"/></it:link>
                           </div>
                              ${ it:getCustomFieldName(customField.id, pageLocale) }
                           (<it:message key="itracker.web.attr.id"/>: ${ customField.id },
                           <it:message
                                   key="itracker.web.attr.fieldtype"/>: ${ it:getCustomFieldTypeString(customField.fieldType.code, pageLocale) }
                           )
                        </td>
                     </tr>
                  </c:forEach>
               </table>
            </div>
         </section>
      </div>
   </div>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
