<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="path" content="${request.contextPath}"/>
    <title><g:layoutTitle default="Welcome!"/>&nbsp;&mdash;&nbsp;<g:message code="app.name" default="Greetim"/></title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'dating.css')}"/>
    <g:render template="/layouts/js"/>
    <g:layoutHead/>
    <%-- Google analytics --%>
    <script type="text/javascript">
        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-22918672-1']);
        _gaq.push(['_trackPageview']);

        (function() {
            var ga = document.createElement('script');
            ga.type = 'text/javascript';
            ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(ga, s);
        })();
    </script>
</head>
<body>
<div id="body">
    <%-- Top header --%>
    <div id="header">
        <div class="left">
            <sec:ifLoggedIn>
                <g:link controller="profile"><g:message code="layout.link.profile"/></g:link>
                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <g:link controller="admin">admin</g:link>
                </sec:ifAllGranted>
                <g:link controller="account" action="edit"><g:message code="layout.link.account.edit"/></g:link>
                <g:link controller="logout"><g:message code="layout.link.logout"/></g:link>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <g:link controller="login" action="auth"><g:message code="layout.link.login"/></g:link>
                <g:link controller="account" action="registration"><g:message code="layout.link.account.registration"/></g:link>
            </sec:ifNotLoggedIn>
        </div>
        <div class="right">
            <g:render template="/layouts/search"/>
            <g:render template="/layouts/luck"/>
            <g:render template="/layouts/language"/>
        </div>
    </div>

<%-- Flash message --%>
    <g:if test="${flash.message}">
        <g:set var="cssClass" value=""/>
        <g:if test="${flash.error}"><g:set var="cssClass" value="${cssClass+' error'}"/></g:if>
        <div id="flash-message" class="${cssClass}">
            <g:message code="${flash.message}" args="${flash.args}" default="${flash.message.encodeAsHTML()}"/>
        </div>
    </g:if>

<%-- Body content --%>
    <g:layoutBody/>

    <div id="footer">&copy; <g:message code="layout.copy"/></div>
</div>

<!-- Yandex.Metrika counter -->
<div style="display:none;"><script type="text/javascript">
    (function(w, c) {
        (w[c] = w[c] || []).push(function() {
            try {
                w.yaCounter6302740 = new Ya.Metrika({id:6302740,
                    clickmap:true,
                    trackLinks:true});
            }
            catch(e) {
            }
        });
    })(window, 'yandex_metrika_callbacks');
</script></div>
<script src="//mc.yandex.ru/metrika/watch.js" type="text/javascript" defer="defer"></script>
<noscript><div><img src="//mc.yandex.ru/watch/6302740" style="position:absolute; left:-9999px;" alt=""/></div></noscript>
<!-- /Yandex.Metrika counter -->
</body>
</html>