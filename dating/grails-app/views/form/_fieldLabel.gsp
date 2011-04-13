<%@ page contentType="text/html;charset=UTF-8" %>
<%--
Field label
required - mark field as required
labelCode - message code for label (required)
label - default label value (optional)
--%>
<label>
    <g:message code="${labelCode}" default="${label}"/>
    <g:if test="required" ><span class="required">*</span></g:if>
</label>