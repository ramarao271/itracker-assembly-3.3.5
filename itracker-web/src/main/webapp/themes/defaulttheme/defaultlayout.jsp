<!DOCTYPE HTML>
<%@ include file="/common/taglibs.jsp"%>


<c:set var="themePath">/themes/defaulttheme</c:set>
<c:if test="${ empty siteTitle}">
    <c:set var="siteTitle" value="itracker.org" />
</c:if>
<c:set var="pageTitle">
    <c:choose>
        <c:when test="${empty pageTitleKey}">
            <%-- looking for tiles pageTitleKey to translate --%>
            <bean:define id="pageTitleKey"><tiles:getAsString name="pageTitleKey" ignore="true"/></bean:define>
            <c:choose>
                <c:when test="${empty pageTitleKey}">
                    <tiles:getAsString name="title" ignore="false"/>
                </c:when>
                <c:otherwise>
                    <it:message key="${pageTitleKey}" arg0="${pageTitleArg}" />
                </c:otherwise>
            </c:choose>
        </c:when>

        <c:otherwise><%-- Looking for untranslated fallback title --%><it:message
                	key="${pageTitleKey}" arg0="${pageTitleArg}" />
        </c:otherwise>
    </c:choose>
</c:set>

<c:set var="pageTitleFull">
    Test <c:out value="${ siteTitle }: ${ pageTitle }" />
</c:set>

<html>
<head>

    <title><c:out value="${ pageTitle }" /></title>
    <link rel="stylesheet" href="${contextPath}${themePath}/includes/bootstrap/bootstrap.min.css"
          type="text/css" media="screen"/>
    <link rel="stylesheet" href="${contextPath}/webjars/bootstrap-datepicker/1.6.1/css/bootstrap-datepicker.min.css"
          type="text/css" media="screen"/>
    <link rel="stylesheet" href="${contextPath}/webjars/font-awesome/4.6.3/css/font-awesome.css"
          type="text/css" media="screen"/>
    <link rel="stylesheet" type="text/css"
          href="${contextPath}${themePath}/includes/bootstrap/bootstrap-theme.css"/>

    <link rel="stylesheet" type="text/css"
          href="${contextPath}${themePath}/includes/styles.css"/>

    <c:if test="${not empty rssFeed}">
        <link href="${contextPath}${rssFeed}" rel="alternate" type="application/rss+xml" title="${pageTitle} RSS" />
    </c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT"/>
    <meta http-equiv="Pragma" content="no-cache"/>

    <script src="${contextPath}/webjars/jquery/2.2.4/jquery.min.js" ></script>
    <script src="${contextPath}${themePath}/includes/bootstrap/bootstrap.min.js" ></script>
    <script src="${contextPath}/webjars/bootstrap-datepicker/1.6.1/js/bootstrap-datepicker.min.js" ></script>
</head>
<body>

<tiles:insert attribute="header" flush="false">
    <tiles:put name="title" beanName="pageTitle" />
</tiles:insert>

<tiles:insert attribute="body" />

<tiles:useAttribute name="footer" id="footerPath" ></tiles:useAttribute>
<tiles:insert attribute="footer" />
</body>

</html>

