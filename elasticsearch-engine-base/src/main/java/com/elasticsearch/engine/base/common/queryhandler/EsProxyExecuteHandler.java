package com.elasticsearch.engine.base.common.queryhandler;

import com.elasticsearch.engine.base.common.proxy.enums.EsQueryType;
import com.elasticsearch.engine.base.common.proxy.handler.EsQueryProxyExecuteFactory;
import com.elasticsearch.engine.base.model.annotion.EsQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-15 19:15
 */
@Component
public class EsProxyExecuteHandler {

    @Resource
    private EsQueryProxyExecuteFactory esQueryProxyExecuteFactory;

    /**
     * 1.注解
     * * 1)model注解
     * * 2)方法参数注解
     * * 1)model注解查询
     * * 2)参数注解查询
     * 2.sql
     * * 1)注解
     * * 2)xml
     * 3.es原生语句
     * * 1)注解
     * * 2)xml
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    public Object invoke(Object proxy, Method method, Object[] args) {
        EsQueryType annotationQuery = EsQueryType.ANNOTATION;
        //有查询注解
        if (method.isAnnotationPresent(EsQuery.class)) {
            EsQuery annotation = method.getAnnotation(EsQuery.class);
            annotationQuery = annotation.queryType();
        }
        return esQueryProxyExecuteFactory.getBean(annotationQuery).handle(proxy, method, args);
    }

}
