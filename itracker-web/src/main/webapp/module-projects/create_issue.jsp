<%@ include file="/common/taglibs.jsp" %>

<jsp:useBean id="project" scope="session" type="org.itracker.model.Project"/>
<jsp:useBean id="currUser" scope="session" type="org.itracker.model.User"/>
<jsp:useBean id="statusName" scope="request" type="java.lang.String" />
<jsp:useBean id="possibleOwners" scope="request" type="java.util.Collection" />
<jsp:useBean id="possibleCreators" scope="request" type="java.util.Collection" />
<jsp:useBean id="severities" scope="request" type="java.util.Collection" />
<jsp:useBean id="components" scope="request" type="java.util.Collection" />
<jsp:useBean id="versions" scope="request" type="java.util.Collection" />
<jsp:useBean id="projectFields" scope="request" type="java.util.Collection" />
<jsp:useBean id="listOptions" scope="request" type="java.util.Map" />
<jsp:useBean id="hasAttachmentOption" scope="request" type="java.lang.Boolean" />


<bean:define id="pageTitleKey" value="itracker.web.createissue.title"/>
<bean:define id="pageTitleArg" value="${project.name}"/>

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

   <html:form action="/createissue" focus="description" enctype="multipart/form-data">
      <html:hidden property="projectId" value="${project.id}"/>


      <div class="row">

         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.description"/>:</label>
               <html:text size="80" styleId="description"
                          property="description"
                          styleClass="form-control"/>
            </div>
         </div>
         <div class="col-sm-6">
            <div class="form-group"><label><it:message key="itracker.web.attr.project"/>:</label>
               <p class="form-control-static">
                  <it:formatIconAction forward="listissues"
                                       module="/module-projects"
                                       paramName="projectId"
                                       paramValue="${project.id}"
                                       caller="editissue"
                                       icon="tasks" iconClass="fa-lg"
                                       info="itracker.web.image.issuelist.issue.alt"
                                       textActionKey="itracker.web.image.issuelist.texttag"/>
                     ${project.name}
               </p>
            </div>
         </div>
      </div>


      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.status"/>:</label>
               <p class="form-control-static">${statusName}</p>
            </div>
         </div>

         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.owner"/>:</label>

               <c:choose>
                  <c:when test="${not empty possibleOwners}">
                     <c:set var="unassignedDone" value="${false}"/>

                     <html:select property="ownerId" styleClass="form-control">
                        <html:option value="-1" key="itracker.web.generic.unassigned"/>

                        <c:forEach var="possibleOwner" items="${possibleOwners}">
                           <html:option value="${possibleOwner.value}">${possibleOwner.name}</html:option>

                        </c:forEach>
                     </html:select>
                  </c:when>
                  <c:otherwise>
                     <p class="form-control-static" id="owner"><it:message
                             key="itracker.web.generic.unassigned"/></p>
                  </c:otherwise>
               </c:choose>
            </div>
         </div>
      </div>


      <div class="row">
         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.severity"/>:</label>
               <html:select property="severity" styleClass="form-control">
                  <c:forEach var="severity" items="${severities}">
                     <html:option value="${severity.value}">${severity.name}</html:option>
                  </c:forEach>
               </html:select>
            </div>
         </div>


         <div class="col-sm-6">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.creator"/>:</label>
               <c:choose>
                  <c:when test="${not empty possibleCreators}">
                     <html:select property="creatorId" styleClass="form-control">

                        <c:forEach var="possibleCreator" items="${possibleCreators}">
                           <html:option value="${possibleCreator.value}">${possibleCreator.name}</html:option>

                        </c:forEach>
                     </html:select>
                  </c:when>
                  <c:otherwise>
                     <p class="form-control-static">${currUser.firstName}currUser
                        <!-- TODO: fix this <- % -= currUser.getFirstName() + " " + currUser.getLastName() % >--></p>
                  </c:otherwise>
               </c:choose>
            </div>
         </div>
      </div>


      <div class="row">
         <div class="col-sm-6">
            <c:if test="${not empty components}">
               <div class="form-group">
                  <label><it:message key="itracker.web.attr.components"/>:</label>
                  <html:select property="components" size="5" multiple="true" styleClass="form-control">
                     <c:forEach var="component" items="${components }">
                        <html:option value="${component.value}"
                                     styleClass="form-control">${component.name}
                        </html:option>
                     </c:forEach>
                  </html:select>
               </div>
            </c:if>
         </div>
         <div class="col-sm-6">

            <c:if test="${not empty versions}">
               <div class="form-group">
                  <label><it:message key="itracker.web.attr.versions"/>:</label>
                  <html:select property="versions" size="5" multiple="true" styleClass="form-control">
                     <c:forEach var="version" items="${versions}">
                        <html:option value="${version.value}">${version.name}</html:option>
                     </c:forEach>
                  </html:select>
               </div>
            </c:if>
         </div>
      </div>

      <c:if test="${not empty projectFields}">

         <div class="row">
            <div class="col-sm-6">
               <h5><it:message
                       key="itracker.web.attr.customfields"/></h5>
            </div>
         </div>
         <div class="row">
            <c:forEach var="projectField" items="${ projectFields }" varStatus="i">
               <div class="col-sm-6">
                  <it:formatCustomField field="${ projectField }" formName="createIssueForm"
                                        listOptions="${ listOptions }"/>
               </div>
            </c:forEach>
         </div>
      </c:if>


      <c:if test="${hasAttachmentOption}">
         <div class="row">
            <div class="col-sm-6">
               <div class="form-group">
                  <label><it:message key="itracker.web.attr.description"/></label>
                  <html:text property="attachmentDescription" size="30"
                             maxlength="60" styleClass="form-control"/>

               </div>
            </div>
            <div class="col-sm-6">
               <div class="form-group">
                  <label><it:message key="itracker.web.attr.file"/></label>
                  <html:file property="attachment" styleClass=""/>
               </div>
            </div>
         </div>
      </c:if>

      <div class="row">
         <div class="col-sm-12">
            <h5><it:message key="itracker.web.attr.detaileddescription"/></h5>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-12">
            <c:set var="histPlaceholder"><it:message key="itracker.web.attr.detaileddescription"/></c:set>
                  <textarea name="history"
                            rows="6" placeholder="${histPlaceholder}"
                            class="form-control"><bean:write name="createIssueForm" property="history"/></textarea>
         </div>
      </div>
      <div class="row">
         <div class="col-sm-12">
            <html:submit styleClass="btn btn-primary btn-block" altKey="itracker.web.button.update.alt"
                         titleKey="itracker.web.button.update.alt">
               <it:message key="itracker.web.button.create"/></html:submit>
         </div>
      </div>
   </html:form>
</div>
<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
