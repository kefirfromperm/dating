package ru.perm.kefir.dating

import freemarker.template.Template
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory

class TemplateService {
    static scope = 'singleton';
    static transactional = false;

    FreeMarkerConfigurationFactory freemarkerConfig;

    String process(String name, Object params, Locale locale) {
        Template template = template(name, locale);
        CharArrayWriter writer = new CharArrayWriter();
        template.process(params, writer);
        return writer.toString();
    }

    private Template template(String name, Locale locale) {
        return  freemarkerConfig.createConfiguration().getTemplate("${name}.ftl", locale?:Locale.ENGLISH);
    }
}