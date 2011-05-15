<#-- @ftlvariable name="notifications" type="java.util.List<com.greetim.Notification>" -->
<body>
<p>Hello ${recipient.name?html},</p>
<p>New messages are at <a href="${serverUrl}">${serverUrl}</a></p>
<#list notifications as notification>
<#assign message=notification.message/>
<p>
    <label>Message is from
        <a href="${serverUrl}/look/${message.from.alias}">${message.from.name?html}</a>
        at ${message.date?string("yyyy-MM-dd HH:mm:ss")}:</label>
    ${message.text?html}
</p>
</#list>
<p>
    --<br/>
    Please, do not reply to this message.
</p>
</body>