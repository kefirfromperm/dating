<%@ page import="com.greetim.Light" contentType="text/html;charset=UTF-8" %>
<g:if test="${light==Light.GREEN}">
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
    <img src="${resource(dir:'images', file:'green.png')}" alt="GREEN"/>
</g:if>
<g:elseif test="${light==Light.YELLOW}">
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
    <img src="${resource(dir:'images', file:'yellow.png')}" alt="YELLOW"/>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
</g:elseif>
<g:elseif test="${light==Light.RED}">
    <img src="${resource(dir:'images', file:'red.png')}" alt="RED"/>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
</g:elseif>
<g:else>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
    <img src="${resource(dir:'images', file:'gray.png')}" alt=""/>
</g:else>
