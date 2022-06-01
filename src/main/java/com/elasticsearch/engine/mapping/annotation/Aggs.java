package com.elasticsearch.engine.mapping.annotation;


import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.Query;
import com.elasticsearch.engine.model.annotion.Sign;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: 标记注解 Aggs
 * @date 2022-01-26 11:28
 */
@Sign
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Aggs {

    /**
     * 按照分组的key 正序排序
     */
    String KEY_ASC = "KEY_ASC";
    /**
     * 按照分组的key 倒序排序
     */
    String KEY_DESC = "KEY_DESC";
    /**
     * 按照分组的count 正序排序 按count排序时默认会加上 key asc
     * "order":[{"_count":"desc"},{"_key":"asc"}]
     */
    String COUNT_ASC = "COUNT_ASC";
    /**
     * 按照分组的count 倒序排序 按count排序时默认会加上 key asc
     * "order":[{"_count":"desc"},{"_key":"asc"}]
     */
    String COUNT_DESC = "COUNT_DESC";

    /**
     * es分组查询返回的size
     * ES分组查询不设置size,默认只返回10条数据
     */
    Base value() default @Base;

    /**
     * ES分组查询不设置size,默认只返回10条数据
     */
    int size() default 1000;

    /**
     * 排序类型
     *
     * @return
     */
    String type() default KEY_ASC;
}
