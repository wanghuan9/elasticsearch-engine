package com.elasticsearch.engine.mapping.annotation;


import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.Query;

import java.lang.annotation.*;

/**
 * WildCard
 * real-fuzzy for elasticsearch, un-use analyzer, similar mysql's like %holder%
 * WildCard query's value tag:
 * '*': multi-words holder
 * '?': single-words holder
 *
 * @author wanghuan
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface WildCard {

    //表示 like %${param}
    String BEFORE_MATCH = "*%s";
    //表示 like ${param}%
    String AFTER_MATCH = "%s*";
    //表示 like %${param}%
    String INCLUDE_MATCH = "*%s*";

    Base value() default @Base;

    /**
     * 匹配类型 (required)
     *
     * @return
     */
    String tag() default INCLUDE_MATCH;

}
