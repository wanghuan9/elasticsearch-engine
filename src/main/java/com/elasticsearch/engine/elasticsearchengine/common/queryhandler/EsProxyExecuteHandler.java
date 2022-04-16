package com.elasticsearch.engine.elasticsearchengine.common.queryhandler;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.EsQueryProxyExecuteFactory;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
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
        if (args == null || args.length == 0 || args.length > 1) {
            throw new EsHelperQueryException("ES-HELPER un-support multi-params or miss-param, params must be single");
        }
        Class<?> returnType = method.getReturnType();
        if (args != null && args.length == 1) {
            Object param = args[0];
            return esQueryProxyExecuteFactory.getBean(EsQueryProxyExecuteEnum.ANNOTATION_QUERY).handle(proxy, method, args);
        }
        return null;
    }

}
