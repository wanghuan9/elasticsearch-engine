package com.elasticsearch.engine.model.annotion;


import com.elasticsearch.engine.model.emenu.EsConnector;

import java.lang.annotation.*;


/**
 * @author wanghuan
 * @description: Base
 * @date 2022-01-26 11:28
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Base {

    /**
     * filed name
     * return
     */
    String name() default "";

    /**
     * filed pase order
     * 可以用于多排序时指定字段排序顺序
     *
     * @return
     */
    int order() default 0;

    /**
     * connector (boolQuery: must,must_not,should,filter)
     * return
     */
    EsConnector connect() default EsConnector.FILTER;


}
