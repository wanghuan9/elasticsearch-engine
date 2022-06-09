package com.elasticsearch.engine.common.proxy;

import com.elasticsearch.engine.common.queryhandler.EsProxyExecuteHandler;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;

/**
* @author wanghuan
* @description EsEngineProxyBeanFactory
* @mail 958721894@qq.com       
* @date 2022/6/9 14:10 
*/
public class EsEngineProxyBeanFactory<T> implements ApplicationContextAware, InitializingBean, FactoryBean<T> {

    private static final String ENABLE_LOG_OUT_PROPERTIES = "es.helper.queryLogOut.enable";
    private Class<T> targetInterfaceClazz;
    private boolean visitQueryBeanParent = Boolean.TRUE;
    @Resource
    private EsProxyExecuteHandler esProxyExecuteHandler;
    private ApplicationContext applicationContext;
    private boolean enableLogOut = false;

    public EsEngineProxyBeanFactory(Class<T> targetInterfaceClazz) {
        this.targetInterfaceClazz = targetInterfaceClazz;
    }

    public EsEngineProxyBeanFactory(Class<T> targetInterfaceClazz, boolean visitQueryBeanParent) {
        this.targetInterfaceClazz = targetInterfaceClazz;
        this.visitQueryBeanParent = visitQueryBeanParent;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() {
        return (T) Proxy.newProxyInstance(
                targetInterfaceClazz.getClassLoader(),
                new Class[]{targetInterfaceClazz},
                new EsQueryProxy<T>(targetInterfaceClazz, visitQueryBeanParent, esProxyExecuteHandler, enableLogOut)
        );
    }

    @Override
    public Class<?> getObjectType() {
        return targetInterfaceClazz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        RestHighLevelClient restClient = applicationContext.getBean(RestHighLevelClient.class);
        Objects.requireNonNull(restClient, "SpringContext haven't RestHighLevelClient, config it");
        this.enableLogOut = Optional.ofNullable(
                applicationContext.getEnvironment().getProperty(ENABLE_LOG_OUT_PROPERTIES, Boolean.class)
        ).orElse(true);
    }
}
