<#-- @ftlvariable name="url" type="String" -->
<body>
<p>Здравствуйте,</p>
<p>Ваш email был использован при регистрации на сайте <a href="${serverUrl}">${serverUrl}</a></p>
<p>
    Для входа на сайт используйте ссылку:<br/>
    <a href="${serverUrl+url}">${serverUrl+url}</a>
</p>
<p>
    Если Вы не регистрировались на сайте <a href="${serverUrl}">${serverUrl}</a> и не пытались
    восстановить пароль проигнорируйте данное сообщение.
</p>
<p>
    --<br/>
    Сообщение сформировано почтовым роботом сайта <a href="${serverUrl}">${serverUrl}</a>.
    Пожалуйста, не отвечайте на него.
</p>
</body>