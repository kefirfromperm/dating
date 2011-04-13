package ru.perm.kefir.dating.template;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.util.Map;
import ru.permintel.saga.BbService;

import java.io.IOException;

/**
 * Freemarker directive for bb processing with KefirBB
 */
public class BbDirective implements TemplateDirectiveModel {
    private BbService bbService;

    @Override
    public void execute(Environment environment,
                        Map params,
                        TemplateModel[] templateModels,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        TemplateModel value = (TemplateModel) params.get("value");
        if (value != null) {
            environment.getOut().write(bbService.process(value.toString()).toString());
        }
    }

    /**
     * Set text processor
     * <p/>
     * Dependency with spring.
     *
     * @param bbService text processor
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public void setBbService(BbService bbService) {
        this.bbService = bbService;
    }
}
