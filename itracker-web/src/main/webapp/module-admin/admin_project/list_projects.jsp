<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.listprojects.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="maincontent container-fluid">
   <div class="row">
      <div class="col-xs-12">
         <c:if test="${ isSuperUser }">
            <div class="pull-right">

                  <c:choose>
                  <c:when test="${ showAll }">
                  <it:formatIconAction icon="stack" styleClass="unlock fa fa-stack" action="/listprojectsadmin?showAll=false"
                                       info="itracker.web.listprojects.locked.hide.alt"
                                       textActionKey="itracker.web.listprojects.locked.hide.texttag">
                        <i class="fa fa-ban fa-stack-2x" aria-hidden="true"></i>
                        <i class="fa fa-lock fa-stack-1x " aria-hidden="true"></i>
                  </it:formatIconAction>
                  </c:when>
                  <c:otherwise>
                  <it:formatIconAction icon="stack" styleClass="lock fa fa-stack" action="/listprojectsadmin?showAll=true"
                                       info="itracker.web.listprojects.locked.show.alt"
                                       textActionKey="itracker.web.listprojects.locked.show.texttag">
                     <i class="fa fa-circle fa-stack-2x" aria-hidden="true"></i>
                     <i class="fa fa-lock fa-stack-1x fa-inverse " aria-hidden="true"></i>
                  </it:formatIconAction>
                  </c:otherwise>
                  </c:choose>

                     <it:formatIconAction action="editprojectform" targetAction="create" icon="plus" iconClass="fa-2x"
                                          info="itracker.web.image.create.project.alt"
                                          textActionKey="itracker.web.image.create.texttag"/>
                     <it:formatIconAction forward="listattachments" icon="paperclip" iconClass="fa-2x"
                                          info="itracker.web.image.view.attachments.alt"
                                          textActionKey="itracker.web.image.view.texttag"/>
            </div>
         </c:if>
         <h4> <it:message key="itracker.web.attr.projects"/>:</h4>

      </div>

   </div>

   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">
            <c:choose>
               <c:when test="${not empty projects}">
                  <table class="table table-striped">
                     <thead>
                     <tr>
                        <th></th>
                        <th><it:message key="itracker.web.attr.name"/></th>
                        <th><it:message key="itracker.web.attr.description"/></th>
                        <th><it:message key="itracker.web.attr.created"/></th>
                        <th class="text-right"><it:message key="itracker.web.attr.lastmodified"/></th>
                           <%-- <td align="left"><it:message key="itracker.web.attr.issues"/></td> --%>
                     </tr>
                     </thead>
                     <tbody>
                     <c:forEach var="project" varStatus="i" items="${ projects }" step="1">

                        <tr class="${ project.active ? '':  project.viewable ? 'warning' : 'danger' }">

                           <td>
                              <it:formatIconAction action="editprojectform"
                                                   paramName="id" paramValue="${ project.id }"
                                                   targetAction="update" icon="edit" iconClass="fa-lg"
                                                   info="itracker.web.image.edit.project.alt"
                                                   arg0="${ project.name }"
                                                   textActionKey="itracker.web.image.edit.texttag"/>
                           </td>
                           <td>${ project.name }</td>
                           <td>${ project.description }</td>
                           <td><it:formatDate date="${ project.createDate }"/></td>
                           <td class="text-right"><it:formatDate date="${ project.lastModifiedDate }"/></td>
                              <%-- <td align="left">${ project.totalNumberIssues }</td> --%>
                        </tr>

                     </c:forEach>
                     </tbody>
                     <tfoot>
                     <tr>
                        <td colspan="5" class="tableNote"><it:message key="itracker.web.admin.listprojects.note"/></td>
                     </tr>
                     </tfoot>
                  </table>
               </c:when>
               <c:otherwise>
                  <div class="alert alert-info">
                     <strong><it:message key="itracker.web.error.noprojects"/></strong>
                  </div>
               </c:otherwise>
            </c:choose>
         </div>
      </div>
   </div>

</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
