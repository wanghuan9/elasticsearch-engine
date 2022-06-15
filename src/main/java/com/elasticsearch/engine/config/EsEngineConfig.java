package com.elasticsearch.engine.config;


import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author wanghuan
 * @description: 全局配置
 * @date 2022-01-26 11:28
 */
@Data
@Component
public class EsEngineConfig {

    @Resource
    private EsEngineConfigProperties esEngineConfigProperties;
    private static EsEngineConfig globalConfig;

    @PostConstruct
    public void init() {
        globalConfig = this;
        globalConfig.esEngineConfigProperties = this.esEngineConfigProperties;
    }

    /**
     * elasticSearch version
     *
     * @return
     */
    public static Integer getElasticVersion() {
        return globalConfig.esEngineConfigProperties.getElasticVersion();
    }

    /**
     * 对没有添加注解的字段 默然按照trem/trems查询
     *
     * @return
     */
    public static boolean isIsBuildDefault() {
        return globalConfig.esEngineConfigProperties.isBuildDefault();
    }

    /**
     * 查询参及响应参数数解析是否是下划线  true解析成下划线  false按照参数名驼峰
     *
     * @return
     */
    public static boolean isNamingStrategy() {
        return globalConfig.esEngineConfigProperties.isNamingStrategy();
    }

    /**
     * 是否解析param 继承的类中的属性
     *
     * @return
     */
    public static boolean isVisitQueryBeanParent() {
        return globalConfig.esEngineConfigProperties.isVisitQueryBeanParent();
    }

    /**
     * es查询超时时间 单位:TimeUnit.SECONDS
     *
     * @return
     */
    public static Integer getQueryTimeOut() {
        return globalConfig.esEngineConfigProperties.getQueryTimeOut();
    }

    /**
     * 查询字段前后缀列表 解析字段名时会使用去除前后缀后的值解析
     *
     * @return
     */
    public static Set<String> getQueryParamPrefixAndSuffix() {
        Set<String> queryParamPrefix = globalConfig.esEngineConfigProperties.getQueryParamPrefix();
        Set<String> queryParamSuffix = globalConfig.esEngineConfigProperties.getQueryParamSuffix();
        queryParamPrefix.addAll(queryParamSuffix);
        return queryParamPrefix;
    }

    /**
     * 默认查询需要忽略的字段
     *
     * @return
     */
    public static Set<String> getQueryIgnoreParam() {
        return globalConfig.esEngineConfigProperties.getQueryIgnoreParam();
    }

    /**
     * 默认查询size,查询没有设置size时默认的size
     *
     * @return
     */
    public static Integer getDefaultQuerySize() {
        return globalConfig.esEngineConfigProperties.getDefaultQuerySize();
    }

    /**
     * es extend(mybatis,jpa,jooq)查询 全局开关, true表示查询es, false表示查询mysql
     *
     * @return
     */
    public static boolean getEsQuery() {
        return globalConfig.esEngineConfigProperties.isEsQuery();
    }

    /**
     * es extend(mybatis,jpa,jooq)查询降级 包含的接口,仅再esQuery=false时才生效
     *
     * @return
     */
    public static Set<String> getEsQueryInclude() {
        return globalConfig.esEngineConfigProperties.getEsQueryInclude();
    }

    /**
     * es extend(mybatis,jpa,jooq)查询降级 排除的接口,仅再esQuery=true时才生效
     *
     * @return
     */
    public static Set<String> getEsQueryExclude() {
        return globalConfig.esEngineConfigProperties.getEsQueryExclude();
    }

    /**
     * 判断是否走es查询
     *
     * @param method
     * @return
     */
    public static boolean isEsquery(Method method) {
        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        //全局开关打开 并且exclude不包含
        if (EsEngineConfig.getEsQuery() && !EsEngineConfig.getEsQueryExclude().contains(methodName)) {
            return true;
        }
        //全局开关关闭 并且include包含
        if (!EsEngineConfig.getEsQuery() && EsEngineConfig.getEsQueryInclude().contains(methodName)) {
            return true;
        }
        return false;
    }
}
