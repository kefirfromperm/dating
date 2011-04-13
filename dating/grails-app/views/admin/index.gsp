<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Admin console</title>
</head>
<body>
<h1>Admin console</h1>
<table>
    <tr>
        <td><g:link controller="account" action="list">Accounts</g:link></td>
        <td>${accountCount}</td>
    </tr>
    <tr>
        <td>Profiles</td>
        <td>${profileCount}</td>
    </tr>
    <tr>
        <td><g:link controller="asynchronousMail" action="list">Email</g:link></td>
        <td></td>
    </tr>
</table>
</body>
</html>