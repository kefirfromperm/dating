<%@ page contentType="text/html;charset=UTF-8" %>
<%--
Text field
field - field name (required)
bean - bean with value or errors (optional)
--%>
<g:hasErrors bean="${bean}" field="${field}">
    <div class="field error">
        <g:render template="/form/fieldErrors1" model="[bean:bean, field:field]"/>
    </div>
</g:hasErrors>
