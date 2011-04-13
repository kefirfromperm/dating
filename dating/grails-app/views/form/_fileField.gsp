<%@ page contentType="text/html;charset=UTF-8" %>
<%--
Text field
required - mark field as required
labelCode - message code for label (required)
label - default label value (optional)
field - field name (required)
bean - bean with value or errors (optional)
--%>
<g:set var="cssClass" value="field"/>
<g:if test="${bean?.errors?.hasFieldErrors(field)}"><g:set var="cssClass" value="${cssClass+' error'}"/></g:if>
<div class="${cssClass}">
<g:render template="/form/fieldLabel" model="[required:required, labelCode:labelCode, label:label]"/>
<input type="file" name="${field}" id="${field}"/>
<g:render template="/form/fieldErrors1" model="[bean:bean, field:field]"/>
</div>