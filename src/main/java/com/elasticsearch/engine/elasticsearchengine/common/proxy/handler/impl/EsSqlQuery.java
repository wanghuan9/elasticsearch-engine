package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.EsQueryProxyExecuteHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: es sql查询代理实现逻辑
 * @date 2022-04-15 18:10
 */
@Component
public class EsSqlQuery implements EsQueryProxyExecuteHandler {
    @Override
    public Boolean matching(EsQueryProxyExecuteEnum factory) {
        return EsQueryProxyExecuteEnum.SQL_QUERY.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return null;
    }
}
