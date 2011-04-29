<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.greetim.Profile" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="profile.photo.upload.label" /></title>
</head>
<body>
<h1><g:message code="profile.photo.upload.label"/></h1>
<div>
    <div id="photo">
        <g:render template="avatar" model="[profile:profile]"/>
    </div>
    <div id="photo-form">
        <g:form method="post" action="uploadPhoto" enctype="multipart/form-data">
            <g:hiddenField name="id" value="${profile?.id}"/>
            <g:hiddenField name="version" value="${profile?.version}"/>
            <g:render template="/form/fieldErrors" model="[bean:profile, field:'version']"/>
            <g:render template="/form/fileField" model="[
                labelCode: 'profile.photo.label', label: 'File',
                required: false, field:'photo', bean:profile
            ]"/>
            <g:render template="/form/textField" model="[
                    labelCode: 'profile.photo.label1', label: 'URL',
                    required: false, field:'url', size:'medium', maxLength:2048
            ]"/>
            <g:render template="/form/globalErrors" bean="${profile}"/>
            <div class="buttons">
                <g:submitButton name="save" value="${message(code: 'file.button.upload.label', default: 'Upload')}"/>
            </div>
        </g:form>
        <p>
            <g:link action="useGravatar" id="${profile.id}"><g:if test="${profile.useGravatar}"><g:message code="profile.dont.use.gravatar.label" /></g:if><g:else><g:message code="profile.use.gravatar.label" /></g:else></g:link>
            <a href="${gr.profileUrl(mail:profile.account.mail)}"><g:message code="gravatar.profile.label" /></a>
        </p>
        <p>
            <g:link action="deletePhoto" id="${profile.id}"><g:message code="profile.delete.photo.label" /></g:link>
        </p>
    </div>
</div>
</body>
</html>
