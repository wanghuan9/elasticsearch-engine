package com.elasticsearch.engine.elasticsearchengine.common.proxy;

import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.EsExecuteHandler;
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
 * EsHelperProxyBeanFactory
 * <p>
 * author     JohenTeng
 * date      2021/9/18
 */
public class EsHelperProxyBeanFactory<T> implements ApplicationContextAware, InitializingBean, FactoryBean<T> {

    private static final String ENABLE_LOG_OUT_PROPERTIES = "es.helper.queryLogOut.enable";
    private Class<T> targetInterfaceClazz;
    private boolean visitQueryBeanParent;
    @Resource
    private EsExecuteHandler esExecuteHandler;
    private ApplicationContext applicationContext;
    private boolean enableLogOut = false;

    public EsHelperProxyBeanFactory(Class<T> targetInterfaceClazz, boolean visitQueryBeanParent) {
        this.targetInterfaceClazz = targetInterfaceClazz;
        this.visitQueryBeanParent = visitQueryBeanParent;
    }

    public EsHelperProxyBeanFactory(Class<T> targetInterfaceClazz, boolean visitQueryBeanParent, RestHighLevelClient client) {
        this.targetInterfaceClazz = targetInterfaceClazz;
        this.visitQueryBeanParent = visitQueryBeanParent;
        this.esExecuteHandler = esExecuteHandler;
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
                new EsQueryProxy<T>(targetInterfaceClazz, visitQueryBeanParent, esExecuteHandler, enableLogOut)
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
