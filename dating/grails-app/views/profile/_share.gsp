<%@ page import="org.springframework.web.servlet.support.RequestContextUtils" contentType="text/html;charset=UTF-8" %>
<div class="share">
    <%-- Page title --%>
    <g:set var="pageTitle" value="${profile.name}" />

    <%-- Page URL --%>
    <g:set var="pageUrl" value="${createLink(absolute: true, controller:'profile', action:'show', params: ['alias': profile.alias])}"/>

    <%-- Image URL --%>
    <g:if test="${profile.useGravatar}">
        <g:set var="imageUrl" value="${applicationContext.gravatarService.imageUrl(profile.account.mail)}"/>
    </g:if>
    <g:elseif test="${profile.photo}">
        <g:set var="imageUrl" value="${grailsApplication.config.grails.serverURL}/${request.contextPath}/file/${profile.photo.id}/${profile.photo.name.encodeAsURL()}"/>
    </g:elseif>
    <g:else>
        <g:set var="imageUrl" value=""/>
    </g:else>

    <%-- Facebook --%>
    <div class="share-button">
    <iframe src="http://www.facebook.com/plugins/like.php?href=${pageUrl.encodeAsURL()}&amp;send=false&amp;layout=button_count&amp;width=150&amp;show_faces=true&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:150px; height:21px;" allowTransparency="true"></iframe>
    </div>

    <%-- Twitter --%>
    <div class="share-button">
    <a href="http://twitter.com/share" class="twitter-share-button" data-url="${pageUrl.encodeAsHTML()}" data-count="horizontal" data-lang="ru">Tweet</a><script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script>
    </div>

    <g:if test="${RequestContextUtils.getLocale(request)?.getLanguage()?.equalsIgnoreCase('ru')}">
    <%-- Vkontakte --%>
        <div class="share-button">
        <div id="vk_like_${profile.alias.encodeAsHTML()}"></div>
        <script type="text/javascript">
            VK.Widgets.Like("vk_like_${profile.alias.encodeAsHTML()}", {
                type: "button", pageTitle:'${pageTitle.encodeAsJavaScript()}',
                pageUrl: "${pageUrl.encodeAsJavaScript()}", pageImage: "${imageUrl.encodeAsJavaScript()}"
            });
        </script>
        </div>

    <%-- Odnoklassniki --%>
        <div class="share-button">
        <a class="odkl-klass-stat" href="${pageUrl.encodeAsHTML()}" onclick="ODKL.Share(this);return false;" ><span>0</span></a>
        </div>
    </g:if>
</div>
