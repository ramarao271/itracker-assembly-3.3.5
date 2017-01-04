<%@ include file="/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<p class="help">
    <b><it:message key="itracker.web.helpabout.itrackerversion"/>:</b> <bean:write name="currentVersion"/><br/>
    <b><it:message key="itracker.web.helpabout.starttime"/>:</b> <bean:write name="starttime"/><br/>
    <b><it:message key="itracker.web.helpabout.defaultlocale"/>:</b> <it:message key="itracker.locale.name"/><br/>
    <br/>

    <b><it:message key="itracker.web.helpabout.javaversion"/>:</b> <bean:write name="javaVersion" scope="request"/>,
    <bean:write name="javaVendor" scope="request"/><br/>
    <br/>
</p>

<table cellspacing="0" cellspacing="1" border="0" width="100%" class="help">
    <tr>
        <td><b><it:message key="itracker.web.helpabout.createdby"/>:</b></td>
        <td align="left">itracker developer community, <a href="http://itracker.sourceforge.net">itracker.org, based on the
            initial code donation by Jason Carroll (jcarroll@cowsultants.com)</a>
        </td>
    </tr>
</table>
