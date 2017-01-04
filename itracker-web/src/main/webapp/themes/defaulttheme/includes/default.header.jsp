<%@ include file="/common/taglibs.jsp" %>

<sec:authorize ifAllGranted="ROLE_USER">
   <sec:authentication property="principal.username" var="username"/>
   <sec:authentication property="principal.displayName" var="userDN" />
</sec:authorize>
<header>
   <div class="container-fluid">
      <div class="row">
         <div class="col-xs-4 headerText">
            <c:choose>
               <c:when test="${ not empty siteLogo }">
                  <html:img title="${ siteTitle }" src="${ siteLogo }"/>
               </c:when>
               <c:otherwise>
                  <c:out value="${ siteTitle }"/>
               </c:otherwise>
            </c:choose>

         </div>
         <div class="col-xs-4 headerTextPageTitle">
            <h1>
               <tiles:getAsString name="title"/>
            </h1>

         </div>
         <div class="col-xs-4 headerTextWelcome"><it:message
                 key="itracker.web.header.welcome"/>
            <c:choose>
               <c:when test="${ username != null}">
                  <html:link module="/module-preferences"
                             forward="editpreferences" styleClass="headerTextWelcome"
                             title="${ userDN }">
                     ${ userDN }</html:link>

               </c:when>
               <c:otherwise>
                  <em><it:message key="itracker.web.header.guest"/></em>
               </c:otherwise>
            </c:choose>

         </div>

      </div>

      <div class="row">
         <div class="col-xs-12 text-right headerLinks">

            <c:if test="${username != null}">
               <div class="pull-left">
                  <form name="lookupForm"
                        action="<html:rewrite module="/module-projects" forward="viewissue"/>">
                     <input type="text" name="id" size="5" class="lookupBox"
                            onchange="document.lookupForm.submit();" /></form>
               </div>
            </c:if>
            <c:if test="${username != null}">

               <html:link styleClass="headerLinks"
                          titleKey="itracker.web.header.menu.home.alt" module="/"
                          action="/portalhome">
                  <it:message key="itracker.web.header.menu.home"/>
               </html:link>

               | <html:link linkName="listprojects" styleClass="headerLinks"
                            titleKey="itracker.web.header.menu.projectlist.alt"
                            module="/module-projects" action="/list_projects">
               <it:message key="itracker.web.header.menu.projectlist"/>
            </html:link>
               | <html:link forward="searchissues" module="/module-searchissues"
                            styleClass="headerLinks"
                            titleKey="itracker.web.header.menu.search.alt">
               <it:message key="itracker.web.header.menu.search"/>
            </html:link>
               <sec:authorize ifAllGranted="ISSUE_VIEW_ALL">

                  | <html:link styleClass="headerLinks"
                               titleKey="itracker.web.header.menu.reports.alt"
                               module="/module-reports" action="/list_reports">
                  <it:message key="itracker.web.header.menu.reports"/>
               </html:link>
               </sec:authorize>
               <sec:authorize ifAllGranted="USER_ADMIN">
               <%--<c:if test="${hasPermissionUserAdmin}">--%>
                  |
                  <html:link styleClass="headerLinks"
                             titleKey="itracker.web.header.menu.admin.alt"
                             module="/module-admin" action="/adminhome">
                     <it:message key="itracker.web.header.menu.admin"/>
                  </html:link>
               <%--</c:if>--%>
               </sec:authorize>
               <sec:authorize ifAllGranted="PRODUCT_ADMIN">
                     | <html:link styleClass="headerLinks" linkName="projectadmin"
                                  titleKey="itracker.web.header.menu.projectadmin.alt"
                                  module="/module-admin" action="/listprojectsadmin">
                     <it:message key="itracker.web.header.menu.projectadmin"/>
                  </html:link>
               </sec:authorize>

               | <html:link module="/module-preferences"
                            forward="editpreferences" styleClass="headerLinks"
                            titleKey="itracker.web.header.menu.preferences.alt">
               <it:message key="itracker.web.header.menu.preferences"/>
            </html:link>
               | <html:link forward="help" styleClass="headerLinks"
                            titleKey="itracker.web.header.menu.help.alt" module="/module-help">
               <it:message key="itracker.web.header.menu.help"/>
            </html:link>
               | <html:link linkName="logoff" action="/logoff"
                            styleClass="headerLinks"
                            titleKey="itracker.web.header.menu.logout.alt" module="/">
               <it:message key="itracker.web.header.menu.logout"/>
            </html:link>
            </c:if> <c:if test="${username == null}">

            <c:if test="${fn:length(locales) gt 1}">
               <div class="locales"><c:forEach items="${locales}" varStatus="status" var="locMap">
                    <span><c:if test="${not status.first}"> | </c:if><a href="?loc=${locMap.key}"
                                                                        class="${locMap.key}_loc">${locMap.key}</a></span>
                  <c:forEach items="${locMap.value}" var="loc"> <span> | <a href="?loc=${loc}"
                                                                            class="${loc}_loc">${loc}</a></span></c:forEach>
               </c:forEach></div>
            </c:if>

         </c:if>
         </div>
      </div>
      <div class="row">
         <div class="col-xs-12">
            <tiles:useAttribute name="errorHide" id="errorHide" ignore="true"/>
            <c:if test="${not errorHide}">
               <logic:messagesPresent>
                  <div class="alert alert-danger">
                     <div id="pageErrors" class="text-center">
                        <html:messages id="error">
                           <div><bean:write name="error"/></div>
                        </html:messages>
                     </div>
                  </div>
               </logic:messagesPresent>
            </c:if>
         </div>

      </div>
   </div>
</header>