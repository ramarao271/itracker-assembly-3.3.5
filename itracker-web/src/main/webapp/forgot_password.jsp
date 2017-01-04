<%@ include file="/common/taglibs.jsp" %>

<c:choose>
    <c:when test="${! allowForgotPassword}">
        <span style="color: red;"><it:message key="itracker.web.error.notenabled"/></span>
    </c:when>
    <c:otherwise>

        <html:form action="/forgotpassword" focus="login">
            <html:javascript formName="forgotPasswordForm"/>

            <div class="container">
                <div class="row">
                    <div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1">
                        <div id="forgotpassword" class="well well-lg" tabindex="-1" aria-hidden="true">
                            <div class="form center-block">
                                <div class="form-group">
                                    <c:set var="login"><it:message key="itracker.web.attr.login"/></c:set>
                                    <input type="text" name="login" class="form-control input-lg"
                                           placeholder="${login}">
                                </div>
                                <div class="form-group">
                                    <c:set var="lastname"><it:message key="itracker.web.attr.lastname"/></c:set>
                                    <input type="text" name="lastName" class="form-control input-lg"
                                           placeholder="${lastname}">
                                </div>
                                <div class="form-group">
                                    <button id="btn-submit" class="btn btn-primary btn-lg btn-block"><it:message
                                                                key="itracker.web.button.submit"/></button>
                                        <span class="pull-right"><html:link linkName="index" forward="index"
                                                                            titleKey="itracker.web.login.title">
                                            <it:message key="itracker.web.login.title"/>
                                        </html:link></span>
                                        <%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
                                    <c:if test="${allowSelfRegister}">
                                         <span><html:link forward="selfregistration"
                                                          titleKey="itracker.web.header.menu.selfreg.alt">
                                             <it:message key="itracker.web.header.menu.selfreg"/>
                                         </html:link></span>
                                    </c:if>
                                </div>

                            </div>
                        </div>

                    </div>
                </div>
            </div>


        </html:form>
    </c:otherwise>
</c:choose>
