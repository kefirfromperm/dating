<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.springframework.web.servlet.support.RequestContextUtils; com.greetim.BookmarkStatus" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>${profile.name.encodeAsHTML()}</title>
    <g:render template="/profile/shareHead"/>
</head>
<body>
<div id="profile-menu">
    <%-- Bookmarks --%>
    <g:if test="${currentProfile!=null && profile.id!=currentProfile.id}">
        <g:if test="${bookmark==null || bookmark.status==BookmarkStatus.NEUTRAL}">
            <g:link controller="bookmark" action="add" params="[alias:profile.alias]"><g:message code="bookmark.add.label"/></g:link>
            <g:link controller="bookmark" action="ban" params="[alias:profile.alias]"><g:message code="bookmark.ban.label"/></g:link>
        </g:if>
        <g:elseif test="${bookmark.status==BookmarkStatus.BAN}">
            <g:link controller="bookmark" action="add" params="[alias:profile.alias]"><g:message code="bookmark.add.label"/></g:link>
            <g:link controller="bookmark" action="remove" params="[alias:profile.alias]"><g:message code="bookmark.unban.label"/></g:link>
        </g:elseif>
        <g:else>
            <g:link controller="bookmark" action="remove" params="[alias:profile.alias]"><g:message code="bookmark.remove.label"/></g:link>
            <g:link controller="bookmark" action="ban" params="[alias:profile.alias]"><g:message code="bookmark.ban.label"/></g:link>
        </g:else>
    </g:if>

    <%-- Manage --%>
    <g:if test="${managed}">
        <g:link controller="profile" action="edit" id="${profile.id}"><g:message code="profile.edit.button.label"/></g:link>
        <g:link controller="profile" action="photo" id="${profile.id}"><g:message code="profile.upload.photo.button.label"/></g:link>
    </g:if>

    <sec:ifAllGranted roles="ROLE_ADMIN">
        <g:link controller="account" action="show" id="${profile.account.id}"><g:message code="account.link.label"/></g:link>
        <g:link controller="profile" action="deleteAndLock" id="${profile.id}"
                title="${message(code:'default.button.delete.confirm.message')}" class="must-confirm"><g:message code="profile.delete.and.lock.link.label"/></g:link>
    </sec:ifAllGranted>
</div>

<g:if test="${currentProfile!=null}">
    <g:render template="/bookmark/bookmarks"/>
</g:if>

<div id="photo">
    <g:render template="avatar" model="[profile:profile]"/>
</div>

<div id="traffic-lights">
    <g:render template="trafficLights"/>
</div>

<g:if test="${currentProfile!=null && profile.id!=currentProfile.id}">
    <g:render template="/message/messenger"/>
</g:if>

<div id="about">
    <h1>${profile.name.encodeAsHTML()}</h1>
    <p><saga:bb>${profile.about}</saga:bb></p>
</div>

<%-- Social network integration. Like buttons. --%>
<g:render template="share" model="[profile:profile]" />
</body>
</html>
