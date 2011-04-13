<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${profile.useGravatar}">
    <img src="<gr:imageUrl mail="${profile.account.mail}" size="200"/>" alt="gravatar"/>
</g:if>
<g:elseif test="${profile.photo}">
    <img src="<dating:fileUrl file="${profile.photo}"/>" alt="photo"/>
</g:elseif>
