<%@ include file="/common/taglibs.jsp" %>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<script type="text/javascript">
   function validateForm() {
      var userForm = document.forms['userForm'];

      var password = userForm.password.value;
      var confPassword = userForm.confPassword.value;

      if (password != confPassword) {
         alert("Password does not match confirmation password.");
         return false;
      }
   }
</script>

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
   <html:form action="/edituser" method="post" acceptCharset="UTF-8" enctype="multipart/form-data"
              onsubmit="return validateForm()">
      <html:hidden property="action"/>
      <html:hidden property="id"/>


      <div class="panel-group">
         <div class="panel panel-default">
            <div class="panel-heading">
               <h3 class="panel-title">
                  <div class="pull-right">
                     <div class="label label-${edituser.status ==1 ? 'success':'danger'}">${ userStatus }</div>

                     <c:choose>
                        <c:when test="${ edituser.status != 1 }">

                           <it:formatIconAction action="unlockuser" paramName="id" paramValue="${edituser.id}"
                                                icon="key" iconClass="text-success"
                                                info="itracker.web.image.unlock.user.alt" arg0="${edituser.login}"
                                                textActionKey="itracker.web.image.unlock.texttag">
                           </it:formatIconAction>
                        </c:when>
                        <c:otherwise>
                           <it:formatIconAction action="lockuser" paramName="id" paramValue="${edituser.id}"
                                                icon="lock"
                                                info="itracker.web.image.lock.user.alt" arg0="${edituser.login}"
                                                textActionKey="itracker.web.image.lock.texttag"/>
                        </c:otherwise>
                     </c:choose>
                  </div>
                  <it:message key="itracker.web.attr.login"/>:
                  <c:choose>
                     <c:when test="${isUpdate && !allowProfileUpdate}">
                        ${edituser.login}<html:hidden property="login"/>
                     </c:when>
                     <c:otherwise>
                        <html:text property="login" styleClass="editColumnText"/>
                     </c:otherwise>
                  </c:choose></h3>
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
                           <c:choose>
                              <c:when test="${allowProfileUpdate || !isUpdate}">
                                 <html:text property="firstName" styleClass="form-control"/>
                              </c:when>
                              <c:otherwise>
                                 <p class="form-control-static">${edituser.lastName}</p>
                                 <html:hidden property="firstName" value="${edituser.firstName}"/>
                              </c:otherwise>
                           </c:choose>
                        </div>
                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.lastname"/><sup>*</sup>:</label>
                           <c:choose>
                              <c:when test="${allowProfileUpdate || !isUpdate}">
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
                              <c:when test="${allowProfileUpdate || !isUpdate}">
                                 <html:text property="email" styleClass="form-control"/>
                              </c:when>
                              <c:otherwise>
                                 <p class="form-control-static">${edituser.email}</p>
                                 <html:hidden property="email" value="${edituser.email}"/>
                              </c:otherwise>
                           </c:choose>
                        </div>
                     </div>
                     <div class="col-sm-6">
                        <div class="form-group">
                           <label><it:message key="itracker.web.attr.superuser"/>:</label>
                           <c:choose>
                              <c:when test="${allowProfileUpdate || !isUpdate}">
                                 <div>
                                    <div class="radio-inline">
                                       <html:radio property="superUser" value="true" styleId="superUser_1"/>
                                       <label for="superUser_1"><it:message key="itracker.web.generic.yes"/></label>
                                    </div>
                                    <div class="radio-inline">
                                       <html:radio property="superUser" value="false" styleId="superUser_0"/>
                                       <label for="superUser_0"><it:message key="itracker.web.generic.no"/></label>
                                    </div>
                                 </div>
                              </c:when>
                              <c:otherwise>
                                 <p class="form-control-static">
                                    <html:hidden property="superUser"/>
                                    <c:choose>
                                       <c:when test="${edituser.superUser}">
                                          <it:message key="itracker.web.generic.yes"/>
                                       </c:when>
                                       <c:otherwise>
                                          <it:message key="itracker.web.generic.no"/>
                                       </c:otherwise>

                                    </c:choose></p>
                              </c:otherwise>
                           </c:choose>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>

         <c:if test="${allowPasswordUpdate || !isUpdate}">
            <div class="panel panel-default ">

               <div class="panel-heading">
                  <h3 class="panel-title"><it:message key="itracker.web.attr.password"/></h3>
               </div>
               <div class="panel-body">
                  <div class="container-fluid">
                     <div class="row">
                        <div class="col-sm-6">
                           <div class="form-group">
                              <label><it:message key="itracker.web.attr.newpassword"/>:</label>
                              <html:password property="password"
                                             styleClass="form-control" redisplay="false"/>
                           </div>
                        </div>
                        <div class="col-sm-6">
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

      </div>

      <div class="row">
         <div class="col-xs-12">
            <c:choose>
               <c:when test="${isUpdate}">
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

      <div class="row">
         <div class="col-xs-12">
            <h4><it:message key="itracker.web.attr.permissions"/>:</h4>
         </div>
      </div>


      <c:forEach items="${projects}" var="project" varStatus="i" step="1">
         <div class="panel panel-default">
            <div class="panel-heading">
               <h5>
                  <div class="checkbox-inline">
                     <input title="toggle all" type="checkbox" class="pull-left"
                            onchange="toggleProjectPermissionsChecked(this)"
                            name="#${ project.id }" id="All#${ project.id }"/>
                     <label for="All#${ project.id }"><it:message
                             key="itracker.web.attr.project"/> ${ project.name }</label>

                  </div>
               </h5>
            </div>
            <c:set var="projectPermissions"
                   value="${ edituserperms[project.id] }"/>
            <c:set var="currentPermissionDate" value="${ null }"/>

            <div class="panel-body container-fluid">

               <c:set var="titleLastMod"><it:message key="itracker.web.attr.lastmodified"/></c:set>
                  <%-- iterate over all permissions --%>
               <c:forEach items="${ permissionNames }" var="permissionName" varStatus="j">
                  <div class="col-sm-6">
                     <c:set var="keyName"
                            value="permissions(${ permissionName.value }#${ project.id })"/>

                     <c:set var="permission" value="${ projectPermissions[ permissionName.value ] }"/>

                     <div class="checkbox-inline">
                        <c:choose>
                           <c:when test="${isUpdate && !allowPermissionUpdate}">
                              <c:set var="checkIcon" value="${null!=permission ? 'check-square-o': 'square-o'}"/>
                              <i class="fa fa-${checkIcon}" aria-hidden="true"
                                 id="${ permissionName.value }#${ project.id }"></i>
                              <html:hidden property="${ keyName }"/>
                           </c:when>
                           <c:otherwise>
                              <html:checkbox property="${ keyName }"
                                             styleId="${ permissionName.value }#${ project.id }"/>
                           </c:otherwise>
                        </c:choose>
                        <label for="${ permissionName.value }#${ project.id }">${ permissionName.name }</label>
                     </div>
                        <%--<c:if test="${ permission != null }">--%>
                        <%--<em class="text-nowrap" title="${titleLastMod}"><it:formatDate--%>
                        <%--date="${ permission.lastModifiedDate }"/></em>--%>
                        <%--</c:if>--%>
                  </div>
               </c:forEach>
            </div>
         </div>
      </c:forEach>

      <div class="row">
         <div class="col-xs-12">
            <c:choose>
               <c:when test="${isUpdate}">
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
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/>
</body>
</html>
