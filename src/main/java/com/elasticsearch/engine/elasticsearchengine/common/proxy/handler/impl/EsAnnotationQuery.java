package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.EsQueryProxyExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.EsAnnotationQueryFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: es注解查询代理实现逻辑
 * @date 2022-04-15 18:01
 */
@Component
public class EsAnnotationQuery implements EsQueryProxyExecuteHandler {

    @Resource
    private EsAnnotationQueryFactory esAnnotationQueryFactory;

    @Override
    public Boolean matching(EsQueryProxyExecuteEnum factory) {
        return EsQueryProxyExecuteEnum.ANNOTATION_QUERY.equals(factory);
    }

    //代理类的泛型, 或者自定义泛型获取的公共方法
    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        return esAnnotationQueryFactory.getBean(EsAnnotationQueryEnum.ANNOTATION_MODEL_QUERY).handle(proxy, method, args);
    }
}
