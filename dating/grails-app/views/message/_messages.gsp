<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${messages}">
    <g:each in="${messages}" var="message">
        <p>
            <label><g:formatDate date="${message.date}"/> ${message.from.alias.encodeAsHTML()}:</label>
            ${message.text.encodeAsHTML()}
        </p>
    </g:each>
</g:if>
