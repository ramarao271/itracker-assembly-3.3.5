<%@ include file="/common/taglibs.jsp" %>
<html:form action="/security_check" focus="login" method="post">
    <div class="container">
        <div class="row">
            <div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1">
                <div id="login" class="well well-lg" tabindex="-1" role="dialog" aria-hidden="true">
                    <div class="form center-block">
                        <div class="form-group">
                            <c:set var="login"><it:message key="itracker.web.attr.login"/></c:set>
                            <input type="text" name="login" class="form-control input-lg" placeholder="${login}">
                        </div>
                        <div class="form-group">
                            <c:set var="password"><it:message key="itracker.web.attr.password"/></c:set>
                            <input type="password" name="password" class="form-control input-lg"
                                   placeholder="${password}">
                        </div>
                        <c:if test="${ allowSaveLogin }">
                            <div class="form-group">
                                <div class="col-md-offset-2 col-md-10">
                                    <div class="checkbox input-lg">
                                        <label for="saveLogin">
                                            <html:checkbox styleId="saveLogin" value="true" property="saveLogin"/>
                                            <it:message key="itracker.web.attr.savelogin"/></label>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <div class="form-group">
                            <button id="btn-login" class="btn btn-primary btn-lg btn-block"><it:message
                                                        key="itracker.web.button.login"/></button>

                            <c:if test="${allowForgotPassword}">
         <span class="pull-right"><html:link linkName="forgotpassword" forward="forgotpassword"
                                             titleKey="itracker.web.header.menu.forgotpass.alt">
             <it:message key="itracker.web.header.menu.forgotpass"/>
         </html:link></span>
                            </c:if>
                                <%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
                            <c:if test="${allowSelfRegister}">
         <span><html:link forward="selfregistration"
                          titleKey="itracker.web.header.menu.selfreg.alt">
             <it:message key="itracker.web.header.menu.selfreg"/>
         </html:link></span>
                            </c:if>
                        </div>
                        <input type="hidden" name="authtype" value="1">

                    </div>
                </div>

            </div>
        </div>
    </div>
</html:form>