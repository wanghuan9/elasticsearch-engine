package com.elasticsearch.engine.base.common.proxy.handler;


import com.elasticsearch.engine.base.common.factory.MatchingBean;
import com.elasticsearch.engine.base.common.proxy.enums.EsQueryType;

import java.lang.reflect.Method;

/**
 * @Author 王欢
 * @Date 2020/02/19
 * @Time 15:28:59
 */
public interface EsQueryProxyExecuteHandler extends MatchingBean<EsQueryType> {

    Object handle(Object proxy, Method method, Object[] args);
}
