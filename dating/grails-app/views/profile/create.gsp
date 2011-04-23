<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Profile" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="profile.create.label"/></title>
</head>
<body>
<h1><g:message code="profile.create.label"/></h1>
<div class="form">
    <g:form action="save">
        <g:render template="fields" model="[profile:profile]"/>

        <div class="buttons">
            <g:submitButton name="create" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
        </div>
    </g:form>
</div>
</body>
</html>
