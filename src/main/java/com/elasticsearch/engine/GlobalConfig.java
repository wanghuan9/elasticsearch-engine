package com.elasticsearch.engine;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wanghuan
 * @description: 全局配置
 * @date 2022-01-26 11:28
 */
@Component
public class GlobalConfig {

    /**
     * elasticSearch version
     */
    public static Integer elasticVersion = 7;

    /**
     * 对没有添加注解的字段 默然按照trem/trems查询
     */
    public static boolean isBuildDefault = Boolean.TRUE;

    /**
     * 查询参及响应参数数解析是否是下划线  true解析成下划线  false按照参数名驼峰
     */
    public static boolean namingStrategy = Boolean.FALSE;

    /**
     * 是否解析param 继承的类中的属性
     */
    public static boolean visitQueryBeanParent = Boolean.TRUE;

    /**
     * es查询超时时间 单位:TimeUnit.SECONDS
     */
    public static Integer queryTimeOut = 10;

//    /**
//     * es分组查询返回的size
//     * ES分组查询不设置size,默认只返回10条数据
//     */
//    public static Integer QUERY_BUCKET_SIZE = 2000;
//
//    /**
//     * es深度分页查询size限制
//     */
//    public static Integer DEP_PAGE_SIZE_MAX = 1000000;
//
//    /**
//     * es下载size限制
//     */
//    public static Integer EXPORT_SIZE_MAX = 3000000;

    /**
     * 查询字段前缀列表 解析字段名时会使用去除后缀后的值解析
     */
    public static Set<String> QUERY_PARAM_PREFIX = new HashSet<String>() {
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
    public static Set<String> QUERY_PARAM_SUFFIX = new HashSet<String>() {
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
    public static Set<String> QUERY_IGNORE_PARAM = new HashSet<String>() {
        {
            add("log");
            add("page");
            add("pageSize");
            add("size");
            add("sort");
        }
    };

    @Value("${es.engine.config.naming.strategy:false}")
    public void setNamingStrategy(boolean isSnakeCase) {
        GlobalConfig.namingStrategy = isSnakeCase;
    }

    @Value("${es.engine.config.query.timeout:10}")
    public void setQueryTimeOut(Integer queryTimeOut) {
        GlobalConfig.queryTimeOut = queryTimeOut;
    }
}
