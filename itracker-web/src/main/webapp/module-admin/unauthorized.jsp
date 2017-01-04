<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.unauthorized.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>
<div class="alert alert-danger">
   <div id="pageErrors" class="text-center">
      <div><it:message key="itracker.web.error.unauthorized"/></div>
   </div>
</div>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
