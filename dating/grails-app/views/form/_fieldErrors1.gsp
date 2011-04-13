<%@ page contentType="text/html;charset=UTF-8" %>
<%--
Text field
field - field name (required)
bean - bean with value or errors (optional)
--%>
<g:hasErrors bean="${bean}" field="${field}">
    <div>
        <g:eachError bean="${bean}" field="${field}">
            <g:message error="${it}"/>
        </g:eachError>
    </div>
</g:hasErrors>
