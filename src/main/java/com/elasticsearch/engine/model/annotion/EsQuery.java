package com.elasticsearch.engine.model.annotion;

import com.elasticsearch.engine.common.proxy.enums.EsQueryType;

import java.lang.annotation.*;


/**
 * @author wanghuan
 * @description: EsQuery
 * @date 2022-01-26 11:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface EsQuery {

    /**
     * 查询语句
     *
     * @return
     */
    String value() default "";

    /**
     * 查询类型
     *
     * @return
     */
    EsQueryType queryType() default EsQueryType.SQL;
    //表示左右都不包含 >L and < G
}
