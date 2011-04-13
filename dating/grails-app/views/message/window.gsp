<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="path" content="${request.contextPath}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dating.css')}"/>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
    <g:javascript src="jquery/jquery-1.5.min.js"/>
    <g:javascript src="jquery/jquery.timers-1.2.js"/>
    <g:javascript src="application.js"/>
    <title>${profile.name.encodeAsHTML()} IM</title>
</head>
<body>
<div id="im-body">
    <h1>${profile.name.encodeAsHTML()}</h1>
    <g:render template="/message/messenger"/>
    </div>
</body>
</html>