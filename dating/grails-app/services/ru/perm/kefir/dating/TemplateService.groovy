package ru.perm.kefir.dating

import freemarker.template.Template
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory

class TemplateService {
    static scope = 'singleton';
    static transactional = false;

    FreeMarkerConfigurationFactory freemarkerConfig;

    String process(String name, Object params){
        Template template = freemarkerConfig.createConfiguration().getTemplate(name+'.ftl');
        CharArrayWriter writer = new CharArrayWriter();
        template.process(params, writer);
        return writer.toString();
    }
}