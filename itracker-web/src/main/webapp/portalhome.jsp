<%@ include file="/common/taglibs.jsp" %>

<jsp:useBean id="UserUtilities_PREF_HIDE_ASSIGNED" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="UserUtilities_PREF_HIDE_UNASSIGNED" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="UserUtilities_PREF_HIDE_CREATED" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="UserUtilities_PREF_HIDE_WATCHED" scope="request" type="java.lang.Boolean" />
<jsp:useBean id="ownedIssues" scope="request" type="java.util.Collection" />
<jsp:useBean id="unassignedIssues" scope="request" type="java.util.Collection" />
<jsp:useBean id="createdIssues" scope="request" type="java.util.Collection" />
<jsp:useBean id="watchedIssues" scope="request" type="java.util.Collection" />
<jsp:useBean id="userPrefs" scope="request" type="org.itracker.model.UserPreferences" />
<jsp:useBean id="currUser" scope="session" type="org.itracker.model.User" />
<jsp:useBean id="allSections" scope="session" type="java.lang.Boolean" />
<jsp:useBean id="showAll" scope="session" type="java.lang.Boolean" />

<bean:define toScope="request" id="pageTitleKey" value="itracker.web.index.title"/>
<bean:define toScope="request" id="pageTitleArg" value=""/>
<!-- assigned issues -->

