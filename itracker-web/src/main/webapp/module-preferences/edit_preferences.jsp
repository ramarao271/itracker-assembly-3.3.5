<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE html>
<bean:define id="pageTitleKey" value="itracker.web.editprefs.title"/>
<bean:define id="pageTitleArg" value=""/>

<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
<html:javascript formName="preferencesForm"/>

<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>

<html:form action="/editpreferences"
           onsubmit="return validatePreferencesForm(this);" acceptCharset="UTF-8"
           enctype="multipart/form-data">
   <html:hidden property="action" value="preferences"/>
   <html:hidden property="login" value="${ edituser.login }"/>
   <div class="container-fluid">
      <div class="panel-group">


         <div class="panel panel-default">
            <div class="panel-heading">
               <h3 class="panel-title">
                  <div class="pull-right label label-${edituser.status ==1 ? 'success':'danger'}">${ statusName }</div>
                  <it:message key="itracker.web.attr.login"/>: ${ edituser.login }</h3>
            </div>

            <div class="panel-body">
               <div class="container-fluid">

                  <div class="row">
                     <div class="col-sm-6 col-sm-push-6">
                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.created"/>:</label>
                           <p class="form-control-static text-nowrap"><it:formatDate
                                   date="${ edituser.createDate }"/></p>
                        </div>

                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.lastmodified"/>:</label>
                           <p class="form-control-static text-nowrap"><it:formatDate
                                   date="${edituser.lastModifiedDate}"/></p>
                        </div>
                     </div>
                     <div class="col-sm-6 col-sm-pull-6">
                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.firstname"/><sup>*</sup>:</label>
                           <html:text property="firstName" styleClass="form-control"/>
                        </div>
                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.lastname"/><sup>*</sup>:</label>
                           <c:choose>
                              <c:when test="${allowProfileUpdate}">
                                 <html:text property="lastName" styleClass="form-control"/>
                              </c:when>
                              <c:otherwise>
                                 <p class="form-control-static">${edituser.lastName}</p>
                                 <html:hidden property="lastName" value="${edituser.lastName}"/>
                              </c:otherwise>
                           </c:choose>
                        </div>
                     </div>
                  </div>
                  <div class="row">
                     <div class="col-sm-6">
                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.email"/><sup>*</sup>:</label>
                           <c:choose>
                              <c:when test="${allowProfileUpdate}">
                                 <html:text property="email" styleClass="form-control"/>
                              </c:when>
                              <c:otherwise>
                                 <p class="form-control-static">${edituser.email}</p>
                                 <html:hidden property="email" value="${edituser.email}"/>
                              </c:otherwise>
                           </c:choose>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>

         <c:if test="${allowPasswordUpdate}">
            <div class="panel panel-default ">

               <div class="panel-heading">
                  <h3 class="panel-title"><it:message key="itracker.web.attr.password"/></h3>
               </div>
               <div class="panel-body">
                  <div class="container-fluid">
                     <div class="row">
                        <div class="col-sm-4">
                           <div class="form-group ">
                              <label><it:message key="itracker.web.attr.currpassword"/>:</label>
                              <html:password property="currPassword"
                                             styleClass="form-control" redisplay="false"/>
                           </div>
                        </div>
                        <div class="col-sm-4">
                           <div class="form-group">
                              <label><it:message key="itracker.web.attr.newpassword"/>:</label>
                              <html:password property="password"
                                             styleClass="form-control" redisplay="false"/>
                           </div>
                        </div>
                        <div class="col-sm-4">
                           <div class="form-group">
                              <label><it:message key="itracker.web.attr.confpassword"/>:</label>
                              <html:password property="confPassword"
                                             styleClass="form-control" redisplay="false"/>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </c:if>


         <div class="panel panel-default">
            <div class="panel-heading">
               <h3 class="panel-title"><it:message key="itracker.web.attr.preferences"/></h3>
            </div>
            <div class="container-fluid panel-body">
               <div class="row" id="advanced">
                  <div class="col-sm-6">
                     <div class="form-group">
                        <label><it:message key="itracker.web.attr.locale"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <html:select property="userLocale"
                                           styleClass="form-control">
                                 <html:option value="" styleClass="editColumnText"></html:option>
                                 <c:forEach var="lang" items="${languagesList}">
                                    <html:option value="${lang.key}"
                                                 styleClass="editColumnText">${lang.value}</html:option>
                                 </c:forEach>
                              </html:select>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">${ userLocaleAsString }</p>
                           </c:otherwise>
                        </c:choose>
                     </div>

                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.showclosed"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <p>
                              <div class="radio-inline">
                                 <html:radio property="showClosedOnIssueList" value="true"
                                             styleId="showClosedOnIssueList_1"/>
                                 <label for="showClosedOnIssueList_1"><it:message
                                         key="itracker.web.generic.yes"/></label>
                              </div>
                              <div class="radio-inline">
                                 <html:radio property="showClosedOnIssueList" value="false"
                                             styleId="showClosedOnIssueList_0"/>
                                 <label for="showClosedOnIssueList_0"><it:message
                                         key="itracker.web.generic.no"/></label>
                              </div>
                              </p>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">${showClosedOnIssueListLocalized}</p>
                           </c:otherwise>
                        </c:choose>
                        </p>
                     </div>

                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.remembersearch"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <div>
                                 <div class="radio-inline">
                                    <html:radio property="rememberLastSearch" value="true"
                                                styleId="rememberLastSearch_1"/>
                                    <label for="rememberLastSearch_1"><it:message
                                            key="itracker.web.generic.yes"/></label>
                                 </div>
                                 <div class="radio-inline">
                                    <html:radio property="rememberLastSearch" value="false"
                                                styleId="rememberLastSearch_0"/>
                                    <label for="rememberLastSearch_0"><it:message
                                            key="itracker.web.generic.no"/></label>
                                 </div>
                              </div>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">${getRememberLastSearchLocalized}</p>
                           </c:otherwise>
                        </c:choose>
                     </div>

                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.usetextactions"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <p>
                              <div class="radio-inline">
                                 <html:radio property="useTextActions" value="true" styleId="useTextActions_1"/>
                                 <label for="useTextActions_1"> <it:message key="itracker.web.generic.yes"/></label>
                              </div>
                              <div class="radio-inline">
                                 <html:radio property="useTextActions" value="false" styleId="useTextActions_0"/>
                                 <label for="useTextActions_0"><it:message key="itracker.web.generic.no"/></label>
                              </div>
                              </p>
                           </c:when>
                           <c:otherwise>
                              <td class="form-control-static">${getUseTextActionsLocalized}</td>
                           </c:otherwise>
                        </c:choose>
                     </div>
                  </div>

                  <div class="col-sm-6">
                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.numissuesindex"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <html:text property="numItemsOnIndex"
                                         styleClass="form-control" size="6"/>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">${edituserprefs.numItemsOnIndex}</p>
                           </c:otherwise>
                        </c:choose>
                     </div>
                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.numissuesproject"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <html:text property="numItemsOnIssueList"
                                         styleClass="form-control" size="6"/>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">${edituserprefs.numItemsOnIndex}</p>
                           </c:otherwise>
                        </c:choose>
                     </div>
                  </div>

                  <div class="col-sm-6">
                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.sortcolumn"/>:</label>
                        <c:choose>
                           <c:when test="${ allowPreferenceUpdate }">
                              <html:select
                                      property="sortColumnOnIssueList" styleClass="form-control">
                                 <html:option value="id" styleClass="editColumnText"
                                              key="itracker.web.attr.id"/>
                                 <html:option value="sev" styleClass="editColumnText"
                                              key="itracker.web.attr.severity"/>
                                 <html:option value="stat" styleClass="editColumnText"
                                              key="itracker.web.attr.status"/>
                                 <html:option value="own" styleClass="editColumnText"
                                              key="itracker.web.attr.owner"/>
                                 <html:option value="lm" styleClass="editColumnText"
                                              key="itracker.web.attr.lastmodified"/>
                              </html:select>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">
                                 <c:choose>
                                    <c:when test="${ edituserprefs.sortColumnOnIssueList=='sev' }">
                                       <it:message key="itracker.web.attr.severity"/>
                                    </c:when>
                                    <c:when test="${ edituserprefs.sortColumnOnIssueList=='stat' }">
                                       <it:message key="itracker.web.attr.status"/>
                                    </c:when>
                                    <c:when test="${ edituserprefs.sortColumnOnIssueList=='own' }">
                                       <it:message key="itracker.web.attr.owner"/>
                                    </c:when>
                                    <c:when test="${edituserprefs.sortColumnOnIssueList=='lm'}">
                                       <it:message key="itracker.web.attr.lastmodified"/>
                                    </c:when>
                                    <c:otherwise>
                                       <it:message key="itracker.web.attr.id"/>
                                    </c:otherwise>
                                 </c:choose>
                              </p>
                           </c:otherwise>
                        </c:choose>
                     </div>
                     <div class="form-group">
                        <label><it:message key="itracker.web.editprefs.hideindex"/>:</label>
                        <c:choose>
                           <c:when test="${allowPreferenceUpdate}">
                              <div>
                                 <div class="checkbox-inline"><html:multibox
                                         property="hiddenIndexSections" styleId="hiddenIndexSections_0"
                                         value="${ PREF_HIDE_ASSIGNED }"/> <label
                                         for="hiddenIndexSections_0"><it:message
                                         key="itracker.web.editprefs.section.assigned"/>
                                 </label></div>
                                 <div class="checkbox-inline"><html:multibox
                                         property="hiddenIndexSections" styleId="hiddenIndexSections_1"
                                         value="${ PREF_HIDE_UNASSIGNED }"/> <label
                                         for="hiddenIndexSections_1"><it:message
                                         key="itracker.web.editprefs.section.unassigned"/>
                                 </label></div>
                              </div>
                              <div>
                                 <div class="checkbox-inline"><html:multibox
                                         property="hiddenIndexSections" styleId="hiddenIndexSections_2"
                                         value="${ PREF_HIDE_CREATED }"/> <label for="hiddenIndexSections_2"><it:message
                                         key="itracker.web.editprefs.section.created"/>
                                 </label></div>
                                 <div class="checkbox-inline"><html:multibox
                                         property="hiddenIndexSections" styleId="hiddenIndexSections_3"
                                         value="${ PREF_HIDE_WATCHED }"/> <label for="hiddenIndexSections_3"><it:message
                                         key="itracker.web.editprefs.section.watched"/>
                                 </label></div>
                              </div>
                           </c:when>
                           <c:otherwise>
                              <p class="form-control-static">${hiddenSectionsString}</p>
                           </c:otherwise>
                        </c:choose>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>

      <div class="row">
         <div class="col-sm-12">
            <c:if test="${allowProfileUpdate || allowPasswordUpdate || allowPreferenceUpdate}">
               <html:submit styleClass="btn btn-primary btn-block"
                            altKey="itracker.web.button.update.alt"
                            titleKey="itracker.web.button.update.alt">
                  <it:message key="itracker.web.button.update"/>
               </html:submit>
            </c:if>
         </div>
      </div>

   </div>


</html:form>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
