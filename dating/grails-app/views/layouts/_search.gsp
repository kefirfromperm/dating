<%@ page contentType="text/html;charset=UTF-8" %>
<form action="${createLink(controller: 'profile', action: 'list')}" method="get" id="search">
    <label for="query"><g:message code="layout.search.label" /></label>
    <input type="text" name="q" id="query" value="${params.q?.encodeAsHTML()}"/>
    <input type="submit" value="<g:message code="layout.find.label" />"/>
</form>
