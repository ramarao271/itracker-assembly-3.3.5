<%@ page language="java" isErrorPage="true" pageEncoding="UTF-8" contentType="text/html; charset=utf-8" %>
<%@ include file="/common/taglibs.jsp" %>

 <% if (exception != null) { %>
    <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>
 <% } else if ((Exception)request.getAttribute("javax.servlet.error.exception") != null) { %>
    <pre><% ((Exception)request.getAttribute("javax.servlet.error.exception"))
                           .printStackTrace(new java.io.PrintWriter(out)); %></pre>
 <% } else if (pageContext.findAttribute("org.apache.struts.action.EXCEPTION") != null) { %>
    <bean:define id="exception2" name="org.apache.struts.action.EXCEPTION"
     type="java.lang.Exception"/>
    <c:if test="exception2 != null">
        <pre><% exception2.printStackTrace(new java.io.PrintWriter(out));%></pre>
    </c:if>
    <%-- only show this if no error messages present --%>
    <c:if test="exception2 == null">
        <bean:message key="errors.none"/>
    </c:if>
 <% } %>
