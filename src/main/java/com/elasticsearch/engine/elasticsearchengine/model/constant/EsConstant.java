package com.elasticsearch.engine.elasticsearchengine.model.constant;

/**
 * @author wanghuan
 * @description: es content
 * @date 2021-09-29
 * @time 10:21
 */
public interface EsConstant {

    /**
     * es 分组桶返回size
     */
    Integer ES_AGG_BUCKETS_SIZE = 2000;

    /**
     * es 通用 查询分组别名
     */
    String _AGG = "es_agg";
    String _SUM = "es_sum";
    String _variance = "es_variance";
    String _SQUARE = "es_square";

    /**
     * 字符串0
     */
    String STRING_ZERO = "0";


}
