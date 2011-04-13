<%@ page contentType="text/html;charset=UTF-8" %>
<%--
Text field
labelCode - message code for label (required)
label - default label value (optional)
field - field name (required)
bean - bean with value or errors (optional)
value - field value if bean is absent
--%>
<g:set var="cssClass" value="field checkbox"/>
<g:if test="${bean?.errors?.hasFieldErrors(field)}"><g:set var="cssClass" value="${cssClass+' error'}"/></g:if>
<div class="${cssClass}">
<g:checkBox name="${field}" checked="${bean?.properties?.get(field)?:value}" value="true"/>
<g:render template="/form/fieldLabel" model="[labelCode:labelCode, label:label]"/>
<g:render template="/form/fieldErrors1" model="[bean:bean, field:field]"/>
</div>