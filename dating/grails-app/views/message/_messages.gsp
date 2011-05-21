<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${messages}">
    <g:each in="${messages}" var="message">
        <p>
            <label class="${currentProfile==message.from?'out':'in'}">
                <g:formatDate
                        date="${message.date}" formatName="message.date.format"
                    timeZone="${applicationContext.timeZoneResolver.resolveTimeZone(request)}"
                />
                ${message.from.alias.encodeAsHTML()}:
            </label>
            ${message.text.encodeAsHTML()}
        </p>
    </g:each>
</g:if>
