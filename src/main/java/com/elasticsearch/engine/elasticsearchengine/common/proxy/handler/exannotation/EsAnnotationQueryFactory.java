package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation;

import com.elasticsearch.engine.elasticsearchengine.common.factory.FactoryList;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperExecuteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-15 19:03
 */
@Component
public class EsAnnotationQueryFactory implements FactoryList<EsAnnotationQueryHandler, EsAnnotationQueryEnum> {

    @Resource
    private List<EsAnnotationQueryHandler> esAnnotationQueryHandlers;

    @Override
    public EsAnnotationQueryHandler getBean(EsAnnotationQueryEnum factory) {
        for (EsAnnotationQueryHandler esAnnotationQueryHandler : esAnnotationQueryHandlers) {
            if (esAnnotationQueryHandler.matching(factory)) {
                return esAnnotationQueryHandler;
            }
        }
        throw new EsHelperExecuteException("factory class not found");
    }
}
