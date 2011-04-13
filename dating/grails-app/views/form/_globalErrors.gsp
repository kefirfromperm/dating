<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${bean?.errors?.hasGlobalErrors()}">
    <div class="field error">
        <g:each in="${bean?.errors?.globalErrors}">
            <g:message error="${it}"/>
        </g:each>
    </div>
</g:if>
