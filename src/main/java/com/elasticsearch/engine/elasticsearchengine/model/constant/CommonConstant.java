package com.elasticsearch.engine.elasticsearchengine.model.constant;

/**
 * @author wanghuan
 * @description: es content
 * @date 2021-09-29
 * @time 10:21
 */
public class CommonConstant {

    /**
     * string里面的特殊字符
     */
    public static final String SPECIAL_CHAR = "\\p{C}";
    
    /**
     * 记录代理类的名称
     */
    public static final String INTERFACE_METHOD_NAME = "method";
    
    /**
     * 查询sql
     */
    public static final String QUERY_SQL = "sql";

    /**
     * es查询标记
     */
    public static final String IS_ES_QUERY = "is_es_query";

    /**
     * es默认连接
     */
    public static final String DEFAULT_ES_HOST = "127.0.0.1:9200";

}
