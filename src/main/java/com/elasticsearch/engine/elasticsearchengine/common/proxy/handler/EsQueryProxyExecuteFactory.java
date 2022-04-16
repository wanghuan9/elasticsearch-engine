package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler;


import com.elasticsearch.engine.elasticsearchengine.common.factory.FactoryList;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperExecuteException;
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
public class EsQueryProxyExecuteFactory implements FactoryList<EsQueryProxyExecuteHandler, EsQueryProxyExecuteEnum> {

    @Resource
    private List<EsQueryProxyExecuteHandler> esQueryProxyHandles;

    @Override
    public EsQueryProxyExecuteHandler getBean(EsQueryProxyExecuteEnum factory) {
        for (EsQueryProxyExecuteHandler esQueryProxyHandle : esQueryProxyHandles) {
            if (esQueryProxyHandle.matching(factory)) {
                return esQueryProxyHandle;
            }
        }
        throw new EsHelperExecuteException("factory class not found");
    }
}


