<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Account" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title><g:message code="account.label"/></title>
</head>
<body>
<h1><g:message code="account.edit.label"/></h1>
<div class="form">
    <g:form method="post" action="update">
        <g:hiddenField name="id" value="${command.account.id}"/>
        <g:hiddenField name="version" value="${command.version}"/>

        <g:render template="/form/fieldErrors" model="[bean:command, field:'version']"/>

        <sec:ifAllGranted roles="ROLE_ADMIN">
            <g:render template="/form/textField" model="[
                labelCode: 'account.mail.label', label: 'E-mail',
                required: true, field:'mail', bean:command, size:'medium', maxLength:320
        ]"/>
        </sec:ifAllGranted>

        <g:render template="/form/passwordField" model="[
                labelCode: 'account.password.label', label: 'Password',
                field:'password', bean:command, size:'medium', maxLength:63
        ]"/>

        <g:render template="/form/passwordField" model="[
                labelCode: 'account.confirm.label', label: 'Confirm password',
                field:'confirm', bean:command, size:'medium', maxLength:63
        ]"/>

        <sec:ifAllGranted roles="ROLE_ADMIN">
            <g:render template="/form/checkBox" model="[
                    labelCode: 'account.enabled.label', label: 'Enabled',
                    field:'enabled', bean:command
            ]"/>
            <g:render template="/form/checkBox" model="[
                    labelCode: 'account.locked.label', label: 'Locked',
                    field:'locked', bean:command
            ]"/>
            <g:render template="/form/checkBox" model="[
                    labelCode: 'account.user.label', label: 'User role',
                    field:'user', bean:command
            ]"/>
            <g:render template="/form/checkBox" model="[
                    labelCode: 'account.admin.label', label: 'Admin role',
                    field:'admin', bean:command
            ]"/>
        </sec:ifAllGranted>

        <g:render template="/form/globalErrors" bean="${command}"/>

        <div class="buttons">
            <g:submitButton name="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        </div>
    </g:form>
</div>
</body>
</html>
