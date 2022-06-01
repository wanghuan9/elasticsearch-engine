package com.elasticsearch.engine.common.proxy;

import com.elasticsearch.engine.hook.EsHookReedits;
import com.elasticsearch.engine.hook.UserHooks;
import com.elasticsearch.engine.model.annotion.EsHelperProxy;
import com.elasticsearch.engine.model.domain.BaseESRepository;
import com.elasticsearch.engine.model.exception.EsHelperConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EsHelperInterfaceScanner
 * read all Interface which annotated by
 * {@link EsHelperProxy @EsHelperProxy}
 * and  load a proxy instance for it
 * author     JohenTeng
 * date      2021/9/18
 */
@Component
public class EsHelperInterfaceScanner implements ApplicationContextAware, ResourceLoaderAware, BeanDefinitionRegistryPostProcessor, ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EsHelperInterfaceScanner.class);

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ApplicationContext applicationContext;
    private MetadataReaderFactory metadataReaderFactory;
    private ResourcePatternResolver resourcePatternResolver;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    /**
     * 对应添加了@EsHelperProxy的类生成代理
     * 修改 BeanDefinition 并重新注册到 beanDefinitionMap
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // scan packages get all Class that annotation by @EsHelperProxy
        Set<Class<?>> beanClazzSet = this.findAllClazz();
        for (Class beanClazz : beanClazzSet) {
            if (!isRepository(beanClazz)) {
                continue;
            }
//            EsHelperProxy proxyAnn = AnnotationUtils.findAnnotation(beanClazz, EsHelperProxy.class);
            // BeanDefinition builder
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);
            definition.setBeanClass(beanClazz);
            definition.setLazyInit(true);
            //根据其set方法的解释：就是替代工厂方法（包含静态工厂）或者构造器创建对象，但是其后面的生命周期回调不影响。
            //也就是框架在创建对象的时候会校验这个instanceSupplier是否有值，有的话，调用这个字段获取对象。
//            definition.setInstanceSupplier(() ->
//                    new EsHelperProxyBeanFactory(beanClazz, proxyAnn.visitParent())
//            );
            definition.setInstanceSupplier(() ->
                    new EsHelperProxyBeanFactory(beanClazz)
            );
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            String simpleName = beanClazz.getSimpleName();
            simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            registry.registerBeanDefinition(simpleName, definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * hook 外置化扩展
     * TODO
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<Class<?>> beanClazzSet = this.findAllClazz();
        for (Class<?> clazz : beanClazzSet) {
            if (UserHooks.class.isAssignableFrom(clazz)) {
                EsHookReedits.loadHooksFromTargetInterface(clazz);
            }
        }
    }

    /**
     * 获取路径下的 class文件
     * com.elasticsearch.engine.elasticsearchengine
     * TODO
     *
     * @return
     */
    private Set<Class<?>> findAllClazz() {
        //拿到的package 是根路径 com.elasticsearch.engine.elasticsearchengine
        List<String> packages = AutoConfigurationPackages.get(applicationContext);
        // scan packages get all Class that annotation by @EsHelperProxy
        return packages.stream().flatMap(path -> findAllQueryHelperProxyInterfaces(path).stream()).collect(Collectors.toSet());
    }

    /**
     * 获取 class文件
     *
     * @param basePackage
     * @return
     */
    private Set<Class<?>> findAllQueryHelperProxyInterfaces(String basePackage) {
        Set<Class<?>> set = new LinkedHashSet<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    set.add(clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new EsHelperConfigException("Es-helper init Reflection ERROR, cause", e);
        } catch (IOException e) {
            throw new EsHelperConfigException("Es-helper init I/O ERROR, cause", e);
        }
        return set;
    }

    /**
     * 获取根路径 com/elasticsearch/engine/elasticsearchengine
     *
     * @param basePackage
     * @return
     */
    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.applicationContext.getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    /**
     * 判断是否有@EsHelperProxy注解
     *
     * @param beanClazz
     * @return
     */
    private boolean isNotNeedProxy(Class beanClazz) {
        return null == AnnotatedElementUtils.findMergedAnnotation(beanClazz, EsHelperProxy.class);
    }

    /**
     * 判断是继承了 BaseESRepository
     *
     * @param beanClazz
     * @return
     */
    private boolean isRepository(Class beanClazz) {
        return BaseESRepository.class.isAssignableFrom(beanClazz);
    }
}
