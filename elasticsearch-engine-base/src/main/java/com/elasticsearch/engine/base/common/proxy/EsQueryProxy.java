package com.elasticsearch.engine.base.common.proxy;

import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
* @author wanghuan
* @description EsQueryProxy
* @mail 958721894@qq.com       
* @date 2022/6/9 14:12 
*/
public class EsQueryProxy<T> implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(EsQueryProxy.class);

    private Class<T> targetInterface;

    private boolean visitQueryBeanParent = true;

    private EsProxyExecuteHandler esProxyExecuteHandler;

    private boolean enableLogOut = false;

    public EsQueryProxy(Class<T> targetInterface, boolean visitQueryBeanParent) {
        this.targetInterface = targetInterface;
        this.visitQueryBeanParent = visitQueryBeanParent;
    }

    public EsQueryProxy(Class<T> targetInterface, boolean visitQueryBeanParent, EsProxyExecuteHandler esProxyExecuteHandler, boolean enableLogOut) {
        this.targetInterface = targetInterface;
        this.visitQueryBeanParent = visitQueryBeanParent;
        this.esProxyExecuteHandler = esProxyExecuteHandler;
        this.enableLogOut = enableLogOut;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = targetInterface.getSimpleName() + "." + method.getName() + " ";
        ThreadLocalUtil.set(CommonConstant.INTERFACE_METHOD_NAME, methodName);
        try {
            return esProxyExecuteHandler.invoke(proxy, method, args);
        } catch (Exception e) {
            throw e;
        } finally {
            ThreadLocalUtil.remove(CommonConstant.INTERFACE_METHOD_NAME);
        }
    }

}
