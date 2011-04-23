<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Account" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'account.label', default: 'Account')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>
<body>
<div>
    <img src="<gr:imageUrl mail="${account.mail}" size="80"/>" alt="Gravatar"/>
</div>
<div>
    <ul>
        <li><label><g:message code="account.mail.label" />:</label> ${account.mail.encodeAsHTML()}</li>
        <li><label><g:message code="account.date.label" />:</label> <g:formatDate date="${account.date}"/></li>
        <sec:ifAllGranted roles="ROLE_ADMIN">
            <li><label><g:message code="account.enabled.label" />:</label> <g:formatBoolean boolean="${account.enabled}"/></li>
            <li><label><g:message code="account.locked.label" />:</label> <g:formatBoolean boolean="${account.locked}"/></li>
            <li>
                <label><g:message code="account.roles.label" />:</label>
                <g:render template="roles" model="[account:account]"/>
            </li>
        </sec:ifAllGranted>
    </ul>
</div>
<div>
    <g:link controller="account" action="edit" id="${account.id}"><g:message code="default.button.edit.label" /></g:link>
    <sec:ifAllGranted roles="ROLE_ADMIN">
        <g:if test="${!account.locked}">
            <g:link controller="account" action="lock"><g:message code="account.button.lock.label" /></g:link>
        </g:if>
        <g:if test="${account.locked}">
            <g:link controller="account" action="unlock"><g:message code="account.button.unlock.label" /></g:link>
        </g:if>
        <g:link controller="account" action="delete"
                title="${message(code:'default.button.delete.confirm.message')}" class="must-confirm"
                ><g:message code="default.button.delete.label" /></g:link>
    </sec:ifAllGranted>
</div>
</body>
</html>
