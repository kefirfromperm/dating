package ru.permintel.saga

class BbTagLib {
    BbService bbService;
    static namespace = "saga";

    def bb = {attrs, body ->
        String text = extractText(attrs, body);
        if (text != null) {
            if (attrs.conf) {
                out << bbService.process(attrs.conf, text);
            } else {
                out << bbService.process(text);
            }
        }
    }

    def xml = {attrs, body ->
        String text = extractText(attrs, body);
        if (text != null) {
            out << bbService.escapeXml(text);
        }
    }

    /**
     * Extract text from attribute or body
     */
    private String extractText(attrs, body) {
        String text = attrs.text;
        if (text == null) {
            text = body()?.toString();
        }
        return text
    }
}
