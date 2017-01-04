<%@ include file="/common/taglibs.jsp" %>

<c:set var="pageTitleKey" scope="request">itracker.web.admin.listlanguages.title</c:set>
<c:set var="pageTitleArg" value="" scope="request"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp">
   <tiles:put name="errorHide" value="${ false }"/>
</tiles:insert>

<div class="container-fluid maincontent">

   <div class="row">
      <div class="col-xs-12">
         <div class="table-responsive">
            <table class="table table-striped table-hover table-condensed">
               <colgroup>
                  <col>
                  <col>
                  <col class="col-xs-7">
                  <col class="col-xs-5">
               </colgroup>
               <tr>
                  <td colspan="3">
                     <it:message key="itracker.web.attr.baselocale"/>
                  </td>
                  <td>
                     <div class="pull-right btn-group btn-group-sm">
                        <it:link action="editlanguageform" targetAction="create" paramName="locale" styleClass="btn btn-sm btn-link"
                                 paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.create.alt"
                                 arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.create"
                                                                        arg0="${ baseLocaleName }"/></it:link>
                        <it:link action="createlanguagekeyform" targetAction="create" styleClass="btn btn-sm btn-link"
                                 titleKey="itracker.web.admin.listlanguages.createkey.alt"><it:message
                                key="itracker.web.admin.listlanguages.createkey"/></it:link>
                        <it:link action="editlanguageform" targetAction="update" paramName="locale" styleClass="btn btn-sm btn-link"
                                 paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.update.alt"
                                 arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.update"
                                                                        arg0="${ baseLocaleName }"/></it:link>
                        <%--        <it:link action="editlanguage" targetAction="disable" paramName="locale" paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.disable.alt" arg0="${ languageName }"><it:message key="itracker.web.admin.listlanguages.disable"/></it:link>--%>
                        <it:link action="exportlanguage" targetAction="export" paramName="locale" styleClass="btn btn-sm btn-link"
                                 paramValue="${ baseLocale }" titleKey="itracker.web.admin.listlanguages.export.alt"
                                 arg0="${ baseLocaleName }"><it:message key="itracker.web.admin.listlanguages.export"
                                                                        arg0="${ baseLocaleName }"/></it:link>
                     </div>
                  </td>
               </tr>

               <c:set var="key" value="itracker.locale.name"/>

               <c:forEach items="${ languageKeys }" var="language">
                  <c:set var="locales" value="${ languages[language] }"/>
                  <c:set var="languageName" value="${ it:ITrackerResources_GetString(key, language) }"/>
                  <c:set var="keyL">${ key }.${ language }</c:set>
                  <c:set var="localizedName" value="${ it:ITrackerResources_GetString(keyL, 'BASE' ) }"/>
                  <tr>
                     <td></td>
                     <td colspan="2">
                           ${ languageName }
                        ( ${ localizedName } )
                     </td>
                     <td>
                        <div class="pull-right btn-group btn-group-sm">
                           <it:link action="editlanguageform" targetAction="create" paramName="locale" styleClass="btn btn-sm btn-link"
                                    paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.create.alt"
                                    arg0="${ languageName }"><it:message
                                   key="itracker.web.admin.listlanguages.create"/></it:link>
                           <it:link action="editlanguageform" targetAction="update" paramName="locale" styleClass="btn btn-sm btn-link"
                                    paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.update.alt"
                                    arg0="${ languageName }"><it:message
                                   key="itracker.web.admin.listlanguages.update"/></it:link>
                           <it:link action="editlanguage" targetAction="disable" paramName="locale" styleClass="btn btn-sm btn-link"
                                    paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.disable.alt"
                                    arg0="${ languageName }"><it:message
                                   key="itracker.web.admin.listlanguages.disable"/></it:link>
                           <it:link action="exportlanguage" targetAction="export" paramName="locale" styleClass="btn btn-sm btn-link"
                                    paramValue="${ language }" titleKey="itracker.web.admin.listlanguages.export.alt"
                                    arg0="${ languageName }"><it:message
                                   key="itracker.web.admin.listlanguages.export"/></it:link>
                        </div>
                     </td>
                  </tr>
                  <c:forEach items="${ locales }" var="locale">
                     <c:set var="localeName" value="${ it:ITrackerResources_GetString(key, locale) }"/>
                     <c:set var="keyL">${ key }.${ locale }</c:set>
                     <c:set var="localizedName" value="${ it:ITrackerResources_GetString(keyL, 'BASE' ) }"/>
                     <tr>
                        <td></td>
                        <td></td>
                        <td>
                              ${ localeName }
                           ( ${ localizedName } )
                        </td>
                        <td>
                           <div class="pull-right btn-group btn-group-sm">
                              <it:link action="editlanguageform" targetAction="update" paramName="locale" styleClass="btn btn-sm btn-link"
                                       paramValue="${ locale }" titleKey="itracker.web.admin.listlanguages.update.alt"
                                       arg0="${ localeName }"><it:message
                                      key="itracker.web.admin.listlanguages.update"/></it:link>
                              <it:link action="editlanguage" targetAction="disable" paramName="locale" styleClass="btn btn-sm btn-link"
                                       paramValue="${ language }"
                                       titleKey="itracker.web.admin.listlanguages.disable.alt"
                                       arg0="${ languageName }"><it:message
                                      key="itracker.web.admin.listlanguages.disable"/></it:link>
                              <it:link action="exportlanguage" targetAction="export" paramName="locale" styleClass="btn btn-sm btn-link"
                                       paramValue="${ locale }" titleKey="itracker.web.admin.listlanguages.export.alt"
                                       arg0="${ localeName }"><it:message
                                      key="itracker.web.admin.listlanguages.export"/></it:link>
                           </div>
                        </td>
                     </tr>
                  </c:forEach>
               </c:forEach>
            </table>
         </div>
      </div>
   </div>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
