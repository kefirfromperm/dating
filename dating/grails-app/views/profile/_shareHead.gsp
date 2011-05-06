<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" contentType="text/html;charset=UTF-8" %>
<g:if test="${RequestContextUtils.getLocale(request)?.getLanguage()?.equalsIgnoreCase('ru')}">
    <%-- Vkontakte --%>
    <script type="text/javascript" src="http://userapi.com/js/api/openapi.js?26"></script>

    <%-- Odnoklassniki --%>
    <link href="http://stg.odnoklassniki.ru/share/odkl_share.css" rel="stylesheet">
    <script src="http://stg.odnoklassniki.ru/share/odkl_share.js" type="text/javascript" ></script>

    <script type="text/javascript">
        VK.init({apiId: 2318219, onlyWidgets: true});
        $(document).ready(function(){
            ODKL.init();
        });
    </script>
</g:if>
