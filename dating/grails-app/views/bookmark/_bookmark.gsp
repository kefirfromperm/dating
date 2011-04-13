<%@ page contentType="text/html;charset=UTF-8" %>
<g:link controller="profile" action="show" params="[alias:bookmark.target.alias]">
    ${bookmark.target.name.encodeAsHTML()}
    <g:if test="${bookmark.incoming>0}">(<g:formatNumber number="${bookmark.incoming}"/>)</g:if>
</g:link>
