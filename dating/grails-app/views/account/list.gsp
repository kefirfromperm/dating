<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Account" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title><g:message code="account.list.label"/></title>
</head>
<body>
<h1><g:message code="account.list.label" /></h1>
<div class="list">
    <table>
        <thead>
        <tr>
            <g:sortableColumn property="id" title="${message(code: 'account.id.label', default: 'Id')}"/>
            <g:sortableColumn property="mail" title="${message(code: 'account.mail.label', default: 'E-mail')}"/>
            <g:sortableColumn property="date" title="${message(code: 'account.date.label', default: 'Date')}"/>
            <th>${message(code: 'account.enabled.label', default: 'Enabled')}</th>
            <th>${message(code: 'account.locked.label', default: 'Locked')}</th>
            <th>${message(code: 'account.roles.label', default: 'Roles')}</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${list}" status="i" var="account">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:link action="show" id="${account.id}">${account.id}</g:link></td>
                <td><a href="mailto:${account.mail.encodeAsHTML()}">${account.mail.encodeAsHTML()}</a></td>
                <td><g:formatDate date="${account.date}"/></td>
                <td><g:formatBoolean boolean="${account.enabled}"/></td>
                <td><g:formatBoolean boolean="${account.locked}"/></td>
                <td><g:render template="roles" model="[account:account]"/></td>
                <td><g:link action="edit" id="${account.id}">изменить</g:link></td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>
<g:if test="${total>params.max}">
    <div class="paginate">
        <g:paginate total="${total}"/>
    </div>
</g:if>
</body>
</html>
