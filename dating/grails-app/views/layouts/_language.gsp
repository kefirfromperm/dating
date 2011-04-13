<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" contentType="text/html;charset=UTF-8" %>
<g:set var="ru" value="${RequestContextUtils.getLocale(request)?.getLanguage()?.equalsIgnoreCase('ru')}"/>
<form action="" method="get" id="lang">
    <select name="lang">
        <option value="en">English</option>
        <option value="ru"<g:if test="${ru}"> selected="selected"</g:if>>Русский</option>
    </select>
    <input type="submit" value="<g:message code="layout.lang.change.label" />" class="nojs"/>
</form>