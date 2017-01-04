<%@ include file="/common/taglibs.jsp" %>

<c:if test="${ empty siteTitle}">
    <c:set var="siteTitle" value="itracker.org" />
</c:if>

<html>
<head>
<title><c:out value="${ siteTitle }" />: <it:message
	key="${pageTitleKey}" arg0="${pageTitleArg}" /></title>

    <link rel="stylesheet" type="text/css"
          href="${contextPath}/themes/defaulttheme/includes/bootstrap/bootstrap.css"/>
    <link rel="stylesheet" href="${contextPath}/webjars/bootstrap-datepicker/1.6.1/css/bootstrap-datepicker.min.css"
          type="text/css" media="screen"/>
    <link rel="stylesheet" href="${contextPath}/webjars/font-awesome/4.6.3/css/font-awesome.css"
          type="text/css" media="screen"/>
    <link rel="stylesheet" type="text/css"
          href="${contextPath}/themes/defaulttheme/includes/bootstrap/bootstrap-theme.css"/>

    <link rel="stylesheet" type="text/css"
          href="${contextPath}/themes/defaulttheme/includes/styles.css"/>

    <c:if test="${not empty rssFeed}">
        <link href="${contextPath}${rssFeed}" rel="alternate" type="application/rss+xml" title="${pageTitle} RSS" />
    </c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
    <meta http-equiv="Pragma" content="no-cache"/>

    <script src="${contextPath}/webjars/jquery/2.2.4/jquery.min.js" ></script>
    <script src="${contextPath}/themes/defaulttheme/includes/bootstrap/bootstrap.min.js" ></script>
    <script src="${contextPath}/webjars/bootstrap-datepicker/1.6.1/js/bootstrap-datepicker.min.js" ></script>

<script type="text/javascript"
	src="${contextPath}/themes/defaulttheme/includes/scripts.js"></script>

<body>
  <!-- inserted header -->
<tiles:insert template="default.header.jsp" >
      <tiles:put name="title"><it:message key="${ pageTitleKey }" arg0="${ pageTitleArg }" /></tiles:put>
      <tiles:put name="errorHide" value="${true}" />
</tiles:insert>
