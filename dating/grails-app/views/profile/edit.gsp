<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.perm.kefir.dating.Profile" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="profile.edit.label"/></title>
</head>
<body>
<h1><g:message code="profile.edit.label"/></h1>
<div class="form">
    <g:form method="post" action="update">
        <g:hiddenField name="id" value="${profile?.id}"/>
        <g:hiddenField name="version" value="${profile?.version}"/>
        <g:render template="/form/fieldErrors" model="[bean:profile, field:'version']"/>
        <g:render template="fields" model="[profile:profile]"/>
        <div class="buttons">
            <g:submitButton name="save" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
        </div>
    </g:form>
</div>
</body>
</html>
