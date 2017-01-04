<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="id" scope="request" value="${param.id}"/>

<logic:redirect forward="viewissue" paramScope="request" paramName="id" paramId="id"></logic:redirect>