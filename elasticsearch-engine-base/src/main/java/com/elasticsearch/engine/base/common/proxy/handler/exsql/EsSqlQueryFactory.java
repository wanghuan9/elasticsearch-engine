package com.elasticsearch.engine.base.common.proxy.handler.exsql;

import com.elasticsearch.engine.base.common.factory.FactoryList;
import com.elasticsearch.engine.base.common.proxy.enums.EsSqlQueryEnum;
import com.elasticsearch.engine.base.model.exception.EsEngineExecuteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-15 19:03
 */
@Component
public class EsSqlQueryFactory implements FactoryList<EsSqlQueryHandler, EsSqlQueryEnum> {

    @Resource
    private List<EsSqlQueryHandler> esSqlQueryHandlers;

    @Override
    public EsSqlQueryHandler getBean(EsSqlQueryEnum factory) {
        for (EsSqlQueryHandler esAnnotationQueryHandler : esSqlQueryHandlers) {
            if (esAnnotationQueryHandler.matching(factory)) {
                return esAnnotationQueryHandler;
            }
        }
        throw new EsEngineExecuteException("factory class not found");
    }
}
