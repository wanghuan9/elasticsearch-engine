package com.elasticsearch.engine.common.proxy.handler.impl;

import com.elasticsearch.engine.common.proxy.enums.EsQueryType;
import com.elasticsearch.engine.common.proxy.handler.EsQueryProxyExecuteHandler;
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
    public Boolean matching(EsQueryType factory) {
        return EsQueryType.ES_NATIVE.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return null;
    }
}
