package com.elasticsearch.engine.common.proxy.handler;


import com.elasticsearch.engine.common.factory.FactoryList;
import com.elasticsearch.engine.common.proxy.enums.EsQueryType;
import com.elasticsearch.engine.model.exception.EsEngineExecuteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Author 王欢
 * @Date 2020/02/19
 * @Time 15:28:59
 * <p>
 * messageProcessFacadeFactory.getBean(MqHandleCodeEnum.of(cache.getEventCode()))
 * .process(cache);
 */
@Component
public class EsQueryProxyExecuteFactory implements FactoryList<EsQueryProxyExecuteHandler, EsQueryType> {

    @Resource
    private List<EsQueryProxyExecuteHandler> esQueryProxyHandles;

    @Override
    public EsQueryProxyExecuteHandler getBean(EsQueryType factory) {
        for (EsQueryProxyExecuteHandler esQueryProxyHandle : esQueryProxyHandles) {
            if (esQueryProxyHandle.matching(factory)) {
                return esQueryProxyHandle;
            }
        }
        throw new EsEngineExecuteException("factory class not found");
    }
}


