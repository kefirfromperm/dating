<%@ page contentType="text/html;charset=UTF-8" %>
<form action="${createLink(controller: 'profile', action: 'luck')}" method="get" id="luck">
    <input type="submit" value="<g:message code="layout.luck.label" />"/>
</form>
