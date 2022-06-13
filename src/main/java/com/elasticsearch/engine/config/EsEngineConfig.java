package com.elasticsearch.engine.config;


import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

    public static Integer getElasticVersion() {
        return globalConfig.esEngineConfigProperties.getElasticVersion();
    }

    public static boolean isIsBuildDefault() {
        return globalConfig.esEngineConfigProperties.isBuildDefault();
    }

    public static boolean isNamingStrategy() {
        return globalConfig.esEngineConfigProperties.isNamingStrategy();
    }

    public static boolean isVisitQueryBeanParent() {
        return globalConfig.esEngineConfigProperties.isVisitQueryBeanParent();
    }

    public static Integer getQueryTimeOut() {
        return globalConfig.esEngineConfigProperties.getQueryTimeOut();
    }

    public static Integer getDefaultQuerySize() {
        return globalConfig.esEngineConfigProperties.getDefaultQuerySize();
    }

    public static Set<String> getQueryParamPrefixAndSuffix() {
        Set<String> queryParamPrefix = globalConfig.esEngineConfigProperties.getQueryParamPrefix();
        Set<String> queryParamSuffix = globalConfig.esEngineConfigProperties.getQueryParamSuffix();
        queryParamPrefix.addAll(queryParamSuffix);
        return queryParamPrefix;
    }

    public static Set<String> getQueryIgnoreParam() {
        return globalConfig.esEngineConfigProperties.getQueryIgnoreParam();
    }
}
