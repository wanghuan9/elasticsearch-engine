package com.elasticsearch.engine.elasticsearchengine.mapping.annotation;


import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Range
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    //表示左右包含  >= L and <= G
    String LE_GE = "[L,G]";
    //表示左右都不包含 >L and < G
    String L_G = "(L,G)";
    //表示左右都不包含 >=L and < G
    String LE_G = "[L,G)";
    //表示左右都不包含 >L and <= G
    String L_GE = "(L,G]";
    //表示 from to
    String F_T = "F_T";

    Base value() default @Base;

    /**
     * range-query both side relation
     * {@value LE_GE}
     * {@value L_G}
     * {@value LE_G}
     * {@value L_GE}
     * {@value F_T}
     * return
     */
    String tag() default L_G;

    //es7的时间及格式化
    String format() default "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    String timeZone() default "GMT+8";

    /**
     * es6的时间及格式化
     *
     * @return
     */
//    String format() default "yyyy-MM-dd'T'HH:mm:ss.SSS";
//
//    String timeZone() default "+08:00";

    //within、contains。intersects
    String relation() default "";

    boolean includeLower() default false;

    boolean includeUpper() default false;
}
