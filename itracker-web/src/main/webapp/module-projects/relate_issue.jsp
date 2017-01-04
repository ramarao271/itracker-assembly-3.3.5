<%@ include file="/common/taglibs.jsp"%>

<%@ page import="org.itracker.model.util.IssueUtilities" %>

<% // TODO : move redirect logic to the Action class. 
    String issueId = (String) request.getParameter("id");
    if(issueId == null || issueId.equals("")) {
%>
      <logic:forward name="unauthorized"/>
<%  } else { %>
        <bean:define id="pageTitleKey" value="itracker.web.relateissue.title"/>
        <bean:define id="pageTitleArg" value="<%= issueId %>"/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

        <html:form action="/addissuerelation" onsubmit="validateIssueRelationForm(this);">

        <html:javascript formName="issueRelationForm"/>

           <logic:messagesPresent>
              <div class="alert alert-danger">
                 <div id="pageErrors" class="text-center">
                    <html:messages id="error">
                       <div><bean:write name="error"/></div>
                    </html:messages>
                 </div>
              </div>
           </logic:messagesPresent>
        <html:hidden property="issueId" value="<%= issueId %>"/>
        <html:hidden property="caller" value="<%= (String) request.getParameter("caller") %>"/>
        <table width="100%" cellspacing="1"  cellspacing="0"  border="0" align="left">
          <tr><td align="left">
            <span class="editColumnText"><it:message key="itracker.web.attr.thisissue" /></span>
            <html:select property="relationType" styleClass="editColumnText">
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_RELATED_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_RELATED_P, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_P, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DUPLICATE_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DUPLICATE_C, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_P) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_P, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
                <html:option value="<%= Integer.toString(IssueUtilities.RELATION_TYPE_DEPENDENT_C) %>" styleClass="editColumnText"><%= IssueUtilities.getRelationName(IssueUtilities.RELATION_TYPE_DEPENDENT_C, (java.util.Locale)pageContext.getAttribute("currLocale")) %></html:option>
            </html:select>
            <span class="editColumnText"><it:message key="itracker.web.attr.issue"/></span>
            <html:text size="5" property="relatedIssueId" styleClass="editColumnText"/>.
          </td></tr>
          <tr><td><html:img width="1" height="18" src="/themes/defaulttheme/images/blank.gif"/></td></tr>
          <tr><td align="left">
            <html:submit styleClass="button" altKey="itracker.web.button.add.alt" titleKey="itracker.web.button.add.alt"><it:message key="itracker.web.button.add"/></html:submit>
          </td></tr>
        </table>
        </html:form>
        <br/>

        <tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
<%  } %>