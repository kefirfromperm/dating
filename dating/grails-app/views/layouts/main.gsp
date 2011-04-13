<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="path" content="${request.contextPath}"/>
    <title><g:layoutTitle default="Welcome!"/> &mdash; <g:message code="app.name"/></title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dating.css')}"/>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
    <g:javascript src="jquery/jquery-1.5.min.js"/>
    <g:javascript src="jquery/jquery.timers-1.2.js"/>
    <g:javascript src="application.js"/>
    <g:layoutHead/>
</head>
<body>
<a id="tooCool" href="http://www.w3junkies.com/toocool/">Too Cool for Internet Explorer</a>

<div id="body">
    <%-- Top header --%>
    <div id="header">
        <div class="left">
            <sec:ifLoggedIn>
                <g:link controller="profile"><g:message code="layout.link.profile"/></g:link>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <g:link controller="admin">admin</g:link>
                </sec:ifAllGranted>
                <g:link controller="account" action="edit"><g:message code="layout.link.account.edit"/></g:link>
                <g:link controller="logout"><g:message code="layout.link.logout"/></g:link>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <g:link controller="login" action="auth"><g:message code="layout.link.login"/></g:link>
                <g:link controller="account" action="registration"><g:message code="layout.link.account.registration"/></g:link>
            </sec:ifNotLoggedIn>
        </div>
        <div class="right">
            <g:render template="/layouts/search"/>
            <g:render template="/layouts/luck"/>
            <g:render template="/layouts/language"/>
        </div>
    </div>

<%-- Flash message --%>
    <g:if test="${flash.message}">
        <g:set var="cssClass" value=""/>
        <g:if test="${flash.error}"><g:set var="cssClass" value="${cssClass+' error'}"/></g:if>
        <div id="flash-message" class="${cssClass}">
            <g:message code="${flash.message}" args="${flash.args}" default="${flash.message.encodeAsHTML()}"/>
        </div>
    </g:if>

<%-- Body content --%>
    <g:layoutBody/>

    <div id="footer">&copy; <g:message code="layout.copy"/></div>
</div>
</body>
</html>