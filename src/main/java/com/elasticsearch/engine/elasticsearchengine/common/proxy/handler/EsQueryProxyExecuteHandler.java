package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler;


import com.elasticsearch.engine.elasticsearchengine.common.factory.MatchingBean;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;

import java.lang.reflect.Method;

/**
 * @Author 王欢
 * @Date 2020/02/19
 * @Time 15:28:59
 */
public interface EsQueryProxyExecuteHandler extends MatchingBean<EsQueryProxyExecuteEnum> {

    Object handle(Object proxy, Method method, Object[] args);
}
