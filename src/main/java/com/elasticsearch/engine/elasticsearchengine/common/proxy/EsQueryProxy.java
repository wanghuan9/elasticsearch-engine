package com.elasticsearch.engine.elasticsearchengine.common.proxy;

import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.EsExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * author     JohenTeng
 * date      2021/7/21
 */
public class EsQueryProxy<T> implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(EsQueryProxy.class);

    private Class<T> targetInterface;

    private boolean visitQueryBeanParent = true;

    private EsExecuteHandler esExecuteHandler;

    private boolean enableLogOutEsQueryJson = false;

    public EsQueryProxy(Class<T> targetInterface, boolean visitQueryBeanParent, RestHighLevelClient client) {
        this.targetInterface = targetInterface;
        this.visitQueryBeanParent = visitQueryBeanParent;
        this.esExecuteHandler = esExecuteHandler;
    }

    public EsQueryProxy(Class<T> targetInterface, boolean visitQueryBeanParent, EsExecuteHandler esExecuteHandler, boolean enableLogOutEsQueryJson) {
        this.targetInterface = targetInterface;
        this.visitQueryBeanParent = visitQueryBeanParent;
        this.esExecuteHandler = esExecuteHandler;
        this.enableLogOutEsQueryJson = enableLogOutEsQueryJson;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (args == null || args.length == 0 || args.length > 1) {
            throw new EsHelperQueryException("ES-HELPER un-support multi-params or miss-param, params must be single");
        }
        Class<?> returnType = method.getReturnType();
        if (args != null && args.length == 1) {
            Object param = args[0];
            return esExecuteHandler.execute(param, returnType);
        }
        return null;
    }

}
