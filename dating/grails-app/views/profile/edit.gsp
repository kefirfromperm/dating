<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Profile" %>
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
        <g:if test="${currentProfile!=null && profile.id==currentProfile.id}">
            <div class="buttons">
                <g:link controller="profile" action="confirmDelete"><g:message code="profile.delete.button.label"/></g:link>
            </div>
        </g:if>
    </g:form>
</div>
</body>
</html>
