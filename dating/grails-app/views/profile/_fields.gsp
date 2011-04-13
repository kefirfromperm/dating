<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${profile?.useGravatar}"><input type="hidden" name="useGravatar" value="true"/></g:if>

<g:render template="/form/aliasField" model="[
    labelCode: 'profile.alias.label', label: 'Link to your profile',
    required: true, field:'alias', bean:profile
]"/>

<g:render template="/form/textField" model="[
        labelCode: 'profile.name.label', label: 'Name',
        required: true, field:'name', bean:profile, size:'big', maxLength:255
]"/>

<g:render template="/form/textArea" model="[
        labelCode: 'profile.about.label', label: 'About',
        required: true, field:'about', bean:profile, cols:80, rows:8, size:'big'
]"/>

<g:render template="/form/globalErrors" bean="${profile}"/>
