package com.elasticsearch.engine.elasticsearchengine.common.proxy;

import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.EsProxyExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
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

    private EsProxyExecuteHandler esProxyExecuteHandler;

    private boolean enableLogOutEsQueryJson = false;

    public EsQueryProxy(Class<T> targetInterface, boolean visitQueryBeanParent, RestHighLevelClient client) {
        this.targetInterface = targetInterface;
        this.visitQueryBeanParent = visitQueryBeanParent;
        this.esProxyExecuteHandler = esProxyExecuteHandler;
    }

    public EsQueryProxy(Class<T> targetInterface, boolean visitQueryBeanParent, EsProxyExecuteHandler esProxyExecuteHandler, boolean enableLogOutEsQueryJson) {
        this.targetInterface = targetInterface;
        this.visitQueryBeanParent = visitQueryBeanParent;
        this.esProxyExecuteHandler = esProxyExecuteHandler;
        this.enableLogOutEsQueryJson = enableLogOutEsQueryJson;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        ThreadLocalUtil.set(CommonConstant.INTERFACE_NAME, targetInterface.getSimpleName());
        ThreadLocalUtil.set(CommonConstant.METHOD_NAME, method.getName());
        try {
            return esProxyExecuteHandler.invoke(proxy, method, args);
        } catch (Exception e) {
            throw e;
        } finally {
            ThreadLocalUtil.remove(CommonConstant.INTERFACE_NAME);
            ThreadLocalUtil.remove(CommonConstant.METHOD_NAME);
        }
    }

}
