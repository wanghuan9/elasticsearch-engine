package com.elasticsearch.engine.elasticsearchengine.common;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wanghuan
 * @description: 全局配置
 * @date 2022-01-26 11:28
 */
public class GlobalConfig {

    /**
     * 对没有添加注解的字段 默然按照trem/trems查询
     */
    public static final boolean IS_BUILD_DEFAULT = Boolean.TRUE;

    /**
     * 查询参数解析是否默认下划线  true解析成下划线  false按照参数名驼峰
     */
    public static final boolean QUERY_PARAM_IS_LOWER_UNDERSCORE = Boolean.TRUE;

    /**
     * 是否解析param 继承的类中的属性
     */
    public static final boolean VISIT_QUERY_BEAN_PARENT = Boolean.TRUE;

    /**
     * 响应参数解析 json默认解析方式  SNAKE_CASE表示下划线  LOWER_CAMEL_CASE表示首字母小写驼峰
     */
    public static final PropertyNamingStrategy RES_PROPERTY_NAMING_STRATEGY = PropertyNamingStrategy.SNAKE_CASE;

    /**
     * es查询超时时间 单位:TimeUnit.SECONDS
     */
    public static final Integer QUERY_TIME_OUT = 10;

    /**
     * es分组查询返回的size
     * ES分组查询不设置size,默认只返回10条数据
     */
    public static final Integer QUERY_BUCKET_SIZE = 2000;

    /**
     * es深度分页查询size限制
     */
    public static final Integer DEP_PAGE_SIZE_MAX = 1000000;

    /**
     * es下载size限制
     */
    public static final Integer EXPORT_SIZE_MAX = 3000000;

    /**
     * 查询字段前缀列表 解析字段名时会使用去除后缀后的值解析
     */
    public static final Set<String> QUERY_PARAM_PREFIX = new HashSet<String>() {
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
    public static final Set<String> QUERY_PARAM_SUFFIX = new HashSet<String>() {
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
    public static final Set<String> QUERY_IGNORE_PARAM = new HashSet<String>() {
        {
            add("log");
            add("page");
            add("pageSize");
            add("size");
            add("sort");
        }
    };
}
