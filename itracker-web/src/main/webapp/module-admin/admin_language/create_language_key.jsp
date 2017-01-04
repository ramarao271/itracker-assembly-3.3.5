<%@ include file="/common/taglibs.jsp" %>

<c:set var="pageTitleKey" scope="request">itracker.web.admin.createlanguagekey.title</c:set>
<c:set var="pageTitleArg" value="" scope="request"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<div class="container-fluid maincontent">

   <html:form action="/createlanguagekey">
      <html:hidden property="action" value="create"/>

      <div class="row">
         <div class="col-sm-12">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.key"/>:</label>
               <html:text property="key" styleClass="form-control"/>
            </div>
         </div>
      </div>
      <div class="row">
         <div class="col-xs-12">
            <h5><it:message key="itracker.web.attr.translations"/>:</h5>
         </div>
      </div>
      <c:set var="localeKey" value="items(${baseLocale})"/>
      <div class="row">
         <div class="col-xs-12">
            <div class="form-group">
               <label><it:message key="itracker.web.attr.baselocale"/></label>
               <html:textarea rows="2" cols="60" property="${ localeKey }" styleClass="form-control"/>
            </div>
         </div>
      </div>

      <c:set var="localeName" value="itracker.locale.name"/>
      <c:forEach items="${ languageKeys }" var="language">
         <c:set var="localeKey" value="items(${language})"/>

         <div class="row">
            <div class="col-xs-11 col-xs-offset-1">
               <div class="form-group">
                  <label> ${ it:ITrackerResources_GetString (localeName, language) }</label>
                  <html:textarea rows="2" cols="60" property="${ localeKey }" styleClass="form-control"/>
               </div>
            </div>
         </div>

         <c:forEach items="${ languages[language] }" var="locale">
            <c:set var="localeKey" value="items(${locale})"/>

            <div class="row">
               <div class="col-xs-10 col-xs-offset-2">
                  <div class="form-group">
                     <label> ${ it:ITrackerResources_GetString (localeName, locale) }</label>
                     <html:textarea rows="2" cols="60" property="${ localeKey }" styleClass="form-control"/>
                  </div>
               </div>
            </div>
         </c:forEach>
      </c:forEach>
      <div class="row">
         <div class="col-xs-12">
            <html:submit styleClass="btn btn-primary btn-block" altKey="itracker.web.button.create.alt"
                         titleKey="itracker.web.button.create.alt"><it:message
                    key="itracker.web.button.create"/></html:submit>
         </div>
      </div>
   </html:form>

</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
