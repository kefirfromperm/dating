<%@ page contentType="text/html;charset=UTF-8" %>
<div id="bookmarks">
    <g:render template="/bookmark/bookmarksContent" model="[bookmarks:bookmarks,incomings:incomings]"/>
</div>