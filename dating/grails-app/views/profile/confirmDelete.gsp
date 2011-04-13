<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.perm.kefir.dating.Profile" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="profile.delete.label"/></title>
</head>
<body>
<h1><g:message code="default.button.delete.confirm.message"/></h1>
<div class="form">
    <g:form method="post" action="delete">
        <div class="field">
            <g:render template="/form/fieldLabel"
                    model="[required:required, labelCode:'profile.confirmDelete.label', label:'Are you sure to delete your profile?']"/>
            <g:textField name="confirm" size="60" maxlength="255"/>
        </div>

        <div class="buttons">
            <g:submitButton name="delete" value="${message(code:'default.button.delete.label')}"/>
        </div>
    </g:form>
</div>
</body>
</html>