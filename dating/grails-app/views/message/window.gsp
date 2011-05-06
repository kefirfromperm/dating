<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="path" content="${request.contextPath}"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dating.css')}"/>
    <g:render template="/layouts/js"/>
    <title>${profile.name.encodeAsHTML()}&nbsp;IM</title>
</head>
<body>
<div id="im-body">
    <h1>${profile.name.encodeAsHTML()}</h1>
    <g:render template="/message/messenger"/>
    </div>
</body>
</html>