<%@ include file="/common/taglibs.jsp" %>

<bean:define id="pageTitleKey" value="itracker.web.admin.import.load.title"/>
<bean:define id="pageTitleArg" value=""/>

<!DOCTYPE HTML>
<tiles:insert page="/themes/defaulttheme/includes/header.jsp"/>

<html:javascript formName="importForm"/>

<logic:messagesPresent>
   <div class="alert alert-danger">
      <div id="pageErrors" class="text-center">
         <html:messages id="error">
            <div><bean:write name="error"/></div>
         </html:messages>
      </div>
   </div>
</logic:messagesPresent>

<html:form action="/importdataverify" enctype="multipart/form-data">
   <table border="0" cellspacing="0" cellspacing="1" width="100%">
      <tr>
         <td colspan="2" width="50%"></td>
         <td colspan="2" width="50%"></td>
      </tr>
      <tr>
         <td class="editColumnTitle" width="1%"><it:message key="itracker.web.attr.file"/>:&nbsp;&nbsp;</td>
         <td class="editColumnText" colspan="3" align="left"><html:file property="importFile"
                                                                        styleClass="editColumnText"/></td>
      </tr>

      <tr>
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td>
      </tr>
      <tr class="editColumnTitle">
         <td colspan="4"><it:message key="itracker.web.admin.import.options"/></td>
      </tr>
      <tr class="listHeading">
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="2" width="1"/></td>
      </tr>
      <tr>
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="3" width="1"/></td>
      </tr>
      <tr>
         <td colspan="2" valign="top">
            <table width="100%" cellspacing="0" cellspacing="1" border="0">
               <tr>
                  <td width="25"></td>
                  <td></td>
               </tr>
               <tr>
                  <td class="editColumnText"><html:checkbox property="optionreuseusers" value="true"
                                                            styleId="optionreuseusers"/></td>
                  <td class="editColumnText"><label for="optionreuseusers"><it:message
                          key="itracker.web.admin.import.options.reuseusers"/></label></td>
               </tr>
               <tr>
                  <td class="editColumnText"><html:checkbox property="optionreuseprojects" value="true"
                                                            styleId="optionreuseprojects"/></td>
                  <td class="editColumnText"><label for="optionreuseprojects"><it:message
                          key="itracker.web.admin.import.options.reuseprojects"/></label></td>
               </tr>
               <tr>
                  <td class="editColumnText"><html:checkbox property="optioncreatepasswords" value="true"
                                                            styleId="optioncreatepasswords"/></td>
                  <td class="editColumnText"><label for="optioncreatepasswords"><it:message
                          key="itracker.web.admin.import.options.createpasswords"/></label></td>
               </tr>
            </table>
         </td>
         <td colspan="2" valign="top">
            <table width="100%" cellspacing="0" cellspacing="1" border="0">
               <tr>
                  <td width="25"></td>
                  <td></td>
               </tr>
               <tr>
                  <td class="editColumnText"><html:checkbox property="optionreuseconfig" value="true"
                                                            styleId="optionreuseconfig"/></td>
                  <td class="editColumnText"><label for="optionreuseconfig"><it:message
                          key="itracker.web.admin.import.options.reuseconfig"/></label></td>
               </tr>
               <tr>
                  <td class="editColumnText"><html:checkbox property="optionreusefields" value="true"
                                                            styleId="optionreusefields"/></td>
                  <td class="editColumnText"><label for="optionreusefields"><it:message
                          key="itracker.web.admin.import.options.reusefields"/></label></td>
               </tr>
            </table>
         </td>
      </tr>

      <tr>
         <td colspan="4"><html:img module="/" page="/themes/defaulttheme/images/blank.gif" height="12" width="1"/></td>
      </tr>
      <tr>
         <td align="left" colspan="4"><html:submit styleClass="button" altKey="itracker.web.button.import.alt"
                                                   titleKey="itracker.web.button.import.alt"><it:message
                 key="itracker.web.button.import"/></html:submit></td>
      </tr>
   </table>
   <br/>
</html:form>

<tiles:insert page="/themes/defaulttheme/includes/footer.jsp"/></body></html>
