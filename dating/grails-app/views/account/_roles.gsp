<%@ page contentType="text/html;charset=UTF-8" %>
<g:each in="${account.roles*.authority}" var="authority" status="i"><g:if test="${i!=0}">, </g:if>${authority.encodeAsHTML()}</g:each>