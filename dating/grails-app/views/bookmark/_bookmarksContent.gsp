<%@ page contentType="text/html;charset=UTF-8" %>
<h2><g:message code="bookmark.label" /></h2>
<g:if test="${bookmarks}">
    <g:render template="/bookmark/bookmark" collection="${bookmarks}" var="bookmark"/>
</g:if>
<g:if test="${incomings}">
    <hr/>
    <h2><g:message code="bookmark.incoming.label" /></h2>
    <g:render template="/bookmark/bookmark" collection="${incomings}" var="bookmark"/>
</g:if>
