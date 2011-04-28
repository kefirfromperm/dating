<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Profile" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="profile.list.label"/></title>
</head>
<body>
<div id="search-result">
    <h1><g:message code="profile.list.label"/></h1>
    <table>
        <tbody>
        <g:each in="${list}" status="i" var="profile">
            <tr>
                <td><g:render template="avatar" model="[profile:profile]"/></td>
                <td class="traffic-lights"><g:render template="trafficLights" model="[light:lights[profile]]"/></td>
                <td>
                    <p><g:link controller="profile" action="show" params="[alias:profile.alias]">${profile.name.encodeAsHTML()}</g:link></p>
                    <p><saga:bb>${profile.about}</saga:bb></p>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
    <g:if test="${total>params.max}">
        <div class="paginate">
            <g:paginate total="${total}"/>
        </div>
    </g:if>
</div>
<g:if test="${currentProfile!=null}">
    <g:render template="/bookmark/bookmarks"/>
</g:if>
</body>
</html>
