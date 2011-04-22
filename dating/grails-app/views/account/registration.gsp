<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title><g:message code="account.registration.label" default="Registration"/></title>
</head>
<body>
<h1><g:message code="account.registration.label" default="Registration"/></h1>
<div class="form">
    <g:form action="apply" method="POST">
        <g:render template="/form/textField" model="[
                labelCode: 'account.registration.mail.label', label: 'Enter your email',
                required: true, field:'mail', bean:account, size:'medium', maxLength:320
                ]"/>
        <g:render template="/form/globalErrors" bean="${account}"/>
        <div class="buttons">
            <g:submitButton name="apply" value="${message(code:'account.registration.apply.label', 'default':'Apply')}"/>
        </div>
    </g:form>
</div>
</body>
</html>