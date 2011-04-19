<%@ page contentType="text/html;charset=UTF-8" %>
<div id="messenger">
    <div id="message-list">
        <input type="hidden" name="profile.id" value="${profile.id}"/>
        <input type="hidden" name="beginTimestamp" value="${messagesCommand.firstMessageTimestamp?:0}"/>
        <input type="hidden" name="endTimestamp" value="${messagesCommand.lastMessageTimestamp?:0}"/>
        <div>
        <g:render template="/message/messages" model="[messages:messagesCommand.messages]"/>
        </div>
    </div>
    <div id="message-manage">
        <g:if test="${params.controller!='message' && params.action!='messenger'}" >
            <g:link controller="message" action="messenger" params="[alias:profile.alias]" target="_blank"
                elementId="new-window"
            ><g:message code="message.new.window.label" /></g:link>
        </g:if>
        <g:if test="${bookmark==null || bookmark?.incoming==0}"><g:set var="cssClass" value="default-hide"/></g:if>
            <g:link controller="message" action="markAsDelivered" params="['profile.id':profile.id]"
                elementId="mark-as-delivered" class="${cssClass}"
            ><g:message code="message.mark.as.delivered.label" /></g:link>
    </div>
    <div id="message-error" class="error"></div>
    <div id="message-input">
        <form action="${createLink(controller: 'message', action: 'send')}" method="post">
            <g:if test="${params.controller=='message' && params.action=='messenger'}" >
                <input type="hidden" name="window" value="true"/>
            </g:if>
            <input type="hidden" name="profile.id" value="${profile.id}"/>
            <textarea name="text"></textarea>
            <input type="submit" value="<g:message code="message.send.label" />" id="message-send-button"/>
            <br/>
        </form>
    </div>
</div>
