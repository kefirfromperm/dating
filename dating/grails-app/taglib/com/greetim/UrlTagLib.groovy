package com.greetim

import ru.permintel.saga.SagaFile

class UrlTagLib {
    static namespace = 'dating';
    def fileUrl = {attrs->
        SagaFile file = attrs.file;
        out << request.contextPath << '/file/' << file.id << '/' << file.name.encodeAsURL();
    }
}
