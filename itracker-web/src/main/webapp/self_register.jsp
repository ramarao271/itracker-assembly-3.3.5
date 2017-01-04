<%@ include file="/common/taglibs.jsp" %>
<div class="container">
    <div class="row">
        <div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1">

            <c:choose>
                <c:when test="${! allowSelfRegister}">
                    <span style="color: red;"><it:message key="itracker.web.error.notenabled"/></span>
                </c:when>
                <c:otherwise>

                    <html:form action="/selfregister" focus="login">
                        <html:hidden property="action" value="register"/>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="username"><it:message key="itracker.web.attr.login"/>:</label>
                                    <html:text property="login" styleClass="form-control" styleId="username"/>
                                </div>
                                <div class="form-group">
                                    <label for="password"><it:message key="itracker.web.attr.password"/>:</label>
                                    <html:password property="password" styleClass="form-control" styleId="password"
                                                   redisplay="false"/>
                                </div>
                                <div class="form-group">
                                    <label for="confpassword"><it:message
                                            key="itracker.web.attr.confpassword"/>:</label>
                                    <html:password property="confPassword" styleClass="form-control"
                                                   styleId="confpassword"
                                                   redisplay="false"/>
                                </div>
                                <div class="form-group">
                                    <label for="firstname"><it:message key="itracker.web.attr.firstname"/>:</label>
                                    <html:text property="firstName" styleClass="form-control" styleId="firstname"/>
                                </div>
                                <div class="form-group">
                                    <label for="lastname"><it:message key="itracker.web.attr.lastname"/>:</label>
                                    <html:text property="lastName" styleClass="form-control" styleId="lastname"/>
                                </div>
                                <div class="form-group">
                                    <label for="email"><it:message key="itracker.web.attr.email"/>:</label>
                                    <html:text property="email" styleClass="form-control" styleId="email"/>
                                </div>
                                <div class="form-group">
                                    <button id="btn-submit" class="btn btn-primary btn-lg btn-block"><it:message
                                                                key="itracker.web.button.submit"/></button>
                                        <span class="pull-right"><html:link linkName="index" forward="index"
                                                                            titleKey="itracker.web.login.title">
                                            <it:message key="itracker.web.login.title"/>
                                        </html:link></span>
                                        <%-- <nitrox:var name="allowSelfRegister" type="java.lang.Boolean"/> --%>
                                    <c:if test="${allowForgotPassword}">
                                           <span ><html:link linkName="forgotpassword" forward="forgotpassword"
                                                                               titleKey="itracker.web.header.menu.forgotpass.alt">
                                               <it:message key="itracker.web.header.menu.forgotpass"/>
                                           </html:link></span>
                                    </c:if>
                                </div>

                            </div>
                        </div>


                    </html:form>

                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
