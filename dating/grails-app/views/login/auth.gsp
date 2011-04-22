<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Login</title>
</head>
<body>
<div class="form">
    <g:form url="${postUrl}" method="post">
        <g:render template="/form/textField" model="[
                labelCode: 'account.mail.label', label: 'E-mail',
                required: true, field:'j_username', size:'medium', maxLength:320
    ]"/>
        <g:render template="/form/passwordField" model="[
                labelCode: 'account.password.label', label: 'Password',
                required: true, field:'j_password', size:'medium', maxLength:63
    ]"/>
        <g:render template="/form/checkBox" model="[
                    labelCode: 'login.rememberme.label', label: 'Remember me',
                    field:rememberMeParameter, value:true
    ]"/>
        <div class="buttons">
            <g:submitButton name="login" value="${message(code:'login.label')}"/>
            <g:link controller="account" action="registration"><g:message code="account.password.forgot" default="Forgot your password?"/></g:link>
        </div>
    </g:form>
</div>
</body>
</html>