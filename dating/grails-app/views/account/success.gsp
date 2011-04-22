<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title><g:message code="account.registration.success.label" default="Registration success"/></title>
</head>
<body>
    <div id="success-registration">
        <g:message code="account.registration.success.message" args="[flash.mail?.encodeAsHTML()]" />
    </div>
</body>
</html>