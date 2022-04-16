package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.EsQueryProxyExecuteHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: es原生语句查询代理实现逻辑
 * @date 2022-04-15 18:11
 */
@Component
public class EsNativeQuery implements EsQueryProxyExecuteHandler {
    @Override
    public Boolean matching(EsQueryProxyExecuteEnum factory) {
        return EsQueryProxyExecuteEnum.ES_NATIVE_QUERY.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return null;
    }
}
