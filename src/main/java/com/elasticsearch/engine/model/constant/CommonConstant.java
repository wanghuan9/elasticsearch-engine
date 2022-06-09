package com.elasticsearch.engine.model.constant;

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
     * banner文件路径
     */
    public final static String BANNER_PATH = "engine-banner.txt";

    /**
     * version文件路径
     */
    public final static String VERSION_PATH = "elasticseach-engine.properties";

    /**
     * 查询sql
     */
    public static final String BACK_QUERY_SQL = "back_sql";

    /**
     * 查询sql前缀 小写
     */
    public static final String SELECT_SQL_PREFIX_LOWER = "select";

    /**
     * 查询sql前缀 大写
     */
    public static final String SELECT_SQL_PREFIX_UPPER = "SELECT";

    /**
     * es查询标记
     */
    public static final String IS_ES_QUERY = "is_es_query";

    /**
     * es默认连接
     */
    public static final String DEFAULT_ES_HOST = "127.0.0.1:9200";

}