<div class="container-fluid maincontent">
   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">

            <table class="portalhomeMain shadeList table table-striped table-hover">
               <colgroup>
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-1">
                  <col class="col-xs-1">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
                  <col class="col-xs-2">
               </colgroup>
               <c:if test="${(! UserUtilities_PREF_HIDE_ASSIGNED) || allSections}">
                  <thead>
                  <tr id="ownedIssues">
                     <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.index.assigned"/>:</td>
                  </tr>
                  </thead>
                  <c:choose>
                     <c:when test="${empty ownedIssues}">
                        <tr>
                           <td colspan="7" class="tableNote">
                              <it:message key="itracker.web.error.noissues"/>
                           </td>
                        </tr>
                     </c:when>
                     <c:otherwise>
                        <thead>
                        <tr class="listHeading">
                           <th class="text-right"><html:link action="/portalhome?sortKey=id"><it:message
                                   key="itracker.web.attr.id"/></html:link></th>
                           <th><it:message key="itracker.web.attr.project"/></th>
                           <th><html:link action="/portalhome?sortKey=stat"><it:message
                                   key="itracker.web.attr.status"/></html:link></th>
                           <th><html:link action="/portalhome?sortKey=sev"><it:message
                                   key="itracker.web.attr.severity"/></html:link></th>
                           <th><it:message key="itracker.web.attr.description"/></th>
                           <th><html:link action="/portalhome?sortKey=own"><it:message
                                   key="itracker.web.attr.owner"/></html:link></th>
                           <th class="text-right"><html:link action="/portalhome?sortKey=lm"><it:message
                                   key="itracker.web.attr.lastmodified"/></html:link></th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach items="${ownedIssues}" var="ownedIssue" step="1" varStatus="i">
                           <c:choose>
                              <c:when test="${showAll || (i.count <=userPrefs.numItemsOnIndex)}">
                                 <tr>

                                    <td class="text-right">
                                       <div class="pull-left">
                                          <c:if test="${ownedIssue.userCanViewIssue}">
                                             <it:formatIconAction
                                                     forward="viewissue" module="/module-projects"
                                                     paramName="id"
                                                     paramValue="${ownedIssue.issue.id}"
                                                     caller="index"
                                                     icon="tasks" styleClass="fa-lg"
                                                     info="itracker.web.image.view.issue.alt"
                                                     arg0="${ownedIssue.issue.id}"
                                                     textActionKey="itracker.web.image.view.texttag"/>
                                          </c:if>
                                          <c:if test="${ownedIssue.userCanEdit}">
                                             <it:formatIconAction
                                                     forward="editissue" module="/module-projects"
                                                     paramName="id"
                                                     paramValue="${ownedIssue.issue.id}"
                                                     caller="index"
                                                     icon="edit" styleClass="fa-lg"
                                                     info="itracker.web.image.edit.issue.alt"
                                                     arg0="${ownedIssue.issue.id}"
                                                     textActionKey="itracker.web.image.edit.texttag"/>
                                          </c:if></div>
                                          ${ownedIssue.issue.id}</td>
                                    <td><c:out value="${ownedIssue.issue.project.name}"/></td>
                                    <td>${ownedIssue.statusLocalizedString}</td>
                                    <td>${ownedIssue.severityLocalizedString}</td>
                                    <td><it:formatDescription><c:out
                                            value="${ownedIssue.issue.description}"/></it:formatDescription></td>
                                    <td>${ownedIssue.issue.owner.firstName} ${ownedIssue.issue.owner.lastName}</td>
                                    <td class="text-right text-nowrap"><it:formatDate
                                            date="${ownedIssue.issue.lastModifiedDate}"/></td>
                                 </tr>
                              </c:when>
                              <c:otherwise>

                                 <c:if test="${i.count == userPrefs.numItemsOnIndex + 1}">
                                    <tr>
                                       <td class="moreissues tableNote" colspan="7"><html:link module="/"
                                                                                               action="/portalhome?showAll=true"><it:message
                                               key="itracker.web.index.moreissues"/></html:link></td>
                                    </tr>
                                 </c:if>
                              </c:otherwise>
                           </c:choose>
                        </c:forEach>
                        </tbody>
                     </c:otherwise></c:choose>
                  <%-- END c:if UserUtilities_PREF_HIDE_ASSIGNED --%>
               </c:if>


               <!-- unassigned issues -->


               <c:if test="${(! UserUtilities_PREF_HIDE_UNASSIGNED)  || allSections}">
                  <thead>
                  <tr id="unassignedIssues">
                     <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.index.unassigned"/>:</td>
                  </tr>
                  </thead>
                  <c:choose>
                     <c:when test="${empty unassignedIssues}">
                        <tr>
                           <td colspan="7" class="tableNote">
                              <it:message key="itracker.web.error.noissues"/>
                           </td>
                        </tr>
                     </c:when>
                     <c:otherwise>
                        <thead>
                        <tr class="listHeading">
                           <th class="text-right"><html:link action="/portalhome?sortKey=id"><it:message
                                   key="itracker.web.attr.id"/></html:link></th>
                           <th><it:message key="itracker.web.attr.project"/></th>
                           <th><html:link action="/portalhome?sortKey=stat"><it:message
                                   key="itracker.web.attr.status"/></html:link></th>
                           <th><html:link action="/portalhome?sortKey=sev"><it:message
                                   key="itracker.web.attr.severity"/></html:link></th>
                           <th><it:message key="itracker.web.attr.description"/></th>
                           <th><html:link action="/portalhome?sortKey=own"><it:message
                                   key="itracker.web.attr.owner"/></html:link></th>
                           <th class="text-right"><html:link action="/portalhome?sortKey=lm"><it:message
                                   key="itracker.web.attr.lastmodified"/></html:link></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${unassignedIssues}" var="unassignedIssue" step="1" varStatus="i">
                           <c:set var="iCount" value="${i.index +1}"/>
                           <c:choose>
                              <c:when test="${showAll || (iCount <=userPrefs.numItemsOnIndex)}">
                                 <tr id="unassignedIssue.${iCount}">
                                    <td class="text-right">
                                       <div class="pull-left">
                                          <c:if test="${not unassignedIssue.userHasIssueNotification}">
                            <span class="HTTP_POST">
                            <it:formatIconAction forward="watchissue" paramName="id"
                                                 paramValue="${unassignedIssue.issue.id}" caller="index"
                                                 icon="bell" styleClass="fa-lg"
                                                 info="itracker.web.image.watch.issue.alt"
                                                 arg0="${unassignedIssue.issue.id}"
                                                 textActionKey="itracker.web.image.watch.texttag"/>
                            </span>
                                          </c:if>
                                          <it:formatIconAction
                                                  forward="viewissue" module="/module-projects"
                                                  paramName="id" paramValue="${unassignedIssue.issue.id}"
                                                  caller="index"
                                                  icon="tasks" styleClass="fa-lg"
                                                  info="itracker.web.image.view.issue.alt"
                                                  arg0="${unassignedIssue.issue.id}"
                                                  textActionKey="itracker.web.image.view.texttag"/>
                                          <c:if test="${unassignedIssue.userCanEdit}">
                                             <it:formatIconAction
                                                     forward="editissue" module="/module-projects"
                                                     paramName="id" paramValue="${unassignedIssue.issue.id}"
                                                     caller="index"
                                                     icon="edit" styleClass="fa-lg"
                                                     info="itracker.web.image.edit.issue.alt"
                                                     arg0="${unassignedIssue.issue.id}"
                                                     textActionKey="itracker.web.image.edit.texttag"/>
                                          </c:if>
                                       </div>
                                          ${unassignedIssue.issue.id}</td>
                                    <td>${unassignedIssue.issue.project.name}</td>
                                    <td><c:out value="${unassignedIssue.statusLocalizedString}"/></td>
                                    <td><c:out
                                            value="${unassignedIssue.severityLocalizedString}"/></td>
                                    <td>
                                       <it:formatDescription>${unassignedIssue.issue.description}</it:formatDescription></td>
                                    <c:choose>
                                       <c:when
                                               test="${unassignedIssue.userHasPermission_PERMISSION_ASSIGN_OTHERS}">

                                          <td>
                                             <html:form action="/assignissue" styleClass="assignIssueForm">
                                                <html:hidden property="issueId"
                                                             value="${unassignedIssue.issue.id}"/>
                                                <html:hidden property="projectId"
                                                             value="${unassignedIssue.issue.project.id}"/>

                                                <html:select property="userId"
                                                             styleClass="form-control input-sm"
                                                             onchange="this.form.submit();">
                                                   <c:choose>
                                                      <c:when test="${unassignedIssue.unassigned}">
                                                         <option value="-1"><it:message
                                                                 key="itracker.web.generic.unassigned"/></option>
                                                      </c:when>
                                                      <c:otherwise>
                                                         <option value="${unassignedIssue.issue.owner.id}"><c:out
                                                                 value="${unassignedIssue.issue.owner.firstName}"/>
                                                            <c:out
                                                                    value="${unassignedIssue.issue.owner.lastName}"/></option>
                                                      </c:otherwise>
                                                   </c:choose>
                                                   <c:forEach items="${unassignedIssue.possibleOwners}"
                                                              var="possibleIssueOwner" varStatus="k">
                                                      <c:if test="${possibleIssueOwner.lastName != null}">
                                                         <option value="${possibleIssueOwner.id}"
                                                                 <c:choose>
                                                                    <c:when test="${unassignedIssue.issue.owner.id == possibleIssueOwner.id}">
                                                                       selected
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                    </c:otherwise>
                                                                 </c:choose>>${possibleIssueOwner.firstName}
                                                               ${possibleIssueOwner.lastName}</option>
                                                      </c:if>
                                                   </c:forEach>
                                                </html:select>
                                             </html:form>
                                          </td>
                                       </c:when>
                                       <c:otherwise>
                                          <c:choose>
                                             <c:when test="${unassignedIssue.userHasPermission_PERMISSION_ASSIGN_SELF}">
                                                <td>
                                                   <html:form action="/assignissue" styleClass="assignIssueForm">
                                                      <html:hidden property="issueId"
                                                                   value="${unassignedIssue.issue.id}"/>
                                                      <html:hidden property="projectId"
                                                                   value="${unassignedIssue.issue.project.id}"/>

                                                      <html:select property="userId"
                                                                   onchange="this.form.submit();">
                                                         <c:choose>
                                                            <c:when test="${unassignedIssue.unassigned}">
                                                               <option value="-1">
                                                                  <it:message key="itracker.web.generic.unassigned"/>
                                                               </option>
                                                            </c:when>
                                                            <c:otherwise>
                                                               <option value="${unassignedIssue.issue.owner.id}"><c:out
                                                                       value="${unassignedIssue.issue.owner.firstName}"/>
                                                                  <c:out
                                                                          value="${unassignedIssue.issue.owner.lastName}"/>Test2
                                                               </option>
                                                            </c:otherwise>
                                                         </c:choose>
                                                         <option value="${currUser.id}"
                                                                 <c:if test="${unassignedIssue.owner.id==currUser.id}">selected</c:if>>
                                                               ${currUser.firstName} ${currUser.lastName}</option>

                                                      </html:select>
                                                   </html:form>
                                                </td>
                                             </c:when>
                                             <c:otherwise>
                                                <td><it:formatIssueOwner
                                                        issue="${unassignedIssue.issue}" format="short"/></td>
                                             </c:otherwise>
                                          </c:choose>
                                       </c:otherwise>
                                    </c:choose>
                                    <td class="text-right text-nowrap"><it:formatDate
                                            date="${unassignedIssue.issue.lastModifiedDate}"/></td>
                                 </tr>
                              </c:when>
                              <c:otherwise>

                                 <c:if test="${iCount == userPrefs.numItemsOnIndex + 1}">
                                    <tr>
                                       <td class="moreissues tableNote" colspan="7"><html:link anchor="unassignedIssues"
                                                                                               module="/"
                                                                                               action="/portalhome?showAll=true"><it:message
                                               key="itracker.web.index.moreissues"/></html:link></td>
                                    </tr>
                                 </c:if>
                              </c:otherwise>
                           </c:choose>
                        </c:forEach>
                        </tbody>
                     </c:otherwise></c:choose>
               </c:if>


               <!-- created issues -->

               <c:if test="${(! UserUtilities_PREF_HIDE_CREATED) || allSections}">

                  <thead>
                  <tr id="createdIssues">
                     <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.index.created"/>:</td>
                  </tr>
                  </thead>
                  <c:choose>
                     <c:when test="${empty createdIssues}">
                        <tr>
                           <td colspan="7" class="tableNote">
                              <it:message key="itracker.web.error.noissues"/>
                           </td>
                        </tr>
                     </c:when>
                     <c:otherwise>

                        <thead>
                        <tr class="listHeading">
                           <th class="text-right"><html:link action="/portalhome?sortKey=id"><it:message
                                   key="itracker.web.attr.id"/></html:link></th>
                           <th><it:message key="itracker.web.attr.project"/></th>
                           <th><html:link action="/portalhome?sortKey=stat"><it:message
                                   key="itracker.web.attr.status"/></html:link></th>
                           <th><html:link action="/portalhome?sortKey=sev"><it:message
                                   key="itracker.web.attr.severity"/></html:link></th>
                           <th><it:message key="itracker.web.attr.description"/></th>
                           <th><html:link action="/portalhome?sortKey=own"><it:message
                                   key="itracker.web.attr.owner"/></html:link></th>
                           <th class="text-right"><html:link action="/portalhome?sortKey=lm"><it:message
                                   key="itracker.web.attr.lastmodified"/></html:link></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:if test="${empty createdIssues}">
                           <tr>
                              <td colspan="7" class="tableNote">
                                 <it:message key="itracker.web.error.noissues"/>
                              </td>
                           </tr>
                        </c:if>
                        <c:forEach items="${createdIssues}" var="createdIssue" step="1"
                                   varStatus="i">

                           <c:choose>
                              <c:when test="${showAll || (i.count <=userPrefs.numItemsOnIndex)}">
                                 <tr id="createdIssue.${i.count}">
                                    <td class="text-right">
                                       <div class="pull-left">
                                          <c:if test="${createdIssue.userCanViewIssue}">
                                             <it:formatIconAction
                                                     forward="viewissue" module="/module-projects"
                                                     paramName="id"
                                                     paramValue="${createdIssue.issue.id}"
                                                     caller="index"
                                                     icon="tasks" styleClass="fa-lg"
                                                     arg0="${createdIssue.issue.id}"
                                                     textActionKey="itracker.web.image.view.issue.alt"/>

                                             <c:if test="${createdIssue.userCanEdit}">
                                                <it:formatIconAction
                                                        forward="editissue" module="/module-projects"
                                                        paramName="id"
                                                        paramValue="${createdIssue.issue.id}"
                                                        caller="index"
                                                        icon="edit" styleClass="fa-lg"
                                                        info="itracker.web.image.edit.issue.alt"
                                                        arg0="${createdIssue.issue.id}"
                                                        textActionKey="itracker.web.image.edit.texttag"/>

                                             </c:if>
                                          </c:if>
                                       </div>
                                          ${createdIssue.issue.id}
                                    </td>
                                    <td>${createdIssue.issue.project.name}</td>
                                    <td>${createdIssue.statusLocalizedString}</td>
                                    <td>${createdIssue.severityLocalizedString}</td>
                                    <td>
                                       <it:formatDescription>${createdIssue.issue.description}</it:formatDescription></td>
                                    <td>
                                       <c:choose>
                                          <c:when test="${createdIssue.unassigned}">
                                             <it:message key="itracker.web.generic.unassigned"/>
                                          </c:when>
                                          <c:otherwise>
                                             ${createdIssue.issue.owner.firstName} ${createdIssue.issue.owner.lastName}
                                          </c:otherwise>
                                       </c:choose>
                                    </td>
                                    <td class="text-right text-nowrap">
                                       <it:formatDate date="${createdIssue.issue.lastModifiedDate}"/>
                                    </td>
                                 </tr>
                              </c:when>
                              <c:otherwise>

                                 <c:if test="${i.count == userPrefs.numItemsOnIndex + 1 }">
                                    <tr>
                                       <td class="moreissues tableNote" colspan="7"><html:link anchor="createdIssues"
                                                                                               module="/"
                                                                                               action="/portalhome?showAll=true"><it:message
                                               key="itracker.web.index.moreissues"/></html:link></td>
                                    </tr>
                                 </c:if>
                              </c:otherwise>
                           </c:choose>
                        </c:forEach>
                        </tbody>

                     </c:otherwise>
                  </c:choose>
               </c:if>


               <!-- watched issues -->


               <%--
                  // I could make this all the issues that have changed since the last login.  Wonder if that would be
                  // better than the watches? No then you lose them.
               --%>


               <c:if test="${(! UserUtilities_PREF_HIDE_WATCHED) || allSections}">
                  <thead>
                  <tr id="watchedIssues">
                     <td class="editColumnTitle" colspan="7"><it:message key="itracker.web.index.watched"/>:
                     </td>
                  </tr>
                  </thead>
                  <c:choose>
                     <c:when test="${empty watchedIssues}">
                        <tr>
                           <td colspan="7" class="tableNote">
                              <it:message key="itracker.web.error.noissues"/>
                           </td>
                        </tr>
                     </c:when>
                     <c:otherwise>
                        <thead>
                        <tr class="listHeading">
                           <th class="text-right"><html:link action="/portalhome?sortKey=id"><it:message
                                   key="itracker.web.attr.id"/></html:link></th>
                           <th><it:message key="itracker.web.attr.project"/></th>
                           <th><html:link action="/portalhome?sortKey=stat"><it:message
                                   key="itracker.web.attr.status"/></html:link></th>
                           <th><html:link action="/portalhome?sortKey=sev"><it:message
                                   key="itracker.web.attr.severity"/></html:link></th>
                           <th><it:message key="itracker.web.attr.description"/></th>
                           <th><html:link action="/portalhome?sortKey=own"><it:message
                                   key="itracker.web.attr.owner"/></html:link></th>
                           <th class="text-right"><html:link action="/portalhome?sortKey=lm"><it:message
                                   key="itracker.web.attr.lastmodified"/></html:link></th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:forEach items="${watchedIssues}" var="watchedIssue" step="1"
                                   varStatus="i">
                           <c:choose>
                              <c:when test="${showAll || (i.count <=userPrefs.numItemsOnIndex)}">
                                 <tr id="watchedIssue.${i.count}">
                                    <td class="text-right">
                                       <div class="pull-left">
                        <span class="HTTP_POST">
                        <it:formatIconAction
                                forward="watchissue" paramName="id"
                                paramValue="${watchedIssue.issue.id}" caller="index"
                                icon="bell-slash" styleClass="fa-lg"
                                info="itracker.web.image.unwatch.issue.alt"
                                arg0="${watchedIssue.issue.id}"
                                textActionKey="itracker.web.image.unwatch.texttag"/>
                        </span>
                                          <c:if test="${watchedIssue.userCanViewIssue}">
                                             <it:formatIconAction
                                                     forward="viewissue" module="/module-projects"
                                                     paramName="id"
                                                     paramValue="${watchedIssue.issue.id}"
                                                     caller="index"
                                                     icon="tasks" styleClass="fa-lg"
                                                     info="itracker.web.image.view.issue.alt"
                                                     arg0="${watchedIssue.issue.id}"
                                                     textActionKey="itracker.web.image.view.texttag"/>
                                             <c:if test="${watchedIssue.userCanEdit}">
                                                <it:formatIconAction
                                                        forward="editissue" module="/module-projects"
                                                        paramName="id"
                                                        paramValue="${watchedIssue.issue.id}"
                                                        caller="index"
                                                        icon="edit" styleClass="fa-lg"
                                                        info="itracker.web.image.edit.issue.alt"
                                                        arg0="${watchedIssue.issue.id}"
                                                        textActionKey="itracker.web.image.edit.texttag"/>
                                             </c:if>
                                          </c:if>
                                       </div>
                                          ${watchedIssue.issue.id}</td>
                                    <td>${watchedIssue.issue.project.name}</td>
                                    <td>${watchedIssue.statusLocalizedString}</td>
                                    <td>${watchedIssue.severityLocalizedString}</td>
                                    <td>
                                       <it:formatDescription>${watchedIssue.issue.description}</it:formatDescription></td>
                                    <td>
                                       <c:choose>
                                          <c:when test="${watchedIssue.unassigned}">
                                             <it:message key="itracker.web.generic.unassigned"/>
                                          </c:when>
                                          <c:otherwise>
                                             ${watchedIssue.issue.owner.firstName} ${watchedIssue.issue.owner.lastName}
                                          </c:otherwise>
                                       </c:choose>
                                    </td>
                                    <td class="text-right text-nowrap"><it:formatDate
                                            date="${watchedIssue.issue.lastModifiedDate}"/></td>

                                 </tr>
                              </c:when>
                           </c:choose>

                        </c:forEach>
                        </tbody>
                        <tfoot>

                        <c:if test="${i.count == userPrefs.numItemsOnIndex + 1}">
                           <tr>
                              <td class="moreissues tableNote" colspan="7">
                                 <html:link anchor="watchedIssues" module="/"
                                            action="/portalhome?showAll=true"><it:message
                                         key="itracker.web.index.moreissues"/></html:link></td>
                           </tr>
                        </c:if>
                        </tfoot>
                     </c:otherwise></c:choose>
               </c:if>

               <tfoot>
               <!-- view hidden sections link -->
               <c:if test="${showAll && userPrefs.numItemsOnIndex > 0}">
                  <tr>
                     <td class="moreissues tableNote" colspan="7">
                        <html:link module="/" action="/portalhome?showAll=false"><it:message
                                key="itracker.web.index.lessissues"/></html:link>
                     </td>
                  </tr>
               </c:if>
               <c:if test="${userPrefs.hiddenIndexSections>0}">
                  <tr>
                     <td colspan="7">
                        <c:choose>
                           <c:when test="${!allSections}">
                              <html:link action="/portalhome?allSections=true"><it:message
                                      key="itracker.web.index.viewhidden"/></html:link>
                           </c:when>
                           <c:otherwise>
                              <html:link action="/portalhome?allSections=false"><it:message
                                      key="itracker.web.index.hidehidden"/></html:link>
                           </c:otherwise>
                        </c:choose>
                     </td>
                  </tr>
               </c:if>
               </tfoot>
            </table>

         </div>
      </div>
   </div>
</div>