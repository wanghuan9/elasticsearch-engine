package com.elasticsearch.engine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wanghuan
 * @description EsEngineConfigProperties
 * @mail 958721894@qq.com
 * @date 2022-06-11 21:45
 */
@ConfigurationProperties(prefix = "es.engine.config")
@Data
public class EsEngineConfigProperties {
    /**
     * elasticSearch version
     */
    private Integer elasticVersion = 7;

    /**
     * 对没有添加注解的字段 默然按照trem/trems查询
     */
    private boolean isBuildDefault = Boolean.TRUE;

    /**
     * 查询参及响应参数数解析是否是下划线  true解析成下划线  false按照参数名驼峰
     */
    private boolean namingStrategy = Boolean.FALSE;

    /**
     * 是否解析param 继承的类中的属性
     */
    private boolean visitQueryBeanParent = Boolean.TRUE;

    /**
     * es查询超时时间 单位:TimeUnit.SECONDS
     */
    private Integer queryTimeOut = 10;


    /**
     * 默认查询size,查询没有设置size时默认的size
     */
    private Integer defaultQuerySize = 1000;

    /**
     * 查询字段前缀列表 解析字段名时会使用去除后缀后的值解析
     */
    private Set<String> queryParamPrefix = new HashSet<String>() {
        private static final long serialVersionUID = -7716606177924143554L;

        {
            add("list");
            add("start");
            add("end");
            add("begin");
        }
    };

    /**
     * 查询字段后缀列表, 解析字段名时会使用去除后缀后的值解析
     */
    private Set<String> queryParamSuffix = new HashSet<String>() {
        private static final long serialVersionUID = 6255704971422131027L;

        {
            add("List");
            add("Start");
            add("End");
            add("Begin");
        }
    };

    /**
     * 默认查询需要忽略的字段
     */
    private Set<String> queryIgnoreParam = new HashSet<String>() {
        private static final long serialVersionUID = -5794700187017984887L;

        {
            add("log");
            add("page");
            add("pageSize");
            add("size");
            add("sort");
        }
    };
}
